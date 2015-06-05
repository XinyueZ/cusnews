package com.cusnews.app.adapters;

import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.databinding.EntryBinding;
import com.cusnews.ds.Entry;

/**
 * The adapter for the list of {@link com.cusnews.ds.Entry}s.
 *
 * @author Xinyue Zhao
 */
public final class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.ViewHolder> {
	/**
	 * Main layout for this component.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_entry;
	/**
	 * Data-source.
	 */
	private List<Entry> mEntries;

	/**
	 * Constructor of {@link EntriesAdapter}
	 */
	public EntriesAdapter() {
		setData(new ObservableArrayList<Entry>());
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
		EntryBinding binding = DataBindingUtil.inflate(inflater, ITEM_LAYOUT, parent, false);
		return new EntriesAdapter.ViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		final Entry entry = getData().get(position);
		holder.mBinding.setVariable(BR.entry, entry);
		holder.mBinding.executePendingBindings();
	}


	@Override
	public int getItemCount() {
		return mEntries == null ? 0 : mEntries.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private EntryBinding mBinding;

		public ViewHolder(EntryBinding binding) {
			super(binding.getRoot());
			mBinding = binding;
		}
	}
}
