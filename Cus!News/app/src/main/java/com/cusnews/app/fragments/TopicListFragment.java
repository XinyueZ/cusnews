package com.cusnews.app.fragments;

import java.util.List;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cusnews.R;
import com.cusnews.app.adapters.TopicListAdapter;
import com.cusnews.databinding.TopicListBinding;
import com.cusnews.ds.Topic;
import com.cusnews.ds.TopicsFactory;

/**
 * A list of all topics that will be subscribed to push.
 *
 * @author Xinyue Zhao
 */
public final class TopicListFragment extends CusNewsFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_topic_list;
	/**
	 * Pre-defined list of push-topics.
	 */
	private List<Topic> mTopicList;
	/**
	 * Data-binding.
	 */
	private TopicListBinding mBinding;

	public static TopicListFragment newInstance(Context context) {
		return (TopicListFragment) Fragment.instantiate(context, TopicListFragment.class.getName());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTopicList = TopicsFactory.create();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding = DataBindingUtil.bind(view.findViewById(R.id.topic_list_rv));
		mBinding.topicListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mBinding.setTopicsAdapter(new TopicListAdapter(mTopicList));
	}
}
