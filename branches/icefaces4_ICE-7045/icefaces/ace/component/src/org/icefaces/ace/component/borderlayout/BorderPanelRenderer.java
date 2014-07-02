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
 * Code Modification 2: (ICE-6978) Used JSONBuilder to add the functionality of escaping JS output.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 */
package org.icefaces.ace.component.borderlayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.icefaces.ace.component.menu.Menu;
import org.icefaces.ace.renderkit.CoreRenderer;

import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.Utils;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.ace.model.borderlayout.PanelModel;


public class BorderPanelRenderer extends CoreRenderer {
    private final static Logger logger = Logger.getLogger(BorderPanelRenderer.class.getName());
    @Override
    public void decode(FacesContext context, UIComponent component) {
        BorderPanel panel = (BorderPanel) component;
        String clientId = panel.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        //Restore toggle state
  /*      String collapsedParam = params.get(clientId + "_collapsed");
        if(collapsedParam != null) {
            panel.setCollapsed(Boolean.valueOf(collapsedParam));
        } */

        //Restore visibility state DO I NEED THIS?
   /*     String visibleParam = params.get(clientId + "_visible");
        if(visibleParam != null) {
            panel.setVisible(Boolean.valueOf(visibleParam));
        }  */

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        BorderPanel panel = (BorderPanel) component;

        encodeMarkup(facesContext, component);
    }

    protected void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
		Map<String, Object> domUpdateMap = new HashMap<String, Object>();
        ResponseWriter writer = context.getResponseWriter();
        BorderPanel panel = (BorderPanel) component;
        String clientId = panel.getClientId(context);
        BorderLayout bl = null;
        UIComponent parentLayout = component.getParent();
        if (!(parentLayout instanceof BorderLayout)){
            logger.info(" borderPanel must be direct child of borderLayout component");
            return;
        } else {
           bl = (BorderLayout)parentLayout;
        }
        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, null);
  //      String styleClass = panel.getStyleClass() ;
        String panelClass = BorderPanel.PANEL_CENTER; //default
        Boolean resizable = panel.isResizable();
        Boolean closable = panel.isClosable();
        Boolean slidable = panel.isSlidable();
        Location myLocation = panel.getLocation();
        JSONBuilder jb = null;
//  logger.info(" location is ="+myLocation.toString());
        if (panel.getModel() != null){
            PanelModel pm = panel.getModel();
            if (panel !=null){
                jb = pm.getPanelOptionsJSON(myLocation.toString().toLowerCase(), resizable, closable, slidable);
            }
        }
        else {
            jb = new JSONBuilder();
            jb.beginMap(myLocation.toString().toLowerCase());
            if (resizable !=null){
                jb.entry("resizable", resizable);
            }
            if (closable!=null){
                jb.entry("closable", closable);
            }
            if (slidable !=null){
                jb.entry("slidable", slidable);
            }
            jb.endMap();
        }
        if (myLocation == null){
            //default to center
            myLocation = Location.CENTER;
            if (bl!=null && jb!=null){
                bl.setCenterVar(jb);
            }
        }
        if (myLocation == Location.NORTH){
            panelClass = BorderPanel.PANEL_NORTH;
            if (bl!=null && jb!=null){
                bl.setNorthVar(jb);
            }
        }
        else if (myLocation==Location.SOUTH){
            panelClass = BorderPanel.PANEL_SOUTH;
            if (bl!=null && jb!=null){
                bl.setSouthVar(jb);
            }
        }
        else if (myLocation== Location.EAST){
            panelClass = BorderPanel.PANEL_EAST;
            if (bl!=null && jb!=null){
                bl.setEastVar(jb);
            }
        }
        else if (myLocation==Location.WEST){
            panelClass = BorderPanel.PANEL_WEST;
            if (bl!=null && jb!=null){
                bl.setWestVar(jb);
            }
        }
		//Utils.writeConcatenatedStyleClasses(writer, panelClass, styleClass);
        writer.writeAttribute("class", panelClass, "class");
		String style = panel.getStyle();
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

  //      encodeHeader(context, panel, domUpdateMap);
        encodeContent(context, panel);
  //      encodeFooter(context, panel);

   /*     if(panel.isToggleable()) {
            encodeStateHolder(context, panel, clientId + "_collapsed", String.valueOf(panel.isCollapsed()));
        }

        if(panel.isClosable()) {
            encodeStateHolder(context, panel, clientId + "_visible", String.valueOf(panel.isVisible()));
        }   */

 /*       writer.startElement("span", null);
        writer.writeAttribute("data-hashcode", domUpdateMap.hashCode(), null);
        writer.writeAttribute("class", "ui-helper-hidden", null);
        writer.endElement("span"); */
        writer.endElement("div");
		

    }

    protected void encodeContent(FacesContext facesContext, BorderPanel panel) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

     /*   writer.startElement("div", null);
        writer.writeAttribute("id", panel.getClientId() + "_content", null);
        writer.writeAttribute("class", BorderPanel.PANEL_CONTENT_CLASS, null);
        if (panel.isCollapsed()) {
            writer.writeAttribute("style", "display:none", null);
        } */

        renderChildren(facesContext, panel);

      /*  writer.endElement("div"); */
    }


 /*   protected void encodeStateHolder(FacesContext context, BorderPanel panel, String name, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("value", value, null);
        writer.writeAttribute(HTML.AUTOCOMPLETE_ATTR, "off", null);
        writer.endElement("input");
    }    */

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
