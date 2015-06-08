package com.cusnews.app.adapters;

import android.databinding.BindingAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.webkit.WebView;

import com.cusnews.ds.Entry;

public final class DetailPagerAdapterBinder {
	@SuppressWarnings("unchecked")
	@BindingAdapter("detailPagerAdapter")
	public static void setDetailPagerAdapter(ViewPager vp,  FragmentStatePagerAdapter adp) {
		vp.setAdapter(adp);
	}

	@SuppressWarnings("unchecked")
	@BindingAdapter("detailSiteUrl")
	public static void setDetailSiteUrl(WebView wv,  Entry entry) {
		wv.loadUrl(entry.getUrl());
	}
}
