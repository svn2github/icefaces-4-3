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
import javax.faces.event.ActionEvent;

@ManagedBean(name= DateSelectableRangesBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DateSelectableRangesBean implements Serializable {
    public static final String BEAN_NAME = "dateSelectableRangesBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private Date selectedDate;

    private Date firstRangeStart;
    private Date firstRangeEnd;
    private Date secondRangeStart;
    private Date secondRangeEnd;

    public DateSelectableRangesBean() {
		Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, 5);
        firstRangeStart = calendar.getTime();
		selectedDate = calendar.getTime(); // select first date in range
        
        calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, 10);
        firstRangeEnd = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, 15);
        secondRangeStart = calendar.getTime();
        
        calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, 20);
        secondRangeEnd = calendar.getTime();
    }
    
    public Date getSelectedDate() {
        return selectedDate;
    }
    
    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public Date getFirstRangeStart() { return firstRangeStart; }
    public void setFirstRangeStart(Date firstRangeStart) { this.firstRangeStart = firstRangeStart; }

    public Date getFirstRangeEnd() { return firstRangeEnd; }
    public void setFirstRangeEnd(Date firstRangeEnd) { this.firstRangeEnd = firstRangeEnd; }

    public Date getSecondRangeStart() { return secondRangeStart; }
    public void setSecondRangeStart(Date secondRangeStart) { this.secondRangeStart = secondRangeStart; }

    public Date getSecondRangeEnd() { return secondRangeEnd; }
    public void setSecondRangeEnd(Date secondRangeEnd) { this.secondRangeEnd = secondRangeEnd; }
    
	public List<Date> getRanges() {
		List<Date> ranges = new ArrayList<Date>();
		ranges.add(firstRangeStart);
		ranges.add(firstRangeEnd);
		ranges.add(secondRangeStart);
		ranges.add(secondRangeEnd);
		return ranges;
	}
}
