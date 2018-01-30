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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BorderLayoutPaneRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		super.decodeBehaviors(context, component);
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		BorderLayoutPane borderLayoutPane = (BorderLayoutPane) component;
		String clientId = borderLayoutPane.getClientId(context);

		writer.startElement("div", component);
		//ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

		String position = borderLayoutPane.getPosition();
		if (position == null) return;
		position = position.toLowerCase();

		String baseClass = "ice-ace-boderlayout-" + position;
		String styleClass = borderLayoutPane.getStyleClass();
		if (styleClass != null) {
			baseClass += " " + styleClass;
		}
		writer.writeAttribute("class", baseClass, null);

		writer.startElement("div", null); // for table-like styling

		encodeHeader(context, borderLayoutPane);

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_content", null);
		writer.writeAttribute("class", "ice-ace-boderlayout-content", null);
		String style = borderLayoutPane.getStyle();
		if (style != null) {
			writer.writeAttribute("style", style, null);
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		BorderLayoutPane borderLayoutPane = (BorderLayoutPane) component;

		writer.endElement("div");

		encodeFooter(context, borderLayoutPane);

		writer.endElement("div"); // for table-like styling

		writer.endElement("div");
	}

    public void encodeHeader(FacesContext context, BorderLayoutPane borderLayoutPane) throws IOException {
        String headerText = borderLayoutPane.getHeaderText();
        UIComponent headerFacet = borderLayoutPane.getFacet("header");
        
        if (headerText == null && headerFacet == null) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);

		writer.startElement("div", null);
        writer.writeAttribute("class", "ui-widget-header ui-corner-all", null);

        writer.startElement("span", null);
		writer.writeAttribute("id", borderLayoutPane.getClientId(context) + "_header", null);
        
        if (headerFacet != null)
            headerFacet.encodeAll(context);
        else if (headerText != null)
            writer.writeText(headerText, null);
        
        writer.endElement("span");

        if (borderLayoutPane.isClosable()) {
			writer.startElement("a", null);
			writer.writeAttribute("href", "javascript:void(0);", null);
			writer.writeAttribute("class", "ice-ace-boderlayout-button-close", null);

			writer.startElement("span", null);
			writer.writeAttribute("class", "fa fa-window-close fa-lg", null);
			writer.endElement("span");

			writer.endElement("a");
        }

        if (borderLayoutPane.isCollapsible()) {
			String position = borderLayoutPane.getPosition();
			position = position.toLowerCase();

			String iconClass = "";
			if ("north".equals(position)) {
				iconClass = "fa-caret-square-o-up";
			} else if ("south".equals(position)) {
				iconClass = "fa-caret-square-o-down";
			} else if ("east".equals(position)) {
				iconClass = "fa-caret-square-o-right";
			} else if ("west".equals(position)) {
				iconClass = "fa-caret-square-o-left";
			}

			writer.startElement("a", null);
			writer.writeAttribute("href", "javascript:void(0);", null);
			writer.writeAttribute("class", "ice-ace-boderlayout-button-collapsible", null);

			writer.startElement("span", null);
			writer.writeAttribute("class", "fa " + iconClass + " fa-lg", null);
			writer.endElement("span");
			writer.endElement("a");
        }

        writer.endElement("div");

        writer.endElement("div");
	}

    public void encodeFooter(FacesContext context, BorderLayoutPane borderLayoutPane) throws IOException {
        String footerText = borderLayoutPane.getFooterText();
        UIComponent footerFacet = borderLayoutPane.getFacet("footer");
        
        if (footerText == null && footerFacet == null) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);

		writer.startElement("div", null);
        writer.writeAttribute("class", "ui-widget-header ui-corner-all", null);

        writer.startElement("span", null);
		writer.writeAttribute("id", borderLayoutPane.getClientId(context) + "_footer", null);
        
        if (footerFacet != null)
            footerFacet.encodeAll(context);
        else if (footerText != null)
            writer.writeText(footerText, null);
        
        writer.endElement("span");

        writer.endElement("div");

        writer.endElement("div");
	}
}