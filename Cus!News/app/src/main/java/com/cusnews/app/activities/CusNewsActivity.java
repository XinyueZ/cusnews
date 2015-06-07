package com.cusnews.app.activities;

import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

/**
 * The basic {@link android.app.Activity} of application.
 *
 * @author Xinyue Zhao
 */
public class CusNewsActivity extends AppCompatActivity {

	/**
	 * Handler for {@link}.
	 *
	 * @param e
	 * 		Event {@link}.
	 */
	public void onEvent(Object e) {

	}
	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

}
