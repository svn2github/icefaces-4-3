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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;

@ManagedBean(name= DataTableFiltering.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DataTableFiltering implements Serializable {
    public static final String BEAN_NAME = "dataTableFiltering";
	public String getBeanName() { return BEAN_NAME; }
    private List<Car> carsData;
    private List<SelectItem> accelOptions = new ArrayList<SelectItem>() {{
        add(new SelectItem(""));
        add(new SelectItem(5));
        add(new SelectItem(10));
        add(new SelectItem(15));
    }};

    /////////////---- CONSTRUCTOR BEGIN
    public DataTableFiltering() {
        carsData = new ArrayList<Car>(DataTableData.getDefaultData());
    }

    /////////////---- GETTERS & SETTERS BEGIN
    public List<Car> getCarsData() { return carsData; }
    public void setCarsData(List<Car> carsData) { this.carsData = carsData; }

    public List<SelectItem> getAccelOptions() {
        return accelOptions;
    }

    public void setAccelOptions(List<SelectItem> accelOptions) {
        this.accelOptions = accelOptions;
    }
}
