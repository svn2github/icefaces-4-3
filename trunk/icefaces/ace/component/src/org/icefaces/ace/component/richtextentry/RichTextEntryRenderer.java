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

package org.icefaces.ace.component.richtextentry;

import org.icefaces.ace.renderkit.InputRenderer;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.ace.util.JSONBuilder;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;

@MandatoryResourceComponent(tagName="richTextEntry", value="org.icefaces.ace.component.richtextentry.RichTextEntry")
public class RichTextEntryRenderer extends InputRenderer {

    public void decode(FacesContext context, UIComponent component) {
		decodeBehaviors(context, component);
	}
	
	public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = uiComponent.getClientId(facesContext);
        RichTextEntry richTextEntry = (RichTextEntry) uiComponent;

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "container", null);
		writer.writeAttribute("class", "ice-ace-richtextentry " + richTextEntry.getStyleClass(), null);
		if (richTextEntry.getStyle() != null) {
			writer.writeAttribute("style", richTextEntry.getStyle(), null);
		}
		renderResetSettings(facesContext, uiComponent);

		writer.startElement("textarea", uiComponent);
		writer.writeAttribute("name", clientId, null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("style", "display:none;", null);
		Object value = richTextEntry.getValue();
		if (value != null) {
			writer.writeText(value, null);
		}
		
		writer.endElement("textarea");
		writer.endElement("div");

		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + "scrpt", null);
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		String customConfig =  richTextEntry.getCustomConfigPath();
		customConfig = customConfig == null ? "" : resolveResourceURL(facesContext, customConfig);
		
		int hashCode = 0;
		if (value != null) {
			hashCode = value.toString().hashCode();
		}
		
		JSONBuilder jb = JSONBuilder.create();
		jb.beginFunction("ice.ace.richtextentry.renderEditor")
			.item(clientId)
			.item(richTextEntry.getToolbar())
			.item(richTextEntry.getLanguage())
			.item(richTextEntry.getSkin().toLowerCase())
			.item(richTextEntry.getHeight())
			.item(richTextEntry.getWidth())
			.item(customConfig)
			.item(richTextEntry.isSaveOnSubmit())
			.item(richTextEntry.isDisabled())
			.item(hashCode)
			.beginMap()
			.entry("p", ""); // dummy property
			encodeClientBehaviors(facesContext, richTextEntry, jb);
        jb.endMap().endFunction();
		writer.write("ice.ace.richtextentry.registry['" + clientId + "container'] = function(){" + jb.toString() + "};");
		writer.write("ice.ace.richtextentry.registry['" + clientId + "container']();");
		
		writer.endElement("script");
		writer.endElement("span");

		writer.startElement("button", null);
		writer.writeAttribute("id", clientId + "_accesskey_proxy", null);
		String accesskey = richTextEntry.getAccesskey();
		if (accesskey != null) {
			writer.writeAttribute("accesskey", accesskey, null);
			writer.writeAttribute("onfocus", "CKEDITOR.instances['" + clientId + "'].focus();", null);
			writer.writeAttribute("style", "width:0;height:0;border:0;background-color:transparent;"
				+ "cursor:pointer;overflow:hidden;outline:none;", null);
		}
		writer.endElement("button");
    }
	
    // taken from com.icesoft.faces.util.CoreUtils
	public static String resolveResourceURL(FacesContext facesContext, String path) {
        ExternalContext ec = facesContext.getExternalContext();
        String ctxtPath = ec.getRequestContextPath();

        if (path.length() > 0 && path.charAt(0) == '/' && path.startsWith(ctxtPath)) {
            path = path.substring(ctxtPath.length());
        }

        return facesContext.getApplication().getViewHandler().getResourceURL(facesContext, path);
    }

	protected void renderResetSettings(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		String clientId = component.getClientId(context);

		JSONBuilder jb = JSONBuilder.create();
		jb.beginArray();
		jb.item("richtextentry");
		jb.beginArray();
		jb.item(clientId);
		jb.endArray();
		jb.endArray();

		writer.writeAttribute("data-ice-reset", jb.toString(), null);
	}
}