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
        //String clientId = borderLayoutPane.getClientId(context);

        writer.startElement("div", component);
        //writer.writeAttribute("id", clientId, null);
        //ComponentUtils.enableOnElementUpdateNotify(writer, clientId);

/*
		String style = borderLayoutPane.getStyle();
        if (style != null) {
            writer.writeAttribute("class", style, null);
        }
*/

		String position = borderLayoutPane.getPosition();
		if (position == null) return;
		position = position.toLowerCase();

		String baseClass = "ice-ace-boderlayout-pane ui-layout-" + position;
		String styleClass = borderLayoutPane.getStyleClass();
        if (styleClass != null) {
            baseClass += " " + styleClass;
        }
        writer.writeAttribute("class", baseClass, null);
	}

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.endElement("div");
    }
}