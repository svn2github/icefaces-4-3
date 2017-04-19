package org.icefaces.ace.component.accordion;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.IOException;


public class AccordionPaneRenderer extends Renderer {

    public void encodeBegin(FacesContext context, UIComponent kid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        AccordionPane tab = (AccordionPane) kid;
        String clientId = kid.getClientId(context);

        //title
        writer.startElement("h3", null);
        writer.writeAttribute("id", clientId + "_header", null);
        String accesskey = tab.getAccesskey();
        if (accesskey != null) writer.writeAttribute("accesskey", accesskey, null);
        writer.startElement("a", null);
        writer.writeAttribute("href", "#", null);
        if (tab.getTitle() != null) {
            writer.write(tab.getTitle());
        }
        writer.endElement("a");
        writer.endElement("h3");
        //content
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_content", null);
        //help resizable component calculate properly its maximum bounds
        writer.writeAttribute("style", "height: 100%;", null);
    }

    public void encodeEnd(FacesContext context, UIComponent kid) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.endElement("div");
        writer.endElement("div");
    }
}
