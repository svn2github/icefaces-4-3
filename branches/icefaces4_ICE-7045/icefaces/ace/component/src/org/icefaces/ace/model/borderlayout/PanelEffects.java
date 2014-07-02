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

package org.icefaces.ace.model.borderlayout;

import org.icefaces.ace.util.JSONBuilder;

import javax.faces.component.UIComponent;
import javax.xml.stream.Location;
import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class PanelEffects implements Serializable {


    //Pane animation effects
    FxName fxName;
    FxSpeed fxSpeed;
    Integer effectSpeed;
    String fxSettings;

    public FxName getFxName() {
        return fxName;
    }

    public void setFxName(FxName fxName) {
        this.fxName = fxName;
    }

    /**
     * can use FxSpeed of fast, normal or slow.
     * if this is null, then can use getEffectSpeed
     * instead...first look for FxSpeed, then if not set
     * will look to see if effectSpeed is set.
     * @return
     */
    public FxSpeed getFxSpeed() {
        return fxSpeed;
    }

    public void setFxSpeed(FxSpeed fxSpeed) {
        this.fxSpeed = fxSpeed;
    }

    /**
     * if no FxSpeed is set, then can use effectSpeed
     * integer that is used as speed in ms.
     * @return
     */
    public Integer getEffectSpeed() {
        return effectSpeed;
    }

    public void setEffectSpeed(Integer effectSpeed) {
        this.effectSpeed = effectSpeed;
    }

    /**
     * for now just returns an empty set
     * model this later. direction, duration, easing, etc.
     * @return
     */
    public String getFxSettings() {
        return "{}";
    }

    public void setFxSettings(String fxSettings) {
        this.fxSettings = fxSettings;
    }
}
