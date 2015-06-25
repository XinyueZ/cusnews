package com.cusnews.app.adapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.chopping.utils.Utils;
import com.cusnews.ds.Bookmark;

public final class BookmarksAdapterBinder {




	@SuppressWarnings("unchecked")
	@BindingAdapter("bookmarksAdapter")
	public static void setBookmarksBinder(RecyclerView recyclerView, RecyclerView.Adapter adp) {
		recyclerView.setAdapter(adp);
	}


	@SuppressWarnings("unchecked")
	@BindingAdapter("bookmarkClickListener")
	public static void setBookmarkClickListener(View view, final Bookmark bookmark) {
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.showShortToast(v.getContext(), "click");
			}
		});
	}


}
