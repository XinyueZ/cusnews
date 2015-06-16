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

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.cusnews.R;
import com.cusnews.utils.Prefs;
import com.google.android.gms.iid.InstanceID;

public class UnregistrationIntentService extends IntentService {
    public static final String UNREGISTRATION_COMPLETE = "unregistrationComplete";
    private static final String TAG = "UnregIntentService";

    public UnregistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                instanceID.deleteInstanceID();
                Prefs.getInstance().setPushToken(null);
            }
        } catch (Exception e) {
            com.chopping.utils.Utils.showLongToast(this, R.string.lbl_unregister_push_failed);
        }
        Intent unregistrationComplete = new Intent(UNREGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(unregistrationComplete);
    }


}
