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

package org.icefaces.ace.component.gmap;

import org.icefaces.ace.event.MarkerDragDropEvent;
import org.icefaces.ace.json.JSONException;
import org.icefaces.ace.json.JSONObject;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@MandatoryResourceComponent(tagName = "gMap", value = "org.icefaces.ace.component.gmap.GMap")
public class GMapMarkerRenderer extends CoreRenderer {

	@Override
	public void decode(FacesContext context, UIComponent component) {

		GMapMarker marker = (GMapMarker) component;
		String clientId = marker.getClientId(context);
		Map<String, String> params = context.getExternalContext().getRequestParameterMap();

		if (params.containsKey(clientId + "_lat") && marker.getDragDropListener() != null) {
			String newLatitude = params.get(clientId + "_lat");
			String newLongitude = params.get(clientId + "_lng");
			if (newLatitude != null && !"".equals(newLatitude)
				&& newLongitude != null && !"".equals(newLongitude)) {
				MarkerDragDropEvent event = new MarkerDragDropEvent(marker, 
					newLatitude,
					newLongitude,
					marker.getLatitude(),
					marker.getLongitude());
				marker.queueEvent(event);
				marker.setLatitude(newLatitude);
				marker.setLongitude(newLongitude);
			}
		}
	}

    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        GMapMarker marker = (GMapMarker) component;
        String clientId = marker.getClientId(context);
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_marker", null);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("ice.ace.jq(function() {");
        String address = marker.getAddress();
        String currentLat = marker.getLatitude();
        String currentLon = marker.getLongitude();
		String mapClientId = GMapRenderer.getMapClientId(context, marker);
		JSONBuilder jb;
        //create a marker if lat and lon defined on the component itself
        if (!marker.isDisabled()) {
            String options = marker.getOptions() == null ? "{}" : "{" + marker.getOptions() + "}";

            if (address != null && !"".equals(address)) {
                jb = JSONBuilder.create();
				jb.beginFunction("ice.ace.gMap.removeMarker")
					.item(mapClientId)
					.item(clientId)
					.item(true) // persist object, since it's going to be updated
				.endFunction();
				writer.write(jb.toString());
				if (marker.getOptions() != null) {
					jb = JSONBuilder.create();
					jb.beginFunction("ice.ace.gMap.addMarker")
						.item(mapClientId)
						.item(clientId)
						.item("")
						.item("")
						.item(address)
                        .item(options, false)
                        .item(marker.getDragDropListener() != null)
                    .endFunction();
					writer.write(jb.toString());
				} else {
					jb = JSONBuilder.create();
					jb.beginFunction("ice.ace.gMap.addMarker")
						.item(mapClientId)
						.item(clientId)
						.item("")
						.item("")
						.item(address)
						.item("")
                        .item(marker.getDragDropListener() != null)
					.endFunction();
					writer.write(jb.toString());
				}
            } else if (currentLat != null && currentLon != null
                    && currentLat.length() > 0 && currentLon.length() > 0) {
				jb = JSONBuilder.create();
				jb.beginFunction("ice.ace.gMap.removeMarker")
					.item(mapClientId)
					.item(clientId)
					.item(true) // persist object, since it's going to be updated
				.endFunction();
				writer.write(jb.toString());
				if (marker.getOptions() != null) {
					jb = JSONBuilder.create();
					jb.beginFunction("ice.ace.gMap.addMarker")
						.item(mapClientId)
						.item(clientId)
						.item(currentLat)
						.item(currentLon)
						.item("")
                        .item(options, false)
                        .item(marker.getDragDropListener() != null)
                    .endFunction();
					writer.write(jb.toString());
				} else {
					jb = JSONBuilder.create();
					jb.beginFunction("ice.ace.gMap.addMarker")
						.item(mapClientId)
						.item(clientId)
						.item(currentLat)
						.item(currentLon)
						.item("")
                        .item("")
                        .item(marker.getDragDropListener() != null)
					.endFunction();
					writer.write(jb.toString());
				}
            }
            if (marker.getAnimation() != null) {
				jb = JSONBuilder.create();
				jb.beginFunction("ice.ace.gMap.animateMarker")
					.item(mapClientId)
					.item(clientId)
					.item(marker.getAnimation())
				.endFunction();
				writer.write(jb.toString());				
			}
        } else {
			jb = JSONBuilder.create();
			jb.beginFunction("ice.ace.gMap.removeMarker")
				.item(mapClientId)
				.item(clientId)
				.item(true) // persist object, since it's going to be updated
			.endFunction();
			writer.write(jb.toString());
        }
        writer.write("});");
		writer.write("ice.onElementRemove(\"" + clientId + "_marker\", function() {");
		jb = JSONBuilder.create();
		jb.beginFunction("ice.ace.gMap.removeMarker")
			.item(mapClientId)
			.item(clientId)
		.endFunction();
		writer.write(jb.toString());
		writer.write("});");
        writer.endElement("script");
        writer.endElement("span");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        if (component.getChildCount() == 0) return;
        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            kid.encodeBegin(context);
            if (kid.getRendersChildren()) {
                kid.encodeChildren(context);
            }
            kid.encodeEnd(context);
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}