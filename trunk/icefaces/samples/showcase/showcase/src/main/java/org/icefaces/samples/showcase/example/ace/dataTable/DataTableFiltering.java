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

	private String customFilter;
	public String getCustomFilter() { return customFilter; }
	public void setCustomFilter(String customFilter) { this.customFilter = customFilter; }

	private boolean custom;
	public boolean isCustom() { return custom; }
	public void setCustom(boolean custom) { this.custom = custom; }

	private boolean bus;
	public boolean isBus() { return bus; }
	public void setBus(boolean bus) { this.bus = bus; }

	private boolean luxury;
	public boolean isLuxury() { return luxury; }
	public void setLuxury(boolean luxury) { this.luxury = luxury; }

	private boolean midSize;
	public boolean isMidSize() { return midSize; }
	public void setMidSize(boolean midSize) { this.midSize = midSize; }

	private boolean motorcycle;
	public boolean isMotorcycle() { return motorcycle; }
	public void setMotorcycle(boolean motorcycle) { this.motorcycle = motorcycle; }

	private boolean pickup;
	public boolean isPickup() { return pickup; }
	public void setPickup(boolean pickup) { this.pickup = pickup; }

	private boolean semiTruck;
	public boolean isSemiTruck() { return semiTruck; }
	public void setSemiTruck(boolean semiTruck) { this.semiTruck = semiTruck; }

	private boolean stationWagon;
	public boolean isStationWagon() { return stationWagon; }
	public void setStationWagon(boolean stationWagon) { this.stationWagon = stationWagon; }

	private boolean subcompact;
	public boolean isSubcompact() { return subcompact; }
	public void setSubcompact(boolean subcompact) { this.subcompact = subcompact; }

	private boolean van;
	public boolean isVan() { return van; }
	public void setVan(boolean van) { this.van = van; }

	private Integer currentRowCount;
	public Integer getCurrentRowCount() { return currentRowCount; }
	public void setCurrentRowCount(Integer currentRowCount) { this.currentRowCount = currentRowCount; }

	public List<String> getFilterValues() {
		List<String> filterValues = new ArrayList<String>();
		if (custom) filterValues.add(customFilter);
		if (bus) filterValues.add("Bus");
		if (luxury) filterValues.add("Luxury");
		if (midSize) filterValues.add("Mid-Size");
		if (motorcycle) filterValues.add("Motorcycle");
		if (pickup) filterValues.add("Pickup");
		if (semiTruck) filterValues.add("Semi-Truck");
		if (stationWagon) filterValues.add("Station Wagon");
		if (subcompact) filterValues.add("Subcompact");
		if (van) filterValues.add("Van");
		return filterValues;
	}
}
