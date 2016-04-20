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

package org.icefaces.ace.component.datatable;

import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.column.ColumnType;
import org.icefaces.ace.component.tableconfigpanel.TableConfigPanel;
import org.icefaces.ace.model.table.ColumnGroupModel;
import org.icefaces.ace.model.table.ColumnModel;
import org.icefaces.ace.model.table.DepthFirstHeadTraversal;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.util.EnvUtils;

import org.icefaces.ace.component.datetimeentry.DateTimeEntry;
import org.icefaces.ace.component.datetimeentry.DateTimeEntryRenderer;
import org.icefaces.ace.component.datetimeentry.DateTimeEntryUtils;

import javax.faces.application.Resource;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.*;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class DataTableHeadRenderer {
    protected static void encodeTableHead(FacesContext context, DataTableRenderingContext tableContext) throws IOException {
        DataTable table = tableContext.getTable();
        if (!table.hasHeaders()) return;

        String clientId = table.getClientId(context);
        ColumnModel columnModel = tableContext.getColumnModel();
        Map<String, AutoAdjustRenderedColspan.AdjustedRenderedColspan> map =
            AutoAdjustRenderedColspan.adjustIfAllowed(
                table.findTableConfigPanel(context), columnModel);
        ColumnGroupModel.TreeIterator iterator = columnModel.getHeaderModel().iterate();
        if (iterator.empty()) return;

        ResponseWriter writer = context.getResponseWriter();

        if (tableContext.isStaticHeaders() && !table.isInDuplicateSegment()) {
            writer.startElement(HTML.DIV_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SCROLLABLE_HEADER_CLASS, null);
            writer.startElement(HTML.TABLE_ELEM, null);
        }

        writer.startElement(HTML.THEAD_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, clientId + "_header", null);

        if (table.isInDuplicateSegment())
            writer.writeAttribute(HTML.STYLE_ATTR, "display:none;", null);

        boolean renderingFirstCol = true;
        if (iterator.columnGroup() == null || iterator.columnGroup().isRendered()) {
            do {
                if (iterator.row() != null && !iterator.row().isRendered()) {
                    continue;
                }
                writer.startElement(HTML.TR_ELEM, null);
                tableContext.setInHeaderSubrows(iterator.row() != null);
                do {
                    List<Column> columnsInCell = iterator.columns();
                    for (int i = 0; i < columnsInCell.size(); i++) {
                        Column column = columnsInCell.get(i);
                        if (column.isRendered()) {
                            tableContext.setFirstColumn(renderingFirstCol);
                            tableContext.setLastColumn(!iterator.nextRendered(false));
                            boolean isCurrStacked = DataTableRendererUtil.
                                isCurrColumnStacked(columnsInCell, column);
                            boolean isNextStacked = DataTableRendererUtil.
                                isNextStacked(columnsInCell, column);
                            if (isNextStacked) { // Used to only check if tableContext.isInHeaderSubrows()
                                if (!DataTableRendererUtil.areBothSingleColumnSpan(column, columnsInCell.get(i+1)))
                                    throw new FacesException("DataTable : \"" + clientId + "\" must not have stacked header columns, with colspan values greater than 1.");
                                if (!DataTableRendererUtil.isNextColumnRowSpanEqual(column, columnsInCell.get(i+1)))
                                    throw new FacesException("DataTable : \"" + clientId + "\" must not have stacked header columns, with unequal rowspan values.");
                            }
                            encodeColumn(context, tableContext, column,
                                isCurrStacked, isNextStacked, map);
                            renderingFirstCol = false;
                        }
                    }
                } while (iterator.nextPeer(false, true));

                writer.endElement(HTML.TR_ELEM);
            } while (iterator.nextRow(true, true));
        }

        // None of the header rows or columns are rendered, so render the
        // TableConfigPanel launcher button on its own
        TableConfigPanel panel = table.findTableConfigPanel(context);
        if (renderingFirstCol && panel != null && !panel.getType().equals(
            "paginator-button")) {
            writer.startElement(HTML.TR_ELEM, null);
            writer.startElement(HTML.TD_ELEM, null);
            writer.writeAttribute(HTML.COLSPAN_ATTR,
                iterator.getColumnGroupModel().getColumns(), null);
            encodeConfigPanelLaunchButton(writer, table, true);
            writer.endElement(HTML.TD_ELEM);
            writer.endElement(HTML.TR_ELEM);
        }

        writer.endElement(HTML.THEAD_ELEM);

        if (tableContext.isStaticHeaders() && !table.isInDuplicateSegment()) {
            writer.endElement(HTML.TABLE_ELEM);
            writer.endElement(HTML.DIV_ELEM);
        }
    }

   private static void encodeColumn(FacesContext context,
            DataTableRenderingContext tableContext, Column column,
            boolean isCurrStacked, boolean isNextStacked,
            Map<String, AutoAdjustRenderedColspan.AdjustedRenderedColspan>
            adjColspans) throws IOException {
        DataTable table = tableContext.getTable();
        ResponseWriter writer = context.getResponseWriter();
        String clientId = column.getClientId(context);

        tableContext.setColumnSortable(column.getValueExpression("sortBy") != null);
        tableContext.setColumnFilterable(column.getValueExpression("filterBy") != null);

        if (!isCurrStacked) {
            String style = column.getStyle();
            String styleClass = column.getStyleClass();
            String columnClass = DataTableConstants.COLUMN_HEADER_CLASS;

            columnClass = (tableContext.isReorderableColumns() && column.isReorderable())
                    ? columnClass + " " + DataTableConstants.REORDERABLE_COL_CLASS
                    : columnClass;
            columnClass = styleClass != null
                    ? columnClass + " " + styleClass
                    : columnClass;
            columnClass = (column.hasSortPriority() && !isNextStacked)
                    ? columnClass + " ui-state-active"
                    : columnClass;
			Integer displayPriority = column.getDisplayPriority();
			if (displayPriority != null) {
				columnClass += " ui-table-priority-" + displayPriority;
			}

            writer.startElement(HTML.TH_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, columnClass, null);

            if (style != null)
                writer.writeAttribute(HTML.STYLE_ELEM, style, null);
            int rowspan = column.getRowspan();
            if (rowspan != 1)
                writer.writeAttribute(HTML.ROWSPAN_ATTR, rowspan, null);
            AutoAdjustRenderedColspan.AdjustedRenderedColspan adj =
                (adjColspans != null) ? adjColspans.get(clientId) : null;
            int colspan = adj != null ? adj.getAdjustedColspan() : column.getColspan();
            if (colspan != 1)
                writer.writeAttribute(HTML.COLSPAN_ATTR, colspan, null);
        }

        else {
            writer.startElement("hr", null);
            writer.endElement("hr");
        }

        //Container
        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, clientId, null);

        if (tableContext.isResizableColumns())
            writer.writeAttribute(HTML.STYLE_ATTR, "position:relative;", null);

        String columnClass = DataTableConstants.COLUMN_HEADER_CONTAINER_CLASS;
        columnClass = tableContext.isColumnSortable() ? columnClass + " " + DataTableConstants.SORTABLE_COLUMN_CLASS : columnClass;
        columnClass = table.isClickableHeaderSorting() ? columnClass + " clickable" : columnClass;
        // Add style class to div in stacking case, else style th
        columnClass = (column.hasSortPriority() && (isCurrStacked || isNextStacked)) ? columnClass + " ui-state-active" : columnClass;

        writer.writeAttribute(HTML.CLASS_ATTR, columnClass, null);

        TableConfigPanel panel = table.findTableConfigPanel(context);

        if (!isCurrStacked && panelTargetsColumn(panel, column,
                tableContext.isFirstColumn(),
                tableContext.isLastColumn(), true))
            encodeLeftSideControls(writer, table,
                    tableContext.isFirstColumn());

        writer.startElement(HTML.SPAN_ELEM, null);

        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, clientId+"_text", null);
        writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.HEAD_TEXT_CLASS, null);

        //Header content
        UIComponent header = column.getFacet("header");
        String headerText = column.getHeaderText();

        if (header != null) header.encodeAll(context);
        else if (headerText != null) writer.write(headerText);
        else if (tableContext.isInHeaderSubrows())
            for (UIComponent c : column.getChildren())
                c.encodeAll(context);


        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.SPAN_ELEM);

        boolean configButton = !isCurrStacked && panelTargetsColumn(panel, column, tableContext.isFirstColumn(),
                tableContext.isLastColumn(), false);

        if (tableContext.isColumnSortable() || tableContext.isColumnPinningEnabled() || configButton)
            encodeRightSideControls(writer, context, tableContext, column, configButton);

        //Filter
        if (tableContext.isColumnFilterable())
            encodeFilter(context, tableContext, column);

		if (tableContext.isColumnSortable() && table.isClickableHeaderSorting()) {
			writer.startElement(HTML.SCRIPT_ELEM, null);
			writer.writeAttribute(HTML.TYPE_ATTR, "text/javascript", null);
			writer.writeText("(function(){var table = ice.ace.instance('"+table.getClientId(context)+"');if(table) table.setupClickableHeaderEventsForColumn('"+clientId+"');})();", null);
			writer.endElement(HTML.SCRIPT_ELEM);
		}

        writer.endElement(HTML.DIV_ELEM);

        if (!isNextStacked) {
            writer.endElement("th");
        }
    }

    private static boolean panelTargetsColumn(TableConfigPanel panel, Column column, boolean firstColumn, boolean lastColumn, boolean left) {
        if (panel == null) return false;
        String type = panel.getType();
        if (type.equals("first-col") && firstColumn && left) {
            return true;
        } else if (type.equals("last-col") && lastColumn && !left)  {
            return true;
        } else if (type.equals("in-col-left") && left && panel.getInColumnId().equals(column.getId())) {
            return true;
        } else if (type.equals("in-col-right") && !left && panel.getInColumnId().equals(column.getId())) {
            return true;
        }
        return false;
    }

    private static void encodeLeftSideControls(ResponseWriter writer, DataTable table, boolean first) throws IOException {
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.HEADER_LEFT_CLASS, null);

        encodeConfigPanelLaunchButton(writer, table, first);

        writer.endElement(HTML.SPAN_ELEM);
    }

    private static void encodeRightSideControls(ResponseWriter writer, FacesContext context, DataTableRenderingContext tableContext, Column column, boolean renderConfButton) throws IOException {
        writer.write("&nbsp;");

        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.HEADER_RIGHT_CLASS, null);

        //Sort icon
        if (tableContext.isColumnSortable())
            encodeSortControl(writer, context, tableContext, column);

        if (tableContext.isColumnPinningEnabled() && tableContext.showPinningControls())
            encodePinningControl(writer, context, tableContext, column);

        //Configurable last-col controls
        if (renderConfButton)
            encodeConfigPanelLaunchButton(writer, tableContext.getTable(), false);

        writer.endElement(HTML.SPAN_ELEM);
    }

    private static void encodePinningControl(ResponseWriter writer, FacesContext context, DataTableRenderingContext tableContext, Column column) throws IOException {
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.PIN_COLUMN_CONTROL_CLASS, null);
        writer.startElement(HTML.ANCHOR_ELEM, null);

        writer.writeAttribute(HTML.CLASS_ATTR, "ui-state-default ui-corner-all", null);
        writer.writeAttribute(HTML.HREF_ATTR, "#", null);
        writer.writeAttribute(HTML.ONCLICK_ATTR,
                "ice.ace.instance('"+tableContext.getTable().getClientId(context)+"').pinThisColumn(event);return false;", null);
        writer.startElement(HTML.SPAN_ELEM, null);

        writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon", null);

        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.ANCHOR_ELEM);
        writer.endElement(HTML.SPAN_ELEM);

    }

    private static void encodeSortControl(ResponseWriter writer, FacesContext context, DataTableRenderingContext tableContext, Column column) throws IOException {
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.ID_ATTR, column.getClientId() + "_sortControl", null);
        writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_CONTROL_CLASS, null);

        // Write carats
		if (!column.isHideSortControls()) {
			writer.startElement(HTML.SPAN_ELEM, null);
			writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_CONTAINER, null);

            final String iconUpID = column.getClientId() + "_sortControl_up";
            final String iconDownID = column.getClientId() + "_sortControl_down";

			writer.startElement(HTML.ANCHOR_ELEM, null);
            writer.writeAttribute(HTML.ID_ATTR, iconUpID, null);
            writer.writeAttribute(HTML.TABINDEX_ATTR, tableContext.getTabIndex(), null);
            writer.writeAttribute(HTML.ONCLICK_ATTR, "ice.setFocus('" + iconUpID + "');", null);
			if (column.hasSortPriority() && column.isSortAscending())
				writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_UP_CLASS + " ui-toggled", null);
			else writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_UP_CLASS, null);
			if (!tableContext.isColumnFilterable()) {
				String accesskey = column.getAccesskey();
				if (accesskey != null) writer.writeAttribute("accesskey", accesskey, null);
			}
			writer.endElement(HTML.ANCHOR_ELEM);

			writer.startElement(HTML.ANCHOR_ELEM, null);
            writer.writeAttribute(HTML.ID_ATTR, iconDownID, null);
			writer.writeAttribute(HTML.TABINDEX_ATTR, tableContext.getTabIndex(), null);
            writer.writeAttribute(HTML.ONCLICK_ATTR, "ice.setFocus('" + iconDownID + "');", null);
			if (column.hasSortPriority() && !column.isSortAscending())
				writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_DOWN_CLASS + " ui-toggled", null);
			else writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_DOWN_CLASS, null);
			writer.endElement(HTML.ANCHOR_ELEM);

			DataTable table = tableContext.getTable();
			writer.startElement(HTML.SCRIPT_ELEM, null);
			writer.writeAttribute(HTML.TYPE_ATTR, "text/javascript", null);
			writer.writeText("(function(){var table = ice.ace.instance('"+table.getClientId(context)+"');if(table) table.setupSortEventsForColumn('"+column.getClientId()+"_sortControl');})();", null);
			writer.endElement(HTML.SCRIPT_ELEM);

			writer.endElement(HTML.SPAN_ELEM);
		}

        // Write Sort Order Integer
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ORDER_CLASS, null);

        if (tableContext.getTable().isSingleSort())
            writer.writeAttribute(HTML.STYLE_ATTR, "display:none;", null);
        else if (column.hasSortPriority()) writer.writeText(column.getSortPriority(), null);

        writer.endElement(HTML.SPAN_ELEM);

        writer.endElement(HTML.SPAN_ELEM);
    }

    private static void encodeFilter(FacesContext context, DataTableRenderingContext tableContext, Column column) throws IOException {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        ResponseWriter writer = context.getResponseWriter();
        DataTable table = tableContext.getTable();

        String filterId = column.getClientId(context) + "_filter";
        String filterFunction = "ice.ace.instance('"+table.getClientId(context)+"').filter(event)";
        String filterStyleClass = column.getFilterStyleClass();
        String filterEvent = table.getFilterEvent();
		boolean rangeFiltering = column.isRangedFilter();
        filterStyleClass = filterStyleClass == null
                ? DataTableConstants.COLUMN_FILTER_CLASS
                : DataTableConstants.COLUMN_FILTER_CLASS + " " + filterStyleClass;

        if (column.getValueExpression("filterOptions") == null) {
			if (!(column.getColumnType() == ColumnType.DATE)) {
				if (rangeFiltering && (column.getColumnType() != ColumnType.TEXT && column.getColumnType() != ColumnType.BOOLEAN)) {
					encodeFilterField(context, tableContext, column, filterId, filterFunction, 
						filterStyleClass, filterEvent, "_min");
					encodeFilterField(context, tableContext, column, filterId, filterFunction, 
						filterStyleClass, filterEvent, "_max");
				} else if (column.getColumnType() == ColumnType.BOOLEAN) {
					encodeBooleanMenu(context, tableContext, column, filterId, filterFunction, 
						filterStyleClass);
				} else {
					encodeFilterField(context, tableContext, column, filterId, filterFunction, 
						filterStyleClass, filterEvent, "");
				}
			} else {
				if (rangeFiltering) {
					encodeDatePicker(context, table, column, filterId, filterFunction, "_min");
					encodeDatePicker(context, table, column, filterId, filterFunction, "_max");
				} else {
					encodeDatePicker(context, table, column, filterId, filterFunction, "");
				}
			}
        }
        else {
            writer.startElement("select", null);
            writer.writeAttribute(HTML.ID_ATTR, filterId, null);
            writer.writeAttribute(HTML.NAME_ATTR, filterId, null);
            writer.writeAttribute(HTML.TABINDEX_ATTR, tableContext.getTabIndex(), null);
            writer.writeAttribute(HTML.CLASS_ATTR, filterStyleClass, null);
            writer.writeAttribute("onchange", filterFunction, null);
			String accesskey = column.getAccesskey();
			if (accesskey != null) {
				writer.writeAttribute("accesskey", accesskey, null);				
			}

            SelectItem[] itemsArray = (SelectItem[]) getFilterOptions(column);
            Object filterVal = column.getFilterValue();

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

	private static void encodeBooleanMenu(FacesContext context, DataTableRenderingContext tableContext, Column column,
			String filterId, String filterFunction, String filterStyleClass) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Object filterValue = column.getFilterValue();
		filterValue = filterValue != null ? filterValue : "";

		writer.startElement(HTML.SELECT_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, filterId, null);
		writer.writeAttribute(HTML.NAME_ATTR, filterId, null);
		writer.writeAttribute(HTML.TABINDEX_ATTR, tableContext.getTabIndex(), null);
		writer.writeAttribute(HTML.CLASS_ATTR, filterStyleClass, null);
		writer.writeAttribute("value", filterValue , null);
		String accesskey = column.getAccesskey();
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

		if (column.getFilterStyle() != null)
			writer.writeAttribute(HTML.STYLE_ELEM, column.getFilterStyle(), null);

		writer.endElement(HTML.SELECT_ELEM);

		writer.startElement(HTML.SPAN_ELEM, null);
		writer.startElement(HTML.SCRIPT_ELEM, null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.write("document.getElementById('"+filterId+"').submitOnEnter = 'disabled'; // "+filterValue);
		writer.endElement(HTML.SCRIPT_ELEM);
		writer.endElement(HTML.SPAN_ELEM);
	}

	private static void encodeFilterField(FacesContext context, DataTableRenderingContext tableContext, Column column,
			String filterId, String filterFunction, String filterStyleClass, String filterEvent, String suffix) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Object filterValue;
		if ("_min".equals(suffix)) filterValue = column.getFilterValueMin() != null ? column.getFilterValueMin() : "";
		else if ("_max".equals(suffix)) filterValue = column.getFilterValueMax() != null ? column.getFilterValueMax() : "";
		else filterValue = column.getFilterValue() != null ? column.getFilterValue() : "";

		ColumnType type = column.getColumnType();
		boolean isNumber = type == ColumnType.BYTE
				|| type == ColumnType.SHORT
				|| type == ColumnType.INT
				|| type == ColumnType.LONG
				|| type == ColumnType.FLOAT
				|| type == ColumnType.DOUBLE;

		if (type == ColumnType.FLOAT || type == ColumnType.DOUBLE) {
			filterValue = filterValue.toString().replaceAll("\\.0$", "");
		}

		writer.startElement(HTML.INPUT_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, filterId + suffix, null);
		writer.writeAttribute(HTML.NAME_ATTR, filterId + suffix, null);
		writer.writeAttribute(HTML.TABINDEX_ATTR, tableContext.getTabIndex(), null);
		writer.writeAttribute(HTML.CLASS_ATTR, filterStyleClass, null);
		writer.writeAttribute("size", "1", null); // Webkit requires none zero/null size value to use CSS width correctly.
		writer.writeAttribute("value", filterValue , null);
		if (isNumber) {
			writer.writeAttribute("onkeydown", "return ice.ace.DataTable.numberRestriction(event || window.event);", null);
		}

		if (filterEvent.equals("keyup") || filterEvent.equals("blur"))
			writer.writeAttribute("on"+filterEvent, "ice.setFocus('"+filterId+suffix+"');"+filterFunction , null);

		if (column.getFilterStyle() != null)
			writer.writeAttribute(HTML.STYLE_ELEM, column.getFilterStyle(), null);

		if ("".equals(suffix) || "_min".equals(suffix)) {
			String accesskey = column.getAccesskey();
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

	private static void encodeDatePicker(FacesContext context, DataTable table, Column column,
			String clientId, String filterFunction, String suffix) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

        String inputId = clientId + suffix + "_input";
        Map paramMap = context.getExternalContext().getRequestParameterMap();
        boolean ariaEnabled = EnvUtils.isAriaEnabled(context);

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + suffix, null);
        writer.writeAttribute("class", "ui-column-filter", null);

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
			filterValue = column.getFilterValueMin() != null ? column.getFilterValueMin() : "";
		} else if ("_max".equals(suffix)) {
			filterValue = column.getFilterValueMax() != null ? column.getFilterValueMax() : "";
		} else filterValue = column.getFilterValue() != null ? column.getFilterValue() : "";

		String datePattern = column.getFilterDatePattern();

		// convert date to string
		if (filterValue instanceof Date) {
			Locale locale = column.calculateLocale(context);
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

		encodeDatePickerScript(context, table, column, clientId + suffix, labelIsInField, datePattern, inFieldLabelClass);

        writer.endElement("span");
	}

	private static void encodeDatePickerScript(FacesContext context, DataTable table, Column column,
			String clientId, boolean labelIsInField, String datePattern, String InFieldLabelClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        StringBuilder script = new StringBuilder();
        JSONBuilder json = JSONBuilder.create();

        writer.write("ice.ace.jq(function(){");

        Locale locale = column.calculateLocale(context);
        json.beginMap()
            .entry("id", clientId)
            .entry("popup", true)
            .entry("locale", locale.toString())
			.entryNonNullValue("inFieldLabel", datePattern)
			.entry("inFieldLabelStyleClass", InFieldLabelClass)
			.entry("labelIsInField", labelIsInField)
            .entryNonNullValue("pattern", 
                DateTimeEntryUtils.parseTimeZone(DateTimeEntryUtils.convertPattern(column.getFilterDatePattern()), locale, java.util.TimeZone.getDefault()));

        json.entryNonNullValue("yearRange", "c-10:c+10");

		String iconSrc = getResourceRequestPath(context, DateTimeEntry.POPUP_ICON);

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

    protected static String getResourceRequestPath(FacesContext facesContext, String resourceName) {
		Resource resource = facesContext.getApplication().getResourceHandler().createResource(resourceName, "icefaces.ace");

        return resource.getRequestPath();
	}

    protected static void encodeConfigPanelLaunchButton(ResponseWriter writer, DataTable component, boolean first) throws IOException {

        TableConfigPanel configPanel = component.findTableConfigPanel(FacesContext.getCurrentInstance());

        if (configPanel.isRendered()) {
            String clientId = configPanel.getClientId();

            JSONBuilder json = JSONBuilder.create().beginFunction("ice.ace.lazy")
                    .item("TableConfLauncher")
                    .beginArray()
                    .item(clientId + "_tableconf_launch")
                    .endArray()
                    .endFunction();

            String script = json.toString();

            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, "ui-tableconf-button", null);
            writer.writeAttribute(HTML.STYLE_ELEM, (first) ? "left:0;" : "right:0;", null);

            writer.startElement(HTML.ANCHOR_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, "ui-state-default ui-corner-all", null);
            writer.writeAttribute(HTML.ONMOUSEOVER_ATTR, script, null);
            writer.writeAttribute(HTML.ONFOCUS_ATTR, script, null);
            writer.writeAttribute(HTML.TABINDEX_ATTR, 0, null);
            writer.writeAttribute(HTML.ID_ATTR, clientId +"_tableconf_launch", null);
			String accesskey = configPanel.getAccesskey();
			if (accesskey != null) {
				writer.writeAttribute("accesskey", accesskey, null);
				writer.writeAttribute("onclick", "this.focus();event.stopPropagation();", null);
			}

            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-gear", null);

            writer.endElement(HTML.SPAN_ELEM);
            writer.endElement(HTML.ANCHOR_ELEM);
            writer.endElement(HTML.SPAN_ELEM);
        }
    }

    /* Util Methods */

    private static SelectItem[] getFilterOptions(Column column) {
        Object options = column.getFilterOptions();
        if (options instanceof SelectItem[]) return (SelectItem[]) options;
        else if (options instanceof Collection<?>) return ((Collection<SelectItem>) column.getFilterOptions()).toArray(new SelectItem[] {});
        else throw new FacesException("Filter options for column " + column.getClientId() + " should be a SelectItem array or collection");
    }
}
