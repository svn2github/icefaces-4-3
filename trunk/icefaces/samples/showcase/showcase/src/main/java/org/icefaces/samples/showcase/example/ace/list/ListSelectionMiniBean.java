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

package org.icefaces.samples.showcase.example.ace.list;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import org.icefaces.samples.showcase.example.ace.dataTable.Car;
import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import java.util.*;

@ComponentExample(
    parent = ListBean.BEAN_NAME,
    title = "example.ace.list.selectionMini.title",
    description = "example.ace.list.selectionMini.description",
    example = "/resources/examples/ace/list/listSelectionMini.xhtml"
)
@ExampleResources(
    resources ={
        // xhtml
        @ExampleResource(type = ResourceType.xhtml,
                title="ListSelectionMini.xhtml",
                resource = "/resources/examples/ace/"+
                        "list/listSelectionMini.xhtml"),
        // Java Source
        @ExampleResource(type = ResourceType.java,
                title="ListSelectionMiniBean.java",
                resource = "/WEB-INF/classes/org/icefaces/samples/"+
                        "showcase/example/ace/list/ListSelectionMiniBean.java")
    }
)
@ManagedBean(name= ListSelectionMiniBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ListSelectionMiniBean extends ComponentExampleImpl<ListSelectionMiniBean> {
    public static final String BEAN_NAME = "listSelectionMiniBean";
	public String getBeanName() { return BEAN_NAME; }

    public ListSelectionMiniBean() {
        super(ListSelectionMiniBean.class);
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }



    private List<SelectItem> selections = new ArrayList<SelectItem>();
    private Car selectItemObject = DataTableData.getDefaultData().subList(11,12).get(0);
    private Map<String, Car> selectItemMap = new HashMap<String, Car>() {{
        for (Car c : DataTableData.getDefaultData().subList(0,10))
            put(c.getName(), c);
    }};

    public Map<String, Car> getSelectItemMap() {
        return selectItemMap;
    }

    public void setSelectItemMap(Map<String, Car> selectItemMap) {
        this.selectItemMap = selectItemMap;
    }

    public List<SelectItem> getSelections() {
        return selections;
    }

    public void setSelections(List<SelectItem> selections) {
        this.selections = selections;
    }

    public Car getSelectItemObject() {
        return selectItemObject;
    }

    public void setSelectItemObject(Car selectItemObject) {
        this.selectItemObject = selectItemObject;
    }
}
