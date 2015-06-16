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

public class SubscribeIntentService extends IntentService {
	public static final String SUBSCRIBE_COMPLETE = "subscribeComplete";
	public static final String TOPIC = "topic";
	private static final String TAG = "SubscribeIntentService";

	public SubscribeIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			synchronized (TAG) {
				String token = Prefs.getInstance().getPushToken();
				subscribeTopics(token, intent.getStringExtra(TOPIC));
			}
		} catch (Exception e) {
		}

		Intent subscribeComplete = new Intent(SUBSCRIBE_COMPLETE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(subscribeComplete);
	}


	/**
	 * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
	 *
	 * @param token GCM token
	 *              @param topic  The topic-name.
	 * @throws IOException if unable to reach the GCM PubSub service
	 */
	// [START subscribe_topics]
	private void subscribeTopics(String token, String topic) throws IOException {
		GcmPubSub pubSub = GcmPubSub.getInstance(this);
		pubSub.subscribe(token, "/topics/" + topic, null);
	}
}
