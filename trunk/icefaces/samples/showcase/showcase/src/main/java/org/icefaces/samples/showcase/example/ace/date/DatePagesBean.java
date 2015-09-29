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
import java.util.Date;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import org.icefaces.ace.event.DateSelectEvent;

@ManagedBean(name= DatePagesBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DatePagesBean implements Serializable {
    public static final String BEAN_NAME = "datePages";
	public String getBeanName() { return BEAN_NAME; }

    private Date selectedDate;
    private int pages;
    
    public DatePagesBean() {
        this.selectedDate = new Date(System.currentTimeMillis());
        this.pages = 3;
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
    
    public int getPages() {
        return pages;
    }
    
    public void setPages(int pages) {
        this.pages = pages;
    }
}
