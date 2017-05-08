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

package org.icefaces.ace.component.textareaentry;

import org.icefaces.ace.renderkit.InputRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.ace.util.Utils;
import org.icefaces.component.PassthroughAttributes;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.util.EnvUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@MandatoryResourceComponent(tagName = "textAreaEntry", value = "org.icefaces.ace.component.textareaentry.TextAreaEntry")
public class TextAreaEntryRenderer extends InputRenderer {
    private final static String[] PASSTHROUGH_ATTRIBUTES = ((PassthroughAttributes) TextAreaEntry.class.getAnnotation(PassthroughAttributes.class)).value();

    public static void printMap(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue() + ";");
        }
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        TextAreaEntry textAreaEntry = (TextAreaEntry) component;

        if (textAreaEntry.isDisabled() || textAreaEntry.isReadonly()) {
            return;
        }

        String clientId = textAreaEntry.getClientId(context);
        Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
        String submittedValue = requestParameterMap.get(clientId + "_input");
        if (submittedValue == null && requestParameterMap.get(clientId + "_label") != null) {
            submittedValue = "";
        }

        if (submittedValue != null) {
            textAreaEntry.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(context, textAreaEntry);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, Object> domUpdateMap = new HashMap<String, Object>();
        TextAreaEntry textAreaEntry = (TextAreaEntry) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = textAreaEntry.getClientId(context);
        domUpdateMap.clear();

        writer.startElement("span", component);
        writer.writeAttribute("id", clientId, "clientId");
		renderResetSettings(context, component);

        Map<String, Object> labelAttributes = getLabelAttributes(component);
		labelAttributes.put("fieldClientId", clientId + "_input");

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_markup", null);
        writer.writeAttribute("class", "ui-textareaentry-container", null);
        encodeLabelAndInput(component, labelAttributes, domUpdateMap);
        writer.endElement("span");

        domUpdateMap.putAll(labelAttributes);
        writer.startElement("span", null);
        writer.writeAttribute("data-hashcode", domUpdateMap.hashCode(), null);
        writer.writeAttribute("style", "display: none;", null);
        writer.endElement("span");
        writer.endElement("span");
    }

    @Override
    protected void writeInputField(UIComponent component, Map<String, Object> labelAttributes, Map<String, Object> domUpdateMap) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        ResponseWriter writer = context.getResponseWriter();
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        TextAreaEntry textAreaEntry = (TextAreaEntry) component;
        boolean ariaEnabled = EnvUtils.isAriaEnabled(context);

        String clientId = textAreaEntry.getClientId(context);
        int maxLength = textAreaEntry.getMaxlength();
        writer.startElement("textarea", null);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
		writer.writeAttribute("data-ice-clear-ignore", "true", null);
        if (ariaEnabled) {
            writer.writeAttribute("role", "textbox", null);
        }
        if (maxLength > -1) {
            writer.writeAttribute("maxlength", maxLength, null);
        }
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);
        String iceFocus = (String) paramMap.get("ice.focus");
        String inFieldLabel = (String) labelAttributes.get("inFieldLabel");
        String value;
		if (textAreaEntry.isValid()) {
			value = ComponentUtils.getStringValueToRender(context, component);
		} else {
			value = (String) textAreaEntry.getSubmittedValue();
		}
        String defaultClass = themeForms() ? TextAreaEntry.THEME_INPUT_CLASS : TextAreaEntry.PLAIN_INPUT_CLASS;
        defaultClass += getStateStyleClasses(textAreaEntry);
        if (isValueBlank(value) && !isValueBlank(inFieldLabel) && !clientId.equals(iceFocus)) {
            writer.writeAttribute("name", clientId + "_label", null);
            value = inFieldLabel;
            defaultClass += " " + IN_FIELD_LABEL_STYLE_CLASS;
            labelAttributes.put("labelIsInField", true);
        }
        defaultClass += textAreaEntry.isResizable() ? " ui-textareaentry-resizable" : " ui-textareaentry-non-resizable";

		String accesskey = textAreaEntry.getAccesskey();
		if (accesskey != null) writer.writeAttribute("accesskey", accesskey, null);

        if (ariaEnabled) {
            final TextAreaEntry compoent = (TextAreaEntry) component;
            Map<String, Object> ariaAttributes = new HashMap<String, Object>() {{
                put("multiline", true);
                put("readonly", compoent.isReadonly());
                put("required", compoent.isRequired());
                put("disabled", compoent.isDisabled());
                put("invalid", !compoent.isValid());
            }};
            writeAriaAttributes(ariaAttributes, labelAttributes);
        }

		String placeholder = textAreaEntry.getPlaceholder();
		if (placeholder != null) writer.writeAttribute("placeholder", placeholder, null);
		if (textAreaEntry.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if (textAreaEntry.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        String style = textAreaEntry.getStyle();
        if (style != null) writer.writeAttribute("style", style, "style");

        String styleClass = textAreaEntry.getStyleClass();
        Utils.writeConcatenatedStyleClasses(writer, defaultClass, styleClass);
        domUpdateMap.put("defaultClass", defaultClass);
        domUpdateMap.put("styleClass", styleClass);
        domUpdateMap.put("value", value);

        if (value != null) {
            writer.writeText(value, "value");
        }

        JSONBuilder jb = JSONBuilder.create();
        jb.beginFunction("ice.ace.lazy")
                .item("TextAreaEntry")
                .beginArray()
                .item(clientId)
          .beginMap()
          .entryNonNullValue("inFieldLabel", (String) labelAttributes.get("inFieldLabel"))
          .entry("inFieldLabelStyleClass", IN_FIELD_LABEL_STYLE_CLASS);
        if (maxLength > -1) {
            jb.entry("maxlength", maxLength);
        }
        jb.entry("placeholder", placeholder != null ? placeholder : "");

        encodeClientBehaviors(context, textAreaEntry, jb);

        if (!themeForms()) {
            jb.entry("theme", false);
        }

        jb.endMap().endArray().endFunction();

        for (int i = 0; i < PASSTHROUGH_ATTRIBUTES.length; i++) {
            String name = PASSTHROUGH_ATTRIBUTES[i];
            if ("onfocus".equals(name)) {
                ComponentUtils.renderPassThroughAttribute(writer, textAreaEntry, name, jb.toString());
            } else {
                ComponentUtils.renderPassThroughAttribute(writer, textAreaEntry, name);
            }
        }
        ComponentUtils.renderExternalPassThroughAttributes(writer, textAreaEntry);

        writer.endElement("textarea");
    }

	protected void renderResetSettings(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		String clientId = component.getClientId(context);
		String labelPosition = (String) component.getAttributes().get("labelPosition");

		JSONBuilder jb = JSONBuilder.create();
		jb.beginArray();
		jb.item("TextAreaEntry");
		jb.beginArray();
		jb.item(clientId);

		if ("inField".equals(labelPosition)) {
			TextAreaEntry textAreaEntry = (TextAreaEntry) component;
			String label = (String) component.getAttributes().get("label");	
			String indicatorPosition = (String) component.getAttributes().get("indicatorPosition");
			String optionalIndicator = (String) component.getAttributes().get("optionalIndicator");
			String requiredIndicator = (String) component.getAttributes().get("requiredIndicator");
			if ("labelLeft".equals(indicatorPosition)) {
				if (textAreaEntry.isRequired())
					label = requiredIndicator + label;
				else
					label = optionalIndicator + label;
			} else if ("labelRight".equals(indicatorPosition)) {
				if (textAreaEntry.isRequired())
					label = label + requiredIndicator;
				else
					label = label + optionalIndicator;
			}
			jb.item(label);
			jb.item(IN_FIELD_LABEL_STYLE_CLASS);
		}

		jb.endArray();
		jb.endArray();

		writer.writeAttribute("data-ice-reset", jb.toString(), null);
	}
}
