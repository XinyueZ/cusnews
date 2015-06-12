package com.cusnews.app.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.AppListFragment;
import com.cusnews.utils.Prefs;

/**
 * Impl. the list of all external applications.
 *
 * @author Xinyue Zhao
 */
public final class AppListImpFragment extends AppListFragment {

	/**
	 * New an instance of {@link AppListImpFragment}.
	 *
	 * @param context {@link Context}.
	 * @return An instance of {@link AppListImpFragment}.
	 */
	public static Fragment newInstance(Context context ) {
		return  AppListImpFragment.instantiate(context, AppListImpFragment.class.getName()  );
	}
	/**
	 * App that use this Chopping should know the preference-storage.
	 *
	 * @return An instance of {@link com.chopping.application.BasicPrefs}.
	 */
	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
