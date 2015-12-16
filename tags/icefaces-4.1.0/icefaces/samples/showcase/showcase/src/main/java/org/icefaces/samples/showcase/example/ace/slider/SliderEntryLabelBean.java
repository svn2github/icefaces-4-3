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

package org.icefaces.samples.showcase.example.ace.slider;

import java.io.Serializable;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name= SliderEntryLabelBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class SliderEntryLabelBean implements Serializable
{
    public static final String BEAN_NAME = "sliderEntryLabelBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private String sliderLabelText = "Label";
    private String labelPosition = "left";
    private boolean showLabels = false;
    private int value;
    
    public String getSliderLabelText() {
        return sliderLabelText;
    }
        
    public String getLabelPosition() {
        return labelPosition;
    }
    
    public void setSliderLabelText(String labelText) {
        this.sliderLabelText = labelText;
    }
    
    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

	public boolean isShowLabels() {
		return showLabels;
	}

	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}