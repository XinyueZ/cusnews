package com.cusnews.ds;


import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Entries {
	@SerializedName("query")
	private String mQuery;
	@SerializedName("suggestions")
	private List<String> mSuggestions;
	@SerializedName("count")
	private int mCount;
	@SerializedName("start")
	private int mStart;
	@SerializedName("length")
	private int mLength;
	@SerializedName("time")
	private String mTime;
	@SerializedName("results")
	private List<Entry> mList;

	public String getQuery() {
		return mQuery;
	}

	public List<String> getSuggestions() {
		return mSuggestions;
	}

	public int getCount() {
		return mCount;
	}

	public int getStart() {
		return mStart;
	}

	public int getLength() {
		return mLength;
	}

	public String getTime() {
		return mTime;
	}

	public List<Entry> getList() {
		return mList;
	}
}
