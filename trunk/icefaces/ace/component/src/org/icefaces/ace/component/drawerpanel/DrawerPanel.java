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

package org.icefaces.ace.component.drawerpanel;

import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.icefaces.ace.event.CloseEvent;
import org.icefaces.ace.util.Constants;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

public class DrawerPanel extends DrawerPanelBase {

    static final String SHOW_CLASS = "ice-ace-drawer" ;
    static final String HIDE_CLASS = "ice-ace-drawer-hidden "; 

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName != null && eventName.equals("close") && event instanceof AjaxBehaviorEvent) {
            setVisible(false);
            CloseEvent closeEvent = new CloseEvent(this, ((AjaxBehaviorEvent) event).getBehavior());
            closeEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            super.queueEvent(closeEvent);
        } else {
            super.queueEvent(event);
        }
    }
}