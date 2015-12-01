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
	private String mCustomizedTopic4;
	private String mCustomizedTopic5;
	private String mLanguage;

	public PushToken( String googleId, String deviceId, String pushToken, String language ) {
		mGoogleId = googleId;
		mDeviceId = deviceId;
		mPushToken = pushToken;
		mLanguage = language;
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

	public String getLanguage() {
		return mLanguage;
	}

	public String getCustomizedTopic1() {
		return mCustomizedTopic1;
	}

	public void setCustomizedTopic1( String customizedTopic1 ) {
		mCustomizedTopic1 = customizedTopic1;
	}

	public String getCustomizedTopic2() {
		return mCustomizedTopic2;
	}

	public void setCustomizedTopic2( String customizedTopic2 ) {
		mCustomizedTopic2 = customizedTopic2;
	}

	public String getCustomizedTopic3() {
		return mCustomizedTopic3;
	}

	public void setCustomizedTopic3( String customizedTopic3 ) {
		mCustomizedTopic3 = customizedTopic3;
	}

	public String getCustomizedTopic4() {
		return mCustomizedTopic4;
	}

	public void setCustomizedTopic4( String customizedTopic4 ) {
		mCustomizedTopic4 = customizedTopic4;
	}

	public String getCustomizedTopic5() {
		return mCustomizedTopic5;
	}

	public void setCustomizedTopic5( String customizedTopic5 ) {
		mCustomizedTopic5 = customizedTopic5;
	}
}
