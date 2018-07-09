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
import javax.faces.model.SelectItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

@ManagedBean(name= ScheduleConfigurationBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ScheduleConfigurationBean implements Serializable {
    public static final String BEAN_NAME = "scheduleConfigurationBean";
	public String getBeanName() { return BEAN_NAME; }

    private List<ScheduleEvent> events;

	public ScheduleConfigurationBean() {
		events = (new DefaultDistributionEventGenerator()).getEvents();
	}

	public List<ScheduleEvent> getEvents() { return events; }
	public void setEvents(List<ScheduleEvent> events) { this.events = events; }

	private String viewMode = "month";
	public String getViewMode() { return viewMode; }
	public void setViewMode(String viewMode) { this.viewMode = viewMode; }

	private boolean scrollable;
	public boolean isScrollable() { return scrollable; }
	public void setScrollable(boolean scrollable) { this.scrollable = scrollable; }

	private int scrollHeight = 600;
	public int getScrollHeight() { return scrollHeight; }
	public void setScrollHeight(int scrollHeight) { this.scrollHeight = scrollHeight; }

	private String timeZone = "UTC";
	public String getTimeZone() { return timeZone; }
	public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    private List<SelectItem> timeZoneList = null;
    public List<SelectItem> getTimeZoneList() {
		if (timeZoneList == null) {
			timeZoneList = new ArrayList<SelectItem>();
			timeZoneList.add(new SelectItem("UTC"));
			timeZoneList.add(new SelectItem(TimeZone.getDefault().getID(),
				"System Default (" + TimeZone.getDefault().getID() + ")"));
			timeZoneList.add(new SelectItem("America/Vancouver"));
			timeZoneList.add(new SelectItem("America/Edmonton"));
			timeZoneList.add(new SelectItem("America/Toronto"));
		}
		return timeZoneList;
	}
    public void setTimeZoneList(List<SelectItem> timeZoneList) { this.timeZoneList = timeZoneList; }

	private String sideBar = "right";
	public String getSideBar() { return sideBar; }
	public void setSideBar(String sideBar) { this.sideBar = sideBar; }

	private String eventDetails = "popup";
	public String getEventDetails() { return eventDetails; }
	public void setEventDetails(String eventDetails) { this.eventDetails = eventDetails; }

	private boolean enhancedStyling = true;
	public boolean isEnhancedStyling() { return enhancedStyling; }
	public void setEnhancedStyling(boolean enhancedStyling) { this.enhancedStyling = enhancedStyling; }

	private boolean twelveHourClock = false;
	public boolean isTwelveHourClock() { return twelveHourClock; }
	public void setTwelveHourClock(boolean twelveHourClock) { this.twelveHourClock = twelveHourClock; }

	private boolean tooltip = false;
	public boolean isTooltip() { return tooltip; }
	public void setTooltip(boolean tooltip) { this.tooltip = tooltip; }

	private int defaultDuration = 60;
	public int getDefaultDuration() { return defaultDuration; }
	public void setDefaultDuration(int defaultDuration) { this.defaultDuration = defaultDuration; }
}