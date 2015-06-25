package com.cusnews.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cusnews.R;
import com.cusnews.app.activities.MainActivity;
import com.cusnews.app.adapters.BookmarksAdapter;
import com.cusnews.bus.BookmarksInitEvent;
import com.cusnews.bus.BookmarksLoadingErrorEvent;
import com.cusnews.bus.CloseBookmarksEvent;
import com.cusnews.databinding.BookmarksBinding;
import com.cusnews.utils.BookmarksManager;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;

import de.greenrobot.event.EventBus;

/**
 * List of all {@link com.cusnews.ds.Bookmark}s.
 *
 * @author Xinyue Zhao
 */
public final class BookmarksFragment extends CusNewsFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_bookmarks;
	/**
	 * Data-binding.
	 */
	private BookmarksBinding mBinding;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.cusnews.bus.BookmarksInitEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.BookmarksInitEvent}.
	 */
	public void onEvent(BookmarksInitEvent e) {
		mBinding.setBookmarksAdapter(new BookmarksAdapter(Prefs.getInstance().getViewType(),
				BookmarksManager.getInstance().getCachedBookmarks()));
		mBinding.bookmarksSrl.setRefreshing(false);
		mBinding.bookmarksTv.setVisibility(View.GONE);

		if(mBinding.getBookmarksAdapter().getItemCount() == 0) {
			mBinding.bookmarksTv.setVisibility(View.VISIBLE);
			mBinding.bookmarksTv.setText(R.string.lbl_empty_bookmarks);
		}
	}

	/**
	 * Handler for {@link BookmarksLoadingErrorEvent}.
	 *
	 * @param e
	 * 		Event {@link BookmarksLoadingErrorEvent}.
	 */
	public void onEvent(BookmarksLoadingErrorEvent e) {
		mBinding.bookmarksSrl.setRefreshing(false);
		mBinding.bookmarksTv.setVisibility(View.VISIBLE);
		mBinding.bookmarksTv.setText(R.string.lbl_load_bookmarks_error);

		Snackbar.make(mBinding.coordinatorLayout, R.string.lbl_loading_error, Snackbar.LENGTH_LONG).setAction(
				R.string.lbl_retry, new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBinding.bookmarksSrl.setRefreshing(true);
				mBinding.bookmarksTv.setVisibility(View.GONE);
				BookmarksManager.getInstance().init();
			}
		}).show();
	}

	//------------------------------------------------
	public static BookmarksFragment newInstance(Context context) {
		return (BookmarksFragment) Fragment.instantiate(context, BookmarksFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding = DataBindingUtil.bind(view.findViewById(R.id.coordinator_layout));

		//Init pull2load.
		mBinding.bookmarksSrl.setColorSchemeResources(R.color.green_1, R.color.green_2, R.color.green_3,
				R.color.green_4);
		mBinding.bookmarksSrl.setProgressViewEndTarget(true, getActionBarHeight(getActivity()) * 2);
		mBinding.bookmarksSrl.setProgressViewOffset(false, 0, getActionBarHeight(getActivity()) * 2);
		mBinding.bookmarksSrl.setRefreshing(true);
		mBinding.bookmarksSrl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				BookmarksManager.getInstance().init();
				mBinding.bookmarksTv.setVisibility(View.GONE);
			}
		});

		//Init actionbar.
		mBinding.toolbar.setTitle(R.string.action_bookmarks);
		mBinding.toolbar.setTitleTextColor(getResources().getColor(R.color.common_white));
		mBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
		mBinding.toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new CloseBookmarksEvent());
			}
		});


		ViewType vt = Prefs.getInstance().getViewType();
		//Init recycleview.
		switch (vt) {
		case GRID:
			mBinding.bookmarksRv.setLayoutManager(new GridLayoutManager(getActivity(), MainActivity.GRID_SPAN));
			break;
		default:
			mBinding.bookmarksRv.setLayoutManager(new LinearLayoutManager(getActivity()));
			//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}
		boolean inited = BookmarksManager.getInstance().isInit();
		if (inited) {
			mBinding.bookmarksSrl.setRefreshing(false);
			mBinding.bookmarksTv.setVisibility(View.GONE);
			mBinding.setBookmarksAdapter(new BookmarksAdapter(vt, BookmarksManager.getInstance().getCachedBookmarks()));
		}
	}

	@Override
	public void onResume() {
		if(!BookmarksManager.getInstance().isInit() ) {
			BookmarksManager.getInstance().init();
		} else {
			if (mBinding.getBookmarksAdapter() != null) {
				mBinding.getBookmarksAdapter().notifyDataSetChanged();
				if (mBinding.getBookmarksAdapter().getItemCount() == 0) {
					mBinding.bookmarksTv.setVisibility(View.VISIBLE);
					mBinding.bookmarksTv.setText(R.string.lbl_empty_bookmarks);
				}
			}
		}
		super.onResume();
	}


	/**
	 * Get height of {@link android.support.v7.app.ActionBar}.
	 *
	 * @param activity
	 * 		{@link Activity} that hosts an  {@link android.support.v7.app.ActionBar}.
	 *
	 * @return Height of bar.
	 */
	public static int getActionBarHeight(Activity activity) {
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = activity.obtainStyledAttributes(abSzAttr);
		return a.getDimensionPixelSize(0, -1);
	}
}
