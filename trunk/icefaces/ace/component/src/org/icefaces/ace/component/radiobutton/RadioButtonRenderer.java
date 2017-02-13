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

package org.icefaces.ace.component.radiobutton;



import org.icefaces.component.PassthroughAttributes;
import org.icefaces.ace.renderkit.InputRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.ace.util.Utils;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.util.EnvUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.icefaces.ace.api.ButtonGroupMember;
import org.icefaces.util.JavaScriptRunner;

@MandatoryResourceComponent(tagName="radioButton", value="org.icefaces.ace.component.radiobutton.RadioButton")
public class RadioButtonRenderer extends InputRenderer {
    protected enum EventType {
        HOVER, FOCUS
    }
    private final static String[] PASSTHROUGH_ATTRIBUTES = ((PassthroughAttributes) RadioButton.class.getAnnotation(PassthroughAttributes.class)).value();

    private static final Logger logger =
            Logger.getLogger(RadioButtonRenderer.class.toString());

    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        Map requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
        RadioButton radioButton = (RadioButton) uiComponent;
        String clientId = uiComponent.getClientId();
        String hiddenValue = String.valueOf(requestParameterMap.get(clientId+"_hidden"));

        if (null==hiddenValue || hiddenValue.equals("null")){
            return;
        }else {
            boolean submittedValue = isChecked(hiddenValue);
            radioButton.setSubmittedValue(submittedValue);
        }

        decodeBehaviors(facesContext, radioButton);
    }


    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
    throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        RadioButton radioButton = (RadioButton) uiComponent;
        String clientId = uiComponent.getClientId(facesContext);
		Map<String, Object> labelAttributes = getLabelAttributes(uiComponent);
		labelAttributes.put("fieldClientId", clientId + "_button");
        String firstWrapperClass = "ice-ace-radiobutton-main";
        boolean ariaEnabled = EnvUtils.isAriaEnabled(facesContext);

        // Root Container
        writer.startElement(HTML.DIV_ELEM, uiComponent);
        writer.writeAttribute(HTML.ID_ATTR, clientId, null);
        encodeScript(writer, EventType.HOVER);
        renderResetSettings(facesContext, clientId);
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);
        boolean disabled = radioButton.isDisabled();
        encodeRootStyle(writer, radioButton);
        String script = getScript(facesContext, radioButton, clientId);
        writer.writeAttribute("data-init", "if (!document.getElementById('" + clientId + "').widget) " + script, null);

        writeLabelAndIndicatorBefore(labelAttributes);
        encodeButtonWrappers(writer, firstWrapperClass);

        if (ariaEnabled) {
            encodeAriaEnabled(writer, disabled);
            writer.writeAttribute(HTML.ARIA_DESCRIBED_BY_ATTR, radioButton.getLabel(), null);
        }

        // Button Element
        encodeButtonElementStart(writer, clientId);
        /* not same */
		if (labelAttributes.get("label") != null
			&& !"inField".equals(labelAttributes.get("labelPosition"))) {
			writer.writeAttribute("aria-labelledby", "label_" + clientId, null);
		}

		String selectedClass = "";
		Object value = radioButton.getValue();
		if (value != null && ((Boolean) value)) selectedClass = " ice-ace-radiobutton-selected";
		writer.writeAttribute(HTML.CLASS_ATTR, "ui-corner-all ui-widget-content" + selectedClass, null);

        encodeButtonTabIndex(writer, radioButton, ariaEnabled);
        encodeButtonStyle(writer, radioButton.isDisabled());
        String style = radioButton.getStyle();
        if (style != null && style.trim().length() > 0) {
            writer.writeAttribute(HTML.STYLE_ATTR, style, HTML.STYLE_ATTR);
        }

        encodeScript(writer, EventType.FOCUS);

        renderPassThruAttributes(facesContext, radioButton, PASSTHROUGH_ATTRIBUTES);
 
		writer.startElement(HTML.SPAN_ELEM, null);
		encodeIconStyle(writer, (Boolean)value);
		writer.endElement(HTML.SPAN_ELEM);

        writer.endElement(HTML.BUTTON_ELEM);
        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.SPAN_ELEM);
		
		writeLabelAndIndicatorAfter(labelAttributes);
        writer.startElement("input", uiComponent);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("name",clientId+"_hidden", null);
        writer.writeAttribute("value",value, null);
        writer.writeAttribute("data-ice-clear-ignore", "true", null);
        writer.endElement("input");

        writer.endElement(HTML.DIV_ELEM);
        JavaScriptRunner.runScript(facesContext, "ice.ace.radiobutton.register('"+clientId+"','"+getGroupId(facesContext, radioButton)+"');");
        JavaScriptRunner.runScript(facesContext, "ice.ace.registerLazyComponent('" + clientId + "');");
    }

    private String getGroupId(FacesContext facesContext, RadioButton radioButton) {
        String groupId = radioButton.getGroup();
        List<String> groupLookInCtx = ComponentUtils.findInFacesContext(radioButton, facesContext);
        if (!groupLookInCtx.isEmpty()){  //at least one buttonGroup is in the view
            if (groupId !=null){
                groupId = groupId.trim();
                if (groupLookInCtx.contains(groupId)) {
                    for(String sid: groupLookInCtx){
                        if (sid.toLowerCase().contains(groupId.toLowerCase())){
                            groupId=sid;
                        }
                    }
                }  else {
                    //does it end in the groupId --so incomplete?
                    groupId= ComponentUtils.findInHeirarchy((ButtonGroupMember)radioButton, facesContext);
                }
            } else { //have at least one buttonGroup, but groupId is not set
                groupId= ComponentUtils.findInHeirarchy((ButtonGroupMember)radioButton, facesContext);
                if (groupId.length()< 1){
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("groupId of:-"+groupId+" not found in view.");
                    }
                }
            }
        }else {  //no buttonGroups in the view buttons are just non-managed buttons
            groupId="";
        }

        return groupId;
    }


    protected void encodeScript(ResponseWriter writer, RadioButtonRenderer.EventType type) throws IOException {
        String eventType = "";
        if (RadioButtonRenderer.EventType.HOVER.equals(type))
            eventType = HTML.ONMOUSEOVER_ATTR;
        else if (RadioButtonRenderer.EventType.FOCUS.equals(type))
            eventType = HTML.ONFOCUS_ATTR;

        writer.writeAttribute(eventType, "ice.ace.evalInit(this);", null);
    }


    protected void encodeButtonWrappers(ResponseWriter writer, String firstWrapperClass) throws IOException {
        // First Wrapper
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, firstWrapperClass, null);

        // Second Wrapper
        writer.startElement(HTML.SPAN_ELEM, null);
    }

    protected void encodeButtonElementStart(ResponseWriter writer, String clientId) throws IOException {
        writer.startElement(HTML.BUTTON_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "button", null);
        writer.writeAttribute(HTML.ID_ATTR, clientId+"_button", null);
        writer.writeAttribute(HTML.NAME_ATTR, clientId+"_button", null);
    }

    protected void encodeAriaEnabled(ResponseWriter writer, boolean disabled) throws IOException {
   		writer.writeAttribute(HTML.ROLE_ATTR, "radio", null);
   		writer.writeAttribute(HTML.ARIA_DISABLED_ATTR, disabled, null);
   	}

    private void encodeButtonTabIndex(ResponseWriter writer, RadioButton radioButton, boolean ariaEnabled) throws IOException {
        Integer tabindex = radioButton.getTabindex();

        if (ariaEnabled && tabindex == null)
            tabindex = 0;

        if (tabindex != null)
            writer.writeAttribute(HTML.TABINDEX_ATTR, tabindex, null);
    }


    private String getScript(FacesContext facesContext, RadioButton radioButton, String clientId) throws IOException {
        String groupId = radioButton.getGroup();
        List<String> groupLookInCtx = ComponentUtils.findInFacesContext(radioButton, facesContext);
        if (!groupLookInCtx.isEmpty()){  //at least one buttonGroup is in the view
            if (groupId !=null){
                groupId = groupId.trim();
                if (groupLookInCtx.contains(groupId)) {
                    for(String sid: groupLookInCtx){
                       if (sid.toLowerCase().equals(groupId.toLowerCase())){
                           groupId=sid;
                       }
                    }
                }  else {
                    //does it end in the groupId --so incomplete?
                   groupId= ComponentUtils.findInHeirarchy((ButtonGroupMember)radioButton, facesContext);
                }
            } else { //have at least one buttonGroup, but groupId is not set
                groupId= ComponentUtils.findInHeirarchy((ButtonGroupMember)radioButton, facesContext);
                if (groupId.length()< 1){
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("groupId of:-"+groupId+" not found in view.");
                    }
                }
            }
        }else {  //no buttonGroups in the view buttons are just non-managed buttons
            groupId="";
        }

        boolean ariaEnabled = EnvUtils.isAriaEnabled(facesContext);
        JSONBuilder jb = JSONBuilder.create();
        List<UIParameter> uiParamChildren = Utils.captureParameters(radioButton);

        jb.beginFunction("ice.ace.lazy")
          .item("radiobutton")
          .beginArray()
          .item(clientId)
          .beginMap()
          .entry("groupId", groupId)
          .entry("ariaEnabled", ariaEnabled);

        if (radioButton.isDisabled())
            jb.entry("disabled", true);

        if (uiParamChildren != null) {
            jb.beginMap("uiParams");
            for (UIParameter p : uiParamChildren)
                jb.entry(p.getName(), (String)p.getValue());
            jb.endMap();
        }

        encodeClientBehaviors(facesContext, radioButton, jb);

        jb.endMap().endArray().endFunction();

        return jb.toString();
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

    protected void encodeButtonStyle(ResponseWriter writer, boolean disabled) throws IOException {
        String buttonClasses = "";
        String disabledClass = "ui-state-disabled";

        if (disabled) {
            buttonClasses += disabledClass + " ";
        }

        if (!buttonClasses.equals("")) {
            writer.writeAttribute(HTML.CLASS_ATTR, buttonClasses.trim(), null);
        }

    }

    protected void encodeIconStyle(ResponseWriter writer, Boolean value) throws IOException {
        String iconClass = "fa";
        String selectedStyle = "fa-dot-circle-o";
        String unselectedStyle = "fa-circle-o";
		String largeStyle = "fa-lg";

        if (value != null && value) {
            iconClass += " " + selectedStyle + " " + largeStyle;
        } else {
            iconClass += " " + unselectedStyle + " " + largeStyle;
        };

        writer.writeAttribute(HTML.CLASS_ATTR, iconClass, null);
    }

    private void encodeRootStyle(ResponseWriter writer, RadioButton radioButton) throws IOException {
        String styleClass = radioButton.getStyleClass();
        String styleClassVal = "ice-ace-radiobutton";

        if (styleClass != null && styleClass.trim().length() > 0)
            styleClassVal += " " + styleClass;

        writer.writeAttribute(HTML.CLASS_ATTR, styleClassVal, null);
    }

	protected void renderResetSettings(FacesContext context, String clientId) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		JSONBuilder jb = JSONBuilder.create();
		jb.beginArray();
		jb.item("radiobutton");
		jb.beginArray();
		jb.item(clientId);
		jb.item(EnvUtils.isAriaEnabled(context));
		jb.endArray();
		jb.endArray();

		writer.writeAttribute("data-ice-reset", jb.toString(), null);
	}
}
