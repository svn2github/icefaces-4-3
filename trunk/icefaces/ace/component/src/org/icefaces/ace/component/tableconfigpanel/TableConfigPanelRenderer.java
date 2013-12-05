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

package org.icefaces.ace.component.tableconfigpanel;

import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.component.datatable.DataTableConstants;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.JSONBuilder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TableConfigPanelRenderer extends CoreRenderer {
    private static final String ACE_MESSAGES_BUNDLE = "org.icefaces.ace.resources.messages";
    private static final String TABLECONFIG_KEY_PREFIX = "org.icefaces.ace.component.tableconfigpanel.";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TableConfigPanel panel = (TableConfigPanel)component;
        String event = params.get("javax.faces.behavior.event");
        String source = params.get("javax.faces.source");
        if (source != null && event != null && source.equals(component.getClientId()) && event.equals("cancel")) {
            panel.setForcedRenderCount(panel.getForcedRenderCount()+1);
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        TableConfigPanel panel = (TableConfigPanel)component;

        encodePopup(context, panel);
        super.encodeEnd(context, component);
    }
    private void encodePopup(FacesContext context, TableConfigPanel component) throws IOException {
        DataTable table = (DataTable)component.getTargetedDatatable();
        String tableId = table.getClientId(context);
        String clientId = component.getClientId(context);
        String jsId = this.resolveWidgetVar(component);
        ResponseWriter writer = context.getResponseWriter();
        List<Column> columns = table.getColumns();
        int i;

        writer.startElement(HTML.DIV_ELEM, component);
        writer.writeAttribute(HTML.ID_ATTR, clientId, null);
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

        writer.writeAttribute(HTML.CLASS_ATTR, "ui-tableconf ui-widget", null);
        writer.writeAttribute(HTML.STYLE_ATTR, "display:none;", null);

        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-tableconf-header ui-widget-header ui-corner-tr ui-corner-tl", null);
        ResourceBundle bundle = getComponentResourceBundle(context, ACE_MESSAGES_BUNDLE);
        String columnSettingsTitle = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "COLUMN_SETTINGS_TITLE", "Column Settings");
        writer.writeText(columnSettingsTitle, null);

        String okTitle = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "OK_BUTTON_TITLE", "Save Changes");
        String closeTitle = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "CLOSE_BUTTON_TITLE", "Cancel Changes");
        String trashTitle = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "RESET_BUTTON_TITLE", "Reset To Original Settings");
        writeConfigPanelOkButton(writer, clientId, okTitle);
        writeConfigPanelCloseButton(writer, clientId, closeTitle);
        writeConfigPanelTrashButton(writer, clientId, trashTitle);

        writer.endElement(HTML.DIV_ELEM);

        writer.startElement(HTML.DIV_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR,  "ui-tableconf-body ui-widget-content ui-corner-br ui-corner-bl", null);

        writer.startElement(HTML.TABLE_ELEM, null);

        writeHeaderRow(writer, component, context);

        writer.startElement(HTML.TBODY_ELEM, null);

        writeColumnConfigRows(writer, component, clientId, columns);

        writer.endElement(HTML.TBODY_ELEM);
        writer.endElement(HTML.TABLE_ELEM);

        writer.endElement(HTML.DIV_ELEM);

        writeJavascript(writer, clientId, tableId, component);

        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "hidden", null);
        writer.writeAttribute(HTML.NAME_ATTR, "absRend", null);
        writer.writeAttribute(HTML.VALUE_ATTR, component.getForcedRenderCount(), null);
        writer.writeAttribute(HTML.AUTOCOMPLETE_ATTR, "off", null);
        writer.endElement(HTML.INPUT_ELEM);
        writer.endElement(HTML.DIV_ELEM);

        if (component.isModal()) {
            writer.startElement(HTML.DIV_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR,  "ui-tableconf-modal", null);
            writer.endElement(HTML.DIV_ELEM);
        }
    }

    private void writeColumnConfigRows(ResponseWriter writer, TableConfigPanel component, String tableId, List<Column> columns) throws IOException {
        int i;
        boolean ordering = component.isColumnOrderingConfigurable();
        boolean naming = component.isColumnNameConfigurable();
        boolean sizing = false; //component.isColumnSizingConfigurable();
        boolean visibility = component.isColumnVisibilityConfigurable();
        boolean sorting = component.isColumnSortingConfigurable();
        boolean firstCol = component.getType().equals("first-col");
        boolean lastCol = component.getType().equals("last-col");
        boolean firstRendered = true;
        List<Integer> columnOrdering = component.getTargetedDatatable().getColumnOrdering();

        for (i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);

            String rowClass = "ui-tableconf-row-"+columnOrdering.get(i);
            if (!column.isConfigurable()) rowClass += " ui-disabled ui-opacity-40";

            writer.startElement(HTML.TR_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR,  rowClass, null);
            if (!column.isConfigurable() && component.isHideDisabledRows())
                writer.writeAttribute(HTML.STYLE_ATTR, "display:none;", null);

            boolean disableVisibilityControl = (firstRendered && firstCol && i == 0) || ((lastCol && isLastRendered(columns, i)));
            if (column.isRendered()) firstRendered = false;

            if (ordering) writeColumnOrderingControl(writer, column, i, tableId);
            writeColumnNameControl(writer, column, i, tableId, naming);
            if (sizing) writeColumnSizingControl(writer, column, i, tableId);
            if (visibility) writeColumnVisibilityControl(writer, column, i, tableId, disableVisibilityControl);
            if (sorting) writeSortControl(writer, column);

            writer.endElement(HTML.TR_ELEM);
        }
    }

    private boolean isLastRendered(List<Column> columns, int i) {
        while (++i < columns.size())
            if (columns.get(i).isRendered()) return false;

        return true;
    }

    private void writeHeaderRow(ResponseWriter writer, TableConfigPanel component, FacesContext context) throws IOException {
        writer.startElement(HTML.THEAD_ELEM, null);
        writer.startElement(HTML.TR_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-state-default", null);
        writer.writeAttribute(HTML.STYLE_ATTR, "border:0;", null);
        ResourceBundle bundle = getComponentResourceBundle(context, ACE_MESSAGES_BUNDLE);
        String orderLabel = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "ORDER_LABEL", "Order");
        String nameLabel = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "NAME_LABEL", "Name");
        String visibleLabel  = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "VISIBLE_LABEL", "Visible");
        String sortLabel = getLocalisedMessageFromBundle(bundle, TABLECONFIG_KEY_PREFIX, "SORT_LABEL", "Sort");
        if (component.isColumnOrderingConfigurable()) {
            writer.startElement(HTML.TH_ELEM, null);
            writer.writeText(orderLabel, null);
            writer.endElement(HTML.TH_ELEM);
        }

        writer.startElement(HTML.TH_ELEM, null);
        writer.writeText(nameLabel, null);
        writer.endElement(HTML.TH_ELEM);

        //if (component.isColumnSizingConfigurable()) {
        //    writer.startElement(HTML.TH_ELEM, null);
        //    writer.writeText("Sizing", null);
        //    writer.endElement(HTML.TH_ELEM);
        //}

        if (component.isColumnVisibilityConfigurable()) {
            writer.startElement(HTML.TH_ELEM, null);
            writer.writeText(visibleLabel, null);
            writer.endElement(HTML.TH_ELEM);
        }
        if (component.isColumnSortingConfigurable()) {
            writer.startElement(HTML.TH_ELEM, null);
            writer.writeText(sortLabel, null);
            writer.endElement(HTML.TH_ELEM);
        }

        writer.endElement(HTML.TR_ELEM);
        writer.endElement(HTML.THEAD_ELEM);
    }

    private void writeConfigPanelTrashButton(ResponseWriter writer, String clientId, String title) throws IOException {
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.STYLE_ATTR, "float:right;", null);

        writer.startElement(HTML.ANCHOR_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-state-default ui-corner-all ui-tableconf-head-button", null);
        writer.writeAttribute(HTML.HREF_ATTR, "#", null);
        writer.writeAttribute(HTML.TITLE_ATTR, title, null);
		writer.writeAttribute(HTML.ONCLICK_ATTR, "return false;", null);
        writer.writeAttribute(HTML.ID_ATTR, clientId +"_tableconf_trash", null);

        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-trash", null);

        writer.writeText("table", null);

        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.ANCHOR_ELEM);
        writer.endElement(HTML.SPAN_ELEM);
    }

    private void writeConfigPanelOkButton(ResponseWriter writer, String clientId, String title) throws IOException {
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.STYLE_ATTR, "float:right;", null);

        writer.startElement(HTML.ANCHOR_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-state-default ui-corner-all ui-tableconf-head-button", null);
        writer.writeAttribute(HTML.HREF_ATTR, "#", null);
        writer.writeAttribute(HTML.TITLE_ATTR, title, null);
		writer.writeAttribute(HTML.ONCLICK_ATTR, "return false;", null);
        writer.writeAttribute(HTML.ID_ATTR, clientId +"_tableconf_ok", null);

        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-check", null);

        writer.writeText("table", null);

        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.ANCHOR_ELEM);
        writer.endElement(HTML.SPAN_ELEM);
    }

    private void writeConfigPanelCloseButton(ResponseWriter writer, String clientId, String title) throws IOException {
        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.STYLE_ATTR, "float:right;", null);

        writer.startElement(HTML.ANCHOR_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-state-default ui-corner-all ui-tableconf-head-button", null);
        writer.writeAttribute(HTML.HREF_ATTR, "#", null);
        writer.writeAttribute(HTML.TITLE_ATTR, title, null);
		writer.writeAttribute(HTML.ONCLICK_ATTR, "return false;", null);
        writer.writeAttribute(HTML.ID_ATTR, clientId +"_tableconf_close", null);

        writer.startElement(HTML.SPAN_ELEM, null);
        writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-close", null);

        writer.writeText("table", null);

        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.ANCHOR_ELEM);
        writer.endElement(HTML.SPAN_ELEM);
    }

    private void writeJavascript(ResponseWriter writer, String clientId, String tableId, TableConfigPanel component) throws IOException {
        String jsId = this.resolveWidgetVar(component);
        boolean isSortable = component.isColumnSortingConfigurable();
        boolean isReorderable = component.isColumnOrderingConfigurable();
        boolean isSingleSort = ((DataTable)component.getTargetedDatatable()).isSingleSort();
        String handle = component.getDragHandle();
        Integer left = component.getOffsetLeft();
        Integer top = component.getOffsetTop();

        writer.startElement(HTML.SCRIPT_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "text/javascript", null);

        JSONBuilder json = new JSONBuilder()
                .initialiseVar(jsId).beginFunction("ice.ace.create")
                .item("TableConf").beginArray().item(clientId)
                .beginMap();

        if (handle != null && handle.length() > 0)
            json.entry("handle", handle);

        if (isReorderable)
            json.entry("reorderable", true);

        if (isSortable)
            json.entry("sortable", isSortable);

        if (isSingleSort)
            json.entry("singleSort", isSingleSort);

        if (left != null)
            json.entry("left", left);

        if (top != null)
            json.entry("top", top);

        json.entry("tableId", tableId);

        encodeClientBehaviors(FacesContext.getCurrentInstance(), component, json);

        json.endMap().endArray().endFunction();

        writer.write(json.toString());

        writer.endElement(HTML.SCRIPT_ELEM);
    }

    private void writeSortControl(ResponseWriter writer, Column column) throws IOException {
        writer.startElement(HTML.TD_ELEM, null);

        if (column.getValueExpression("sortBy") != null) {
            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, "ui-tableconf-sort-cont", null);

            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_CONTROL_CLASS, null);

            // Write carats
            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_CONTAINER, null);

            writer.startElement(HTML.ANCHOR_ELEM, null);
            if (column.getSortPriority() != null && column.isSortAscending())
                writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_UP_CLASS + " ui-toggled", null);
            else writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_UP_CLASS, null);
            writer.writeAttribute(HTML.TABINDEX_ATTR, 0, null);
            writer.endElement(HTML.ANCHOR_ELEM);

            writer.startElement(HTML.ANCHOR_ELEM, null);
            if (column.getSortPriority() != null && !column.isSortAscending())
                writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_DOWN_CLASS + " ui-toggled", null);
            else writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ICON_DOWN_CLASS, null);
            writer.writeAttribute(HTML.TABINDEX_ATTR, 0, null);
            writer.endElement(HTML.ANCHOR_ELEM);

            writer.endElement(HTML.SPAN_ELEM);

            // Write Sort Order Integer
            writer.startElement(HTML.SPAN_ELEM, null);
            writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.SORTABLE_COLUMN_ORDER_CLASS, null);
            if (column.getSortPriority() != null) writer.writeText(column.getSortPriority(), null);
            else writer.write(HTML.NBSP_ENTITY);
            writer.endElement(HTML.SPAN_ELEM);

            writer.endElement(HTML.SPAN_ELEM);
            writer.endElement(HTML.SPAN_ELEM);
        }
        writer.endElement(HTML.TD_ELEM);
    }
    private void writeColumnOrderingControl(ResponseWriter writer, Column column, int i, String tableId) throws IOException {
        writer.startElement(HTML.TD_ELEM, null);
        writer.startElement(HTML.ANCHOR_ELEM, null);

        String style = "display:inline-block; padding:0 1px 0 0; margin:0px 10px; text-align:left;";
        String styleClass = "ui-state-default ui-corner-all ui-sortable-handle";

        if (!column.isConfigurable()) styleClass += " ui-disabled";

        writer.writeAttribute(HTML.STYLE_ELEM, style, null);
        writer.writeAttribute(HTML.CLASS_ATTR, styleClass, null);
        writer.writeAttribute(HTML.HREF_ATTR, "#", null);

        writer.startElement(HTML.SPAN_ELEM, null);

        writer.writeAttribute(HTML.CLASS_ATTR, "ui-icon ui-icon-arrow-2-n-s", null);
        writer.writeText("table", null);

        writer.endElement(HTML.SPAN_ELEM);
        writer.endElement(HTML.ANCHOR_ELEM);
        writer.endElement(HTML.TD_ELEM);
    }
    private void writeColumnSizingControl(ResponseWriter writer, Column column, int i, String clientId) throws IOException {
        writer.writeText("DataTable Settings", null);
    }
    private void writeColumnVisibilityControl(ResponseWriter writer, Column column, int i, String clientId, boolean disable) throws IOException {
        writer.startElement(HTML.TD_ELEM, null);
        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "checkbox", null);
        if (disable || !column.isConfigurable())
            writer.writeAttribute(HTML.DISABLED_ATTR, "disabled", null);
        writer.writeAttribute(HTML.NAME_ATTR, clientId+"_colvis_"+i, null);
        if (column.isRendered()) writer.writeAttribute(HTML.CHECKED_ATTR, "checked", null);
        writer.endElement(HTML.INPUT_ELEM);
        writer.endElement(HTML.TD_ELEM);
    }
    private void writeColumnNameControl(ResponseWriter writer, Column column, int i, String clientId, boolean naming) throws IOException {
        writer.startElement(HTML.TD_ELEM, null);
        writer.startElement(HTML.INPUT_ELEM, null);
        writer.writeAttribute(HTML.TYPE_ATTR, "text", null);
        if (!naming || !column.isConfigurable())
            writer.writeAttribute(HTML.DISABLED_ATTR, "disabled", null);
        writer.writeAttribute(HTML.NAME_ATTR, clientId+"_head_"+i, null);
        writer.writeAttribute(HTML.VALUE_ATTR, column.getHeaderText(), null);
        writer.endElement(HTML.INPUT_ELEM);
        writer.endElement(HTML.TD_ELEM);
    }


}
