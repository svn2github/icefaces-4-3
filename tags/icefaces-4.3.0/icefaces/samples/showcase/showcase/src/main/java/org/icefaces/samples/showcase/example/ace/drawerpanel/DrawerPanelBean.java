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

package org.icefaces.samples.showcase.example.ace.drawerpanel;

import org.icefaces.samples.showcase.example.ace.schedule.RandomEventGenerator;
import org.icefaces.ace.model.schedule.ScheduleEvent;
import org.icefaces.ace.model.schedule.LazyScheduleEventList;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AjaxBehaviorEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

@ManagedBean(name= DrawerPanelBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DrawerPanelBean implements Serializable {
    public static final String BEAN_NAME = "drawerPanelBean";
	public String getBeanName() { return BEAN_NAME; }

	public DrawerPanelBean() {
		
	}

	private String position = "left";
	public String getPosition() { return position; }
	public void setPosition(String position) { this.position = position; }

	private boolean showHandleOpen = true;
	public boolean isShowHandleOpen() { return showHandleOpen; }
	public void setShowHandleOpen(boolean showHandleOpen) { this.showHandleOpen = showHandleOpen; }

	private boolean showHandleClose = true;
	public boolean isShowHandleClose() { return showHandleClose; }
	public void setShowHandleClose(boolean showHandleClose) { this.showHandleClose = showHandleClose; }

	private boolean divContainer = false;
	public boolean isDivContainer() { return divContainer; }
	public void setDivContainer(boolean divContainer) { this.divContainer = divContainer; }

	public String getContainer() {
		if (divContainer) return "div";
		else return "window";
	}

	private boolean visible = false;
	public boolean isVisible() { return visible; }
	public void setVisible(boolean visible) { this.visible = visible; }
}