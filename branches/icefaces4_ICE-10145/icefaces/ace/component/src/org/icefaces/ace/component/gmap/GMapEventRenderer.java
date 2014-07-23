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

import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.FacesException;
import java.io.IOException;

@MandatoryResourceComponent(tagName = "gMap", value = "org.icefaces.ace.component.gmap.GMap")
public class GMapEventRenderer extends CoreRenderer {


    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        GMapEvent gMapEvent = (GMapEvent) component;
        writer.startElement("span", null);
        writer.writeAttribute("id", clientId + "_layer", null);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.write("ice.ace.jq(function() {");
        String mapContext = null;
        UIComponent testComponent = component.getParent();
        while (testComponent != null) {
            if (testComponent instanceof GMap) {
                mapContext = testComponent.getClientId(context);
                break;
            } else {
                testComponent = testComponent.getParent();
			}
        }
		if (mapContext == null) {
			throw new FacesException("ace:gMapEvent component '" + gMapEvent.getId() + "' is not nested inside an ace:gMap component.");
		}
		JSONBuilder jb = JSONBuilder.create();
		jb.beginFunction("ice.ace.gMap.addEvent")
			.item(mapContext)
			.item(gMapEvent.getParent().getClientId(context))
			.item(clientId)
			.item(gMapEvent.getParent().getClass().getName())
			.item(gMapEvent.getEventType())
			.item(gMapEvent.getRendererType())
			.item(gMapEvent.getScriptToUse())
		.endFunction();
        writer.write(jb.toString());
        writer.write("});");
        writer.endElement("script");
        writer.endElement("span");

    }
}