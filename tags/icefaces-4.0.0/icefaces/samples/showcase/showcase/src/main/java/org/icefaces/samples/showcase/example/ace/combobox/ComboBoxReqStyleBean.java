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

package org.icefaces.samples.showcase.example.ace.combobox;

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ComponentExample(
        parent = ComboBoxBean.BEAN_NAME,
        title = "example.ace.combobox.reqStyle.title",
        description = "example.ace.combobox.reqStyle.description",
        example = "/resources/examples/ace/combobox/comboBoxReqStyle.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="comboBoxReqStyle.xhtml",
                    resource = "/resources/examples/ace/combobox/comboBoxReqStyle.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="ComboBoxReqStyleBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/combobox/ComboBoxReqStyleBean.java"),
            @ExampleResource(type = ResourceType.java,
                    title="ComboBoxBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/combobox/ComboBoxBean.java")
        }
)
@ManagedBean(name= ComboBoxReqStyleBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ComboBoxReqStyleBean extends ComponentExampleImpl<ComboBoxReqStyleBean> implements Serializable
{
    public static final String BEAN_NAME = "comboBoxReqStyleBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private String selectedText1, selectedText2;
    private String reqColor = "redRS";
    private String optColor = "greenRS";

    public ComboBoxReqStyleBean() 
    {
        super(ComboBoxReqStyleBean.class);
    }
    
    public String getSelectedText1() { return selectedText1; }
    public String getSelectedText2() { return selectedText2; }
    
    public String getReqColor() {
        return reqColor;
    }
    
    public String getOptColor() {
        return optColor;
    }
    
    public void setSelectedText1(String selectedText1) { this.selectedText1 = selectedText1; }
    public void setSelectedText2(String selectedText2) { this.selectedText2 = selectedText2; }
    
    public void setReqColor(String reqColor) {
        this.reqColor = reqColor;
    }
    
    public void setOptColor(String optColor) {
        this.optColor = optColor;
    }
    
    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }
	
	private boolean useTheme = false;

    public boolean getUseTheme() {
        return useTheme;
    }

    public void setUseTheme(boolean useTheme) {
        this.useTheme = useTheme;
    }
}
