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

package org.icefaces.mobi.component.scan;


import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.icefaces.impl.application.AuxUploadResourceHandler;
import org.icefaces.mobi.renderkit.BaseInputRenderer;
import org.icefaces.mobi.renderkit.ResponseWriterWrapper;
import org.icefaces.mobi.util.MobiJSFUtils;
import org.icefaces.mobi.util.CSSUtils;
import org.icefaces.util.ClientDescriptor;
import org.icefaces.util.EnvUtils;

import static org.icefaces.mobi.util.HTML.*;

public class ScanRenderer extends BaseInputRenderer {
    private static final Logger logger = Logger.getLogger(ScanRenderer.class.getName());

    @Override
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        Scan scan = (Scan) uiComponent;
        String clientId = scan.getClientId();
        if (scan.isDisabled()) {
            return;
        }
        Map requestParameterMap = facesContext.getExternalContext()
                .getRequestParameterMap();
        String valueId = clientId;
        Object submitted = requestParameterMap.get(valueId);
        if (null == submitted)  {
            Map auxMap = AuxUploadResourceHandler.getAuxRequestMap();
            submitted = auxMap.get(valueId);
        }
        if (null != submitted) {
            String submittedString = String.valueOf(submitted);
            if (submittedString != null){
                Object convertedValue = this.getConvertedValue(facesContext, uiComponent, submittedString);
                this.setSubmittedValue(scan, convertedValue);
            }

        }
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        Scan scan = (Scan) uiComponent;
		ResponseWriterWrapper writer = new ResponseWriterWrapper(facesContext.getResponseWriter());
		String clientId = scan.getClientId();

		writer.startElement(SPAN_ELEM, scan);
		writer.writeAttribute(ID_ATTR, clientId);
        String oldLabel = scan.getButtonLabel();
        if (MobiJSFUtils.uploadInProgress(scan))  {
           scan.setButtonLabel(scan.getCaptureMessageLabel()) ;
        } 
        StringBuilder baseClass = new StringBuilder(CSSUtils.STYLECLASS_BUTTON);
        //ClientDescriptor cd = scan.getClient();
		// button element
		writer.startElement(BUTTON_ELEM, scan);
		writer.writeAttribute(ID_ATTR, clientId + "_button");
		writer.writeAttribute(NAME_ATTR, clientId + "_button");
		writer.writeAttribute(TYPE_ATTR, "button");
		//writeStandardAttributes(writer, scan, baseClass.toString(), IDevice.DISABLED_STYLE_CLASS);
		//default value of unset in params is Integer.MIN_VALUE
		String script = "bridgeit.scan('" + clientId + "', '', {postURL:'" + scan.getPostURL() + "', " 
		+ "cookies:{'JSESSIONID':'" + MobiJSFUtils.getSessionIdCookie(facesContext) +  "'}});";
		writer.writeAttribute(ONCLICK_ATTR, script);
		boolean disabled = scan.isDisabled();
		if (disabled) writer.writeAttribute(DISABLED_ATTR, "disabled");
		String style = scan.getStyle();
		if (style != null) writer.writeAttribute(STYLE_ATTR, style);
		String styleClass = scan.getStyleClass();
		if (styleClass != null) writer.writeAttribute(CLASS_ATTR, styleClass);
		writer.writeAttribute(TABINDEX_ATTR, scan.getTabindex());
		writer.startElement(SPAN_ELEM, scan);
		writer.writeText(scan.getButtonLabel());
		writer.endElement(SPAN_ELEM);

		writer.endElement(BUTTON_ELEM);

		writer.endElement(SPAN_ELEM);
        scan.setButtonLabel(oldLabel);
    }


}
