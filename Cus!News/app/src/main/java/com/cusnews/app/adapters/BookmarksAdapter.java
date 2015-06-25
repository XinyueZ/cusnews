package com.cusnews.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.ds.Bookmark;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;

/**
 * The adapter for the list of {@link com.cusnews.ds.Bookmark}s.
 *
 * @author Xinyue Zhao
 */
public final class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolder> {
	/**
	 * The view-type,  {@link ViewType}.
	 */
	private int mLayoutResId;
	/**
	 * Data-source.
	 */
	private List<Bookmark> mBookmarks;

	/**
	 * Constructor of {@link BookmarksAdapter}
	 *
	 * @param viewType The view-type,  {@link ViewType}.
	 */
	public BookmarksAdapter(ViewType viewType) {
		setData(new ArrayList<Bookmark>());
		mLayoutResId = viewType.getLayoutResId();
	}

	/**
	 * Constructor of {@link BookmarksAdapter}
	 *
	 * @param  viewType  The view-type,  {@link ViewType}.
	 * @param bookmarks
	 * 		Data-source.
	 */
	public BookmarksAdapter(ViewType viewType, List<Bookmark> bookmarks) {
		setData(bookmarks);
		mLayoutResId = viewType.getLayoutResId();
	}

	/**
	 * Set data-source.
	 *
	 * @param bookmarks
	 */
	public void setData(List<Bookmark> bookmarks) {
		mBookmarks = bookmarks;
	}

	/**
	 * @return Data-source.
	 */
	public List<Bookmark> getData() {
		return mBookmarks;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		LayoutInflater inflater = LayoutInflater.from(cxt);
		if(!Prefs.getInstance().showAllImages()) {
			mLayoutResId = R.layout.item_vertical_no_image_entry;
		}
		ViewDataBinding binding = DataBindingUtil.inflate(inflater, mLayoutResId, parent, false);
		return new BookmarksAdapter.ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final Entry entry = getData().get(position);
		holder.mBinding.setVariable(BR.entry, entry);
		holder.mBinding.executePendingBindings();
	}


	@Override
	public int getItemCount() {
		return mBookmarks == null ? 0 : mBookmarks.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;

		public ViewHolder(ViewDataBinding binding) {
			super(binding.getRoot());
			mBinding = binding;
		}
	}
}
