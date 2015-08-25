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

package org.icefaces.samples.showcase.example.core.onlineStatus;


import javax.annotation.PostConstruct;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.lang.String;


@ManagedBean(name = OnlineStatusBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class OnlineStatusBean  implements Serializable {
    public static final String BEAN_NAME = "onlineStatusBean";
	public String getBeanName() { return BEAN_NAME; }

    private String firstName;
    private String lastName;
    private String submittedValues="";

    public OnlineStatusBean() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void submitValues(ActionEvent ae){
        this.submittedValues=" You have entered firstName="+firstName+" lastName="+lastName;
    }

    public String getSubmittedValues() {
        return submittedValues;
    }

    public void setSubmittedValues(String submittedValues) {
        this.submittedValues = submittedValues;
    }
}
