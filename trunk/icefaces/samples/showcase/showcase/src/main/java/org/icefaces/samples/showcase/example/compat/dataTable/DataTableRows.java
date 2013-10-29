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

package org.icefaces.samples.showcase.example.compat.dataTable;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import java.io.Serializable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

@ComponentExample(
        parent = DataTableBean.BEAN_NAME,
        title = "example.compat.dataTable.rows.title",
        description = "example.compat.dataTable.rows.description",
        example = "/resources/examples/compat/dataTable/dataTableRows.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="dataTableRows.xhtml",
                    resource = "/resources/examples/compat/"+
                               "dataTable/dataTableRows.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="DataTableRows.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/compat/dataTable/DataTableRows.java")
        }
)
@ManagedBean(name= DataTableRows.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DataTableRows extends ComponentExampleImpl<DataTableRows> implements Serializable {
	
    public static final String BEAN_NAME = "dataTableRows";
    private int rows = DataTableData.DEFAULT_ROWS;
    private ArrayList<Car> cars;

    public DataTableRows() {
        super(DataTableRows.class);
        rows = DataTableData.DEFAULT_ROWS;
        cars = new ArrayList<Car>(DataTableData.getDefaultData());
    }

    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public ArrayList<Car> getCars() {return cars;}
}
