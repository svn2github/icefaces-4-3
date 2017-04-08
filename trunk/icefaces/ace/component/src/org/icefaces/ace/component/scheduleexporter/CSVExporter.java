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
import java.util.List;
import java.util.Collection;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.icefaces.ace.model.schedule.ScheduleEvent;

import org.icefaces.ace.component.schedule.Schedule;

public class CSVExporter extends Exporter {
	
    @Override
	public String export(FacesContext facesContext, ScheduleExporter component, Schedule schedule) throws IOException {
		setUp(component, schedule);
		StringBuilder builder = new StringBuilder();
    	
    	if (includeHeaders) {
			builder.append("\"Title\",\"Start Date\",\"End Date\",\"Location\",\"Notes\"\n");
		}
    	
		int rowCount = schedule.getRowCount();
    	int first = 0;

    	for (int i = first; i < rowCount; i++) {
    		schedule.setRowIndex(i);
			Object rowData = schedule.getRowData();

			if (rowData instanceof ScheduleEvent) {
				addScheduleEventData(builder, (ScheduleEvent) rowData);
			}
			builder.append("\n");
		}
    	
    	schedule.setRowIndex(-1);

		byte[] bytes;
		try {
			bytes = builder.toString().getBytes(encodingType);
		} catch (Exception e) {
			bytes = builder.toString().getBytes("UTF-8");
			encodingType = "UTF-8";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Unsupported encoding specified in ace:scheduleExporter component with id '"+component.getId()+"' in view "+facesContext.getViewRoot().getViewId()+". Defaulting to UTF-8 instead.");
		}
		
		return registerResource(bytes, filename + ".csv", "text/csv; charset=" + encodingType);
	}

	protected void addScheduleEventData(StringBuilder builder, ScheduleEvent event) throws IOException {
		if (event == null) return;
		else {
			String value;
			value = event.getTitle();
			value = value == null ? "" : value.trim();
			builder.append("\"" + value + "\",");

			Date startDate = event.getStartDate();
			value = startDate != null ? startDate.toString() : "";
			builder.append("\"" + value + "\",");

			Date endDate = event.getEndDate();
			value = endDate != null ? endDate.toString() : "";
			builder.append("\"" + value + "\",");

			value = event.getLocation();
			value = value == null ? "" : value.trim();
			builder.append("\"" + value + "\",");

			value = event.getNotes();
			value = value == null ? "" : value.trim();
			builder.append("\"" + value + "\"");
		}
	}
}
