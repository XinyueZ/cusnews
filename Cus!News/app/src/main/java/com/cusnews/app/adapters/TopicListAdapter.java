package com.cusnews.app.adapters;

import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.ds.Topic;

/**
 * The adapter for the list of {@link com.cusnews.ds.Topic}s.
 *
 * @author Xinyue Zhao
 */
public final class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.ViewHolder> {
	/**
	 * Data-source.
	 */
	private List<Topic> mTopics;

	/**
	 * Layout of each line.
	 */
	private static final int ITEM_LAYOUT = R.layout.item_topic_list;


	/**
	 * Constructor of {@link TopicListAdapter}
	 *
	 * @param topics
	 * 		Data-source.
	 */
	public TopicListAdapter( List<Topic> topics ) {
		setData( topics );
	}
	/**
	 * @return Data-source.
	 */
	public List<Topic> getData() {
		return mTopics;
	}
	/**
	 * Set data-source.
	 *
	 * @param topics
	 */
	public void setData( List<Topic> topics ) {
		mTopics = topics;
	}
	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		LayoutInflater  inflater = LayoutInflater.from( cxt );
		ViewDataBinding binding  = DataBindingUtil.inflate( inflater, ITEM_LAYOUT, parent, false );
		return new TopicListAdapter.ViewHolder( binding );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position ) {
		final Topic topic = getData().get( position );
		holder.mBinding.setVariable( BR.topic, topic );
		holder.mBinding.executePendingBindings();
	}


	@Override
	public int getItemCount() {
		return mTopics == null ? 0 : mTopics.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;

		public ViewHolder( ViewDataBinding binding ) {
			super( binding.getRoot() );
			mBinding = binding;
		}
	}

}
