package com.cusnews.ds;

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
	private
	@StringRes
	int mLocalNameResId;
	private String mPrefsKey;


	public Topic( String language, String name, @StringRes int localNameResId, String prefsKey ) {
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
		return String.format( "%s-%s", mName, mLanguage );
	}


	public OnClickListener ClickListener = new OnClickListener() {
		@Override
		public void onClick( View v ) {
			CheckBoxFontTextView checkBoxFontTextView = (CheckBoxFontTextView) v.findViewById( R.id.checkbox_tv );
			boolean              prevStatus           = checkBoxFontTextView.isChecked();
			checkBoxFontTextView.setChecked( !prevStatus );

			if( !prevStatus ) {
				//Previous is not checked, then we wanna check and subscribe.
				Intent intent = new Intent( v.getContext(), SubscribeIntentService.class );
				intent.putExtra( SubscribeIntentService.TOPIC, getApiName() );
				intent.putExtra( SubscribeIntentService.STORAGE_NAME, getPrefsKey() );
				intent.putExtra( SubscribeIntentService.SUBSCRIBE_NAME, v.getContext().getString( getLocalNameResId() ) );
				v.getContext().startService( intent );
			} else {
				//Unsubscribe topic.
				Intent intent = new Intent( v.getContext(), UnsubscribeIntentService.class );
				intent.putExtra( UnsubscribeIntentService.TOPIC, getApiName() );
				intent.putExtra( UnsubscribeIntentService.STORAGE_NAME, getPrefsKey() );
				intent.putExtra( UnsubscribeIntentService.UNSUBSCRIBE_NAME, v.getContext().getString( getLocalNameResId() ) );
				v.getContext().startService( intent );
			}
		}
	};

	public boolean getSubscribed() {
		return Prefs.getInstance().getPush( mPrefsKey );
	}

	private String getPrefsKey() {
		return mPrefsKey;
	}
}
