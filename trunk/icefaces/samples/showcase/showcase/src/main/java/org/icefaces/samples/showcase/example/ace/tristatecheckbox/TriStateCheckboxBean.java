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

package org.icefaces.samples.showcase.example.ace.borderlayout;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AjaxBehaviorEvent;

import java.io.Serializable;

@ManagedBean(name= TriStateCheckboxBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class TriStateCheckboxBean implements Serializable {
    public static final String BEAN_NAME = "triStateCheckboxBean";
	public String getBeanName() { return BEAN_NAME; }

	public TriStateCheckboxBean() {
		
	}

	private Object value = "unchecked";
	public Object getValue() { return value; }
	public void setValue(Object value) { this.value = value; }

	private boolean indeterminateBeforeChecked = false;
	public boolean getIndeterminateBeforeChecked() { return indeterminateBeforeChecked; }
	public void setIndeterminateBeforeChecked(boolean indeterminateBeforeChecked) { this.indeterminateBeforeChecked = indeterminateBeforeChecked; }
}