package com.cusnews.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

import com.chopping.application.LL;
import com.chopping.bus.ApplicationConfigurationDownloadedEvent;
import com.chopping.bus.ApplicationConfigurationLoadingIgnoredEvent;
import com.chopping.exceptions.CanNotOpenOrFindAppPropertiesException;
import com.chopping.exceptions.InvalidAppPropertiesException;
import com.cusnews.R;
import com.cusnews.utils.Prefs;

import de.greenrobot.event.EventBus;


/**
 * Setting .
 */
public final class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	/**
	 * The "ActionBar".
	 */
	private Toolbar mToolbar;

	/**
	 * Progress indicator.
	 */
	private ProgressDialog mPb;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.chopping.bus.ApplicationConfigurationDownloadedEvent}
	 *
	 * @param e
	 * 		Event {@link  com.chopping.bus.ApplicationConfigurationDownloadedEvent}.
	 */
	public void onEvent(ApplicationConfigurationDownloadedEvent e) {
		onAppConfigLoaded();
	}

	/**
	 * Handler for {@link com.chopping.bus.ApplicationConfigurationLoadingIgnoredEvent}.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.ApplicationConfigurationLoadingIgnoredEvent}.
	 */
	public void onEvent(ApplicationConfigurationLoadingIgnoredEvent e) {
		LL.i("Ignored a change to load application's configuration.");
		onAppConfigIgnored();
	}

	//------------------------------------------------

	/**
	 * Show an instance of SettingsActivity.
	 *
	 * @param context
	 * 		A context object.
	 */
	public static void showInstance(Activity context) {
		Intent intent = new Intent(context, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Prefs prefs = Prefs.getInstance();
		//		if(getResources().getBoolean(R.bool.landscape)) {
		//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//		}
		addPreferencesFromResource(R.xml.settings);
		//		mPb = ProgressDialog.show(this, null, getString(R.string.msg_app_init));
		//		mPb.setCancelable(true);
		mToolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar, null, false);
		addContentView(mToolbar, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mToolbar.setTitle(R.string.action_settings);
		mToolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
		mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		mToolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				backPressed();
			}
		});
		setTitle(R.string.action_settings);


		ListPreference sort = (ListPreference) findPreference(Prefs.KEY_LANG_VALUE);
		String value = prefs.getLanguageValue();
		sort.setValue(value);
		int pos = Integer.valueOf(value);
		String[] arr = getResources().getStringArray(R.array.lang_types);
		sort.setSummary(arr[pos]);
		sort.setOnPreferenceChangeListener(this);


		CheckBoxPreference showImages = (CheckBoxPreference) findPreference(Prefs.KEY_SHOW_IMAGES);
		showImages.setOnPreferenceChangeListener(this);


		((MarginLayoutParams) findViewById(android.R.id.list).getLayoutParams()).topMargin = getActionBarHeight(this);
	}


	/**
	 * Get height of {@link android.support.v7.app.ActionBar}.
	 *
	 * @param activity
	 * 		{@link Activity} that hosts an  {@link android.support.v7.app.ActionBar}.
	 *
	 * @return Height of bar.
	 */
	public static int getActionBarHeight(Activity activity) {
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = activity.obtainStyledAttributes(abSzAttr);
		return a.getDimensionPixelSize(0, -1);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			backPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().equals(Prefs.KEY_LANG_VALUE)) {
			int pos = Integer.valueOf(newValue.toString());
			String[] arr = getResources().getStringArray(R.array.lang_types);
			preference.setSummary(arr[pos]);

			switch (pos) {
			case 0:
				Prefs.getInstance().setLanguage("en");
				break;
			case 1:
				Prefs.getInstance().setLanguage("de");
				break;
			case 2:
				Prefs.getInstance().setLanguage("zh");
				break;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.application_name).setMessage(R.string.lbl_app_reload).setNegativeButton(
					R.string.btn_no, null).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ActivityCompat.finishAffinity(SettingActivity.this);
				}
			});
			builder.create().show();
		}

		if (preference.getKey().equals(Prefs.KEY_SHOW_IMAGES)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.application_name).setMessage(R.string.lbl_app_reload).setNegativeButton(
					R.string.btn_no, null).setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.finishAffinity(SettingActivity.this);
						}
					});
			builder.create().show();
		}
		return true;
	}


	@Override
	protected void onResume() {
		EventBus.getDefault().registerSticky(this);
		super.onResume();

		String mightError = null;
		try {
			Prefs.getInstance().downloadApplicationConfiguration();
		} catch (InvalidAppPropertiesException _e) {
			mightError = _e.getMessage();
		} catch (CanNotOpenOrFindAppPropertiesException _e) {
			mightError = _e.getMessage();
		}
		if (mightError != null) {
			new AlertDialog.Builder(this).setTitle(com.chopping.R.string.app_name).setMessage(mightError).setCancelable(
					false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).create().show();
		}
	}

	@Override
	protected void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	/**
	 * Remove the progress indicator.
	 */
	private void dismissPb() {
		if (mPb != null && mPb.isShowing()) {
			mPb.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		backPressed();
	}

	private void onAppConfigLoaded() {
		dismissPb();
	}

	private void onAppConfigIgnored() {
		dismissPb();
	}

	private void backPressed() {
		dismissPb();
		ActivityCompat.finishAfterTransition(this);
	}
}
