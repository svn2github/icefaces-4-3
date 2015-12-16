/*
 * Original Code Copyright Prime Technology.
 * Subsequent Code Modifications Copyright 2011-2014 ICEsoft Technologies Canada Corp. (c)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE THIS CODE HAS BEEN MODIFIED FROM ORIGINAL FORM
 *
 * Subsequent Code Modifications have been made and contributed by ICEsoft Technologies Canada Corp. (c).
 *
 * Code Modification 1: Integrated with ICEfaces Advanced Component Environment.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 *
 * Code Modification 2: [ADD BRIEF DESCRIPTION HERE]
 * Contributors: ______________________
 * Contributors: ______________________
 */
package org.icefaces.ace.component.dnd;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.icefaces.ace.event.DragDropEvent;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.ComponentUtils;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.util.CoreComponentUtils;

import org.icefaces.ace.util.JSONBuilder;

@MandatoryResourceComponent(tagName="droppable", value="org.icefaces.ace.component.dnd.Droppable")
public class DroppableRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        Droppable droppable = (Droppable) component;
        String clientId = droppable.getClientId(context);
        String datasourceId = droppable.getDatasource();

        if(params.containsKey(clientId)) {
            String dragId = params.get(clientId + "_dragId");
            String dropId = params.get(clientId + "_dropId");
            DragDropEvent event = null;

            if(datasourceId != null) {
                UIData datasource = findDatasource(context, droppable, datasourceId);
                String[] idTokens = dragId.split(String.valueOf(UINamingContainer.getSeparatorChar(context)));
                int rowIndex;
				try {
					rowIndex = Integer.parseInt(idTokens[idTokens.length - 2]);
				} catch(Exception e) {
					rowIndex = -1;
				}
                datasource.setRowIndex(rowIndex);
                Object data = datasource.getRowData();
                datasource.setRowIndex(-1);

                event = new DragDropEvent(droppable, dragId, dropId, data);
            } else {
                event = new DragDropEvent(droppable, dragId, dropId);
            }

            droppable.queueEvent(event);
        }
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Droppable droppable = (Droppable) component;
        String target = findTarget(context, droppable).getClientId(context);
        String clientId = droppable.getClientId(context);

        // empty node with client id, needed for AJAX request
		writer.startElement("span", droppable);
		writer.writeAttribute("id", clientId, null);
		
		writer.startElement("script", droppable);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("ice.ace.jq(function() {");

		JSONBuilder jb = JSONBuilder.create();

        jb.beginFunction("ice.ace.create")
          .item("Droppable")
          .beginArray()
          .item(clientId)
          .beginMap()
          .entry("target", target);

        if (droppable.isDisabled()) jb.entry("disabled", true);
        if (droppable.getHoverStyleClass() != null) jb.entry("hoverClass", droppable.getHoverStyleClass());
        if (droppable.getActiveStyleClass() != null) jb.entry("activeClass", droppable.getActiveStyleClass());
        if (droppable.getAccept() != null) jb.entry("accept", droppable.getAccept());
        if (droppable.getScope() != null) jb.entry("scope", droppable.getScope());
        if (droppable.getTolerance() != null) jb.entry("tolerance", droppable.getTolerance());

        if (droppable.getDropListener() != null) {
            jb.entry("ajaxDrop", true);
        }

        encodeClientBehaviors(context, droppable, jb);

        jb.endMap().endArray().endFunction();

		writer.write(jb.toString());
        writer.write("});");
        writer.endElement("script");
		writer.endElement("span");
    }

    protected UIComponent findTarget(FacesContext facesContext, Droppable droppable) {
        String _for = droppable.getFor();

        if(_for != null) {
            UIComponent component = droppable.findComponent(_for);
            if (component == null)
                throw new FacesException("Cannot find component \"" + _for + "\" in view.");
            else
                return component;
        } else {
            return droppable.getParent();
        }
    }

    protected UIData findDatasource(FacesContext context, Droppable droppable, String datasourceId) {
        UIComponent datasource = droppable.findComponent(datasourceId);
		if (datasource == null) datasource = CoreComponentUtils.findComponentInView(context.getViewRoot(), datasourceId);
        
        if(datasource == null)
            throw new FacesException("Cannot find component \"" + datasourceId + "\" in view.");
        else
            return (UIData) datasource;
    }
}
