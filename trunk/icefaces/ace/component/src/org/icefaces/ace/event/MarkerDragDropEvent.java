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

package org.icefaces.ace.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class MarkerDragDropEvent extends FacesEvent {

	private String newLat;
	private String newLng;
	private String oldLat;
	private String oldLng;
	
	public MarkerDragDropEvent(UIComponent component, String newLat, String newLng, String oldLat, String oldLng) {
		super(component);
		this.newLat = newLat;
		this.newLng = newLng;
		this.oldLat = oldLat;
		this.oldLng = oldLng;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return false;
	}

	@Override
	public void processListener(FacesListener listener) {
		throw new UnsupportedOperationException();
	}
	
	public String getNewLatitude() {
		return newLat;
	}

	public String getNewLongitude() {
		return newLng;
	}

	public String getOldLatitude() {
		return oldLat;
	}

	public String getOldLongitude() {
		return oldLng;
	}
}