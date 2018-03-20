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

package org.icefaces.ace.component.dashboard;

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

public class DashboardPaneRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {
		super.decodeBehaviors(context, component);
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		DashboardPane dashboardPane = (DashboardPane) component;
		String clientId = dashboardPane.getClientId(context);

		writer.startElement("li", component);

		String baseClass = "ice-ace-dashboard-pane";
		String styleClass = dashboardPane.getStyleClass();
		if (styleClass != null) {
			baseClass += " " + styleClass;
		}
		writer.writeAttribute("class", baseClass, null);

		writer.writeAttribute("data-sizex", "1", null);
		writer.writeAttribute("data-sizey", "1", null);
		writer.writeAttribute("data-col", "1", null);

		List<UIComponent> siblings = dashboardPane.getParent().getChildren();
		for (int i = 0; i < siblings.size(); i++) {
			UIComponent sibling = siblings.get(i);
			if (sibling == dashboardPane) {
				writer.writeAttribute("data-row", "" + (i + 1), null);
				break;
			}
		}

		//encodeHeader(context, dashboardPane, position);

		writer.startElement("span", null);
		writer.writeAttribute("id", clientId + "_content", null);
		writer.writeAttribute("class", "ice-ace-boderlayout-content", null);
		String style = dashboardPane.getStyle();
		if (style != null) {
			writer.writeAttribute("style", style, null);
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		DashboardPane dashboardPane = (DashboardPane) component;

		writer.endElement("span");

		//encodeFooter(context, dashboardPane);

		writer.endElement("li");
	}

    public void encodeHeader(FacesContext context, DashboardPane dashboardPane, String position) throws IOException {
        String headerText = dashboardPane.getHeaderText();
        UIComponent headerFacet = dashboardPane.getFacet("header");
        
        if (headerText == null && headerFacet == null) return;
        
        ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);

		writer.startElement("div", null); // for paddings and theme styling
        writer.writeAttribute("class", "ui-widget-header ui-corner-all", null);

		if (!"center".equals(position)) {

			// --- close button ---
			if (dashboardPane.isClosable()) {
				writer.startElement("a", null);
				writer.writeAttribute("href", "javascript:void(0);", null);
				writer.writeAttribute("class", "ice-ace-boderlayout-button-close", null);
				writer.writeAttribute("role", "button", null);

				writer.startElement("span", null);
				writer.writeAttribute("class", "fa fa-window-close fa-lg", null);
				writer.endElement("span");

				writer.endElement("a");
			}

			// --- toggle button ---
			if (dashboardPane.isToggleable()) {

				String iconClass = "";
				if ("north".equals(position)) {
					iconClass = "fa-caret-up";
				} else if ("south".equals(position)) {
					iconClass = "fa-caret-down";
				} else if ("east".equals(position)) {
					iconClass = "fa-caret-right";
				} else if ("west".equals(position)) {
					iconClass = "fa-caret-left";
				}

				writer.startElement("a", null);
				writer.writeAttribute("href", "javascript:void(0);", null);
				writer.writeAttribute("class", "ice-ace-boderlayout-button-toggle", null);
				writer.writeAttribute("role", "button", null);

				writer.startElement("span", null);
				writer.writeAttribute("class", "fa " + iconClass + " fa-lg", null);
				writer.endElement("span");
				writer.endElement("a");
			}
		}

		// --- header text ---
        writer.startElement("span", null);
		writer.writeAttribute("id", dashboardPane.getClientId(context) + "_header", null);
        
        if (headerFacet != null)
            headerFacet.encodeAll(context);
        else if (headerText != null)
            writer.writeText(headerText, null);
        
        writer.endElement("span");

        writer.endElement("div"); // for paddings and theme styling

        writer.endElement("div");
	}

    public void encodeFooter(FacesContext context, DashboardPane dashboardPane) throws IOException {
        String footerText = dashboardPane.getFooterText();
        UIComponent footerFacet = dashboardPane.getFacet("footer");
        
        if (footerText == null && footerFacet == null) return;
        
        ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);

		writer.startElement("div", null); // for paddings and theme styling
        writer.writeAttribute("class", "ui-widget-header ui-corner-all", null);

		// --- footer text ---
        writer.startElement("span", null);
		writer.writeAttribute("id", dashboardPane.getClientId(context) + "_footer", null);
        
        if (footerFacet != null)
            footerFacet.encodeAll(context);
        else if (footerText != null)
            writer.writeText(footerText, null);
        
        writer.endElement("span");

        writer.endElement("div"); // for paddings and theme styling

        writer.endElement("div");
	}
}