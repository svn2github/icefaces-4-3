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

import org.icefaces.ace.component.list.ACEList;
import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.columngroup.ColumnGroup;
import org.icefaces.ace.component.row.Row;
import org.icefaces.ace.component.listexportervalue.ListExporterValue;
import org.icefaces.ace.component.expansiontoggler.ExpansionToggler;
import org.icefaces.ace.component.excludefromexport.ExcludeFromExport;
import org.icefaces.ace.component.celleditor.CellEditor;
import org.icefaces.ace.component.panelexpansion.PanelExpansion;

import org.icefaces.application.ResourceRegistry;

import java.util.logging.Logger;

public abstract class Exporter {

	protected final static Logger logger = Logger.getLogger(Exporter.class.getName());

	protected static final Pattern HTML_TAG_PATTERN = Pattern.compile("\\<.*?\\>");
	protected String filename;
	protected String encodingType;
	protected MethodExpression preProcessor;
	protected MethodExpression postProcessor;
	protected boolean includeHeaders;
	protected boolean includeFooters;
	protected boolean selectedItemsOnly;
	protected String pdfFont;
	protected List<ListExporterValue> listExporterValues;
	
	public void setUp(ListExporter component, ACEList list) {
		filename = component.getFileName();
		if (filename == null) {
			filename = "data";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Required attribute 'file' is null in ace:listExporter component with id "+component.getId()+" in view "+FacesContext.getCurrentInstance().getViewRoot().getViewId()+".");
		}
		encodingType = component.getEncoding();
		preProcessor = component.getPreProcessor();
		postProcessor = component.getPostProcessor();
		includeHeaders = component.isIncludeHeaders();
		includeFooters = component.isIncludeFooters();
		selectedItemsOnly = component.isSelectedItemsOnly();
		pdfFont = component.getPdfFont();

		List<UIComponent> children = list.getChildren();
		listExporterValues = new ArrayList<ListExporterValue>();
		int childrenSize = children.size();
		for (int j = 0; j < childrenSize; j++) {
			UIComponent child = children.get(j);
			if (child instanceof ListExporterValue) listExporterValues.add((ListExporterValue) child);
		}
	}

    public abstract String export(FacesContext facesContext, ListExporter component, ACEList list) throws IOException;
	
	protected boolean shouldExcludeFromExport(UIComponent component) {
	
		for (UIComponent child : component.getChildren()) {
            if (child instanceof ExcludeFromExport) {	
				if (child.isRendered()) return true;
			}
		}
		return false;
	}

    protected String exportValue(FacesContext context, UIComponent component) {
		if (shouldExcludeFromExport(component) || !component.isRendered()) return "";
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
}
