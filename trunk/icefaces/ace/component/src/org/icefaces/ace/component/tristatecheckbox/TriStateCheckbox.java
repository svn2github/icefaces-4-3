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

package org.icefaces.ace.component.tristatecheckbox;

import org.icefaces.ace.api.ButtonGroupMember;
import org.icefaces.component.Focusable;


public class TriStateCheckbox extends TriStateCheckboxBase implements Focusable, ButtonGroupMember {
    public static final String GROUP_LIST_KEY = "org.icefaces.ace.buttonGroup.LIST_KEY";
    public String getFocusedElementId() {
        return getClientId() + "_button";
    }

    public final static String UNCHECKED = "unchecked";
    public final static String CHECKED = "checked";
    public final static String PARTIALLY_CHECKED = "partial";

	public boolean isUnchecked() {
		return ((String) getValue()).equalsIgnoreCase(UNCHECKED);
	}

	public void setUnchecked() {
		setValue(UNCHECKED);
	}

	public boolean isPartiallyChecked() {
		return ((String) getValue()).equalsIgnoreCase(PARTIALLY_CHECKED);
	}

	public void setPartiallyChecked() {
		setValue(PARTIALLY_CHECKED);
	}

	public boolean isChecked() {
		return ((String) getValue()).equalsIgnoreCase(CHECKED);
	}

	public void setChecked() {
		setValue(CHECKED);
	}

	public void setValue(Object value) {
		String stringValue = (String) value;

		if (stringValue == null) {
			super.setValue(UNCHECKED);
			return;
		}

		if (stringValue.equalsIgnoreCase(PARTIALLY_CHECKED)) {
			super.setValue(PARTIALLY_CHECKED);
			return;
		}

		if (stringValue.equalsIgnoreCase(CHECKED)) {
			super.setValue(CHECKED);
			return;
		}

		super.setValue(UNCHECKED);
	}

	public Object getValue() {
		String stringValue = (String) super.getValue();

		if (stringValue == null) return UNCHECKED;

		if (stringValue.equalsIgnoreCase(PARTIALLY_CHECKED)) return PARTIALLY_CHECKED;

		if (stringValue.equalsIgnoreCase(CHECKED)) return CHECKED;

		return UNCHECKED;
	}
}

