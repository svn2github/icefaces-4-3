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

package org.icefaces.samples.showcase.example.ace.ajax;

import java.io.Serializable;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name= AjaxBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class AjaxBean implements Serializable 
{
    public static final String BEAN_NAME = "ajaxBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private boolean ajaxEnabled = true;
    private String textValue;
    private String selectValue;
    private int sliderValue;

    public boolean getAjaxEnabled() {
        return ajaxEnabled;
    }

    public void setAjaxEnabled(boolean ajaxEnabled) {
        this.ajaxEnabled = ajaxEnabled;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getSelectValue() {
        return selectValue;
    }

    public void setSelectValue(String selectValue) {
        this.selectValue = selectValue;
    }

    public int getSliderValue() {
        return sliderValue;
    }

    public void setSliderValue(int sliderValue) {
        this.sliderValue = sliderValue;
    }
}
