package com.cusnews.gcm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Window;

import com.cusnews.R;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;

import retrofit.Callback;
import retrofit.RetrofitError;

public class NotificationShareService extends Activity {
	public static final String EXTRAS_TYPE = NotificationShareService.class.getName() + ".EXTRAS.type";
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_FACEBOOK = 1;
	public static final String EXTRAS_SHARED_ENTRY = NotificationShareService.class.getName() + ".EXTRAS.entry";
	/**
	 * A tinyurl to the {@link Entry}.
	 */
	private String mSharedEntryUrl;

	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(_savedInstanceState);
		onHandleIntent(getIntent());

	}

	private void onHandleIntent(final Intent intent) {
		final Entry entry = (Entry) intent.getSerializableExtra(EXTRAS_SHARED_ENTRY);
		Api.getTinyUrl(entry.getUrl(), new Callback<Response>() {
			@Override
			public void success(Response response, retrofit.client.Response response2) {
				mSharedEntryUrl = response.getResult();
				share(intent);
			}

			@Override
			public void failure(RetrofitError error) {
				mSharedEntryUrl = entry.getUrl();
				share(intent);
			}
		});
	}

	private void share(Intent intent) {
		Entry entry = (Entry) intent.getSerializableExtra(EXTRAS_SHARED_ENTRY);

		switch (intent.getIntExtra(EXTRAS_TYPE, TYPE_NORMAL)) {
		case TYPE_FACEBOOK:
			Bundle postParams = new Bundle();
			final WebDialog fbDlg = new WebDialog.FeedDialogBuilder(NotificationShareService.this, getString(
					R.string.applicationId), postParams).setCaption(entry.getTitle()).setDescription( entry.getKwic()).setLink(
					mSharedEntryUrl).build();
			fbDlg.setOnCompleteListener(new OnCompleteListener() {
				@Override
				public void onComplete(Bundle bundle, FacebookException e) {
					fbDlg.dismiss();
					ActivityCompat.finishAfterTransition(NotificationShareService.this);
				}
			});
			fbDlg.show();
			break;
		default:
			String subject = getString(R.string.lbl_share_entry_title, getString(R.string.application_name),
					entry.getTitle());
			String text = getString(R.string.lbl_share_entry_content, entry.getKwic(), mSharedEntryUrl,
					Prefs.getInstance().getAppDownloadInfo());
			Intent i = com.chopping.utils.Utils.getShareInformation(subject, text);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			startActivity(i);
			ActivityCompat.finishAfterTransition(NotificationShareService.this);
			break;
		}
	}


}
