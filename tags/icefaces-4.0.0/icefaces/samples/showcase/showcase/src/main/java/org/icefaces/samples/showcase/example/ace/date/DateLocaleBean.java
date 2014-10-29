/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
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

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.util.Date;

@ComponentExample(
        parent = DateEntryBean.BEAN_NAME,
        title = "example.ace.dateentry.locale.title",
        description = "example.ace.dateentry.locale.description",
        example = "/resources/examples/ace/date/datelocale.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="datelocale.xhtml",
                    resource = "/resources/examples/ace/date/datelocale.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="DateLocaleBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase/example/ace/date/DateLocaleBean.java")
        }
)
@ManagedBean(name= DateLocaleBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DateLocaleBean extends ComponentExampleImpl<DateLocaleBean> implements Serializable {
    public static final String BEAN_NAME = "dateLocale";
	public String getBeanName() { return BEAN_NAME; }

    private Date selectedDate = new Date(System.currentTimeMillis());
    private String locale;

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public DateLocaleBean() {
        super(DateLocaleBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        if (locale != null && locale.equals("default")) {
            locale = null;
        }
        this.locale = locale;
    }
}
