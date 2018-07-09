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

package org.icefaces.samples.showcase.example.ace.schedule;

import org.icefaces.ace.model.schedule.ScheduleEvent;
import org.icefaces.ace.model.schedule.LazyScheduleEventList;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@ManagedBean(name= ScheduleLazyLoadingBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ScheduleLazyLoadingBean implements Serializable {
    public static final String BEAN_NAME = "scheduleLazyLoadingBean";
	public String getBeanName() { return BEAN_NAME; }

    private List<ScheduleEvent> eventList;

	public ScheduleLazyLoadingBean() {
		eventList = (new RandomEventGenerator()).getEvents();
	}

	private LazyScheduleEventList lazyScheduleEventList =
		new LazyScheduleEventList() {
			public List<ScheduleEvent> load(Date startDate, Date endDate) {
				List<ScheduleEvent> events = new ArrayList<ScheduleEvent>();
				long startTime = startDate.getTime();
				long endTime = endDate.getTime();
				int size = eventList.size();
				for (int i = 0; i < size; i++) {
					ScheduleEvent event = eventList.get(i);
					long eventStartTime = event.getStartDate().getTime();
					long eventEndTime = event.getEndDate().getTime();
					// add events...
					// ...which start in the current period...
					if (eventStartTime >= startTime && eventStartTime <= endTime
						// ...which don't start in the current period but end in it...
						|| eventEndTime >= startTime && eventEndTime <= endTime
						// ...and which neither start nor end in the current period but encompass it.
						|| eventStartTime <= startTime && eventEndTime >= endTime) {
							events.add(event);
					}
				}
				return events;
			}

			public boolean add(ScheduleEvent e) {
				return eventList.add(e);
			}

			public ScheduleEvent set(int index, ScheduleEvent element) {
				ScheduleEvent oldElement = get(index);
				oldElement.setStartDate(element.getStartDate());
				oldElement.setEndDate(element.getEndDate());
				oldElement.setAllDay(element.isAllDay());
				oldElement.setTitle(element.getTitle());
				oldElement.setLocation(element.getLocation());
				oldElement.setNotes(element.getNotes());
				oldElement.setStyleClass(element.getStyleClass());
				oldElement.setId(element.getId());
				return element;
			}

			public ScheduleEvent remove(int index) {
				ScheduleEvent element = get(index);
				eventList.remove(element);
				return element;
			}
		};

	public LazyScheduleEventList getLazyScheduleEventList() {
		return lazyScheduleEventList;
	}

	public void setLazyScheduleEventList(LazyScheduleEventList lazyScheduleEventList) {
		this.lazyScheduleEventList = lazyScheduleEventList;
	}

	private String viewMode = "month";
	public String getViewMode() { return viewMode; }
	public void setViewMode(String viewMode) { this.viewMode = viewMode; }
}