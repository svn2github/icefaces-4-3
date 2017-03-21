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
package org.icefaces.ace.component.listexporter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.icefaces.ace.component.list.ACEList;
import org.icefaces.ace.component.listexportervalue.ListExporterValue;

public class CSVExporter extends Exporter {
	
    @Override
	public String export(FacesContext facesContext, ListExporter component, ACEList list) throws IOException {
		setUp(component, list);
		StringBuilder builder = new StringBuilder();
    	
    	if (includeHeaders) {
			// addFacetColumns(builder, columns, ColumnType.HEADER);
		}

		if (listExporterValues.size() > 0) {
			int listExporterValuesSize = listExporterValues.size();
			for (int i = 0; i < listExporterValuesSize; i++) {
				addColumnName(builder, listExporterValues.get(i));
				if (i < (listExporterValuesSize - 1)) builder.append(", ");
			}
			builder.append("\n");
		}
    	
		int rowCount = list.getRowCount();
    	int first = 0;

		final Collection<Object> selections = list.isSelectItemModel() ? (Collection)list.getValue() : list.getSelections();
    	for (int i = first; i < rowCount; i++) {
    		list.setRowIndex(i);
			boolean exportRow = true;
			Object rowData = list.getRowData();

			if (selectedItemsOnly && !selections.contains(rowData)) exportRow = false;

			if (exportRow) {
				if (rowData instanceof SelectItem) {
					addSelectItemValue(builder, (SelectItem) rowData);
				} else {
					if (listExporterValues.size() == 0) {
						addItemValue(builder, list.getChildren());
					} else {
						int listExporterValuesSize = listExporterValues.size();
						for (int j = 0; j < listExporterValuesSize; j++) {
							addItemValue(builder, listExporterValues.get(j));
							if (j < (listExporterValuesSize - 1)) builder.append(", ");
						}
					}
				}
				builder.append("\n");
			}
		}

    	if (includeFooters) {
			//addFacetColumns(builder, columns, ColumnType.FOOTER);
		}
    	
    	list.setRowIndex(-1);

		byte[] bytes;
		try {
			bytes = builder.toString().getBytes(encodingType);
		} catch (Exception e) {
			bytes = builder.toString().getBytes("UTF-8");
			encodingType = "UTF-8";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Unsupported encoding specified in ace:listExporter component with id '"+component.getId()+"' in view "+facesContext.getViewRoot().getViewId()+". Defaulting to UTF-8 instead.");
		}
		
		return registerResource(bytes, filename + ".csv", "text/csv; charset=" + encodingType);
	}

/*
	protected void addItemValues(StringBuilder builder, List<UIColumn> columns) throws IOException {
		for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
            addColumnValue(builder, iterator.next().getChildren());
            if (iterator.hasNext()) { builder.append(","); }
		}
	}
*/

	protected void addSelectItemValue(StringBuilder builder, SelectItem item) throws IOException {
		String value;
		if (item == null) value = "";
		else {
			if ("value".equalsIgnoreCase(textToExport)) value = (String) item.getValue();
			else if ("both".equalsIgnoreCase(textToExport)) value = item.getLabel() + "(" + item.getValue() + ")";
			else value = item.getLabel();
		}
		value = value == null ? "" : value.trim();
		builder.append("\"" + value + "\"");
	}
	
	protected void addItemValue(StringBuilder builder, UIComponent component) throws IOException {
		String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
		builder.append("\"" + value + "\"");
	}

	protected void addItemValue(StringBuilder builder, ListExporterValue listExporterValue) throws IOException {
		StringBuilder builder1 = new StringBuilder();

		if (listExporterValue.isRendered()) {
			builder1.append("\"" + listExporterValue.getValue() + "\"");
		}

		builder.append(builder1.toString());
	}

	protected void addColumnName(StringBuilder builder, ListExporterValue listExporterValue) throws IOException {
		StringBuilder builder1 = new StringBuilder();
		String name = listExporterValue.getName();
		name = name != null ? name : "";

		if (listExporterValue.isRendered()) {
			builder1.append("\"" + name + "\"");
		}

		builder.append(builder1.toString());
	}
	
	protected void addItemValue(StringBuilder builder, List<UIComponent> components) throws IOException {
		StringBuilder localBuilder = new StringBuilder();
		for (UIComponent component : components)
			if (component.isRendered()) {
				String value = exportValue(FacesContext.getCurrentInstance(), component);
				localBuilder.append(value);
            }
		builder.append("\"" + localBuilder.toString() + "\"");
	}

/*
	protected void addFacetColumns(StringBuilder builder, List<UIColumn> columns, ColumnType columnType) throws IOException {
		for (Iterator<UIColumn> iterator = columns.iterator(); iterator.hasNext();) {
			UIColumn uiColumn = iterator.next();
			UIComponent facet = uiColumn.getFacet(columnType.facet());
			if (facet != null) {
				addColumnValue(builder, facet);
			} else {
				String value = "";
				if (uiColumn instanceof Column) {
					Column column = (Column) uiColumn;
					if (columnType == ColumnType.HEADER) {
						String headerText = column.getHeaderText();
						value = headerText != null ? headerText : "";
					} else if (columnType == ColumnType.FOOTER) {
						String footerText = column.getFooterText();
						value = footerText != null ? footerText : "";
					}
				}
				builder.append("\"" + value + "\"");
			}
            if (iterator.hasNext()) { builder.append(","); }
		}
		builder.append("\n");
    }
*/
}
