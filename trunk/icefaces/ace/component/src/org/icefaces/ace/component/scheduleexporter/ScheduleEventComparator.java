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

package org.icefaces.ace.component.scheduleexporter;

import java.util.Comparator;
import org.icefaces.ace.model.schedule.ScheduleEvent;

public class ScheduleEventComparator implements Comparator {

	private String field;
	private boolean isAscending;

	public ScheduleEventComparator(String field, boolean isAscending) {
		this.field = field;
		this.isAscending = isAscending;
	}
	
	public int compare(Object obj1, Object obj2) {
		int result;
		ScheduleEvent event1 = (ScheduleEvent) obj1;
		ScheduleEvent event2 = (ScheduleEvent) obj2;

		if (event1 == null) {
			if (event2 == null) result = 0;
			else result = 1;
		} else if (event2 == null) {
			result = -1;
		} else {
			Object value1 = null;
			Object value2 = null;
			if ("id".equals(field)) {
				value1 = event1.getId();
				value2 = event2.getId();
			} else if ("title".equals(field)) {
				value1 = event1.getTitle();
				value2 = event2.getTitle();
			} else if ("location".equals(field)) {
				value1 = event1.getLocation();
				value2 = event2.getLocation();
			} else if ("styleclass".equals(field)) {
				value1 = event1.getStyleClass();
				value2 = event2.getStyleClass();
			} else if ("notes".equals(field)) {
				value1 = event1.getNotes();
				value2 = event2.getNotes();
			} else if ("enddate".equals(field)) {
				value1 = event1.getEndDate();
				value2 = event2.getEndDate();
			} else {
				value1 = event1.getStartDate();
				value2 = event2.getStartDate();
			}
			if (value1 == null) {
				if (value2 == null) result = 0;
				else result = 1;
			} else if (value2 == null) {
				result = -1;
			} else {
				result = ((Comparable) value1).compareTo(value2);
			}
		}
		
		return isAscending ? result : -1 * result;
	}
}