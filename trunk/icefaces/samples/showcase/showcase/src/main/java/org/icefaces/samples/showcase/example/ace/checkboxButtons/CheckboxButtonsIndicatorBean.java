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

package org.icefaces.samples.showcase.example.ace.checkboxButtons;

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

import java.util.*;

@ComponentExample(
        parent = CheckboxButtonsBean.BEAN_NAME,
        title = "example.ace.checkboxButtons.indicator.title",
        description = "example.ace.checkboxButtons.indicator.description",
        example = "/resources/examples/ace/checkboxButtons/checkboxButtonsIndicator.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="checkboxButtonsIndicator.xhtml",
                    resource = "/resources/examples/ace/checkboxButtons/checkboxButtonsIndicator.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="CheckboxButtonsIndicatorBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/checkboxButtons/CheckboxButtonsIndicatorBean.java")
        }
)
@ManagedBean(name= CheckboxButtonsIndicatorBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class CheckboxButtonsIndicatorBean extends ComponentExampleImpl<CheckboxButtonsIndicatorBean> implements Serializable
{
    public static final String BEAN_NAME = "checkboxButtonsIndicatorBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private boolean required = true;
    private String requiredText = "This field is required.";
    private String optionalText = "Not mandatory.";
    private String position = "right";

    public CheckboxButtonsIndicatorBean() 
    {
        super(CheckboxButtonsIndicatorBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

	private List<String> selected = new ArrayList<String>();
	public List<String> getSelected() { return selected; }
	public void setSelected(List<String> selected) { this.selected = selected; }

	public String getSelectedString() {
		String result = "";
		if (selected != null) {
			result += selected.toString();
			result = result.replace("[", "");
			result = result.replace("]", "");
			if ("".equals(result)) {
				result = "None";
			}
		} else {
			result = "None";
		}
		return result;
	}

    public boolean getRequired() {
        return required;
    }
    
    public String getRequiredText() {
        return requiredText;
    }
    
    public String getOptionalText() {
        return optionalText;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setRequired(boolean required) {
        this.required = required;
    }
    
    public void setRequiredText(String requiredText) {
        this.requiredText = requiredText;
    }
    
    public void setOptionalText(String optionalText) {
        this.optionalText = optionalText;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
}
