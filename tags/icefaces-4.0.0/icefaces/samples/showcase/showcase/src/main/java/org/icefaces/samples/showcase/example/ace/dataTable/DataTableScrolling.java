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

package org.icefaces.samples.showcase.example.ace.dataTable;

import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import java.util.ArrayList;
import java.util.List;
import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import org.icefaces.samples.showcase.example.ace.dataTable.Car;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ComponentExample(
        parent = DataTableBean.BEAN_NAME,
        title = "example.ace.dataTable.scrolling.title",
        description = "example.ace.dataTable.scrolling.description",
        example = "/resources/examples/ace/dataTable/dataTableScrolling.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="dataTableScrolling.xhtml",
                    resource = "/resources/examples/ace/dataTable/dataTableScrolling.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="DataTableScrolling.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/showcase"+
                    "/example/ace/dataTable/DataTableScrolling.java")
        }
)
@ManagedBean(name= DataTableScrolling.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DataTableScrolling extends ComponentExampleImpl<DataTableScrolling> implements Serializable {
    public static final String BEAN_NAME = "dataTableScrolling";
	public String getBeanName() { return BEAN_NAME; }
    
    private boolean scrolling = true;
    private int height = 200;
    private List<Car> carsData;
    /////////////---- CONSTRUCTOR BEGIN
    public DataTableScrolling() {
        super(DataTableScrolling.class);
        carsData = new ArrayList<Car>(DataTableData.getDefaultData());
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

    /////////////---- GETTERS & SETTERS BEGIN
    public List<Car> getCarsData() { return carsData; }
    public void setCarsData(List<Car> carsData) { this.carsData = carsData; }
    public boolean getScrolling() { return scrolling; }
    public int getHeight() { return height; }
    public void setScrolling(boolean scrolling) { this.scrolling = scrolling; }
    public void setHeight(int height) { this.height = height; }
}
