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

package org.icefaces.ace.component.gmap;

import org.icefaces.ace.event.MarkerDragDropEvent;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.context.FacesContext;
import javax.el.MethodExpression;

public class GMapMarker extends GMapMarkerBase {

	@Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
		super.broadcast(event);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getDragDropListener();

		if (me != null && event instanceof MarkerDragDropEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}
}