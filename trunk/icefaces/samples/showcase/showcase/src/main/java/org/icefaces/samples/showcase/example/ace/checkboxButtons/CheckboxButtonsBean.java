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

package org.icefaces.samples.showcase.example.ace.checkboxButtons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name= CheckboxButtonsBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class CheckboxButtonsBean implements Serializable {

    public static final String BEAN_NAME = "checkboxButtons";
	public String getBeanName() { return BEAN_NAME; }

	private List<String> selected = new ArrayList<String>();
	private boolean mutuallyExclusive = false;

    public CheckboxButtonsBean() {
		selected.add("One");
		selected.add("Three");
		selected.add("Five");
    }

	public List<String> getSelected() { return selected; }
	public void setSelected(List<String> selected) { this.selected = selected; }

	public boolean isMutuallyExclusive() { return mutuallyExclusive; }
	public void setMutuallyExclusive(boolean mutuallyExclusive) { this.mutuallyExclusive = mutuallyExclusive; }

	public String getSelectedString() {
		String result = "";
		if (selected != null) {
			result += selected.toString();
			result = result.replace("[", "");
			result = result.replace("]", "");
			if ("".equals(result)) {
				result = "None";
			}
		} else {
			result = "None";
		}
		return result;
	}
}
