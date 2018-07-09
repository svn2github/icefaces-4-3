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
import java.util.List;
import java.util.Collection;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.icefaces.ace.model.schedule.ScheduleEvent;

import org.icefaces.ace.component.schedule.Schedule;
import org.icefaces.ace.util.XMLChar;

public class XMLExporter extends Exporter {

    @Override
	public String export(FacesContext facesContext, ScheduleExporter component, Schedule schedule) throws IOException {
		setUp(component, schedule);
		StringBuilder builder = new StringBuilder();
    	
		try {
			"".toString().getBytes(encodingType);
		} catch (Exception e) {
			encodingType = "UTF-8";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Unsupported encoding specified in ace:scheduleExporter component with id '"+component.getId()+"' in view "+facesContext.getViewRoot().getViewId()+". Defaulting to UTF-8 instead.");
		}

    	builder.append("<?xml version=\"1.0\" encoding=\"" + encodingType + "\"?>\n");
    	builder.append("<" + schedule.getId() + ">\n");
		
		int rowCount = schedule.getRowCount();
    	int first = 0;
		ArrayList<ScheduleEvent> eventsToExport = new ArrayList<ScheduleEvent>();

    	String tagName = "event";
    	for (int i = first; i < rowCount; i++) {
    		schedule.setRowIndex(i);
			Object rowData = schedule.getRowData();

			if (rowData instanceof ScheduleEvent) {
				if (exportAllEvents) {
					eventsToExport.add((ScheduleEvent) rowData);
				} else {
					ScheduleEvent event = (ScheduleEvent) rowData;
					if (isWithinRange(event)) eventsToExport.add(event);
				}
			}
		}

    	schedule.setRowIndex(-1);

		sortEvents(eventsToExport);

		int size = eventsToExport.size();
		for (int i = 0; i < size; i++) {
			builder.append("\t<" + tagName + ">\n");
			addScheduleEventData(builder, eventsToExport.get(i));
			builder.append("\t</" + tagName + ">\n");
		}
    	
    	builder.append("</" + schedule.getId() + ">");

		byte[] bytes = builder.toString().getBytes(encodingType);
		
		return registerResource(bytes, filename + ".xml", "text/xml; charset=" + encodingType);
	}

	protected void addScheduleEventData(StringBuilder builder, ScheduleEvent event) throws IOException {
		if (event == null) return;
		else {
			int numFields = fields.size();
			for (int i = 0; i < numFields; i++) {
				Field field = fields.get(i);
				String value = null;
				String tag = "_";
				if (Field.ID == field) {
					value = event.getId();
					tag = "id";
				} else if (Field.TITLE == field) {
					value = event.getTitle();
					tag = "title";
				} else if (Field.STARTDATE == field) {
					Date startDate = event.getStartDate();
					value = startDate != null ? formatDate(startDate) : "";;
					tag = "startDate";
				} else if (Field.ENDDATE == field) {
					Date endDate = event.getEndDate();
					value = endDate != null ? formatDate(endDate) : "";
					tag = "endDate";
				} else if (Field.LOCATION == field) {
					value = event.getLocation();
					tag = "location";
				} else if (Field.STYLECLASS == field) {
					value = event.getStyleClass();
					tag = "styleClass";
				} else if (Field.NOTES == field) {
					value = event.getNotes();
					tag = "notes";
				}
				value = value == null ? "" : value.trim();
				builder.append("\t\t<" + tag + ">");
				builder.append(encloseInCDATASection(value));
				builder.append("</" + tag + ">\n");
			}
		}
	}

	protected String encloseInCDATASection(String string) {
		if (string.indexOf("<") > -1 || string.indexOf(">") > -1) {
			return "<![CDATA[" + string + "]]>";
		} else {
			return string;
		}
	}
}
