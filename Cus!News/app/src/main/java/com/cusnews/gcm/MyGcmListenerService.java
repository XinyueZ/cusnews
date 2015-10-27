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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;

import com.chopping.utils.Utils;
import com.cusnews.R;
import com.cusnews.app.activities.DetailActivity;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;
import com.google.android.gms.gcm.GcmListenerService;
import com.squareup.picasso.Picasso;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;

import retrofit.Callback;
import retrofit.RetrofitError;

public class MyGcmListenerService extends GcmListenerService {
	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotifyBuilder;
	private static final String TAG = "MyGcmListenerService";

	/**
	 * Called when message is received.
	 *
	 * @param from
	 * 		SenderID of the sender.
	 * @param data
	 * 		Data bundle containing message data as key/value pairs. For Set of keys use data.keySet().
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

		final Entry entry = new Entry(title, desc, content, url, image, domain, author, true, "", date, null);
		Intent intent = new Intent(this, DetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(DetailActivity.EXTRAS_ENTRY, (Serializable) entry);
		intent.putExtra(DetailActivity.EXTRAS_QUERY, "");
		final PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent,
				PendingIntent.FLAG_ONE_SHOT);

		//Share
		Api.getTinyUrl(entry.getUrl(), new Callback<Response>() {
			@Override
			public void success(Response response, retrofit.client.Response response2) {
				String shareSubject = getString(R.string.lbl_share_entry_title, getString(R.string.application_name),
						entry.getTitle());
				String shareText = getString(R.string.lbl_share_entry_content, entry.getKwic(), response.getResult(),
						Prefs.getInstance().getAppDownloadInfo());

				createNotification(shareSubject, shareText);
			}

			@Override
			public void failure(RetrofitError error) {
				String shareSubject = getString(R.string.lbl_share_entry_title, getString(R.string.application_name),
						entry.getTitle());
				String shareText = getString(R.string.lbl_share_entry_content, entry.getKwic(), entry.getUrl(),
						Prefs.getInstance().getAppDownloadInfo());

				createNotification(shareSubject, shareText);
			}

			private void createNotification(String shareSubject, String shareText) {
				if (!TextUtils.isEmpty(image)) {
					Picasso picasso = Picasso.with(MyGcmListenerService.this);
					MyGcmListenerService.this.notify(title, desc, image, contentIntent, shareSubject, shareText,
							picasso);
				} else {
					fallbackNotify(title, desc, contentIntent, shareSubject, shareText);
				}
			}
		});


	}

	private void notify(final String title, final String desc, final String image, final PendingIntent contentIntent,
			final String shareSubject, final String shareText, final Picasso picasso) {
		AsyncTaskCompat.executeParallel(new AsyncTask<Object, Object, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Object... params) {
				try {
					return picasso.load(Utils.uriStr2URI(image).toASCIIString()).get();
				} catch (IOException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				super.onPostExecute(bitmap);
				if (bitmap == null) {
					fallbackNotify(title, desc, contentIntent, shareSubject, shareText);
				} else {
					PendingIntent sharePi = PendingIntent.getActivity(MyGcmListenerService.this, (int) System.currentTimeMillis(),
							Utils.getShareInformation(shareSubject, shareText), PendingIntent.FLAG_ONE_SHOT);

					mNotifyBuilder = new NotificationCompat.Builder(MyGcmListenerService.this).setWhen(System.currentTimeMillis())
							.setSmallIcon(R.drawable.ic_push_notify).setTicker(title).setContentTitle(title)
							.setContentText(desc).setStyle(new BigPictureStyle().bigPicture(bitmap).setBigContentTitle(
									title)).setAutoCancel(true).addAction(R.drawable.ic_action_social_share, getString(
									R.string.action_share), sharePi).setLargeIcon(bitmap);
					mNotifyBuilder.setContentIntent(contentIntent);


					AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
					if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
						mNotifyBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000 });
						mNotifyBuilder.setSound(Uri.parse(String.format("android.resource://%s/%s", getPackageName(),
								R.raw.signal)));
					}
					mNotifyBuilder.setLights(getResources().getColor(R.color.primary_color), 1000, 1000);

					mNotificationManager.notify((int) System.currentTimeMillis(), mNotifyBuilder.build());
				}
			}
		});

	}

	private void fallbackNotify(String title, String desc, PendingIntent contentIntent, String shareSubject,
			String shareText) {
		PendingIntent sharePi = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
				Utils.getShareInformation(shareSubject, shareText), PendingIntent.FLAG_ONE_SHOT);

		mNotifyBuilder = new NotificationCompat.Builder(this).setWhen(System.currentTimeMillis()).setSmallIcon(
				R.drawable.ic_push_notify).setTicker(title).setContentTitle(title).setContentText(desc).setStyle(
				new BigTextStyle().bigText(desc).setBigContentTitle(title)).addAction(R.drawable.ic_action_social_share,
				getString(R.string.action_share), sharePi).setAutoCancel(true);
		mNotifyBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify((int) System.currentTimeMillis(), mNotifyBuilder.build());
	}
}
