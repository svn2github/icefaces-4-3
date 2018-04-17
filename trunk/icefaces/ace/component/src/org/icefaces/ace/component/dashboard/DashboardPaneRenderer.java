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

		writer.startElement("div", component);

		writer.writeAttribute("id", clientId, null);

		String baseClass = "ice-ace-dashboard-pane ui-widget ui-widget-content ui-corner-all";
		String styleClass = dashboardPane.getStyleClass();
		if (styleClass != null) {
			baseClass += " " + styleClass;
		}
		writer.writeAttribute("class", baseClass, null);

		encodeHeader(context, dashboardPane);

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_content", null);
		writer.writeAttribute("class", "ice-ace-dashboard-content ui-widget-content", null);
		String style = dashboardPane.getStyle();
		if (style != null) {
			writer.writeAttribute("style", style, null);
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		DashboardPane dashboardPane = (DashboardPane) component;

		writer.endElement("div");

		//encodeFooter(context, dashboardPane);

		writer.endElement("div");
	}

    public void encodeHeader(FacesContext context, DashboardPane dashboardPane) throws IOException {
        String headerText = dashboardPane.getHeaderText();
        UIComponent headerFacet = dashboardPane.getFacet("header");
        
        ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
        writer.writeAttribute("class", "ui-widget-header ui-corner-top", null);

		// --- close button ---
		if (dashboardPane.isClosable()) {
			writer.startElement("a", null);
			writer.writeAttribute("href", "javascript:void(0);", null);
			writer.writeAttribute("class", "ice-ace-dashboard-button-close ui-corner-all", null);
			writer.writeAttribute("role", "button", null);

			writer.startElement("span", null);
			writer.writeAttribute("class", "fa fa-times", null);
			writer.endElement("span");

			writer.endElement("a");
		}

		// --- toggle button ---
		if (dashboardPane.isToggleable()) {

			String iconClass = "fa-caret-up";

			writer.startElement("a", null);
			writer.writeAttribute("href", "javascript:void(0);", null);
			writer.writeAttribute("class", "ice-ace-dashboard-button-toggle", null);
			writer.writeAttribute("role", "button", null);

			writer.startElement("span", null);
			writer.writeAttribute("class", "fa " + iconClass, null);
			writer.endElement("span");
			writer.endElement("a");
		}

		// --- header text ---
        writer.startElement("span", null);
		writer.writeAttribute("id", dashboardPane.getClientId(context) + "_header", null);
        
        if (headerFacet != null)
            headerFacet.encodeAll(context);
        else if (headerText != null)
            writer.writeText(headerText, null);
		else writer.writeText("\u00A0", null);
        
        writer.endElement("span");

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