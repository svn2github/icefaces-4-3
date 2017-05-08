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

package org.icefaces.ace.component.textentry;

import org.icefaces.ace.renderkit.InputRenderer;
import org.icefaces.ace.util.*;
import org.icefaces.component.PassthroughAttributes;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.util.ClientDescriptor;
import org.icefaces.util.EnvUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@MandatoryResourceComponent(tagName="textEntry", value="org.icefaces.ace.component.textentry.TextEntry")
public class TextEntryRenderer extends InputRenderer {
    private final static String[] PASSTHROUGH_ATTRIBUTES = ((PassthroughAttributes) TextEntry.class.getAnnotation(PassthroughAttributes.class)).value();
    @Override
	public void decode(FacesContext context, UIComponent component) {
		TextEntry textEntry = (TextEntry) component;

        if(textEntry.isDisabled() || textEntry.isReadonly()) {
            return;
        }

		String clientId = textEntry.getClientId(context);
        Map<String,String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
        String submittedValue = requestParameterMap.get(clientId + "_input");
        if (submittedValue == null && requestParameterMap.get(clientId + "_label") != null) {
            submittedValue = "";
        }

        if(submittedValue != null) {
            textEntry.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(context, textEntry);
    }

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		TextEntry textEntry = (TextEntry) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = textEntry.getClientId(context);
        boolean ariaEnabled = EnvUtils.isAriaEnabled(context);

		String type = textEntry.validateType(textEntry.getType());
        boolean isNumberType = type.equals("number");
        boolean isDateType = type.equals("date");

        writer.startElement("span", component);
        writer.writeAttribute("id", clientId, "clientId");
		renderResetSettings(context, component);

        String defaultClass = themeForms() ? TextEntry.THEME_INPUT_CLASS : TextEntry.PLAIN_INPUT_CLASS;
        String styleClass = textEntry.getStyleClass();

        defaultClass += getStateStyleClasses(textEntry);
        Map<String, Object> labelAttributes = getLabelAttributes(component);
		labelAttributes.put("fieldClientId", clientId + "_input");

        writer.startElement("span", null);
        //check to see if passthrough library is loaded...

        writeLabelAndIndicatorBefore(labelAttributes);

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId + "_input", null);
		writer.writeAttribute("data-ice-clear-ignore", "true", null);
        if (ariaEnabled) {
            writer.writeAttribute("role", "textbox", null);
        }
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

        if (!isNumberType) {
            ClientDescriptor client = Utils.getClientDescriptor();
            String typeVal = (String)textEntry.getAttributes().get("type");
            if( isDateType && client.isAndroidOS() && client.isICEmobileContainer() ){ //Android container borks date types
                typeVal = "text";
            }
            type = typeVal;
        }
        if (!isDateType) writer.writeAttribute("autocorrect", textEntry.getAutocorrect(), null);
        else writer.writeAttribute("autocorrect", "on", null);
        writer.writeAttribute("autocapitalize", textEntry.getAutocapitalize(), null);
        writer.writeAttribute("pattern", textEntry.getPattern(), null);
        String embeddedLabel = null;
        String nameToRender = clientId + "_input";
        String valueToRender = null;
		if (textEntry.isValid() || textEntry.getSubmittedValue() == null) {
			valueToRender = textEntry.isRedisplay() ? ComponentUtils.getStringValueToRender(context, textEntry) : "";
		} else {
			valueToRender = (String) textEntry.getSubmittedValue();
		}
        boolean hasLabel = (Boolean) labelAttributes.get("hasLabel");
        String labelPosition = (String) labelAttributes.get("labelPosition");
        String label = (String) labelAttributes.get("label");
        boolean hasIndicator = (Boolean) labelAttributes.get("hasIndicator");
        String indicatorPosition = (String) labelAttributes.get("indicatorPosition");
        String indicator = (String) labelAttributes.get("indicator");
        if (valueToRender == null && isDateType){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
                Date aDate = new Date();
                valueToRender =  sdf.format(aDate);
        }
		if (hasLabel && labelPosition.equals("inField") && hasIndicator) {
			if (indicatorPosition.equals("labelLeft")) {
				label = indicator + label;
			} else if (indicatorPosition.equals("labelRight")) {
				label = label + indicator;
			}
		}
        if ((valueToRender == null || valueToRender.trim().length() <= 0) && hasLabel && labelPosition.equals("inField")) {
			writer.writeAttribute("type", type, null);
            nameToRender = clientId + "_label";
            valueToRender = label;
            defaultClass += " " + LABEL_STYLE_CLASS + "-infield";
        } else {
			writer.writeAttribute("type", textEntry.isSecret() ? "password" : type, null);
		}
		if (hasLabel && labelPosition.equals("inField")) embeddedLabel = label;
        writer.writeAttribute("name", nameToRender, null);
        writer.writeAttribute("value", valueToRender , null);

        if (ariaEnabled) {
            final TextEntry compoent = (TextEntry) component;
            Map<String, Object> ariaAttributes = new HashMap<String, Object>() {{
                put("readonly", compoent.isReadonly());
                put("required", compoent.isRequired());
                put("disabled", compoent.isDisabled());
                put("invalid", !compoent.isValid());
            }};
            writeAriaAttributes(ariaAttributes, labelAttributes);
        }

        if(textEntry.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if(textEntry.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        String style = textEntry.getStyle();
        if(style != null) writer.writeAttribute("style", style, "style");

        Utils.writeConcatenatedStyleClasses(writer, defaultClass, styleClass);
		
        JSONBuilder jb = JSONBuilder.create();
        jb.beginFunction("ice.ace.lazy")
          .item("TextEntry")
          .beginArray()
          .item(clientId)
          .beginMap()
          .entryNonNullValue("embeddedLabel", embeddedLabel)
          .entry("autoTab", textEntry.isAutoTab() && textEntry.getMaxlength() > 0)
          .entry("secret", textEntry.isSecret())
          .entry("originalType", type)
          .entry("indicatorPosition", indicatorPosition)
                .entry("labelPosition", labelPosition)
                .entry("immediate", textEntry.isImmediate());

        encodeClientBehaviors(context, textEntry, jb);

        if(!themeForms()) {
            jb.entry("theme", false);
        }

        jb.endMap().endArray().endFunction();
		String script = jb.toString();

        for (int i = 0; i < PASSTHROUGH_ATTRIBUTES.length; i++) {
            String name = PASSTHROUGH_ATTRIBUTES[i];
            if ("onfocus".equals(name)) {
                ComponentUtils.renderPassThroughAttribute(writer, textEntry, name, script);
            } else {
                ComponentUtils.renderPassThroughAttribute(writer, textEntry, name);
            }
        }
        ComponentUtils.renderExternalPassThroughAttributes(writer, textEntry);

        writer.endElement("input");

        writeLabelAndIndicatorAfter(labelAttributes);

        writer.endElement("span");

		writer.startElement("span", null);
        writer.writeAttribute("style", "display:none;", null);
		writer.writeAttribute("data-hashcode", script.hashCode(), null);
		writer.endElement("span");

        writer.endElement("span");
	}

	protected void renderResetSettings(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		TextEntry textEntry = (TextEntry) component;

		String clientId = textEntry.getClientId(context);
		String labelPosition = (String) component.getAttributes().get("labelPosition");

		String type = textEntry.validateType(textEntry.getType());
		ClientDescriptor client = Utils.getClientDescriptor();
		String typeVal = (String) textEntry.getAttributes().get("type");
		if (type.equals("date") && client.isAndroidOS() && client.isICEmobileContainer()) {
			typeVal = "text";
		}
		type = typeVal;

		JSONBuilder jb = JSONBuilder.create();
		jb.beginArray();
		jb.item("TextEntry");
		jb.beginArray();
		jb.item(clientId);
		jb.item(textEntry.isSecret());
		jb.item(type);

		if ("inField".equals(labelPosition)) {
			String label = (String) component.getAttributes().get("label");	
			String indicatorPosition = (String) component.getAttributes().get("indicatorPosition");
			String optionalIndicator = (String) component.getAttributes().get("optionalIndicator");
			String requiredIndicator = (String) component.getAttributes().get("requiredIndicator");
			if ("labelLeft".equals(indicatorPosition)) {
				if (textEntry.isRequired())
					label = requiredIndicator + label;
				else
					label = optionalIndicator + label;
			} else if ("labelRight".equals(indicatorPosition)) {
				if (textEntry.isRequired())
					label = label + requiredIndicator;
				else
					label = label + optionalIndicator;
			}
			jb.item(label);
		}

		jb.endArray();
		jb.endArray();

		writer.writeAttribute("data-ice-reset", jb.toString(), null);
	}
}
