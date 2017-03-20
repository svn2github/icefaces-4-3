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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
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
import org.icefaces.ace.util.XMLChar;

public class XMLExporter extends Exporter {

    @Override
	public String export(FacesContext facesContext, ListExporter component, ACEList list) throws IOException {
		setUp(component, list);
		StringBuilder builder = new StringBuilder();
    	
    	String var = list.getVar();
		var = var == null ? "item" : "".equals(var.trim()) ? "item" : var.toLowerCase();
		var = sanitizeXMLTagName(var);
    	
		try {
			"".toString().getBytes(encodingType);
		} catch (Exception e) {
			encodingType = "UTF-8";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Unsupported encoding specified in ace:listExporter component with id '"+component.getId()+"' in view "+facesContext.getViewRoot().getViewId()+". Defaulting to UTF-8 instead.");
		}

    	builder.append("<?xml version=\"1.0\" encoding=\"" + encodingType + "\"?>\n");
    	builder.append("<" + list.getId() + ">\n");
		
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
					builder.append("\t<" + var + ">");
					addSelectItemValue(builder, (SelectItem) rowData);
					builder.append("</" + var + ">\n");
				} else {
					if (listExporterValues.size() == 0) {
						builder.append("\t<" + var + ">");
						addItemValue(builder, list.getChildren());
						builder.append("</" + var + ">\n");
					} else {
						builder.append("\t<" + var + ">");
						int listExporterValuesSize = listExporterValues.size();
						for (int j = 0; j < listExporterValuesSize; j++) {
							addItemValue(builder, listExporterValues.get(j));
						}
						builder.append("\n\t</" + var + ">\n");
					}
				}
			}
		}
    	
    	builder.append("</" + list.getId() + ">");
    	
    	list.setRowIndex(-1);

		byte[] bytes = builder.toString().getBytes(encodingType);
		
		return registerResource(bytes, filename + ".xml", "text/xml; charset=" + encodingType);
	}

	protected void addSelectItemValue(StringBuilder builder, SelectItem item) throws IOException {
		String value = item == null ? "" : item.getLabel();
		value = value == null ? "" : value.trim();
		builder.append(value);
	}

	protected void addItemValue(StringBuilder builder, UIComponent component) throws IOException {
		StringBuilder builder1 = new StringBuilder();
		//String tag = header.toLowerCase();
		//builder.append("\t\t<" + tag + ">");

		if (component.isRendered()) {
			String value = exportValue(FacesContext.getCurrentInstance(), component);

			builder1.append(value);
		}

		builder.append(builder1.toString());
		
		//builder.append("</" + tag + ">\n");
	}

	protected void addItemValue(StringBuilder builder, ListExporterValue listExporterValue) throws IOException {
		StringBuilder builder1 = new StringBuilder();
		String name = listExporterValue.getName();
		String tag = name == null? "" : name.toLowerCase();
		tag = sanitizeXMLTagName(tag);
		builder.append("\n\t\t<" + tag + ">");

		if (listExporterValue.isRendered()) {
			builder1.append(listExporterValue.getValue());
		}

		builder.append(builder1.toString());
		
		builder.append("</" + tag + ">");
	}

	protected void addItemValue(StringBuilder builder, List<UIComponent> components) throws IOException {
		StringBuilder builder1 = new StringBuilder();
		//String tag = header.toLowerCase();
		//builder.append("\t\t<" + tag + ">");

		for (UIComponent component : components) {
			if (component.isRendered()) {
				String value = exportValue(FacesContext.getCurrentInstance(), component);

				builder1.append(value);
			}
		}

		builder.append(builder1.toString());
		
		//builder.append("</" + tag + ">\n");
	}	
	
	protected String sanitizeXMLTagName(String tag) {
		StringBuilder sb = new StringBuilder();
		
		int length = tag.length();
		for (int i = 0; i < length; i++) {
			if (XMLChar.isName(tag.codePointAt(i)))
				sb.appendCodePoint(tag.codePointAt(i));
		}
		
		String sanitized = sb.toString();
		if ("".equals(sanitized)) // case where all characters are invalid
			return "_";
		else if (!XMLChar.isNameStart(sanitized.codePointAt(0))) // case where tag has invalid start character
			return ("_" + sanitized);
		else return sanitized;
	}
}
