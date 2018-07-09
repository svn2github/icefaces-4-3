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

package org.icefaces.samples.showcase.example.mobi.dataview;



import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = DataViewBean.BEAN_NAME)
@SessionScoped
public class DataViewBean /*extends ExampleImpl<DataViewBean>*/
        implements Serializable {

    public static final String BEAN_NAME = "dataViewBean";

    public boolean mpgColVis = false;
    public boolean accelColVis = false;
    public boolean costColVis = false;
    public boolean weightColVis = false;
    public boolean textColVis = false;
    public boolean nope = false;
    public String blank = "";

    private List<Car> cars = new ArrayList<Car>() {{
        VehicleGenerator vg = new VehicleGenerator();
        addAll(vg.getRandomCars(100));
    }};

    public DataViewBean() {
        //super(DataViewBean.class);
    }

    public String getBlank() {
        return blank;
    }

    public void setBlank(String blank) {}

    public List<Car> getCars() {
        return cars;
    }


    public boolean isMpgColVis() {
        return mpgColVis;
    }

    public void setMpgColVis(boolean mpgColVis) {
        this.mpgColVis = mpgColVis;
    }

    public boolean isAccelColVis() {
        return accelColVis;
    }

    public void setAccelColVis(boolean accelColVis) {
        this.accelColVis = accelColVis;
    }

    public boolean isCostColVis() {
        return costColVis;
    }

    public void setCostColVis(boolean costColVis) {
        this.costColVis = costColVis;
    }

    public boolean isWeightColVis() {
        return weightColVis;
    }

    public void setWeightColVis(boolean weightColVis) {
        this.weightColVis = weightColVis;
    }

    public boolean isTextColVis() {
        return textColVis;
    }

    public void setTextColVis(boolean textColVis) {
        this.textColVis = textColVis;
    }

    public boolean isNope() {
        return nope;
    }

    public void setNope(boolean nope) {
        this.nope = false;
    }

    public void toggleCostColVis() {
        setCostColVis(!isCostColVis());
    }

    public void toggleWeightColVis() {
        setWeightColVis(!isWeightColVis());
    }

    public void toggleAccelColVis() {
        setAccelColVis(!isAccelColVis());
    }

    public void toggleMpgColVis() {
        setMpgColVis(!isMpgColVis());
    }

    public void toggleTextColVis() {
        setTextColVis(!isTextColVis());
    }
}
