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

package org.icefaces.mobi.component.cloudpush;

import org.icefaces.mobi.renderkit.ResponseWriterWrapper;
import org.icefaces.util.ClientDescriptor;
import org.icefaces.util.EnvUtils;
import org.icepush.PushContext;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static org.icefaces.ace.util.HTML.*;

public class CloudPushRenderer extends Renderer {

    public void decode(FacesContext context, UIComponent component) {
        final String source = context.getExternalContext().getRequestParameterMap().get("javax.faces.source");
        final Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();

        if (component.getClientId().equals(source)) {
            sessionMap.put(CloudPushRenderer.class.getName(), true);
        }
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        CloudPush cloudPush = (CloudPush) uiComponent;
        ResponseWriterWrapper writer = new ResponseWriterWrapper(facesContext.getResponseWriter());
        String clientId = cloudPush.getClientId();
        final ClientDescriptor clientDescriptor = ClientDescriptor.getInstance((HttpServletRequest) facesContext.getExternalContext().getRequest());
		// button element
        final String email = cloudPush.getEmail();
        final boolean desktopBrowser = clientDescriptor.isDesktopBrowser() || clientDescriptor.isSimulator();

        final PushContext pushContext = PushContext.getInstance(EnvUtils.getSafeContext(facesContext));
        final Cookie browserIDCookie = (Cookie) facesContext.getExternalContext().getRequestCookieMap().get("ice.push.browser");
        final boolean cloudPushEnabled = browserIDCookie == null ? false : pushContext.hasNotifyBackURI(browserIDCookie.getValue());

        if (!cloudPushEnabled && (!desktopBrowser || (email != null && email.length() > 0))) {
            writer.startElement(BUTTON_ELEM, cloudPush);
            writer.writeAttribute(ID_ATTR, clientId);
			writer.writeAttribute(CLASS_ATTR, "mobi-cloud-push");
            writer.writeAttribute(NAME_ATTR, clientId + "_button");
            writer.writeAttribute(TYPE_ATTR, "submit");
            String script = "ice.s(this.form, this); " + (desktopBrowser ? "ice.push.parkInactivePushIds('mailto:" + email + "');" : "bridgeit.register();return false;");
            writer.writeAttribute(ONCLICK_ATTR, script);
            writer.startElement(SPAN_ELEM, cloudPush);
            writer.writeText(cloudPush.getButtonLabel());
            writer.endElement(SPAN_ELEM);
            // themeroller support
            writer.startElement("span", cloudPush);
            writer.startElement("script", cloudPush);
            writer.writeAttribute("type", "text/javascript");
            writer.writeText("new ice.mobi.button('"+clientId+"');");
            writer.endElement("script");
            writer.endElement("span");
            writer.endElement(BUTTON_ELEM);
        }
    }
}
