package com.cusnews.ds;

import cn.bmob.v3.BmobObject;

/**
 * Push-token that saved on backend.
 */
public final class PushToken extends BmobObject {
	private String mGoogleId;
	private String mDeviceId;
	private String mPushToken;
	private String mCustomizedTopic1;
	private String mCustomizedTopic2;
	private String mCustomizedTopic3;

	public PushToken(String googleId, String deviceId, String pushToken) {
		mGoogleId = googleId;
		mDeviceId = deviceId;
		mPushToken = pushToken;
	}

	public PushToken() {
	}

	public String getGoogleId() {
		return mGoogleId;
	}

	public String getDeviceId() {
		return mDeviceId;
	}

	public String getPushToken() {
		return mPushToken;
	}

	public String getCustomizedTopic1() {
		return mCustomizedTopic1;
	}

	public void setCustomizedTopic1(String customizedTopic1) {
		mCustomizedTopic1 = customizedTopic1;
	}

	public String getCustomizedTopic2() {
		return mCustomizedTopic2;
	}

	public void setCustomizedTopic2(String customizedTopic2) {
		mCustomizedTopic2 = customizedTopic2;
	}

	public String getCustomizedTopic3() {
		return mCustomizedTopic3;
	}

	public void setCustomizedTopic3(String customizedTopic3) {
		mCustomizedTopic3 = customizedTopic3;
	}
}
