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
import java.io.Serializable;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.text.TextUtils;

import com.chopping.utils.Utils;
import com.cusnews.R;
import com.cusnews.app.activities.DetailActivity;
import com.cusnews.ds.Entry;
import com.google.android.gms.gcm.GcmListenerService;
import com.squareup.picasso.Picasso;

public class MyGcmListenerService extends GcmListenerService {
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotifyBuilder;
	private static final String TAG = "MyGcmListenerService";

	/**
	 * Called when message is received.
	 *
	 * @param from SenderID of the sender.
	 * @param data Data bundle containing message data as key/value pairs.
	 *             For Set of keys use data.keySet().
	 */
	// [START receive_message]
	@Override
	public void onMessageReceived(String from, Bundle data) {
		sendNotification(data);
	}
	// [END receive_message]


	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(final Bundle msg) {
		final String title = msg.getString("title");
		final String desc = msg.getString("kwic");
		final String content = msg.getString("content");
		final String url = msg.getString("url");
		final String image = msg.getString("iurl");
		final String domain = msg.getString("domain");
		final String author = msg.getString("author");
		final long date = msg.getLong("date");

		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


		Entry entry = new Entry(title, desc, content, url, image, domain, author, true, "", date, null);

		//Action to open new detail window for this entry.
		Intent intent = new Intent(this, DetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(DetailActivity.EXTRAS_ENTRY, (Serializable) entry);
		intent.putExtra(DetailActivity.EXTRAS_QUERY, "");
		final PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
				PendingIntent.FLAG_ONE_SHOT);

		long id = System.currentTimeMillis();
		//Action to share this entry: normal sharing or facebook.
		Intent shareIntent = new Intent(this, NotificationShareService.class);
		shareIntent.putExtra(NotificationShareService.EXTRAS_ID, id);
		shareIntent.putExtra(NotificationShareService.EXTRAS_TYPE, NotificationShareService.TYPE_NORMAL);
		shareIntent.putExtra(NotificationShareService.EXTRAS_SHARED_ENTRY, entry);
		Intent facebookIntent = new Intent(this, NotificationShareService.class);
		facebookIntent.putExtra(NotificationShareService.EXTRAS_ID, id);
		facebookIntent.putExtra(NotificationShareService.EXTRAS_TYPE, NotificationShareService.TYPE_FACEBOOK);
		facebookIntent.putExtra(NotificationShareService.EXTRAS_SHARED_ENTRY, entry);
		PendingIntent sharePi = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), shareIntent,
				PendingIntent.FLAG_ONE_SHOT);
		PendingIntent facebookPi = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), facebookIntent,
				PendingIntent.FLAG_ONE_SHOT);


		//Show notification, when not OK it shows only without image.
		if (!TextUtils.isEmpty(image)) {
			Picasso picasso = Picasso.with(this);
			try {
				notify(id,title, desc, image, contentIntent, picasso, sharePi, facebookPi);
			} catch (NullPointerException | IOException | OutOfMemoryError e) {
				fallbackNotify(id,title, desc, contentIntent,sharePi,facebookPi);
			}

		} else {
			fallbackNotify(id,title, desc, contentIntent,sharePi,facebookPi);
		}
	}

	private void notify(long id, String title, String desc, String image, PendingIntent contentIntent, Picasso picasso, PendingIntent sharePi,PendingIntent facebookPi) throws
			IOException, OutOfMemoryError {

		Bitmap bitmap = picasso.load(Utils.uriStr2URI(image).toASCIIString()).get();
		mNotifyBuilder = new NotificationCompat.Builder(this).setWhen(id).setSmallIcon(
				R.drawable.ic_push_notify).setTicker(title).setContentTitle(title).setContentText(desc).setStyle(
				new BigPictureStyle().bigPicture(bitmap).setBigContentTitle(title)).setAutoCancel(true).setLargeIcon(
				bitmap)
				.addAction(R.drawable.ic_action_social_share, getString(R.string.action_share), sharePi)
				.addAction(R.drawable.ic_stat_f, getString(R.string.action_fb), facebookPi);
		mNotifyBuilder.setContentIntent(contentIntent);


		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
			mNotifyBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000 });
			mNotifyBuilder.setSound(Uri.parse(String.format("android.resource://%s/%s", getPackageName(),
					R.raw.signal)));
		}
		mNotifyBuilder.setLights(getResources().getColor(R.color.primary_color), 1000, 1000);

		mNotificationManager.notify((int) id, mNotifyBuilder.build());
	}

	private void fallbackNotify(long id, String title, String desc, PendingIntent contentIntent, PendingIntent sharePi,PendingIntent facebookPi) {
		mNotifyBuilder = new NotificationCompat.Builder(this).setWhen(id).setSmallIcon(
				R.drawable.ic_push_notify).setTicker(title).setContentTitle(title).setContentText(desc).setStyle(
				new BigTextStyle().bigText(desc).setBigContentTitle(title)).setAutoCancel(true)
				.addAction(R.drawable.ic_action_social_share, getString(R.string.action_share), sharePi)
				.addAction(R.drawable.ic_stat_f, getString(R.string.action_fb), facebookPi);
		mNotifyBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify((int)id, mNotifyBuilder.build());
	}
}
