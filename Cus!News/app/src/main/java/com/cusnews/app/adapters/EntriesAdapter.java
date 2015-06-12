package com.cusnews.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.bus.OpenEntryEvent;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;

import de.greenrobot.event.EventBus;

/**
 * The adapter for the list of {@link com.cusnews.ds.Entry}s.
 *
 * @author Xinyue Zhao
 */
public final class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.ViewHolder> {
	/**
	 * The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 */
	private int mLayoutResId;
	/**
	 * Data-source.
	 */
	private List<Entry> mEntries;

	/**
	 * Constructor of {@link EntriesAdapter}
	 *
	 * @param layoutResId
	 * 		The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 */
	public EntriesAdapter(@LayoutRes int layoutResId) {
		setData(new ArrayList<Entry>());
		mLayoutResId = layoutResId;
	}

	/**
	 * Constructor of {@link EntriesAdapter}
	 *
	 * @param layoutResId
	 * 		The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 * @param entries
	 * 		Data-source.
	 */
	public EntriesAdapter(@LayoutRes int layoutResId, List<Entry> entries) {
		setData(entries);
		mLayoutResId = layoutResId;
	}

	/**
	 * Set data-source.
	 *
	 * @param entries
	 */
	public void setData(List<Entry> entries) {
		mEntries = entries;
	}

	/**
	 * @return Data-source.
	 */
	public List<Entry> getData() {
		return mEntries;
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
		return new EntriesAdapter.ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		final Entry entry = getData().get(position);
		holder.mBinding.setVariable(BR.entry, entry);
		holder.mBinding.executePendingBindings();

		//TODO Must be optimized late.
		holder.mBinding.getRoot().findViewById(R.id.content_v).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new OpenEntryEvent(entry));
			}
		});
	}


	@Override
	public int getItemCount() {
		return mEntries == null ? 0 : mEntries.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;

		public ViewHolder(ViewDataBinding binding) {
			super(binding.getRoot());
			mBinding = binding;
		}
	}
}
