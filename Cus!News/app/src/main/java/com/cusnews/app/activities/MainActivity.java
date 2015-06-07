package com.cusnews.app.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.app.SearchSuggestionProvider;
import com.cusnews.app.adapters.EntriesAdapter;
import com.cusnews.bus.ChangeViewTypeEvent;
import com.cusnews.databinding.ActivityMainBinding;
import com.cusnews.ds.Entries;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends CusNewsActivity implements  SearchView.OnQueryTextListener{
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
	private int mStart = 1;
	/**
	 * {@code true} if user loading data.
	 */
	private boolean mInProgress = false;
	/**
	 * {@code true} if the feeds arrive bottom.
	 */
	private boolean mIsBottom;
	/**
	 * Keyword that will be searched.
	 */
	private String mKeyword ="";
	/**
	 * Suggestion list while tipping.
	 */
	protected SearchRecentSuggestions mSuggestions;
	/**
	 * The search.
	 */
	private SearchView mSearchView;

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

		mKeyword = App.Instance.getLastTimeSearched();

		//For search and suggestions.
		mSuggestions = new SearchRecentSuggestions(this, getString(R.string.suggestion_auth),
				SearchSuggestionProvider.MODE);


		mBinding = DataBindingUtil.setContentView(MainActivity.this, LAYOUT);
		mBinding.setEntriesAdapter(new EntriesAdapter(App.Instance.getViewType().getLayoutResId()));
		mBinding.entriesRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(MainActivity.this));

		mBinding.entriesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				float y = ViewCompat.getY(recyclerView);
				if (y < dy ) {
					if(!mBinding.fab.isHidden()) {
						mBinding.fab.hide();
					}
				} else {
					if(mBinding.fab.isHidden()) {
						mBinding.fab.show();
					}
				}


				mVisibleItemCount = mLayoutManager.getChildCount();
				mTotalItemCount = mLayoutManager.getItemCount();
				mPastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

				if (!mIsBottom) {
					if (mLoading) {
						if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
							mLoading = false;
							mStart += 10;
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
		mBinding.contentSrl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getData();
			}
		});

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
			Api.getEntries(mKeyword, mStart, "en", App.Instance.getApiKey(), new Callback<Entries>() {
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
						Snackbar
								.make(mBinding.snackbarPosition, R.string.lbl_no_data, Snackbar.LENGTH_LONG)
								.show();
					}

					App.Instance.setLastTimeSearched(mKeyword);
				}

				@Override
				public void failure(RetrofitError error) {
					if (mStart > 10) {
						mStart -= 10;
					}
					Snackbar
							.make(mBinding.snackbarPosition, R.string.lbl_loading_error, Snackbar.LENGTH_LONG)
							.setAction(R.string.lbl_retry, new OnClickListener() {
								@Override
								public void onClick(View v) {
									getData();
								}
							})
							.show();

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

		//Search
		final MenuItem searchMenu = menu.findItem(R.id.action_search);
		MenuItemCompat.setOnActionExpandListener(searchMenu, new MenuItemCompat.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				mKeyword="";
				doSearch();
				return true;
			}
		});
		mSearchView = (SearchView) MenuItemCompat.getActionView(searchMenu);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setIconifiedByDefault(true);
		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		if (searchManager != null) {
			SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
			mSearchView.setSearchableInfo(info);
		}
		return true;
	}

	/**
	 * Start searching.
	 */
	private void doSearch() {
		mStart = 1;
		mBinding.setEntriesAdapter(new EntriesAdapter(App.Instance.getViewType().getLayoutResId()));
		getData();
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



	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	/**
	 * Getting intent var.
	 *
	 * @param intent
	 */
	protected void handleIntent(Intent intent) {
		mKeyword = intent.getStringExtra(SearchManager.QUERY);
		mKeyword = mKeyword.trim();
		if (!TextUtils.isEmpty(mKeyword)) {
			mSearchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + mKeyword + "</font>"));

			mKeyword = intent.getStringExtra(SearchManager.QUERY);
			mKeyword = mKeyword.trim();
			resetSearchView();

			mSuggestions.saveRecentQuery(mKeyword, null);

			//Do search
			doSearch();
		}
	}


	@Override
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String s) {
		InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
		resetSearchView();
		return false;
	}


	/**
	 * Reset the UI status of searchview.
	 */
	protected void resetSearchView() {
		if (mSearchView != null) {
			mSearchView.clearFocus();
		}
	}
}
