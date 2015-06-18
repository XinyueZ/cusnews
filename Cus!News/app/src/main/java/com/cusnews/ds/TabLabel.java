package com.cusnews.ds;


import android.text.TextUtils;

import cn.bmob.v3.BmobObject;

public final class TabLabel extends BmobObject  {
	private String mUID;
	private String mLabel;

	public TabLabel( String label, String UID) {
		mUID = UID;
		mLabel = label;
	}

	public TabLabel(
	) {

	}

	public String getUID() {
		return mUID;
	}

	public String getLabel() {
		return mLabel;
	}

	@Override
	public boolean equals(Object o) {
		TabLabel other = (TabLabel) o;
		return TextUtils.equals(mLabel, other.getLabel());
	}
}
