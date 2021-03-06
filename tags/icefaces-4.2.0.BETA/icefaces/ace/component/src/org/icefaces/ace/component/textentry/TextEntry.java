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

package org.icefaces.ace.component.textentry;

import org.icefaces.ace.component.clientValidator.Validateable;
import org.icefaces.ace.event.CharCountEvent;
import org.icefaces.ace.util.Constants;
import org.icefaces.component.Focusable;
import org.icefaces.ace.util.Attribute;
import org.icefaces.ace.util.Utils;

import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Map;

public class TextEntry extends TextEntryBase implements Focusable, Validateable {
    public final static String THEME_INPUT_CLASS = "ui-inputfield ui-textentry ui-widget ui-state-default ui-corner-all";
    public final static String PLAIN_INPUT_CLASS = "ui-textentry";

    public String getFocusedElementId() {
        return getClientId() + "_input";
    }

    public String getValidatedElementId() {
        return getClientId() + "_input";
    }

    // ----------------------------------------
	// ----- imported from mobi:inputText -----
	// ----------------------------------------
	
    //passthrough attributes for textArea
    private Attribute[] textAreaAttributeNames = {
            new Attribute("cols", null),
            new Attribute("dirname", null),
            new Attribute("wrap", null),
            new Attribute("rows", null),
    };
    
    private Attribute[] numberAttributeNames = {
            new Attribute("type", null),
            new Attribute("min", null),
            new Attribute("max", null),
            new Attribute("step", null),
            new Attribute("rows", null),
    };

    //passthrough attributes for input text
    private Attribute[] inputtextAttributeNames = {
            new Attribute("autocomplete", null),
            new Attribute("autocapitalize", null),
            new Attribute("autocorrect", null),
            new Attribute("pattern", null),
            new Attribute("size", null)
    };

    private Attribute[] commonInputAttributeNames = {
            new Attribute("title", null),
            new Attribute("placeholder", null),
            new Attribute("maxlength", null),
            new Attribute("name", null),
            new Attribute("tabindex", null),
            new Attribute("style", null)
    };

    private Attribute[] booleanAttNames = {
            new Attribute("immediate", null)};


    public Attribute[] getBooleanAttNames() {
        return booleanAttNames;
    }

    public void setBooleanAttNames(Attribute[] booleanAttNames) {
        this.booleanAttNames = booleanAttNames;
    }

    public Attribute[] getTextAreaAttributeNames() {
        return textAreaAttributeNames;
    }

    public void setTextAreaAttributeNames(Attribute[] textAreaAttributeNames) {
        this.textAreaAttributeNames = textAreaAttributeNames;
    }

    public Attribute[] getInputtextAttributeNames() {
        return inputtextAttributeNames;
    }

    public void setInputTextAttributeNames(Attribute[] inputtextAttributeNames) {
        this.inputtextAttributeNames = inputtextAttributeNames;
    }

    public Attribute[] getCommonInputAttributeNames() {
        return commonInputAttributeNames;
    }

    public void setCommonInputAttributeNames(Attribute[] commonInputAttributeNames) {
        this.commonInputAttributeNames = commonInputAttributeNames;
    }

	public String validateType(String attributeType) {
	    if (attributeType.equals("text")||attributeType.equals("number")||attributeType.equals("url")||
	    		attributeType.equals("textarea")||attributeType.equals("phone")||attributeType.equals("email")|| 
	    		attributeType.equals("password")||attributeType.equals("date") || attributeType.equals("time")){
	    	return attributeType;
	    }
	    else return "text";
	}

	public Attribute[] getNumberAttributeNames() {
		return numberAttributeNames;
	}
    public String getDefaultEventName(FacesContext facesContext){
        return Utils.isTouchEventEnabled(facesContext) ? "onblur" : "onchange";
    }

    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if (eventName != null && eventName.equals("charCount")) {
            if (event instanceof AjaxBehaviorEvent) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                String val = (String) this.getSubmittedValue();
                long currentLength = (val == null || val.isEmpty()) ? 0 : val.length();
                long charsRemaining = this.getMaxlength() - currentLength;
                super.queueEvent(new CharCountEvent(this, behaviorEvent.getBehavior(), currentLength, charsRemaining));
            }
        } else {
            super.queueEvent(event);
        }
    }
}
