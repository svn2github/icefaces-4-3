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

package com.icesoft.faces.component.ext.renderkit;

import com.icesoft.faces.component.IceExtended;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;

import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.Set;

public class SelectManyCheckboxListRenderer extends
                                            com.icesoft.faces.renderkit.dom_html_basic.SelectManyCheckboxListRenderer {

    protected Element getTableElement(DOMContext domContext) {
        return (Element) domContext.getRootNode().getFirstChild();
    }
    
    protected Element createRootNode(DOMContext domContext) {
        Element rootElement = domContext.createRootElement(HTML.FIELDSET_ELEM);
        Element tableElement = domContext.createElement(HTML.TABLE_ELEM);
        rootElement.appendChild(tableElement);
        return rootElement;
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiComponent, Element root,
                                 Set excludes) {
        if (((IceExtended) uiComponent).getPartialSubmit()) {
            root.setAttribute(getEventType(uiComponent),
                              "setFocus('');" + this.ICESUBMITPARTIAL);
            excludes.add(getEventType(uiComponent));
        }
    }
}
