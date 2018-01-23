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

package org.icefaces.ace.component.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.FacesException;

import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@MandatoryResourceComponent(tagName="layout", value="org.icefaces.ace.component.layout.Layout")
public class LayoutRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;
        String clientId = layout.getClientId(context);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        //ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

		String style = layout.getStyle();
        if (style != null) {
            writer.writeAttribute("class", style, null);
        }

		String baseClass = "ice-ace-layout";
		String styleClass = layout.getStyleClass();
        if (styleClass != null) {
            baseClass += " " + styleClass;
        }
        writer.writeAttribute("class", baseClass, null);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) component;

		encodeScript(context, layout);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Layout layout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = layout.getClientId(context);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        JSONBuilder jb = JSONBuilder.create();
        jb.beginFunction("ice.ace.create")
          .item("Layout")
          .beginArray()
          .item(clientId)
          .beginMap();
          //.entry("isVisible", drawerPanel.isVisible())

        // Behaviors
        //encodeClientBehaviors(context, drawerPanel, jb);

        jb.endMap().endArray();
		jb.endFunction();

		writer.write(jb.toString());

        writer.endElement("script");
    }
}