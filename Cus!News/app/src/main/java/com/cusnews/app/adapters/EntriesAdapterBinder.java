package com.cusnews.app.adapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.cusnews.utils.Utils;
import com.squareup.picasso.Picasso;

public final class EntriesAdapterBinder {




	@SuppressWarnings("unchecked")
	@BindingAdapter("entriesAdapter")
	public static void setEntriesBinder(RecyclerView recyclerView, RecyclerView.Adapter adp) {
		recyclerView.setAdapter(adp);
	}



	//@BindingAdapter({ "bind:imageUrl", "bind:error" })
	//public static void loadImage(ImageView view, String url, Drawable error) {
	@BindingAdapter({ "imageUrl" })
	public static void loadImage(ImageView view, String url) {
		if (TextUtils.isEmpty(url)) {
			url = "http://www.faroo.com/hp/api/faroo_attribution.png";
		}
		Picasso.with(view.getContext()).load(url).into(view);

		try {
			Picasso picasso = Picasso.with(view.getContext());
			picasso.load(Utils.uriStr2URI(url).toASCIIString()).tag(view.getContext()).into(view);
		} catch (NullPointerException e) {
			loadImage(view, "http://www.faroo.com/hp/api/faroo_attribution.png");
		}
	}
}
