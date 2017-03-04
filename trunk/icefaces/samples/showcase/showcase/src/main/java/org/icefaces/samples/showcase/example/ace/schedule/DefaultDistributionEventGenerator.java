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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/**
 * This class generates a semi-random sample of events of various types and durations
 * to showcase the various possible rendering scenarios of events in the ace:schedule 
 * component.
 */
public class DefaultDistributionEventGenerator implements Serializable {

	private List<ScheduleEvent> events = null;

	public List<ScheduleEvent> getEvents() {

		if (events == null) {

			events = new ArrayList<ScheduleEvent>();

			Calendar cal = Calendar.getInstance();
			int currentYear = cal.get(Calendar.YEAR);
			int currentMonth = cal.get(Calendar.MONTH);

			// current month
			populateMonth(currentYear, currentMonth, events);

			// previous month
			int year = currentYear;
			int month = currentMonth;
			if (currentMonth == 0) {
				month = 11;
				year -= 1;
			} else month -= 1;
			populateMonth(year, month, events);

			// two months ago
			year = currentYear;
			month = currentMonth;
			if (currentMonth == 0) {
				month = 10;
				year -= 1;
			} else if (currentMonth == 1) {
				month = 11;
				year -= 1;
			} else month -= 2;
			populateMonth(year, month, events);

			// next month
			year = currentYear;
			month = currentMonth;
			if (currentMonth == 11) {
				month = 0;
				year += 1;
			} else month += 1;
			populateMonth(year, month, events);

			// two months ahead
			year = currentYear;
			month = currentMonth;
			if (currentMonth == 10) {
				month = 0;
				year += 1;
			} else if (currentMonth == 11) {
				month = 1;
				year += 1;
			} else month += 2;
			populateMonth(year, month, events);
		}

		return events;
	};

	private void populateMonth(int year, int month, List<ScheduleEvent> events) {

		// initialize and shuffle the list of days on which multiple-day events can start
		List<Integer> multipleDayEventsDays = new ArrayList<Integer>() {{
			add(new Integer(4));
			add(new Integer(11));
			add(new Integer(19));
			add(new Integer(25));
		}};
		Collections.shuffle(multipleDayEventsDays);

		// initialize and shuffle the list of days on which overlapping events can start
		List<Integer> overlappingEventsDays = new ArrayList<Integer>() {{
			add(new Integer(2));
			add(new Integer(9));
			add(new Integer(15));
			add(new Integer(22));
		}};
		Collections.shuffle(overlappingEventsDays);

		// initialize and shuffle the list of days on which regular events can start
		List<Integer> regularEventsDays = new ArrayList<Integer>() {{
			add(new Integer(1));
			add(new Integer(3));
			add(new Integer(5));
			add(new Integer(6));
			add(new Integer(7));
			add(new Integer(8));
			add(new Integer(10));
			add(new Integer(12));
			add(new Integer(13));
			add(new Integer(14));
			add(new Integer(16));
			add(new Integer(17));
			add(new Integer(18));
			add(new Integer(20));
			add(new Integer(21));
			add(new Integer(23));
			add(new Integer(24));
			add(new Integer(26));
			add(new Integer(27));
			add(new Integer(28));
		}};
		Collections.shuffle(regularEventsDays);

		Random randomHours = new Random();
		Random randomDays = new Random();

		int day, hour, minute, duration;

		//events.add(generateEvent(year, month, day, hour, minute, duration, false, "title", "location", "notes", "class"));

		// add allday events
		day = multipleDayEventsDays.get(0);
		hour = 0;
		minute = 0;
		duration = (randomDays.nextInt(3) + 1) * 1440; // 1-3 days
		events.add(generateEvent(year, month, day, hour, minute, duration, true, "Public Event 1", "Some public location", "Notes about public event...", "schedule-green"));
		day = multipleDayEventsDays.get(1);
		duration = (randomDays.nextInt(3) + 1) * 1440; // 1-3 days
		events.add(generateEvent(year, month, day, hour, minute, duration, true, "Public Event 2", "Some public location", "Notes about public event...", "schedule-green"));

		// add very long event
		day = multipleDayEventsDays.get(2);
		hour = 15;
		minute = 0;
		duration = 2 * 1440; // 2 days
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Very Long Event", "Some location", "Notes about event...", ""));

		// add cluster of 3 overlapping events
		day = overlappingEventsDays.get(0);
		hour = randomHours.nextInt(8) + 8;
		minute = 0;
		duration = 240; // 4 hours
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Overlapping Event 1", "Some location", "Notes about event...", ""));
		hour += 1;
		duration = 180;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Overlapping Event 2", "Some location", "Notes about event...", ""));
		minute = 30;
		duration = 60;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Overlapping Event 3", "Some location", "Notes about event...", ""));

		// add 2 clusters of 2 overlapping events
		day = overlappingEventsDays.get(1);
		hour = randomHours.nextInt(8) + 8;
		minute = 0;
		duration = 180; // 3 hours
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Overlapping Event 4", "Some location", "Notes about event...", ""));
		hour += 1;
		duration = 90;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Overlapping Event 5", "Some location", "Notes about event...", ""));

		day = overlappingEventsDays.get(2);
		hour = randomHours.nextInt(8) + 8;
		minute = 0;
		duration = 150; // 2.5 hours
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Overlapping Event 6", "Some location", "Notes about event...", ""));
		hour += 1;
		minute = 30;
		duration = 60;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Overlapping Event 7", "Some location", "Notes about event...", ""));

		// add regular events, making sure some days have 1 event, others have 2 events and a few have no events

		// very short events
		day = regularEventsDays.get(0);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		duration = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Very Short Event 1", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(1);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Very Short Event 2", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(2);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Very Short Event 3", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(3);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Very Short Event 4", "Some location", "Notes about event...", ""));

		// short events
		day = regularEventsDays.get(4);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		duration = 60;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Short Event 1", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(5);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Short Event 2", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(6);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Short Event 3", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(7);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Short Event 4", "Some location", "Notes about event...", ""));

		// meetings (1.5 hours)
		day = regularEventsDays.get(8);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		duration = 90;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Meeting 1", "Some meeting room", "Notes about meeting...", "schedule-yellow"));
		day = regularEventsDays.get(9);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Meeting 2", "Some meeting room", "Notes about meeting...", "schedule-yellow"));
		day = regularEventsDays.get(10);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Meeting 3", "Some meeting room", "Notes about meeting...", "schedule-yellow"));
		day = regularEventsDays.get(11);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Meeting 4", "Some meeting room", "Notes about meeting...", "schedule-yellow"));

		// conferences (2 hours)
		day = regularEventsDays.get(12);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		duration = 120;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Conference 1", "Some conference centre", "Notes about conference...", "conference"));
		day = regularEventsDays.get(13);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Conference 2", "Some conference centre", "Notes about conference...", "conference"));
		day = regularEventsDays.get(14);
		hour = randomHours.nextInt(4) + 8;
		minute = 0;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Conference 3", "Some conference centre", "Notes about conference...", "conference"));
		day = regularEventsDays.get(15);
		hour = randomHours.nextInt(4) + 8;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Conference 4", "Some conference centre", "Notes about conference...", "conference"));

		// long event
		day = regularEventsDays.get(16);
		hour = randomHours.nextInt(4) + 7;
		minute = 0;
		duration = 180;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Long Event 1", "Some location", "Notes about event...", ""));

		// important event
		day = regularEventsDays.get(17);
		hour = randomHours.nextInt(4) + 10;
		minute = 0;
		duration = 120;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Important Event", "Some location", "Important notes...", "schedule-red"));

		// now add more events of every type again but happening in the afternoon

		// very short events
		day = regularEventsDays.get(8);
		hour = randomHours.nextInt(4) + 17;
		minute = 0;
		duration = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Very Short Event 5", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(9);
		hour = randomHours.nextInt(4) + 17;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Very Short Event 6", "Some location", "Notes about event...", ""));

		// short events
		day = regularEventsDays.get(10);
		hour = randomHours.nextInt(4) + 17;
		minute = 0;
		duration = 60;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Short Event 5", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(11);
		hour = randomHours.nextInt(4) + 17;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Short Event 6", "Some location", "Notes about event...", ""));

		// meetings (1.5 hours)
		day = regularEventsDays.get(12);
		hour = randomHours.nextInt(4) + 17;
		minute = 0;
		duration = 90;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Meeting 5", "Some meeting room", "Notes about meeting...", "schedule-yellow"));
		day = regularEventsDays.get(13);
		hour = randomHours.nextInt(4) + 17;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Meeting 6", "Some meeting room", "Notes about meeting...", "schedule-yellow"));

		// conferences (2 hours)
		day = regularEventsDays.get(0);
		hour = randomHours.nextInt(4) + 17;
		minute = 0;
		duration = 120;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Conference 5", "Some conference centre", "Notes about conference...", "conference"));
		day = regularEventsDays.get(1);
		hour = randomHours.nextInt(4) + 17;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Conference 6", "Some conference centre", "Notes about conference...", "conference"));

		// long events
		day = regularEventsDays.get(2);
		hour = randomHours.nextInt(4) + 17;
		minute = 0;
		duration = 180;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Long Event 2", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(3);
		hour = randomHours.nextInt(4) + 17;
		minute = 30;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Long Event 3", "Some location", "Notes about event...", ""));
		day = regularEventsDays.get(4);
		hour = randomHours.nextInt(4) + 17;
		minute = 0;
		events.add(generateEvent(year, month, day, hour, minute, duration, false, "Long Event 4", "Some location", "Notes about event...", ""));
	}

	/**
	 * duration is in minutes
	 */
	private ScheduleEvent generateEvent(int year, int month, int day, int hour, int minute, int duration, boolean isAllDay,
		String title, String location, String notes, String styleClass) {

		ScheduleEvent event = new ScheduleEvent();
		event.setStartDate(getDate(year, month, day, hour, minute, 0));
		event.setEndDate(getDate(year, month, day, hour, minute, duration));
		event.setAllDay(isAllDay);
		event.setTitle(title);
		event.setLocation(location);
		event.setNotes(notes);
		event.setStyleClass(styleClass);

		return event;
	}
	
	private Date getDate(int year, int month, int day, int hour, int minute, int duration) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute);
		cal.add(Calendar.MINUTE, duration);
		return cal.getTime();
	}
}
