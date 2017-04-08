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
package org.icefaces.ace.component.scheduleexporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Collection;
import java.util.Date;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.icefaces.ace.model.schedule.ScheduleEvent;

import org.icefaces.ace.component.schedule.Schedule;

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
	public String export(FacesContext facesContext, ScheduleExporter component, Schedule schedule) throws IOException {
		setUp(component, schedule);
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

			if (schedule.getRowCount() > 0) {
				//PdfPTable pdfTable = exportPDFTable(table, pageOnly,excludeColumns, encodingType, include3, includeFooters, selectedRowsOnly);

				Object pdfTable = pdfPTableConstructor.newInstance(new Object[] { new Integer(5) });
				// TO DO: specify number of fields specified in attribute

				exportPDFTable(facesContext, pdfTable, schedule, encodingType, includeHeaders);
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
	
	protected void exportPDFTable(FacesContext facesContext, Object pdfTable, Schedule schedule, String encoding, boolean includeHeaders) 
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
		
		int rowCount = schedule.getRowCount();
    	int first = 0;
    	
		if (includeHeaders) {
			//Font headerFont = FontFactory.getFont(FontFactory.TIMES, encoding, Font.DEFAULTSIZE, Font.BOLD);			
			Object headerFont = fontFactoryClass.getMethod("getFont", new Class[] { String.class, String.class, float.class, int.class }).invoke(null, new Object[] { pdfFont, encoding, new Integer(12), new Integer(1) });

			addHeaders(pdfTable, headerFont);
		}

    	for (int i = first; i < rowCount; i++) {
    		schedule.setRowIndex(i);
			Object rowData = schedule.getRowData();

			//addColumnValue(pdfTable, columns.get(j).getChildren(), j, font);
			if (rowData instanceof ScheduleEvent) {
				addScheduleEventData(pdfTable, (ScheduleEvent) rowData, font);
			}
		}
    	
    	schedule.setRowIndex(-1);
	}

	protected void addScheduleEventData(Object pdfTable, ScheduleEvent event, Object font)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {
		if (event == null) return;
		else {
			String value;
			value = event.getTitle();
			value = value == null ? "" : value.trim();
			//pdfTable.addCell(new Paragraph(value, font));
			Object paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
			addCellMethod.invoke(pdfTable, new Object[] { paragraph });

			Date startDate = event.getStartDate();
			value = startDate != null ? startDate.toString() : "";
			//pdfTable.addCell(new Paragraph(value, font));
			paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
			addCellMethod.invoke(pdfTable, new Object[] { paragraph });

			Date endDate = event.getEndDate();
			value = endDate != null ? endDate.toString() : "";
			//pdfTable.addCell(new Paragraph(value, font));
			paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
			addCellMethod.invoke(pdfTable, new Object[] { paragraph });

			value = event.getLocation();
			value = value == null ? "" : value.trim();
			//pdfTable.addCell(new Paragraph(value, font));
			paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
			addCellMethod.invoke(pdfTable, new Object[] { paragraph });

			value = event.getNotes();
			value = value == null ? "" : value.trim();
			//pdfTable.addCell(new Paragraph(value, font));
			paragraph = paragraphConstructor.newInstance(new Object[] { value, font });
			addCellMethod.invoke(pdfTable, new Object[] { paragraph });
		}
	}

	protected void addHeaders(Object pdfTable, Object font)
		throws IllegalAccessException, InvocationTargetException, InstantiationException {

		//pdfTable.addCell(new Paragraph(value, font));
		Object paragraph = paragraphConstructor.newInstance(new Object[] { "Title", font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });

		//pdfTable.addCell(new Paragraph(value, font));
		paragraph = paragraphConstructor.newInstance(new Object[] { "Start Date", font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });

		//pdfTable.addCell(new Paragraph(value, font));
		paragraph = paragraphConstructor.newInstance(new Object[] { "End Date", font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });

		//pdfTable.addCell(new Paragraph(value, font));
		paragraph = paragraphConstructor.newInstance(new Object[] { "Location", font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });

		//pdfTable.addCell(new Paragraph(value, font));
		paragraph = paragraphConstructor.newInstance(new Object[] { "Notes", font });
		addCellMethod.invoke(pdfTable, new Object[] { paragraph });
	}
}
