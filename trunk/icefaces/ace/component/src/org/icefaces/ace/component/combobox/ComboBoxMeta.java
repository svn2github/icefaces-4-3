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

package org.icefaces.ace.component.combobox;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.annotation.Field;
import org.icefaces.ace.meta.baseMeta.HtmlInputTextMeta;
import org.icefaces.ace.meta.annotation.Expression;
import org.icefaces.ace.meta.annotation.Implementation;
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
        tagName = "comboBox",
        componentClass = "org.icefaces.ace.component.combobox.ComboBox",
        generatedClass = "org.icefaces.ace.component.combobox.ComboBoxBase",
        extendsClass = "javax.faces.component.html.HtmlInputText",
		rendererClass   = "org.icefaces.ace.component.combobox.ComboBoxRenderer",
        componentFamily = "org.icefaces.ace.ComboBox",
        componentType = "org.icefaces.ace.component.ComboBox",
		rendererType    = "org.icefaces.ace.component.ComboBoxRenderer",
        disinheritProperties = {"onclick","onblur", "onchange", "ondblclick", "onselect", "onmouseup", "onmousedown","onfocus", "onkeydown",
                                 "onkeypress", "onkeyup", "onmousemove", "onmouseover", "onmouseout"},
        tlddoc = "A component that merges a select and an input field." +
                 "<p>For more information, see the " +
                 "<a href=\"http://wiki.icefaces.org/display/ICE/ComboBox\">ComboBox Wiki Documentation</a>."
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
public class ComboBoxMeta extends HtmlInputTextMeta {

    @Property(tlddoc = "Style class name of the container element.", defaultValue="")
    private String style;
	
    @Property(tlddoc = "Style class name of the container element.", defaultValue="")
    private String styleClass;
	
    @Property(tlddoc = "The width of the text input field, in pixels.", defaultValue="200")
    private int width;
	
    @Property(tlddoc = "Maximum height in pixels of the list of possible matches (if 0, then the size is automatically adjusted to show all possible matches).", defaultValue="200")
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
	
	@Property(tlddoc="Defines the method of filter comparison used. " +
            "Types available include: \"contains\", \"exact\", \"startsWith\", \"endsWith\" and \"none\".", defaultValue="none")
	private String filterMatchMode;
	
    @Property(tlddoc = "Delay in milliseconds for showing the list of possible matches after typing a character.", defaultValue="400")
    private int delay;

    @Property(tlddoc = "Minimum number of characters that must be in the text field before submitting and before producing the list of possible matches.", defaultValue="0")
    private int minChars;

    @Property(tlddoc = "Boolean value that indicates whether the filtering should be case sensitive or not.", defaultValue="false")
    private boolean caseSensitive;
	
    @Property(tlddoc = "The maximum number of possible options to show to the user (set to 0 to display all rows).", defaultValue="0")
    private int rows;
	
    @Property(tlddoc = "Boolean value that indicates whether the popup list should be displayed when typing in the text field or only when clicking the down arrow button.", defaultValue="true")
    private boolean showListOnInput;

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
	
	@Property(tlddoc = "Not supported. Use 'width' instead.", implementation=Implementation.EXISTS_IN_SUPERCLASS)
	private int size;	
	
	@Property(tlddoc="Effect to use when showing the list. Possible values are 'blind', 'bounce', 'clip', 'drop', 'explode', 'fade', 'fold', 'puff', 'pulsate', 'scale', 'slide', and 'shake'.", defaultValue="fade")
	private String showEffect;

	@Property(tlddoc="Length of time in milliseconds the show effect will last for.", defaultValue="150")
	private int showEffectLength;

	@Property(tlddoc="Effect to use when hiding the list. Possible values are 'blind', 'bounce', 'clip', 'drop', 'explode', 'fade', 'fold', 'puff', 'pulsate', 'scale', and 'slide'.", defaultValue="fade")
	private String hideEffect;

	@Property(tlddoc="Length of time in milliseconds the hide effect will last for.", defaultValue="150")
	private int hideEffectLength;
	
    @Property(tlddoc = "The HTML5 placeholder attribute represents a short hint (a word or short phrase) intended to aid the user with data entry " +
    		"when the input element has no value. If the placeholder attribute is not supported by the browser, the label 'inField' functionality " +
			"will be used instead.")
    private String placeholder;
	
    @Field()
    private List itemList;
}
