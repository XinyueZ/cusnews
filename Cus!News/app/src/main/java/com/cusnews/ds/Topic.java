package com.cusnews.ds;

import android.support.annotation.StringRes;

/**
 * The topic can be subscribed as pushed by Google.
 *
 * @author Xinyue Zhao
 */
public final class Topic {
	private String mLanguage;
	private String mName;
	private @StringRes
	int mLocalNameResId;


	public Topic(String language, String name, @StringRes int localNameResId) {
		mLanguage = language;
		mName = name;
		mLocalNameResId = localNameResId;
	}

	public String getLanguage() {
		return mLanguage;
	}

	public String getName() {
		return mName;
	}

	public int getLocalNameResId() {
		return mLocalNameResId;
	}

	public String getApiName() {
		return String.format("%s-%s", mName, mLanguage);
	}
}
