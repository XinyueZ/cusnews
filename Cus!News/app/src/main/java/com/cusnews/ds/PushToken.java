package com.cusnews.ds;

import cn.bmob.v3.BmobObject;

/**
 * Push-token that saved on backend.
 */
public final class PushToken extends BmobObject  {
	private String mGoogleId;
	private String mPushToken;

	public PushToken(String googleId, String pushToken) {
		mGoogleId = googleId;
		mPushToken = pushToken;
	}

	public PushToken() {
	}

	public String getGoogleId() {
		return mGoogleId;
	}

	public String getPushToken() {
		return mPushToken;
	}
}
