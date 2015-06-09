package com.cusnews.app.adapters;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

import com.cusnews.ds.Entry;
import com.cusnews.utils.URLImageParser;

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

	@SuppressWarnings("unchecked")
	@BindingAdapter("entryContent")
	public static void setDetailSiteUrl(TextView tv,  Entry entry) {
		Context cxt = tv.getContext();
		Spanned htmlSpan = Html.fromHtml(entry.getContent(), new URLImageParser(cxt, tv),
				null);
		tv.setText(htmlSpan);
	}
}
