package com.cusnews.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.cusnews.R;
import com.cusnews.app.fragments.TopicListFragment;

/**
 * A dialog-model {@link Activity}.
 *
 * @author Xinyue Zhao
 */
public final class TopicListActivity extends FragmentActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_topic_list;

	/**
	 * Show single instance of {@link TopicListActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 */
	public static void showInstance(Context cxt) {
		Intent intent = new Intent(cxt, TopicListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		getSupportFragmentManager().beginTransaction().replace(R.id.topic_list_container, TopicListFragment.newInstance(
				this))
				.commit();
	}
}
