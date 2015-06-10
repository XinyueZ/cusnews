package com.cusnews.bus;


import android.content.Intent;

/**
 * Common and native sharing event .
 *
 * @author Xinyue Zhao
 */
public final class ShareEvent {
	private Intent mIntent;

	public ShareEvent(Intent intent) {
		mIntent = intent;
	}

	public Intent getIntent() {
		return mIntent;
	}
}
