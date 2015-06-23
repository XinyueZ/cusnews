package com.cusnews.app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cusnews.R;
import com.cusnews.app.fragments.CustomizedTopicsFragment;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;

/**
 * A dialog to define customized topics.
 *
 * @author Xinyue Zhao
 */
public final class CustomizedTopicsActivity extends FragmentActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_customized_topics;

	/**
	 * Show single instance of {@link CustomizedTopicsActivity}
	 *
	 * @param cxt {@link Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, CustomizedTopicsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		ViewType vt = Prefs.getInstance().getViewType();
		switch (vt) {
		case GRID:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		default:
			//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.topics_container, CustomizedTopicsFragment.newInstance(
				this))
				.commit();
	}
}
