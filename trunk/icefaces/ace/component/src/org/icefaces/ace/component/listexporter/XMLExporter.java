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

import javax.el.MethodExpression;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.icefaces.ace.component.list.ACEList;
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

    	for (int i = first; i < rowCount; i++) {
    		list.setRowIndex(i);
			boolean exportRow = true;

			//if (selectedRowsOnly && !rowState.isSelected()) exportRow = false;

			if (exportRow) {
				builder.append("\t<" + var + ">");
				addSelectItemValue(builder, (SelectItem) list.getRowData());
				builder.append("</" + var + ">\n");
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
	
	protected void addColumnValues(StringBuilder builder, List<UIColumn> columns, List<String> headers) throws IOException {
		for (int i = 0; i < columns.size(); i++) {
            addColumnValue(builder, columns.get(i).getChildren(), headers.get(i));
		}
	}

	protected void addColumnValue(StringBuilder builder, List<UIComponent> components, String header) throws IOException {
		StringBuilder builder1 = new StringBuilder();
		String tag = header.toLowerCase();
		builder.append("\t\t<" + tag + ">");

		for (UIComponent component : components) {
			if (component.isRendered()) {
				String value = exportValue(FacesContext.getCurrentInstance(), component);

				builder1.append(value);
			}
		}

		builder.append(builder1.toString());
		
		builder.append("</" + tag + ">\n");
	}
	
	protected void addColumnValue(StringBuilder builder, String footer, String header) throws IOException {
		String tag = header.toLowerCase();
		builder.append("\t\t<" + tag + ">");

		builder.append(footer.toLowerCase());
		
		builder.append("</" + tag + ">\n");
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
