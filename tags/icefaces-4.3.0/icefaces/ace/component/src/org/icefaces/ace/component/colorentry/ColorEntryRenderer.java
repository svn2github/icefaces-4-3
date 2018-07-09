/*
 * Copyright 2004-2016 ICEsoft Technologies Canada Corp.
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
package org.icefaces.ace.component.colorentry;


import org.icefaces.ace.renderkit.InputRenderer;
import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.util.EnvUtils;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.model.colorEntry.ColorEntryLayout;
import org.icefaces.ace.model.colorEntry.SwatchEntry;
import org.icefaces.ace.renderkit.InlineScriptEventListener;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


@MandatoryResourceComponent(tagName="colorEntry", value="org.icefaces.ace.component.colorentry.ColorEntry")
public class ColorEntryRenderer extends InputRenderer {
    private static final String ACE_MESSAGES_BUNDLE = "org.icefaces.ace.resources.messages";
    private static final String COLOR_ENTRY_LOOKUP_KEY = "org.icefaces.ace.component.colorentry.regional.";
    @Override
     public void decode(FacesContext facesContext, UIComponent component) {
         ColorEntry picker = (ColorEntry) component;
         String clientId = picker.getClientId(facesContext);
         Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
         if(picker.isDisabled() || picker.isReadonly()) {
                 return;
 		 }
         boolean popup = picker.isRenderAsPopup();
         String submittedValue = params.get(clientId + "_input");
         String hidden = params.get(clientId+"_hidden");
         if (picker.getColorFormat()!=null){
             String preferredFormat = picker.getColorFormat().getValue();
             if (!isValueEmpty(preferredFormat) && preferredFormat.startsWith("HSL")) {
                String hexVal = params.get(clientId + "_hiddenHex");
                if (!isValueBlank(hexVal)) {
                    picker.setHexVal(hexVal);
                }
            }
         }
         if (isValueBlank(hidden))hidden= params.get(clientId+"_hidden2");
         if (!popup){
           //   is inline so get value from hidden input field"+hidden2
             submittedValue= hidden;
         }
         picker.setSubmittedValue(submittedValue);
         decodeBehaviors(facesContext, component);
     }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ColorEntry picker = (ColorEntry) component;
        ResponseWriter writer = context.getResponseWriter();
        String lookupLocale=null;
        Locale mylocale = context.getCurrentInstance().getViewRoot().getLocale();
        String localeScript=null;
        String swatchName = (picker.getSwatchesName() !=null)? picker.getSwatchesName() : null;
        if (swatchName !=null && picker.getSwatches() !=null){
            encodeSwatch(writer, swatchName, picker.getSwatches(), picker.getClientId(), component, context);
        }
        if (picker.getLocale() !=null) {
            //first see if the locale is a string and if it is already included in plugin
            if (picker.getLocale() instanceof String) {
                String lKey = (String)(picker.getLocale());
                if (lKey.trim().matches("fr|el|en|de|en-GB|en-US|nl|pt-br|ru|sr")) {
                    lookupLocale = lKey;
                }  else{
                    lookupLocale=(String)(picker.getLocale());
                    localeScript = createRegionalScript(writer, context, lookupLocale);
                }
            }else if (picker.getLocale() instanceof java.util.Locale){
                mylocale = (Locale)picker.getLocale();
                if (mylocale !=null){
                    lookupLocale = mylocale.getLanguage();
                    if (mylocale.getVariant()!=null ) {
                        lookupLocale+= "-"+mylocale.getVariant();
                    }
                    String check = mylocale.toString();
                    localeScript = createRegionalScript(writer, context, lookupLocale);
                }
            }  else {
                throw new IllegalArgumentException("Type:" +  mylocale.getClass()+ " is not a valid locale type for component id=" + component.getClientId(context));
            }

        }
        String clientId = picker.getClientId(context);
        String valueToRender = getValueAsString(context,picker);
        boolean popup = picker.isRenderAsPopup();
        String showOn = picker.getShowOn();   //default is focus for use of popup on input field
        boolean customParts=false;
        List<String> partsList = picker.getCustomParts();
        if (partsList !=null  && partsList.size()> 0) {
           customParts = true;
        }
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        String iceFocus = (String) paramMap.get("ice.focus");
        boolean ariaEnabled = EnvUtils.isAriaEnabled(context);
        String buttonText = null;
        if (!isValueEmpty(picker.getButtonText())){
            buttonText = picker.getButtonText();
        }
        boolean isPopupIconOnly = picker.isPopupIconOnly();
        boolean useButton =
                isPopupIconOnly || buttonText!=null || picker.getPopupIcon() !=null;
        String type = "text";
        String inputStyleClass = ColorEntry.INPUT_STYLE_CLASS;
        String preferredFormat = picker.getColorFormat().HEX.getValue();
        if (picker.getColorFormat()!=null){
            preferredFormat = picker.getColorFormat().getValue();
        }
        String iconSrc = null;
        String styleClasses = "";
        Map<String, Object> labelAttributes = getLabelAttributes(component);
        labelAttributes.put("fieldClientId", clientId + "_input");
        if (popup){
            if (!useButton){
                /* style for popup on input field */
                if (isValueBlank(valueToRender))  {
                    inputStyleClass = ColorEntry.INPUT_EMPTY_STYLE_CLASS;
                }
            } else {
                /* style for popup on buttons */
             //   inputStyleClass = ColorEntry.BUTTON_STYLE_CLASS;
                type="hidden";

                if (!isValueEmpty(picker.getPopupIcon())  ){
                    iconSrc = getResourceURL(context, picker.getPopupIcon()) ;
                }  else {
                    iconSrc = getResourceRequestPath(context, ColorEntry.POPUP_ICON);
                }
                if (showOn.equals("focus")){
                    showOn="button click"; //force the button to not fire on focus.
                }
            }
            styleClasses = (themeForms() ? inputStyleClass : "");
            /* only popup has label abilities */
            writeLabelAndIndicatorBefore(labelAttributes);
        }
        /* common writes for both inline and popup */
        writer.startElement(HTML.SPAN_ELEM, component);
        writer.writeAttribute("id", clientId, "clientId");
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);
        String style = picker.getStyle();
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }
        String styleClass = picker.getStyleClass();
        if(styleClass != null){
            writer.writeAttribute("class", styleClass, null);
        }
        String tabindex = picker.getTabindex();
        //inline container
        if(!popup) {
            writer.startElement("span", null);
            writer.writeAttribute("id", clientId+"_input", null);
            renderResetSettings(context, component);
            writer.writeAttribute("style", "display:inline-block", null);
            commonAttributes(picker, writer, tabindex);
            if (ariaEnabled) {
                writer.writeAttribute("role", "region", null);
                writeCommonAriaAttribs((ColorEntry) component, labelAttributes);
            }
            writer.endElement("span");
        }
        if (popup){
            writer.startElement("input", null);
            String borderColor = "#f2eaea";
            if (valueToRender!=null && valueToRender.length()>0) {
                borderColor=valueToRender ;
                if (preferredFormat.startsWith("HEX")){
                    if (!borderColor.startsWith("#")){
                        borderColor="#"+borderColor;
                    }
                }
                if (preferredFormat.startsWith("HSL") && !isValueEmpty(picker.getHexVal())){
                     borderColor= picker.getHexVal();
                }
                String popupStyleBorder = "border-left-color: "+borderColor+" !important;";
                writer.writeAttribute("style", popupStyleBorder, null);
            }
            writer.writeAttribute("id", clientId+"_input", null);
            writer.writeAttribute("name", clientId+"_input", null);
            renderResetSettings(context, component);
            writer.writeAttribute("type", type, null);
            commonAttributes(picker, writer, tabindex);

            if(!isValueBlank(valueToRender)) {
                writer.writeAttribute("value", valueToRender, null);
            } else if (popup && !useButton && !clientId.equals(iceFocus)) {
                String inFieldLabel = (String) labelAttributes.get("inFieldLabel");
                if (!isValueBlank(inFieldLabel)) {
                    writer.writeAttribute("name", clientId + "_label", null);
                    writer.writeAttribute("value", inFieldLabel, null);
                    styleClasses += " " + IN_FIELD_LABEL_STYLE_CLASS;
                    labelAttributes.put("labelIsInField", true);
                }
                if (ariaEnabled) {
                   writer.writeAttribute("role", "textbox", null);
                }
            }
            writer.writeAttribute("data-ice-clear-ignore", "true", null);

            if (ariaEnabled) {
                writeCommonAriaAttribs((ColorEntry) component, labelAttributes);
            }

            if(picker.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
            if(picker.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");

            if(!isValueBlank(styleClasses)) {
                writer.writeAttribute("class", styleClasses, null);
            }
            writer.endElement("input");
            writeLabelAndIndicatorAfter(labelAttributes);
        }

        JSONBuilder jb = JSONBuilder.create();
        jb.beginFunction("ice.ace.create")
                .item("ColorEntryInit")
                .beginArray()
                .beginMap()
                .beginMap("options")
                .entry("id", clientId)
                .entry("colorFormat", preferredFormat)
                .entry("disabled", picker.isDisabled());
        if (valueToRender!=null && valueToRender.length()>0){
            jb.entry("color", valueToRender) ;
        }
        jb.entry("draggable", false);
        if (lookupLocale!=null){
            jb.entry("regional", lookupLocale);
        }
        if (picker.getSwatchesWidth() > 0){
            jb.entry("swatchesWidth", picker.getSwatchesWidth());
        }
        if (swatchName!=null && swatchName.length()>0){
            jb.entry("swatches", swatchName.toLowerCase());
        }
        if (picker.getTitle()!=null && picker.getTitle().length()>0){
            jb.entry("title", picker.getTitle());
        }
        if (showOn !=null) {
            jb.entry("showOn", showOn);
        }
        if (customParts) {  /* custom layout */
            jb.beginArray("parts");
            for (String entry: partsList){
                jb.item(entry);
            }
            jb.endArray();
            List<ColorEntryLayout> customLayout =picker.getCustomLayout();
            if (customLayout!=null && customLayout.size()> 0){
                jb.beginMap("layout");
                for (ColorEntryLayout layout: customLayout){
                    jb.beginArray(layout.getPart());
                    jb.item(layout.getColumn());
                    jb.item(layout.getRow());
                    jb.item(layout.getColspan());
                    jb.item(layout.getRowspan());
                    jb.endArray();
                }
                jb.endMap();
            }
        }

        if (popup){
            jb.entry("alpha", picker.isShowAlpha());
            if (iconSrc !=null) {
                jb.entry("buttonImage", iconSrc);
            }
            if (buttonText !=null){
                jb.entry("buttonText", buttonText);
            }
            if (isPopupIconOnly)jb.entry("buttonImageOnly", isPopupIconOnly);
            jb.entry("showCloseButton", picker.isShowCloseButton());
            jb.entry("showCancelButton", picker.isShowCancelButton());
            jb.entry("buttonColorize", picker.isButtonColorize());
            jb.entry("showNoneButton", picker.isShowNoneButton());

            if (picker.getEffect() !=null && picker.getEffect().length()> 0) {
                 jb.entry("showAnim", picker.getEffect()) ;
                String duration = picker.getEffectDuration();
                if (duration !=null && duration.length()>0){
                    jb.entry("duration", duration);
                }
            }
        } else{              //default inline
            jb.entry("inline", true);
            jb.entry("inlineForm", true);
        }

        if (picker.getLimitSelection()!=null ){
            jb.entry("limit", picker.getLimitSelection());
        }
        if (!picker.isRequired()){
            jb.entry("allowEmpty", "true");
        }
        jb.endMap();

        encodeClientBehaviors(context, picker, jb);
        jb.endMap().endArray().endFunction();
        String script = jb.toString();
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId+"_script", null);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        if (localeScript !=null){
            //check to see if the script for this locale is already loaded
            writer.write(localeScript.toString());
        }
        writer.write(script);

        writer.endElement("script");
        writer.endElement("span");

        writer.startElement("span", component);
        writer.writeAttribute("style", "display:none;", null);
        writer.writeAttribute("data-hashcode", script.hashCode(), null);
        writer.endElement("span");

        if (!popup) {
            createHiddenField(writer, clientId + "_hidden2", valueToRender);

        }
        if (preferredFormat.startsWith("HSL")){
            createHiddenField(writer, clientId+"_hiddenHex", null) ;
        }
        writer.endElement(HTML.SPAN_ELEM);

    }

    private void writeCommonAriaAttribs(ColorEntry component, Map<String, Object> labelAttributes) throws IOException {
        final ColorEntry comp = component;
        Map<String, Object> ariaAttributes = new HashMap<String, Object>() {{
            put("readonly", comp.isReadonly());
            put("required", comp.isRequired());
            put("disabled", comp.isDisabled());
            put("invalid", !comp.isValid());
        }};
        writeAriaAttributes(ariaAttributes, labelAttributes);
    }

    private void commonAttributes(ColorEntry picker, ResponseWriter writer, String tabindex) throws IOException {
        if (tabindex != null)
            writer.writeAttribute("tabindex", tabindex, null);
        String accesskey = picker.getAccesskey();
        if (accesskey != null) {
            writer.writeAttribute("accesskey", accesskey, null);
            if (tabindex == null) writer.writeAttribute("tabindex", "0", null);
        }
    }

    private void encodeSwatch(ResponseWriter writer, String swatchName, List<SwatchEntry> swatches, String clientId,
                              UIComponent component, FacesContext context) throws IOException{
        //idea is to write this before the element div so that Domdiff only updates when necessary
        writer.startElement(HTML.DIV_ELEM, component);
        writer.writeAttribute(HTML.ID_ATTR, component.getClientId(context) + "_swatch", null);
        writer.startElement(HTML.SCRIPT_ELEM, component);
        writer.writeAttribute("type", "text/javascript", null);
        StringBuilder scriptA = new StringBuilder("ice.ace.jq.colorpicker.swatchesNames['");
        scriptA.append(swatchName.toLowerCase()).append("']='").append(swatchName).append("';");
        StringBuilder scriptB =  new StringBuilder("ice.ace.jq.colorpicker.swatches['");
        scriptB.append(swatchName.toLowerCase());
        scriptB.append("']=[") ;
        for (int i=0; i<swatches.size(); i++) {
            SwatchEntry se = (SwatchEntry)swatches.get(i);
            if (i!=0)scriptB.append(",");
           scriptB.append(se.getWrittenEntry()) ;
        }
        scriptB.append("]") ;
        String finalScript = scriptA.toString()+scriptB.toString()+";";
        writer.write(finalScript);
        writer.endElement(HTML.SCRIPT_ELEM);
        writer.endElement(HTML.DIV_ELEM) ;
    }

    private String createRegionalScript(ResponseWriter writer, FacesContext context, String lookupLocale){
        String[] lookupRegVals = new String[]{"ok","cancel", "none", "button", "title","transparent","hsvH","hsvS","hsvV","rgbR","rgbG","rgbB",
                   "labL","labA","labB","hslH","hslS","hslL","cmykC","cmykM","cmykK","cmykC","alphaA"};
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String bundleName = context.getApplication().getMessageBundle()+"_"+lookupLocale;
        if (classLoader == null) classLoader = bundleName.getClass().getClassLoader();
        if (bundleName == null) bundleName = ACE_MESSAGES_BUNDLE;
        final ResourceBundle bundle = ComponentUtils.getComponentResourceBundle(context, bundleName);
        //create js object to be written to page ahead of init script
        if (bundle == null){
            Locale browserLocale = context.getCurrentInstance().getViewRoot().getLocale();
            lookupLocale = browserLocale.toString();
        }
        StringBuilder scriptB =  new StringBuilder("ice.ace.jq.colorpicker.regional['");
        scriptB.append(lookupLocale);
        scriptB.append("']=") ;
        JSONBuilder regionalScript =  JSONBuilder.create();
        regionalScript.beginMap();
        for (int i=0; i<lookupRegVals.length; i++){
            String key = lookupRegVals[i];
            regionalScript.entry(key,ComponentUtils.getLocalisedMessageFromBundle(bundle,COLOR_ENTRY_LOOKUP_KEY, key,key));
        }
        regionalScript.endMap();
        scriptB.append(regionalScript.toString()).append(";");
        return scriptB.toString();
    }

    private void createHiddenField(ResponseWriter writer, String id, String value) throws IOException {
        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", id, null);
        if (value!=null){
            writer.writeAttribute("value", value, null);
        }
        writer.writeAttribute("name", id, null);
        writer.endElement("input");
    }

    protected void renderResetSettings(FacesContext context, UIComponent component) throws IOException {
    		ResponseWriter writer = context.getResponseWriter();
    		ColorEntry picker = (ColorEntry) component;

    		String clientId = picker.getClientId(context);

    		JSONBuilder jb = JSONBuilder.create();
    		jb.beginArray();
    		jb.item("ColorEntry");
    		jb.beginArray();
    		jb.item(clientId);

    		jb.endArray();
    		jb.endArray();

    		writer.writeAttribute("data-ice-reset", jb.toString(), null);
    }
    protected String getValueAsString(FacesContext context,ColorEntry picker){
        Object submittedValue = picker.getSubmittedValue();
       		if(submittedValue != null) {
       			return submittedValue.toString();
       		}

       		Object value = picker.getValue();
       		if(value == null) {
       			return null;
       		} else {
       			//first ask the converter
       			if(picker.getConverter() != null) {
       				return picker.getConverter().getAsString(context, picker, value);
       			}else {
                    return String.valueOf(value);
                }

       		}
    }
}
