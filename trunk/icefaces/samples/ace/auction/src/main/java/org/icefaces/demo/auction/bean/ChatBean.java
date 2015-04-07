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

package org.icefaces.demo.auction.bean;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

import org.icefaces.demo.auction.chat.ChatRoom;
import org.icefaces.demo.auction.service.ChatService;
import org.icefaces.demo.auction.util.FacesUtils;

@ManagedBean(name=ChatBean.BEAN_NAME)
@CustomScoped(value="#{window}")
public class ChatBean implements Serializable {
	public static final String BEAN_NAME = "chatBean";
	private static final Logger log = Logger.getLogger(ChatBean.class.getName());
	
	public enum ChatPosition {
		TAB("Tab"),
		POPUP("Popup"),
		BOTTOM("Bottom Panel");
		
		private String value;
		public String getValue() { return value; }
		private ChatPosition(String value) { this.value = value; }
		@Override
		public String toString() { return value; }
	}
	
	private String cachedName; // Store our previous name successfully pulled from SettingsBean
	private ChatRoom currentRoom;
	private String currentMessage;
	private ChatPosition position = ChatPosition.TAB;
	private boolean renderOccupants = true;
	
	@ManagedProperty(value="#{" + ChatService.BEAN_NAME + "}")
	private ChatService service;
	
	@PostConstruct
	public void initChatBean() {
		// If we only have a single room then just autojoin on creation
		if ((service != null) && (!service.getHasMultipleRooms())) {
			service.joinRoom(this, service.getDefaultRoom());
			currentRoom = service.getDefaultRoom();
		}
	}
	
	@PreDestroy
	public void destroyChatBean() {
		if ((currentRoom != null) && (service != null)) {
			log.info("ChatBean timed out, removing user " + getName() + " from chat room " + currentRoom);
			service.leaveRoom(this, currentRoom);
		}
	}
	
	public String getName() {
		// Try to get our name each time from the SettingsBean
		// This saves us having to manually sync the names between beans
		SettingsBean settings = (SettingsBean)FacesUtils.getManagedBean(SettingsBean.BEAN_NAME);
		if ((settings != null) && (settings.getName() != null) && (!settings.getName().isEmpty())) {
			cachedName = settings.getName();
			return settings.getName();
		}
		
		if ((cachedName != null) && (!cachedName.isEmpty())) {
			return cachedName;
		}
		
		return "Anonymous";
	}

	public ChatRoom getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(ChatRoom currentRoom) {
		this.currentRoom = currentRoom;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public void setCurrentMessage(String currentMessage) {
		this.currentMessage = currentMessage;
	}
	
	public ChatPosition getPosition() {
		return position;
	}

	public void setPosition(ChatPosition position) {
		this.position = position;
	}

	public boolean isRenderOccupants() {
		return renderOccupants;
	}

	public void setRenderOccupants(boolean renderOccupants) {
		this.renderOccupants = renderOccupants;
	}

	public ChatService getService() {
		return service;
	}

	public void setService(ChatService service) {
		this.service = service;
	}
	
	public boolean getIsPositionTab() {
		return position == ChatPosition.TAB;
	}
	
	public boolean getIsPositionPopup() {
		return position == ChatPosition.POPUP;
	}
	
	public boolean getIsPositionBottom() {
		return position == ChatPosition.BOTTOM;
	}
}
