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

package org.icefaces.ace.component.borderlayout;

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

@MandatoryResourceComponent(tagName="boderLayout", value="org.icefaces.ace.component.borderlayout.BorderLayout")
public class BorderLayoutRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BorderLayout borderLayout = (BorderLayout) component;
        String clientId = borderLayout.getClientId(context);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        //ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

		String style = borderLayout.getStyle();
        if (style != null) {
            writer.writeAttribute("class", style, null);
        }

		String baseClass = "ice-ace-borderlayout";
		String styleClass = borderLayout.getStyleClass();
        if (styleClass != null) {
            baseClass += " " + styleClass;
        }
        writer.writeAttribute("class", baseClass, null);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BorderLayout borderLayout = (BorderLayout) component;

		encodeScript(context, borderLayout);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, BorderLayout borderLayout) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = borderLayout.getClientId(context);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        JSONBuilder jb = JSONBuilder.create();
        jb.beginFunction("ice.ace.create")
			.item("BorderLayout")
			.beginArray()
			.item(clientId)
			.beginMap();
			for (UIComponent child : borderLayout.getChildren()) {
				if (child.isRendered() && child instanceof BorderLayoutPane) {
					BorderLayoutPane pane = (BorderLayoutPane) child;

					String position = pane.getPosition();
					if (position == null) continue;
					position = position.toLowerCase();
					jb.beginMap(pane.getPosition())
						.entry("paneSelector", ".ice-ace-boderlayout-" + position)
						.entry("size", pane.getSize())
						.entry("resizable", pane.isResizable())
						.entry("closable", pane.isCollapsible())
						.entry("minSize", pane.getMinSize())
						.entry("maxSize", pane.getMaxSize())
						.entry("spacing_open", pane.getBorderWidth())
						.entry("togglerLength_open", 0); // hide default toggler button
					
						if (pane.isCollapsible()) {
							jb.entry("spacing_closed", pane.getCollapseSize());
						}
					
						jb.entry("initHidden", !pane.isVisible())
						.entry("initClosed", pane.isCollapsed())
						.entryNonNullValue("fxName", pane.getEffect())
						.entryNonNullValue("fxSpeed", pane.getEffectSpeed());

						encodeClientBehaviors(context, pane, jb);
/*
						.entry("resizerTip", layout.getResizeTitle(), null)
						.entry("togglerTip_closed", layout.getExpandTitle(), null);
*/
					jb.endMap();
				}
			}

        // Behaviors
        //encodeClientBehaviors(context, borderLayout, jb);

        jb.endMap().endArray();
		jb.endFunction();

		writer.write(jb.toString());

        writer.endElement("script");
    }
}