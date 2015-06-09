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

package org.icefaces.ace.component.checkboxbutton;



import java.io.IOException;
import java.lang.String;
import java.util.*;


import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;

import org.icefaces.ace.component.buttongroup.ButtonGroup;
import org.icefaces.ace.util.*;

import org.icefaces.util.EnvUtils;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.ace.renderkit.InputRenderer;

@MandatoryResourceComponent(tagName="checkboxButton", value="org.icefaces.ace.component.checkboxbutton.CheckboxButton")
public class CheckboxButtonRenderer extends InputRenderer {
    private enum EventType {
        HOVER, FOCUS
    }

    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        Map requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
        CheckboxButton checkbox = (CheckboxButton) uiComponent;
        String clientId = uiComponent.getClientId();
        String hiddenValue = String.valueOf(requestParameterMap.get(clientId+"_hidden"));

        if (null==hiddenValue || hiddenValue.equals("null")){
            return;
        }else {
            boolean submittedValue = isChecked(hiddenValue);
            checkbox.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(facesContext, checkbox);
    }


    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
    throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        CheckboxButton checkbox = (CheckboxButton) uiComponent;
        String clientId = uiComponent.getClientId(facesContext);
		Map<String, Object> labelAttributes = getLabelAttributes(uiComponent);
        String firstWrapperClass = "yui-button yui-checkboxbutton-button ui-button ui-widget";
        String secondWrapperClass = "first-child";
        boolean ariaEnabled = EnvUtils.isAriaEnabled(facesContext);

        // Root Container
        writer.startElement(HTML.DIV_ELEM, uiComponent);
        writer.writeAttribute(HTML.ID_ATTR, clientId, null);
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

        encodeScript(facesContext, writer, checkbox, clientId, EventType.HOVER);
        encodeRootStyle(writer, checkbox);

		writeLabelAndIndicatorBefore(labelAttributes);

        // First Wrapper
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, firstWrapperClass, null);

        // Second Wrapper
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, secondWrapperClass, null);

        if (ariaEnabled)
            encodeAriaAttributes(writer, checkbox);

        // Button Element
        writer.startElement(HTML.BUTTON_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "button", null);
        String buttonId = clientId + "_button";
        writer.writeAttribute(HTML.ID_ATTR, buttonId, null);
        writer.writeAttribute(HTML.NAME_ATTR, buttonId, null);
		String selectedClass = "";
		Object value = checkbox.getValue();
		if (value != null) selectedClass = (((Boolean) value) ? "ice-checkboxbutton-checked" : "ice-checkboxbutton-unchecked");
		else selectedClass = "ice-checkboxbutton-unchecked";
		writer.writeAttribute(HTML.CLASS_ATTR, "ui-corner-all " + selectedClass, null);

        encodeButtonTabIndex(writer, checkbox, ariaEnabled);
        encodeButtonStyle(writer, checkbox);
        encodeScript(facesContext, writer, checkbox, clientId, EventType.FOCUS);

        renderPassThruAttributes(facesContext, checkbox, HTML.BUTTON_ATTRS, new String[]{"style"});

        if (checkbox.getLabel() != null && "inField".equalsIgnoreCase(checkbox.getLabelPosition())) {
            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, "ui-label", null);
            writer.write(checkbox.getLabel());
            writer.endElement(HTML.SPAN_ELEM);
        } else {
            writer.startElement(HTML.SPAN_ELEM, null);
            encodeIconStyle(writer, checkbox);
            writer.endElement(HTML.SPAN_ELEM);
        }

        writer.endElement(HTML.BUTTON_ELEM);
        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.SPAN_ELEM);

		writeLabelAndIndicatorAfter(labelAttributes);
    }

    private void encodeAriaAttributes(ResponseWriter writer, CheckboxButton checkbox) throws IOException {
        writer.writeAttribute(HTML.ROLE_ATTR, "checkbox", null);
        writer.writeAttribute(HTML.ARIA_DESCRIBED_BY_ATTR, checkbox.getLabel(), null);
        writer.writeAttribute(HTML.ARIA_DISABLED_ATTR, checkbox.isDisabled(), null);
    }

    private void encodeButtonTabIndex(ResponseWriter writer, CheckboxButton checkbox, boolean ariaEnabled) throws IOException {
        Integer tabindex = checkbox.getTabindex();

        if (ariaEnabled && tabindex == null)
            tabindex = 0;

        if (tabindex != null)
            writer.writeAttribute(HTML.TABINDEX_ATTR, tabindex, null);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
    throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = uiComponent.getClientId(facesContext);
        CheckboxButton checkbox = (CheckboxButton) uiComponent;
        Object val = checkbox.getValue();

        writer.startElement("input", uiComponent);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("name",clientId+"_hidden", null);
        writer.writeAttribute("value",val, null);
        writer.endElement("input");

		// register checkbox with group
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeText("ice.ace.checkboxbutton.register('"+clientId+"','"+getGroupId(facesContext, checkbox)+"');", null);
		writer.endElement("script");

        writer.endElement(HTML.DIV_ELEM);
		
		Utils.registerLazyComponent(facesContext, clientId, getScript(facesContext, writer, checkbox, clientId));
    }

    private String getScript(FacesContext facesContext, ResponseWriter writer,
                              CheckboxButton checkbox, String clientId) throws IOException {
        String groupId = getGroupId(facesContext, checkbox);

        boolean ariaEnabled = EnvUtils.isAriaEnabled(facesContext);
        JSONBuilder jb = JSONBuilder.create();
        List<UIParameter> uiParamChildren = Utils.captureParameters(checkbox);

        jb.beginFunction("ice.ace.lazy")
          .item("checkboxbutton")
          .beginArray()
          .item(clientId)
          .beginMap()
          .entry("groupId", groupId)
          .entry("ariaEnabled", ariaEnabled);

        if (checkbox.isDisabled())
            jb.entry("disabled", true);

        if (uiParamChildren != null) {
            jb.beginMap("uiParams");
            for (UIParameter p : uiParamChildren)
                jb.entry(p.getName(), (String)p.getValue());
            jb.endMap();
        }

        encodeClientBehaviors(facesContext, checkbox, jb);

        jb.endMap().endArray().endFunction();
		
		return jb.toString();
	}

	private String getGroupId(FacesContext facesContext, CheckboxButton checkbox) {
        UIComponent groupComp;
        String groupId = checkbox.getGroup();
		if (groupId != null) {
			groupId = groupId.trim();
			groupComp = checkbox.findComponent(groupId);
			groupId = groupComp instanceof ButtonGroup ? groupComp.getClientId(facesContext) : "";
		}
		if (groupId == null || "".equals(groupId)) {
			groupComp = findNearestButtonGroup(checkbox);
			if (groupComp != null) {
				groupId = ((ButtonGroup) groupComp).isMutuallyExclusive() ? groupComp.getClientId(facesContext) : "";
			} else {
				groupId = "";
			}
		}
		return groupId;
	}

	private ButtonGroup findNearestButtonGroup(UIComponent component) {
		if (component == null) return null;
		UIComponent parent = component.getParent();
		if (parent == null) return null;
		if (parent instanceof ButtonGroup) return (ButtonGroup) parent;
		return findNearestButtonGroup(parent);
	}
	
    private void encodeScript(FacesContext facesContext, ResponseWriter writer,
                              CheckboxButton checkbox, String clientId, EventType type) throws IOException {

        String eventType = "";
        if (EventType.HOVER.equals(type))
            eventType = HTML.ONMOUSEOVER_ATTR;
        else if (EventType.FOCUS.equals(type))
            eventType = HTML.ONFOCUS_ATTR;

        writer.writeAttribute(eventType, "if (!document.getElementById('" + clientId + "').widget) "+ getScript(facesContext, writer, checkbox, clientId), null);
    }

    /**
     * support similar return values as jsf component
     * so can use strings true/false, on/off, yes/no to
     * support older browsers
     * @param hiddenValue
     * @return
     */
    private boolean isChecked(String hiddenValue) {
        return hiddenValue.equalsIgnoreCase("true") ||
               hiddenValue.equalsIgnoreCase("on") ||
               hiddenValue.equalsIgnoreCase("yes");
    }

    //forced converter support. It's either a boolean or string.
    @Override
    public Object getConvertedValue(FacesContext facesContext, UIComponent uiComponent,
                                    Object submittedValue) throws ConverterException{
        if (submittedValue instanceof Boolean) {
            return submittedValue;
        }
        else {
            return Boolean.valueOf(submittedValue.toString());
        }
    }

    private void encodeButtonStyle(ResponseWriter writer, CheckboxButton checkbox) throws IOException {
        String buttonClasses = "";
        String disabledClass = "ui-state-disabled";
        Boolean val = (Boolean)checkbox.getValue();

        if (checkbox.isDisabled()) {
            buttonClasses += disabledClass + " ";
        }

        if (!buttonClasses.equals("")) {
            writer.writeAttribute(HTML.CLASS_ATTR, buttonClasses.trim(), null);
        }
    }

    private void encodeIconStyle(ResponseWriter writer, CheckboxButton checkbox) throws IOException {
        String iconClass = "fa";
        String selectedStyle = "fa-check-square-o";
        String unselectedStyle = "fa-square-o";
		String largeStyle = "fa-lg";
        Boolean val = (Boolean)checkbox.getValue();

        if (val != null && val) {
            iconClass += " " + selectedStyle + " " + largeStyle;
        } else {
            iconClass += " " + unselectedStyle + " " + largeStyle;
        };

        writer.writeAttribute(HTML.CLASS_ATTR, iconClass, null);
    }

    private void encodeRootStyle(ResponseWriter writer, CheckboxButton checkbox) throws IOException {
        String styleClass = checkbox.getStyleClass();
        String styleClassVal = "ice-checkboxbutton";
        String style = checkbox.getStyle();

        if (styleClass != null && styleClass.trim().length() > 0)
            styleClassVal += " " + styleClass;

        if (style != null && style.trim().length() > 0)
            writer.writeAttribute(HTML.STYLE_ATTR, style, HTML.STYLE_ATTR);

        writer.writeAttribute(HTML.CLASS_ATTR, styleClassVal, null);
    }
}
