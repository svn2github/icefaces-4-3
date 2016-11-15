/*
 * Copyright 2004-2014 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icefaces.ace.component.schedule;

import org.icefaces.ace.model.schedule.ScheduleEvent;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.JSONBuilder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ScheduleRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {

		Schedule schedule = (Schedule) component;
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		schedule.resetDataModel();

		String clientId = schedule.getClientId(context);
		if (params.containsKey(clientId + "_add")) decodeAdd(context, schedule, params);
		else if (params.containsKey(clientId + "_edit")) decodeEdit(context, schedule, params);
		else if (params.containsKey(clientId + "_delete")) decodeDelete(context, schedule, params);

		decodeBehaviors(context, schedule);
	}

	public void decodeAdd(FacesContext context, Schedule schedule, Map<String, String> params) {
		String clientId = schedule.getClientId(context);

		schedule.addEvent(buildScheduleEventFromRequest(schedule, params, clientId));
	}

	public void decodeEdit(FacesContext context, Schedule schedule, Map<String, String> params) {
		String clientId = schedule.getClientId(context);

		String indexParam = params.get(clientId + "_index");

		int index;
		try {
			index = Integer.valueOf(indexParam);
		} catch(Exception e) {
			/* TO_DO: log warning */
			return;
		}

		schedule.editEvent(index, buildScheduleEventFromRequest(schedule, params, clientId));
	}

	public void decodeDelete(FacesContext context, Schedule schedule, Map<String, String> params) {
		String clientId = schedule.getClientId(context);
		Object value = schedule.getValue();
		if (value instanceof List || Object[].class.isAssignableFrom(value.getClass())) {
			String indexParam = params.get(clientId + "_index");

			int index;
			try {
				index = Integer.valueOf(indexParam);
			} catch(Exception e) {
				/* TO_DO: log warning */
				return;
			}
			schedule.deleteEvent(index);
		} else if (value instanceof Collection) {
			schedule.deleteEvent(buildScheduleEventFromRequest(schedule, params, clientId));
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
			
		Schedule schedule = (Schedule) component;
		ResponseWriter writer = context.getResponseWriter();
		String clientId = component.getClientId();
		TimeZone timeZone = schedule.calculateTimeZone();

		// if in lazy mode, update current year and month values
		boolean isLazy = schedule.isLazy();
		if (isLazy) {
			Map<String, String> params = context.getExternalContext().getRequestParameterMap();
			String lazyYear = params.get(clientId + "_lazyYear");
			String lazyMonth = params.get(clientId + "_lazyMonth");
			String lazyDay = params.get(clientId + "_lazyDay");
			if (lazyYear != null && lazyMonth != null) {
				schedule.setLazyYear(new Integer(lazyYear));
				schedule.setLazyMonth(new Integer(lazyMonth));
				schedule.setLazyDay(new Integer(lazyDay));
			}
		}
		schedule.resetDataModel();

		String viewMode = schedule.getViewMode();
		if ("week".equalsIgnoreCase(viewMode)) {
			viewMode = "week";
		} else if ("day".equalsIgnoreCase(viewMode)) {
			viewMode = "day";
		} else {
			viewMode = "month";
		}
		String sideBar = schedule.getSideBar();
		String sideBarClass = "schedule-config-sidebar-right";
		if (sideBar != null) {
			if ("left".equalsIgnoreCase(sideBar)) sideBarClass = "schedule-config-sidebar-left";
			else if ("hidden".equalsIgnoreCase(sideBar)) sideBarClass = "schedule-config-sidebar-hidden";
		}
		String displayEventDetails = schedule.getDisplayEventDetails();
		String displayEventDetailsClass = "schedule-config-details-sidebar";
		if (displayEventDetails != null) {
			if ("popup".equalsIgnoreCase(displayEventDetails)) {
				displayEventDetailsClass = "schedule-config-details-popup";
				displayEventDetails = "popup";
			} else if ("tooltip".equalsIgnoreCase(displayEventDetails)) {
				displayEventDetailsClass = "schedule-config-details-tooltip";
				displayEventDetails = "tooltip";
			} else if ("disabled".equalsIgnoreCase(displayEventDetails)) {
				displayEventDetailsClass = "schedule-config-details-disabled";
				displayEventDetails = "disabled";
			} else {
				displayEventDetails = "sidebar";
			}
		} else {
			displayEventDetails = "sidebar";
		}
		String scrollableClass = schedule.isScrollable() ? "schedule-config-scrollable" : "";

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", "ice-ace-schedule", null);

		writer.startElement("div", null);
		writer.writeAttribute("class", "schedule-details-popup-content", null);
		writer.writeAttribute("title", "Event Details", null);
		writer.endElement("div");

		writer.startElement("div", null);
		writer.writeAttribute("class", "schedule-details-tooltip-content", null);
		writer.endElement("div");

		// render configuration and event data
		JSONBuilder jb = JSONBuilder.create();
		jb.beginFunction("ice.ace.create")
			.item("Schedule")
			.beginArray()
				.item(clientId)
				.beginMap()
					.entry("viewMode", viewMode)
					.entry("displayEventDetails", displayEventDetails)
					.entry("isEventAddition", "disabled".equalsIgnoreCase(schedule.getAdditionControls()) ? false : true)
					.entry("isEventEditing", "disabled".equalsIgnoreCase(schedule.getEditingControls()) ? false : true)
					.entry("isEventDeletion", "disabled".equalsIgnoreCase(schedule.getDeletionControls()) ? false : true);

					if (schedule.isScrollable()) jb.entry("scrollHeight", schedule.getScrollHeight());

					if (isLazy) {
						int[] lazyDateValues = schedule.getLazyDateValues();
						jb.entry("isLazy", true)
						.entry("lazyYear", lazyDateValues[0])
						.entry("lazyMonth", lazyDateValues[1])
						.entry("lazyDay", lazyDateValues[2]);
					} else {
						int[] currentDateValues = schedule.getCurrentDateValues();
						jb.entry("currentYear", currentDateValues[0])
						.entry("currentMonth", currentDateValues[1])
						.entry("currentDay", currentDateValues[2]);
					}

					encodeClientBehaviors(context, schedule, jb);

					jb.beginArray("events");

					int rowCount = schedule.getRowCount();
					for (int i = 0; i < rowCount; i++) {
						schedule.setRowIndex(i);
						ScheduleEvent scheduleEvent = (ScheduleEvent) schedule.getRowData();
						Date startDate = ScheduleUtils.toTimeZoneFromUTC(scheduleEvent.getStartDate(), timeZone);
						Date endDate = ScheduleUtils.toTimeZoneFromUTC(scheduleEvent.getEndDate(), timeZone);
						jb.beginMap();
						jb.entry("index", i);
						jb.entry("startDate", convertDateToClientFormat(startDate));
						jb.entry("startTime", convertTimeToClientFormat(startDate));
						jb.entry("endDate", convertDateToClientFormat(endDate));
						jb.entry("endTime", convertTimeToClientFormat(endDate));
						jb.entry("title", scheduleEvent.getTitle());
						jb.entry("location", scheduleEvent.getLocation());
						jb.entry("notes", scheduleEvent.getNotes());
						String styleClass = scheduleEvent.getStyleClass();
						if (styleClass != null) jb.entry("styleClass", scheduleEvent.getStyleClass());
						jb.endMap();
					}

					jb.endArray()
				.endMap()
			.endArray()
          .endFunction();

		writer.startElement("div", null);
		writer.writeAttribute("class", "schedule-main ui-widget" + " schedule-view-" + viewMode + " " 
			+ sideBarClass + " " + displayEventDetailsClass + " " + scrollableClass, null);
		writer.endElement("div");

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write(jb.toString());
		writer.endElement("script");

		writer.endElement("div");
	}

	private String convertDateToClientFormat(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		return (year + "-" + addLeadingZero(month) + month + "-" + addLeadingZero(day) + day);
	}

	private String convertTimeToClientFormat(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		return (addLeadingZero(hour) + hour + ":" + addLeadingZero(minute) + minute);
	}

	private Date convertDateTimeToServerFormat(String date, String time) {
		Calendar cal = Calendar.getInstance();
		String yearString = date.substring(0, 4);
		String monthString = date.substring(5, 7);
		String dayString = date.substring(8, 10);
		String hourString = time.substring(0, time.indexOf(":"));
		String minuteString = time.substring(time.indexOf(":")+1);
		try {
			int year, month, day, hour, minute;
			year = Integer.valueOf(yearString);
			month = Integer.valueOf(monthString) - 1;
			day = Integer.valueOf(dayString);
			hour = Integer.valueOf(hourString);
			minute = Integer.valueOf(minuteString);
			cal.set(year, month, day, hour, minute);
		} catch (Exception e) {
			/* TO_DO: log warning */
			return null;
		}
		return cal.getTime();
	}

	private ScheduleEvent buildScheduleEventFromRequest(Schedule schedule, Map<String, String> params, String clientId){
		String startDate = params.get(clientId + "_date");
		String startTime = params.get(clientId + "_time");
		String endDate = params.get(clientId + "_endDate");
		String endTime = params.get(clientId + "_endTime");
		String title = params.get(clientId + "_title");
		String location = params.get(clientId + "_location");
		String notes = params.get(clientId + "_notes");
		TimeZone timeZone = schedule.calculateTimeZone();

		ScheduleEvent scheduleEvent = new ScheduleEvent();
		Date convertedDate = convertDateTimeToServerFormat(startDate, startTime);
		if (convertedDate == null) return null;
		Date convertedEndDate = convertDateTimeToServerFormat(endDate, endTime);
		if (convertedEndDate == null) convertedEndDate = new Date(convertedDate.getTime());
		scheduleEvent.setStartDate(ScheduleUtils.toUTCFromTimeZone(convertedDate, timeZone));
		scheduleEvent.setEndDate(ScheduleUtils.toUTCFromTimeZone(convertedEndDate, timeZone));
		scheduleEvent.setTitle(title);
		scheduleEvent.setLocation(location);
		scheduleEvent.setNotes(notes);

		return scheduleEvent;
	}

	private String addLeadingZero(int value) {
		if (value < 10) return "0";
		return "";
	}
}