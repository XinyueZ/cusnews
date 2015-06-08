package com.cusnews.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cusnews.app.fragments.DetailInfoFragment;
import com.cusnews.app.fragments.DetailSiteFragment;

/**
 * Detail-pager's adapter. A news detail and a {@link android.webkit.WebView} for news-web.
 *
 * @author Xinyue Zhao
 */
public final class DetailPagerAdapter extends FragmentStatePagerAdapter {
	/**
	 * {@link Context}
	 */
	private Context mContext;

	/**
	 * Constructor of {@link DetailPagerAdapter}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 * @param fm
	 * 		{@link FragmentManager}.
	 */
	public DetailPagerAdapter(Context cxt, FragmentManager fm) {
		super(fm);
		mContext = cxt;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return DetailInfoFragment.newInstance(mContext);
		case 1:
			return DetailSiteFragment.newInstance(mContext);
		}
		return null;
	}


	@Override
	public int getCount() {
		return 2;
	}
}
