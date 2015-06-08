package com.cusnews.app.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.app.App;

/**
 * Show basic information of a news.
 *
 * @author Xinyue Zhao
 */
public final class DetailInfoFragment extends CusNewsFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_detail_info;

	/**
	 * Data-binding.
	 */
	private ViewDataBinding mBinding;

	/**
	 * Initialize an {@link  DetailInfoFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link DetailInfoFragment}.
	 */
	public static DetailInfoFragment newInstance(Context context) {
		return (DetailInfoFragment) Fragment.instantiate(context, DetailInfoFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding = DataBindingUtil.bind(view);
		mBinding.setVariable(BR.entry, App.Instance.getOpenedEntry());
	}
}
