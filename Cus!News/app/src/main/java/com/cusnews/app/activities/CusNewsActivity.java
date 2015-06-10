package com.cusnews.app.activities;

import android.support.v7.app.AppCompatActivity;

import com.cusnews.bus.ShareEvent;

import de.greenrobot.event.EventBus;

/**
 * The basic {@link android.app.Activity} of application.
 *
 * @author Xinyue Zhao
 */
public class CusNewsActivity extends AppCompatActivity {
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link}.
	 *
	 * @param e
	 * 		Event {@link}.
	 */
	public void onEvent(Object e) {

	}

	/**
	 * Handler for {@link com.cusnews.bus.ShareEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.ShareEvent}.
	 */
	public void onEvent(final ShareEvent e) {
		startActivity(e.getIntent());
	}
	//------------------------------------------------


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
