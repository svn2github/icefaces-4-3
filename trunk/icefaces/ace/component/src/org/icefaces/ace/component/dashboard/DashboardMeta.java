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
		tlddoc = "The Dashboard component is a container with panes that are resizable, draggable and closable. The panes are arranged in a grid with fixed base dimensions, and they can span multiple rows and/or columns. <p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/Dashboard\">Dashboard Wiki Documentation</a>."
        )
@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
	@ICEResourceDependency(name = "fontawesome/font-awesome.css"),
	@ICEResourceDependency(name = "dashboard/jquery.gridster.js"),
	@ICEResourceDependency(name = "dashboard/dashboard.js")
})
public class DashboardMeta extends UIPanelMeta {

	@Property(tlddoc="Style to apply to the main container element.")
	private String style;

	@Property(tlddoc="Style class of the main container element.")
	private String styleClass;

    @Property(tlddoc="Specifies whether the panes are draggable or not.", defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean draggable;

    @Property(tlddoc="Specifies whether the panes are resizable or not.", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean resizable;

    @Property(tlddoc="The base/minimum width of the panes in pixels, which determines the column width as well. This value can be multiplied by the pane's \"sizeX\" property.", defaultValue = "150")
    private int paneWidth;

    @Property(tlddoc="The base/minimum height of the panes in pixels, which determines the row height as well. This value can be multiplied by the pane's \"sizeY\" property.", defaultValue = "150")
    private int paneHeight;

    @Property(tlddoc="The size of the horizontal margins between panes, in pixels.", defaultValue = "10")
    private int marginX;

    @Property(tlddoc="The size of the vertical margins between panes, in pixels.", defaultValue = "10")
    private int marginY;

    @Property(tlddoc="The maximum number of columns to allow in the dashboard. Valid values range from 1 to 100. Any other value will be treated as 100.", defaultValue = "5")
    private int maxColumns;

	@Field
	private Long lastRefresh;
}