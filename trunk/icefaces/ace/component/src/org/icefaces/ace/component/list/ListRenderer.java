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

package org.icefaces.ace.component.list;

import org.icefaces.ace.json.JSONException;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.*;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.icefaces.ace.component.datetimeentry.DateTimeEntry;
import org.icefaces.ace.component.datetimeentry.DateTimeEntryRenderer;
import org.icefaces.ace.component.datetimeentry.DateTimeEntryUtils;
import org.icefaces.util.EnvUtils;
import javax.faces.application.Resource;

import java.util.Locale;
import java.util.ResourceBundle;

@MandatoryResourceComponent(tagName="list", value="org.icefaces.ace.component.list.ACEList")
public class ListRenderer extends CoreRenderer {
    public static final String containerStyleClass = "if-list ui-widget ui-widget-content ui-corner-all";
    public static final String controlsContainerStyleClass = "if-list-ctrls";
    public static final String pointerStyleClass = "if-pntr";
    public static final String bodyStyleClass = "if-list-body";
    public static final String miniClass = "if-mini";
    public static final String itemStyleClass = "if-list-item ui-state-default";
    public static final String selectedItemStyleClass = "ui-state-active";
    public static final String disabledItemStyleClass = "disabled";
    public static final String controlsItemStyleClass = "if-list-ctrl";
    public static final String controlsItemSpacerClass = "if-list-ctrl-spcr";
    public static final String headerStyleClass = "if-list-head ui-widget-header";
    public static final String footerStyleClass = "if-list-foot ui-widget-content";
    public static final String placeholderStyleClass = "if-list-plhld if-list-item ui-state-default";
	private static final String ACE_MESSAGES_BUNDLE = "org.icefaces.ace.resources.messages";
    private static final String MESSAGE_KEY_PREFIX = "org.icefaces.ace.component.list.";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        ACEList list = (ACEList)component;
        String id = list.getClientId(context);

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String filtering = id + "_filtering";
        String sorting = id + "_sorting";

		if (params.get(filtering) != null) {
			list.savedFilterState = new FilterState(context, list);
			list.applyFilters();
		} else if (params.get(sorting) != null) {
			list.savedSortState = SortState.getSortStateFromRequest(context, list);
			list.applySorting();
		} else {
			String select = id + "_selections";
			String deselect = id + "_deselections";
			String reordering = id + "_reorderings";
			String removal = id + "_removals";
			String immigration = id + "_immigration";
			String emigration = id + "_emigration";

			String selectInput = params.get(select);
			String deselectInput = params.get(deselect);
			String reorderingInput = params.get(reordering);
			String removalInput = params.get(removal);
			String immigrationInput = params.get(immigration);
			String emigrationInput = params.get(emigration);

			ListDecoder decoder = new ListDecoder(list);

			try {
				decoder.processSelections(selectInput)
						.processDeselections(deselectInput)
						 // If source, find outgoing objects and pass them
						 // to their destination list
						 .attachEmigrants(context, emigrationInput)
						  .processReorderings(reorderingInput)
						  .processRemovals(removalInput)
						   /// If destination, fetch incoming objects if not
						   /// already passed by decoding source list
						   .fetchImmigrants(context, immigrationInput)
							//// If destination, insert immigrant objects from
							//// records attached to List
							.insertImmigrants()
							 ///// If source, check destination list for attached
							 ///// immigrants (if we did not earlier put them there)
							 ///// and remove them from our attached list
							 .removeEmigrants(context, emigrationInput);
			} catch (JSONException e) {
				throw new FacesException(e);
			}
		}

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ACEList list = (ACEList) component;
        String clientId = component.getClientId(context);
        String styleClass = list.getStyleClass();
        String style = list.getStyle();
        Boolean mini = list.isCompact();

		// Make sure filters and sorting are applied in the current data model at this stage of the lifecycle
		// when using Mojarra (in some scenarios, filters and sorting aren't being applied at this point).
		if (!EnvUtils.isMyFaces()) {
				if (list.savedFilterState != null) {
					list.savedFilterState.apply(list);
					list.setFilteredData(list.processFilters(context));
				}
		}
		if (list.savedSortState != null) {
			list.setFilteredData(list.processFilters(context));
			list.processSorting();
		}
        list.getDataModel(); // DataModel init determines if some features are available

        styleClass = styleClass == null ? containerStyleClass : styleClass + " " + containerStyleClass;
        if (mini) styleClass += " " + miniClass;

        writer.startElement(HTML.DIV_ELEM, component);
        writer.writeAttribute(HTML.ID_ATTR, clientId, "clientId");
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, "styleClass");
        if (style != null)
            writer.writeAttribute(HTML.STYLE_ATTR, style, "style");

        if (list.getFacet("header") != null)
            encodeHeader(context, writer, list);

		if (list.getValueExpression("sortBy") != null) {
			writer.startElement(HTML.DIV_ELEM, null);
			writer.writeAttribute(HTML.CLASS_ATTR, "if-list-sort", null);
			encodeSortControl(writer, context, list);
			writer.endElement(HTML.DIV_ELEM);
		}

		if (list.getValueExpression("filterBy") != null) {
			writer.startElement(HTML.DIV_ELEM, null);
			writer.writeAttribute(HTML.CLASS_ATTR, "if-list-filter", null);
			writer.startElement(HTML.DIV_ELEM, null);
			encodeFilter(context, list);
			writer.endElement(HTML.DIV_ELEM);
			writer.endElement(HTML.DIV_ELEM);
		}

        if (list.isControlsEnabled())
            encodeControls(context, writer, list);
    }

    private void encodeControls(FacesContext context, ResponseWriter writer, ACEList component) throws IOException {
        String styleClass = component.getControlsContainerClass();
        styleClass = styleClass == null ? controlsContainerStyleClass : styleClass + " " + controlsContainerStyleClass;

        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, "controlsContainerClass");

        for (String buttonCode : component.getControlsFormat().split(" "))
            encodeControl(context, writer, component, buttonCode);

        writer.endElement(HTML.DIV_ELEM);
    }

    private void encodeControl(FacesContext context, ResponseWriter writer, ACEList component, String buttonCode) throws IOException {
        String itemStyleClass = "if-list-ctrl-" + buttonCode + " " + component.getControlsItemClass();
        String iconStyleClass;
        String property;
        UIComponent facet;

        itemStyleClass = itemStyleClass != null
                ? itemStyleClass + " " + controlsItemStyleClass
                : controlsItemStyleClass;

        if (buttonCode.equals("top")) {
            iconStyleClass = component.getTopButtonClass();
            property = "topButtonClass";
            facet = component.getFacet("topButton");
        } else if (buttonCode.equals("up")) {
            iconStyleClass = component.getUpButtonClass();
            property = "upButtonClass";
            facet = component.getFacet("upButton");
        } else if (buttonCode.equals("dwn")) {
            iconStyleClass = component.getDownButtonClass();
            property = "downButtonClass";
            facet = component.getFacet("downButton");
        } else if (buttonCode.equals("btm")) {
            iconStyleClass = component.getBottomButtonClass();
            property = "bottomButtonClass";
            facet = component.getFacet("bottomButton");
        } else if (buttonCode.equals("rmv")) {
            iconStyleClass = component.getRemoveButtonClass();
            property = "removeButtonClass";
            facet = component.getFacet("removeButton");
        } else return;

        if (facet != null) {
            facet.encodeAll(context);
        } else {
            writer.startElement(HTML.SPAN_ELEM, null);
            String controlsSpacerClass = component.getControlsSpacerClass();
            String spacerClass = controlsItemSpacerClass + (controlsSpacerClass == null ?  "" : (" " + controlsSpacerClass));
            writer.writeAttribute(HTML.CLASS_ATTR, spacerClass, null);

            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, itemStyleClass, "controlsItemClass");
            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, iconStyleClass, property);
            writer.endElement(HTML.SPAN_ELEM);
            writer.endElement(HTML.SPAN_ELEM);

            writer.endElement(HTML.SPAN_ELEM);
        }
    }

    private void encodeHeader(FacesContext context, ResponseWriter writer, ACEList component) throws IOException {
        String styleClass = component.getHeaderClass();
        styleClass = styleClass == null ? headerStyleClass : styleClass + " " + headerStyleClass;
        String style = component.getHeaderStyle();

        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, "headerClass");
        if (style != null)
            writer.writeAttribute(HTML.STYLE_ATTR, style, "headerStyle");

        UIComponent facet = component.getFacet("header");
        if (facet != null)
            facet.encodeAll(context);

        writer.endElement(HTML.DIV_ELEM);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ACEList list = (ACEList)component;

        //reset data model
        list.setDataModel(null);
        list.getDataModel();

        String dropGroup = list.getDropGroup();
        String style = list.getBodyStyle();
        String bodyHeight = list.getHeight();
        String styleClass = list.getBodyClass();

        styleClass = styleClass == null
                ? bodyStyleClass
                : styleClass + " " + bodyStyleClass;

        if (dropGroup != null) styleClass += " dg-" + dropGroup;

        writer.startElement(HTML.UL_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, "bodyClass");

        if (bodyHeight != null) {
            bodyHeight = " height:"+bodyHeight+";";
            style = style == null ? bodyHeight : style + "; " + bodyHeight;
        }

        if (style != null)
            writer.writeAttribute(HTML.STYLE_ATTR, style, "bodyStyle");

        encodeChildren(context, writer, list);

        writer.endElement(HTML.UL_ELEM);
    }

    private void encodeChildren(FacesContext context, ResponseWriter writer, ACEList list) throws IOException {
        final Collection<Object> selections = list.isSelectItemModel() ? (Collection)list.getValue() : list.getSelections();
        String style = list.getItemStyle();
        String styleClass = list.getItemClass();
        String selectionMode = list.getSelectionMode();
        boolean pointerTable = ("single".equals(selectionMode) || "multiple".equals(selectionMode)) || list.isDragging();

        styleClass = styleClass == null ? itemStyleClass : styleClass + " " + itemStyleClass;

        int first = list.getFirst();
        int rows = list.getRows();
        int last = first + (rows == 0 ? list.getDataModel().getRowCount() : rows);
        int index = first;
        list.setRowIndex(first);

        boolean selectItems = false;
        if (list.isRowAvailable())
            selectItems = list.getRowData() instanceof SelectItem;

        while (index < last) {
            String itemStyleClass = new String(styleClass);
            SelectItem item = selectItems ? (SelectItem)list.getRowData() : null;
            Object val = selectItems && list.isSelectItemModel() ? item.getValue() : list.getRowData();

            boolean selected = selections == null ? false : selections.contains(val);
            boolean disabled = selectItems ? item.isDisabled() : false;

            if (selected) itemStyleClass = itemStyleClass + " " + selectedItemStyleClass;
            if (disabled) itemStyleClass = itemStyleClass + " " + disabledItemStyleClass;
            else if (pointerTable) itemStyleClass = itemStyleClass + " " + pointerStyleClass;

            if (selectItems)
                encodeStringChild(context, writer, list, item, itemStyleClass, style);
            else
                encodeCompositeChild(context, writer, list, itemStyleClass, style);

            list.setRowIndex(++index);
        }

        list.setRowIndex(-1);
    }

    private void encodeStringChild(FacesContext context, ResponseWriter writer, ACEList list, SelectItem item, String styleClass, String style) throws IOException {
        writer.startElement(HTML.LI_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, list.getContainerClientId(context), null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, "itemClass");
        if (style != null)
            writer.writeAttribute(HTML.STYLE_ATTR, style, "itemStyle");

        writer.write(item.getLabel());

        writer.endElement(HTML.LI_ELEM);
    }

    private void encodeCompositeChild(FacesContext context, ResponseWriter writer, ACEList list, String styleClass, String style) throws IOException {
        writer.startElement(HTML.LI_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, list.getContainerClientId(context), null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, "itemClass");
        if (style != null)
            writer.writeAttribute(HTML.STYLE_ATTR, style, "itemStyle");

        // List has implicit UIColumn child to wrap composite children
        for (UIComponent component : list.getChildren()) {
            if (!(component instanceof UISelectItem ||
                    component instanceof UISelectItems)) {
				recalculateClientId(component);
                component.encodeAll(context);
			}
        }
        writer.endElement(HTML.LI_ELEM);
    }

	// cause to re-calculate client ID to include naming container ID
	private void recalculateClientId(UIComponent component) {
		component.setId(component.getId());
		for (UIComponent child : component.getChildren()) {
			recalculateClientId(child);
		}
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ACEList list = (ACEList) component;

        if (component.getFacet("footer") != null)
            encodeFooter(context, writer, list);

        encodeHiddenFields(context, writer, list);
        encodeScript(context, writer, list);

        writer.endElement(HTML.DIV_ELEM);
    }

    private void encodeHiddenFields(FacesContext context, ResponseWriter writer, ACEList list) throws IOException {
        // Write fields intended for delayed communications of changes to list
        String id = list.getClientId(context);

        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, id + "_selections", null);
        writer.writeAttribute(HTML.NAME_ATTR, id + "_selections", null);
        writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
        writer.writeAttribute(HTML.AUTOCOMPLETE_ATTR, "off", null);
        writer.endElement(HTML.INPUT_ELEM);

        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, id + "_deselections", null);
        writer.writeAttribute(HTML.NAME_ATTR, id + "_deselections", null);
        writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
        writer.writeAttribute(HTML.AUTOCOMPLETE_ATTR, "off", null);
        writer.endElement(HTML.INPUT_ELEM);

        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, id + "_reorderings", null);
        writer.writeAttribute(HTML.NAME_ATTR, id + "_reorderings", null);
        writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
        writer.writeAttribute(HTML.AUTOCOMPLETE_ATTR, "off", null);
        writer.endElement(HTML.INPUT_ELEM);

        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, id + "_removals", null);
        writer.writeAttribute(HTML.NAME_ATTR, id + "_removals", null);
        writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
        writer.writeAttribute(HTML.AUTOCOMPLETE_ATTR, "off", null);
        writer.endElement(HTML.INPUT_ELEM);
    }

    private void encodeScript(FacesContext context, ResponseWriter writer, ACEList component) throws IOException {
        String clientId = component.getClientId(context);

        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, clientId+"_script", null);
        writer.startElement(HTML.SCRIPT_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "text/javascript", null);

        String styleClass = component.getPlaceholderClass();
        styleClass = styleClass == null ? placeholderStyleClass : styleClass + " " + placeholderStyleClass;
        String selectionMode = component.getSelectionMode();
        String dropGroup = component.getDropGroup();
        boolean selectItemModel = component.isSelectItemModel();

        JSONBuilder cfgBuilder = JSONBuilder.create()
                .beginFunction("ice.ace.create").item("List").beginArray()
                .item(clientId);

        cfgBuilder.beginMap();
        cfgBuilder.entry("separator", UINamingContainer.getSeparatorChar(context));

        if (component.isPlaceholder())
            cfgBuilder.entry("placeholder", styleClass);

        if ("single".equals(selectionMode) || "multiple".equals(selectionMode))
            cfgBuilder.entry("selection", selectionMode);

        // Select item model doesn't allow reordering or migration
        if (!selectItemModel) {
            if (dropGroup != null)
                cfgBuilder.entry("connectWith", ".dg-"+dropGroup);

            if (component.isDragging()) {
                cfgBuilder.entry("dragging", true);
				String handle = component.getDragHandle();
				if (handle != null) cfgBuilder.entry("handle", handle);
			}

            if (component.isControlsEnabled())
                cfgBuilder.entry("controls", true);

            if (component.isDoubleClickMigration())
                cfgBuilder.entry("dblclk_migrate", true);
        }

        if (component.getValueExpression("filterBy") != null) {
			cfgBuilder.entry("filterEvent", component.getFilterEvent());
		}

		if (component.getValueExpression("sortBy") != null) {
			cfgBuilder.entry("sorting", true);
		}

		Locale locale;
		ClassLoader classLoader;
		ResourceBundle bundle = null;
		locale = context.getViewRoot().getLocale();
		classLoader = Thread.currentThread().getContextClassLoader();
		String bundleName = context.getApplication().getMessageBundle();
		if (classLoader == null) classLoader = bundleName.getClass().getClassLoader();
		if (bundleName == null) bundleName = ACE_MESSAGES_BUNDLE;
		bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);

		cfgBuilder.entry("removeConfirmationMessage", bundle.getString(MESSAGE_KEY_PREFIX + "REMOVE_CONFIRMATION_QUESTION"));
		cfgBuilder.entry("yesMessage", bundle.getString(MESSAGE_KEY_PREFIX + "YES"));
		cfgBuilder.entry("noMessage", bundle.getString(MESSAGE_KEY_PREFIX + "NO"));

        encodeClientBehaviors(context, component, cfgBuilder);

        cfgBuilder.endMap().endArray().endFunction();

        writer.write(cfgBuilder.toString());
        writer.endElement(HTML.SCRIPT_ELEM);
        writer.endElement(HTML.SPAN_ELEM);
    }

    private void encodeFooter(FacesContext context, ResponseWriter writer, ACEList component) throws IOException {
        String styleClass = component.getFooterClass();
        styleClass = styleClass == null ? footerStyleClass : styleClass + " " + footerStyleClass;
        String style = component.getFooterStyle();

        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, "footerClass");
        if (style != null)
            writer.writeAttribute(HTML.STYLE_ATTR, style, "footerStyle");

        UIComponent facet = component.getFacet("footer");
        if (facet != null)
            facet.encodeAll(context);

        writer.endElement(HTML.DIV_ELEM);
    }

	// ---------------------
	// ----- FILTERING -----
	// ---------------------

    private static void encodeFilter(FacesContext context, ACEList list) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ResponseWriter writer = context.getResponseWriter();

		if (list.getFilterFacet() != null) {
			encodeFilterFacet(context, list);
			return;
		}

		String clientId = list.getClientId(context);
        String filterId = clientId + "_filter";
        String filterFunction = "ice.ace.instance('"+clientId+"').filter(event)";

        String filterStyleClass = list.getFilterStyleClass();
        String filterEvent = list.getFilterEvent();
		boolean rangeFiltering = list.isRangedFilter();
        filterStyleClass = filterStyleClass == null ? 
				"if-list-filter"
                : "if-list-filter" + " " + filterStyleClass;


        if (list.getValueExpression("filterOptions") == null) {
			if (!(list.getFilterType() == FilterType.DATE)) {
				if (rangeFiltering && (list.getFilterType() != FilterType.TEXT && list.getFilterType() != FilterType.BOOLEAN)) {
					encodeFilterField(context, list, filterId, filterFunction, 
						filterStyleClass, filterEvent, "_min");
					encodeFilterField(context, list, filterId, filterFunction, 
						filterStyleClass, filterEvent, "_max");
				} else if (list.getFilterType() == FilterType.BOOLEAN) {
					encodeBooleanMenu(context, list, filterId, filterFunction, 
						filterStyleClass);
				} else {

					encodeFilterField(context, list, filterId, filterFunction, 
						filterStyleClass, filterEvent, "");

				}
			} else {
				if (rangeFiltering) {
					encodeDatePicker(context, list, filterId, filterFunction, "_min");
					encodeDatePicker(context, list, filterId, filterFunction, "_max");
				} else {
					encodeDatePicker(context, list, filterId, filterFunction, "");
				}
			}
        }
        else {
            writer.startElement("select", null);
            writer.writeAttribute(HTML.ID_ATTR, filterId, null);
            writer.writeAttribute(HTML.NAME_ATTR, filterId, null);
            writer.writeAttribute(HTML.TABINDEX_ATTR, list.getTabIndex(), null);
            //writer.writeAttribute(HTML.CLASS_ATTR, filterStyleClass, null);
            writer.writeAttribute("onchange", filterFunction, null);
			String accesskey = list.getAccesskey();
			if (accesskey != null) {
				writer.writeAttribute("accesskey", accesskey, null);				
			}

            SelectItem[] itemsArray = (SelectItem[]) getFilterOptions(list);
            Object filterVal = list.getFilterValue();

            for (SelectItem item : itemsArray) {
                writer.startElement("option", null);
                writer.writeAttribute("value", item.getValue(), null);

                Object itemVal = item.getValue();

                if ((filterVal == null && itemVal == null)
                        || itemVal.toString().equals(filterVal)) {
                    writer.writeAttribute("selected", "selected", null);
                }

                writer.write(item.getLabel());
                writer.endElement("option");
            }

            writer.endElement("select");
        }
    }

	private static void encodeFilterField(FacesContext context, ACEList list,
			String filterId, String filterFunction, String filterStyleClass, String filterEvent, String suffix) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Object filterValue;

		if ("_min".equals(suffix)) filterValue = list.getFilterValueMin() != null ? list.getFilterValueMin() : "";
		else if ("_max".equals(suffix)) filterValue = list.getFilterValueMax() != null ? list.getFilterValueMax() : "";
		else filterValue = list.getFilterValue() != null ? list.getFilterValue() : "";

		FilterType type = list.getFilterType();
		boolean isNumber = type == FilterType.BYTE
				|| type == FilterType.SHORT
				|| type == FilterType.INT
				|| type == FilterType.LONG
				|| type == FilterType.FLOAT
				|| type == FilterType.DOUBLE;

		if (type == FilterType.FLOAT || type == FilterType.DOUBLE) {
			filterValue = filterValue.toString().replaceAll("\\.0$", "");
		}

		writer.startElement(HTML.INPUT_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, filterId + suffix, null);
		writer.writeAttribute(HTML.NAME_ATTR, filterId + suffix, null);
		writer.writeAttribute(HTML.TABINDEX_ATTR, list.getTabIndex(), null);
		writer.writeAttribute(HTML.CLASS_ATTR, filterStyleClass, null);
		writer.writeAttribute("size", "1", null); // Webkit requires none zero/null size value to use CSS width correctly.
		writer.writeAttribute("value", filterValue , null);
/*
		if (isNumber) {
			writer.writeAttribute("onkeydown", "return ice.ace.DataTable.numberRestriction(event || window.event);", null);
		}
*/

		if (filterEvent.equals("keyup") || filterEvent.equals("blur"))
			writer.writeAttribute("on"+filterEvent, "ice.setFocus('"+filterId+suffix+"');"+filterFunction , null);

		if (list.getFilterStyle() != null)
			writer.writeAttribute(HTML.STYLE_ELEM, list.getFilterStyle(), null);

		if ("".equals(suffix) || "_min".equals(suffix)) {
			String accesskey = list.getAccesskey();
			if (accesskey != null) writer.writeAttribute("accesskey", accesskey, null);
		}

		writer.endElement(HTML.INPUT_ELEM);

		writer.startElement(HTML.SPAN_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, filterId + suffix + "_script", null);
		writer.startElement(HTML.SCRIPT_ELEM, null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write("document.getElementById('"+filterId+suffix+"').submitOnEnter = 'disabled'; // "+filterValue);

		writer.endElement(HTML.SCRIPT_ELEM);
		writer.endElement(HTML.SPAN_ELEM);
	}

	private static void encodeBooleanMenu(FacesContext context, ACEList list,
			String filterId, String filterFunction, String filterStyleClass) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Object filterValue = list.getFilterValue();
		filterValue = filterValue != null ? filterValue : "";

		writer.startElement(HTML.SELECT_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, filterId, null);
		writer.writeAttribute(HTML.NAME_ATTR, filterId, null);
		writer.writeAttribute(HTML.TABINDEX_ATTR, list.getTabIndex(), null);
		writer.writeAttribute(HTML.CLASS_ATTR, filterStyleClass, null);
		writer.writeAttribute("value", filterValue , null);
		String accesskey = list.getAccesskey();
		if (accesskey != null) {
			writer.writeAttribute("accesskey", accesskey, null);
		}

		writer.startElement(HTML.OPTION_ELEM, null);
		writer.writeAttribute("value", "", null);
		if (!"true".equalsIgnoreCase((String) filterValue)
			&& !"false".equalsIgnoreCase((String) filterValue)) writer.writeAttribute("selected", "selected", null);
		writer.endElement(HTML.OPTION_ELEM);

		writer.startElement(HTML.OPTION_ELEM, null);
		writer.writeAttribute("value", "true", null);
		if ("true".equalsIgnoreCase((String) filterValue)) writer.writeAttribute("selected", "selected", null);
		writer.write("True");
		writer.endElement(HTML.OPTION_ELEM);

		writer.startElement(HTML.OPTION_ELEM, null);
		writer.writeAttribute("value", "false", null);
		if ("false".equalsIgnoreCase((String) filterValue)) writer.writeAttribute("selected", "selected", null);
		writer.write("False");
		writer.endElement(HTML.OPTION_ELEM);

		writer.writeAttribute("onchange", filterFunction , null);

		if (list.getFilterStyle() != null)
			writer.writeAttribute(HTML.STYLE_ELEM, list.getFilterStyle(), null);

		writer.endElement(HTML.SELECT_ELEM);

		writer.startElement(HTML.SPAN_ELEM, null);
		writer.startElement(HTML.SCRIPT_ELEM, null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write("document.getElementById('"+filterId+"').submitOnEnter = 'disabled'; // "+filterValue);
		writer.endElement(HTML.SCRIPT_ELEM);
		writer.endElement(HTML.SPAN_ELEM);
	}

	private static void encodeDatePicker(FacesContext context, ACEList list,
			String clientId, String filterFunction, String suffix) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

        String inputId = clientId + suffix + "_input";
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        boolean ariaEnabled = EnvUtils.isAriaEnabled(context);

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + suffix, null);
        writer.writeAttribute("class", "if-list-filter", null);

        // input
        writer.startElement("input", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);
        writer.writeAttribute("type", "text", null);
		writer.writeAttribute("tabindex", "0", null);
		writer.writeAttribute("onchange", filterFunction , null);
        if (ariaEnabled) {
            writer.writeAttribute("role", "textbox", null);
        }

		Object filterValue;
		if ("_min".equals(suffix)) {
			filterValue = list.getFilterValueMin() != null ? list.getFilterValueMin() : "";
		} else if ("_max".equals(suffix)) {
			filterValue = list.getFilterValueMax() != null ? list.getFilterValueMax() : "";
		} else filterValue = list.getFilterValue() != null ? list.getFilterValue() : "";

		String datePattern = list.getFilterDatePattern();

		// convert date to string
		if (filterValue instanceof Date) {
			Locale locale = list.calculateLocale(context);
			DateFormat format = new SimpleDateFormat(datePattern, locale);
			filterValue = format.format((Date) filterValue);
		}

		String inFieldLabelClass = "ui-input-label-infield";
		boolean labelIsInField = false;
		if (filterValue != null && !"".equals(filterValue)) {
			writer.writeAttribute("value", filterValue, null);
		} else {
			writer.writeAttribute("value", datePattern, null);
			labelIsInField = true;
		}

		writer.writeAttribute("class", "ui-inputfield ui-widget ui-state-default ui-corner-all" 
			+ (labelIsInField ? " " + inFieldLabelClass : ""), null);

		writer.writeAttribute("size", "12", null);

        writer.endElement("input");

		encodeDatePickerScript(context, list, clientId + suffix, labelIsInField, datePattern, inFieldLabelClass);

        writer.endElement("span");
	}

	private static void encodeDatePickerScript(FacesContext context, ACEList list,
			String clientId, boolean labelIsInField, String datePattern, String InFieldLabelClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        StringBuilder script = new StringBuilder();
        JSONBuilder json = JSONBuilder.create();

        writer.write("ice.ace.jq(function(){");

        Locale locale = list.calculateLocale(context);
        json.beginMap()
            .entry("id", clientId)
            .entry("popup", true)
            .entry("locale", locale.toString())
			.entryNonNullValue("inFieldLabel", datePattern)
			.entry("inFieldLabelStyleClass", InFieldLabelClass)
			.entry("labelIsInField", labelIsInField)
            .entryNonNullValue("pattern", 
                DateTimeEntryUtils.parseTimeZone(DateTimeEntryUtils.convertPattern(list.getFilterDatePattern()), locale, java.util.TimeZone.getDefault()));

        json.entryNonNullValue("yearRange", "c-10:c+10");

		Resource resource = context.getApplication().getResourceHandler().createResource(DateTimeEntry.POPUP_ICON, "icefaces.ace");
        String iconSrc = resource.getRequestPath();

		json.entry("showOn", "both")
			.entry("buttonImage", iconSrc)
			.entry("buttonImageOnly", false);

		json.entry("showOtherMonths", true)
			.entry("selectOtherMonths", false);

        json.entry("disableHoverStyling", true);
        json.entry("showCurrentAtPos", 0);
        json.entry("clientId", clientId);
        json.entry("buttonText", "");
        json.entry("ariaEnabled", EnvUtils.isAriaEnabled(context));
        json.entry("todayNowButtonsAlsoSelect", false);

        Calendar calendar = Calendar.getInstance(locale);
        SimpleDateFormat formatter = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
        DateFormatSymbols dateFormatSymbols = formatter.getDateFormatSymbols();
        DateTimeEntryRenderer.buildUnicodeArray(json, "monthNames", dateFormatSymbols.getMonths(), 0);
        DateTimeEntryRenderer.buildUnicodeArray(json, "monthNamesShort", dateFormatSymbols.getShortMonths(), 0);
        DateTimeEntryRenderer.buildUnicodeArray(json, "dayNames", dateFormatSymbols.getWeekdays(), 1);
        DateTimeEntryRenderer.buildUnicodeArray(json, "dayNamesShort", dateFormatSymbols.getShortWeekdays(), 1);
        DateTimeEntryRenderer.buildUnicodeArray(json, "dayNamesMin", dateFormatSymbols.getShortWeekdays(), 1);
        json.entry("firstDay", calendar.getFirstDayOfWeek() - 1);

        json.endMap();

        writer.write("ice.ace.create('CalendarInit',[" + json + "]);");
		writer.write("});");
		writer.write("document.getElementById('"+clientId+"_input').submitOnEnter = 'disabled';");

        writer.endElement("script");
	}

	private static void encodeFilterFacet(FacesContext context, ACEList list) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = list.getClientId(context);

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_filter", null);
        writer.writeAttribute("class", "if-list-filter if-list-filter-button fa fa-chevron-down", null);
		writer.endElement("span");

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_filterFacet", null);
        writer.writeAttribute("class", "ui-widget-content if-list-filter-facet", null);
		writer.writeAttribute("style", "display: none;", null);

		list.getFilterFacet().encodeAll(context);

		writer.endElement("span");
	}

    private static SelectItem[] getFilterOptions(ACEList list) {
        Object options = list.getFilterOptions();
        if (options instanceof SelectItem[]) return (SelectItem[]) options;
        else if (options instanceof Collection<?>) return ((Collection<SelectItem>) list.getFilterOptions()).toArray(new SelectItem[] {});
        else throw new FacesException("Filter options for list " + list.getClientId() + " should be a SelectItem array or collection");
    }

	// -------------------
	// ----- SORTING -----
	// -------------------

    private static void encodeSortControl(ResponseWriter writer, FacesContext context, ACEList list) throws IOException {
        writer.startElement(HTML.SPAN_ELEM, null);
		String clientId = list.getClientId();
        writer.writeAttribute(HTML.ID_ATTR, clientId + "_sortControl", null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-sortable-control", null);

        // Write carats
		writer.startElement(HTML.SPAN_ELEM, null);
		writer.writeAttribute(HTML.CLASS_ATTR, "ui-sortable-list-icon", null);

		final String iconUpID = clientId + "_sortControl_up";
		final String iconDownID = clientId + "_sortControl_down";

		writer.startElement(HTML.ANCHOR_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, iconUpID, null);
		writer.writeAttribute(HTML.TABINDEX_ATTR, list.getTabIndex(), null);
		writer.writeAttribute(HTML.ONCLICK_ATTR, "ice.setFocus('" + iconUpID + "');", null);
		if (list.isSortAscending())
			writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-triangle-1-n" + " ui-toggled", null);
		else writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-triangle-1-n", null);
		if (list.getValueExpression("filterBy") == null) {
			String accesskey = list.getAccesskey();
			if (accesskey != null) writer.writeAttribute("accesskey", accesskey, null);
		}
		writer.endElement(HTML.ANCHOR_ELEM);

		writer.startElement(HTML.ANCHOR_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, iconDownID, null);
		writer.writeAttribute(HTML.TABINDEX_ATTR, list.getTabIndex(), null);
		writer.writeAttribute(HTML.ONCLICK_ATTR, "ice.setFocus('" + iconDownID + "');", null);
		if (!list.isSortAscending())
			writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-triangle-1-s" + " ui-toggled", null);
		else writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-triangle-1-s", null);
		writer.endElement(HTML.ANCHOR_ELEM);

		writer.startElement(HTML.SCRIPT_ELEM, null);
		writer.writeAttribute(HTML.TYPE_ATTR, "text/javascript", null);
		writer.writeText("(function() {var list = ice.ace.instance('" + clientId + "'); if (list) list.setupSortEvents();})();", null);
		writer.endElement(HTML.SCRIPT_ELEM);

        writer.endElement(HTML.SPAN_ELEM);

        writer.endElement(HTML.SPAN_ELEM);
    }
}
