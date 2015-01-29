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

package org.icefaces.ace.component.config;

import org.icefaces.ace.util.Constants;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.*;
import java.io.IOException;
import java.util.Map;

public class ConfigHandler extends ComponentHandler {
    private final TagAttribute theme;


    public ConfigHandler(ComponentConfig config) {
        super(config);
        this.theme = this.getAttribute("theme");
    }

    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        FacesContext fc = ctx.getFacesContext();
        UIViewRoot root = fc.getViewRoot();
        Map viewMap = root.getViewMap();

        if (theme != null) {
            viewMap.put(Constants.THEME_PARAM, theme.getValue());
        }
    }
}