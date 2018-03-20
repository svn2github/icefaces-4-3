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

package org.icefaces.ace.component.dashboard;

import org.icefaces.ace.meta.annotation.*;
import org.icefaces.ace.meta.baseMeta.UIPanelMeta;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;

import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import org.icefaces.resources.ICEResourceLibrary;

@Component(
        tagName         = "dashboard",
        componentClass  = "org.icefaces.ace.component.dashboard.Dashboard",
        rendererClass   = "org.icefaces.ace.component.dashboard.DashboardRenderer",
        generatedClass  = "org.icefaces.ace.component.dashboard.DashboardBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.Dashboard",
        rendererType    = "org.icefaces.ace.component.DashboardRenderer",
		componentFamily = "org.icefaces.ace.Dashboard",
		tlddoc = ""
        )
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "fontawesome/font-awesome.css"),
	@ICEResourceDependency(name = "dashboard/jquery.gridster.css"),
	@ICEResourceDependency(name = "dashboard/dashboard.css"),
	@ICEResourceDependency(name = "dashboard/jquery.gridster.js"),
	@ICEResourceDependency(name = "dashboard/dashboard.js")
})
public class DashboardMeta extends UIPanelMeta {

	@Property(tlddoc="Style to apply to the main container of the layout.")
	private String style;

	@Property(tlddoc="Style class of the main container of the layout.")
	private String styleClass;
}