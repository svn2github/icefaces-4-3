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

package org.icefaces.mobi.component.flipswitch;

import org.icefaces.ace.component.clientValidator.Validateable;
import org.icefaces.ace.util.Attribute;
import org.icefaces.impl.util.Util;


import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;


public class FlipSwitch extends FlipSwitchBase {
    //src is NOT part of the pass through attributes
    public static final String FLIPSWITCH_ON_CLASS = "mobi-flipswitch mobi-flipswitch-on ui-widget";
    public static final String FLIPSWITCH_OFF_CLASS = "mobi-flipswitch mobi-flipswitch-off ui-widget";
    private String[] attributesNames = {
            "tabindex", "style"
    };

    private String[] booleanAttNames = {
            "required", "immediate"
    };
 
    public String[] getAttributesNames() {
        return attributesNames;
    }

	public String[] getBooleanAttNames() {
		return booleanAttNames;
	}
}
