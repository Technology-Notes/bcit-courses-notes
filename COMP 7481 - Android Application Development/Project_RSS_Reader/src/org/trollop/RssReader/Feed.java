/**
 * Project: Project_RSS_Reader
 * File: Feeds.java
 * Date: 2011-04-12
 * Time: 6:11:51 PM
 */
package org.trollop.RssReader;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

/**
 * @author Steffen L. Norgren, A00683006
 *
 */
public class Feed {
	private String name;
	private String feedURL;
	private Bitmap icon;
	private List<FeedItem> itemList;
	
	public Feed(String feedURL) {
		this.feedURL = feedURL;
		this.itemList = new ArrayList<FeedItem>();
	}
	
	public void addItem(FeedItem item) {
		itemList.add(item);
	}
	
	public int getItemCount() {
		return itemList.size();
	}
	
	public int getUnreadCount() {
		int unreadCount = 0;
		
		for (FeedItem item : itemList) {
			if (item.isUnread()) {
				unreadCount++;
			}
		}
		
		return unreadCount;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the feedURL
	 */
	public String getFeedURL() {
		return feedURL;
	}

	/**
	 * @param feedURL the feedURL to set
	 */
	public void setFeedURL(String feedURL) {
		this.feedURL = feedURL;
	}

	/**
	 * @return the icon
	 */
	public Bitmap getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	/**
	 * @return the itemList
	 */
	public List<FeedItem> getItemList() {
		return itemList;
	}
	
	public FeedItem getItem(int itemID) {
		return itemList.get(itemID);
	}
}
