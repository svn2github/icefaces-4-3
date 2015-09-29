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

package org.icefaces.samples.showcase.example.ace.textEntry;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name= TextEntryAutotabBean.BEAN_NAME)
@ViewScoped
public class TextEntryAutotabBean implements Serializable
{
    public static final String BEAN_NAME = "textEntryAutotabBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private int firstNameLength = 5;
    private int lastNameLength = 15;
    private int cityLength = 10;
    private int provinceLength = 2;
    
    private String firstName, lastName, city, province;
    
    public int getFirstNameLength() {
        return firstNameLength;
    }
    
    public int getLastNameLength() {
        return lastNameLength;
    }
    
    public int getCityLength() {
        return cityLength;
    }
    
    public int getProvinceLength() {
        return provinceLength;
    }
    
    public void setFirstNameLength(int firstNameLength) {
        this.firstNameLength = firstNameLength;
    }
    
    public void setLastNameLength(int lastNameLength) {
        this.lastNameLength = lastNameLength;
    }
    
    public void setCityLength(int cityLength) {
        this.cityLength = cityLength;
    }
    
    public void setProvinceLength(int provinceLength) {
        this.provinceLength = provinceLength;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
