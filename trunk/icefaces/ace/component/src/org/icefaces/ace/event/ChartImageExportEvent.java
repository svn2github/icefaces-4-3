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
import javax.faces.event.FacesListener;
import javax.faces.event.FacesEvent;

public class ChartImageExportEvent extends FacesEvent {

	private byte[] bytes;
	
	public ChartImageExportEvent(UIComponent component, byte[] bytes) {
		super(component);
		this.bytes = bytes;
	}
	
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return true;
	}
	
	@Override
	public void processListener(FacesListener facesListener) {
		throw new UnsupportedOperationException();
	}
}
