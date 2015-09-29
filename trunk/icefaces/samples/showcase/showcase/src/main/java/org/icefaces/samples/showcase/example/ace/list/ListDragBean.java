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

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import org.icefaces.samples.showcase.dataGenerators.utilityClasses.DataTableData;

@ManagedBean(name= ListDragBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ListDragBean implements Serializable {
    public static final String BEAN_NAME = "listDragBean";
	public String getBeanName() { return BEAN_NAME; }

    List<SelectItem> stringList = new ArrayList<SelectItem>() {{
        for (String s : DataTableData.CHASSIS_ALL) {
            add(new SelectItem(s));
        }
    }};

    List<SelectItem> twoWaySourceList = new ArrayList<SelectItem>() {{
        for (String s : DataTableData.CHASSIS_ALL) {
            add(new SelectItem(s));
        }
    }};

    List<SelectItem> twoWayDestList = new ArrayList<SelectItem>();

    List<SelectItem> oneWaySourceList = new ArrayList<SelectItem>() {{
        for (String s : DataTableData.CHASSIS_ALL) {
            add(new SelectItem(s));
        }
    }};

    List<SelectItem> oneWayDestList = new ArrayList<SelectItem>();

    public List<SelectItem> getStringList() {
        return stringList;
    }

    public void setStringList(List<SelectItem> stringList) {
        this.stringList = stringList;
    }

    public List<SelectItem> getTwoWaySourceList() {
        return twoWaySourceList;
    }

    public void setTwoWaySourceList(List<SelectItem> twoWaySourceList) {
        this.twoWaySourceList = twoWaySourceList;
    }

    public List<SelectItem> getTwoWayDestList() {
        return twoWayDestList;
    }

    public void setTwoWayDestList(List<SelectItem> twoWayDestList) {
        this.twoWayDestList = twoWayDestList;
    }

    public List<SelectItem> getOneWaySourceList() {
        return oneWaySourceList;
    }

    public void setOneWaySourceList(List<SelectItem> oneWaySourceList) {
        this.oneWaySourceList = oneWaySourceList;
    }

    public List<SelectItem> getOneWayDestList() {
        return oneWayDestList;
    }

    public void setOneWayDestList(List<SelectItem> oneWayDestList) {
        this.oneWayDestList = oneWayDestList;
    }
}
