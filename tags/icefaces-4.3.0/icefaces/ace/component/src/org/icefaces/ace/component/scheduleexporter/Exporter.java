/*
 * Original Code Copyright Prime Technology.
 * Subsequent Code Modifications Copyright 2011-2014 ICEsoft Technologies Canada Corp. (c)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE THIS CODE HAS BEEN MODIFIED FROM ORIGINAL FORM
 *
 * Subsequent Code Modifications have been made and contributed by ICEsoft Technologies Canada Corp. (c).
 *
 * Code Modification 1: Integrated with ICEfaces Advanced Component Environment.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 *
 * Code Modification 2: [ADD BRIEF DESCRIPTION HERE]
 * Contributors: ______________________
 * Contributors: ______________________
 */
package org.icefaces.ace.component.scheduleexporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.text.SimpleDateFormat;

import org.icefaces.ace.component.schedule.Schedule;
import org.icefaces.ace.component.schedule.ScheduleUtils;
import org.icefaces.ace.model.schedule.ScheduleEvent;

import org.icefaces.application.ResourceRegistry;

import java.util.logging.Logger;

public abstract class Exporter {

	enum Field {
		ID,
		TITLE,
		STARTDATE,
		ENDDATE,
		LOCATION,
		STYLECLASS,
		NOTES;
	}

	protected final static Logger logger = Logger.getLogger(Exporter.class.getName());

	protected static final Pattern HTML_TAG_PATTERN = Pattern.compile("\\<.*?\\>");
	protected String filename;
	protected String encodingType;
	protected MethodExpression preProcessor;
	protected MethodExpression postProcessor;
	protected boolean includeHeaders;
	protected String pdfFont;
	protected List<Field> fields;
	protected SimpleDateFormat simpleDateFormat;
	protected boolean exportAllEvents;
	protected long startTime;
	protected long endTime;
	protected ScheduleEventComparator comparator;
	
	public void setUp(ScheduleExporter component, Schedule schedule) {
		filename = component.getFileName();
		if (filename == null) {
			filename = "data";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Required attribute 'file' is null in ace:scheduleExporter component with id "+component.getId()+" in view "+FacesContext.getCurrentInstance().getViewRoot().getViewId()+".");
		}
		encodingType = component.getEncoding();
		preProcessor = component.getPreProcessor();
		postProcessor = component.getPostProcessor();
		includeHeaders = component.isIncludeHeaders();
		pdfFont = component.getPdfFont();

		String fieldsToExport = component.getFieldsToExport();
		if (fieldsToExport == null) fieldsToExport = "title,startdate,enddate,location,notes";
		String[] fieldsToExportArray = fieldsToExport.split(",");
		fields = new ArrayList<Field>();
		for (int i = 0; i < fieldsToExportArray.length; i++) {
			String field = fieldsToExportArray[i];
			field = field.trim();
			field = field.toLowerCase();
			if ("id".equals(field)) fields.add(Field.ID);
			else if ("title".equals(field)) fields.add(Field.TITLE);
			else if ("startdate".equals(field)) fields.add(Field.STARTDATE);
			else if ("enddate".equals(field)) fields.add(Field.ENDDATE);
			else if ("location".equals(field)) fields.add(Field.LOCATION);
			else if ("styleclass".equals(field)) fields.add(Field.STYLECLASS);
			else if ("notes".equals(field)) fields.add(Field.NOTES);
		}

		String datePattern = component.getDatePattern();
		datePattern = datePattern == null ? "" : datePattern.trim();
		datePattern = "".equals(datePattern) ? "yyyy-MM-dd HH:mm" : datePattern;
		simpleDateFormat = new SimpleDateFormat(datePattern);

		exportAllEvents = component.isExportAllEvents();
		if (schedule.isLazy()) exportAllEvents = false;
		if (!exportAllEvents) {
			int[] currentDateValues = schedule.getCurrentDateValues();
			int currentYear = currentDateValues[0];
			int currentMonth = currentDateValues[1];
			int currentDay = currentDateValues[2];
			String viewMode = schedule.getViewMode();
			viewMode = viewMode != null ? viewMode.toLowerCase() : "month";
			Date startDate, endDate;
			if ("day".equals(viewMode)) {
				startDate = ScheduleUtils.getDateFromIntegerValues(new ScheduleUtils.DateIntegerValues(currentYear,
					currentMonth, currentDay, 0, 0, 0));
				endDate = ScheduleUtils.getDateFromIntegerValues(new ScheduleUtils.DateIntegerValues(currentYear,
					currentMonth, currentDay, 23, 59, 59));
			} else if ("week".equals(viewMode)) {
				int[] lastDayOfWeek = ScheduleUtils.determineLastDayOfWeek(currentYear, currentMonth, currentDay);
				startDate = ScheduleUtils.getDateFromIntegerValues(new ScheduleUtils.DateIntegerValues(currentYear,
					currentMonth, currentDay, 0, 0, 0));
				endDate = ScheduleUtils.getDateFromIntegerValues(new ScheduleUtils.DateIntegerValues(lastDayOfWeek[0],
					lastDayOfWeek[1], lastDayOfWeek[2], 23, 59, 59));
			} else {
				int lastDayOfMonth = ScheduleUtils.determineLastDayOfMonth(currentYear, currentMonth);
				startDate = ScheduleUtils.getDateFromIntegerValues(new ScheduleUtils.DateIntegerValues(currentYear,
					currentMonth, currentDay, 0, 0, 0));
				endDate = ScheduleUtils.getDateFromIntegerValues(new ScheduleUtils.DateIntegerValues(currentYear,
					currentMonth, lastDayOfMonth, 23, 59, 59));
			}
			startTime = startDate.getTime();
			endTime = endDate.getTime();
		}

		String sortBy = component.getSortBy();
		boolean isAscending = component.isSortAscending();
		if (sortBy == null) sortBy = "startdate";
		sortBy = sortBy.trim();
		sortBy = sortBy.toLowerCase();
		comparator = new ScheduleEventComparator(sortBy, isAscending);
	}

    public abstract String export(FacesContext facesContext, ScheduleExporter component, Schedule schedule) throws IOException;

	protected String registerResource(byte[] bytes, String filename, String contentType) {
		ExporterResource resource = new ExporterResource(bytes);
		resource.setContentType(contentType);
		Map<String, String> headers = resource.getResponseHeaders();
		headers.put("Expires", "0");
		headers.put("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		headers.put("Pragma", "public");
		headers.put("Content-disposition", "attachment; filename=" + filename);
		String path = ResourceRegistry.addSessionResource(resource);
		return path;
	}

	protected String formatDate(Date date) {
		return simpleDateFormat.format(date);
	}

	protected boolean isWithinRange(ScheduleEvent event) {
		long eventStartTime = event.getStartDate().getTime();
		long eventEndTime = event.getEndDate().getTime();
		// starts in the current period
		if (eventStartTime >= startTime && eventStartTime <= endTime
			// doesn't start in the current period but ends in it
			|| eventEndTime >= startTime && eventEndTime <= endTime
			// neither starts nor ends in the current period but encompasses it
			|| eventStartTime <= startTime && eventEndTime >= endTime) {
				return true;
		} else {
			return false;
		}
	}

	protected void sortEvents(List<ScheduleEvent> events) {
		Collections.sort(events, comparator);
	}
}
