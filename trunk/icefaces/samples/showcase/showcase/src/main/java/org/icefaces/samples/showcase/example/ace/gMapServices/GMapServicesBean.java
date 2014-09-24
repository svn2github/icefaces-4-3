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

package org.icefaces.samples.showcase.example.ace.gMapServices;

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
        title = "example.ace.gMapServices.title",
        description = "example.ace.gMapServices.description",
        example = "/resources/examples/ace/gMapServices/gMapServices.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="gMapServices.xhtml",
                    resource = "/resources/examples/ace/gMapServices/gMapServices.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="GMapServicesBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase/example/ace/gMapServices/GMapServicesBean.java")
        }
)
@Menu(
    title = "menu.ace.gMapServices.subMenu.title", 
    menuLinks = {
        @MenuLink(title = "menu.ace.gMapServices.subMenu.main", isDefault = true, exampleBeanName = GMapServicesBean.BEAN_NAME)
    }
)
@ManagedBean(name= GMapServicesBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class GMapServicesBean extends ComponentExampleImpl<GMapServicesBean> implements Serializable{
	public static final String BEAN_NAME = "gMapServicesBean";
	public String getBeanName() { return BEAN_NAME; }

    private String points="Regent's Park, London: Kingston upon Thames";
    private String options="optimizeWaypoints:true";
    private String travelMode="DRIVING";

	public GMapServicesBean() {
        super(GMapServicesBean.class);
    }
	
    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }
	@PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }
}
