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
import org.icefaces.ace.component.column.IProxiableColumn;
import org.icefaces.ace.component.panelexpansion.PanelExpansion;
import org.icefaces.ace.component.row.Row;
import org.icefaces.ace.component.rowexpansion.RowExpansion;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.model.table.TreeDataModel;
import org.icefaces.ace.util.HTML;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataTableRowRenderer {
    protected static boolean encodeRow(FacesContext context, DataTableRenderingContext tableContext, String clientId, int rowIndex, String parentIndex, boolean topVisibleRowRendered) throws IOException {
        DataTable table = tableContext.getTable();

        table.setRowIndex(rowIndex);

        if (!table.isRowAvailable()) return false;

        if (tableContext.getRowIndexVar() != null)
            context.getExternalContext().getRequestMap().put(tableContext.getRowIndexVar(), rowIndex);

        RowState rowState = tableContext.getStateMap().get(table.getRowData());
        boolean selected = rowState.isSelected();
        boolean unselectable = !rowState.isSelectable();
        boolean expanded = rowState.isExpanded();
        boolean visible = rowState.isVisible();
        List<String> selectedColumnIds = rowState.getSelectedColumnIds();

        context.getExternalContext().getRequestMap().put(tableContext.getRowStateVar(), rowState);

		ResponseWriter writer = context.getResponseWriter();

        if (visible) {
			// Add leading conditional row for this row object if required
			List<Row> leadingRows = table.getConditionalRows(rowIndex, true);
			for (Row r : leadingRows) encodeConditionalRow(context, tableContext, r);
		}

		String userRowStyleClass = table.getRowStyleClass();
		String expandedClass = expanded ? DataTableConstants.EXPANDED_ROW_CLASS : "";
		String unselectableClass = unselectable ? DataTableConstants.UNSELECTABLE_ROW_CLASS : "";
		String rowStyleClass = rowIndex % 2 == 0
				? DataTableConstants.ROW_CLASS + " " + DataTableConstants.EVEN_ROW_CLASS
				: DataTableConstants.ROW_CLASS + " " + DataTableConstants.ODD_ROW_CLASS;

		if (selected)
			rowStyleClass = rowStyleClass + " ui-selected ui-state-active";
		if (userRowStyleClass != null)
			rowStyleClass = rowStyleClass + " " + userRowStyleClass;

		writer.startElement(HTML.TR_ELEM, null);
		parentIndex = (parentIndex != null) ? parentIndex + "." : "";
		writer.writeAttribute(HTML.ID_ATTR, clientId + "_row_" + parentIndex + rowIndex, null);

        boolean innerTdDivRequired = ((tableContext.isScrollable() || tableContext.isResizableColumns()) & !topVisibleRowRendered);

        if (visible) {
            writer.writeAttribute(HTML.CLASS_ATTR, rowStyleClass + " " + expandedClass + " " + unselectableClass, null);
            if (table.isRenderRowTabindex()) {
                writer.writeAttribute(HTML.TABINDEX_ATTR, tableContext.getTabIndex(), null);
            }

            //TODO DONE groupBy, stacked, etc.
            List<IProxiableColumn> cols = tableContext.getProxiedBodyColumns();
			int visibleIndex = 0;
            for (int i = 0; i < cols.size(); i++) {
                IProxiableColumn kid = cols.get(i);
                if (kid.isRendered()) {
                    encodeRegularCell(new CellRenderingContext(
                            context, cols, i, visibleIndex,
                            selectedColumnIds.contains(kid.getId()),
                            innerTdDivRequired)
                    );
					visibleIndex++;
                }
            }
		} else {
			writer.writeAttribute(HTML.STYLE_ATTR, "display:none;", null);
		}

		if (tableContext.getRowIndexVar() != null)
			context.getExternalContext().getRequestMap().put(tableContext.getRowIndexVar(), rowIndex);
		writer.endElement(HTML.TR_ELEM);

		boolean isPanel = table.getPanelExpansion() != null;
		boolean isRow = table.getRowExpansion() != null;

		context.getExternalContext().getRequestMap()
				.put(clientId + "_expandedRowId", "" + rowIndex);

		// Ensure that table.getTableId returns correctly for request map look
		if (isPanel || isRow) table.setRowIndex(-1);

		if (isPanel && isRow) {
			if (rowState.getExpansionType() == RowState.ExpansionType.ROW) {
				encodeRowExpansion(context, tableContext, writer, expanded);
			}
			else if (rowState.getExpansionType() == RowState.ExpansionType.PANEL) {
				encodeRowPanelExpansion(context, table, expanded, true);
			}
		} else if (isPanel) {
			encodeRowPanelExpansion(context, table, expanded, true);
		} else if (isRow) {
			encodeRowExpansion(context, tableContext, writer, expanded);
		}

		// Row index will have come back different from row expansion.
		if (isPanel || isRow) table.setRowIndex(rowIndex);

        if (visible) {
			// Add tailing conditional row for this row object if required
			List<Row> tailingRows = table.getConditionalRows(rowIndex, false);
			for (Row r : tailingRows) encodeConditionalRow(context, tableContext, r);

			return innerTdDivRequired;
		}

        return false;
    }

    private static void encodeConditionalRow(FacesContext context, DataTableRenderingContext tableContext, Row r) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement(HTML.TR_ELEM, null);

        if (r.getStyle() != null)
            writer.writeAttribute(HTML.STYLE_ATTR, r.getStyle(), null);

        if (r.getStyleClass() != null)
            writer.writeAttribute(HTML.CLASS_ATTR,
                DataTableConstants.CONDITIONAL_ROW_CLASS + " " + r.getStyleClass(), null);
        else
            writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.CONDITIONAL_ROW_CLASS, null);

        List<UIComponent> children = r.getChildren();
        List<Column> rowColumns = new ArrayList<Column>(children.size());
        for (UIComponent kid : children)
            if (kid instanceof Column)
                rowColumns.add((Column)kid);

        tableContext.getTable().setInConditionalRow(true, rowColumns);
        for (Column kid : rowColumns)
            if (kid.isRendered())
                encodeConditionalRowCell(context, kid);
        tableContext.getTable().setInConditionalRow(false, rowColumns);

        writer.endElement(HTML.TR_ELEM);
    }

    private static void encodeConditionalRowCell(FacesContext context, Column c) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement(HTML.TD_ELEM, null);
        writer.writeAttribute(HTML.COLSPAN_ATTR, c.getColspan(), null);
        if (c.getStyle() != null) writer.writeAttribute(HTML.STYLE_ATTR, c.getStyle(), null);
        if (c.getStyleClass() != null) writer.writeAttribute(HTML.CLASS_ATTR, c.getStyleClass(), null);

        c.encodeAll(context);
        writer.endElement(HTML.TD_ELEM);
    }

    private static void encodeRegularCell(CellRenderingContext cellContext) throws IOException {
        List<IProxiableColumn> columns = cellContext.columns;
        IProxiableColumn column = columns.get(cellContext.index);
        ResponseWriter writer = cellContext.context.getResponseWriter();

        boolean isCurrStacked = DataTableRendererUtil.isCurrColumnStacked(columns, column);
        boolean isCurrGrouped = column.getCurrGroupLength() > 0;

        boolean isNextStacked = DataTableRendererUtil.isNextStacked(columns, column);

        boolean isNextGrouped = isCurrGrouped ? false // No need to calculate next group if grouped
                : DataTableRendererUtil.isNextColumnGrouped(column);

        if (isCurrGrouped)
            column.setCurrGroupLength(column.getCurrGroupLength()-1);
        else {
            if (!isCurrStacked) {
                writer.startElement(HTML.TD_ELEM, null);

                if (column.getStyle() != null)
                    writer.writeAttribute(HTML.STYLE_ELEM, column.getStyle(), null);

                if (isNextGrouped)
                    writer.writeAttribute(HTML.ROWSPAN_ATTR, DataTableRendererUtil.findCurrGroupLength(column)+1, null);

                String columnStyleClass = "ui-col-"+cellContext.visibleIndex;

                if (column.getStyleClass() != null)
                    columnStyleClass += " " + column.getStyleClass();

                if (column.hasCellEditor())
                    columnStyleClass += " " + DataTableConstants.EDITABLE_COLUMN_CLASS;

                // Add alternating styling, except when last group is the same value, split by an expansion or conditional row
                if (column.getValueExpression("groupBy") != null) {
                    if (DataTableRendererUtil.isLastGroupDifferent(column))
                        column.setOddGroup(!column.isOddGroup());

                    columnStyleClass += column.isOddGroup()
                            ? " ui-datatable-group-odd"
                            : " ui-datatable-group-even";
                }

                if (cellContext.selected)
                    columnStyleClass += " ui-state-active ui-selected";

                writer.writeAttribute(HTML.CLASS_ATTR, columnStyleClass, null);

                if (cellContext.resizable) writer.startElement(HTML.DIV_ELEM, null);
            }
            else {
                writer.startElement("hr", null);
                writer.endElement("hr");
            }

            column.encodeAll(cellContext.context);

            if (!isNextStacked) {
                if (cellContext.resizable) writer.endElement(HTML.DIV_ELEM);
                writer.endElement(HTML.TD_ELEM);
            }
        }
    }

    private static void encodeRowExpansion(FacesContext context, DataTableRenderingContext tableContext, ResponseWriter writer, boolean display) throws IOException {
        DataTable table = tableContext.getTable();

        String rowVar = tableContext.getVar();
        String rowIndexVar = tableContext.getRowIndexVar();
        String clientId = table.getClientId(context);

        String expandedRowId = context.getExternalContext()
                .getRequestParameterMap().get(clientId + "_expandedRowId");

        if (expandedRowId == null) {
            expandedRowId = (String) context.getExternalContext()
                    .getRequestMap().get(clientId + "_expandedRowId");
        }

        Object model = table.getDataModel();

        if (!(table.hasTreeDataModel()))
            throw new FacesException("DataTable : \"" + clientId +
                    "\" must be bound to an instance of TreeDataModel when using sub-row expansion.");

        TreeDataModel rootModel = (TreeDataModel)model;
        rootModel.setRootIndex(expandedRowId);
        if (display) tableContext.getStateMap().get(rootModel.getRootData()).setExpanded(true);
        table.setRowIndex(0);

        if (rootModel.getRowCount() > 0)
            while (rootModel.getRowIndex() < rootModel.getRowCount()) {
                if (rowVar != null) context.getExternalContext()
                        .getRequestMap().put(rowVar, rootModel.getRowData());
                if (rowIndexVar != null) context.getExternalContext()
                        .getRequestMap().put(rowIndexVar, rootModel.getRowIndex());

                RowState rowState = tableContext.getStateMap().get(rootModel.getRowData());
                boolean selected = rowState.isSelected();
                boolean expanded = rowState.isExpanded();
                boolean unselectable = !rowState.isSelectable();
                boolean visible = rowState.isVisible();
                List<String> selectedColumnIds = rowState.getSelectedColumnIds();

                context.getExternalContext().getRequestMap()
                        .put(tableContext.getRowStateVar(), rowState);

                String expandedClass = expanded ? DataTableConstants.EXPANDED_ROW_CLASS : "";
                String alternatingClass = (rootModel.getRowIndex() % 2 == 0)
                        ? DataTableConstants.EVEN_ROW_CLASS
                        : DataTableConstants.ODD_ROW_CLASS;

                String selectionClass = (selected && tableContext.getSelectionMode() != null)
                        ? "ui-selected ui-state-active" : "";
                String unselectableClass = unselectable
                        ? DataTableConstants.UNSELECTABLE_ROW_CLASS : "";

				writer.startElement(HTML.TR_ELEM, null);
				writer.writeAttribute(HTML.ID_ATTR,
						clientId + "_row_" + expandedRowId + "." + rootModel.getRowIndex(), null);

                if (visible && display) {
                    writer.writeAttribute(HTML.CLASS_ATTR,
                            DataTableConstants.ROW_CLASS + " " + alternatingClass + " " + selectionClass + " " + expandedClass + " " + unselectableClass, null);

                    List<IProxiableColumn> cols = tableContext.getProxiedBodyColumns();
					int visibleIndex = 0;
                    for (int i = 0; i < cols.size(); i++) {
                        IProxiableColumn kid = cols.get(i);
                        if (kid.isRendered()) {
                            boolean cellSelected = false;
                            encodeRegularCell(new CellRenderingContext(context, cols, i, visibleIndex, selectedColumnIds.contains(kid.getId()), false));
							visibleIndex++;
                        }
                    }
                } else {
					writer.writeAttribute(HTML.STYLE_ATTR, "display:none;", null);
				}

				writer.endElement(HTML.TR_ELEM);

				int rowIndex = rootModel.getRowIndex();
				context.getExternalContext().getRequestMap().put(clientId + "_expandedRowId", expandedRowId+"."+rowIndex);

				PanelExpansion panelExpansion = table.getPanelExpansion();
				RowExpansion rowExpansion = table.getRowExpansion();
				boolean isPanel = panelExpansion != null;
				boolean isRow = rowExpansion != null;

				// Ensure that table.getTableId returns correctly for request map look
				if (isPanel || isRow) table.setRowIndex(-1);

				if (isPanel && isRow) {
					if (rowState.getExpansionType() == RowState.ExpansionType.ROW) {
						encodeRowExpansion(context, tableContext, writer, expanded);
					}
					else if (rowState.getExpansionType() == RowState.ExpansionType.PANEL) {
						encodeRowPanelExpansion(context, table, expanded, false);
					}
				} else if (isPanel) {
					encodeRowPanelExpansion(context, table, expanded, false);
				} else if (isRow) {
					encodeRowExpansion(context, tableContext, writer, expanded);
				}

				rootModel = (TreeDataModel) table.getDataModel();
				rootModel.setRootIndex(expandedRowId);
				if (isPanel || isRow) table.setRowIndex(rowIndex); // Row index will have come back different from row expansion.
				context.getExternalContext().getRequestMap().put(clientId + "_expandedRowId", expandedRowId);

                table.setRowIndex(rootModel.getRowIndex() + 1);
                if (rowIndexVar != null) context.getExternalContext().getRequestMap().remove(rowIndexVar);
                if (rowVar != null) context.getExternalContext().getRequestMap().remove(rowVar);
            }

        rootModel.setRootIndex(null);
        table.setRowIndex(-1);
    }

    private static void encodeRowPanelExpansion(FacesContext context, DataTable table, boolean display, boolean isRootRow) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = table.getClientId(context);
        Object model = table.getDataModel();


        String expandedRowId = params.get(clientId + "_expandedRowId");
        if (expandedRowId == null) {
            expandedRowId = (String) context.getExternalContext().getRequestMap().get(clientId + "_expandedRowId");
        }
		String originalExpandedRowId = expandedRowId;

        int sepIndex = expandedRowId.lastIndexOf('.');
        String rootIndex = null;
        if (sepIndex >= 0) {
            rootIndex = expandedRowId.substring(0,sepIndex);
            expandedRowId = expandedRowId.substring(sepIndex+1);
        }

        if (rootIndex != null) ((TreeDataModel)model).setRootIndex(rootIndex);
        table.setRowIndex(Integer.parseInt(expandedRowId));

        if (display) table.getStateMap().get(table.getRowData()).setExpanded(true);

        writer.startElement(HTML.TR_ELEM, null);
		writer.writeAttribute(HTML.ID_ATTR, clientId + "_exp_" + originalExpandedRowId, null);

		if (display) {
			writer.writeAttribute(HTML.CLASS_ATTR, DataTableConstants.EXPANDED_ROW_CONTENT_CLASS + " ui-widget-content " + DataTableConstants.UNSELECTABLE_ROW_CLASS , null);

			writer.startElement(HTML.TD_ELEM, null);

			int enabledColumns = 0;
			for (Column c : table.getColumns()) if (c.isRendered() && !c.isStacked()) enabledColumns++;

			writer.writeAttribute(HTML.COLSPAN_ATTR, enabledColumns, null);
			table.getPanelExpansion().encodeAll(context);

			writer.endElement(HTML.TD_ELEM);
		} else {
			writer.writeAttribute(HTML.STYLE_ATTR, "display:none;", null);
		}

        writer.endElement(HTML.TR_ELEM);
        table.setRowIndex(-1);
    }

    private static class CellRenderingContext {
        FacesContext context;
        List<IProxiableColumn> columns;
        int index;
		int visibleIndex;
        boolean selected;
        boolean resizable;

        public CellRenderingContext(FacesContext context, List<IProxiableColumn> columns, int index, int visibleIndex, boolean selected, boolean innerDiv) {
            this.context = context;
            this.columns = columns;
            this.index = index;
			this.visibleIndex = visibleIndex;
            this.selected = selected;
            this.resizable = innerDiv;
        }
    }
}
