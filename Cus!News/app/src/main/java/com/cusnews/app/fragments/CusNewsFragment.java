package com.cusnews.app.fragments;

import com.chopping.application.BasicPrefs;
import com.chopping.fragments.BaseFragment;
import com.cusnews.utils.Prefs;

/**
 * Basic {@link android.support.v4.app.Fragment}.
 *
 * @author Xinyue Zhao
 */
public   class CusNewsFragment extends BaseFragment {

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}
}
