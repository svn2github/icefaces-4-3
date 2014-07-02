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

package org.icefaces.ace.component.borderlayout;

import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.annotation.Field;
import org.icefaces.ace.meta.baseMeta.UIPanelMeta;
//import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
//import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;
import org.icefaces.resources.ICEResourceLibrary;
import org.icefaces.ace.model.borderlayout.PanelModel;

@Component(
        tagName         = "borderPanel",
        componentClass  = "org.icefaces.ace.component.borderlayout.BorderPanel",
        rendererClass   = "org.icefaces.ace.component.borderlayout.BorderPanelRenderer",
        generatedClass  = "org.icefaces.ace.component.borderlayout.BorderPanelBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.BorderPanel",
        rendererType    = "org.icefaces.ace.component.BorderPanelRenderer",
		componentFamily = "org.icefaces.ace.BorderPanel",
		tlddoc = "BorderPanel is a child of the borderLayout component and can have location of north, south, east west or " +
                "center.  Note that not all properties apply to the center panel as it is not an actual border panel. The four " +
                "actual border panels may be resizable, slidable, closed, hidden, have nested content, you can define the " +
                "spacing when open and closed." +
                 "<p>For more information, see the " +
                 "<a href=\"http://wiki.icefaces.org/display/ICE/BorderLayout\">BorderLayout Wiki Documentation</a>."
        )

/*@ClientBehaviorHolder(events = {
	@ClientEvent(name="toggle", javadoc="Fired when the toggle button is activated to expand or compress the borderPanel (default event).",
	    tlddoc="Fired when the toggle button is activated to expand or collapse the borderPanel (default event).",
        defaultRender="@all",
        defaultExecute="@this",
        argumentClass="org.icefaces.ace.event.ToggleEvent"),

    @ClientEvent(name="close",
        javadoc="Fired when the close button is activated to remove the borderPanel from view.",
        tlddoc="Fired when the close button is activated to remove the borderPanel from view.",
        defaultRender="@all",
        defaultExecute="@this",
        argumentClass="org.icefaces.ace.event.CloseEvent")
}, defaultEvent="toggle")  */

public class BorderPanelMeta extends UIPanelMeta {

	@Property(tlddoc="location of the borderPanel. Must be NORTH, SOUTH, EAST, WEST or CENTER. " +
            "The only required location is Center.  All others are optional")
    private Location location;

    @Property(tlddoc="Boolean value that specifies a borderPanel is resizable. " +
            "This attribute does not pertain to center panel.")
	private Boolean resizable;

	@Property(tlddoc="Style to apply to the container element.")
	private String style;

	@Property(tlddoc="Style class to apply to the container element.")
	private String styleClass;

	@Property(tlddoc="Boolean value that specifies whether the panel can be closed. This attribute does not pertain to center panel")
	private Boolean closable;

    @Property(tlddoc="Boolean value that specifies whether this panel can slide open over top of another panel. " +
            "This attribute does not pertain to center panel")
	private Boolean slidable;

	@Property(tlddoc="Boolean value that specifies whether the panel is visible. " +
            "This attribute does not pertain to the center panel", defaultValue="true")
	private boolean visible;

    @Property(tlddoc="Model of panel options that pertain to this particular panel.  " +
            "Note that the center panel cannot be resized, collapsed or toggled, and is required.")
    private PanelModel model;

}