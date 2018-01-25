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

@Component(
        tagName         = "borderLayoutPane",
        componentClass  = "org.icefaces.ace.component.borderlayout.BorderLayoutPane",
        rendererClass   = "org.icefaces.ace.component.borderlayout.BorderLayoutPaneRenderer",
        generatedClass  = "org.icefaces.ace.component.borderlayout.BorderLayoutPaneBase",
        extendsClass    = "javax.faces.component.UIPanel",
        componentType   = "org.icefaces.ace.component.BorderLayoutPane",
        rendererType    = "org.icefaces.ace.component.BorderLayoutPaneRenderer",
		componentFamily = "org.icefaces.ace.BorderLayout",
		tlddoc = ""
        )
/*
@ClientBehaviorHolder(events = {

})
*/
public class BorderLayoutPaneMeta extends UIPanelMeta {

    @Property(tlddoc="The pane position that this component defines in the layout. Valid values are \"center\", \"north\", \"south\", \"east\", and \"west\". The center pane is required for the layout to work.")
    private String position;

    @Property(tlddoc="Specifies the size of the pane in pixels. For the north and south panes the size is the height, and for the east and west panes the size is the width. The size refers to the total size, including paddings and border widths. The value can be \"auto\" for an appropriate, standard size for the pane.", defaultValue = "auto")
    private String size;

    @Property(tlddoc="Specifies whether this pane is resizable or not. This attribute doesn't apply to the center pane.", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean resizable;

    @Property(tlddoc="", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean closable;

    @Property(tlddoc="Specifies whether this pane can be collapsed or not. ", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean collapsible;

    @Property(tlddoc="Specifies whether this pane starts out being visible or not.", defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean visible;

    @Property(tlddoc="Specifies whether this pane starts out being collapsed or not.", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean collapsed;

    @Property(tlddoc="Specifies the minumum size of the pane in pixels, meaning that it cannot be resized to a smaller size than this. For the north and south panes the size is the height, and for the east and west panes the size is the width. The size refers to the total size, including paddings and border widths.", defaultValue = "100", defaultValueType = DefaultValueType.EXPRESSION)
    private int minSize;

    @Property(tlddoc="Specifies the maximum size of the pane in pixels, meaning that it cannot be resized to a larger size than this. For the north and south panes the size is the height, and for the east and west panes the size is the width. The size refers to the total size, including paddings and border widths.", defaultValue = "0", defaultValueType = DefaultValueType.EXPRESSION)
    private int maxSize;

    @Property(tlddoc="Specifies the width in pixels of the resizable borders. This attribute doesn't apply to the center pane.", defaultValue = "5", defaultValueType = DefaultValueType.EXPRESSION)
    private int borderWidth;

    @Property(tlddoc="Specifies the width in pixels of the border when this pane is collapsed. This attribute doesn't apply to the center pane.", defaultValue = "20", defaultValueType = DefaultValueType.EXPRESSION)
    private int collapseSize;

    @Property(tlddoc="The text to render in the header of the pane.")
    private String headerText;

    @Property(tlddoc="The text to render in the footer of the pane.")
    private String footerText;
	
	@Property(tlddoc="Style to apply to the content container of the pane (i.e. excluding the header and the footer).")
	private String style;

	@Property(tlddoc="Style class of the main container of the pane (the one containing the header, content and footer).")
	private String styleClass;

    @Property(tlddoc="")
    private String effect;

    @Property(tlddoc="")
    private String effectSpeed;
}