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

package org.icefaces.ace.component.dashboard;

import org.icefaces.ace.event.CloseEvent;
import org.icefaces.ace.event.ToggleEvent;
import org.icefaces.ace.model.Visibility;
import org.icefaces.ace.util.Constants;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import java.util.Map;

public class DashboardPane extends DashboardPaneBase {

	@Override
	public void queueEvent(FacesEvent event) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();
		String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
		String source = params.get(Constants.PARTIAL_SOURCE_PARAM);
		String clientId = this.getClientId(context);

		if (clientId.equals(source)) {
			if (eventName != null && eventName.equals("toggle") && event instanceof AjaxBehaviorEvent) {
				boolean collapsed = Boolean.valueOf(params.get(clientId + "_collapsed"));
				Visibility visibility = collapsed ? Visibility.HIDDEN : Visibility.VISIBLE;

				setCollapsed(collapsed);
				ToggleEvent toggleEvent = new ToggleEvent(this, ((AjaxBehaviorEvent) event).getBehavior(), visibility);
				toggleEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
				super.queueEvent(toggleEvent);
			} else if (eventName != null && eventName.equals("close") && event instanceof AjaxBehaviorEvent) {;
				CloseEvent closeEvent = new CloseEvent(this, ((AjaxBehaviorEvent) event).getBehavior());
				closeEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
				super.queueEvent(closeEvent);
			} 
		} else {
			super.queueEvent(event);
		}
	}
}