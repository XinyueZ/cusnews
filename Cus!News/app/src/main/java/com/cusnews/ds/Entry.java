package com.cusnews.ds;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import cn.bmob.v3.BmobObject;

public   class Entry  extends BmobObject implements Serializable {
	@SerializedName("title")
	private final String mTitle;
	@SerializedName("kwic")
	private final String mKwic;
	@SerializedName("content")
	private final String mContent;
	@SerializedName("url")
	private final String mUrl;
	@SerializedName("iurl")
	private final String mImageUrl;
	@SerializedName("domain")
	private final String mDomain;
	@SerializedName("author")
	private final String mAuthor;
	@SerializedName("news")
	private final boolean mNews;
	@SerializedName("votes")
	private final String mVotes;
	@SerializedName("date")
	private final long mDate;
	@SerializedName("related")
	private final List<Entry> mRelated;

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
