package com.cusnews.ds;


import java.util.List;

import com.google.gson.annotations.SerializedName;

public final class Trends {
	@SerializedName("trends")
	private List<String> mList;
	@SerializedName("count")
	private int mCount;

	public List<String> getList() {
		return mList;
	}

	public int getCount() {
		return mCount;
	}
}
