package com.cusnews.ds;

import java.util.ArrayList;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.View.OnClickListener;

import com.cusnews.R;
import com.cusnews.gcm.SubscribeIntentService;
import com.cusnews.gcm.UnsubscribeIntentService;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.CheckBoxFontTextView;

/**
 * The topic can be subscribed as pushed by Google.
 *
 * @author Xinyue Zhao
 */
public final class Topic {
	private String mLanguage;
	private String mName;
	private @StringRes
	int mLocalNameResId;
	private String mPrefsKey;


	public Topic(String language, String name, @StringRes int localNameResId, String prefsKey) {
		mLanguage = language;
		mName = name;
		mLocalNameResId = localNameResId;
		mPrefsKey = prefsKey;
	}

	public String getLanguage() {
		return mLanguage;
	}

	public String getName() {
		return mName;
	}

	public int getLocalNameResId() {
		return mLocalNameResId;
	}

	public String getApiName() {
		return String.format("%s-%s", mName, mLanguage);
	}


	public OnClickListener ClickListener  = new OnClickListener() {
		@Override
		public void onClick(View v) {
			CheckBoxFontTextView checkBoxFontTextView = (CheckBoxFontTextView) v.findViewById(R.id.checkbox_tv);
			checkBoxFontTextView.setChecked(!checkBoxFontTextView.isChecked());
			Prefs.getInstance().setPush(mPrefsKey, checkBoxFontTextView.isChecked());
			if(checkBoxFontTextView.isChecked()) {
				Intent intent = new Intent(v.getContext(), SubscribeIntentService.class);
				intent.putExtra(SubscribeIntentService.TOPIC, getApiName());
				v.getContext().startService(intent);
			} else {
				ArrayList<String> topics = new ArrayList<>();
				topics.add(getApiName());
				Intent intent = new Intent(v.getContext(), UnsubscribeIntentService.class);
				intent.putStringArrayListExtra(UnsubscribeIntentService.TOPICS, topics);
				v.getContext().startService(intent);
			}
		}
	};

	public boolean getSubscribed() {
		return Prefs.getInstance().getPush(mPrefsKey);
	}
}
