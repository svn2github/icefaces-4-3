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

import org.icefaces.ace.meta.annotation.*;
import org.icefaces.ace.meta.baseMeta.UIPanelMeta;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;

import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import org.icefaces.resources.ICEResourceLibrary;

@Component(
        tagName         = "borderLayout",
        componentClass  = "org.icefaces.ace.component.borderlayout.BorderLayout",
        rendererClass   = "org.icefaces.ace.component.borderlayout.BorderLayoutRenderer",
        generatedClass  = "org.icefaces.ace.component.borderlayout.BorderLayoutBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.BorderLayout",
        rendererType    = "org.icefaces.ace.component.BorderLayoutRenderer",
		componentFamily = "org.icefaces.ace.BorderLayout",
		tlddoc = "The BorderLayout component is a container with five different regions (north, south, east, west and center), which are collapsible, expandable, closable, and resizable, except for the center region, which is also required to be defined. The regions are defined by nesting BorderLayoutPane components. <p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/BorderLayout\">BorderLayout Wiki Documentation</a>."
        )
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "fontawesome/font-awesome.css"),
	@ICEResourceDependency(name = "borderlayout/jquerylayout.js"),
	@ICEResourceDependency(name = "borderlayout/borderlayout.js")
})
/*
@ClientBehaviorHolder(events = {

})
*/
public class BorderLayoutMeta extends UIPanelMeta {

    @Property(tlddoc="", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean fullPage;

    @Property(tlddoc="")
    private String style;

    @Property(tlddoc="")
    private String styleClass;
}