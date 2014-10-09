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

package org.icefaces.mobi.component.microphone;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.render.Renderer;

import org.icefaces.mobi.renderkit.ResponseWriterWrapper;
import org.icefaces.mobi.util.HTML;
import org.icefaces.mobi.util.MobiJSFUtils;
import org.icefaces.mobi.util.CSSUtils;
import org.icefaces.util.ClientDescriptor;
import org.icefaces.util.EnvUtils;

import static org.icefaces.mobi.util.HTML.*;

public class MicrophoneRenderer extends Renderer {
    private static final Logger logger = Logger.getLogger(MicrophoneRenderer.class.getName());

    @Override
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        Microphone microphone = (Microphone) uiComponent;
        String clientId = microphone.getClientId();
        if (microphone.isDisabled()) {
            return;
        }
        try {
            //MOBI-18 requirement is to decode the file from the map

            Map<String, Object> map = new HashMap<String, Object>();
            boolean valid = extractAudio(facesContext, map, clientId);
            if (valid){
               if (map !=null){
                  microphone.setValue(map);
             //   trigger valueChange and add map as newEvent value old event is NA
                  uiComponent.queueEvent(new ValueChangeEvent(uiComponent,
    		    		    null, map));
                }
            }
        } catch (Exception e) {
            logger.warning("Exception decoding audio stream: " + e);
        }
    }

    public boolean extractAudio(FacesContext facesContext, Map map, String clientId) throws IOException {
        return MobiJSFUtils.decodeComponentFile(facesContext, clientId, map);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        Microphone microphone = (Microphone) uiComponent;
		ResponseWriterWrapper writer = new ResponseWriterWrapper(facesContext.getResponseWriter());
		String clientId = microphone.getClientId();

		writer.startElement(SPAN_ELEM, microphone);
		writer.writeAttribute(ID_ATTR, clientId);
        String oldLabel = microphone.getButtonLabel();
        if (MobiJSFUtils.uploadInProgress(microphone))  {
           microphone.setButtonLabel(microphone.getCaptureMessageLabel()) ;
        } 
        //StringBuilder baseClass = new StringBuilder(CSSUtils.STYLECLASS_BUTTON);
        //ClientDescriptor cd = microphone.getClient();
		// button element
		writer.startElement(BUTTON_ELEM, microphone);
		writer.writeAttribute(ID_ATTR, clientId + "_button");
		writer.writeAttribute(NAME_ATTR, clientId + "_button");
		writer.writeAttribute(TYPE_ATTR, "button");
		if (microphone.isDisabled()) writer.writeAttribute(DISABLED_ATTR, "disabled");
		String style = microphone.getStyle();
		if (style != null) writer.writeAttribute(STYLE_ATTR, style);
		String styleClass = microphone.getStyleClass();
		if (styleClass != null) writer.writeAttribute(CLASS_ATTR, styleClass);
		writer.writeAttribute(TABINDEX_ATTR, microphone.getTabindex());
		//writeStandardAttributes(writer, microphone, baseClass.toString(), IDevice.DISABLED_STYLE_CLASS);
		//default value of unset in params is Integer.MIN_VALUE
		String script = "bridgeit.microphone('" + clientId + "', '', {postURL:'" + microphone.getPostURL() + "', "
        + "cookies:{'JSESSIONID':'" + MobiJSFUtils.getSessionIdCookie(facesContext) +  "'}});";
		writer.writeAttribute(ONCLICK_ATTR, script);
		writer.startElement(SPAN_ELEM, microphone);
		writer.writeText(microphone.getButtonLabel());
		writer.endElement(SPAN_ELEM);

		writer.endElement(BUTTON_ELEM);

		writer.endElement(SPAN_ELEM);
        microphone.setButtonLabel(oldLabel);
    }
}
