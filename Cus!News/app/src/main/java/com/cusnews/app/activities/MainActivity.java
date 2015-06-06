package com.cusnews.app.activities;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.app.adapters.EntriesAdapter;
import com.cusnews.bus.ChangeViewTypeEvent;
import com.cusnews.databinding.ActivityMainBinding;
import com.cusnews.ds.Entries;
import com.github.johnpersano.supertoasts.SuperToast.OnClickListener;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends CusNewsActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * Height of action-bar general.
	 */
	private int mActionBarHeight;
	/**
	 * The layout-mgr controls over {@link RecyclerView}.
	 */
	private LinearLayoutManager mLayoutManager;
	/**
	 * Data-binding.
	 */
	private ActivityMainBinding mBinding;
	/**
	 * Page-pointer.
	 */
	private int mCurrentPage = 1;
	/**
	 * {@code true} if user loading data.
	 */
	private boolean mInProgress = false;
	/**
	 * {@code true} if the feeds arrive bottom.
	 */
	private boolean mIsBottom;

	/**
	 * Calculate height of actionbar.
	 */
	protected void calcActionBarHeight() {
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);
	}


	private int mVisibleItemCount;
	private int mPastVisibleItems;
	private int mTotalItemCount;
	private boolean mLoading = true;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.cusnews.bus.ChangeViewTypeEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.ChangeViewTypeEvent}.
	 */
	public void onEvent(ChangeViewTypeEvent e) {
		EntriesAdapter newAdp = new EntriesAdapter(e.getViewType().getLayoutResId(),
				mBinding.getEntriesAdapter().getData());
		mBinding.setEntriesAdapter(newAdp);
	}

	//------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calcActionBarHeight();


		mBinding = DataBindingUtil.setContentView(MainActivity.this, LAYOUT);
		mBinding.setEntriesAdapter(new EntriesAdapter(App.Instance.getViewType().getLayoutResId()));
		mBinding.entriesRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(MainActivity.this));

		mBinding.entriesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

				mVisibleItemCount = mLayoutManager.getChildCount();
				mTotalItemCount = mLayoutManager.getItemCount();
				mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

				if (!mIsBottom) {
					if (mLoading) {
						if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
							mLoading = false;
							mCurrentPage += 10;
							getData();
						}
					}
				}
			}
		});


		mBinding.contentSrl.setColorSchemeResources(R.color.green_1, R.color.green_2, R.color.green_3, R.color.green_4);
		mBinding.contentSrl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getData();
			}
		});
		mBinding.contentSrl.setProgressViewEndTarget(true, mActionBarHeight * 2);
		mBinding.contentSrl.setProgressViewOffset(false, 0, mActionBarHeight * 2);
		mBinding.contentSrl.setRefreshing(true);

		setSupportActionBar(mBinding.toolbar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getData();
	}

	/**
	 * Get data from server.
	 */
	private void getData() {
		if (!mInProgress) {
			mBinding.contentSrl.setRefreshing(true);
			mInProgress = true;
			Api.getEntries("", mCurrentPage, "en", App.Instance.getApiKey(), new Callback<Entries>() {
				@Override
				public void success(Entries entries, Response response) {

					mBinding.getEntriesAdapter().getData().addAll(entries.getList());
					mBinding.getEntriesAdapter().notifyDataSetChanged();
					//Finish loading
					mBinding.contentSrl.setRefreshing(false);
					mInProgress = false;
					mLoading = true;

					//Arrive bottom?
					if (entries.getStart() > entries.getCount()) {
						mIsBottom = true;
					}
				}

				@Override
				public void failure(RetrofitError error) {
					if (mCurrentPage > 10) {
						mCurrentPage -= 10;
					}
					showErrorToast("RetrofitError", new OnClickListener() {
						@Override
						public void onClick(View view, Parcelable parcelable) {
							getData();
						}
					});

					//Finish loading
					mBinding.contentSrl.setRefreshing(false);
					mInProgress = false;
					mLoading = true;
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_to_top:
			if (mBinding.getEntriesAdapter() != null && mBinding.getEntriesAdapter().getItemCount() > 0) {
				mLayoutManager.scrollToPositionWithOffset(0, 0);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	/**
	 * Add new label.
	 *
	 * @param view
	 * 		No usage.
	 */
	public void onActionButtonClick(View view) {
	}
}
