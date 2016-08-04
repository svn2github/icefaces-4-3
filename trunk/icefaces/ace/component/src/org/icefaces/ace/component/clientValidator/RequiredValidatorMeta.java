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

package org.icefaces.ace.component.clientValidator;

import org.icefaces.ace.meta.annotation.Component;
import org.icefaces.ace.meta.annotation.Property;
import org.icefaces.ace.meta.baseMeta.UIComponentBaseMeta;
import org.icefaces.ace.resources.ACEResourceNames;
import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import org.icefaces.resources.ICEResourceLibrary;

@Component(
        tagName         = "clientValidateRequired",
        componentClass  = "org.icefaces.ace.component.clientValidator.RequiredValidator",
        rendererClass   = "org.icefaces.ace.component.clientValidator.ValidatorRenderer",
        generatedClass  = "org.icefaces.ace.component.clientValidator.RequiredValidatorBase",
        extendsClass    = "javax.faces.component.UIComponentBase",
        componentType   = "org.icefaces.ace.component.RequiredValidator",
        rendererType    = "org.icefaces.ace.component.ValidatorRenderer",
        componentFamily = "org.icefaces.ace.component",
        tlddoc = "The clientValidateRequired component validates the input of the enclosing component by allowing the form submission to occur only when a non empty value has been entered." +
                "<p>For more information, see the <a href=\"http://wiki.icefaces.org/display/ICE/ClientValidateRequired\">ClientValidateRequired Wiki Documentation</a>."
)

@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
        @ICEResourceDependency(name = "jquery/validate/jquery.validate.js"),
        @ICEResourceDependency(name = "jquery/validate/additional-methods.js"),
        @ICEResourceDependency(name = "clientvalidator/clientvalidator.js")
})
public class RequiredValidatorMeta extends UIComponentBaseMeta {
        @Property(tlddoc = "Enables or disables required validation.", defaultValue="true")
        private Boolean enabled;
        @Property(tlddoc = "Trigger validation on specified event.", defaultValue="")
        private String validateOn;
}
