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
	@ICEResourceDependency(name = "util/ace-core.js"),
	@ICEResourceDependency(name = "jquery/jquery.js"),
	@ICEResourceDependency(name = "util/ace-jquery-ui.js"),
	@ICEResourceDependency(name = "drawerpanel/drawerpanel.js")
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="close", javadoc="Fired when the panel is closed (default event).", tlddoc="Fired when the panel is closed (default event).", defaultRender="@all", defaultExecute="@this", argumentClass="org.icefaces.ace.event.CloseEvent")
}, defaultEvent="close")

public class DrawerPanelMeta extends UIPanelMeta {
	
	@Property(tlddoc="Header text")
	private String header;
	
	@Property(tlddoc="Boolean value that specifies whether the document should be shielded with a partially transparent mask to require the user to close the Panel before being able to activate any elements in the document", defaultValue="false")
	private boolean modal;

	@Property(tlddoc="visible can be used to toggle visibility on the server, rendered should not be used that way, setting rendered=false on a visible modal drawer will not remove the modality layer, visible=false must be set first (or client-side JS function called)", defaultValue="false")
	private boolean visible;

	@Property(tlddoc="Width of the element in pixels. Default (not specified or value <= 0) is auto. If auto, resizable should be false, or resizing may hehave erratically. If auto, IE7 may not size or position properly.", defaultValue="Integer.MIN_VALUE")
	private int width;

	@Property(tlddoc="zindex property to control overlapping with other elements", defaultValue="1000")
	private int zindex;
	
	@Property(tlddoc="Style class of the main container of drawer")
	private String styleClass;
	
	@Property(tlddoc="Style to apply to the container element.")
	private String style;
	
	@Property(tlddoc="Effect to use when showing the drawer. Possible values are 'blind', 'clip', 'drop', 'explode, 'fade', 'fold', 'puff', 'slide', 'scale', 'bounce', 'highlight', 'pulsate', and 'shake' (Some effects are not supported in IE7, see wiki page for more information).")
	private String showEffect;
	
	@Property(tlddoc="Effect to use when hiding the drawer. Possible values are 'blind', 'clip', 'drop', 'explode, 'fade', 'fold', 'puff', 'slide', 'scale', 'bounce', 'highlight', and 'shake' (Some effects are not supported in IE browsers, see wiki page for more information).")
	private String hideEffect;
	
	@Property(tlddoc="Specifies where the drawer should be displayed relative to the viewport. Possible values: \n1) a single string representing position within viewport: 'center', 'left', 'right', 'top', 'bottom'.\n2) an array containing an x,y coordinate pair in pixel offset from left, top corner of viewport (e.g. [350,100])\n3) an array containing x,y position string values (e.g. ['right','top'] for top right corner).")
	private String position;
	
	@Property(tlddoc="Boolean value that Specifies whether the drawer should close when it has focus and the user presses the escape (ESC) key.", defaultValue="true")
	private boolean closeOnEscape;
	
	@Property(tlddoc="Javascript code to be executed when showing the drawer")
	private String onShow;
	
	@Property(tlddoc="Javascript code to be executed when hiding the drawer")
	private String onHide;
	
	@Property(tlddoc="Boolean value that specifies whether the drawer should have a header (default true)", defaultValue="true")
	private boolean showHeader;

    @Property(tlddoc = "Specifies the ID of the component that should receive focus when the drawer is opened.")
    String setFocus;
}
