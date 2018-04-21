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

import org.icefaces.ace.json.*;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.impl.util.CoreUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Locale;
import java.util.ResourceBundle;

@MandatoryResourceComponent(tagName="dashboard", value="org.icefaces.ace.component.dashboard.Dashboard")
public class DashboardRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
		Map<String, String> requestParameterMap = context.getExternalContext().getRequestParameterMap();
		Dashboard dashboard = (Dashboard) component;
		String clientId = component.getClientId();

		// update all sizes and positions of all child dashboard panes
		if (requestParameterMap.containsKey(clientId + "_state")) {
			String raw = requestParameterMap.get(clientId + "_state");
			// TODO: save the submittedState in a transient property
			try {
				JSONArray array = new JSONArray(raw);
				int length = array.length();
				for (int i = 0; i < length; i++) {
					JSONObject pane = array.getJSONObject(i);
					String paneClientId = pane.getString("paneId");
					UIComponent c = CoreUtils.findComponentByClientId(context.getViewRoot(), paneClientId);
					if (c instanceof DashboardPane) {
						DashboardPane dashboardPane = (DashboardPane) c;
						dashboardPane.setSizeX(pane.getInt("sizeX"));
						dashboardPane.setSizeY(pane.getInt("sizeY"));
						dashboardPane.setRow(pane.getInt("row"));
						dashboardPane.setColumn(pane.getInt("column"));
					}
				}
			} catch (JSONException e) {

			}
		}
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

		List<boolean[]> positions = createPositionsList(dashboard);

		jb.beginArray();

		int maxColumns = dashboard.getMaxColumns();
		List<UIComponent> children = dashboard.getChildren();
		// render panes' settings in the order they appear
		for (int i = 0; i < children.size(); i++) {
			UIComponent child = children.get(i);
			if (child instanceof DashboardPane) {
				DashboardPane pane = (DashboardPane) child;
				int row = pane.getRow();
				int column = pane.getColumn();
				int sizeX = pane.getSizeX();

				// if not explicitly set, assign row and column position to pane
				if (!(row > 0 && column > 0 && column + sizeX - 1 <= maxColumns)) {
					System.out.println("* " + pane.getId());
					assignPosition(pane, positions, maxColumns);
				}

				// render pane's size and position settings
				jb.beginMap();
				jb.entry("paneId", pane.getClientId(context));
				jb.entry("sizeX", pane.getSizeX());
				jb.entry("sizeY", pane.getSizeY());
				jb.entry("row", pane.getRow());
				jb.entry("column", pane.getColumn());
				jb.endMap();
			}
		}

		jb.endArray();

		writer.write("ice.ace.Dashboard.data['"+clientId+"'] = " + jb.toString() + ";");

		// TODO: compare the generated state to the submittedState if available
		// if there are changes, reinit or refresh component

        writer.endElement("script");
        writer.endElement("span");
	}

    protected List<boolean[]> createPositionsList(Dashboard dashboard) {
		List<boolean[]> positions = new ArrayList<boolean[]>();

		int maxColumns = dashboard.getMaxColumns();
		List<UIComponent> children = dashboard.getChildren();
		for (int i = 0; i < children.size(); i++) {
			UIComponent child = children.get(i);
			if (child instanceof DashboardPane) {
				DashboardPane pane = (DashboardPane) child;
				int row = pane.getRow();
				int column = pane.getColumn();
				int sizeY = pane.getSizeY();
				int sizeX = pane.getSizeX();
				if (row > 0 && column > 0 && column + sizeX - 1 <= maxColumns) {
					int size = positions.size();
					if (row + sizeY - 1 > size) {
						for (int j = 0; j < (row + sizeY - 1 - size); j++) {
							positions.add(new boolean[maxColumns]);
						}
					}
					int rowIndex = row - 1;
					for (int j = 0; j < sizeY; j++) {
						boolean[] rowArray = positions.get(rowIndex);
						int columnIndex = column - 1;
						for (int k = 0; k < sizeX; k++) {
							rowArray[columnIndex] = true;
							columnIndex++;
						}
						rowIndex++;
					}
				}
			}
		}

		return positions;
	}

	protected void assignPosition(DashboardPane pane, List<boolean[]> positions, int maxColumns) {

		int sizeY = pane.getSizeY();
		int sizeX = pane.getSizeX();

		// make sure no pane takes more columns than the maximum allowed
		sizeX = sizeX > maxColumns ? maxColumns : sizeX;

		// if no rows yet, add one
		if (positions.size() == 0) {
			positions.add(new boolean[maxColumns]);
		}

		boolean assigned = false;
		for (int i = 0; i < positions.size(); i++) {

			// make sure there are enough rows in the position list to fit this pane
			// add enough rows to fit the size of this pane
			// add +1 more in case it doesn't fit at this row, so we can try the next row at the next iteration
			while (positions.size() < i + sizeY + 1) {
				positions.add(new boolean[maxColumns]);
			}

			boolean[] row = positions.get(i);
			for (int j = 0; j < maxColumns; j++) {
				// won't fit in this row due to horizontal size
				if (j + sizeX > maxColumns) {
					break;
				}

				// check if there's enough space for this pane starting at (i,j)
				if (row[j] == false) {

					boolean fits = true;
					for (int k = 0; k < sizeY; k++) {
						boolean[] sizeRow = positions.get(i + k);
						for (int l = 0; l < sizeX; l++) {
							if (sizeRow[j + l] == true) {
								fits = false;
								break;
							}
						}
						if (!fits) break;
					}

					if (fits) {System.out.println("-> fits!");
						// mark occupied spaces in the positions list
						for (int k = 0; k < sizeY; k++) {
							boolean[] sizeRow = positions.get(i + k);
							for (int l = 0; l < sizeX; l++) {
								sizeRow[j + l] = true;
							}
						}

						// assign row and column positions to pane
						pane.setRow(i+1);
						pane.setColumn(j+1);

						// signal that we've assigned a suitable position and exit algorithm
						assigned = true;
						break;
					}
				}
			}
			if (assigned) break;
		}
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

			// --- encode panes' settings ---
			jb.beginMap("panes");
			for (UIComponent child : dashboard.getChildren()) {
				if (child.isRendered() && child instanceof DashboardPane) {
					DashboardPane pane = (DashboardPane) child;

					jb.beginMap(pane.getClientId(context));

						encodeClientBehaviors(context, pane, jb);

					jb.endMap();
				}
			}
			jb.endMap();

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