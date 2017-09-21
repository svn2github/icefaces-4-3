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

@MandatoryResourceComponent(tagName = "gMap", value = "org.icefaces.ace.component.gmap.GMap")
public class GMapOverlayRenderer extends CoreRenderer {


    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {

        GMapOverlay overlay = (GMapOverlay) component;
        ResponseWriter writer = context.getResponseWriter();
        String clientId = overlay.getClientId(context);
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_overlay", null);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("ice.ace.jq(function() {");
		String mapClientId = GMapRenderer.getMapClientId(context, overlay);
        if (overlay.getPoints() != null && overlay.getShape() != null) {
			JSONBuilder jb = JSONBuilder.create();
			jb.beginFunction("ice.ace.gMap.removeGOverlay")
				.item(mapClientId)
				.item(clientId)
			.endFunction();
            writer.write(jb.toString());
			String shape = overlay.getShape();
			String points = overlay.getPoints();
            String options;
            try {
                options = (new JSONObject("{" + overlay.getOptions() + "}")).toString();
            } catch (JSONException e) {
                options = "{}";
            }
            jb = JSONBuilder.create();
			jb.beginFunction("ice.ace.gMap.gOverlay")
				.item(mapClientId)
				.item(clientId)
				.item(shape == null ? "" : shape)
				.item(points == null ? "" : points)
                    .item(options, false)
                    .endFunction();
            writer.write(jb.toString());
        }
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