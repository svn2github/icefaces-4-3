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
        title = "example.ace.radioButtons.reqStyle.title",
        description = "example.ace.radioButtons.reqStyle.description",
        example = "/resources/examples/ace/radioButtons/radioButtonsReqStyle.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="radioButtonsReqStyle.xhtml",
                    resource = "/resources/examples/ace/radioButtons/radioButtonsReqStyle.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="RadioButtonsReqStyleBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/radioButtons/RadioButtonsReqStyleBean.java"),
            @ExampleResource(type = ResourceType.java,
                    title="RadioButtonsBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/radioButtons/RadioButtonsBean.java")
        }
)
@ManagedBean(name= RadioButtonsReqStyleBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class RadioButtonsReqStyleBean extends ComponentExampleImpl<RadioButtonsReqStyleBean> implements Serializable
{
    public static final String BEAN_NAME = "radioButtonsReqStyleBean";
	public String getBeanName() { return BEAN_NAME; }
    
    public RadioButtonsReqStyleBean() 
    {
        super(RadioButtonsReqStyleBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

	private List<String> selected1 = new ArrayList<String>();
	public List<String> getSelected1() { return selected1; }
	public void setSelected1(List<String> selected1) { this.selected1 = selected1; }

	private List<String> selected2 = new ArrayList<String>();
	public List<String> getSelected2() { return selected2; }
	public void setSelected2(List<String> selected2) { this.selected2 = selected2; }

    private String reqColor = "redRS";
    private String optColor = "greenRS";
   
    public String getReqColor() {
        return reqColor;
    }
    
    public String getOptColor() {
        return optColor;
    }
        
    public void setReqColor(String reqColor) {
        this.reqColor = reqColor;
    }
    
    public void setOptColor(String optColor) {
        this.optColor = optColor;
    }

	private boolean useTheme = false;

    public boolean getUseTheme() {
        return useTheme;
    }

    public void setUseTheme(boolean useTheme) {
        this.useTheme = useTheme;
    }

	public void clearValues() {
		selected1 = new ArrayList<String>();
		selected2 = new ArrayList<String>();
	}
}
