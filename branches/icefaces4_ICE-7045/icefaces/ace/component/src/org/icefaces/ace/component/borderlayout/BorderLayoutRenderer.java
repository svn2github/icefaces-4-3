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

import org.icefaces.ace.renderkit.CoreRenderer;

import org.icefaces.ace.util.HTML;
import org.icefaces.ace.util.Utils;
import org.icefaces.ace.util.JSONBuilder;
import org.icefaces.render.MandatoryResourceComponent;
import org.icefaces.ace.model.borderlayout.PanelDefaultModel;

@MandatoryResourceComponent(tagName="borderLayout", value="org.icefaces.ace.component.borderlayout.BorderLayout")
public class BorderLayoutRenderer extends CoreRenderer {
     private final static Logger logger = Logger.getLogger(BorderLayoutRenderer.class.getName());

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        BorderLayout container = (BorderLayout) component;

        encodeMarkup(facesContext, container);
    }


    protected void encodeMarkup(FacesContext context, BorderLayout container) throws IOException {
        //must have a center borderPanel so perhaps do check here?
		Map<String, Object> domUpdateMap = new HashMap<String, Object>();
        ResponseWriter writer = context.getResponseWriter();
        String clientId = container.getClientId(context);
        //may need an outer div for overflow control...will wait and seee
        writer.startElement("div", container);
        writer.writeAttribute("class", "ui-container-wrapper", "class");
        writer.startElement("div", container);
        writer.writeAttribute("id", clientId, null);
		
        String styleClass = container.getStyleClass() ;
		Utils.writeConcatenatedStyleClasses(writer, BorderLayout.BORDERLAYOUT_CLASS, styleClass);
		String style = container.getStyle();
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }

      //  encodeContent(context, container);
        renderChildren(context, container);
		
		encodeScript(context, container);

        writer.endElement("div");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, BorderLayout panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);

        JSONBuilder jb = JSONBuilder.create()
            .initialiseVar(this.resolveWidgetVar(panel))
            .beginFunction("ice.ace.create")
            .item("BorderLayout")
            .beginArray()
			.item(clientId)
			.beginMap() ;
        PanelDefaultModel defaults = panel.getDefaults();
        boolean haveOptions = false;
        if (defaults != null){
	//	    JSONBuilder cfg = defaults.getDefaultJSONObject();
            defaults.getDefaultJSONObject(jb);
            haveOptions = true;
 // logger.info(" afer defaults = " + jb.toString());
        }
        if (panel.getNorthVar() !=null){
 //  logger.info(" have northVar = "+ panel.getNorthVar().toString());
            jb.appendToOriginal(panel.getNorthVar().toString(), haveOptions);
        }
        if (panel.getEastVar() !=null){
            jb.appendToOriginal(panel.getEastVar().toString(), true);
        }
        if (panel.getSouthVar() !=null){
            jb.appendToOriginal(panel.getSouthVar().toString(), true);
        }
        if (panel.getCenterVar() !=null){
            jb.appendToOriginal(panel.getCenterVar().toString(), true);
        }
        if (panel.getWestVar() !=null){
            jb.appendToOriginal(panel.getWestVar().toString(), true);
        }
        if (panel.getCenterVar() !=null){
            jb.appendToOriginal(panel.getCenterVar().toString(), true );
        }
        jb.endMap().endArray().endFunction();
  // logger.info(" script = "+jb.toString());
		writer.write(jb.toString());
        writer.endElement("script");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
