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

package org.icefaces.samples.showcase.example.ace.dragDrop;


import java.io.Serializable;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import org.icefaces.samples.showcase.dataGenerators.ImageSet;

@ManagedBean(name= DraggableOverviewBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DraggableOverviewBean implements Serializable
{
    public static final String BEAN_NAME = "draggableOverviewBean";
	public String getBeanName() { return BEAN_NAME; }
    private ImageSet.ImageInfo image;
    
    private String axisMovementConstraint;
    private String helperMode;
    private String gridMode;
    private String containmentConstraint;
    private boolean revert;
    private double opacity;
    
    public DraggableOverviewBean()
    {
        image = ImageSet.getImage(ImageSet.ImageSelect.PICTURE);
        axisMovementConstraint = "x or y";
        helperMode = "original";
        containmentConstraint = "#div1";
        gridMode ="1,1";
        revert = false;
        opacity = 1d;
    }

    public ImageSet.ImageInfo getImage() {
        return image;
    }

    public String getAxisMovementConstraint() {
        return axisMovementConstraint;
    }

    public void setAxisMovementConstraint(String axisMovementConstraint) {
        this.axisMovementConstraint = axisMovementConstraint;
    }

    public String getGridMode() {
        return gridMode;
    }

    public void setGridMode(String gridMode) {
        this.gridMode = gridMode;
    }

    public String getHelperMode() {
        return helperMode;
    }

    public void setHelperMode(String helperMode) {
        this.helperMode = helperMode;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public boolean isRevert() {
        return revert;
    }

    public void setRevert(boolean revert) {
        this.revert = revert;
    }

    public String getContainmentConstraint() {
        return containmentConstraint;
    }

    public void setContainmentConstraint(String containmentConstraint) {
        this.containmentConstraint = containmentConstraint;
    }
    
}
