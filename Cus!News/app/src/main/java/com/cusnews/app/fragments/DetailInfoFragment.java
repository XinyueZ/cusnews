package com.cusnews.app.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cusnews.R;
import com.cusnews.databinding.DetailInfoBinding;
import com.cusnews.ds.Entry;

/**
 * Show basic information of a news.
 *
 * @author Xinyue Zhao
 */
public final class DetailInfoFragment extends CusNewsFragment {
	private static final String EXTRAS_ENTRY = DetailInfoFragment.class.getName() + ".EXTRAS.entry";
	private static final String EXTRAS_QUERY = DetailInfoFragment.class.getName() + ".EXTRAS.query";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_detail_info;



	/**
	 * Initialize an {@link  DetailInfoFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 * @param entry
	 * 		A news {@link Entry}.
	 * 	@param query
	 * 	The query to the {@code entry}.
	 * @return An instance of {@link DetailInfoFragment}.
	 */
	public static DetailInfoFragment newInstance(Context context, Entry entry, @Nullable String query) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_ENTRY, entry);
		args.putSerializable(EXTRAS_QUERY, query);
		return (DetailInfoFragment) Fragment.instantiate(context, DetailInfoFragment.class.getName(), args);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		DetailInfoBinding binding = DataBindingUtil.bind(view.findViewById(R.id.inf_nsv));
		binding.setEntry((Entry) getArguments().getSerializable(EXTRAS_ENTRY));
		binding.setQuery(getArguments().getString(EXTRAS_QUERY));
	}
}
