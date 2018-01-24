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

    @Property(tlddoc="")
    private String position;

    @Property(tlddoc="", defaultValue = "auto")
    private String size;

    @Property(tlddoc="", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean resizable;

    @Property(tlddoc="", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean closable;

    @Property(tlddoc="", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean collapsible;

    @Property(tlddoc="", defaultValue = "true", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean visible;

    @Property(tlddoc="", defaultValue = "false", defaultValueType = DefaultValueType.EXPRESSION)
    private boolean collapsed;

    @Property(tlddoc="", defaultValue = "100", defaultValueType = DefaultValueType.EXPRESSION)
    private int minSize;

    @Property(tlddoc="", defaultValue = "0", defaultValueType = DefaultValueType.EXPRESSION)
    private int maxSize;

    @Property(tlddoc="", defaultValue = "6", defaultValueType = DefaultValueType.EXPRESSION)
    private int gutter;

    @Property(tlddoc="", defaultValue = "25", defaultValueType = DefaultValueType.EXPRESSION)
    private int collapseSize;

    @Property(tlddoc="")
    private String header;

    @Property(tlddoc="")
    private String footer;

    @Property(tlddoc="")
    private String style;

    @Property(tlddoc="")
    private String styleClass;

    @Property(tlddoc="")
    private String effect;

    @Property(tlddoc="")
    private String effectSpeed;
}