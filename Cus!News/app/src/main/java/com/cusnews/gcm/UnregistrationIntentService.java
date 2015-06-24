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

import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.cusnews.ds.PushToken;
import com.cusnews.ds.TopicsFactory;
import com.cusnews.utils.DeviceUniqueUtil;
import com.cusnews.utils.Prefs;
import com.google.android.gms.iid.InstanceID;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class UnregistrationIntentService extends IntentService {
    public static final String UNREGISTRATION_COMPLETE = "unregistrationComplete";
    private static final String TAG = "UnregIntentService";

    public UnregistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
		Prefs prefs =  Prefs.getInstance();
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                instanceID.deleteInstanceID();
				prefs.setPushToken(null);
                //Unsubscribe all.
                TopicsFactory.clear();
            }
        } catch (Exception e) {
        }


        Intent unregistrationComplete = new Intent(UNREGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(unregistrationComplete);
        String deviceId = "0000000000";
        try {
            deviceId  = DeviceUniqueUtil.getDeviceIdent(getApplicationContext());
        } catch (NoSuchAlgorithmException e) {

        }
		BmobQuery<PushToken> query = new BmobQuery<>();
		query.addWhereEqualTo("mGoogleId", prefs.getGoogleId());
        query.addWhereEqualTo("mDeviceId", deviceId);
		query.findObjects(this, new FindListener<PushToken>() {
			@Override
			public void onSuccess(List<PushToken> list) {
				PushToken token = list.get(0);
				if(token != null) {
					token.delete(UnregistrationIntentService.this);
				}
			}

			@Override
			public void onError(int i, String s) {
			}
		});
    }


}
