package com.cusnews.app.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cusnews.R;
import com.cusnews.app.App;
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
	 * @param query
	 * 		The query to the {@code entry}.
	 *
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
		Entry entry = (Entry) getArguments().getSerializable(EXTRAS_ENTRY);
		if (entry != null) {
			DetailInfoBinding binding = DataBindingUtil.bind(view.findViewById(R.id.coordinator_layout));
			binding.setEntry(entry);
			binding.setQuery(getArguments().getString(EXTRAS_QUERY));


			//Init actionbar
			binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
			binding.toolbar.setNavigationOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ActivityCompat.finishAfterTransition(getActivity());
				}
			});
			binding.collapsingToolbar.setTitle(entry.getDomain());

			//Init ImageView
			ViewCompat.setElevation(binding.thumbIv, getResources().getDimensionPixelSize(R.dimen.common_elevation));
			DisplayMetrics metrics = new DisplayMetrics();
			DisplayManagerCompat.getInstance(App.Instance).getDisplay(0).getMetrics(metrics);
			binding.thumbIv.getLayoutParams().height = metrics.heightPixels / 2;
		}
	}
}
