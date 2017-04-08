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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.icefaces.ace.component.schedule.Schedule;

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
	protected String pdfFont;
	
	public void setUp(ScheduleExporter component, Schedule schedule) {
		filename = component.getFileName();
		if (filename == null) {
			filename = "data";
			java.util.logging.Logger.getLogger(this.getClass().getName()).warning("Required attribute 'file' is null in ace:scheduleExporter component with id "+component.getId()+" in view "+FacesContext.getCurrentInstance().getViewRoot().getViewId()+".");
		}
		encodingType = component.getEncoding();
		preProcessor = component.getPreProcessor();
		postProcessor = component.getPostProcessor();
		includeHeaders = component.isIncludeHeaders();
		pdfFont = component.getPdfFont();
	}

    public abstract String export(FacesContext facesContext, ScheduleExporter component, Schedule schedule) throws IOException;

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
