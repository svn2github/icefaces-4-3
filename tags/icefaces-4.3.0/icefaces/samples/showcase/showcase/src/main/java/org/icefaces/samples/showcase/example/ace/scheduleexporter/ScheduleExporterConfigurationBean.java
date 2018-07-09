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

package org.icefaces.samples.showcase.example.ace.scheduleexporter;

import org.icefaces.samples.showcase.example.ace.schedule.DefaultDistributionEventGenerator;
import org.icefaces.ace.model.schedule.ScheduleEvent;

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

@ManagedBean(name= ScheduleExporterConfigurationBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ScheduleExporterConfigurationBean implements Serializable {
    public static final String BEAN_NAME = "scheduleExporterConfigurationBean";
	public String getBeanName() { return BEAN_NAME; }
    
	private List<ScheduleEvent> eventList = new ArrayList<ScheduleEvent>();
	private Random randomEvents = new Random();
	private Random randomDays = new Random();
	private Random randomHours = new Random();
	private Random randomMinutes = new Random();
	private Random randomDurationHours = new Random();

    private List<ScheduleEvent> events;

	public ScheduleExporterConfigurationBean() {
		events = (new DefaultDistributionEventGenerator()).getEvents();
	}

	public List<ScheduleEvent> getEvents() { return events; }
	public void setEvents(List<ScheduleEvent> events) { this.events = events; }

	private String viewMode = "month";
	public String getViewMode() { return viewMode; }
	public void setViewMode(String viewMode) { this.viewMode = viewMode; }

	private String type = "csv";
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }

	private boolean sortAscending = true;
	public boolean isSortAscending() { return sortAscending; }
	public void setSortAscending(boolean sortAscending) { this.sortAscending = sortAscending; }

	private boolean exportAllEvents = false;
	public boolean isExportAllEvents() { return exportAllEvents; }
	public void setExportAllEvents(boolean exportAllEvents) { this.exportAllEvents = exportAllEvents; }

	private String fieldsToExport = "title,startdate,enddate,location,notes";
	public String getFieldsToExport() { return fieldsToExport; }
	public void setFieldsToExport(String fieldsToExport) { this.fieldsToExport = fieldsToExport; }

	private String datePattern = "yyyy-MM-dd HH:mm";
	public String getDatePattern() { return datePattern; }
	public void setDatePattern(String datePattern) { this.datePattern = datePattern; }

	private String sortBy = "startdate";
	public String getSortBy() { return sortBy; }
	public void setSortBy(String sortBy) { this.sortBy = sortBy; }
}