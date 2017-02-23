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
import org.icefaces.samples.showcase.dataGenerators.DefaultDistributionEventGenerator;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

@ManagedBean(name= ScheduleCustomStylingBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ScheduleCustomStylingBean implements Serializable {
    public static final String BEAN_NAME = "scheduleCustomStylingBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private List<ScheduleEvent> events;

	public ScheduleCustomStylingBean() {
		events = (new DefaultDistributionEventGenerator()).getEvents();
	}

	public List<ScheduleEvent> getEvents() { return events; }
	public void setEvents(List<ScheduleEvent> events) { this.events = events; }

	private String viewMode = "month";
	public String getViewMode() { return viewMode; }
	public void setViewMode(String viewMode) { this.viewMode = viewMode; }

	private String timeZone;
	public String getTimeZone() { return timeZone; }
	public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    private List<SelectItem> timeZoneList = null;
    public List<SelectItem> getTimeZoneList() {
		if (timeZoneList == null) {
			timeZoneList = new ArrayList<SelectItem>();
			timeZoneList.add(new SelectItem(TimeZone.getDefault().getID(),
				"System Default (" + TimeZone.getDefault().getID() + ")"));
			timeZoneList.add(new SelectItem("America/Vancouver"));
			timeZoneList.add(new SelectItem("America/Edmonton"));
			timeZoneList.add(new SelectItem("America/Toronto"));
		}
		return timeZoneList;
	}
    public void setTimeZoneList(List<SelectItem> timeZoneList) { this.timeZoneList = timeZoneList; }
}