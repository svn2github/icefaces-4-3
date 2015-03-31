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

package org.icefaces.demo.auction.util;

import java.io.Serializable;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name=ListData.BEAN_NAME)
@ApplicationScoped
public class ListData implements Serializable {
	public static final String BEAN_NAME = "listData";
	
	public static final String DEFAULT_TAB_ORIENTATION = "top";
	public static final String[] TAB_ORIENTATIONS = new String[] {
		"bottom", DEFAULT_TAB_ORIENTATION, "left", "right"
	};

	public String[] getTabOrientations() {
		return TAB_ORIENTATIONS;
	}
}
