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

import org.icefaces.ace.component.borderlayout.BorderPanel;
import org.icefaces.ace.util.JSONBuilder;

import javax.faces.component.UIComponent;
import javax.xml.stream.Location;
import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.List;

public class PanelModel implements Serializable {

    //Basic Pane options
    Boolean applyDefaultstyles;
    Boolean showOverflowOnHover;

    //Pane animation effects
    FxName fxName;
    FxSpeed fxSpeed;
    //FxSetting fxSettings;

    boolean initClosed;
    boolean initHidden;

    Integer spacing_open;
    Integer spacing_closed;
    String togglerAlign_closed = "top"; //align to top of resizer...default to "top"?
    Integer togglerLength_closed;

    //Pane size and spacing
    Integer initialSize;
    Integer minimumSize;
    Integer maximumSize;

    public JSONBuilder getPanelOptionsJSON(String locationString,
                 Boolean resizable, Boolean closable, Boolean slidable) {
        JSONBuilder cfg = JSONBuilder.create();
        cfg.beginMap(locationString);
        if (resizable != null){
            cfg.entry("resizable", resizable);
        }
        if (closable !=null){
            cfg.entry("closable", closable);
        }
        if (slidable !=null){
            cfg.entry("slidable", slidable);
        }
        if (initialSize !=null) {
            cfg.entry("size", initialSize);
        }
        if (minimumSize!=null){
            cfg.entry("minSize", minimumSize);
        }
        if (maximumSize !=null){
            cfg.entry("maxSize", maximumSize);
        }
        if (locationString.toLowerCase().equals("center")){
            cfg.endMap();
            return cfg;
        }
        String effName = null;
        if (getFxName() !=null){
            effName = getFxName().toString();
        }
        String effSpeed =null;
        if (getFxSpeed() !=null){
            effSpeed = getFxSpeed().toString();
        }
        //start to encode this stuff!
        if (effName!=null){
            cfg.entry("fxName", effName);
        }
        if (effSpeed !=null){
            cfg.entry("fxSpeed", effSpeed);
        }

        if (spacing_open !=null){
            cfg.entry("spacing_open", spacing_open);
        }
        if (spacing_closed !=null){
            cfg.entry("spacingClosed", spacing_closed);
        }
        if (togglerAlign_closed !=null){
            cfg.entry("togglerLength_closed", togglerAlign_closed);
        }
        if (togglerLength_closed !=null){
            cfg.entry("togglerLength_closed", togglerLength_closed);
        }
        cfg.endMap();
//    System.out.println(" for panel = "+locationString+" cfg object="+cfg.toString());
        return cfg;
    }

    public void getPanelOptionsJSON(String locationString,
                   Boolean resizable, Boolean closable, Boolean slidable,
                   JSONBuilder cfg) {
          cfg.beginMap(locationString);
          if (resizable != null){
              cfg.entry("resizable", resizable);
          }
          if (closable !=null){
              cfg.entry("closable", closable);
          }
          if (slidable !=null){
              cfg.entry("slidable", slidable);
          }
          if (initialSize !=null) {
              cfg.entry("size", initialSize);
          }
          if (minimumSize!=null){
              cfg.entry("minSize", minimumSize);
          }
          if (maximumSize !=null){
              cfg.entry("maxSize", maximumSize);
          }
          if (locationString.toLowerCase().equals("center")){
              cfg.endMap();
              return;
          }
          String effName = null;
          if (getFxName() !=null){
              effName = getFxName().toString();
          }
          String effSpeed =null;
          if (getFxSpeed() !=null){
              effSpeed = getFxSpeed().toString();
          }
          //start to encode this stuff!
          if (effName!=null){
              cfg.entry("fxName", effName);
          }
          if (effSpeed !=null){
              cfg.entry("fxSpeed", effSpeed);
          }

          if (spacing_open !=null){
              cfg.entry("spacing_open", spacing_open);
          }
          if (spacing_closed !=null){
              cfg.entry("spacingClosed", spacing_closed);
          }
          cfg.endMap();
//      System.out.println(" for panel = "+locationString+" cfg object="+cfg.toString());
      }

    public Boolean getApplyDefaultstyles() {
        return applyDefaultstyles;
    }

    public void setApplyDefaultstyles(Boolean applyDefaultstyles) {
        this.applyDefaultstyles = applyDefaultstyles;
    }

    public Boolean getShowOverflowOnHover() {
        return showOverflowOnHover;
    }

    public void setShowOverflowOnHover(Boolean showOverflowOnHover) {
        this.showOverflowOnHover = showOverflowOnHover;
    }

    public FxName getFxName() {
        return fxName;
    }

    public void setFxName(FxName fxName) {
        this.fxName = fxName;
    }

    public FxSpeed getFxSpeed() {
        return fxSpeed;
    }

    public void setFxSpeed(FxSpeed fxSpeed) {
        this.fxSpeed = fxSpeed;
    }

    public Integer getSpacing_closed() {
        return spacing_closed;
    }

    public void setSpacing_closed(Integer spacing_closed) {
        this.spacing_closed = spacing_closed;
    }

    public Integer getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(Integer initialSize) {
        this.initialSize = initialSize;
    }

    public Integer getMinimumSize() {
        return minimumSize;
    }

    public void setMinimumSize(Integer minimumSize) {
        this.minimumSize = minimumSize;
    }

    public Integer getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Integer maximumSize) {
        this.maximumSize = maximumSize;
    }

    public boolean isInitClosed() {
        return initClosed;
    }

    public void setInitClosed(boolean initClosed) {
        this.initClosed = initClosed;
    }

    public boolean isInitHidden() {
        return initHidden;
    }

    public void setInitHidden(boolean initHidden) {
        this.initHidden = initHidden;
    }

    public Integer getSpacing_open() {
        return spacing_open;
    }

    public void setSpacing_open(Integer spacing_open) {
        this.spacing_open = spacing_open;
    }

    public String getTogglerAlign_closed() {
        return togglerAlign_closed;
    }

    public void setTogglerAlign_closed(String togglerAlign_closed) {
        this.togglerAlign_closed = togglerAlign_closed;
    }

    public Integer getTogglerLength_closed() {
        return togglerLength_closed;
    }

    public void setTogglerLength_closed(Integer togglerLength_closed) {
        this.togglerLength_closed = togglerLength_closed;
    }
}
