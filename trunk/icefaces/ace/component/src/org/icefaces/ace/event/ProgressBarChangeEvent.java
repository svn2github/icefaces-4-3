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

package org.icefaces.ace.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class ProgressBarChangeEvent extends FacesEvent {

    private double percentage;
    private int value;

    public ProgressBarChangeEvent(UIComponent component, String value, String percentage) {
        super(component);
        this.value = Integer.parseInt(value);
        this.percentage = Double.parseDouble(percentage);
    }

    @Override
    public boolean isAppropriateListener(FacesListener listener) {
        return false;
    }

    @Override
    public void processListener(FacesListener listener) {
        throw new UnsupportedOperationException();
    }

    public double getPercentage() {
        return percentage;
    }

    public int getValue() {
        return value;
    }
}
