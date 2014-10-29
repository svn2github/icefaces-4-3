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

package org.icefaces.mobi.component.dataview;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.DefaultValueType;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.baseMeta.UIComponentBaseMeta;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;


@Component(
    tagName = "dataView",
    componentClass = "org.icefaces.mobi.component.dataview.DataView",
    rendererClass = "org.icefaces.mobi.component.dataview.DataViewRenderer",
    generatedClass = "org.icefaces.mobi.component.dataview.DataViewBase",
    componentType = "org.icefaces.DataView",
    rendererType = "org.icefaces.DataViewRenderer",
    extendsClass = "javax.faces.component.UIComponentBase",
    componentFamily = "org.icefaces.DataView",
    tlddoc = "The DataView component renders a table region for iterative output using columns " +
            "defined with the required DataViewColumns component child, and a detail region for " +
            "extensive viewing (and editing) of the row objects of the table model. The detail region" +
            "is defined with the DataViewDetails child component and it's contents are displayed when a " +
            "row of table region is activated, typically by a row tap." +
			"When using ajax, the detail region may contain arbitrary JSF components and is dynamically rendered with the ajax " + "request. When not using ajax, the detail region display logic is done entirely in the client, from " +
			"an existing rendering of the details region for each row. The components " +
			"and attributes supported for client-mode rendering are limited, mostly consisting of the standard h:* components."
)
@ResourceDependencies({
	@ResourceDependency( library = "org.icefaces.component.dataview", name = "dataview.css" ),
	@ResourceDependency(library = "icefaces.ace", name = "fontawesome/font-awesome.css"),
    @ResourceDependency( library = "org.icefaces.component.util", name = "component.js" ),
	@ResourceDependency( library = "org.icefaces.component.dataview", name = "dataview.js" )
})
@ClientBehaviorHolder(events = {
	@ClientEvent(name="select", 
	        javadoc="Fired when a row is selected. It enables server-side updates of the details area.",
	        tlddoc="Fired when a row is selected. It enables server-side updates of the details area.",
	        defaultRender="@all", defaultExecute="@this")
}, defaultEvent="select")
public class DataViewMeta extends UIComponentBaseMeta {
    @Property(tlddoc = "Disables this component, so it does not receive focus or get submitted.")
    boolean disabled;

    @Property(tlddoc = "Sets the CSS style definition to be applied to this component.")
    String style;

    @Property(tlddoc = "Sets the CSS class to apply to this component.")
    String styleClass;

    @Property(tlddoc = "Define the Expression Language variable name to be given to the row object during iterative rendering, " +
            "either in the ValueExpressions of a DataViewColumnModel defining a table cell or within the components of the detail region.")
    String var;

    @Property(tlddoc = "Define the Expression Language variable name to be given to the index of the row object during iterative rendering, " +
            "either in the ValueExpressions of a DataViewColumnModel defining a table cell or within the components of the detail region.")
    String rowIndexVar;

    @Property(tlddoc = "Define the index-based data model of DataView, currently supported types include List and instances of DataViewLazyDataModel.")
    Object value;

    @Property(tlddoc = "Renders cell background shading on the odd numbered rows.")
    boolean rowStripe;

    @Property(defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION,
              tlddoc = "Renders faint bottom border to each row.")
    boolean rowStroke;

    @Property(tlddoc = "ActiveRowIndex property allows control of the row object index currently displayed in the detail region. " +
            "The detail region components instances are not updated until DataView.initDetailContext() is called. " +
            "initDetailContext is regularly called within the component phase iterations. A value of -1 (or any negative value) is interpreted as no row being active at that moment.")
    Integer activeRowIndex;
}
