package com.cusnews.app.activities;

import java.security.NoSuchAlgorithmException;

import android.app.Dialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.app.SearchSuggestionProvider;
import com.cusnews.app.adapters.EntriesAdapter;
import com.cusnews.app.fragments.AboutDialogFragment;
import com.cusnews.app.fragments.AppListImpFragment;
import com.cusnews.bus.ChangeViewTypeEvent;
import com.cusnews.bus.EULAConfirmedEvent;
import com.cusnews.bus.EULARejectEvent;
import com.cusnews.bus.OpenEntryEvent;
import com.cusnews.bus.ShareEvent;
import com.cusnews.databinding.ActivityMainBinding;
import com.cusnews.ds.Entries;
import com.cusnews.ds.TabLabel;
import com.cusnews.utils.DeviceUniqueUtil;
import com.cusnews.utils.Prefs;
import com.cusnews.utils.TabLabelManager;
import com.cusnews.utils.TabLabelManager.TabLabelManagerHelper;
import com.cusnews.utils.Utils;
import com.cusnews.widgets.DynamicShareActionProvider;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends CusNewsActivity implements SearchView.OnQueryTextListener, TabLabelManagerHelper {
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
	 * Search menu.
	 */
	private MenuItem mSearchMenu;

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




	/**
	 * Handler for {@link  EULARejectEvent}.
	 *
	 * @param e
	 * 		Event {@link  EULARejectEvent}.
	 */
	public void onEvent(EULARejectEvent e) {
		ActivityCompat.finishAfterTransition(this);
	}

	/**
	 * Handler for {@link EULAConfirmedEvent}
	 *
	 * @param e
	 * 		Event {@link  EULAConfirmedEvent}.
	 */
	public void onEvent(EULAConfirmedEvent e) {

	}
	//------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		calcActionBarHeight();

		mKeyword = "";//App.Instance.getLastTimeSearched();

		//For search and suggestions.
		mSuggestions = new SearchRecentSuggestions(this, getString(R.string.suggestion_auth),
				SearchSuggestionProvider.MODE);


		mBinding = DataBindingUtil.setContentView(this, LAYOUT);
		setUpErrorHandling((ViewGroup) findViewById(R.id.error_content));

		//Init adapter.
		mBinding.setEntriesAdapter(new EntriesAdapter(Prefs.getInstance().getViewType().getLayoutResId()));

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
						if (mBinding.del.isHidden()) {
							mBinding.fab.show();
						}
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

		//Init pull2load.
		mBinding.contentSrl.setColorSchemeResources(R.color.green_1, R.color.green_2, R.color.green_3, R.color.green_4);
		mBinding.contentSrl.setProgressViewEndTarget(true, mActionBarHeight * 2);
		mBinding.contentSrl.setProgressViewOffset(false, 0, mActionBarHeight * 2);
		mBinding.contentSrl.setRefreshing(true);
		mBinding.contentSrl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				TabLabelManager.getInstance().init(MainActivity.this, false);
				getData();
			}
		});

		//Init actionbar & navi-bar(drawer).
		setSupportActionBar(mBinding.toolbar);
		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		setupDrawerContent(mBinding.navView);

		getData();

		//Init tabs.
		mBinding.tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
		mBinding.tabs.setOnTabSelectedListener(mOnTabSelectedListener);
		TabLabelManager.getInstance().init(this, true);
		if(mBinding.tabs.getTabCount() == 1) {
			mBinding.tabs.setVisibility(View.GONE);
		}

		//Init "fab", "del" for all tabs, save-button for labels.
		mBinding.addTabV.hide();
		mBinding.saveAddedTabBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (!TextUtils.isEmpty(mBinding.newTabLabelTv.getText())) {
						TabLabel newTabLabel = new TabLabel(mBinding.newTabLabelTv.getText().toString().trim(),
								DeviceUniqueUtil.getDeviceIdent(App.Instance));
						TabLabelManager.getInstance().addNewRemoteTab(newTabLabel, MainActivity.this,
								mBinding.coordinatorLayout);
						mBinding.addTabV.hide();
					}
				} catch (NoSuchAlgorithmException e) {
					//TODO Error when can not get device id.
				}
				Utils.closeKeyboard(mBinding.newTabLabelTv);
			}
		});
		mBinding.closeAddTabBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBinding.addTabV.hide();
				mBinding.fab.show();
				mBinding.addTabOpLl.setVisibility(View.GONE);
				mBinding.newTabLabelTv.setVisibility(View.GONE);
				Utils.closeKeyboard(mBinding.newTabLabelTv);
			}
		});
		mBinding.fab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBinding.addTabV.show();
				mBinding.fab.hide();
				mBinding.addTabOpLl.setVisibility(View.VISIBLE);
				mBinding.newTabLabelTv.setText("");
				mBinding.newTabLabelTv.setVisibility(View.VISIBLE);
			}
		});
		mBinding.del.hide();
		if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			mBinding.del.setOnDragListener(new OnDragListener() {
				@Override
				public boolean onDrag(View v, DragEvent event) {
					Tab tab = (Tab) mBinding.del.getTag();
					switch (event.getAction()) {
					case DragEvent.ACTION_DRAG_STARTED:
						break;
					case DragEvent.ACTION_DRAG_ENTERED:
						mBinding.del.setButtonColor(getResources().getColor(R.color.fab_material_blue_grey_900));
						break;
					case DragEvent.ACTION_DRAG_EXITED:
						mBinding.del.setButtonColor(getResources().getColor(R.color.fab_material_blue_grey_500));
						break;
					case DragEvent.ACTION_DROP:
						break;
					case DragEvent.ACTION_DRAG_ENDED:
						mBinding.del.setButtonColor(getResources().getColor(R.color.fab_material_blue_grey_500));
						mBinding.del.hide();
						TabLabel tabLabel = new TabLabel();
						tabLabel.setObjectId(mLongPressedObjectId);
						TabLabelManager.getInstance().removeRemoteTab(tab, tabLabel, MainActivity.this,
								mBinding.coordinatorLayout);
					default:
						break;
					}
					return true;
				}
			});
		}
	}


	/**
	 * Handling {@link Tab} selections.
	 */
	private OnTabSelectedListener mOnTabSelectedListener = new OnTabSelectedListener() {
		private void handleSelectionTab(Tab tab) {
			if (tab.getPosition() == 0) {
				mKeyword = tab.getTag() == null || (mSearchMenu != null && !MenuItemCompat.isActionViewExpanded(
						mSearchMenu)) ? "" : tab.getTag().toString();
			} else {
				mKeyword = tab.getText().toString();
			}
			clear();
			getData();
			if (!mBinding.del.isHidden()) {
				mBinding.del.hide();
			}
		}

		@Override
		public void onTabSelected(Tab tab) {
			handleSelectionTab(tab);
		}


		@Override
		public void onTabReselected(Tab tab) {
			handleSelectionTab(tab);
		}

		@Override
		public void onTabUnselected(Tab tab) {
		}


	};

	/**
	 * Add customized , default, first {@link Tab}.
	 */
	@Override
	public void addDefaultTab() {
		mBinding.tabs.addTab(mBinding.tabs.newTab().setIcon(R.drawable.ic_default));
	}

	/**
	 * The object-id of a selected {@link TabLabel} of a {@link Tab}.
	 */
	private String mLongPressedObjectId;

	/**
	 * Add customized  {@link Tab}.
	 *
	 * @param tabLabel
	 * 		{@link TabLabel}.
	 */
	@Override
	public void addTab(final TabLabel tabLabel) {
		final Tab tab = mBinding.tabs.newTab();
		tab.setText(tabLabel.getLabel());
		View tabV = getLayoutInflater().inflate(R.layout.tab, null, false);
		tab.setCustomView(tabV);
		TextView tabTv = (TextView) tabV.findViewById(R.id.text);
		tabTv.setText(tabLabel.getLabel());
		tabV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tab.select();
			}
		});
		tabV.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				mLongPressedObjectId = tabLabel.getObjectId();
				if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
					//After API-11 we import drag-drop features to delete tabs.
					mBinding.del.show();
					mBinding.del.setTag(tab);
					if (!mBinding.fab.isHidden()) {
						mBinding.fab.hide();
					}

					TextView tabTv = (TextView) v.findViewById(R.id.text);
					String text = tabTv.getText().toString();
					ClipData data = ClipData.newPlainText("bmob_id", text);
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
					v.startDrag(data, shadowBuilder, v, 0);
				} else {
					//Pre API-11, do it with dialog.
					showDialogFragment(new DialogFragment() {
						@Override
						public Dialog onCreateDialog(Bundle savedInstanceState) {
							// Use the Builder class for convenient dialog construction
							AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
							builder.setTitle(R.string.application_name).setMessage(getString(R.string.lbl_remove_tab,
									tabLabel.getLabel())).setPositiveButton(R.string.btn_yes,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											TabLabel tabLabel = new TabLabel();
											tabLabel.setObjectId(mLongPressedObjectId);
											TabLabelManager.getInstance().removeRemoteTab(tab, tabLabel,
													MainActivity.this, mBinding.coordinatorLayout);
										}
									});
							return builder.create();
						}
					}, null);

				}
				return true;
			}
		});
		mBinding.tabs.addTab(tab);
		if(mBinding.tabs.getTabCount() > 1) {
			mBinding.tabs.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Remove a {@link Tab} from {@link Tab}s.
	 *
	 * @param tab
	 * 		{@link Tab}
	 */
	@Override
	public void removeTab(Tab tab) {
		mBinding.tabs.removeTab(tab);
		mBinding.del.hide();
		if (mBinding.tabs.getTabCount() < 2) {
			mBinding.tabs.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		if (!mBinding.del.isHidden()) {
			mBinding.del.hide();
		} else {
			super.onBackPressed();
		}
	}


	/**
	 * Get data from server.
	 */
	private void getData() {
		if (!mInProgress) {
			mBinding.contentSrl.setRefreshing(true);
			mInProgress = true;
			Api.getEntries(mKeyword, mStart, Prefs.getInstance().getLanguage(), mSrc, App.Instance.getApiKey(),
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
									Snackbar.make(mBinding.coordinatorLayout, R.string.lbl_no_data,
											Snackbar.LENGTH_LONG).show();
									//For next
									mSrc = "news";
								} else {
									mIsBottom = false;
									mSrc = "web";
									Snackbar.make(mBinding.coordinatorLayout, R.string.lbl_search_more,
											Snackbar.LENGTH_LONG).show();
									mStart = 1;
									getData();
								}
							}
						}

						@Override
						public void failure(RetrofitError error) {
							if (mStart > 10) {
								mStart -= 10;
							}
							Snackbar.make(mBinding.coordinatorLayout, R.string.lbl_loading_error, Snackbar.LENGTH_LONG)
									.setAction(R.string.lbl_retry, new OnClickListener() {
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
		mSearchMenu = menu.findItem(R.id.action_search);
		MenuItemCompat.setOnActionExpandListener(mSearchMenu, new MenuItemCompat.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return true;
			}
		});
		mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenu);
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
						Prefs.getInstance().getAppDownloadInfo());
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
		Tab tab = mBinding.tabs.getTabAt(0);
		tab.setTag(mKeyword);
		tab.select();
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
		case R.id.action_about:
			showDialogFragment(AboutDialogFragment.newInstance(this), null);
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
		if (!TextUtils.isEmpty(mKeyword)) {
			mKeyword = mKeyword.trim();
		}
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

	/**
	 * Set-up of navi-bar left.
	 */
	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(MenuItem menuItem) {
				menuItem.setChecked(true);
				mDrawerLayout.closeDrawers();

				switch (menuItem.getItemId()) {
				case R.id.action_faroo_home:
					WebViewActivity.showInstance(MainActivity.this, "Faroo", Prefs.getInstance().getFarooBlog());
					break;
				}
				return true;
			}
		});
	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		showAppList();
	}

	@Override
	protected void onAppConfigIgnored() {
		super.onAppConfigIgnored();
		showAppList();
	}

	/**
	 * Show all external applications links.
	 */
	private void showAppList() {
		getSupportFragmentManager().beginTransaction().replace(R.id.app_list_fl, AppListImpFragment.newInstance(this))
				.commit();
	}

	/**
	 * Open Faroo's home-page.
	 * @param view No usage.
	 */
	public void openFarooHome(View view) {
		WebViewActivity.showInstance(MainActivity.this, "Faroo", Prefs.getInstance().getFarooHome());
	}
}