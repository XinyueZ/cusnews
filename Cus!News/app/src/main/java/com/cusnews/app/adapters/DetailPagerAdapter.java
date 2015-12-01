package com.cusnews.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cusnews.app.fragments.DetailInfoFragment;
import com.cusnews.app.fragments.DetailInfoNoImageFragment;
import com.cusnews.app.fragments.DetailSiteFragment;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;

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
	 * News {@link Entry}.
	 */
	private Entry   mEntry;
	/**
	 * The query to the {@link #mEntry}.
	 */
	private String  mQuery;

	/**
	 * Constructor of {@link DetailPagerAdapter}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 * @param fm
	 * 		{@link FragmentManager}.
	 * @param entry
	 * 		News {@link Entry}.
	 * @param query
	 * 		The query to the {@code entry}.
	 */
	public DetailPagerAdapter( Context cxt, FragmentManager fm, Entry entry, String query ) {
		super( fm );
		mContext = cxt;
		mEntry = entry;
		mQuery = query;
	}


	@Override
	public Fragment getItem( int position ) {
		switch( position ) {
			case 0:
				if( !Prefs.getInstance().showAllImages() ) {
					return DetailInfoNoImageFragment.newInstance( mContext, mEntry, mQuery );
				} else {
					return DetailInfoFragment.newInstance( mContext, mEntry, mQuery );
				}
			case 1:
				return DetailSiteFragment.newInstance( mContext, mEntry );
		}
		return null;
	}


	@Override
	public int getCount() {
		return 2;
	}
}
