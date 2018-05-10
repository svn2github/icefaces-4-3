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

import org.icefaces.ace.component.dashboard.DashboardPane;

import javax.faces.event.AjaxBehaviorEvent;

public class DashboardDragStopEvent extends AjaxBehaviorEvent {

    public DashboardDragStopEvent(AjaxBehaviorEvent event) {
		super(event.getComponent(), event.getBehavior());
    }

	public int getOldRow() {
		Integer value = ((DashboardPane) getComponent()).getOldRow();
		if (value == null) return -1;
		return value;
	}

	public int getOldColumn() {
		Integer value = ((DashboardPane) getComponent()).getOldColumn();
		if (value == null) return -1;
		return value;
	}

	public int getNewRow() {
		return ((DashboardPane) getComponent()).getRow();
	}

	public int getNewColumn() {
		return ((DashboardPane) getComponent()).getColumn();
	}
}
