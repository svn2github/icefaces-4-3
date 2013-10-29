/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
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

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContextFactory;
import javax.faces.context.PartialViewContext;

public class DOMPartialViewContextFactory extends PartialViewContextFactory  {
    PartialViewContextFactory delegate;

    public DOMPartialViewContextFactory(PartialViewContextFactory delegate)  {
        this.delegate = delegate;
    }

    public PartialViewContextFactory getWrapped()  {
        return delegate;
    }


    /**
     * <p>Create (if needed)
     * and return a {@link PartialViewContext} instance that is initialized
     * using the current {@link FacesContext} instance.</p>
     *
     * @param facesContext the {@link FacesContext} for the current request.
     */
    public PartialViewContext getPartialViewContext(FacesContext facesContext)  {
        return new DOMPartialViewContext(delegate.getPartialViewContext(facesContext), facesContext);
    }
    
}
