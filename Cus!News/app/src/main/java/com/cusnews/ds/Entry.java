package com.cusnews.ds;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import cn.bmob.v3.BmobObject;

public   class Entry  extends BmobObject implements Serializable {
	@SerializedName("title")
	private   String mTitle;
	@SerializedName("kwic")
	private   String mKwic;
	@SerializedName("content")
	private   String mContent;
	@SerializedName("url")
	private   String mUrl;
	@SerializedName("iurl")
	private   String mImageUrl;
	@SerializedName("domain")
	private   String mDomain;
	@SerializedName("author")
	private   String mAuthor;
	@SerializedName("news")
	private   boolean mNews;
	@SerializedName("votes")
	private   String mVotes;
	@SerializedName("date")
	private   long mDate;
	@SerializedName("related")
	private   List<Entry> mRelated;

	public Entry(String title, String kwic, String content, String url, String imageUrl, String domain, String author,
			boolean news, String votes, long date, List<Entry> related) {
		mTitle = title;
		mKwic = kwic;
		mContent = content;
		mUrl = url;
		mImageUrl = imageUrl;
		mDomain = domain;
		mAuthor = author;
		mNews = news;
		mVotes = votes;
		mDate = date;
		mRelated = related;
	}

	public Entry(){}

	public String getTitle() {
		return mTitle;
	}

	public String getKwic() {
		return mKwic;
	}

	public String getContent() {
		return mContent;
	}

	public String getUrl() {
		return mUrl;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

	public String getDomain() {
		return mDomain;
	}

	public String getAuthor() {
		return mAuthor;
	}

	public boolean isNews() {
		return mNews;
	}

	public String getVotes() {
		return mVotes;
	}

	public long getDate() {
		return mDate;
	}

	public List<Entry> getRelated() {
		return mRelated;
	}


}
