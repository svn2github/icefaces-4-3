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


package org.icefaces.impl.context;

import org.icefaces.util.EnvUtils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICEFacesContextFactory extends FacesContextFactory {
    private static Logger log = Logger.getLogger(ICEFacesContextFactory.class.getName());
    public static String AJAX_FORCED_VIEWS = "org.icefaces.ajaxforcedviews";

    FacesContextFactory delegate;

    public ICEFacesContextFactory(FacesContextFactory delegate) {
        this.delegate = delegate;
    }

    public FacesContextFactory getWrapped() {
        return delegate;
    }


    /**
     * <p>Create and return a {@link FacesContext} instance.</p>
     */
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle) {
        //request will be wrapped if necessary, otherwise left unchanged
        Object wrappedRequest = request;

        Object ajaxForcedViews = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            HttpSession session = servletRequest.getSession(false);
            if (null != session) {
                ajaxForcedViews = session.getAttribute(AJAX_FORCED_VIEWS);
            }
            if (null != ajaxForcedViews) {
                wrappedRequest = wrapIfMultipart(servletRequest);
            }
        }

        FacesContext nativeFacesContext = delegate.getFacesContext(context, wrappedRequest, response, lifecycle);

        //This call is necessary if you want to be able to
        //query request parameters in the resource handlers - particularly in Glassfish
        //which actively prevents setting the encoding if the request parameters have
        //already been read (Tomcat seems to be more forgiving).

        //The character encoding detection code is essentially the same logic as is used in
        //the ViewHandler of the Mojarra implementation which is where the character encoding
        //is currently detected and applied, albeit too late for our purposes.


        // ICE-7791: WebSphere Portal logs error messages if we attempt to set the encoding
        // here.  Since it's really a workaround for Glassfish, it's okay to avoid it for
        // WebSphere Portal.
        if (!EnvUtils.isWebSpherePortal()) {
            setCharacterEncoding(nativeFacesContext);
        }

        return new ICEfacesContext(nativeFacesContext);
    }

    private Object wrapIfMultipart(HttpServletRequest request) {
        if ((null != request.getContentType())
                && request.getContentType().contains("multipart")) {
            return new AjaxForcedRequestWrapper(request);
        }
        return request;
    }

    private void setCharacterEncoding(FacesContext context) {
        String calculatedEncoding = EnvUtils.calculateCharacterEncoding(context);
        if (null != calculatedEncoding) {
            ExternalContext ec = context.getExternalContext();
            String currentEncoding = ec.getRequestCharacterEncoding();
            try {
                if (!calculatedEncoding.equals(currentEncoding)) {
                    ec.setRequestCharacterEncoding(calculatedEncoding);
                }
            } catch (UnsupportedEncodingException e) {
                if (log.isLoggable(Level.WARNING)) {
                    log.warning("can't set encoding to [" + calculatedEncoding + "], current encoding is [" + currentEncoding + "]");
                }
            }
        } else {
            if (log.isLoggable(Level.FINE)) {
                log.fine("character encoding could not be determined");
            }
        }
    }
}

class AjaxForcedRequestWrapper extends HttpServletRequestWrapper {
    private HttpServletRequest wrapped;

    public AjaxForcedRequestWrapper(HttpServletRequest wrapped) {
        super(wrapped);
        this.wrapped = wrapped;
    }

    public String getHeader(String name) {
        if (name.equals("Faces-Request")) {
            return "partial/ajax";
        }
        return wrapped.getHeader(name);
    }
}

