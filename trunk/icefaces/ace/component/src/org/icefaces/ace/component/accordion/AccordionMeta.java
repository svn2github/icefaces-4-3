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

package org.icefaces.ace.component.accordion;

import org.icefaces.ace.meta.annotation.*;
import org.icefaces.ace.meta.baseMeta.UIPanelMeta;
import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import org.icefaces.resources.ICEResourceLibrary;

import javax.el.MethodExpression;

@Component(
        tagName         = "accordion",
        componentClass  = "org.icefaces.ace.component.accordion.Accordion",
        rendererClass   = "org.icefaces.ace.component.accordion.AccordionRenderer",
        generatedClass  = "org.icefaces.ace.component.accordion.AccordionBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.Accordion",
        rendererType    = "org.icefaces.ace.component.AccordionRenderer",
		componentFamily = "org.icefaces.ace.component",
		tlddoc = "The Accordion is a container component that displays contents in a stacked format. Keyboard interaction is supported when the accordion is focused and works by pressing the space or enter keys on the highlighted pane to open/close it and using the arrow keys to move up or down across the panes." +
                 "<p>For more information, see the " +
                 "<a href=\"http://wiki.icefaces.org/display/ICE/Accordion\">Accordion Wiki Documentation</a>."
        )

@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "util/ace-core.js"),
	@ICEResourceDependency(name = "jquery/jquery.js"),
	@ICEResourceDependency(name = "util/ace-jquery-ui.js"),
	@ICEResourceDependency(name = "accordion/accordion.js")
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="panechange",
            javadoc="Fired when the active accordion pane is changed (default event).",
            tlddoc="Fired when the active accordion pane is changed (default event).",
            defaultRender="@all", defaultExecute="@this")
}, defaultEvent="panechange")

public class AccordionMeta extends UIPanelMeta {
	
	@Property(tlddoc="Index of the active pane.", defaultValue="0")
	private int activeIndex;
	
	@Property(tlddoc="Inline style of the container element.")
	private String style;
	
	@Property(tlddoc="Style class of the container element.")
	private String styleClass;
	
	@Property(tlddoc="Disables or enables the accordion.", defaultValue="false")
	private boolean disabled;
	
	@Property(tlddoc="Effect to use when toggling the panes.", defaultValue="slide")
	private String effect;
	
	@Property(tlddoc="When enabled, pane with highest content is used to calculate the height.", defaultValue="true")
	private boolean autoHeight;
	
	@Property(tlddoc="Defines if accordion can be collapsed all together.", defaultValue="false")
	private boolean collapsible;
	
	@Property(tlddoc="When enabled, accordion fills the height of its parent container.", defaultValue="false")
	private boolean fillSpace;
	
	@Property(tlddoc="Client side event to toggle the panes.")
	private String event;
	
	@Property(tlddoc="Server side listener to invoke when active pane changes", expression= Expression.METHOD_EXPRESSION, methodExpressionArgument="org.icefaces.ace.event.AccordionPaneChangeEvent")
	private MethodExpression paneChangeListener;

    @Property(tlddoc="Flag indicating whether or not this component should prepend its id to its descendent's id during the clientId generation process. If this flag is not set, the default value is false.", defaultValue="false")
	private boolean prependId;

	@Field
	private String tabTitles;
}
