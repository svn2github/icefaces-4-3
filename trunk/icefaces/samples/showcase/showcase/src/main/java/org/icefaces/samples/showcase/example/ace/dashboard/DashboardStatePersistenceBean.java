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

package org.icefaces.samples.showcase.example.ace.dashboard;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AjaxBehaviorEvent;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import java.io.Serializable;
import java.util.*;

@ManagedBean(name= DashboardStatePersistenceBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DashboardStatePersistenceBean implements Serializable {
    public static final String BEAN_NAME = "dashboardStatePersistenceBean";
	public String getBeanName() { return BEAN_NAME; }

	public DashboardStatePersistenceBean() {
		
	}

	@PostConstruct
	public void init() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		Object o = sessionMap.get("DashboardStatePersistenceBean.state");
		if (o == null) {
			List<DashboardPaneState> states = new ArrayList<DashboardPaneState>(10);
			states.add(new DashboardPaneState(1, 1, 1, 2, false));
			states.add(new DashboardPaneState(1, 3, 2, 1, false));
			states.add(new DashboardPaneState(2, 1, 1, 1, false));
			states.add(new DashboardPaneState(2, 2, 1, 1, false));
			states.add(new DashboardPaneState(3, 1, 1, 1, false));
			states.add(new DashboardPaneState(3, 2, 2, 2, false));
			states.add(new DashboardPaneState(4, 1, 1, 1, false));
			states.add(new DashboardPaneState(5, 1, 1, 1, false));
			states.add(new DashboardPaneState(5, 2, 1, 1, false));
			states.add(new DashboardPaneState(5, 3, 1, 1, false));
			this.states = states;
		} else {
			this.states = (List<DashboardPaneState>) o;
		}
	}

	private List<DashboardPaneState> states;
	public List<DashboardPaneState> getStates() { return states; }
	public void setStates(List<DashboardPaneState> states) { this.states = states; }

	public static class DashboardPaneState implements Serializable {

		public DashboardPaneState(int row, int column, int sizeY, int sizeX, boolean closed) {
			this.row = row;
			this.column = column;
			this.sizeY = sizeY;
			this.sizeX = sizeX;
			this.closed = closed;
		}

		private int row;
		public int getRow() { return row; }
		public void setRow(int row) { this.row = row; }

		private int column;
		public int getColumn() { return column; }
		public void setColumn(int column) { this.column = column; }

		private int sizeY;
		public int getSizeY() { return sizeY; }
		public void setSizeY(int sizeY) { this.sizeY = sizeY; }

		private int sizeX;
		public int getSizeX() { return sizeX; }
		public void setSizeX(int sizeX) { this.sizeX = sizeX; }

		private boolean closed;
		public boolean isClosed() { return closed; }
		public void setClosed(boolean closed) { this.closed = closed; }
	}
}