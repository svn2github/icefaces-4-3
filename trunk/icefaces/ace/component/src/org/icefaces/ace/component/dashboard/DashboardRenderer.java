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
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.ace.util.ComponentUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Locale;
import java.util.ResourceBundle;

@MandatoryResourceComponent(tagName="dashboard", value="org.icefaces.ace.component.dashboard.Dashboard")
public class DashboardRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Dashboard dashboard = (Dashboard) component;
        String clientId = dashboard.getClientId(context);
        ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);

		String baseClass = "ice-ace-dashboard";
		String styleClass = dashboard.getStyleClass();
        if (styleClass != null) {
            baseClass += " " + styleClass;
        }
        writer.writeAttribute("class", baseClass, null);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Dashboard dashboard = (Dashboard) component;

		encodeSizeAndPositionData(context, dashboard);

		encodeScript(context, dashboard);

        writer.endElement("div");
    }

    protected void encodeSizeAndPositionData(FacesContext context, Dashboard dashboard) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dashboard.getClientId(context);

        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_sizeAndPositionData", null);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		JSONBuilder jb = JSONBuilder.create();

		jb.beginArray();

		int row = 1;
		int column = 1;
		int maxColumns = dashboard.getMaxColumns();
		List<UIComponent> children = dashboard.getChildren();
		for (int i = 0; i < children.size(); i++) {
			UIComponent child = children.get(i);
			if (child instanceof DashboardPane) {
				DashboardPane pane = (DashboardPane) child;
				jb.beginMap();
				jb.entry("paneId", pane.getClientId(context));
				jb.entry("sizeX", pane.getSizeX());
				jb.entry("sizeY", pane.getSizeY());
				jb.entry("row", row);
				jb.entry("column", column);
				jb.endMap();

				column += pane.getSizeX();
				if (column > maxColumns) {
					row++;
					column = 1;
				}
			}
		}

		jb.endArray();

		writer.write("ice.ace.Dashboard.data['"+clientId+"'] = " + jb.toString() + ";");

        writer.endElement("script");
        writer.endElement("span");
	}

    protected void encodeScript(FacesContext context, Dashboard dashboard) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = dashboard.getClientId(context);

		/*
		Locale locale = context.getViewRoot().getLocale();
		String bundleName = context.getApplication().getMessageBundle();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (bundleName == null) bundleName = "org.icefaces.ace.resources.messages";
		if (classLoader == null) classLoader = bundleName.getClass().getClassLoader();
		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, classLoader);
		final String MESSAGES_PREFIX = "org.icefaces.ace.component.dashboard.";
		*/

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        JSONBuilder jb = JSONBuilder.create();
        jb.beginFunction("ice.ace.create")
			.item("Dashboard")
			.beginArray()
			.item(clientId)
			.beginMap();

				jb.entry("resizable", dashboard.isResizable())
				.entry("paneWidth", dashboard.getPaneWidth())
				.entry("paneHeight", dashboard.getPaneHeight())
				.entry("marginX", dashboard.getMarginX())
				.entry("marginY", dashboard.getMarginY())
				.entry("maxColumns", dashboard.getMaxColumns());

/*
			String style = dashboard.getStyle();
			if (style != null) {
				jb.entry("style", style);
			}
*/

/*
			// --- encode panes' settings ---
			for (UIComponent child : dashboard.getChildren()) {
				if (child.isRendered() && child instanceof DashboardPane) {
					DashboardPane pane = (DashboardPane) child;

					jb.beginMap(pane.getPosition())
						.entry("paneSelector", ".ice-ace-boderlayout")
						.entry("size", pane.getSize())
						.entry("resizable", pane.isResizable())
						.entry("closable", pane.isClosable() || pane.isToggleable())
						.entry("minSize", pane.getMinSize())
						.entry("maxSize", pane.getMaxSize())
						.entry("spacing_open", pane.getBorderWidth())

						.entry("togglerLength_open", 0) // hide default toggler button
						.entry("togglerLength_closed", "100%"); // use the whole bar to open collapsed pane
					
						if (pane.isToggleable()) {
							jb.entry("spacing_closed", pane.getCollapseSize());
						}
					
						jb.entry("initHidden", !pane.isVisible())
						.entry("initClosed", pane.isCollapsed())
						.entryNonNullValue("fxName", pane.getEffect())
						.entry("fxSpeed", pane.getEffectLength());
						//.entry("resizerTip", bundle.getString(MESSAGES_PREFIX + "resizeTitle"))
						//.entry("togglerTip_closed", bundle.getString(MESSAGES_PREFIX + "expandTitle"));

						encodeClientBehaviors(context, pane, jb);

					jb.endMap();
				}
			}
*/

        jb.endMap().endArray();
		jb.endFunction();

		writer.write(jb.toString());

        writer.endElement("script");

		// draggable script
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_draggableScript", null);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
		if (dashboard.isDraggable())
			writer.write("(function(){ ice.ace.instance('"+clientId+"').enableDragging(); })();");
		else
			writer.write("(function(){ ice.ace.instance('"+clientId+"').disableDragging(); })();");
        writer.endElement("script");
        writer.endElement("span");
    }
}