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
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import org.icefaces.ace.util.JSONBuilder;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.annotation.Field;
import org.icefaces.ace.meta.baseMeta.UIPanelMeta;
import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;
import org.icefaces.resources.ICEResourceLibrary;
import org.icefaces.ace.model.borderlayout.PanelDefaultModel;

@Component(
        tagName         = "borderLayout",
        componentClass  = "org.icefaces.ace.component.borderlayout.BorderLayout",
        rendererClass   = "org.icefaces.ace.component.borderlayout.BorderLayoutRenderer",
        generatedClass  = "org.icefaces.ace.component.borderlayout.BorderLayoutBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.BorderLayout",
        rendererType    = "org.icefaces.ace.component.BorderLayoutRenderer",
		componentFamily = "org.icefaces.ace.BorderLayout",
		tlddoc = "BorderLayout manages children which are borderPanels which have toggling, closing and resizing. " +
                 "<p>For more information, see the " +
                 "<a href=\"http://wiki.icefaces.org/display/ICE/BorderLayout\">BorderLayout Wiki Documentation</a>."
        )
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
        @ICEResourceDependency(name="util/ace-borderlayout.js")
})


public class BorderLayoutMeta extends UIPanelMeta {

	@Property(tlddoc="Boolean value that specifies whether to apply basic styles to ALL borderPanes " +
            "directly to resizers and buttons. If this option is enabled, must use \'!important\' to override " +
            "the default styles on the individual panes", defaultValue="false")
	private boolean applyDefaultStyles;

    @Property(defaultValue="false", tlddoc="handles the passing of bookmarks to a page.  When position and scrolling " +
            "of elements are altered after a page load, by makeing this attribute true, the bookmark will be reapplied " +
            "after the layout is created, causing the bookmark/anchor to be scrolled to bring it into view")
    private boolean scrollToBookmarkOnLoad;

    @Property(tlddoc="If set, the default values that can be applied to all panels in the borderLayout.  Note that applyDefaultStyles can be " +
            "set to true in this model, but in one of the borderPanel components the same attribute can be set to false to override the " +
            "default styles. ")
    private PanelDefaultModel defaults;
	
	@Property(tlddoc="Style to apply to the container element.")
	private String style;
	
	@Property(tlddoc="Style class to apply to the container element only.")
	private String styleClass;

    @Field
    private JSONBuilder northVar;

    @Field
    private JSONBuilder southVar;

    @Field
    private JSONBuilder eastVar;

    @Field
    private JSONBuilder centerVar;

    @Field
    private JSONBuilder westVar;
}
