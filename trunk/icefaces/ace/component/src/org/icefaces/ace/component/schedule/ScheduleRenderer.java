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
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.JSONBuilder;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.DataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class ScheduleRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {

		Schedule schedule = (Schedule) component;
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		String clientId = schedule.getClientId(context);

		if (params.containsKey(clientId + "_viewModeChange")) {
			String viewMode = params.get(clientId + "_viewModeChange");
			if (viewMode != null && !"".equals(viewMode)) {
				viewMode = viewMode.toLowerCase();
				if ("month".equals(viewMode) || "week".equals(viewMode) || "day".equals(viewMode)) {
					schedule.setViewMode(viewMode);
				}
			}
		}

		if (params.containsKey(clientId + "_add")) decodeAdd(context, schedule, params);
		else if (params.containsKey(clientId + "_edit")) decodeEdit(context, schedule, params);
		else if (params.containsKey(clientId + "_delete")) decodeDelete(context, schedule, params);

		decodeBehaviors(context, schedule);
	}

	public void decodeAdd(FacesContext context, Schedule schedule, Map<String, String> params) {
		String clientId = schedule.getClientId(context);

		schedule.addEvent(ScheduleUtils.buildScheduleEventFromRequest(schedule, params, clientId));
	}

	public void decodeEdit(FacesContext context, Schedule schedule, Map<String, String> params) {
		String clientId = schedule.getClientId(context);

		String indexParam = params.get(clientId + "_index");

		int index;
		try {
			index = Integer.valueOf(indexParam);
		} catch(Exception e) {
			FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Event editing failed. Incorrect index.", "Event editing failed. Incorrect index.");
			context.addMessage(clientId, fm);
			return;
		}

		schedule.editEvent(index, ScheduleUtils.buildScheduleEventFromRequest(schedule, params, clientId));
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
				FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Event editing failed. Incorrect index.", "Event editing failed. Incorrect index.");
				context.addMessage(clientId, fm);
				return;
			}
			schedule.deleteEvent(index);
		} else if (value instanceof Collection) {
			schedule.deleteEvent(ScheduleUtils.buildScheduleEventFromRequest(schedule, params, clientId));
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
			
		Schedule schedule = (Schedule) component;
		ResponseWriter writer = context.getResponseWriter();
		String clientId = component.getClientId();
		TimeZone timeZone = schedule.calculateTimeZone();

		// update current date values
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();
		String currentYear = params.get(clientId + "_currentYear");
		String currentMonth = params.get(clientId + "_currentMonth");
		String currentDay = params.get(clientId + "_currentDay");
		if ((currentYear != null && !"".equals(currentYear.trim()))
			&& (currentMonth != null && !"".equals(currentMonth.trim()))
			&& (currentDay != null && !"".equals(currentDay.trim()))) {
				schedule.setCurrentYear(new Integer(currentYear));
				schedule.setCurrentMonth(new Integer(currentMonth));
				schedule.setCurrentDay(new Integer(currentDay));
		}

		// set current date values again if they were programmatically changed
		boolean isCurrentDateProgrammaticallySet = false;
		Date currentDate = schedule.getViewDate();
		if (currentDate != null) {
			ScheduleUtils.DateIntegerValues currentDateIntegerValues = 
				ScheduleUtils.getDateIntegerValues(currentDate);
			Integer currentYearFromClient;
			Integer currentMonthFromClient;
			Integer currentDayFromClient;
			if (params.containsKey(clientId + "_navigation")) {
				currentYearFromClient = new Integer(params.get(clientId + "_oldYear"));
				currentMonthFromClient = new Integer(params.get(clientId + "_oldMonth"));
				currentDayFromClient = new Integer(params.get(clientId + "_oldDay"));
			} else {
				currentYearFromClient = schedule.getCurrentYear();
				currentMonthFromClient = schedule.getCurrentMonth();
				currentDayFromClient = schedule.getCurrentDay();
			}
			if (currentDateIntegerValues.getYear() != currentYearFromClient
				|| currentDateIntegerValues.getMonth() != currentMonthFromClient
				|| currentDateIntegerValues.getDay() != currentDayFromClient) {
					isCurrentDateProgrammaticallySet = true;
					schedule.setCurrentYear(new Integer(currentDateIntegerValues.getYear()));
					schedule.setCurrentMonth(new Integer(currentDateIntegerValues.getMonth()));
					schedule.setCurrentDay(new Integer(currentDateIntegerValues.getDay()));
			}
		}

		String viewMode = schedule.getViewMode();
		if ("week".equalsIgnoreCase(viewMode)) {
			viewMode = "week";
		} else if ("day".equalsIgnoreCase(viewMode)) {
			viewMode = "day";
		} else {
			viewMode = "month";
		}

		// detect a change in view mode and use the selected date if available
		String previousViewMode = params.get(clientId + "_viewMode");
		String selectedDateString = params.get(clientId + "_selectedDate");
		boolean selectCurrentDate = false;
		boolean selectedDateOnViewChange = false;
		if (!isCurrentDateProgrammaticallySet) {
			if (previousViewMode != null && !viewMode.equalsIgnoreCase(previousViewMode)) {
				if (selectedDateString != null && !"".equals(selectedDateString)) {
					Date selectedDate = ScheduleUtils.convertDateTimeToServerFormat(selectedDateString, "00:00", null);
					if (selectedDate != null) {
						selectedDateOnViewChange = true;
						Calendar cal = Calendar.getInstance();
						cal.setTime(selectedDate);
						schedule.setCurrentYear(cal.get(Calendar.YEAR));
						schedule.setCurrentMonth(cal.get(Calendar.MONTH));
						schedule.setCurrentDay(cal.get(Calendar.DATE));
					}
				}
			}
		} else {
			selectCurrentDate = true;
		}

		// normalize current date values
		int[] currentDateValues = schedule.getCurrentDateValues(selectedDateOnViewChange);

		// set normalized current date values
		schedule.setCurrentYear(currentDateValues[0]);
		schedule.setCurrentMonth(currentDateValues[1]);
		schedule.setCurrentDay(currentDateValues[2]);
		ScheduleUtils.DateIntegerValues dateIntegerValues =
			new ScheduleUtils.DateIntegerValues(schedule.getCurrentYear(),
				schedule.getCurrentMonth(), schedule.getCurrentDay(), 0, 0, 0);
		schedule.setViewDate(ScheduleUtils.getDateFromIntegerValues(dateIntegerValues));

		schedule.resetDataModel();

		String enhancedStylingClass = schedule.isEnhancedStyling() ? " schedule-styling-enhanced" : "";
		String sideBar = schedule.getSideBar();
		String sideBarClass = "schedule-config-sidebar-right";
		if (sideBar != null) {
			if ("left".equalsIgnoreCase(sideBar)) sideBarClass = "schedule-config-sidebar-left";
			else if ("hidden".equalsIgnoreCase(sideBar)) sideBarClass = "schedule-config-sidebar-hidden";
		}
		String eventDetails = schedule.getShowEventDetails();
		String eventDetailsClass = "schedule-config-details-popup";
		if (eventDetails != null) {
			if ("sidebar".equalsIgnoreCase(eventDetails)) {
				eventDetailsClass = "schedule-config-details-sidebar";
				eventDetails = "sidebar";
			} else if ("disabled".equalsIgnoreCase(eventDetails)) {
				eventDetailsClass = "schedule-config-details-disabled";
				eventDetails = "disabled";
			} else {
				eventDetails = "popup";
			}
		} else {
			eventDetails = "sidebar";
		}
		boolean resizableSidebar = schedule.isResizableSidebar();
		int tabindex = schedule.getTabindex();
		boolean displayTooltip = schedule.isShowTooltip();
		String tooltipClass = displayTooltip ? "schedule-config-details-tooltip" : "";
		String scrollableClass = schedule.isScrollable() ? "schedule-config-scrollable" : "";
		boolean autoDetectTimeZone = schedule.isAutoDetectTimeZone();
		boolean enableViewModeControls = schedule.isEnableViewModeControls();

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		ComponentUtils.enableOnElementUpdateNotify(writer, clientId);
		writer.writeAttribute("class", "ice-ace-schedule", null);
		writer.writeAttribute("tabindex", "0", null);
		writer.writeAttribute("role", "widget", null);
		writer.writeAttribute("aria-label", "schedule", null);

		writer.startElement("div", null);
		writer.writeAttribute("class", "schedule-details-popup-content", null);
		writer.writeAttribute("title", "Event Details", null);
		writer.endElement("div");

		writer.startElement("div", null);
		writer.writeAttribute("class", "schedule-details-tooltip-content", null);
		writer.endElement("div");

		writer.startElement("div", null);
		writer.writeAttribute("class", "schedule-navigation-dialog-content", null);
		writer.endElement("div");

		// render configuration and event data
		JSONBuilder jb = JSONBuilder.create();
		jb.beginFunction("ice.ace.create")
			.item("Schedule")
			.beginArray()
				.item(clientId)
				.beginMap()
					.entry("viewMode", viewMode)
					.entry("eventDetails", eventDetails)
					.entry("resizableSidebar", resizableSidebar)
					.entry("tabindex", tabindex)
					.entry("displayTooltip", displayTooltip)
					.entry("defaultDuration", schedule.getDefaultDuration())
					.entry("isEventAddition", schedule.isAddEvents())
					.entry("isEventEditing", schedule.isEditEvents())
					.entry("isEventDeletion", schedule.isDeleteEvents());

					if (schedule.isScrollable()) jb.entry("scrollHeight", schedule.getScrollHeight());
					if (schedule.isTwelveHourClock()) jb.entry("isTwelveHourClock", true);
					if (autoDetectTimeZone) jb.entry("autoDetectTimeZone", true);
					if (enableViewModeControls) jb.entry("enableViewModeControls", true);

					if (schedule.isLazy()) jb.entry("isLazy", true);

					// localised messages
					Locale locale = context.getViewRoot().getLocale();
					String bundleName = context.getApplication().getMessageBundle();
					ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
					if (bundleName == null) bundleName = "org.icefaces.ace.resources.messages";
					if (classLoader == null) classLoader = bundleName.getClass().getClassLoader();
					ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);
					final String MESSAGES_PREFIX = "org.icefaces.ace.component.schedule.";

					jb.beginMap("messages")
						.entry("January", bundle.getString(MESSAGES_PREFIX + "January"))
						.entry("February", bundle.getString(MESSAGES_PREFIX + "February"))
						.entry("March", bundle.getString(MESSAGES_PREFIX + "March"))
						.entry("April", bundle.getString(MESSAGES_PREFIX + "April"))
						.entry("May", bundle.getString(MESSAGES_PREFIX + "May"))
						.entry("June", bundle.getString(MESSAGES_PREFIX + "June"))
						.entry("July", bundle.getString(MESSAGES_PREFIX + "July"))
						.entry("August", bundle.getString(MESSAGES_PREFIX + "August"))
						.entry("September", bundle.getString(MESSAGES_PREFIX + "September"))
						.entry("October", bundle.getString(MESSAGES_PREFIX + "October"))
						.entry("November", bundle.getString(MESSAGES_PREFIX + "November"))
						.entry("December", bundle.getString(MESSAGES_PREFIX + "December"))
						.entry("Jan", bundle.getString(MESSAGES_PREFIX + "Jan"))
						.entry("Feb", bundle.getString(MESSAGES_PREFIX + "Feb"))
						.entry("Mar", bundle.getString(MESSAGES_PREFIX + "Mar"))
						.entry("Apr", bundle.getString(MESSAGES_PREFIX + "Apr"))
						.entry("May", bundle.getString(MESSAGES_PREFIX + "May"))
						.entry("Jun", bundle.getString(MESSAGES_PREFIX + "Jun"))
						.entry("Jul", bundle.getString(MESSAGES_PREFIX + "Jul"))
						.entry("Aug", bundle.getString(MESSAGES_PREFIX + "Aug"))
						.entry("Sep", bundle.getString(MESSAGES_PREFIX + "Sep"))
						.entry("Oct", bundle.getString(MESSAGES_PREFIX + "Oct"))
						.entry("Nov", bundle.getString(MESSAGES_PREFIX + "Nov"))
						.entry("Dec", bundle.getString(MESSAGES_PREFIX + "Dec"))
						.entry("Sunday", bundle.getString(MESSAGES_PREFIX + "Sunday"))
						.entry("Monday", bundle.getString(MESSAGES_PREFIX + "Monday"))
						.entry("Tuesday", bundle.getString(MESSAGES_PREFIX + "Tuesday"))
						.entry("Wednesday", bundle.getString(MESSAGES_PREFIX + "Wednesday"))
						.entry("Thursday", bundle.getString(MESSAGES_PREFIX + "Thursday"))
						.entry("Friday", bundle.getString(MESSAGES_PREFIX + "Friday"))
						.entry("Saturday", bundle.getString(MESSAGES_PREFIX + "Saturday"))
						.entry("Sun", bundle.getString(MESSAGES_PREFIX + "Sun"))
						.entry("Mon", bundle.getString(MESSAGES_PREFIX + "Mon"))
						.entry("Tue", bundle.getString(MESSAGES_PREFIX + "Tue"))
						.entry("Wed", bundle.getString(MESSAGES_PREFIX + "Wed"))
						.entry("Thu", bundle.getString(MESSAGES_PREFIX + "Thu"))
						.entry("Fri", bundle.getString(MESSAGES_PREFIX + "Fri"))
						.entry("Sat", bundle.getString(MESSAGES_PREFIX + "Sat"))
						.entry("EventsThisMonth", bundle.getString(MESSAGES_PREFIX + "EventsThisMonth"))
						.entry("EventsThisWeek", bundle.getString(MESSAGES_PREFIX + "EventsThisWeek"))
						.entry("EventsThisDay", bundle.getString(MESSAGES_PREFIX + "EventsThisDay"))
						.entry("ALLDAY", bundle.getString(MESSAGES_PREFIX + "ALLDAY"))
						.entry("Time", bundle.getString(MESSAGES_PREFIX + "Time"))
						.entry("EventDetails", bundle.getString(MESSAGES_PREFIX + "EventDetails"))
						.entry("Title", bundle.getString(MESSAGES_PREFIX + "Title"))
						.entry("StartDate", bundle.getString(MESSAGES_PREFIX + "StartDate"))
						.entry("StartTime", bundle.getString(MESSAGES_PREFIX + "StartTime"))
						.entry("EndDate", bundle.getString(MESSAGES_PREFIX + "EndDate"))
						.entry("EndTime", bundle.getString(MESSAGES_PREFIX + "EndTime"))
						.entry("AllDayEvent", bundle.getString(MESSAGES_PREFIX + "AllDayEvent"))
						.entry("Location", bundle.getString(MESSAGES_PREFIX + "Location"))
						.entry("Notes", bundle.getString(MESSAGES_PREFIX + "Notes"))
						.entry("Add", bundle.getString(MESSAGES_PREFIX + "Add"))
						.entry("Save", bundle.getString(MESSAGES_PREFIX + "Save"))
						.entry("Delete", bundle.getString(MESSAGES_PREFIX + "Delete"))
						.entry("AreYouSure", bundle.getString(MESSAGES_PREFIX + "AreYouSure"))
						.entry("Yes", bundle.getString(MESSAGES_PREFIX + "Yes"))
						.entry("No", bundle.getString(MESSAGES_PREFIX + "No"))
						.entry("ERROR1", bundle.getString(MESSAGES_PREFIX + "ERROR1"))
						.entry("ERROR2", bundle.getString(MESSAGES_PREFIX + "ERROR2"))
						.entry("ERROR3", bundle.getString(MESSAGES_PREFIX + "ERROR3"))
						.entry("AriaSelectHour", bundle.getString(MESSAGES_PREFIX + "AriaSelectHour"))
						.entry("AriaSelectMinute", bundle.getString(MESSAGES_PREFIX + "AriaSelectMinute"))
						.entry("AriaPreviousMonth", bundle.getString(MESSAGES_PREFIX + "AriaPreviousMonth"))
						.entry("AriaNextMonth", bundle.getString(MESSAGES_PREFIX + "AriaNextMonth"))
						.entry("AriaCurrentMonth", bundle.getString(MESSAGES_PREFIX + "AriaCurrentMonth"))
						.entry("AriaPreviousWeek", bundle.getString(MESSAGES_PREFIX + "AriaPreviousWeek"))
						.entry("AriaNextWeek", bundle.getString(MESSAGES_PREFIX + "AriaNextWeek"))
						.entry("AriaCurrentWeek", bundle.getString(MESSAGES_PREFIX + "AriaCurrentWeek"))
						.entry("AriaPreviousDay", bundle.getString(MESSAGES_PREFIX + "AriaPreviousDay"))
						.entry("AriaNextDay", bundle.getString(MESSAGES_PREFIX + "AriaNextDay"))
						.entry("AriaCurrentDay", bundle.getString(MESSAGES_PREFIX + "AriaCurrentDay"))
					.endMap();

					// behaviors
					encodeClientBehaviors(context, schedule, jb);

					// events
					jb.beginArray("events");

					ArrayList<ScheduleUtils.ScheduleEventDecorator> sortedEvents = 
						new ArrayList<ScheduleUtils.ScheduleEventDecorator>();
					int rowCount = schedule.getRowCount();
					for (int i = 0; i < rowCount; i++) {
						schedule.setRowIndex(i);
						ScheduleEvent scheduleEvent = (ScheduleEvent) schedule.getRowData();
						sortedEvents.add(new ScheduleUtils.ScheduleEventDecorator(scheduleEvent, i));
					}

					Collections.sort(sortedEvents); // sort in chronological order

					for (int i = 0; i < rowCount; i++) {
						ScheduleUtils.ScheduleEventDecorator scheduleEvent = sortedEvents.get(i);
						Date startDate = autoDetectTimeZone ? scheduleEvent.getStartDate() :
								ScheduleUtils.toTimeZoneFromUTC(scheduleEvent.getStartDate(), timeZone);
						Date endDate = autoDetectTimeZone ? scheduleEvent.getEndDate() :
								ScheduleUtils.toTimeZoneFromUTC(scheduleEvent.getEndDate(), timeZone);
						jb.beginMap();
						jb.entry("index", scheduleEvent.getIndex());
						ScheduleUtils.DateIntegerValues startDateValues = ScheduleUtils.getDateIntegerValues(startDate);
						jb.entry("startDate", convertDateToClientFormat(startDateValues));
						jb.entry("startTime", convertTimeToClientFormat(startDateValues));
						ScheduleUtils.DateIntegerValues endDateValues = ScheduleUtils.getDateIntegerValues(endDate);
						jb.entry("endDate", convertDateToClientFormat(endDateValues));
						jb.entry("endTime", convertTimeToClientFormat(endDateValues));
						String title = scheduleEvent.getTitle();
						jb.entry("title", (title != null ? title : ""));
						String location = scheduleEvent.getLocation();
						jb.entry("location", (location != null ? location : ""));
						String notes = scheduleEvent.getNotes();
						jb.entry("notes", (notes != null ? notes : ""));
						String styleClass = scheduleEvent.getStyleClass();
						if (styleClass != null) jb.entry("styleClass", styleClass);
						String id = scheduleEvent.getId();
						jb.entry("isAllDay", scheduleEvent.isAllDay());
						if (id != null) jb.entry("id", id);
						jb.endMap();
					}

					jb.endArray()
				.endMap()
			.endArray()
          .endFunction();

		writer.startElement("div", null);
		writer.writeAttribute("class", "schedule-main ui-widget" + enhancedStylingClass + " schedule-view-" + viewMode + " " 
			+ sideBarClass + " " + eventDetailsClass + " " + tooltipClass + " " + scrollableClass, null);
		writer.endElement("div");

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_currentYear", null);
		writer.writeAttribute("name", clientId + "_currentYear", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", "" + currentDateValues[0], null);
		writer.endElement("input");

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_currentMonth", null);
		writer.writeAttribute("name", clientId + "_currentMonth", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", "" + currentDateValues[1], null);
		writer.endElement("input");

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_currentDay", null);
		writer.writeAttribute("name", clientId + "_currentDay", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", "" + currentDateValues[2], null);
		writer.endElement("input");

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_selectedDate", null);
		writer.writeAttribute("name", clientId + "_selectedDate", null);
		writer.writeAttribute("type", "hidden", null);
		if (selectedDateString != null && !"".equals(selectedDateString) && !selectCurrentDate) {
			writer.writeAttribute("value", selectedDateString, null);
		} else {
			writer.writeAttribute("value", currentDateValues[0]
				+ "-" + addLeadingZero((currentDateValues[1] + 1))
				+ "-" + addLeadingZero(currentDateValues[2]), null);
		}
		writer.endElement("input");

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_viewMode", null);
		writer.writeAttribute("name", clientId + "_viewMode", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", viewMode, null);
		writer.endElement("input");

		writer.startElement("input", null);
		writer.writeAttribute("id", clientId + "_viewModeChange", null);
		writer.writeAttribute("name", clientId + "_viewModeChange", null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", "", null);
		writer.endElement("input");

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write(jb.toString());
		if (params.containsKey(clientId + "_navigation") && schedule.isLazy() && rowCount == 0) {
			// force full markup update when there are no events to render
			writer.write("// " + System.currentTimeMillis());
		}
		writer.endElement("script");

		writer.endElement("div");
	}

	private String convertDateToClientFormat(ScheduleUtils.DateIntegerValues values) {
		return (values.getYear() + "-" + addLeadingZero(values.getMonth() + 1) + "-" + addLeadingZero(values.getDay()));
	}

	private String convertTimeToClientFormat(ScheduleUtils.DateIntegerValues values) {
		return (addLeadingZero(values.getHour()) + ":" + addLeadingZero(values.getMinute()));
	}

	private String addLeadingZero(int value) {
		if (value < 10) return ("0" + value);
		return ("" + value);
	}
}