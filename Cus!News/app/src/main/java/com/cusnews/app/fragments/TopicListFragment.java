package com.cusnews.app.fragments;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.chopping.utils.Utils;
import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.app.adapters.TopicListAdapter;
import com.cusnews.bus.SelectedTopicsEvent;
import com.cusnews.databinding.TopicListBinding;
import com.cusnews.ds.Topic;
import com.cusnews.ds.TopicsFactory;
import com.cusnews.gcm.SubscribeIntentService;
import com.cusnews.utils.Prefs;

import de.greenrobot.event.EventBus;

/**
 * A list of all topics that will be subscribed to push.
 *
 * @author Xinyue Zhao
 */
public final class TopicListFragment extends DialogFragment {
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
	/**
	 * Listener while abo push-topic.
	 */
	private BroadcastReceiver mSubscribeReceiver;

	public static TopicListFragment newInstance(Context context) {
		return (TopicListFragment) Fragment.instantiate(context, TopicListFragment.class.getName());
	}

	private android.os.Handler mHandler = new android.os.Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mTopicList = TopicsFactory.create();

		mSubscribeReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, final Intent intent) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						String name = intent.getStringExtra(SubscribeIntentService.SUBSCRIBE_NAME);
						boolean result = intent.getBooleanExtra(SubscribeIntentService.SUBSCRIBE_RESULT, false);

						if(result) {
							mBinding.getTopicsAdapter().notifyDataSetChanged();
						} else {
							Utils.showLongToast(App.Instance, getString(R.string.lbl_subscribe_fail, name));
						}
					}
				});
			}
		};
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSubscribeReceiver, new IntentFilter(
				SubscribeIntentService.SUBSCRIBE_COMPLETE));
	}

	@Override
	public void onPause() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSubscribeReceiver);
		super.onPause();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding = DataBindingUtil.bind(view.findViewById(R.id.topic_list_ll));
		mBinding.topicListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
		mBinding.setTopicsAdapter(new TopicListAdapter(mTopicList));
		mBinding.closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				if (activity != null) {
					ActivityCompat.finishAfterTransition(activity);
				}
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Prefs prefs = Prefs.getInstance();
		prefs.setPushSelections(null);
		StringBuilder list = new StringBuilder();
		List<Topic> topics = mBinding.getTopicsAdapter().getData();
		for (Topic topic : topics) {
			if (topic.getSubscribed()) {
				list.append(topic.getApiName());
				list.append(",");
			}
		}
		if (list.length() > 0) {
			list.delete(list.length() - 1, list.length());//Remove last ","
		}
		prefs.setPushSelections(list.toString());
		EventBus.getDefault().post(new SelectedTopicsEvent());
	}
}
