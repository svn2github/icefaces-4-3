/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
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

package org.icefaces.ace.component.selectmenu;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.annotation.Field;
import org.icefaces.ace.meta.baseMeta.UIInputMeta;
import org.icefaces.ace.meta.annotation.Expression;
import org.icefaces.ace.meta.annotation.ClientBehaviorHolder;
import org.icefaces.ace.meta.annotation.ClientEvent;
import org.icefaces.ace.api.IceClientBehaviorHolder;

import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import javax.el.ValueExpression;
import javax.el.MethodExpression;

import java.util.List;

@Component(
        tagName = "selectMenu",
        componentClass = "org.icefaces.ace.component.selectmenu.SelectMenu",
        generatedClass = "org.icefaces.ace.component.selectmenu.SelectMenuBase",
        extendsClass = "javax.faces.component.UIInput",
		rendererClass   = "org.icefaces.ace.component.selectmenu.SelectMenuRenderer",
        componentFamily = "org.icefaces.ace.SelectMenu",
        componentType = "org.icefaces.ace.component.SelectMenu",
		rendererType    = "org.icefaces.ace.component.SelectMenuRenderer",
        tlddoc = "" +
                 "<p>For more information, see the " +
                 "<a href=\"http://wiki.icefaces.org/display/ICE/SelectMenu\">SelectMenu Wiki Documentation</a>."
)
@ICEResourceDependencies({
	@ICEResourceDependency(library = ACEResourceNames.ACE_LIBRARY, name = ACEResourceNames.COMPONENTS_JS)
})
@ClientBehaviorHolder(events = {
	@ClientEvent( name="valueChange",
		javadoc="Fired whenever the value of the component changes.",
		tlddoc="Fired whenever the value of the component changes.",
		defaultRender="@all", defaultExecute="@this" ),
	@ClientEvent( name="blur",
		javadoc="Fired any time the component loses focus.",
		tlddoc="Fired any time the component loses focus.",
		defaultRender="@all", defaultExecute="@this" )},
	defaultEvent="valueChange" )
public class SelectMenuMeta extends UIInputMeta {

    @Property(tlddoc = "Style class name of the container element.", defaultValue="")
    private String style;
	
    @Property(tlddoc = "Style class name of the container element.", defaultValue="")
    private String styleClass;
	
    @Property(tlddoc = "The width of the text input field, in pixels.", defaultValue="200")
    private int width;
	
    @Property(tlddoc = "Maximum height in pixels of the list of possible matches (if 0, then the size is automatically adjusted to show all possible matches).")
    private int height;

    @Property(tlddoc = "Variable name to use for referencing each data object in the list when rendering via a facet.")
    private String listVar;

    @Property(tlddoc = "When rendering via a facet, this attribute specifies the list of data objects that contains all possible options.")
    private List listValue;
	
	@Property(expression = Expression.VALUE_EXPRESSION,
            tlddoc="ValueExpression that specifies the property of the data object to use as the value of the item for this component if it gets selected. This only applies when listvar is used and the rendering is done by means of a facet.")
	private Object itemValue;
	
	@Property(expression = Expression.VALUE_EXPRESSION,
            tlddoc="ValueExpression that specifies the property of the data object to use for checking whether the item should be disabled or not. It must evaluate to a boolean value. This only applies when listvar is used and the rendering is done by means of a facet. When using f:selectItem(s), the disabled property on that object/tag is used for the same effect.")
	private Object itemDisabled;
	
    @Property(tlddoc = "Indicator indicating that the user is required to provide a submitted value for this input component.")
    private String requiredIndicator;

    @Property(tlddoc = "Indicator indicating that the user is NOT required to provide a submitted value for this input component.")
    private String optionalIndicator;

    @Property(tlddoc = "Position of label relative to input field. Supported values are \"left/right/top/bottom/inField/none\". Default is \"none\".")
    private String labelPosition;
	
    @Property(tlddoc = "A localized user presentable name for this component.")
    private String label;

    @Property(tlddoc = "Position of input-required or input-optional indicator relative to input field or label. " +
            "Supported values are \"left/right/top/bottom/labelLeft/labelRight/none\". " +
            "Default is \"labelRight\" if labelPosition is \"inField\", \"right\" otherwise.")
    private String indicatorPosition;
	
	@Property(tlddoc = "Flag indicating that this element must never receive focus or be included in a subsequent submit. A value of false causes no attribute to be rendered, while a value of true causes the attribute to be rendered as disabled=\"disabled\".", defaultValue="false")
	private boolean disabled;
	
	@Property(tlddoc = "Flag indicating that this component will prohibit changes by the user. The element may receive focus unless it has also been disabled. A value of false causes no attribute to be rendered, while a value of true causes the attribute to be rendered as readonly=\"readonly\".", defaultValue="false")
	private boolean readonly;
	
	@Property(tlddoc = "Direction indication for text that does not inherit directionality. Valid values are \"LTR\" (left-to-right) and \"RTL\" (right-to-left).")
	private String dir;
	
	@Property(tlddoc = "Code describing the language used in the generated markup for this component.")
	private String lang;
	
	@Property(tlddoc = "Position of this element in the tabbing order for the current document. This value must be an integer between 0 and 32767.")
	private String tabindex;
	
	@Property(tlddoc = "Advisory title information about markup elements generated for this component.")
	private String title;
	
	@Property(tlddoc="Effect to use when showing the list. Possible values are 'blind', 'bounce', 'clip', 'drop', 'explode', 'fade', 'fold', 'puff', 'pulsate', 'scale', 'slide', and 'shake'.", defaultValue="fade")
	private String showEffect;

	@Property(tlddoc="Length of time in milliseconds the show effect will last for.", defaultValue="150")
	private int showEffectLength;

	@Property(tlddoc="Effect to use when hiding the list. Possible values are 'blind', 'bounce', 'clip', 'drop', 'explode', 'fade', 'fold', 'puff', 'pulsate', 'scale', and 'slide'.", defaultValue="fade")
	private String hideEffect;

	@Property(tlddoc="Length of time in milliseconds the hide effect will last for.", defaultValue="150")
	private int hideEffectLength;
	
    @Field()
    private List itemList;
}
