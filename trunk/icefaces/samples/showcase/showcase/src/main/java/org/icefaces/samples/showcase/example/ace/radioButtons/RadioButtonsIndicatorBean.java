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

package org.icefaces.samples.showcase.example.ace.radioButtons;

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

import java.util.*;

@ComponentExample(
        parent = RadioButtonsBean.BEAN_NAME,
        title = "example.ace.radioButtons.indicator.title",
        description = "example.ace.radioButtons.indicator.description",
        example = "/resources/examples/ace/radioButtons/radioButtonsIndicator.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="radioButtonsIndicator.xhtml",
                    resource = "/resources/examples/ace/radioButtons/radioButtonsIndicator.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="RadioButtonsIndicatorBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/radioButtons/RadioButtonsIndicatorBean.java"),
            @ExampleResource(type = ResourceType.java,
                    title="RadioButtonsBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/radioButtons/RadioButtonsBean.java")
        }
)
@ManagedBean(name= RadioButtonsIndicatorBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class RadioButtonsIndicatorBean extends ComponentExampleImpl<RadioButtonsIndicatorBean> implements Serializable
{
    public static final String BEAN_NAME = "radioButtonsIndicatorBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private boolean required = true;
    private String requiredText = "This field is required.";
    private String optionalText = "Not mandatory.";
    private String position = "right";

    public RadioButtonsIndicatorBean() 
    {
        super(RadioButtonsIndicatorBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

	private List<String> selected = new ArrayList<String>();
	public List<String> getSelected() { return selected; }
	public void setSelected(List<String> selected) { this.selected = selected; }
    
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
