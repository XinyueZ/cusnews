package com.cusnews.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cusnews.R;
import com.cusnews.databinding.CustomizedTopicsBinding;

/**
 * A dialog to define customized topics.
 *
 * @author Xinyue Zhao
 */
public final class CustomizedTopicsFragment extends DialogFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_customized_topics;
	/**
	 * Data-binding.
	 */
	private CustomizedTopicsBinding mBinding;

	public static CustomizedTopicsFragment newInstance(Context context) {
		return (CustomizedTopicsFragment) Fragment.instantiate(context, CustomizedTopicsFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding = DataBindingUtil.bind(view.findViewById(R.id.topics_fl));
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
}
