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

package org.icefaces.samples.showcase.example.ace.gMapMarker;

import javax.el.MethodExpression;
import javax.faces.application.*;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.component.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import javax.faces.bean.CustomScoped;
import javax.annotation.PostConstruct;
import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;
import java.io.Serializable;

@ComponentExample(
        title = "example.ace.gMapMarker.title",
        description = "example.ace.gMapMarker.description",
        example = "/resources/examples/ace/gMapMarker/gMapMarker.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="gMapMarker.xhtml",
                    resource = "/resources/examples/ace/gMapMarker/gMapMarker.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="GMapMarkerBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase/example/ace/gMapMarker/GMapMarkerBean.java")
        }
)
@Menu(
    title = "menu.ace.gMapMarker.subMenu.title", 
    menuLinks = {
        @MenuLink(title = "menu.ace.gMapMarker.subMenu.main", isDefault = true, exampleBeanName = GMapMarkerBean.BEAN_NAME)
    }
)
@ManagedBean(name= GMapMarkerBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class GMapMarkerBean extends ComponentExampleImpl<GMapMarkerBean> implements Serializable {
	public static final String BEAN_NAME = "gMapMarkerBean";
	public String getBeanName() { return BEAN_NAME; }
    private Double[] latList = {0.0,7.5,-10.0};
    private Double[] longList = {0.0,7.5,-10.0};
    private String[] optionsList = {"title:'Hover mouse over this marker to see title'","raiseOnDrag:false,draggable:true","draggable:true"};

	public GMapMarkerBean() {
        super(GMapMarkerBean.class);
    }
	
    public Double[] getLatList() {
        return latList;
    }

    public void setLatList(Double[] latList) {
        this.latList = latList;
    }

    public Double[] getLongList() {
        return longList;
    }

    public void setLongList(Double[] longList) {
        this.longList = longList;
    }

    public String[] getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(String[] optionsList) {
        this.optionsList = optionsList;
    }
	@PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }
}
