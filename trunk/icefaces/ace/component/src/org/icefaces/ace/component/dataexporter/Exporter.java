/*
 * Original Code Copyright Prime Technology.
 * Subsequent Code Modifications Copyright 2011-2014 ICEsoft Technologies Canada Corp. (c)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE THIS CODE HAS BEEN MODIFIED FROM ORIGINAL FORM
 *
 * Subsequent Code Modifications have been made and contributed by ICEsoft Technologies Canada Corp. (c).
 *
 * Code Modification 1: Integrated with ICEfaces Advanced Component Environment.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 *
 * Code Modification 2: [ADD BRIEF DESCRIPTION HERE]
 * Contributors: ______________________
 * Contributors: ______________________
 */
package org.icefaces.ace.component.dataexporter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.columngroup.ColumnGroup;
import org.icefaces.ace.component.row.Row;
import org.icefaces.ace.component.expansiontoggler.ExpansionToggler;
import org.icefaces.ace.component.excludefromexport.ExcludeFromExport;
import org.icefaces.ace.component.celleditor.CellEditor;
import org.icefaces.ace.component.panelexpansion.PanelExpansion;
import org.icefaces.ace.model.table.RowState;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.ace.model.table.TreeDataModel;

import org.icefaces.ace.component.list.ACEList;
import org.icefaces.impl.component.Repeat;
import org.icefaces.impl.component.VarStatus;

import org.icefaces.application.ResourceRegistry;

import java.util.logging.Logger;

public abstract class Exporter {

	protected final static Logger logger = Logger.getLogger(Exporter.class.getName());

	protected static final Pattern HTML_TAG_PATTERN = Pattern.compile("\\<.*?\\>");
	protected SpanningRows spanningRows = this.new SpanningRows();
	protected SpanningRows spanningFooterRows = this.new SpanningRows();
	protected String filename;
	protected boolean pageOnly;
	protected int[] excludeColumns;
	protected String encodingType;
	protected MethodExpression preProcessor;
	protected MethodExpression postProcessor;
	protected boolean includeHeaders;
	protected boolean includeFooters;
	protected boolean selectedRowsOnly;
	protected boolean userColumnOrder;
	protected boolean expandedOnly;
	protected String pdfFont;
	private HashMap<Row, List<UIColumn>> headerRowColumns = new HashMap<Row, List<UIColumn>>();
	
	public void setUp(DataExporter component, DataTable table) {
		filename = component.getFileName();
		if (filename == null) {
			filename = "data";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Required attribute 'file' is null in ace:dataExporter component with id "+component.getId()+" in view "+FacesContext.getCurrentInstance().getViewRoot().getViewId()+".");
		}
		pageOnly = component.isPageOnly() || table.isLazy();
		excludeColumns = resolveExcludedColumnIndexes(component.getExcludeColumns());
		encodingType = component.getEncoding();
		preProcessor = component.getPreProcessor();
		postProcessor = component.getPostProcessor();
		includeHeaders = component.isIncludeHeaders();
		includeFooters = component.isIncludeFooters();
		selectedRowsOnly = component.isSelectedRowsOnly();
		userColumnOrder = component.isUserColumnOrder();
		expandedOnly = component.isExpandedOnly();
		pdfFont = component.getPdfFont();
	}

    public abstract String export(FacesContext facesContext, DataExporter component, DataTable table) throws IOException;
	
	protected List<UIColumn> getColumnsToExport(UIData table, int[] excludedColumns) {
        List<UIColumn> columns = new ArrayList<UIColumn>();
        int columnIndex = -1;

        
		List<UIComponent> children;
		if (table instanceof DataTable && userColumnOrder) {
			List<Column> dataTablecolumns = ((DataTable) table).getColumns();
			children = new ArrayList<UIComponent>();
			for (Column column : dataTablecolumns) {
				children.add(column);
			}
		} else {
			children = table.getChildren();
		}

		for (UIComponent child : children) {
            if (child instanceof UIColumn && !(child instanceof PanelExpansion)) {
				if (shouldExcludeFromExport(child)) continue;
                UIColumn column = (UIColumn) child;
                columnIndex++;

				boolean hasExpansionToggler = false;
                for (UIComponent columnChild : column.getChildren())
					if (columnChild instanceof ExpansionToggler) hasExpansionToggler = true;
				if (hasExpansionToggler) continue;
				
				if (column.isRendered()) {
					if (excludedColumns == null || Arrays.binarySearch(excludedColumns, columnIndex) < 0) {
						columns.add(column);
					}
				}
            }
        }
        return columns;
    }
	
	protected ColumnGroup getColumnGroupHeader(UIData table) {
	
		for (UIComponent child : table.getChildren()) {
			if (child instanceof ColumnGroup) {
				if (shouldExcludeFromExport(child)) continue;
				ColumnGroup columnGroup = (ColumnGroup) child;
				if (columnGroup.getType() != null && columnGroup.getType().equalsIgnoreCase("header"))
					return columnGroup;
			}
		}
		
		return null;
	}

	protected ColumnGroup getColumnGroupFooter(UIData table) {
	
		for (UIComponent child : table.getChildren()) {
			if (child instanceof ColumnGroup) {
				if (shouldExcludeFromExport(child)) continue;
				ColumnGroup columnGroup = (ColumnGroup) child;
				if (columnGroup.getType() != null && columnGroup.getType().equalsIgnoreCase("footer"))
					return columnGroup;
			}
		}
		
		return null;
	}

    protected boolean hasColumnFooter(List<UIColumn> columns) {
        for (UIColumn column : columns) {
            if (column.getFooter() != null) return true;
			if (column instanceof Column) {
				if (((Column) column).getFooterText() != null) return true;
			}
		}

		return false;
	}

	protected List<Row> getRows(ColumnGroup columnGroup) {
		return getRows(columnGroup, false);
	}
	
	protected List<Row> getRows(ColumnGroup columnGroup, boolean includeAll) {
		List<Row> rows = new ArrayList<Row>();
		
		for (UIComponent child : columnGroup.getChildren()) {
			if (child instanceof Row) {
				if (!includeAll && shouldExcludeFromExport(child)) continue;
				Row row = (Row) child;
				rows.add(row);
			}
		}
		
		return rows;
	}
	
	protected void determineHeaderColumnOrdering(List<Row> rows, DataTable table) {
		// for each row, take note of the number of columns it has
		// make list of all columns in the header
		// get header ordering
		// create map of row and columns, assigning the ordered columns to the rows
		// in getRowColumnsToExport() get columns from map
		ArrayList<UIColumn> columns = new ArrayList<UIColumn>();
		ArrayList<Integer> sizes = new ArrayList<Integer>();
		for (Row row : rows) {
			int size = 0;
			for (UIComponent child : row.getChildren()) {
				if (child instanceof UIColumn) {
					columns.add((UIColumn) child);
					size++;
				}
			}
			sizes.add(size);
		}
		List<Integer> ordering = table.getHeaderColumnOrdering();
		ArrayList<UIColumn> newColumns = new ArrayList<UIColumn>();
		if (ordering == null || ordering.size() == 0) {
			newColumns = columns;
		} else {
			if (columns.size() == ordering.size()) {
				for (Integer integer : ordering) {
					newColumns.add(columns.get(integer.intValue()));
				}
			} else {
				newColumns = columns;
				logger.warning("Header column ordering list size does not match number of defined columns in table " + table.getId() + ".");
			}
		}
		int index = 0;
		for (int i = 0; i < rows.size(); i++) {
			ArrayList<UIColumn> rowColumns = new ArrayList<UIColumn>();
			for (int j = 0; j < sizes.get(i); j++) {
				rowColumns.add(newColumns.get(index));
				index++;
			}
			headerRowColumns.put(rows.get(i), rowColumns);
		}
	}
	
	protected List<UIColumn> getRowColumnsToExport(Row row, UIData table, int[] excludedColumns) {
        List<UIColumn> columns = new ArrayList<UIColumn>();
		ArrayList<UIColumn> rowColumns = new ArrayList<UIColumn>();
        int columnIndex = -1;
		int rowColumnIndex = 0;

		SpanningRow spanningRow = spanningRows.getNextRow(); // fetch the set of previous columns that span to this row
		int stackedCount = 0;
        for (UIColumn uiColumn : headerRowColumns.get(row)) {
			if (shouldExcludeFromExport(uiColumn)) continue;
			if (uiColumn.isRendered()) {
				if (uiColumn instanceof Column) {

					int added = 0;
					if (spanningRow != null) { // add previous columns that span multiple rows
						added = spanningRow.addColumnsTo(rowColumns, rowColumnIndex-stackedCount);
						rowColumnIndex = rowColumnIndex + added;
					}
					Column column = (Column) uiColumn;
					int colspan = column.getColspan();
					int rowspan = column.getRowspan();
					if (column.isStacked()) {
						stackedCount++;
						if (added > 0) {
							rowColumns.add(rowColumnIndex-added, column);
						} else {
							rowColumns.add(column);
						}
						for (int j = 1; j < rowspan; j++) { // register which columns span multiple rows
							spanningRows.addColumn(column, j-1, rowColumnIndex-added);
						}
						rowColumnIndex++;
					} else {
						for (int i = 0; i < colspan; i++) {
							rowColumns.add(column);
							for (int j = 1; j < rowspan; j++) { // register which columns span multiple rows
								spanningRows.addColumn(column, j-1, rowColumnIndex);
							}
							rowColumnIndex++;
						}
					}
				} else {
					rowColumns.add(uiColumn);
					rowColumnIndex++;
				}
			}
		}
		if (spanningRow != null) { // add previous columns that span multiple rows, at the end of the row
			spanningRow.addColumnsTo(rowColumns, rowColumnIndex-stackedCount);
		}
		rowColumnIndex = -1;
		
		List<UIComponent> children;
		if (table instanceof DataTable && userColumnOrder) {
			List<Column> dataTablecolumns = ((DataTable) table).getColumns();
			children = new ArrayList<UIComponent>();
			for (Column column : dataTablecolumns) {
				children.add(column);
			}
		} else {
			children = table.getChildren();
		}
		for (UIComponent child : children) {
            if (child instanceof UIColumn && !(child instanceof PanelExpansion)) {
				if (shouldExcludeFromExport(child)) continue;
                UIColumn column = (UIColumn) child;
                columnIndex++;

				boolean hasExpansionToggler = false;
                for (UIComponent columnChild : column.getChildren())
					if (columnChild instanceof ExpansionToggler) hasExpansionToggler = true;
				if (hasExpansionToggler) {
					rowColumnIndex++;
					continue;
				}
				
				if (column.isRendered()) {
					rowColumnIndex++;
					if (excludedColumns == null || Arrays.binarySearch(excludedColumns, columnIndex) < 0) {
						if (column instanceof Column && ((Column) column).isStacked()) {
							boolean isRowColumnStacked = false;
							if (rowColumnIndex < rowColumns.size()) {
								UIColumn rowColumn = rowColumns.get(rowColumnIndex);
								if (rowColumn != null && rowColumn instanceof Column && ((Column) rowColumn).isStacked()) {
									columns.add(rowColumn);
									isRowColumnStacked = true;
								}
							}
							if (isRowColumnStacked) continue;
							else rowColumnIndex--;
						}
						if (rowColumnIndex < rowColumns.size()) {
							UIColumn rowColumn = rowColumns.get(rowColumnIndex);
							if (rowColumn != null) {
								columns.add(rowColumn);
								continue;
							}
						}
						columns.add(new UIColumn());
					}
				}
            }
        }
        return columns;
    }

	protected List<UIColumn> getFooterRowColumnsToExport(Row row, UIData table, int[] excludedColumns) {
        List<UIColumn> columns = new ArrayList<UIColumn>();
		ArrayList<UIColumn> rowColumns = new ArrayList<UIColumn>();
        int columnIndex = -1;
		int rowColumnIndex = 0;

		SpanningRow spanningRow = spanningFooterRows.getNextRow(); // fetch the set of previous columns that span to this row
        for (UIComponent child : row.getChildren()) {
			if (child instanceof UIColumn) {
				if (shouldExcludeFromExport(child)) continue;
				UIColumn uiColumn = (UIColumn) child;
				if (uiColumn.isRendered()) {
					if (uiColumn instanceof Column) {

						if (spanningRow != null) { // add previous columns that span multiple rows
							int added = spanningRow.addColumnsTo(rowColumns, rowColumnIndex);
							rowColumnIndex = rowColumnIndex + added;
						}
						Column column = (Column) uiColumn;
						int colspan = column.getColspan();
						int rowspan = column.getRowspan();
						for (int i = 0; i < colspan; i++) {
							rowColumns.add(column);
							for (int j = 1; j < rowspan; j++) { // register which columns span multiple rows
								spanningFooterRows.addColumn(column, j-1, rowColumnIndex);
							}
							rowColumnIndex++;
						}
					} else {
						rowColumns.add(uiColumn);
						rowColumnIndex++;
					}
				}
			}
		}
		
		if (spanningRow != null) spanningRow.addColumnsTo(rowColumns, rowColumnIndex);
		rowColumnIndex = -1;
		
		for (UIComponent child : table.getChildren()) {
            if (child instanceof UIColumn && !(child instanceof PanelExpansion)) {
				if (shouldExcludeFromExport(child)) continue;
                UIColumn column = (UIColumn) child;
                columnIndex++;

				boolean hasExpansionToggler = false;
                for (UIComponent columnChild : column.getChildren())
					if (columnChild instanceof ExpansionToggler) hasExpansionToggler = true;
				if (hasExpansionToggler) {
					rowColumnIndex++;
					continue;
				}
				
				if (column.isRendered()) {
					rowColumnIndex++;
					if (excludedColumns == null || Arrays.binarySearch(excludedColumns, columnIndex) < 0) {
						if (rowColumnIndex < rowColumns.size()) {
							UIColumn rowColumn = rowColumns.get(rowColumnIndex);
							if (rowColumn != null) {
								columns.add(rowColumn);
								continue;
							}
						}
						columns.add(new UIColumn());
					}
				}
            }
        }
        return columns;
    }
	
	protected boolean shouldExcludeFromExport(UIComponent component) {
	
		for (UIComponent child : component.getChildren()) {
            if (child instanceof ExcludeFromExport) {	
				if (child.isRendered()) return true;
			}
		}
		return false;
	}
	
	protected int[] resolveExcludedColumnIndexes(String columnsToExclude) {
        if (columnsToExclude == null || columnsToExclude.equals("")) return null;

        String[] columnIndexesAsString = columnsToExclude.split(",");
        int[] indexes = new int[columnIndexesAsString.length];
        for (int i=0; i < indexes.length; i++)
            indexes[i] = Integer.parseInt(columnIndexesAsString[i].trim());

        return indexes;
	}

    protected String exportValue(FacesContext context, UIComponent component) {
		if (shouldExcludeFromExport(component) || !component.isRendered()) return "";
		if (component instanceof CellEditor) {
			UIComponent facet = component.getFacet("output");
			if (facet != null) {
				component = facet;
			} else {
				facet = component.getFacet("input");
				if (facet != null) {
					component = facet;
				}				
			}
        }
		if (component instanceof HtmlCommandLink) {
            HtmlCommandLink link = (HtmlCommandLink) component;
            Object value = link.getValue();

            if (value != null) return String.valueOf(value);
            else {
                //export first value holder
                for (UIComponent child : link.getChildren())
                    if (child instanceof ValueHolder)
                        return exportValue(context, child);
                return null;
            }

        } else if (component instanceof EditableValueHolder) {
			EditableValueHolder editableValueHolder = (EditableValueHolder) component;
            Object value = editableValueHolder.getValue();
			
			if (value == null) return "";
			
            else if (editableValueHolder.getConverter() != null)
                return editableValueHolder.getConverter().getAsString(context, component, value);

            Class<?> valueType;
            ValueExpression expr = component.getValueExpression("value");
            if (expr != null) if ((valueType = expr.getType(context.getELContext())) != null) {
                Converter converterForType = context.getApplication().createConverter(valueType);
                if (converterForType != null) return converterForType.getAsString(context, component, value);
            }
			
			return value.toString();

        } else if (component instanceof ValueHolder) {
			ValueHolder valueHolder = (ValueHolder) component;
			Object value = valueHolder.getValue();

            if (value == null) return "";

            else if (valueHolder.getConverter() != null)
                return valueHolder.getConverter().getAsString(context, component, value);

			try {
				Class<?> valueType;
				ValueExpression expr = component.getValueExpression("value");
				if (expr != null) if ((valueType = expr.getType(context.getELContext())) != null) {
					Converter converterForType = context.getApplication().createConverter(valueType);
					if (converterForType != null) return converterForType.getAsString(context, component, value);
				}
			} catch (Exception e) {}

			return value.toString();
		} else if (component instanceof DataTable) {
			DataTable table = (DataTable) component;

			Object model = table.getModel();
			TreeDataModel rootModel = null;
			boolean hasRowExpansion = false;
			if (model != null && table.hasTreeDataModel()) {
				rootModel = (TreeDataModel) model;
				hasRowExpansion = true;
			}

			int rowCount = table.getRowCount();
			int first = table.getFirst();
			int rows = table.getRows();
			int size = rows > 0 ? (first + rows) : rowCount;
			size = size > rowCount ? rowCount : size;

			String rowIndexVar = table.getRowIndexVar();
			rowIndexVar = rowIndexVar == null ? "" : rowIndexVar;

			StringBuilder builder = new StringBuilder();
			String rowVar = table.getVar();
			Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
			for (int i = 0; i < size; i++) {
				table.setRowIndex(i);
				Object rowData = table.getRowData();
                if (rowVar != null) requestMap.put(rowVar, rowData);
				if (!"".equals(rowIndexVar)) {
					requestMap.put(rowIndexVar, i);
				}

				// 'before' conditional rows
				List<Row> leadingRows = table.getConditionalRows(i, true);
				for (Row r : leadingRows) exportConditionalRowValues(builder, r);

				for (UIComponent child : table.getChildren()) {
					if (child instanceof UIColumn) {
						builder.append(exportValue(context, child));
						builder.append(" ");
					}
				}

				if (hasRowExpansion) {
					exportChildRowValues(context, rootModel, table.getStateMap(), table, "" + i, builder);
				}

				// 'after' conditional rows
				List<Row> tailingRows = table.getConditionalRows(i, false);
				for (Row r : tailingRows) exportConditionalRowValues(builder, r);
				builder.append(" ");
			}
			requestMap.remove(rowVar);
			return builder.toString();
        } else if (component instanceof ACEList) {
			ACEList list = (ACEList) component;

			StringBuilder builder = new StringBuilder();
			int rowCount = list.getRowCount();
			String rowVar = list.getVar();
			Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
			for (int i = 0; i < rowCount; i++) {
				list.setRowIndex(i);
				Object rowData = list.getRowData();
                if (rowVar != null) requestMap.put(rowVar, rowData);

				for (UIComponent child : list.getChildren()) {
					builder.append(exportValue(context, child));
					builder.append(" ");
				}
				builder.append(" ");
			}
			requestMap.remove(rowVar);
			return builder.toString();
        } else if (component instanceof Repeat) {
			Repeat repeat = (Repeat) component;

			StringBuilder builder = new StringBuilder();
			int rowCount = repeat.getRowCount();
			int rows = repeat.getRows();
			int first = repeat.getFirst();
			first = first > rowCount ? 0 : first;
			if (rows == 0 || rows + first > rowCount) {
				rows = rowCount - first;
			}
			int last = first + rows - 1;
			String rowVar = repeat.getVar();
			String varStatus = repeat.getVarStatus();
			Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
			for (int i = 0; i < rowCount; i++) {
				repeat.setRowIndex(i);
				Object rowData = repeat.getRowData();
                if (rowVar != null) requestMap.put(rowVar, rowData);
                if (varStatus != null) requestMap.put(varStatus, new VarStatus(first, last, i));

				for (UIComponent child : repeat.getChildren()) {
					builder.append(exportValue(context, child));
					builder.append(" ");
				}
				builder.append(" ");
			}
			requestMap.remove(rowVar);
			requestMap.remove(varStatus);
			return builder.toString();
        } else if (component instanceof UIData) {
			UIData uiData = (UIData) component;

			StringBuilder builder = new StringBuilder();
			int rowCount = uiData.getRowCount();
			String rowVar = uiData.getVar();
			Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
			for (int i = 0; i < rowCount; i++) {
				uiData.setRowIndex(i);
				Object rowData = uiData.getRowData();
                if (rowVar != null) requestMap.put(rowVar, rowData);

				for (UIComponent child : uiData.getChildren()) {
					if (child instanceof UIColumn) {
						builder.append(exportValue(context, child));
						builder.append(" ");
					}
				}
				builder.append(" ");
			}
			requestMap.remove(rowVar);
			return builder.toString();
		}
        
		String ret = "";
        //This would get the plain texts on UIInstructions when using Facelets
        String value = component.toString();
        if (value != null) {
			value = value.trim();
			String objectReference = component.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(component));
			if (!value.equals(objectReference)) ret = value;
		}
		
		// strip HTML tags
		ret = HTML_TAG_PATTERN.matcher(ret).replaceAll("");
		ret = (context.getApplication().evaluateExpressionGet(context, ret, Object.class)).toString();
		
		if (component.getChildren().size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (UIComponent child : component.getChildren()) {
				builder.append(exportValue(context, child));
			}
			ret += builder.toString();
		}
        return ret;
    }

	protected void exportConditionalRowValues(StringBuilder builder, Row row) {
		List<UIComponent> children = row.getChildren();
		List<UIColumn> rowColumns = new ArrayList<UIColumn>(children.size());
		for (UIComponent kid : children) {
			if (kid instanceof Column) {
				Column c = (Column) kid;
				int colspan = c.getColspan();
				for (int i = 0; i < colspan; i++) rowColumns.add(c);
			}
		}
		FacesContext context = FacesContext.getCurrentInstance();
		for (UIColumn child : rowColumns) {
			builder.append(exportValue(context, child));
			builder.append(" ");
		}
		builder.append(" ");
	}

	protected void exportChildRowValues(FacesContext context, TreeDataModel rootModel, RowStateMap rowStateMap,
		DataTable table, String rootIndex, StringBuilder builder) {
		rootModel.setRootIndex(rootIndex);
		rootModel.setRowIndex(0);

		RowState rootState = rowStateMap.get(rootModel.getRootData());
		
		String rowVar = table.getVar();
		String rowIndexVar = table.getRowIndexVar();
		Map<String, Object> requestMap = context.getExternalContext().getRequestMap();
		if (rootState.isExpanded() && rootModel.getRowCount() > 0) {
			while (rootModel.getRowIndex() < rootModel.getRowCount()) {
				int rowIndex = rootModel.getRowIndex();
				Object rowData = rootModel.getRowData();
                if (rowVar != null) requestMap.put(rowVar, rowData);
                if (rowIndexVar != null) requestMap.put(rowIndexVar, rowData);
				RowState rowState = rowStateMap.get(rootModel.getRowData());
				
				// export
				for (UIComponent child : table.getChildren()) {
					if (child instanceof UIColumn) {
						builder.append(exportValue(context, child));
						builder.append(" ");
					}
				}
				builder.append(" ");
				
				if (rowState.isExpanded()) {
					
					// recurse
					exportChildRowValues(context, rootModel, rowStateMap, table, rootIndex + "." + rowIndex, builder);
					
					// restore
					rootModel.setRootIndex(rootIndex);
					rootModel.setRowIndex(rowIndex);
				}
                rootModel.setRowIndex(rootModel.getRowIndex() + 1);
                if (rowIndexVar != null) requestMap.remove(rowIndexVar);
                if (rowVar != null) requestMap.remove(rowVar);
			}
		}
		
        rootModel.setRootIndex(null);
	}

	protected String registerResource(byte[] bytes, String filename, String contentType) {
		ExporterResource resource = new ExporterResource(bytes);
		resource.setContentType(contentType);
		Map<String, String> headers = resource.getResponseHeaders();
		headers.put("Expires", "0");
		headers.put("Cache-Control","must-revalidate, post-check=0, pre-check=0");
		headers.put("Pragma", "public");
		headers.put("Content-disposition", "attachment; filename=" + filename);
		String path = ResourceRegistry.addSessionResource(resource);
		return path;
	}

    protected enum ColumnType {
        HEADER("header"),
        FOOTER("footer");
        private final String facet;
        ColumnType(String facet) {
            this.facet = facet;
        }
        public String facet() {
            return facet;
        }
        @Override
        public String toString() {
            return facet;
        }
    }
	
	protected class SpanningRows {
	
		protected ArrayList<SpanningRow> rows = new ArrayList<SpanningRow>();
		protected int currentIndex = 0;
		
		protected SpanningRow getNextRow() {
			if (currentIndex < rows.size()) {
				int index = currentIndex;
				currentIndex++;
				return rows.get(index);
			} else {
				return null;
			}
		}
		
		protected void addColumn(UIColumn column, int to, int index) {
			if ((currentIndex + to) >= rows.size()) {
				rows.add(currentIndex + to, new SpanningRow());
			}
			rows.get(currentIndex + to).addColumn(column, index);
		}
	}
	
	protected static class SpanningRow {
	
		protected ArrayList<SpanningColumn> spanningColumns = new ArrayList<SpanningColumn>();
		
		protected int addColumnsTo(ArrayList<UIColumn> columns, int index) {
			boolean found = false;
			int myIndex = index;
			int added = 0;
			do {
				found = false;
				for (int i = 0; i < spanningColumns.size(); i++) {
					SpanningColumn spanningColumn = spanningColumns.get(i);
					if (spanningColumn.index == myIndex) {
						columns.add(spanningColumn.column);
						added++;
						myIndex++; // try to add the next one
						found = true;
					}
				}
			} while (found);
			return added;
		}
		
		protected void addColumn(UIColumn column, int index) {
			SpanningColumn spanningColumn = new SpanningColumn();
			spanningColumn.column = column;
			spanningColumn.index = index;
			spanningColumns.add(spanningColumn);
		}
	}
	
	protected static class SpanningColumn {
	
		protected UIColumn column;
		protected int index;
	}
}
