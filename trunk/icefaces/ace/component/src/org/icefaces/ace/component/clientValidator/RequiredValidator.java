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

import org.icefaces.ace.component.message.Message;
import org.icefaces.ace.renderkit.CoreRenderer;
import org.icefaces.ace.util.ComponentUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

public class RequiredValidator extends RequiredValidatorBase {

    public void encodeBegin(FacesContext context) throws IOException {
        UIComponent validatedComponent = getParent();
        if (validatedComponent instanceof Validateable) {
            final Validateable v = (Validateable) validatedComponent;
            final String id = v.getValidatedElementId();
            final String messageClientId = MessageMatcher.lookupMessageClientId(validatedComponent);
            final UIComponent form = ComponentUtils.findParentForm(context, validatedComponent);
            final List<UIComponent> children = form.getChildren();
            final ResourceBundle bundle = CoreRenderer.getComponentResourceBundle(FacesContext.getCurrentInstance(), "org.icefaces.ace.resources.messages");
            final String message = CoreRenderer.getLocalisedMessageFromBundle(bundle,
                    "org.icefaces.ace.component.clientvalidation.", "required", "{0} is required.");
            final String label = MessageMatcher.lookupLabel(validatedComponent);

            final StringBuffer script = new StringBuffer();
            script.append("ice.ace.setupClientValidation('");
            script.append(id);
            script.append("', '");
            script.append("required");
            script.append("', ");
            script.append(isEnabled());
            script.append(", '");
            script.append(messageClientId);
            script.append("', '");
            script.append(MessageFormat.format(message, label));
            script.append("', ");
            script.append(MessageMatcher.isMultipleMessage(validatedComponent));
            script.append(")");

            children.add(new ScriptOutputWriter(script.toString()));
        }
    }

    public void encodeEnd(FacesContext context) throws IOException {
    }
}
