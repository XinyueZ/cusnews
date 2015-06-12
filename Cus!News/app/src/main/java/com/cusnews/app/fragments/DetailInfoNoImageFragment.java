package com.cusnews.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cusnews.R;
import com.cusnews.bus.ShareEvent;
import com.cusnews.databinding.DetailInfoNoImageBinding;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.DynamicShareActionProvider;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Show basic information of a news.
 *
 * @author Xinyue Zhao
 */
public final class DetailInfoNoImageFragment extends CusNewsFragment {
	private static final String EXTRAS_ENTRY = DetailInfoNoImageFragment.class.getName() + ".EXTRAS.entry";
	private static final String EXTRAS_QUERY = DetailInfoNoImageFragment.class.getName() + ".EXTRAS.query";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_detail_no_image_info;
	/**
	 * A tinyurl to the {@link Entry}.
	 */
	private String mSharedEntryUrl;

	/**
	 * Initialize an {@link  DetailInfoNoImageFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 * @param entry
	 * 		A news {@link Entry}.
	 * @param query
	 * 		The query to the {@code entry}.
	 *
	 * @return An instance of {@link DetailInfoNoImageFragment}.
	 */
	public static DetailInfoNoImageFragment newInstance(Context context, Entry entry, @Nullable String query) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRAS_ENTRY, entry);
		args.putSerializable(EXTRAS_QUERY, query);
		return (DetailInfoNoImageFragment) Fragment.instantiate(context, DetailInfoNoImageFragment.class.getName(), args);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		if (savedInstanceState != null) {
			mSharedEntryUrl = savedInstanceState.getString("tinyurl");
		} else {
			final Entry entry = (Entry) getArguments().getSerializable(EXTRAS_ENTRY);
			Api.getTinyUrl(entry.getUrl(), new Callback<Response>() {
				@Override
				public void success(Response response, retrofit.client.Response response2) {
					mSharedEntryUrl = response.getResult();
				}

				@Override
				public void failure(RetrofitError error) {
					mSharedEntryUrl = entry.getUrl();
				}
			});
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tinyurl", mSharedEntryUrl);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Entry entry = (Entry) getArguments().getSerializable(EXTRAS_ENTRY);
		if (entry != null) {
			DetailInfoNoImageBinding binding = DataBindingUtil.bind(view.findViewById(R.id.coordinator_layout));
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
			binding.toolbar.inflateMenu(R.menu.menu_detail);
			MenuItem shareMi = binding.toolbar.getMenu().findItem(R.id.action_share);
			DynamicShareActionProvider shareLaterProvider = (DynamicShareActionProvider) MenuItemCompat
					.getActionProvider(shareMi);
			shareLaterProvider.setShareDataType("text/plain");
			shareLaterProvider.setOnShareLaterListener(new DynamicShareActionProvider.OnShareLaterListener() {
				@Override
				public void onShareClick(final Intent shareIntent) {
					String subject = getString(R.string.lbl_share_entry_title, getString(R.string.application_name),
							entry.getTitle());
					String text = getString(R.string.lbl_share_entry_content, entry.getKwic(), mSharedEntryUrl,
							Prefs.getInstance().getAppDownloadInfo());
					shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
					shareIntent.putExtra(Intent.EXTRA_TEXT, text);
					EventBus.getDefault().post(new ShareEvent(shareIntent));
				}
			});
			binding.toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
			binding.toolbar.setTitle(entry.getDomain());
		}
	}
}
