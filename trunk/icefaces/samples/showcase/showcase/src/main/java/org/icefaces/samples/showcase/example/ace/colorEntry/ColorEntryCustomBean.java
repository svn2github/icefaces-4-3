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

package org.icefaces.samples.showcase.example.ace.colorEntry;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import org.icefaces.ace.component.colorentry.ColorFormat;

@ManagedBean(name= ColorEntryCustomBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ColorEntryCustomBean implements Serializable
{
    public static final String BEAN_NAME = "colorEntryCustomBean";
	public String getBeanName() { return BEAN_NAME; }
    
    private String value, chooseText="Choose", cancelText="Cancel", customStyleClass="colorEntryCustom1";
    private boolean showButtons=true;
    private ColorFormat valueFormat;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChooseText() {
        return chooseText;
    }

    public void setChooseText(String chooseText) {
        this.chooseText = chooseText;
    }

    public String getCancelText() {
        return cancelText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public String getCustomStyleClass() {
        return customStyleClass;
    }

    public void setCustomStyleClass(String customStyleClass) {
        this.customStyleClass = customStyleClass;
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public ColorFormat[] getColorFormats(){
        return ColorFormat.values();
    }
    public ColorFormat getValueFormat() {
        return valueFormat;
    }

    public void setValueFormat(ColorFormat valueFormat) {
        this.valueFormat = valueFormat;
    }
}
