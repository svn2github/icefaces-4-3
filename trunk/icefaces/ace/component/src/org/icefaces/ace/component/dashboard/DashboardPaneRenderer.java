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

		DashboardPane dashboardPane = (DashboardPane) component;
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		String clientId = dashboardPane.getClientId(context);

		if (params.containsKey(clientId + "_close")) {
			dashboardPane.setClosed(true);
		}

		super.decodeBehaviors(context, component);
	}

	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		DashboardPane dashboardPane = (DashboardPane) component;
		String clientId = dashboardPane.getClientId(context);

		writer.startElement("div", component);

		writer.writeAttribute("id", clientId, null);

		if (dashboardPane.isClosed()) {
			writer.writeAttribute("style", "display:none;", null);
			writer.endElement("div");
			return;
		}

		String baseClass = "ice-ace-dashboard-pane ui-widget ui-widget-content ui-corner-all";
		String styleClass = dashboardPane.getStyleClass();
		if (styleClass != null) {
			baseClass += " " + styleClass;
		}
		writer.writeAttribute("class", baseClass, null);

		writer.startElement("div", component); // for display: table;

		writer.startElement("div", component); // for display: table-row;

		encodeHeader(context, dashboardPane);

		writer.endElement("div");

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId + "_content", null);
		writer.writeAttribute("class", "ice-ace-dashboard-content ui-widget-content", null);
		String style = dashboardPane.getStyle();
		if (style != null) {
			writer.writeAttribute("style", style, null);
		}
	}

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {

        if (context == null || component == null) {
            throw new NullPointerException();
        }

		ResponseWriter writer = context.getResponseWriter();
		DashboardPane dashboardPane = (DashboardPane) component;

		if (dashboardPane.isClosed()) return;

        if (component.getChildCount() > 0) {
            Iterator<UIComponent> kids = component.getChildren().iterator();
            while (kids.hasNext()) {
                UIComponent kid = kids.next();
                kid.encodeAll(context);
            }
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		DashboardPane dashboardPane = (DashboardPane) component;

		if (dashboardPane.isClosed()) return;

		writer.endElement("div");

        if (dashboardPane.getFooterText() != null || dashboardPane.getFacet("footer") != null) {

			writer.startElement("div", component); // for display: table-row;

			encodeFooter(context, dashboardPane);

			writer.endElement("div");
		}

		writer.endElement("div"); // for display: table;

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
        
        ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
        writer.writeAttribute("class", "ui-widget-header ui-corner-bottom", null);

		// --- header text ---
        writer.startElement("span", null);
		writer.writeAttribute("id", dashboardPane.getClientId(context) + "_header", null);
        
        if (footerFacet != null)
            footerFacet.encodeAll(context);
        else if (footerText != null)
            writer.writeText(footerText, null);
		else writer.writeText("\u00A0", null);
        
        writer.endElement("span");

        writer.endElement("div");
	}
}