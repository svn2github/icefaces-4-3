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

package org.icefaces.samples.showcase.example.ace.listControl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;

import org.icefaces.ace.event.ListMigrateEvent;

@ManagedBean(name= ListDualBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ListDualBean implements Serializable {
    public static final String BEAN_NAME = "listDualBean";
	public String getBeanName() { return BEAN_NAME; }
    
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

	private List<ListMigrateEvent.MigrationRecord> records = null;
	public  List<ListMigrateEvent.MigrationRecord> getRecords() { return records; }
	

	public void migrateListener(ListMigrateEvent event) {
		records = event.getMigrationRecords();
	}
}
