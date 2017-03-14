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
import java.util.List;
import java.util.ArrayList;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.icefaces.ace.component.list.ACEList;

import java.io.ByteArrayOutputStream;

public class ExcelExporter extends Exporter {

	private boolean isXSSF = false;

    @Override
	public String export(FacesContext facesContext, ListExporter component, ACEList list) throws IOException {
		setUp(component, list);
		isXSSF = "xlsx".equalsIgnoreCase(component.getType());
    	Workbook wb;
		if (isXSSF) {
			wb = new XSSFWorkbook();
		} else {
			wb = new HSSFWorkbook();
		}
    	Sheet sheet = wb.createSheet();

    	if (preProcessor != null) {
    		preProcessor.invoke(facesContext.getELContext(), new Object[]{wb});
    	}

		int rowCount = list.getRowCount();
    	int first = 0;
    	int sheetRowIndex = 0;

        if (includeHeaders) {
			//addFacetColumns(sheet, columns, ColumnType.HEADER, 0);
		}

    	for (int i = first; i < rowCount; i++) {
    		list.setRowIndex(i);
			boolean exportRow = true;
			//if (selectedRowsOnly && !rowState.isSelected()) exportRow = false;

			Row row;
			if (exportRow) {			
				row = sheet.createRow(sheetRowIndex++);
				addSelectItemValue(row, (SelectItem) list.getRowData(), 0);
			}
		}

    	if (includeFooters) {
			//addFacetColumns(sheet, columns, ColumnType.FOOTER, sheetRowIndex++);
		}
    	
    	list.setRowIndex(-1);
    	
    	if (postProcessor != null) {
    		postProcessor.invoke(facesContext.getELContext(), new Object[]{wb});
    	}
    	
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		wb.write(baos);
		
		byte[] bytes = baos.toByteArray();

		if (isXSSF) {
			return registerResource(bytes, filename + ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		} else {
			return registerResource(bytes, filename + ".xls", "application/vnd.ms-excel");
		}
	}

/*
	protected void addFacetColumns(Sheet sheet, List<UIColumn> columns, ColumnType columnType, int rowIndex) {
        Row rowHeader = sheet.createRow(rowIndex);

        for (int i = 0; i < columns.size(); i++) {
            UIColumn uiColumn = (UIColumn) columns.get(i);
			UIComponent facet = uiColumn.getFacet(columnType.facet());
			
            if (facet != null) {
				addColumnValue(rowHeader, facet, i);
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
				Cell cell = rowHeader.createCell(i);
				if (isXSSF) {
					cell.setCellValue(new XSSFRichTextString(value));
				} else {
					cell.setCellValue(new HSSFRichTextString(value));
				}
			}
        }
    }
*/

	protected void addSelectItemValue(Row rowHeader, SelectItem item, int index) throws IOException {
        Cell cell = rowHeader.createCell(index);
		String value = item == null ? "" : item.getLabel();
		value = value == null ? "" : value.trim();

		if (isXSSF) {
			cell.setCellValue(new XSSFRichTextString(value));
		} else {
			cell.setCellValue(new HSSFRichTextString(value));
		}
	}
	
    protected void addColumnValue(Row rowHeader, UIComponent component, int index) {
        Cell cell = rowHeader.createCell(index);
        String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);

		if (isXSSF) {
			cell.setCellValue(new XSSFRichTextString(value));
		} else {
			cell.setCellValue(new HSSFRichTextString(value));
		}
    }
    
    protected void addColumnValue(Row rowHeader, List<UIComponent> components, int index) {
        Cell cell = rowHeader.createCell(index);
        StringBuilder builder = new StringBuilder();
        
        for (UIComponent component : components) {
        	if (component.isRendered()) {
                String value = exportValue(FacesContext.getCurrentInstance(), component);
                
                if (value != null)
                	builder.append(value);
            }
		}

		if (isXSSF) {
			cell.setCellValue(new XSSFRichTextString(builder.toString()));
		} else {
			cell.setCellValue(new HSSFRichTextString(builder.toString()));
		}
    }
}
