package com.cusnews.app.adapters;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.ds.Entries;
import com.cusnews.ds.Entry;

import org.jsoup.Jsoup;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class DetailPagerAdapterBinder {
	@SuppressWarnings("unchecked")
	@BindingAdapter("detailPagerAdapter")
	public static void setDetailPagerAdapter(ViewPager vp, FragmentStatePagerAdapter adp) {
		vp.setAdapter(adp);
	}

	@SuppressWarnings("unchecked")
	@BindingAdapter("detailSiteUrl")
	public static void setDetailSiteUrl(WebView wv, Entry entry) {
		wv.loadUrl(entry.getUrl());
	}

	@SuppressWarnings("unchecked")
	@BindingAdapter("entryContent")
	public static void setEntryContent(TextView tv, Entry entry) {
		if (!TextUtils.isEmpty(entry.getContent())) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(Jsoup.parse(entry.getContent()).text());
		} else {
			tv.setVisibility(View.GONE);
		}
	}


	@SuppressWarnings("unchecked")
	@BindingAdapter( { "bind:related", "bind:query" } )
	public static void setRelated(  ViewGroup vg, final Entry entry, final String query) {
		final WeakReference<ViewGroup> vgWrapper = new WeakReference<ViewGroup>(vg);
		if (entry.getRelated().size() > 0) {
			Context cxt = vg.getContext();
			LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View relatedV;
			for (Entry relatedEntry : entry.getRelated()) {
				relatedV = inflater.inflate(R.layout.item_related_entry, vg, false);
				ViewDataBinding binding = DataBindingUtil.bind(relatedV);
				binding.setVariable(BR.entry, relatedEntry);
				vg.addView(relatedV);
			}
		} else {
			Api.getEntries(query, 1, App.Instance.getLanguage(), "web", App.Instance.getApiKey(), new Callback<Entries>() {
				@Override
				public void success(Entries entries, Response response) {
					if (entries.getStart() <= entries.getCount() && vgWrapper.get() != null) {
						ViewGroup vg = vgWrapper.get();
						Context cxt = vg.getContext();
						LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View relatedV;
						for (Entry moreRelated : entries.getList()) {
							relatedV = inflater.inflate(R.layout.item_related_entry, vg, false);
							ViewDataBinding binding = DataBindingUtil.bind(relatedV);
							binding.setVariable(BR.entry, moreRelated);
							vg.addView(relatedV);
						}
					}
				}

				@Override
				public void failure(RetrofitError error) {

				}
			});
		}
	}

}
