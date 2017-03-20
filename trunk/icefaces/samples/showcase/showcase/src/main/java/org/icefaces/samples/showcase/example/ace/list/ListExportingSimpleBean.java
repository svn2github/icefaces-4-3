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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;

@ManagedBean(name= ListExportingSimpleBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ListExportingSimpleBean implements Serializable {
    public static final String BEAN_NAME = "listExportingSimpleBean";
	public String getBeanName() { return BEAN_NAME; }
    
    public ListExportingSimpleBean() {
        this.type = "csv";
    }

    private List<SelectItem> stringList = new ArrayList<SelectItem>() {{
        for (String s : DataTableData.CHASSIS_ALL) {
            add(new SelectItem(s));
        }
    }};

    private Set<Object> selections;
    private boolean selectedItemsOnly = false;
    private String type;

    public List<SelectItem> getStringList() {
        return stringList;
    }

    public void setStringList(List<SelectItem> stringList) {
        this.stringList = stringList;
    }
    
    public Set<Object> getSelections() {
        return selections;
    }
    
    public List<Object> getSelectionList() {
        if (selections != null) {
            return new ArrayList<Object>(selections);
        }
        return null;
    }
    
    public void setSelections(Set<Object> selections) {
        this.selections = selections;
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
