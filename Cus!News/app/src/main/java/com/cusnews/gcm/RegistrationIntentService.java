/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cusnews.gcm;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.ds.PushToken;
import com.cusnews.utils.DeviceUniqueUtil;
import com.cusnews.utils.Prefs;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import cn.bmob.v3.listener.SaveListener;

public class RegistrationIntentService extends IntentService {
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global-zh"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Prefs prefs = Prefs.getInstance();
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(App.Instance.getSenderId(),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                prefs.setPushToken(token);
			}
        } catch (Exception e) {
            prefs.setPushToken(null);
            com.chopping.utils.Utils.showLongToast(this, R.string.lbl_register_push_failed);
        }

		if(!TextUtils.isEmpty(prefs.getPushToken())) {
            String deviceId = "0000000000";
            try {
                deviceId  = DeviceUniqueUtil.getDeviceIdent(getApplicationContext());
            } catch (NoSuchAlgorithmException e) {

            }
            PushToken newPushToken = new PushToken(prefs.getGoogleId(),deviceId , prefs.getPushToken());
			newPushToken.save(this, new SaveListener() {
				@Override
				public void onSuccess() {
					Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
					LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(
							registrationComplete);
				}

				@Override
				public void onFailure(int i, String s) {
					Prefs prefs = Prefs.getInstance();
					prefs.setPushToken(null);
					Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
					LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(
							registrationComplete);
				}
			});
		} else {
			Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
			LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
		}
    }


    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]

}
