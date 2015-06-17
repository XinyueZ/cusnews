package com.cusnews.app.adapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

public final class TopicListAdapterBinder   {
	@SuppressWarnings("unchecked")
	@BindingAdapter("topicsAdapter")
	public static void setEntriesBinder(RecyclerView recyclerView, RecyclerView.Adapter adp) {
		recyclerView.setAdapter(adp);
	}
}
