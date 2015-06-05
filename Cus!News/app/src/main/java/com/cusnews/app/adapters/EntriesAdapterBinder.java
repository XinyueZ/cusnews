package com.cusnews.app.adapters;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.cusnews.R;
import com.cusnews.utils.Utils;
import com.squareup.picasso.Picasso;

public final class EntriesAdapterBinder {


	@SuppressWarnings("unchecked")
	@BindingAdapter("entriesAdapter")
	public static void setEntriesBinder(RecyclerView recyclerView, EntriesAdapter adp) {
		recyclerView.setAdapter(adp);
	}


	@BindingAdapter({ "bind:imageUrl", "bind:error" })
	public static void loadImage(ImageView view, String url, Drawable error) {
		Picasso.with(view.getContext()).load(url).error(error).into(view);

		try {
			Picasso picasso = Picasso.with(view.getContext());
			picasso.load(Utils.uriStr2URI(url).toASCIIString()).placeholder(R.drawable.ic_placeholder_thumb).tag(
					view.getContext()).error(error).into(view);
		} catch (NullPointerException e) {
			view.setImageResource(R.drawable.ic_placeholder_thumb);
		}
	}
}
