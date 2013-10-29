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

package org.icefaces.samples.showcase.example.ace.list;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import org.icefaces.samples.showcase.example.compat.dataTable.Car;
import org.icefaces.samples.showcase.metadata.annotation.*;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ComponentExample(
    parent = ListBean.BEAN_NAME,
    title = "example.ace.list.dual.title",
    description = "example.ace.list.dual.description",
    example = "/resources/examples/ace/list/listDual.xhtml"
)
@ExampleResources(
    resources ={
        // xhtml
        @ExampleResource(type = ResourceType.xhtml,
                title="ListDual.xhtml",
                resource = "/resources/examples/ace/"+
                        "list/listDual.xhtml"),
        // Java Source
        @ExampleResource(type = ResourceType.java,
                title="ListDualBean.java",
                resource = "/WEB-INF/classes/org/icefaces/samples/"+
                        "showcase/example/ace/list/ListDualBean.java")
    }
)
@ManagedBean(name= ListDualBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ListDualBean extends ComponentExampleImpl<ListDualBean> implements Serializable {
    public static final String BEAN_NAME = "listDualBean";
    
    private static final String[] AVAILABLE_POSITIONS = new String[] {
        "ALL", "BOTH", "TOP", "BOTTOM", "MIDDLE"
    };
    private String controlPosition = AVAILABLE_POSITIONS[0];
    private List<SelectItem> stringList = new ArrayList<SelectItem>() {{
        for (String s : DataTableData.CHASSIS_ALL) {
            add(new SelectItem(s));
        }
        remove(DataTableData.CHASSIS_ALL.length-1);
    }};
    private List<SelectItem> destStringList = new ArrayList<SelectItem>() {{
        add(new SelectItem(DataTableData.CHASSIS_ALL[DataTableData.CHASSIS_ALL.length-1]));
    }};
    
    public ListDualBean() {
        super(ListDualBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }
    
    public String[] getAvailablePositions() {
        return AVAILABLE_POSITIONS;
    }
    
    public String getControlPosition() {
        return controlPosition;
    }
    
    public void setControlPosition(String controlPosition) {
        this.controlPosition = controlPosition;
    }

    public List<SelectItem> getStringList() {
        return stringList;
    }

    public void setStringList(List<SelectItem> stringList) {
        this.stringList = stringList;
    }

    public List<SelectItem> getDestStringList() {
        return destStringList;
    }

    public void setDestStringList(List<SelectItem> destStringList) {
        this.destStringList = destStringList;
    }
}
