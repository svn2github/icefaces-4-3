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

import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.baseMeta.UIPanelMeta;
import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;
import org.icefaces.resources.ICEResourceLibrary;

@Component(
        tagName         = "drawerPanel",
        componentClass  = "org.icefaces.ace.component.drawerpanel.DrawerPanel",
        rendererClass   = "org.icefaces.ace.component.drawerpanel.DrawerPanelRenderer",
        generatedClass  = "org.icefaces.ace.component.drawerpanel.DrawerPanelBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.DrawerPanel",
        rendererType    = "org.icefaces.ace.component.DrawerPanelRenderer",
		componentFamily = "org.icefaces.ace.DrawerPanel",
		tlddoc = "The DrawerPanel is a container component that can overlay other elements on the page, appearing from an edge of the page. The DrawerPanel has several customization options such as modal, width, position. <p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/DrawerPanel\">DrawerPanel Wiki Documentation</a>."
        )
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "fontawesome/font-awesome.css"),
	@ICEResourceDependency(name = "drawerpanel/drawerpanel.js")
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="open", javadoc="Fired when the drawer panel is opened (default event).", tlddoc="Fired when the drawer panel is opened (default event).", defaultRender="@all", defaultExecute="@this", argumentClass="org.icefaces.ace.event.OpenEvent"),
	@ClientEvent(name="close", javadoc="Fired when the drawer panel is closed.", tlddoc="Fired when the drawer panel is closed.", defaultRender="@all", defaultExecute="@this", argumentClass="org.icefaces.ace.event.CloseEvent")
}, defaultEvent="open")

public class DrawerPanelMeta extends UIPanelMeta {
	
	@Property(tlddoc="Text that will appear in the header of the drawer.")
	private String header;
	
	@Property(tlddoc="Boolean value that specifies whether the document should be shielded with a partially transparent mask to require the user to close the panel before being able to activate any elements in the document.", defaultValue="false")
	private boolean modal;

	@Property(tlddoc="This attribute can be used to toggle visibility on the server, rendered should not be used that way, setting rendered=false on a visible modal drawer will not remove the modality layer, visible=false must be set first (or client-side JS function called).", defaultValue="false")
	private boolean visible;

	@Property(tlddoc="Width of the element in pixels. Default (not specified or value <= 0) is auto. If auto, IE7 may not size or position properly. This attribute only applies when the 'position' is 'left' or 'right'.", defaultValue="Integer.MIN_VALUE")
	private int width;

	@Property(tlddoc="Height of the element in pixels. Default (not specified or value <= 0) is auto. If auto, IE7 may not size or position properly. This attribute only applies if the 'position' is 'top' or 'bottom'.", defaultValue="Integer.MIN_VALUE")
	private int height;

	@Property(tlddoc="z-index property to control overlapping with other elements.", defaultValue="1000")
	private int zindex;
	
	@Property(tlddoc="Style class of the main container of the drawer.")
	private String styleClass;
	
	@Property(tlddoc="Style to apply to the container element.")
	private String style;
	
	@Property(tlddoc="Effect to use when opening and closing the drawer. Possible values are 'drop', 'fade', and 'slide'. The default effect is 'slide'.", defaultValue="slide")
	private String effect;
	
	@Property(tlddoc="Specifies the side on which the drawer should be displayed. Possible values are 'left', 'right', 'top', and 'bottom'.", defaultValue="left")
	private String position;
	
	@Property(tlddoc="Boolean value that specifies whether the drawer should be closed when it has focus and the user presses the escape (ESC) key.", defaultValue="true")
	private boolean closeOnEscape;

	@Property(tlddoc="Boolean value that specifies whether the drawer should be closed when the user clicks (or touches) any part of the page outside the drawer.", defaultValue="true")
	private boolean closeOnOutsideClick;
	
	@Property(tlddoc="Javascript code to be executed when opening the drawer.")
	private String onOpen;
	
	@Property(tlddoc="Javascript code to be executed when closing the drawer.")
	private String onClose;
	
	@Property(tlddoc="Boolean value that specifies whether the drawer should have a header (default true). If 'showHandleClose' is set to true, a header will be shown regardless of the value of this attribute.", defaultValue="true")
	private boolean showHeader;

    @Property(tlddoc = "Specifies the ID of the component that should receive focus when the drawer is opened.")
    String setFocus;

	@Property(tlddoc="Boolean value that specifies whether to display a three-horizontal-bars icon to open the drawer. This handle will appear at the upper left corner of the viewport if the 'position' is 'left' or 'top', at the uper right corner of the viewport if the 'position' is 'right', and at the lower left corner of the viewport if the 'position' is 'bottom'. This handle will only be displayed if the 'container' is set to 'window'. If a handle is desired when applying the drawer to a specific container, it has to be done manually; please consult the wiki page for this component for guidelines and sample markup.", defaultValue="true")
	private boolean showHandleOpen;

	@Property(tlddoc="Boolean value that specifies whether to display a three-horizontal-bars icon inside the drawer to close it. This handle will appear at the upper left corner of the drawer, inside the drawer's header.", defaultValue="true")
	private boolean showHandleClose;

	@Property(tlddoc="Specify the container whose edges the drawer will appear from. The default mode is the entire window, but it's possible to specify a client id of an element on the page to have the drawer.", defaultValue="window")
	private String container;
}
