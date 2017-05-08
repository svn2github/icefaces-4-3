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

package org.icefaces.ace.component.autocompleteentry;

import org.icefaces.ace.event.TextChangeEvent;
import org.icefaces.ace.renderkit.InputRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.component.PassthroughAttributes;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.util.EnvUtils;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@MandatoryResourceComponent(tagName = "autoCompleteEntry", value = "org.icefaces.ace.component.autocompleteentry.AutoCompleteEntry")
public class AutoCompleteEntryRenderer extends InputRenderer {
    private final static String[] PASSTHROUGH_ATTRIBUTES = ((PassthroughAttributes) AutoCompleteEntry.class.getAnnotation(PassthroughAttributes.class)).value();
    private final static String AUTOCOMPLETE_DIV = "_div";

    public static boolean isHardSubmit(FacesContext facesContext, UIComponent component) {
        Map requestMap = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = component.getClientId(facesContext);
        KeyEvent keyEvent = new KeyEvent(component, requestMap);

        return (keyEvent.getKeyCode() == KeyEvent.CARRIAGE_RETURN ||
                "true".equals(requestMap.get("ice.event.left")) ||
                "onclick".equals(requestMap.get("ice.event.type")) ||
                requestMap.containsKey(clientId + "_hardSubmit"));
    }

    private static String escapeSingleQuote(String text) {
        if (null == text) {
            return "";
        }
        char[] chars = text.toCharArray();
        StringBuilder buffer = new StringBuilder(chars.length);
        for (int index = 0; index < chars.length; index++) {
            char ch = chars[index];
            if (ch == '\'') {
                buffer.append("&#39;");
            } else {
                buffer.append(ch);
            }
        }

        return buffer.toString();
    }

    private static String escapeJavascriptString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\'", "\\'");
    }

    // taken from com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer
    public static void encodeParentAndChildren(FacesContext facesContext, UIComponent parent) throws IOException {
        parent.encodeBegin(facesContext);
        if (parent.getRendersChildren()) {
            parent.encodeChildren(facesContext);
        } else {
            if (parent.getChildCount() > 0) {
                Iterator children = parent.getChildren().iterator();
                while (children.hasNext()) {
                    UIComponent nextChild = (UIComponent) children.next();
                    if (nextChild.isRendered()) {
                        encodeParentAndChildren(facesContext, nextChild);
                    }
                }
            }
        }
        parent.encodeEnd(facesContext);
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        AutoCompleteEntry autoCompleteEntry = (AutoCompleteEntry) uiComponent;
        autoCompleteEntry.setPopulateList(false);
        autoCompleteEntry.setItemList(null);
        Map requestMap = facesContext.getExternalContext().getRequestParameterMap();
        KeyEvent keyEvent = new KeyEvent(autoCompleteEntry, requestMap);
        String clientId = autoCompleteEntry.getClientId(facesContext);
        String text = (String) requestMap.get(clientId + "_input");
        String oldText = (String) autoCompleteEntry.getText();
        boolean textChanged = false;

        autoCompleteEntry.setSubmittedValue(text);
        if (text != null) {
            if (autoCompleteEntry.isCaseSensitive()) {
                if (!text.equals(oldText)) {
                    autoCompleteEntry.setPopulateList(true);
                    textChanged = true;
                }
            } else {
                if (!text.equalsIgnoreCase(oldText)) {
                    autoCompleteEntry.setPopulateList(true);
                    textChanged = true;
                }
            }
            if ("".equals(text) && oldText == null) {
                autoCompleteEntry.setPopulateList(false);
            }
            autoCompleteEntry.setText(text);
        }

        boolean isEventSource = false;
        Object sourceId = requestMap.get("ice.event.captured");
        if (sourceId != null && (sourceId.toString().equals(clientId) || sourceId.toString().equals(clientId + "_input"))) {
            isEventSource = true;
        }
        if (isEventSource) {
            if (isHardSubmit(facesContext, autoCompleteEntry)) {
                autoCompleteEntry.setPopulateList(false);
                autoCompleteEntry.setSubmittedValue(text);
                autoCompleteEntry.setSubmittedText(text);
                if (text != null) {
                    if (autoCompleteEntry.isCaseSensitive()) {
                        if (text.equals(oldText)) {
                            autoCompleteEntry.queueEvent(new ValueChangeEvent(autoCompleteEntry, oldText, text));
                        }
                    } else {
                        if (text.equalsIgnoreCase(oldText)) {
                            autoCompleteEntry.queueEvent(new ValueChangeEvent(autoCompleteEntry, oldText, text));
                        }
                    }
                }
            } else if (keyEvent.getKeyCode() == KeyEvent.UP_ARROW_KEY || keyEvent.getKeyCode() == KeyEvent.DOWN_ARROW_KEY) {
                autoCompleteEntry.setPopulateList(true);
            } else if (textChanged) {
                if ("".equals(text)) autoCompleteEntry.setSubmittedValue("");
                autoCompleteEntry.queueEvent(new TextChangeEvent(autoCompleteEntry, text, oldText, keyEvent.getKeyCode()));
            }
        } else {
            autoCompleteEntry.setPopulateList(false);
            autoCompleteEntry.setSubmittedValue(text);
            autoCompleteEntry.setSubmittedText(text);
        }
        if (keyEvent.getKeyCode() == KeyEvent.TAB) {
            autoCompleteEntry.setPopulateList(false);
        }

        decodeBehaviors(facesContext, autoCompleteEntry);
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        final ResponseWriter writer = facesContext.getResponseWriter();
        final String clientId = uiComponent.getClientId(facesContext);
        final AutoCompleteEntry autoCompleteEntry = (AutoCompleteEntry) uiComponent;

        if (autoCompleteEntry.isReset()) {
            autoCompleteEntry.setText(null);
            autoCompleteEntry.setSubmittedText(null);
            autoCompleteEntry.setValue(null);
            autoCompleteEntry.setSubmittedValue(null);
            autoCompleteEntry.setReset(false);
        }

        int width = autoCompleteEntry.getWidth();
        boolean ariaEnabled = EnvUtils.isAriaEnabled(facesContext);
        Map paramMap = facesContext.getExternalContext().getRequestParameterMap();
        Map<String, Object> labelAttributes = getLabelAttributes(uiComponent);
        labelAttributes.put("fieldClientId", clientId + "_input");
        String inFieldLabel = (String) labelAttributes.get("inFieldLabel");
        String inFieldLabelStyleClass = "";
        String iceFocus = (String) paramMap.get("ice.focus");
        String text = autoCompleteEntry.getText();

        if (isValueBlank(text)) text = null;
        String inputClientId = clientId + "_input";
        boolean labelIsInField = false;

        if (text == null && !isValueBlank(inFieldLabel) && !inputClientId.equals(iceFocus)) {
            text = inFieldLabel;
            inFieldLabelStyleClass = " " + IN_FIELD_LABEL_STYLE_CLASS;
            labelIsInField = true;
        }

        // root
        writer.startElement("div", uiComponent);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", "ui-autocompleteentry " + autoCompleteEntry.getStyleClass(), null);
        renderResetSettings(facesContext, uiComponent);

        writeLabelAndIndicatorBefore(labelAttributes);

        writer.startElement("span", null); // position text field and list at the same level
        writer.writeAttribute("style", "display:inline-block;", null);
        // text field
        writer.startElement("input", null);
        writer.writeAttribute("type", "text", null);
        String accesskey = autoCompleteEntry.getAccesskey();
        if (accesskey != null) writer.writeAttribute("accesskey", accesskey, null);
        String tabindex = autoCompleteEntry.getTabindex();
        if (tabindex != null) writer.writeAttribute("tabindex", tabindex, null);
        writer.writeAttribute("name", inputClientId, null);
        if (ariaEnabled)
            writer.writeAttribute("role", "textbox", null);
        String placeholder = autoCompleteEntry.getPlaceholder();
        if (placeholder != null) writer.writeAttribute("placeholder", placeholder, null);

        writer.writeAttribute("style", "width: " + width + "px;" + autoCompleteEntry.getStyle(), null);
        boolean disabled = autoCompleteEntry.isDisabled();
        writer.writeAttribute("class", "ui-inputfield ui-widget ui-state-default ui-corner-all"
                + getStateStyleClasses(autoCompleteEntry) + inFieldLabelStyleClass + (disabled ? " ui-state-disabled" : ""), null);
        if (disabled) writer.writeAttribute("disabled", "disabled", null);
        writer.writeAttribute("autocomplete", "off", null);
        if (autoCompleteEntry.isReadonly()) {
            writer.writeAttribute("readonly", "readonly", null);
        }

        for (int i = 0; i < PASSTHROUGH_ATTRIBUTES.length; i++) {
            String name = PASSTHROUGH_ATTRIBUTES[i];
            if ("onfocus".equals(name)) {
                ComponentUtils.renderPassThroughAttribute(writer, autoCompleteEntry, name, "ice.setFocus(this.id);");
            } else if ("onmousedown".equals(name)) {
                ComponentUtils.renderPassThroughAttribute(writer, autoCompleteEntry, name, "this.focus();");
            } else {
                ComponentUtils.renderPassThroughAttribute(writer, autoCompleteEntry, name);
            }
        }
        ComponentUtils.renderExternalPassThroughAttributes(writer, autoCompleteEntry);

        if (ariaEnabled) {
            Map<String, Object> ariaAttributes = new HashMap<String, Object>() {{
                put("autocomplete", "list");
                put("readonly", autoCompleteEntry.isReadonly());
                put("required", autoCompleteEntry.isRequired());
                put("disabled", autoCompleteEntry.isDisabled());
                put("invalid", !autoCompleteEntry.isValid());
            }};
            writeAriaAttributes(ariaAttributes, labelAttributes);
        }

        writer.endElement("input");

        String divId = clientId + AUTOCOMPLETE_DIV;

        writer.startElement("span", null);
        writer.writeAttribute("style", "position:relative;display:block;", null);
        writer.startElement("span", null);
        writer.writeAttribute("id", divId, null);
        writer.writeAttribute("class", "ui-widget ui-widget-content ui-corner-all", null);
        writer.writeAttribute("style", "display:none;z-index:500;", null);
        writer.endElement("span");
        writer.endElement("span");

        writer.endElement("span");

        writeLabelAndIndicatorAfter(labelAttributes);

        encodeScript(facesContext, writer, clientId, autoCompleteEntry,
                paramMap, inFieldLabel, text, inputClientId, labelIsInField);

        writer.endElement("div");
    }

    private void encodeScript(FacesContext facesContext, ResponseWriter writer, String clientId, AutoCompleteEntry autoCompleteEntry, Map paramMap, String inFieldLabel, String text, String inputClientId, boolean labelIsInField) throws IOException {
        String divId = clientId + AUTOCOMPLETE_DIV;
        Object sourceId = paramMap.get("ice.event.captured");
        boolean isEventSource = sourceId != null && sourceId.toString().equals(inputClientId);
        KeyEvent keyEvent = new KeyEvent(autoCompleteEntry, paramMap);
        Object event = paramMap.get("javax.faces.behavior.event");
        boolean isBlurEvent = (keyEvent.getKeyCode() == KeyEvent.TAB || (event != null && event.toString().equals("blur")));
        boolean focus = isEventSource && !isBlurEvent;
        String direction = autoCompleteEntry.getDirection();
        direction = direction != null ? ("up".equalsIgnoreCase(direction) || "down".equalsIgnoreCase(direction) ? direction : "auto") : "auto";
        String placeholder = autoCompleteEntry.getPlaceholder();

        // script
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        if (!autoCompleteEntry.isDisabled() && !autoCompleteEntry.isReadonly()) {
            JSONBuilder jb = JSONBuilder.create();

            jb.beginFunction("ice.ace.create")
                    .item("Autocompleter")
                    .beginArray()
                    .item(clientId)
                    .item(divId)
                    .item("ui-widget-content")
                    .item("ui-state-active")
                    .item(autoCompleteEntry.getDelay())
                    .item(autoCompleteEntry.getMinChars())
                    .item(autoCompleteEntry.getHeight())
                    .item(direction)
                    .beginMap()
                    .entry("p", ""); // dummy property
            encodeClientBehaviors(facesContext, autoCompleteEntry, jb);

            jb.endMap();

            jb.beginMap()
                    .entryNonNullValue("inFieldLabel", inFieldLabel)
                    .entry("inFieldLabelStyleClass", IN_FIELD_LABEL_STYLE_CLASS)
                    .entry("labelIsInField", labelIsInField);

            jb.endMap();

            if (autoCompleteEntry.isClientSide()) {
                int rows = autoCompleteEntry.getRows();
                if (rows == 0) rows = Integer.MAX_VALUE;
                FilterMatchMode filterMatchMode = getFilterMatchMode(autoCompleteEntry);

                jb.beginMap()
                        .entry("rows", rows)
                        .entry("filterMatchMode", filterMatchMode.toString())
                        .entry("caseSensitive", autoCompleteEntry.isCaseSensitive())
                        .endMap();
            } else {
                jb.item("null", false);
            }

            // effects
            jb.beginMap()
                    .entry("show", autoCompleteEntry.getShowEffect())
                    .entry("showLength", autoCompleteEntry.getShowEffectLength())
                    .entry("hide", autoCompleteEntry.getHideEffect())
                    .entry("hideLength", autoCompleteEntry.getHideEffectLength())
                    .endMap();

            if (placeholder != null) jb.item(placeholder);
            else jb.item("");

            jb.endArray();

            jb.endFunction();

            writer.writeText(jb.toString(), null);
        }

        writer.endElement("script");

        String renderedValue = null;
        Object valueObject = autoCompleteEntry.getValue();
        String value = valueObject != null ? getConvertedValueForClient(facesContext, autoCompleteEntry, valueObject) : null;
        String submittedText = (String) autoCompleteEntry.getSubmittedText();
        if (autoCompleteEntry.isValid()) {
            if (value != null) {
                if (text == null) renderedValue = value;
            }
            if (submittedText != null && !submittedText.equals(value)) {
                autoCompleteEntry.setText(value);
                text = value;
            }
            if (renderedValue == null) renderedValue = text;
        } else {
            renderedValue = submittedText;
            autoCompleteEntry.setPopulateList(false);
        }
        autoCompleteEntry.setSubmittedText(null);

        // field update script
        encodeDynamicScript(writer, clientId + "_fieldupdate", "ice.ace.Autocompleters[\"" + clientId + "\"].updateField('" + escapeJavascriptString(renderedValue) + "', " + focus + ", "
                + (labelIsInField ? (value != null ? "'" + value + "'" : "''") : "null") + ");");

        if (sourceId != null && !(sourceId.toString().equals(clientId) || sourceId.toString().equals(clientId + "_input"))) {
            autoCompleteEntry.setPopulateList(false);
        }
        encodeList(facesContext, autoCompleteEntry);
    }

    private void encodeList(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        AutoCompleteEntry autoCompleteEntry = (AutoCompleteEntry) uiComponent;
        String clientId = autoCompleteEntry.getClientId(facesContext);
        if (autoCompleteEntry.isClientSide()) {
            populateClientSideList(facesContext, autoCompleteEntry);
        } else if (autoCompleteEntry.getText() != null && autoCompleteEntry.isPopulateList()) {
            populateList(facesContext, autoCompleteEntry);
        } else {
            String call = "ice.ace.Autocompleters[\"" + clientId + "\"].updateNOW('');";
            encodeDynamicScript(writer, clientId + "_update", call);
        }
    }

    public void populateList(FacesContext facesContext, AutoCompleteEntry autoCompleteEntry) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = autoCompleteEntry.getClientId(facesContext);
        autoCompleteEntry.populateItemList();
        Iterator matches = autoCompleteEntry.getItemListIterator();
        FilterMatchMode filterMatchMode = getFilterMatchMode(autoCompleteEntry);
        String text = (String) autoCompleteEntry.getText();
        String timestamp = getTimestamp(facesContext, autoCompleteEntry);
        int rows = autoCompleteEntry.getRows();
        if (rows == 0) rows = Integer.MAX_VALUE;
        int rowCounter = 0;
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_update", null);
        if (autoCompleteEntry.getSelectFacet() != null) {

            UIComponent facet = autoCompleteEntry.getSelectFacet();
            ValueExpression filterBy = autoCompleteEntry.getValueExpression("filterBy");
            ELContext elContext = facesContext.getELContext();
            String listVar = autoCompleteEntry.getListVar();

            writer.startElement("div", null);
            writer.writeAttribute("style", "display: none;", null);
            writer.startElement("a", null);
            Map requestMap = facesContext.getExternalContext().getRequestMap();
            //set index to 0, so child components can get client id from autoComplete component
            autoCompleteEntry.setIndex(0);
            while (matches.hasNext() && rowCounter < rows) {

                requestMap.put(listVar, matches.next());
                String value = (String) filterBy.getValue(elContext);

                if (satisfiesFilter(value, text, filterMatchMode, autoCompleteEntry)) {
                    rowCounter++;
                    writer.startElement("div", null);
                    writer.writeAttribute("style", "border: 0;", null);

                    // When HTML is display we still need a selected value. Hidding the value in a hidden span
                    // accomplishes this.
                    writer.startElement("span", null); // span to display
                    writer.writeAttribute("class", "informal", null);
                    encodeParentAndChildren(facesContext, facet);
                    writer.endElement("span");
                    writer.startElement("span", null); // span to select
                    writer.writeAttribute("style", "visibility:hidden;display:none;", null);
                    String itemLabel;
                    try {
                        itemLabel = getConvertedValueForClient(facesContext, autoCompleteEntry, value);
                    } catch (Exception e) {
                        itemLabel = value;
                    }
                    if (itemLabel != null) {
                        writer.writeText(itemLabel, null);
                    }
                    writer.endElement("span");
                    autoCompleteEntry.resetId(facet);
                    writer.endElement("div");
                }

                requestMap.remove(listVar);
            }
            autoCompleteEntry.setIndex(-1);

            writer.endElement("a");
            String call = "ice.ace.Autocompleters[\"" + clientId +
                    "\"].extractAndUpdateNOW();";
            encodeDynamicScript(writer, null, call + "// " + text + " " + timestamp);
            writer.endElement("div");
        } else {
            if (matches.hasNext()) {
                StringBuffer sb = new StringBuffer("<a>");
                SelectItem item = null;
                while (matches.hasNext() && rowCounter < rows) {
                    item = (SelectItem) matches.next();
                    String itemLabel = item.getLabel();
                    Object itemValue = item.getValue();
                    if (itemValue != null) {
                        try {
                            itemValue = getConvertedValueForClient(facesContext, autoCompleteEntry, itemValue);
                        } catch (Exception e) {
                            itemValue = itemValue.toString();
                        }
                    }
                    itemLabel = itemLabel == null ? itemValue.toString() : itemLabel;
                    if (satisfiesFilter(itemLabel, text, filterMatchMode, autoCompleteEntry)) {
                        sb.append("<div style=\"border: 0;\">");

                        sb.append("<span class=\"informal\">"); // span to display
                        sb.append(itemLabel);
                        sb.append("</span>");
                        sb.append("<span style=\"visibility:hidden;display:none;\">"); // span to select
                        sb.append(itemValue);
                        sb.append("</span>");

                        sb.append("</div>");
                        rowCounter++;
                    }
                }
                sb.append("</a>");
                String call = "ice.ace.Autocompleters[\"" + clientId + "\"]" +
                        ".updateNOW('" + escapeSingleQuote(sb.toString()) + "');";
                encodeDynamicScript(writer, null, call + "// " + text + " " + timestamp);
            }
        }
        writer.endElement("div");
    }

    public void populateClientSideList(FacesContext facesContext, AutoCompleteEntry autoCompleteEntry) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = autoCompleteEntry.getClientId(facesContext);
        autoCompleteEntry.populateItemList();
        Iterator matches = autoCompleteEntry.getItemListIterator();
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_update", null);
        writer.startElement("div", null);
        writer.writeAttribute("style", "display: none;", null);
        if (autoCompleteEntry.getSelectFacet() != null) {
            writer.writeAttribute("class", "facet", null);

            UIComponent facet = autoCompleteEntry.getSelectFacet();
            ValueExpression filterBy = autoCompleteEntry.getValueExpression("filterBy");
            ELContext elContext = facesContext.getELContext();
            String listVar = autoCompleteEntry.getListVar();

            Map requestMap = facesContext.getExternalContext().getRequestMap();
            //set index to 0, so child components can get client id from autoComplete component
            autoCompleteEntry.setIndex(0);
            while (matches.hasNext()) {

                requestMap.put(listVar, matches.next());
                String value = (String) filterBy.getValue(elContext);

                writer.startElement("div", null);
                writer.writeAttribute("style", "border: 0;", null);

                // When HTML is display we still need a selected value. Hidding the value in a hidden span
                // accomplishes this.
                writer.startElement("span", null); // span to display
                writer.writeAttribute("class", "informal", null);
                encodeParentAndChildren(facesContext, facet);
                writer.endElement("span");
                writer.startElement("span", null); // span to select
                writer.writeAttribute("class", "value", null);
                writer.writeAttribute("style", "visibility:hidden;display:none;", null);
                String itemLabel;
                try {
                    itemLabel = getConvertedValueForClient(facesContext, autoCompleteEntry, value);
                } catch (Exception e) {
                    itemLabel = value;
                }
                if (itemLabel != null) {
                    writer.writeText(itemLabel, null);
                }
                writer.endElement("span");
                autoCompleteEntry.resetId(facet);
                writer.endElement("div");

                requestMap.remove(listVar);
            }
            autoCompleteEntry.setIndex(-1);
        } else {
            if (matches.hasNext()) {
                StringBuffer sb = new StringBuffer();
                SelectItem item = null;
                while (matches.hasNext()) {
                    item = (SelectItem) matches.next();
                    String itemLabel = item.getLabel();
                    Object itemValue = item.getValue();
                    if (itemValue != null) {
                        try {
                            itemValue = getConvertedValueForClient(facesContext, autoCompleteEntry, itemValue);
                        } catch (Exception e) {
                            itemValue = itemValue.toString();
                        }
                    }
                    itemLabel = itemLabel == null ? itemValue.toString() : itemLabel;

                    sb.append("<div style=\"border: 0;\">");

                    sb.append("<span class=\"informal\">"); // span to display
                    sb.append(itemLabel);
                    sb.append("</span>");
                    sb.append("<span class=\"value\" style=\"visibility:hidden;display:none;\">"); // span to select
                    sb.append(itemValue);
                    sb.append("</span>");

                    sb.append("</div>");
                }
                writer.write(escapeSingleQuote(sb.toString()));
            }
        }
        writer.endElement("div");
        writer.endElement("div");
    }

    private void encodeDynamicScript(ResponseWriter writer, String clientId, String call) throws IOException {
        writer.startElement("span", null);
        if (clientId != null) {
            writer.writeAttribute("id", clientId, null);
        }
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeText(call, null);
        writer.endElement("script");
        writer.endElement("span");
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
    }

    private String getTimestamp(FacesContext facesContext, AutoCompleteEntry autoCompleteEntry) {
        Map requestMap = facesContext.getExternalContext().getRequestParameterMap();
        Object sourceId = requestMap.get("ice.event.captured");
        String clientId = autoCompleteEntry.getClientId(facesContext);
        KeyEvent keyEvent = new KeyEvent(autoCompleteEntry, requestMap);
        String timestamp = "";
        // only include a timestamp if the user pressed the up or down arrow keys to introduce a difference from previous dom
        // so that the domdiff sends the call to update the list of options
        if (keyEvent.getKeyCode() == KeyEvent.UP_ARROW_KEY || keyEvent.getKeyCode() == KeyEvent.DOWN_ARROW_KEY) {
            timestamp = "" + System.currentTimeMillis();
        }
        return timestamp;
    }

    private FilterMatchMode getFilterMatchMode(AutoCompleteEntry autoCompleteEntry) {
        String filterMatchMode = autoCompleteEntry.getFilterMatchMode();
        if ("contains".equalsIgnoreCase(filterMatchMode)) return FilterMatchMode.contains;
        if ("exact".equalsIgnoreCase(filterMatchMode)) return FilterMatchMode.exact;
        if ("endsWith".equalsIgnoreCase(filterMatchMode)) return FilterMatchMode.endsWith;
        if ("none".equalsIgnoreCase(filterMatchMode)) return FilterMatchMode.none;
        return FilterMatchMode.startsWith;
    }

    private boolean satisfiesFilter(String string, String filter, FilterMatchMode filterMatchMode, AutoCompleteEntry autoCompleteEntry) {

        if (string != null) {
            if (!autoCompleteEntry.isCaseSensitive()) {
                string = string.toLowerCase();
                filter = filter.toLowerCase();
            }
            switch (filterMatchMode) {
                case contains:
                    if (string.indexOf(filter) >= 0) return true;
                    break;
                case exact:
                    if (string.equals(filter)) return true;
                    break;
                case startsWith:
                    if (string.startsWith(filter)) return true;
                    break;
                case endsWith:
                    if (string.endsWith(filter)) return true;
                    break;
                default:
                    return true;
            }
        }

        return false;
    }

    public String getConvertedValueForClient(FacesContext context, UIComponent component, Object value) throws ConverterException {
        AutoCompleteEntry autoCompleteEntry = (AutoCompleteEntry) component;
        Converter converter = autoCompleteEntry.getConverter();

        if (converter != null) {
            return converter.getAsString(context, autoCompleteEntry, value);
        } else {
            ValueExpression ve = autoCompleteEntry.getValueExpression("value");

            if (ve != null) {
                Class<?> valueType = ve.getType(context.getELContext());
                Converter converterForType = context.getApplication().createConverter(valueType);

                if (converterForType != null) {
                    if (converterForType instanceof javax.faces.convert.EnumConverter && "".equals(value))
                        return converterForType.getAsString(context, autoCompleteEntry, null);
                    return converterForType.getAsString(context, autoCompleteEntry, value);
                }
            }
        }

        return (value != null ? value.toString() : "");
    }

    protected void renderResetSettings(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        String clientId = component.getClientId(context) + "_input";
        String labelPosition = (String) component.getAttributes().get("labelPosition");

        JSONBuilder jb = JSONBuilder.create();
        jb.beginArray();
        jb.item("Autocompleter");
        jb.beginArray();
        jb.item(clientId);

        if ("inField".equals(labelPosition)) {
            AutoCompleteEntry autoCompleteEntry = (AutoCompleteEntry) component;
            String label = (String) component.getAttributes().get("label");
            String indicatorPosition = (String) component.getAttributes().get("indicatorPosition");
            String optionalIndicator = (String) component.getAttributes().get("optionalIndicator");
            String requiredIndicator = (String) component.getAttributes().get("requiredIndicator");
            if ("labelLeft".equals(indicatorPosition)) {
                if (autoCompleteEntry.isRequired())
                    label = requiredIndicator + label;
                else
                    label = optionalIndicator + label;
            } else if ("labelRight".equals(indicatorPosition)) {
                if (autoCompleteEntry.isRequired())
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

    private enum FilterMatchMode {
        contains,
        exact,
        startsWith,
        endsWith,
        none
    }
}
