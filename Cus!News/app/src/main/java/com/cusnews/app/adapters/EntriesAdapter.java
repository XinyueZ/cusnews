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
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;

/**
 * The adapter for the list of {@link com.cusnews.ds.Entry}s.
 *
 * @author Xinyue Zhao
 */
public final class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.ViewHolder> {
	/**
	 * The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 */
	private int         mLayoutResId;
	/**
	 * Data-source.
	 */
	private List<Entry> mEntries;

	/**
	 * Constructor of {@link EntriesAdapter}
	 *
	 * @param viewType
	 * 		The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 */
	public EntriesAdapter( ViewType viewType ) {
		setData( new ArrayList<Entry>() );
		mLayoutResId = viewType.getLayoutResId();
	}

	/**
	 * Constructor of {@link EntriesAdapter}
	 *
	 * @param viewType
	 * 		The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 * @param entries
	 * 		Data-source.
	 */
	public EntriesAdapter( ViewType viewType, List<Entry> entries ) {
		setData( entries );
		mLayoutResId = viewType.getLayoutResId();
	}
	/**
	 * @return Data-source.
	 */
	public List<Entry> getData() {
		return mEntries;
	}
	/**
	 * Set data-source.
	 *
	 * @param entries
	 */
	public void setData( List<Entry> entries ) {
		mEntries = entries;
	}
	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		LayoutInflater inflater = LayoutInflater.from( cxt );
		if( !Prefs.getInstance().showAllImages() ) {
			mLayoutResId = R.layout.item_vertical_no_image_entry;
		}
		ViewDataBinding binding = DataBindingUtil.inflate( inflater, mLayoutResId, parent, false );
		return new EntriesAdapter.ViewHolder( binding );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position ) {
		final Entry entry = getData().get( position );
		holder.mBinding.setVariable( BR.entry, entry );
		holder.mBinding.executePendingBindings();
	}


	@Override
	public int getItemCount() {
		return mEntries == null ? 0 : mEntries.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;

		public ViewHolder( ViewDataBinding binding ) {
			super( binding.getRoot() );
			mBinding = binding;
		}
	}
}
