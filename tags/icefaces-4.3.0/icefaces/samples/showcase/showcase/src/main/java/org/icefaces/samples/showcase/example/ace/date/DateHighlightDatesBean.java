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

package org.icefaces.samples.showcase.example.ace.date;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import org.icefaces.ace.event.DateSelectEvent;

@ManagedBean(name= DateHighlightDatesBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DateHighlightDatesBean implements Serializable {
    public static final String BEAN_NAME = "dateHighlightDates";
	public String getBeanName() { return BEAN_NAME; }

    private Date selectedDate;
	private List<Date> dates;
    
    public DateHighlightDatesBean() {
        this.selectedDate = new Date(System.currentTimeMillis());
		this.dates = new ArrayList<Date>();
		Calendar cal =  Calendar.getInstance();
		cal.setTime(this.selectedDate);
		int currentYear = cal.get(Calendar.YEAR);

		// add first and last days of this year's months
		// January
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 31);
		this.dates.add(cal.getTime());

		// February
		cal.set(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		if (((currentYear % 4 == 0) && (currentYear % 100 != 0)) || (currentYear % 400 == 0))
			cal.set(Calendar.DATE, 29);
		else
			cal.set(Calendar.DATE, 28);
		this.dates.add(cal.getTime());

		// March
		cal.set(Calendar.MONTH, 2);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 31);
		this.dates.add(cal.getTime());

		// April
		cal.set(Calendar.MONTH, 3);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 30);
		this.dates.add(cal.getTime());

		// May
		cal.set(Calendar.MONTH, 4);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 31);
		this.dates.add(cal.getTime());

		// June
		cal.set(Calendar.MONTH, 5);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 30);
		this.dates.add(cal.getTime());

		// July
		cal.set(Calendar.MONTH, 6);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 31);
		this.dates.add(cal.getTime());

		// August
		cal.set(Calendar.MONTH, 7);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 31);
		this.dates.add(cal.getTime());

		// September
		cal.set(Calendar.MONTH, 8);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 30);
		this.dates.add(cal.getTime());

		// October
		cal.set(Calendar.MONTH, 9);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 31);
		this.dates.add(cal.getTime());

		// November
		cal.set(Calendar.MONTH, 10);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 30);
		this.dates.add(cal.getTime());

		// December
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DATE, 1);
		this.dates.add(cal.getTime());
		cal.set(Calendar.DATE, 31);
		this.dates.add(cal.getTime());
    }
    
    public void dateSelectListener(DateSelectEvent event) {
        this.selectedDate = event.getDate();
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }
    
	public List<Date> getDates() {
		return dates;
	}

	public void setDates(List<Date> dates) {
		this.dates = dates;
	}
}
