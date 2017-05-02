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

package org.icefaces.samples.showcase.example.ace.listExporter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;
import org.icefaces.samples.showcase.example.ace.dataTable.Car;

@ManagedBean(name= ListExportingBlockBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ListExportingBlockBean implements Serializable {
    public static final String BEAN_NAME = "listExportingBlockBean";
	public String getBeanName() { return BEAN_NAME; }

    public ListExportingBlockBean() {
        this.type = "csv";
    }

    List<Car> carList = new ArrayList(DataTableData.getDefaultData().subList(0,10));
    private boolean selectedItemsOnly = false;
    private String type;

    public List<Car> getCarList() {
        return carList;
    }

    public void setCarList(List<Car> carList) {
        this.carList = new ArrayList(carList);
    }

    public boolean getSelectedItemsOnly() {
        return selectedItemsOnly;
    }
    
    public void setSelectedItemsOnly(boolean selectedItemsOnly) {
        this.selectedItemsOnly = selectedItemsOnly;
    }

    public String getType() {
		return type;
	}

    public void setType(String type) {
		this.type = type;
	}
}
