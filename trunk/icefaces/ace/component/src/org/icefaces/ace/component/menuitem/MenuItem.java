/*
 * Original Code Copyright Prime Technology.
 * Subsequent Code Modifications Copyright 2011-2014 ICEsoft Technologies Canada Corp. (c)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * NOTE THIS CODE HAS BEEN MODIFIED FROM ORIGINAL FORM
 *
 * Subsequent Code Modifications have been made and contributed by ICEsoft Technologies Canada Corp. (c).
 *
 * Code Modification 1: Integrated with ICEfaces Advanced Component Environment.
 * Contributors: ICEsoft Technologies Canada Corp. (c)
 *
 * Code Modification 2: [ADD BRIEF DESCRIPTION HERE]
 * Contributors: ______________________
 * Contributors: ______________________
 */


package org.icefaces.ace.component.menuitem;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.Map;
import java.util.List;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.ClientBehaviorHolder;

import javax.faces.application.*;
import javax.faces.event.ActionListener;

public class MenuItem extends MenuItemBase implements java.io.Serializable {
	
    public void decode(FacesContext facesContext) {
	
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(facesContext);
        if (params.containsKey(clientId)) this.queueEvent(new ActionEvent(this));

        // decode bahaviors 
		Map<String, List<ClientBehavior>> behaviors = getClientBehaviors();
        if(behaviors.isEmpty()) {
            return;
        }
		
        String behaviorEvent = params.get("javax.faces.behavior.event");

        if(null != behaviorEvent) {
            List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);

            if(behaviors.size() > 0) {
               String behaviorSource = params.get("javax.faces.source");

               if(behaviorSource != null && behaviorSource.startsWith(clientId)) {
                   for (ClientBehavior behavior: behaviorsForEvent) {
                       behavior.decode(facesContext, this);
                   }
               }
            }
        }
    }

    public boolean shouldRenderChildren() {
        if (getChildCount() == 0)
            return false;
        else {
            for (UIComponent child : getChildren()) {
                if (!(child instanceof UIParameter)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public NavigationCase getNavigationCase(FacesContext context) {
        NavigationHandler navHandler = context.getApplication().getNavigationHandler();
        if (!(navHandler instanceof ConfigurableNavigationHandler)) {
            return null;
        }

        String outcome = getOutcome();
        if (outcome == null) {
            outcome = context.getViewRoot().getViewId();
        }
        String toFlowDocumentId = (String) getAttributes().get(ActionListener.TO_FLOW_DOCUMENT_ID_ATTR_NAME);
        NavigationCase navCase = null;
        if (null == toFlowDocumentId) {
            navCase = ((ConfigurableNavigationHandler) navHandler).getNavigationCase(context, null, outcome);            
        } else {
            navCase = ((ConfigurableNavigationHandler) navHandler).getNavigationCase(context, null, outcome, toFlowDocumentId);            
        }

        return navCase;
    }

    public String getEncodedTargetURL(FacesContext context, NavigationCase navCase) {
        String toViewId;
		if (navCase != null) {
			toViewId = navCase.getToViewId(context);
		} else {
			toViewId = getOutcome();
			if (toViewId == null) {
				toViewId = context.getViewRoot().getViewId();
			} else if (!"".equals(toViewId) && !toViewId.substring(0, 1).equals("/")) {
				toViewId = "/" + toViewId;
			}
		}
		Map<String,List<String>> params = new java.util.HashMap<String,List<String>>();
        String result = null;
        
		result = context.getApplication().getViewHandler().getBookmarkableURL(context,
			toViewId, params, false);
        
        return result;
    }
}
