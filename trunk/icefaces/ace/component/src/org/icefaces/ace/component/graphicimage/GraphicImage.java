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

package org.icefaces.ace.component.graphicimage;


import org.icefaces.ace.util.IceOutputResource;
import org.icefaces.application.ResourceRegistry;

import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GraphicImage extends GraphicImageBase {
    private static final Logger logger =
            Logger.getLogger(GraphicImage.class.toString());

    public String processSrcAttribute(FacesContext facesContext, Object o, String name, String library, String scope, String mimeType) {
        if (o instanceof IceOutputResource) {
            //register resource..
            IceOutputResource iceResource = (IceOutputResource) o;
            //set name for resource to component id??
            return registerAndGetPath(scope, iceResource);
        }

        if (o instanceof byte[]) {
            // have to create the resource first and cache it in ResourceRegistry
            //create IceOutputResource
            IceOutputResource ior = new IceOutputResource(name, library, o, mimeType);
            String registeredPath = registerAndGetPath(scope, ior);
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Returning path=" + registeredPath + " FOR SCOPE+" + scope);
            }
            return registeredPath;
        } else {
            String value = (String) super.getValue();
            // support url as an alias for value
            if (value == null) {
                value = super.getUrl();
            }
            if (value == null) {
                //assume that a resource is referenced
                ResourceHandler handler = facesContext.getApplication().getResourceHandler();
                Resource res = handler.createResource(name, library);
                if (res == null) {
                    if (facesContext.isProjectStage(ProjectStage.Development)) {
                        String msg = "Unable to find resource " + (library == null ? "" : library + ", ") + name;
                        facesContext.addMessage(getClientId(facesContext), new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
                    }
                    return "RES_NOT_FOUND";
                } else {
                    String requestPath = res.getRequestPath();
                    return facesContext.getExternalContext().encodeResourceURL(requestPath);
                }
            } else {
                return facesContext.getApplication().getViewHandler().getResourceURL(facesContext, value);
            }
        }
    }


    private String registerAndGetPath(String scope,
                                      IceOutputResource iceResource) {
        String registeredPath = "";
        if (scope.equals("application")) {
            registeredPath = ResourceRegistry.addApplicationResource(iceResource);
        } else if (scope.equals("window"))
            registeredPath = ResourceRegistry.addWindowResource(iceResource);
        else if (scope.equals("view"))
            registeredPath = ResourceRegistry.addViewResource(iceResource);
        else if (scope.equals("session"))
            registeredPath = ResourceRegistry.addSessionResource(iceResource);
        return registeredPath;
    }
}
