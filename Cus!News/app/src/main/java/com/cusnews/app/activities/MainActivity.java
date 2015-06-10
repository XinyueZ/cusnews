package com.cusnews.app.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.TextView;

import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.app.SearchSuggestionProvider;
import com.cusnews.app.adapters.EntriesAdapter;
import com.cusnews.bus.ChangeViewTypeEvent;
import com.cusnews.bus.OpenEntryEvent;
import com.cusnews.bus.ShareEvent;
import com.cusnews.databinding.ActivityMainBinding;
import com.cusnews.ds.Entries;
import com.cusnews.widgets.DynamicShareActionProvider;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends CusNewsActivity implements SearchView.OnQueryTextListener {
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
	private String mKeyword = "";
	/**
	 * Suggestion list while tipping.
	 */
	protected SearchRecentSuggestions mSuggestions;
	/**
	 * The search.
	 */
	private SearchView mSearchView;
	/**
	 * Drawer
	 */
	private DrawerLayout mDrawerLayout;
	/**
	 * Different type: News: news, Search: web, Topics: topics.
	 */
	private String mSrc = "news";

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


	/**
	 * Handler for {@link com.cusnews.bus.OpenEntryEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.OpenEntryEvent}.
	 */
	public void onEvent(OpenEntryEvent e) {
		DetailActivity.showInstance(this, e.getEntry(), mKeyword);
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


		mBinding = DataBindingUtil.setContentView(this, LAYOUT);

		//Init adapter
		mBinding.setEntriesAdapter(new EntriesAdapter(App.Instance.getViewType().getLayoutResId()));

		//Init recycleview.
		mBinding.entriesRv.setLayoutManager(mLayoutManager = new LinearLayoutManager(MainActivity.this));
		mBinding.entriesRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				float y = ViewCompat.getY(recyclerView);
				if (y < dy) {
					if (!mBinding.fab.isHidden()) {
						mBinding.fab.hide();
					}
				} else {
					if (mBinding.fab.isHidden()) {
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

		//Init pull2load
		mBinding.contentSrl.setColorSchemeResources(R.color.green_1, R.color.green_2, R.color.green_3, R.color.green_4);
		mBinding.contentSrl.setProgressViewEndTarget(true, mActionBarHeight * 2);
		mBinding.contentSrl.setProgressViewOffset(false, 0, mActionBarHeight * 2);
		mBinding.contentSrl.setRefreshing(true);
		mBinding.contentSrl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getData();
			}
		});

		//Init actionbar.
		setSupportActionBar(mBinding.toolbar);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		if (navigationView != null) {
			setupDrawerContent(navigationView);
		}
		getData();

		//Init tabs
		mBinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
		mBinding.tabs.addTab(addTab("Home"));
		mBinding.tabs.addTab(addTab("China"));
		mBinding.tabs.addTab(addTab("USA"));
		mBinding.tabs.addTab(addTab("Japan"));
		mBinding.tabs.addTab(addTab("Germany"));
		mBinding.tabs.addTab(addTab("Porn"));
		mBinding.tabs.setOnTabSelectedListener(new OnTabSelectedListener() {
			@Override
			public void onTabSelected(Tab tab) {
				mKeyword = tab.getText().toString();
				if (TextUtils.equals("Home", mKeyword)) {
					mKeyword = "";
				}
				clear();
				getData();
			}

			@Override
			public void onTabUnselected(Tab tab) {

			}

			@Override
			public void onTabReselected(Tab tab) {

			}
		});
	}

	/**
	 * Add customized view to {@link Tab}.
	 *
	 * @param text
	 * 		The text to show on customized view to {@link Tab}.
	 *
	 * @return The customized  {@link Tab}.
	 */
	private Tab addTab(String text) {
		final Tab tab = mBinding.tabs.newTab();
		tab.setText(text);
		View tabV = getLayoutInflater().inflate(R.layout.tab, null, false);
		tab.setCustomView(tabV);
		TextView tabTv = (TextView) tabV.findViewById(R.id.text);
		tabTv.setText(text);
		tabV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tab.select();
			}
		});
		return tab;
	}


	/**
	 * Get data from server.
	 */
	private void getData() {
		if (!mInProgress) {
			mBinding.contentSrl.setRefreshing(true);
			mInProgress = true;
			Api.getEntries(mKeyword, mStart, App.Instance.getLanguage(), mSrc, App.Instance.getApiKey(),
					new Callback<Entries>() {
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
								if (TextUtils.equals(mSrc, "web")) {
									mIsBottom = true;
									Snackbar.make(mBinding.mainCl, R.string.lbl_no_data, Snackbar.LENGTH_LONG).show();
									//For next
									mSrc = "news";
								} else {
									mIsBottom = false;
									mSrc = "web";
									Snackbar.make(mBinding.mainCl, R.string.lbl_search_more, Snackbar.LENGTH_LONG)
											.show();
									mStart = 1;
									getData();
								}
							}

							App.Instance.setLastTimeSearched(mKeyword);
						}

						@Override
						public void failure(RetrofitError error) {
							if (mStart > 10) {
								mStart -= 10;
							}
							Snackbar.make(mBinding.mainCl, R.string.lbl_loading_error, Snackbar.LENGTH_LONG).setAction(
									R.string.lbl_retry, new OnClickListener() {
										@Override
										public void onClick(View v) {
											getData();
										}
									}).show();

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
				mKeyword = "";
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


	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		//Share application.
		MenuItem menuAppShare = menu.findItem(R.id.action_share);
		DynamicShareActionProvider shareLaterProvider = (DynamicShareActionProvider) MenuItemCompat.getActionProvider(
				menuAppShare);
		shareLaterProvider.setShareDataType("text/plain");
		shareLaterProvider.setOnShareLaterListener(new DynamicShareActionProvider.OnShareLaterListener() {
			@Override
			public void onShareClick(final Intent shareIntent) {
				String subject = getString(R.string.lbl_share_app_title);
				String text = getString(R.string.lbl_share_app_content, getString(R.string.application_name),
						App.Instance.getAppDownloadInfo());
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				shareIntent.putExtra(Intent.EXTRA_TEXT, text);
				EventBus.getDefault().post(new ShareEvent(shareIntent));
			}
		});
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Start searching.
	 */
	private void doSearch() {
		clear();
		getData();
	}

	/**
	 * Reset page to init.
	 */
	private void clear() {
		mIsBottom = false;
		mStart = 1;
		mBinding.getEntriesAdapter().getData().clear();
		mSrc = "news";
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mDrawerLayout.openDrawer(GravityCompat.START);
			return true;
		case R.id.action_to_top:
			if (mBinding.getEntriesAdapter() != null && mBinding.getEntriesAdapter().getItemCount() > 0) {
				mLayoutManager.scrollToPositionWithOffset(0, 0);
			}
			break;
		}
		return super.onOptionsItemSelected(item);
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


	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				menuItem.setChecked(true);
				mDrawerLayout.closeDrawers();
				return true;
			}
		});
	}
}
