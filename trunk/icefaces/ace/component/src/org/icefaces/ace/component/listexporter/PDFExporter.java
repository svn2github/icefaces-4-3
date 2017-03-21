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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.icefaces.ace.component.list.ACEList;
import org.icefaces.ace.component.listexportervalue.ListExporterValue;

/*
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
*/
import java.lang.reflect.*;



import java.util.logging.Logger;

public class PDFExporter extends Exporter {

	protected final static Logger logger = Logger.getLogger(PDFExporter.class.getName());
	
	protected Class documentClass;
	protected Class fontClass;
	protected Class fontFactoryClass;
	protected Class paragraphClass;
	protected Class phraseClass;
	protected Class pdfPTableClass;
	protected Class elementClass;
	protected Class pdfWriterClass;
	
	protected Method addCellMethod;
	protected Constructor paragraphConstructor;
	
	protected void loadClasses() throws ClassNotFoundException {
		documentClass = Class.forName("com.lowagie.text.Document");
		fontClass = Class.forName("com.lowagie.text.Font");
		fontFactoryClass = Class.forName("com.lowagie.text.FontFactory");
		paragraphClass = Class.forName("com.lowagie.text.Paragraph");
		phraseClass = Class.forName("com.lowagie.text.Phrase");
		pdfPTableClass = Class.forName("com.lowagie.text.pdf.PdfPTable");
		elementClass = Class.forName("com.lowagie.text.Element");
		pdfWriterClass = Class.forName("com.lowagie.text.pdf.PdfWriter");
	}
	
	protected void loadMethods() throws NoSuchMethodException {
		addCellMethod = pdfPTableClass.getMethod("addCell", new Class[] { phraseClass });
		paragraphConstructor = paragraphClass.getConstructor(new Class[] { String.class, fontClass });
	}
	
	@Override
	public String export(FacesContext facesContext, ListExporter component, ACEList list) throws IOException {
		setUp(component, list);
		try {
			loadClasses();
			loadMethods();
			//Document document = new Document();
			Object document = documentClass.newInstance();
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//PdfWriter.getInstance(document, baos);
	        pdfWriterClass.getMethod("getInstance", new Class[] { documentClass, OutputStream.class }).invoke(null, new Object[] { document, baos });
	        
	        if (preProcessor != null) {
	    		preProcessor.invoke(facesContext.getELContext(), new Object[]{document});
	    	}

            //if (!document.isOpen()) {
            //    document.open();
            //}
			Boolean isOpen = (Boolean) documentClass.getMethod("isOpen").invoke(document);
            if (!isOpen) {
                documentClass.getMethod("open").invoke(document);
            }
	        
			Constructor pdfPTableConstructor = pdfPTableClass.getConstructor(new Class[] { int.class });
			Method add = documentClass.getMethod("add", elementClass);

			boolean noSelectedRows = false;
			if (selectedItemsOnly) {
				Collection<Object> selections = list.isSelectItemModel() ? (Collection)list.getValue() : list.getSelections();
				if (selections == null || selections.size() == 0) {
					noSelectedRows = true;
				}
			}

			if (!noSelectedRows) {
				//PdfPTable pdfTable = exportPDFTable(table, pageOnly,excludeColumns, encodingType, includeHeaders, includeFooters, selectedRowsOnly);
				Object pdfTable;
				if (listExporterValues.size() == 0) {
					pdfTable = pdfPTableConstructor.newInstance(new Object[] { new Integer(1) });
				} else {
					pdfTable = pdfPTableConstructor.newInstance(new Object[] { new Integer(listExporterValues.size()) });
				}
				exportPDFTable(facesContext, pdfTable, list, encodingType, includeHeaders, includeFooters, selectedItemsOnly);
				//document.add(pdfTable);
				add.invoke(document, new Object[] { pdfTable });
			} else {
				//PdfPTable pdfTable = new PdfPTable(1);
				Object pdfTable = pdfPTableConstructor.newInstance(new Object[] { new Integer(1) });
				//pdfTable.addCell(new Paragraph(""));
				Object paragraph = paragraphClass.getConstructor(new Class[] { String.class }).newInstance(new Object[] { "" });
				addCellMethod.invoke(pdfTable, new Object[] { paragraph });
				//document.add(pdfTable);
				add.invoke(document, new Object[] { pdfTable });
			}
			
	    	if (postProcessor != null) {
	    		postProcessor.invoke(facesContext.getELContext(), new Object[]{document});
	    	}
	    	
	        //document.close();
			documentClass.getMethod("close").invoke(document);
			
			byte[] bytes = baos.toByteArray();
			
			return registerResource(bytes, filename + ".pdf", "application/pdf");
	        
		} catch (ClassNotFoundException e) {
			logger.severe("Exporting data to PDF format was attempted by a user, but the iText library was not found.");
			return "unsupported format";
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}
	
	protected void exportPDFTable(FacesContext facesContext, Object pdfTable, ACEList list, String encoding, boolean includeHeaders, boolean includeFooters, boolean selectedItemsOnly) 
		throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    	//PdfPTable pdfTable = new PdfPTable(numberOfColumns);
    	//Font font = FontFactory.getFont(FontFactory.TIMES, encoding);
		
		if (pdfFont != null) {
			if ("UTF-8".equalsIgnoreCase(encoding) 
				|| "UTF8".equalsIgnoreCase(encoding) 
				|| "Unicode".equalsIgnoreCase(encoding))
					encoding = "Identity-H";
		} else {
			pdfFont = "Times";
		}
		Object font = fontFactoryClass.getMethod("getFont", new Class[] { String.class, String.class }).invoke(null, new Object[] { pdfFont, encoding });
		
		int rowCount = list.getRowCount();
    	int first = 0;
    	
		if (includeHeaders) {
			//addFacetColumns(pdfTable, columns, headerFont, ColumnType.HEADER);
		}

		if (listExporterValues.size() > 0) {
			//Font headerFont = FontFactory.getFont(FontFactory.TIMES, encoding, Font.DEFAULTSIZE, Font.BOLD);			
			Object headerFont = fontFactoryClass.getMethod("getFont", new Class[] { String.class, String.class, float.class, int.class }).invoke(null, new Object[] { pdfFont, encoding, new Integer(12), new Integer(1) });

			int listExporterValuesSize = listExporterValues.size();
			for (int i = 0; i < listExporterValuesSize; i++) {
				addColumnName(pdfTable, listExporterValues.get(i), headerFont);
			}
		}

		final Collection<Object> selections = list.isSelectItemModel() ? (Collection)list.getValue() : list.getSelections();
    	for (int i = first; i < rowCount; i++) {
    		list.setRowIndex(i);
			boolean exportRow = true;
			Object rowData = list.getRowData();

			if (selectedItemsOnly && !selections.contains(rowData)) exportRow = false;

			if (exportRow) {
				//addColumnValue(pdfTable, columns.get(j).getChildren(), j, font);
				if (rowData instanceof SelectItem) {
					addSelectItemValue(pdfTable, (SelectItem) rowData, font);
				} else {
					if (listExporterValues.size() == 0) {
						addItemValue(pdfTable, list.getChildren(), font);
					} else {
						int listExporterValuesSize = listExporterValues.size();
						for (int j = 0; j < listExporterValuesSize; j++) {
							addItemValue(pdfTable, listExporterValues.get(j), font);
						}
					}
				}
			}
		}

        if (includeFooters) {
			//addFacetColumns(pdfTable, columns, headerFont, ColumnType.FOOTER);
        }
    	
    	list.setRowIndex(-1);
	}

/*
	protected void addFacetColumns(Object pdfTable, List<UIColumn> columns, Object font, ColumnType columnType) 
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for (int i = 0; i < columns.size(); i++) {
            UIColumn uiColumn = (UIColumn) columns.get(i);
			UIComponent facet = uiColumn.getFacet(columnType.facet());

            if (facet != null) {
				addColumnValue(pdfTable, facet, font);
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
				//pdfTable.addCell(new Paragraph(value, font));
				Object paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
				addCellMethod.invoke(pdfTable, new Object[] { paragraph });
			}
        }
	}
*/

	protected void addSelectItemValue(Object pdfTable, SelectItem item, Object font)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
		String value;
		if (item == null) value = "";
		else {
			if ("value".equalsIgnoreCase(textToExport)) value = (String) item.getValue();
			else if ("both".equalsIgnoreCase(textToExport)) value = item.getLabel() + "(" + item.getValue() + ")";
			else value = item.getLabel();
		}
		value = value == null ? "" : value.trim();
            
        //pdfTable.addCell(new Paragraph(value, font));
		Object paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });
	}

    protected void addItemValue(Object pdfTable, UIComponent component, Object font)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
    	String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
            
        //pdfTable.addCell(new Paragraph(value, font));
		Object paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });
    }

	protected void addItemValue(Object pdfTable, ListExporterValue listExporterValue, Object font)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Object value = listExporterValue.getValue();
		value = value != null ? value.toString() : "";

		if (listExporterValue.isRendered()) {            
			//pdfTable.addCell(new Paragraph(value, font));
			Object paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
			addCellMethod.invoke(pdfTable, new Object[] { paragraph });
		}
	}

	protected void addColumnName(Object pdfTable, ListExporterValue listExporterValue, Object font)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
        String name = listExporterValue.getName();
		name = name != null ? name : "";

		if (listExporterValue.isRendered()) {            
			//pdfTable.addCell(new Paragraph(value, font));
			Object paragraph = paragraphConstructor.newInstance(new Object[] { name, font });
			addCellMethod.invoke(pdfTable, new Object[] { paragraph });
		}
	}
    
    protected void addItemValue(Object pdfTable, List<UIComponent> components, Object font)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
        StringBuilder builder = new StringBuilder();
        
        for (UIComponent component : components) {
        	if (component.isRendered() ) {
        		String value = exportValue(FacesContext.getCurrentInstance(), component);
                
                if (value != null)
                	builder.append(value);
            }
		}  
        
        //pdfTable.addCell(new Paragraph(builder.toString(), font));
		Object paragraph = paragraphConstructor.newInstance(new Object[] { builder.toString(), font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });
    }
}
