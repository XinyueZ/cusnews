package com.cusnews.app.adapters;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.AsyncTask;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.bus.OpenRelatedEvent;
import com.cusnews.ds.Entries;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;

import org.jsoup.Jsoup;

import de.greenrobot.event.EventBus;
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
		final WeakReference<TextView> textViewWeakReference = new WeakReference<>(tv);
		if (!TextUtils.isEmpty(entry.getContent())) {
			tv.setVisibility(View.VISIBLE);
			AsyncTaskCompat.executeParallel(new AsyncTask<Entry, Void, String>() {
				@Override
				protected String doInBackground(Entry... params) {
					Entry entry = params[0];
					//try {
						return Jsoup.parse(entry.getContent()).text();
					//} catch (Exception e) {
					//	return Jsoup.parse(new URL(entry.getUrl()), 1000 * 30).body().children().text();
					//}
				}

				@Override
				protected void onPostExecute(String s) {
					super.onPostExecute(s);
					if (textViewWeakReference.get() != null) {
						textViewWeakReference.get().setText(s);
						((View) textViewWeakReference.get().getParent()).findViewById(R.id.load_pb).setVisibility(
								View.GONE);
					}
				}
			}, entry);
		} else {
			((View) tv.getParent()).setVisibility(View.GONE);
		}
	}


	@SuppressWarnings("unchecked")
	@BindingAdapter({ "bind:related", "bind:query" })
	public static void setRelated(ViewGroup vg, final Entry entry, final String query) {
		final WeakReference<ViewGroup> vgWrapper = new WeakReference<ViewGroup>(vg);
		if (entry.getRelated() != null && entry.getRelated().size() > 0) {
			Context cxt = vg.getContext();
			LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View relatedV;
			for (final Entry relatedEntry : entry.getRelated()) {
				relatedV = inflater.inflate(R.layout.item_related_entry, vg, false);
				ViewDataBinding binding = DataBindingUtil.bind(relatedV);
				binding.setVariable(BR.entry, relatedEntry);
				vg.addView(relatedV);
				relatedV.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						EventBus.getDefault().post(new OpenRelatedEvent(relatedEntry, query));
					}
				});
			}
		} else {
			Api.getEntries(query, 1, Prefs.getInstance().getLanguage(), "web", App.Instance.getApiKey(),
					new Callback<Entries>() {
						@Override
						public void success(Entries entries, Response response) {
							if (entries.getStart() <= entries.getCount() && vgWrapper.get() != null) {
								ViewGroup vg = vgWrapper.get();
								Context cxt = vg.getContext();
								LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(
										Context.LAYOUT_INFLATER_SERVICE);
								View relatedV;
								for (final Entry moreRelated : entries.getList()) {
									relatedV = inflater.inflate(R.layout.item_related_entry, vg, false);
									ViewDataBinding binding = DataBindingUtil.bind(relatedV);
									binding.setVariable(BR.entry, moreRelated);
									vg.addView(relatedV);
									relatedV.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											EventBus.getDefault().post(new OpenRelatedEvent(moreRelated, query));
										}
									});
								}
							}
						}

						@Override
						public void failure(RetrofitError error) {
							//TODO Ignore failure at loading related news.
						}
					});
		}
	}

}
