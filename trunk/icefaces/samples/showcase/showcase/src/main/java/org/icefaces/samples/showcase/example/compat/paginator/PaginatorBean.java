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

package org.icefaces.samples.showcase.example.compat.paginator;

import java.io.Serializable;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import org.icefaces.samples.showcase.example.compat.dataTable.Car;
import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.Menu;
import org.icefaces.samples.showcase.metadata.annotation.MenuLink;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

@ComponentExample(
        title = "example.compat.paginator.title",
        description = "example.compat.paginator.description",
        example = "/resources/examples/compat/paginator/paginator.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="paginator.xhtml",
                    resource = "/resources/examples/compat/"+
                               "paginator/paginator.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="PaginatorBean.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/compat/paginator/PaginatorBean.java")
        }
)
@Menu(
	title = "menu.compat.paginator.subMenu.title",
	menuLinks = {
            @MenuLink(title = "menu.compat.paginator.subMenu.main",
                    isDefault = true,
                    exampleBeanName = PaginatorBean.BEAN_NAME),
            @MenuLink(title = "menu.compat.paginator.subMenu.info",
                    exampleBeanName = PaginatorInfo.BEAN_NAME),
            @MenuLink(title = "menu.compat.paginator.subMenu.events",
                    exampleBeanName = PaginatorEvents.BEAN_NAME),
            @MenuLink(title = "menu.compat.paginator.subMenu.max",
                    exampleBeanName = PaginatorMax.BEAN_NAME),
            @MenuLink(title = "menu.compat.paginator.subMenu.fast",
                    exampleBeanName = PaginatorFast.BEAN_NAME),
            @MenuLink(title = "menu.compat.paginator.subMenu.display",
                    exampleBeanName = PaginatorDisplay.BEAN_NAME),
            @MenuLink(title = "menu.compat.paginator.subMenu.vertical",
                    exampleBeanName = PaginatorVertical.BEAN_NAME)
})
@ManagedBean(name= PaginatorBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class PaginatorBean extends ComponentExampleImpl<PaginatorBean> implements Serializable {
	
    public static final String BEAN_NAME = "paginator";
    private int rows;
    private ArrayList<Car> cars;


    public PaginatorBean() {
        super(PaginatorBean.class);
        rows = DataTableData.DEFAULT_ROWS;
        cars = new ArrayList<Car>(DataTableData.getDefaultData());
    }
	
    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

    public int getRows() { return rows; }
    public ArrayList<Car> getCars() {return cars;}
    
    public void setRows(int rows) { this.rows = rows; }
}
