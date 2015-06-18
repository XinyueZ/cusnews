/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.cusnews.gcm;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.cusnews.utils.Prefs;
import com.google.android.gms.gcm.GcmPubSub;

public class UnsubscribeIntentService extends IntentService {
	public static final String UNSUBSCRIBE_COMPLETE = "unsubscribeComplete";
	public static final String TOPIC = "topic";
	public static final String UNSUBSCRIBE_RESULT = "result";
	public static final String UNSUBSCRIBE_NAME = "name";
	public static final String STORAGE_NAME = "storage_name";
	private static final String TAG = "UnsubscribeIntentService";

	public UnsubscribeIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Prefs prefs  = Prefs.getInstance();
		Intent unsubscribeComplete = new Intent(UNSUBSCRIBE_COMPLETE);
		String token = Prefs.getInstance().getPushToken();
		String topic = intent.getStringExtra(TOPIC);
		String storage =  intent.getStringExtra(STORAGE_NAME);
		unsubscribeComplete.putExtra(UNSUBSCRIBE_NAME, intent.getStringExtra(UNSUBSCRIBE_NAME));
		try {
			synchronized (TAG) {
				unsubscribeTopics(token, topic);
				prefs.setPush(storage, false);
				unsubscribeComplete.putExtra(UNSUBSCRIBE_RESULT, true);
			}
		} catch (Exception e) {
			prefs.setPush(storage, true);
			unsubscribeComplete.putExtra(UNSUBSCRIBE_RESULT, false);
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(unsubscribeComplete);
	}


	/**
	 * Unsubscribe to any GCM topics of interest, as defined by the TOPICS constant.
	 *
	 * @param token
	 * 		GCM token
	 * @param topic
	 * 		The topic-name that have been subscribed.
	 *
	 * @throws IOException
	 * 		if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void unsubscribeTopics(String token, String topic) throws IOException {
		GcmPubSub pubSub = GcmPubSub.getInstance(this);
		pubSub.unsubscribe(token, "/topics/" + topic);
	}
}
