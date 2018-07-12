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

@Component(
        tagName         = "dashboardPane",
        componentClass  = "org.icefaces.ace.component.dashboard.DashboardPane",
        rendererClass   = "org.icefaces.ace.component.dashboard.DashboardPaneRenderer",
        generatedClass  = "org.icefaces.ace.component.dashboard.DashboardPaneBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.DashboardPane",
        rendererType    = "org.icefaces.ace.component.DashboardPaneRenderer",
		componentFamily = "org.icefaces.ace.Dashboard",
		tlddoc = "The DashboardPane component defines one of the panes of a Dashboard, which can include a header and a footer and an arbitrary number of child components and markup. <p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/DashboardPane\">DashboardPane Wiki Documentation</a>."
        )

@ClientBehaviorHolder(events = {
	@ClientEvent(name="dragStop", javadoc="Fired when the pane is dragged and dropped by the user.", tlddoc="Fired when the pane is dragged and dropped by the user.", defaultRender="@all", defaultExecute="@this", argumentClass="org.icefaces.ace.event.DashboardDragStopEvent"),
	@ClientEvent(name="resize", javadoc="Fired when the pane is resized by the user.", tlddoc="Fired when the pane is resized by the user.", defaultRender="@all", defaultExecute="@this", argumentClass="org.icefaces.ace.event.DashboardResizeEvent"),
	@ClientEvent(name="close", javadoc="Fired when the pane is closed by the user.", tlddoc="Fired when the pane is closed by the user.", defaultRender="@all", defaultExecute="@this", argumentClass="org.icefaces.ace.event.CloseEvent")
}, defaultEvent="dragStop")
public class DashboardPaneMeta extends UIPanelMeta {

    @Property(tlddoc="The horizontal size of this pane as the number columns it occupies.", defaultValue = "1")
    private int sizeX;

    @Property(tlddoc="The vertical size of the pane as the number of rows it occupies.", defaultValue = "1")
    private int sizeY;

    @Property(tlddoc="The 1-relative row position of this pane. If set to 0, it will be given the next available horizontal position that is not already occupied by a pane with specific row and column values. If this pane overlaps with the position of another pane, this or the other pane will be automatically moved to a more appropriate available space. This value will be automatically updated as the pane changes position in the client, either directly or indirectly, by the dragStop, resize and close ajax events.", defaultValue = "0")
    private int row;

    @Property(tlddoc="The 1-relative column position of this pane. If set to 0 or if the value is greater than \"maxColumns\" in the parent dashboard component, it will be given the next available horizontal position that is not already occupied by a pane with specific row and column values. If this pane overlaps with the position of another pane, this or the other pane will be automatically moved to a more appropriate available space. This value will be automatically updated as the pane changes position in the client, either directly or indirectly, by the dragStop, resize and close ajax events.", defaultValue = "0")
    private int column;

    @Property(tlddoc="Specifies whether this pane can be closed or not. If true, a close button will be rendered at the upper right corner of the pane header.", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean closable;

    @Property(tlddoc="Specifies whether this pane is closed or not.", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean closed;

    @Property(tlddoc="The text to render in the header of the pane. If no header text is specified and no header facet is defined, the header will not be rendered.")
    private String headerText;

    @Property(tlddoc="The text to render in the footer of the pane. If no footer text is specified and no footer facet is defined, the header will not be rendered.")
    private String footerText;
	
	@Property(tlddoc="Style to apply to the content container of the pane (i.e. excluding the header and the footer).")
	private String style;

	@Property(tlddoc="Style class of the main container of the pane (the one containing the header, content and footer).")
	private String styleClass;

    @Field
    private Integer oldSizeX;

    @Field
    private Integer oldSizeY;

    @Field
    private Integer oldRow;

    @Field
    private Integer oldColumn;

    @Facets
    class FacetsMeta {
        @Facet
        UIComponent header;
        @Facet
        UIComponent footer;
    }
}