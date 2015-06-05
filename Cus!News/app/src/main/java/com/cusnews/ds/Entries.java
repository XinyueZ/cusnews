package com.cusnews.ds;


import java.util.List;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import com.google.gson.annotations.SerializedName;

public final class Entries  extends BaseObservable {
	@SerializedName("query")
	private final String mQuery;
	@SerializedName("suggestions")
	private final List<String> mSuggestions;
	@SerializedName("count")
	private final int mCount;
	@SerializedName("start")
	private final int mStart;
	@SerializedName("length")
	private final int mLength;
	@SerializedName("time")
	private final String mTime;
	@SerializedName("results")
	@Bindable
	private final ObservableArrayList<Entry> mList;

	public Entries(String query, List<String> suggestions, int count, int start, int length, String time,
			ObservableArrayList<Entry> list) {
		mQuery = query;
		mSuggestions = suggestions;
		mCount = count;
		mStart = start;
		mLength = length;
		mTime = time;
		mList = list;
	}

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

	public ObservableArrayList<Entry> getList() {
		return mList;
	}
}
