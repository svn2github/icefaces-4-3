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
import java.io.Serializable;
import java.lang.Boolean;
import java.util.ArrayList;
import java.util.List;

public class PanelDefaultModel extends PanelModel {
    //Basic Pane options are only application to entire model or
    //are attributes of the borderPanel component
    Boolean applyDefaultStyles;
//    Boolean scrollToBookmarkOnload;
//    Boolean showOverflowOnHover;
    Boolean closable;
    Boolean resizable;
    Boolean slidable;

    public Boolean getApplyDefaultStyles() {
        return applyDefaultStyles;
    }

    public void setApplyDefaultStyles(Boolean applyDefaultStyles) {
        this.applyDefaultStyles = applyDefaultStyles;
    }

    public Boolean getShowOverflowOnHover() {
        return showOverflowOnHover;
    }

    public void setShowOverflowOnHover(Boolean showOverflowOnHover) {
        this.showOverflowOnHover = showOverflowOnHover;
    }

    public Boolean getClosable() {
        return closable;
    }

    public void setClosable(Boolean closable) {
        this.closable = closable;
    }

    public Boolean getResizable() {
        return resizable;
    }

    public void setResizable(Boolean resizable) {
        this.resizable = resizable;
    }

    public Boolean getSlidable() {
        return slidable;
    }

    public void setSlidable(Boolean slidable) {
        this.slidable = slidable;
    }
    public void getDefaultJSONObject(JSONBuilder cfg){
        super.getPanelOptionsJSON("defaults", resizable, closable, slidable, cfg);
    }
 /*   public JSONBuilder getDefaultJSONObject(){
     /*   if (applyDefaultStyles != null){
             cfg.entry("applyDefaultStyles", applyDefaultStyles);
        }
        if (showOverflowOnHover !=null){
            cfg.entry("showOverfloOnHover", showOverflowOnHover);
        } */

   /*     JSONBuilder cfg = super.getPanelOptionsJSON("defaults", getResizable(), getClosable(), getSlidable() );

        return cfg;

    }  */
}
