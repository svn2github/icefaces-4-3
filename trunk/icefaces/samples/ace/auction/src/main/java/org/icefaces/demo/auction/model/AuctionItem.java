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

package org.icefaces.demo.auction.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.icefaces.demo.auction.converter.TimeLeftConverter;

public class AuctionItem implements Comparable<AuctionItem>, Serializable {
	private static final long serialVersionUID = -7512542650152899004L;
	
	public static final double DEFAULT_BID_INCREMENT = 1.0;
	public static final double SMALL_BID_INCREMENT = 10.0;
	public static final double MAX_BID_INCREASE = 100.0;

	public enum Condition {
		NEW("New"),
		USED_EXCELLENT("Used (Excellent)"),
		USED_GOOD("Used (Good)"),
		USED_BAD("Used (Bad)"),
		DAMAGED("Damaged");
		
		private String value;
		public String getValue() { return value; }
		private Condition(String value) { this.value = value; }
		@Override
		public String toString() { return value; }
	}
	public Condition[] getConditions() {
		return Condition.values();
	}
	
	public enum Delivery {
		ONE_DAY("1 day"),
		THREE_DAYS("1-3 days"),
		ONE_WEEK("3-7 days"),
		ONE_TWO_WEEKS("1-2 weeks"),
		ONE_MONTH("up to 1 month");
		
		private String value;
		public String getValue() { return value; }
		private Delivery(String value) { this.value = value; }
		@Override
		public String toString() { return value; }
	}
	public Delivery[] getDeliveries() {
		return Delivery.values();
	}
	
	// Main info
	private String name;
	private double price;
	private int bids = 0;
	private long expiryDate; // In milliseconds
	
	// Details
	private Double shippingCost;
	private String sellerName;
	private String sellerLocation;
	private String description;
	private Delivery estimatedDelivery;
	private Condition condition;
	
	private List<AuctionHistory> history = new ArrayList<AuctionHistory>();
	
	public AuctionItem() {
	}
	
	public AuctionItem(String name, double price, int bids, long expiryDateMil) {
		this(name, price, bids, expiryDateMil, null, null, null, null, null, null);
	}
	
	public AuctionItem(String name, double price, int bids,
			long expiryDateMil, Double shippingCost, String sellerName,
			String sellerLocation, String description,
			Delivery estimatedDelivery, Condition condition) {
		this.name = name;
		this.price = price;
		this.bids = bids;
		this.expiryDate = expiryDateMil;
		this.shippingCost = shippingCost;
		this.sellerName = sellerName;
		this.sellerLocation = sellerLocation;
		this.description = description;
		this.estimatedDelivery = estimatedDelivery;
		this.condition = condition;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getBids() {
		return bids;
	}
	public void setBids(int bids) {
		this.bids = bids;
	}
	public long getExpiryDate() {
		return expiryDate;
	}
	public String getTimeLeft() {
		return TimeLeftConverter.convertExpiryToTimeLeft(expiryDate);
	}
	public void setExpiryDate(long expiryDate) {
		this.expiryDate = expiryDate;
	}
	public Double getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(Double shippingCost) {
		this.shippingCost = shippingCost;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerLocation() {
		return sellerLocation;
	}
	public void setSellerLocation(String sellerLocation) {
		this.sellerLocation = sellerLocation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Delivery getEstimatedDelivery() {
		return estimatedDelivery;
	}
	public void setEstimatedDelivery(Delivery estimatedDelivery) {
		this.estimatedDelivery = estimatedDelivery;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public List<AuctionHistory> getHistory() {
		return history;
	}
	public void setHistory(List<AuctionHistory> history) {
		this.history = history;
	}
	
	public void placeBid(double newPrice) {
		placeBid(new Date(), newPrice);
	}

	public void placeBid(Date bidDate, double newPrice) {
		archiveHistory(bidDate, newPrice);
		setPrice(newPrice);
		increaseBids();
	}
	
	public void increaseBids() {
		bids++;
	}
	
	public void archiveHistory(Date bidDate, double newBid) {
		if (history != null) {
			// Store the bid increase between the incoming new bid and our current price
			// We'll keep this with the newest bid at the "top" of the list (0 index)
			history.add(0, new AuctionHistory(bidDate, (newBid - price), newBid));
		}
	}

	public boolean isExpired() {
		return new Date().after(new Date(expiryDate));
	}
	
	@Override
	public String toString() {
		return name + " for " + price + " with " + bids + " bids ending at " + new Date(expiryDate);
	}

	@Override
	public int compareTo(AuctionItem o) {
		if (expiryDate < o.getExpiryDate()) {
			return -1;
		}
		else if (expiryDate > o.getExpiryDate()) {
			return 1;
		}
		return 0;
	}
}
