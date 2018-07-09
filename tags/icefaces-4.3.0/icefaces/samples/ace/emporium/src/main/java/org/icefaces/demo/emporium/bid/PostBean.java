/*
 * Copyright 2004-2015 ICEsoft Technologies Canada Corp.
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

package org.icefaces.demo.emporium.bid;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.icefaces.demo.emporium.bid.model.AuctionItem;
import org.icefaces.demo.emporium.bid.util.AuctionItemGenerator;
import org.icefaces.demo.emporium.settings.SettingsBean;
import org.icefaces.demo.emporium.util.FacesUtils;
import org.icefaces.demo.emporium.util.StringUtil;

@ManagedBean(name=PostBean.BEAN_NAME)
@CustomScoped(value="#{window}")
public class PostBean implements Serializable {
	private static final long serialVersionUID = -7979594191648800420L;

	public static final String BEAN_NAME = "postBean";
	
	private AuctionItem toAdd;
	private AuctionItem toRemove;
	private Date expiryDate;
	private int postedCount = 0; // Track how many auctions this user session has added
	private boolean showItemImageDialog = false;
	private boolean hasLoadedImages = false;
	private String clickedImage;
    private ServletContext servletContext;

    public PostBean() {
        servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
    }

    public void clear() {
        setToAdd(null);
		setExpiryDate(null);
	}
	
	public void incrementPostedCount() {
		postedCount++;
	}
	
	public void decrementPostedCount() {
		postedCount--;
	}

	public AuctionItem getToAdd() {
		if (toAdd == null) {
			toAdd = new AuctionItem();
			
			// Set some defaults
			toAdd.setName(AuctionItemGenerator.generateName());
            toAdd.setImageName(AuctionItemGenerator.generateImageName(servletContext, toAdd.getName()));
            toAdd.setPrice(0.1);
			toAdd.setShippingCost(1.0);
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, AuctionItem.DEFAULT_EXPIRY_DATE_HOURS);
			setExpiryDate(cal.getTime());
			toAdd.setExpiryDate(cal.getTimeInMillis());
			
			SettingsBean settingsBean = (SettingsBean)FacesUtils.getManagedBean(SettingsBean.BEAN_NAME);
			if (StringUtil.validString(settingsBean.getName())) {
				toAdd.setSellerName(settingsBean.getName());
			}
			if (StringUtil.validString(settingsBean.getLocation())) {
				toAdd.setSellerLocation(settingsBean.getLocation());
			}
		}
		
		return toAdd;
	}

	public void setToAdd(AuctionItem toAdd) {
		this.toAdd = toAdd;
	}
	
	public AuctionItem getToRemove() {
		return toRemove;
	}

	public void setToRemove(AuctionItem toRemove) {
		this.toRemove = toRemove;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public int getPostedCount() {
		return postedCount;
	}

	public void setPostedCount(int postedCount) {
		this.postedCount = postedCount;
	}
	
	public boolean isShowItemImageDialog() {
		return showItemImageDialog;
	}

	public void setShowItemImageDialog(boolean showImageDialog) {
		this.showItemImageDialog = showImageDialog;
	}
	
	public boolean isHasLoadedImages() {
		return hasLoadedImages;
	}

	public void setHasLoadedImages(boolean hasLoadedImages) {
		this.hasLoadedImages = hasLoadedImages;
	}

	public String getClickedImage() {
		return clickedImage;
	}

	public void setClickedImage(String clickedImage) {
		this.clickedImage = clickedImage;
	}
}
