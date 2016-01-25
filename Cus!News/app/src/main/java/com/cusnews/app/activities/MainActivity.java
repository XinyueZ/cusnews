package com.cusnews.app.activities;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chopping.bus.CloseDrawerEvent;
import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.app.SearchSuggestionProvider;
import com.cusnews.app.adapters.EntriesAdapter;
import com.cusnews.app.fragments.AboutDialogFragment;
import com.cusnews.app.fragments.AppListImpFragment;
import com.cusnews.app.fragments.BookmarksFragment;
import com.cusnews.bus.ChangeViewTypeEvent;
import com.cusnews.bus.CloseBookmarksEvent;
import com.cusnews.bus.EULAConfirmedEvent;
import com.cusnews.bus.EULARejectEvent;
import com.cusnews.bus.OpenEntryEvent;
import com.cusnews.bus.ShareEvent;
import com.cusnews.bus.ShareFBEvent;
import com.cusnews.databinding.ActivityMainBinding;
import com.cusnews.ds.Entries;
import com.cusnews.ds.Entry;
import com.cusnews.ds.TabLabel;
import com.cusnews.ds.Trends;
import com.cusnews.gcm.RegistrationIntentService;
import com.cusnews.gcm.UnregistrationIntentService;
import com.cusnews.utils.BookmarksManager;
import com.cusnews.utils.Prefs;
import com.cusnews.utils.TabLabelManager;
import com.cusnews.utils.TabLabelManager.TabLabelManagerUIHelper;
import com.cusnews.utils.Utils;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class MainActivity extends CusNewsActivity implements SearchView.OnQueryTextListener, TabLabelManagerUIHelper {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * The menu to this view.
	 */
	private static final int MENU   = R.menu.menu_main;
	/**
	 * Height of action-bar general.
	 */
	private int                        mActionBarHeight;
	/**
	 * The layout-mgr controls over {@link RecyclerView}.
	 */
	private RecyclerView.LayoutManager mLayoutManager;
	/**
	 * Data-binding.
	 */
	private ActivityMainBinding        mBinding;
	/**
	 * Page-pointer.
	 */
	private int     mStart      = 1;
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
	private   SearchView              mSearchView;
	/**
	 * Drawer
	 */
	private   DrawerLayout            mDrawerLayout;
	/**
	 * Different type: News: news, Search: web, Topics: topics.
	 */
	private String mSrc = "news";
	/**
	 * Search menu.
	 */
	private MenuItem mSearchMenu;

	/**
	 * Progress indicator.
	 */
	private ProgressDialog    mPb;
	/**
	 * Listener while registering push-feature.
	 */
	private BroadcastReceiver mRegistrationBroadcastReceiver;
	/**
	 * Column count of grid-mode.
	 */
	public static final int GRID_SPAN = 3;

	/**
	 * Display-name of user on Google.
	 */
	private TextView          mAccountTv;
	/**
	 * User-photo on Google.
	 */
	private ImageView         mThumbIv;
	/**
	 * Exit clickable view.
	 */
	private View              mExitV;
	/**
	 * {@code true} if the app starts customize topics.
	 */
	private boolean           mCustomizedTopicsSetting;
	//Begin [Bookmark-list]
	private SlidingPaneHelper mSlidingPaneHelper;
	private SlidingPaneLayout mBookmarkSpl;
	//End [Bookmark-list]

	/**
	 * Action bar helper for use on ICS and newer devices.
	 */
	private static class SlidingPaneHelper {
		ActionBar mActionBar;

		SlidingPaneHelper( ActionBar actionBar ) {
			mActionBar = actionBar;
		}

		public void init() {
			mActionBar.setDisplayHomeAsUpEnabled( true );
			mActionBar.setHomeButtonEnabled( true );
		}

		public void onPanelClosed() {
			mActionBar.setDisplayHomeAsUpEnabled( true );
			mActionBar.setHomeButtonEnabled( true );
			mActionBar.setTitle( R.string.application_name );
		}

		public void onPanelOpened() {
			mActionBar.setHomeButtonEnabled( true );
			mActionBar.setDisplayHomeAsUpEnabled( true );
			if( !mActionBar.isShowing() ) {
				mActionBar.show();
			}
		}

	}

	/**
	 * This panel slide listener updates the action bar accordingly for each panel state.
	 */
	private static class SliderListener extends SlidingPaneLayout.SimplePanelSlideListener {
		SlidingPaneHelper mSlidingPaneHelper;

		SliderListener( SlidingPaneHelper slidingPaneHelper ) {
			mSlidingPaneHelper = slidingPaneHelper;
		}

		@Override
		public void onPanelOpened( View panel ) {
			mSlidingPaneHelper.onPanelOpened();
		}

		@Override
		public void onPanelClosed( View panel ) {
			mSlidingPaneHelper.onPanelClosed();
		}
	}


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Prefs prefs = Prefs.getInstance();
		calcActionBarHeight();
		//Bookmark
		getSupportFragmentManager().beginTransaction().replace( R.id.bookmark_list_container_fl, BookmarksFragment.newInstance( this ) ).commit();

		//Listener on registering push-feature.
		mRegistrationBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive( Context context, Intent intent ) {
				if( !TextUtils.isEmpty( Prefs.getInstance().getPushToken() ) ) {
					dismissPb();
					if( mCustomizedTopicsSetting ) {
						CustomizedTopicsActivity.showInstance( MainActivity.this );
					} else {
						TopicListActivity.showInstance( MainActivity.this );
					}
				} else {
					//Register push-token not success and try-again.
					dismissPb();
					Snackbar.make( mBinding.coordinatorLayout, R.string.lbl_register_push_failed, Snackbar.LENGTH_LONG ).setAction(
							R.string.lbl_retry, new OnClickListener() {
								@Override
								public void onClick( View v ) {
									mPb = ProgressDialog.show( MainActivity.this, null, getString( R.string.lbl_registering ) );
									mPb.setCancelable( true );
									Intent intent = new Intent( MainActivity.this, RegistrationIntentService.class );
									startService( intent );
								}
							} ).show();
				}
			}
		};

		mKeyword = "";//App.Instance.getLastTimeSearched();

		//For search and suggestions.
		mSuggestions = new SearchRecentSuggestions( this, getString( R.string.suggestion_auth ), SearchSuggestionProvider.MODE );


		mBinding = DataBindingUtil.setContentView( this, LAYOUT );
		setUpErrorHandling( (ViewGroup) findViewById( R.id.error_content ) );
		View header = getLayoutInflater().inflate( R.layout.nav_header, mBinding.navView, false );
		mBinding.navView.addHeaderView( header );
		mThumbIv = (ImageView) header.findViewById( R.id.thumb_iv );
		ViewHelper.setAlpha( mThumbIv, 0f );
		ViewHelper.setScaleX( mThumbIv, 0f );
		ViewHelper.setScaleY( mThumbIv, 0f );
		mAccountTv = (TextView) header.findViewById( R.id.account_tv );
		mExitV = header.findViewById( R.id.exit_btn );
		mExitV.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				exitAccount();
			}
		} );
		ViewHelper.setAlpha( mExitV, 0f );
		ViewHelper.setScaleX( mExitV, 0f );
		ViewHelper.setScaleY( mExitV, 0f );

		//Init adapter.
		ViewType vt = Prefs.getInstance().getViewType();
		mBinding.setEntriesAdapter( new EntriesAdapter( vt ) );

		//Init recycleview.
		switch( vt ) {
			case GRID:
				mBinding.entriesRv.setLayoutManager( mLayoutManager = new GridLayoutManager( this, GRID_SPAN ) );
				setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
				break;
			default:
				mBinding.entriesRv.setLayoutManager( mLayoutManager = new LinearLayoutManager( MainActivity.this ) );
				//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
		}
		mBinding.entriesRv.addOnScrollListener( new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled( RecyclerView recyclerView, int dx, int dy ) {
				float y = ViewCompat.getY( recyclerView );
				if( y < dy ) {
					if( !mBinding.fab.isHidden() ) {
						mBinding.fab.hide();
					}
				} else {
					if( mBinding.fab.isHidden() ) {
						if( mBinding.del.isHidden() ) {
							mBinding.fab.show();
						}
					}
				}


				mVisibleItemCount = mLayoutManager.getChildCount();
				mTotalItemCount = mLayoutManager.getItemCount();
				if( mLayoutManager instanceof LinearLayoutManager ) {
					mPastVisibleItems = ( (LinearLayoutManager) mLayoutManager ).findFirstVisibleItemPosition();
				}

				if( !mIsBottom ) {
					if( mLoading ) {
						if( ( mVisibleItemCount + mPastVisibleItems ) >= mTotalItemCount ) {
							mLoading = false;
							mStart += 10;
							getData();
						}
					}
				}

			}

		} );

		//Init pull2load.
		mBinding.contentSrl.setColorSchemeResources( R.color.green_1, R.color.green_2, R.color.green_3, R.color.green_4 );
		mBinding.contentSrl.setProgressViewEndTarget( true, mActionBarHeight * 2 );
		mBinding.contentSrl.setProgressViewOffset( false, 0, mActionBarHeight * 2 );
		mBinding.contentSrl.setRefreshing( true );
		mBinding.contentSrl.setOnRefreshListener( new OnRefreshListener() {
			@Override
			public void onRefresh() {
				//				TabLabelManager.getInstance().init(MainActivity.this, false);
				getData();
			}
		} );

		//Init actionbar & navi-bar(drawer), bookmark-list slid
		setSupportActionBar( mBinding.toolbar );
		getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_menu );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );
		mSlidingPaneHelper = new SlidingPaneHelper( getSupportActionBar() );
		mBookmarkSpl = (SlidingPaneLayout) findViewById( R.id.sliding_pane_layout );
		mBookmarkSpl.setPanelSlideListener( new SliderListener( mSlidingPaneHelper ) );
		mSlidingPaneHelper.init();
		mDrawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
		setupDrawerContent( mBinding.navView );
		mDrawerLayout.setDrawerListener( new SimpleDrawerListener() {
			@Override
			public void onDrawerOpened( View drawerView ) {
				super.onDrawerOpened( drawerView );
				ViewPropertyAnimator.animate( mThumbIv ).cancel();
				ViewPropertyAnimator.animate( mThumbIv ).alpha( 1f ).scaleX( 1f ).scaleY( 1 ).setDuration( 500 ).start();
				ViewPropertyAnimator.animate( mExitV ).cancel();
				ViewPropertyAnimator.animate( mExitV ).alpha( 1f ).scaleX( 1f ).scaleY( 1 ).setDuration( 800 ).start();
			}
		} );


		//Init "fab", "del" for all tabs, save-button for labels.
		mBinding.addTabV.hide();
		mBinding.saveAddedTabBtn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				if( Utils.validateKeyword( mBinding.newTabLabelTv ) ) {
					if( !TextUtils.isEmpty( mBinding.newTabLabelTv.getText() ) ) {
						TabLabel newTabLabel = new TabLabel( mBinding.newTabLabelTv.getText().toString().trim(), Prefs.getInstance().getGoogleId() );
						TabLabelManager.getInstance().addNewRemoteTab( newTabLabel, MainActivity.this, mBinding.coordinatorLayout );
						mBinding.addTabV.hide();
						mBinding.newTabLabelTv.setText( "" );
						mBinding.addTabOpLl.setVisibility( View.GONE );
						mBinding.newTabLabelTv.setVisibility( View.GONE );
					}

					mBinding.fab.show();
					Utils.closeKeyboard( mBinding.newTabLabelTv );
				}
			}
		} );
		mBinding.closeAddTabBtn.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				mBinding.fab.show();
				mBinding.addTabV.hide();
				mBinding.newTabLabelTv.setText( "" );
				mBinding.addTabOpLl.setVisibility( View.GONE );
				mBinding.newTabLabelTv.setVisibility( View.GONE );
				Utils.closeKeyboard( mBinding.newTabLabelTv );
			}
		} );
		mBinding.fab.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				mBinding.fab.hide();
				mBinding.addTabV.show();
				mBinding.addTabOpLl.setVisibility( View.VISIBLE );
				mBinding.newTabLabelTv.setVisibility( View.VISIBLE );
			}
		} );
		mBinding.del.hide();
		if( Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB ) {
			mBinding.del.setOnDragListener( new OnDragListener() {
				@Override
				public boolean onDrag( View v, DragEvent event ) {
					Tab tab = (Tab) mBinding.del.getTag();
					switch( event.getAction() ) {
						case DragEvent.ACTION_DRAG_STARTED:
							break;
						case DragEvent.ACTION_DRAG_ENTERED:
							mBinding.del.setButtonColor( getResources().getColor( R.color.fab_material_blue_grey_900 ) );
							break;
						case DragEvent.ACTION_DRAG_EXITED:
							mBinding.del.setButtonColor( getResources().getColor( R.color.fab_material_blue_grey_500 ) );
							break;
						case DragEvent.ACTION_DROP:
							break;
						case DragEvent.ACTION_DRAG_ENDED:
							mBinding.del.setButtonColor( getResources().getColor( R.color.fab_material_blue_grey_500 ) );
							mBinding.del.hide();
							TabLabel tabLabel = new TabLabel();
							tabLabel.setObjectId( mLongPressedObjectId );
							TabLabelManager.getInstance().removeRemoteTab( tab, tabLabel, MainActivity.this, mBinding.coordinatorLayout );
						default:
							break;
					}
					return true;
				}
			} );
		}

		//Top-Trends.
		Api.getTopTrends( "", Prefs.getInstance().getLanguage(), App.Instance.getApiKey(), new Callback<Trends>() {
			@Override
			public void success( Trends trends, Response response ) {
				List<String> list          = trends.getList();
				MenuItem     trendsMenu    = mBinding.navView.getMenu().findItem( R.id.action_trends );
				SubMenu      trendsMenuSub = trendsMenu.getSubMenu();
				for( String trend : list ) {
					trendsMenuSub.add( trend ).setIcon( R.drawable.ic_social_whatshot ).setVisible( true ).setOnMenuItemClickListener(
							new OnMenuItemClickListener() {
								@Override
								public boolean onMenuItemClick( MenuItem item ) {
									mDrawerLayout.closeDrawers();

									if( !Prefs.getInstance().addTabTip() ) {
										showDialogFragment( new DialogFragment() {
											@Override
											public Dialog onCreateDialog( Bundle savedInstanceState ) {
												// Use the Builder class for convenient dialog construction
												AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
												builder.setTitle( R.string.application_name ).setMessage( R.string.lbl_tabs_tip ).setPositiveButton(
														R.string.btn_ok, null );
												return builder.create();
											}
										}, null );
										Prefs.getInstance().setAddTabTip( true );
									}


									addTrendToTab( item );
									return true;
								}

								private void addTrendToTab( MenuItem item ) {
									TabLabel newTabLabel;
									newTabLabel = new TabLabel( item.getTitle().toString().trim(), Prefs.getInstance().getGoogleId() );

									TabLabelManager.getInstance().addNewRemoteTab( newTabLabel, MainActivity.this, mBinding.coordinatorLayout );
								}
							} );
				}

				for( int i = 0, count = mBinding.navView.getChildCount(); i < count; i++ ) {
					final View child = mBinding.navView.getChildAt( i );
					if( child != null && child instanceof ListView ) {
						final ListView              menuView = (ListView) child;
						final HeaderViewListAdapter adapter  = (HeaderViewListAdapter) menuView.getAdapter();
						final BaseAdapter           wrapped  = (BaseAdapter) adapter.getWrappedAdapter();
						wrapped.notifyDataSetChanged();
						break;
					}
				}
			}

			@Override
			public void failure( RetrofitError error ) {

			}
		} );


		if( prefs.isEULAOnceConfirmed() && TextUtils.isEmpty( prefs.getGoogleId() ) ) {
			ConnectGoogleActivity.showInstance( this );
		} else if( prefs.isEULAOnceConfirmed() && !TextUtils.isEmpty( prefs.getGoogleId() ) ) {
			loadAllData();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance( this ).registerReceiver(
				mRegistrationBroadcastReceiver, new IntentFilter( RegistrationIntentService.REGISTRATION_COMPLETE ) );
	}

	@Override
	protected void onPause() {
		LocalBroadcastManager.getInstance( this ).unregisterReceiver( mRegistrationBroadcastReceiver );
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		dismissPb();
		super.onDestroy();
	}

	/**
	 * Calculate height of actionbar.
	 */
	protected void calcActionBarHeight() {
		int[] abSzAttr;
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = obtainStyledAttributes( abSzAttr );
		mActionBarHeight = a.getDimensionPixelSize( 0, -1 );
	}


	private int mVisibleItemCount;
	private int mPastVisibleItems;
	private int mTotalItemCount;
	private boolean mLoading = true;


	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.chopping.bus.CloseDrawerEvent }.
	 *
	 * @param e
	 * 		Event {@link com.chopping.bus.CloseDrawerEvent}.
	 */
	public void onEvent( CloseDrawerEvent e ) {
		mDrawerLayout.closeDrawer( Gravity.RIGHT );
	}

	/**
	 * Handler for {@link com.cusnews.bus.ChangeViewTypeEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.ChangeViewTypeEvent}.
	 */
	public void onEvent( ChangeViewTypeEvent e ) {
		switch( e.getViewType() ) {
			case GRID:
				mBinding.entriesRv.setLayoutManager( mLayoutManager = new GridLayoutManager( this, GRID_SPAN ) );
				setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
				break;
			default:
				mBinding.entriesRv.setLayoutManager( mLayoutManager = new LinearLayoutManager( MainActivity.this ) );
				setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
				break;
		}
		EntriesAdapter newAdp = new EntriesAdapter( e.getViewType(), mBinding.getEntriesAdapter().getData() );
		mBinding.setEntriesAdapter( newAdp );
	}


	/**
	 * Handler for {@link com.cusnews.bus.OpenEntryEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.OpenEntryEvent}.
	 */
	public void onEvent( OpenEntryEvent e ) {
		DetailActivity.showInstance( this, e.getEntry(), mKeyword );
	}


	/**
	 * Handler for {@link  EULARejectEvent}.
	 *
	 * @param e
	 * 		Event {@link  EULARejectEvent}.
	 */
	public void onEvent( EULARejectEvent e ) {
		ActivityCompat.finishAfterTransition( this );
	}

	/**
	 * Handler for {@link EULAConfirmedEvent}
	 *
	 * @param e
	 * 		Event {@link  EULAConfirmedEvent}.
	 */
	public void onEvent( EULAConfirmedEvent e ) {
		ConnectGoogleActivity.showInstance( this );
	}


	/**
	 * Handler for {@link com.cusnews.bus.CloseBookmarksEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.CloseBookmarksEvent}.
	 */
	public void onEvent( CloseBookmarksEvent e ) {
		if( mBookmarkSpl.isOpen() ) {
			mBookmarkSpl.closePane();
		}
	}


	/**
	 * Handler for {@link ShareEvent}.
	 *
	 * @param e
	 * 		Event {@link ShareEvent}.
	 */
	public void onEvent( final ShareEvent e ) {
		startActivity( e.getIntent() );
	}


	//------------------------------------------------
	/**
	 * Show single instance of {@link}
	 *
	 * @param cxt
	 * 		{@link Activity}.
	 */
	public static void showInstance( Activity cxt ) {
		Intent intent = new Intent( cxt, MainActivity.class );
		intent.setFlags( FLAG_ACTIVITY_SINGLE_TOP|FLAG_ACTIVITY_CLEAR_TOP );
		ActivityCompat.startActivity( cxt, intent, null );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
		switch( requestCode ) {
			case ConnectGoogleActivity.REQ:
				if( resultCode == RESULT_OK ) {
					loadAllData();
				} else {
					ActivityCompat.finishAffinity( this );
				}
		}
		super.onActivityResult( requestCode, resultCode, data );
	}

	/**
	 * Get all data inc. feeds, labels, user's profile etc.
	 */
	private void loadAllData() {
		Prefs prefs = Prefs.getInstance();
		if( !Prefs.getInstance().askedPush() ) {
			askPush();
		}
		getData();
		Picasso picasso = Picasso.with( App.Instance );
		if( !TextUtils.isEmpty( prefs.getGoogleThumbUrl() ) ) {
			picasso.load( com.chopping.utils.Utils.uriStr2URI( prefs.getGoogleThumbUrl() ).toASCIIString() ).into( mThumbIv );
		}
		mAccountTv.setText( prefs.getGoogleDisplyName() );

		//Init tabs.
		mBinding.tabs.setTabMode( TabLayout.MODE_SCROLLABLE );
		mBinding.tabs.setOnTabSelectedListener( mOnTabSelectedListener );
		if( mBinding.tabs.getTabCount() <= 1 ) {
			mBinding.tabs.setVisibility( View.GONE );
		}
		TabLabelManager.getInstance().init( this, mBinding.tabs.getTabCount() <= 0 );
		BookmarksManager.getInstance().init();
	}

	/**
	 * Index of current selected tab.
	 */
	private int mSelectedIndex;
	/**
	 * Handling {@link Tab} selections.
	 */
	private OnTabSelectedListener mOnTabSelectedListener = new OnTabSelectedListener() {
		private void handleSelectionTab( Tab tab ) {
			if( tab.getPosition() == 0 ) {
				mKeyword = tab.getTag() == null || ( mSearchMenu != null && !MenuItemCompat.isActionViewExpanded( mSearchMenu ) ) ? "" :
						   tab.getTag().toString();
			} else {
				mKeyword = tab.getText().toString();
			}
			clear();
			getData();
			if( !mBinding.del.isHidden() ) {
				mBinding.del.hide();
			}
			mSelectedIndex = tab.getPosition();
		}

		@Override
		public void onTabSelected( Tab tab ) {
			handleSelectionTab( tab );
		}


		@Override
		public void onTabReselected( Tab tab ) {
			handleSelectionTab( tab );
		}

		@Override
		public void onTabUnselected( Tab tab ) {
		}


	};

	/**
	 * Add customized , default, first {@link Tab}.
	 */
	@Override
	public void addDefaultTab() {
		mBinding.tabs.addTab( mBinding.tabs.newTab().setIcon( R.drawable.ic_default ) );
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
	 *
	 * @return The added new {@link Tab}.
	 */
	@Override
	public Tab addTab( TabLabel tabLabel ) {
		View      tabV  = getLayoutInflater().inflate( R.layout.tab, null, false );
		TextView  tabTv = (TextView) tabV.findViewById( R.id.text );
		final Tab tab   = mBinding.tabs.newTab();
		tab.setText( tabLabel.getLabel() );
		tab.setCustomView( tabV );
		tabTv.setText( tabLabel.getLabel() );
		tabV.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				tab.select();
			}
		} );
		tabV.setTag( tabLabel );
		tabV.setOnLongClickListener( new OnLongClickListener() {
			@Override
			public boolean onLongClick( View v ) {
				final TabLabel tabLabel = (TabLabel) v.getTag();
				mLongPressedObjectId = tabLabel.getObjectId();
				if( android.os.Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB ) {
					//After API-11 we import drag-drop features to delete tabs.
					mBinding.del.show();
					mBinding.del.setTag( tab );
					if( !mBinding.fab.isHidden() ) {
						mBinding.fab.hide();
					}

					TextView          tabTv         = (TextView) v.findViewById( R.id.text );
					String            text          = tabTv.getText().toString();
					ClipData          data          = ClipData.newPlainText( "bmob_id", text );
					DragShadowBuilder shadowBuilder = new View.DragShadowBuilder( v );
					v.startDrag( data, shadowBuilder, v, 0 );
				} else {
					//Pre API-11, do it with dialog.
					showDialogFragment( new DialogFragment() {
						@Override
						public Dialog onCreateDialog( Bundle savedInstanceState ) {
							// Use the Builder class for convenient dialog construction
							AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
							builder.setTitle( R.string.application_name ).setMessage( getString( R.string.lbl_remove_tab, tabLabel.getLabel() ) )
									.setPositiveButton( R.string.btn_yes, new DialogInterface.OnClickListener() {
										@Override
										public void onClick( DialogInterface dialog, int which ) {
											TabLabel tabLabel = new TabLabel();
											tabLabel.setObjectId( mLongPressedObjectId );
											TabLabelManager.getInstance().removeRemoteTab( tab, tabLabel, MainActivity.this,
																						   mBinding.coordinatorLayout
											);
										}
									} );
							return builder.create();
						}
					}, null );

				}
				return true;
			}
		} );
		if( mBinding.tabs.getTabCount() > 0 ) {
			mBinding.tabs.addTab( tab, 1 );
		} else {
			mBinding.tabs.addTab( tab );
		}
		if( mBinding.tabs.getTabCount() > 1 ) {
			mBinding.tabs.setVisibility( View.VISIBLE );
		}
		return tab;
	}

	/**
	 * Remove a {@link Tab} from {@link Tab}s.
	 *
	 * @param tab
	 * 		{@link Tab}
	 */
	@Override
	public void removeTab( Tab tab ) {
		mBinding.tabs.removeTab( tab );
		mBinding.del.hide();
		if( mBinding.tabs.getTabCount() < 2 ) {
			mBinding.tabs.setVisibility( View.GONE );
		}
	}

	@Override
	public void onBackPressed() {
		if( mBookmarkSpl.isOpen() ) {
			mBookmarkSpl.closePane();
		} else if( !mBinding.del.isHidden() ) {
			mBinding.del.hide();
		} else {
			if( mDrawerLayout.isDrawerOpen( Gravity.RIGHT ) || mDrawerLayout.isDrawerOpen( Gravity.LEFT ) ) {
				mDrawerLayout.closeDrawers();
			} else {
				super.onBackPressed();
			}
		}
	}


	/**
	 * Get data from server.
	 */
	private void getData() {
		if( !TextUtils.isEmpty( Prefs.getInstance().getGoogleId() ) ) {
			if( !mInProgress ) {
				mBinding.contentSrl.setRefreshing( true );
				mInProgress = true;
				Api.getEntries( mKeyword, mStart, Prefs.getInstance().getLanguage(), mSrc, App.Instance.getApiKey(), new Callback<Entries>() {
					@Override
					public void success( Entries entries, Response response ) {

						mBinding.getEntriesAdapter().getData().addAll( entries.getList() );
						mBinding.getEntriesAdapter().notifyDataSetChanged();
						//Finish loading
						mBinding.contentSrl.setRefreshing( false );
						mInProgress = false;
						mLoading = true;

						//Arrive bottom?
						if( entries.getStart() > entries.getCount() ) {
							if( TextUtils.equals( mSrc, "web" ) ) {
								mIsBottom = true;
								Snackbar.make( mBinding.coordinatorLayout, R.string.lbl_no_data, Snackbar.LENGTH_LONG ).show();
								//For next
								mSrc = "news";
							} else {
								mIsBottom = false;
								mSrc = "web";
								Snackbar.make( mBinding.coordinatorLayout, R.string.lbl_search_more, Snackbar.LENGTH_LONG ).show();
								mStart = 1;
								getData();
							}
						}
					}

					@Override
					public void failure( RetrofitError error ) {
						if( mStart > 10 ) {
							mStart -= 10;
						}
						Snackbar.make( mBinding.coordinatorLayout, R.string.lbl_loading_error, Snackbar.LENGTH_LONG ).setAction(
								R.string.lbl_retry, new OnClickListener() {
									@Override
									public void onClick( View v ) {
										getData();
									}
								} ).show();

						//Finish loading
						mBinding.contentSrl.setRefreshing( false );
						mInProgress = false;
						mLoading = true;
					}
				} );
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( MENU, menu );

		//Search
		mSearchMenu = menu.findItem( R.id.action_search );
		MenuItemCompat.setOnActionExpandListener( mSearchMenu, new MenuItemCompat.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand( MenuItem item ) {
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse( MenuItem item ) {
				if( mSelectedIndex == 0 ) {
					clear();
					mKeyword = "";
				}
				return true;
			}
		} );
		mSearchView = (SearchView) MenuItemCompat.getActionView( mSearchMenu );
		mSearchView.setOnQueryTextListener( this );
		mSearchView.setIconifiedByDefault( true );
		SearchManager searchManager = (SearchManager) getSystemService( SEARCH_SERVICE );
		if( searchManager != null ) {
			SearchableInfo info = searchManager.getSearchableInfo( getComponentName() );
			mSearchView.setSearchableInfo( info );
		}

		if( !Prefs.getInstance().showAllImages() ) {
			menu.findItem( R.id.action_view_type ).setVisible( false );
		}


		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onPrepareOptionsMenu( Menu menu ) {

		MenuItem menuShare = menu.findItem( R.id.action_share );
		android.support.v7.widget.ShareActionProvider provider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(
				menuShare );

		String subject = getString( R.string.lbl_share_app_title );
		String text    = getString(
				R.string.lbl_share_app_content, getString( R.string.application_name ), Prefs.getInstance().getAppDownloadInfo() );

		provider.setShareIntent( com.chopping.utils.Utils.getDefaultShareIntent( provider, subject, text ) );

		return super.onPrepareOptionsMenu( menu );
	}

	/**
	 * Start searching.
	 */
	private void doSearch() {
		Tab tab = mBinding.tabs.getTabAt( 0 );
		tab.setTag( mKeyword );
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
	public boolean onOptionsItemSelected( MenuItem item ) {
		if( item.getItemId() == android.R.id.home && mBookmarkSpl.isOpen() ) {
			mBookmarkSpl.closePane();
			return true;
		}
		switch( item.getItemId() ) {
			case android.R.id.home:
				mDrawerLayout.openDrawer( GravityCompat.START );
				return true;
			case R.id.action_to_top:
				if( mBinding.getEntriesAdapter() != null && mBinding.getEntriesAdapter().getItemCount() > 0 ) {
					if( mLayoutManager instanceof LinearLayoutManager ) {
						( (LinearLayoutManager) mLayoutManager ).scrollToPositionWithOffset( 0, 0 );
					}
				}
				break;
			case R.id.action_about:
				showDialogFragment( AboutDialogFragment.newInstance( this ), null );
				break;
		}
		return super.onOptionsItemSelected( item );
	}


	@Override
	protected void onNewIntent( Intent intent ) {
		super.onNewIntent( intent );
		setIntent( intent );
		handleIntent( intent );
	}

	/**
	 * Getting intent var.
	 *
	 * @param intent
	 */
	protected void handleIntent( Intent intent ) {
		mKeyword = intent.getStringExtra( SearchManager.QUERY );
		if( !TextUtils.isEmpty( mKeyword ) && Utils.validateStr( App.Instance, mKeyword ) ) {
			mKeyword = mKeyword.trim();
			mSearchView.setQueryHint( Html.fromHtml( "<font color = #ffffff>" + mKeyword + "</font>" ) );

			mKeyword = intent.getStringExtra( SearchManager.QUERY );
			mKeyword = mKeyword.trim();
			resetSearchView();

			mSuggestions.saveRecentQuery( mKeyword, null );

			//Do search
			doSearch();
		}
	}


	@Override
	public boolean onQueryTextChange( String newText ) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit( String s ) {
		InputMethodManager mgr = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE );
		mgr.hideSoftInputFromWindow( mSearchView.getWindowToken(), 0 );
		resetSearchView();
		return false;
	}


	/**
	 * Reset the UI status of searchview.
	 */
	protected void resetSearchView() {
		if( mSearchView != null ) {
			mSearchView.clearFocus();
		}
	}

	/**
	 * Set-up of navi-bar left.
	 */
	private void setupDrawerContent( NavigationView navigationView ) {
		navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected( MenuItem menuItem ) {
				mDrawerLayout.closeDrawer( Gravity.LEFT );

				switch( menuItem.getItemId() ) {
					case R.id.action_faroo_home:
						WebViewActivity.showInstance( MainActivity.this, getString( R.string.action_visit_faroo ),
													  Prefs.getInstance().getFarooBlog()
						);
						break;
					case R.id.action_setting:
						SettingActivity.showInstance( MainActivity.this );
						break;
					case R.id.action_more_apps:
						mDrawerLayout.openDrawer( Gravity.RIGHT );
						break;
					case R.id.action_bookmarks:
						mBookmarkSpl.openPane();
						break;
					case R.id.action_push:
						if( TextUtils.isEmpty( Prefs.getInstance().getPushToken() ) ) {
							askPush();
						} else {
							showDialogFragment( new DialogFragment() {
								@Override
								public Dialog onCreateDialog( Bundle savedInstanceState ) {
									// Use the Builder class for convenient dialog construction
									AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
									builder.setTitle( R.string.action_push ).setSingleChoiceItems( R.array.setting_pushs, -1,
																								   new DialogInterface.OnClickListener() {
																									   @Override
																									   public void onClick( DialogInterface dialog,
																															int which
																									   ) {
																										   switch( which ) {
																											   case 0:
																												   TopicListActivity.showInstance(
																														   MainActivity.this );
																												   break;
																											   case 1:
																												   CustomizedTopicsActivity
																														   .showInstance(
																																   MainActivity.this );
																												   break;
																										   }
																										   //												dialog.dismiss();
																									   }
																								   }
									).setNegativeButton( R.string.btn_ok, null );
									return builder.create();
								}
							}, null );
						}
						break;
				}
				return true;
			}
		} );
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
		getSupportFragmentManager().beginTransaction().replace( R.id.app_list_fl, AppListImpFragment.newInstance( this ) ).commit();
	}

	/**
	 * To ask whether opening Push-feature.
	 */
	private void askPush() {
		Prefs.getInstance().setAskedPush( true );
		//TODO Need using fragment, but I do not know why it does not work.
		//		showDialogFragment(new DialogFragment() {
		//			@Override
		//			public Dialog onCreateDialog(Bundle savedInstanceState) {
		//				// Use the Builder class for convenient dialog construction
		//				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//				builder.setTitle(R.string.application_name).setMessage(R.string.lbl_ask_push).setNegativeButton(
		//						R.string.btn_no, null).setPositiveButton(R.string.btn_yes,
		//						new DialogInterface.OnClickListener() {
		//							@Override
		//							public void onClick(DialogInterface dialog, int which) {
		//								mPb = ProgressDialog.show(MainActivity.this, null, getString(R.string.lbl_registering));
		//								mPb.setCancelable(true);
		//								Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
		//								startService(intent);
		//							}
		//						});
		//				return builder.create();
		//			}
		//		}, null);

		AlertDialog.Builder builder = new AlertDialog.Builder( this );
		builder.setTitle( R.string.application_name ).setMessage( R.string.lbl_ask_push ).setNeutralButton( R.string.btn_diy,
																											new DialogInterface.OnClickListener() {
																												@Override
																												public void onClick(
																														DialogInterface dialog,
																														int which
																												) {
																													dismissPb();
																													mPb = ProgressDialog.show(
																															MainActivity.this, null,
																															getString(
																																	R.string.lbl_registering )
																													);
																													mPb.setCancelable( true );
																													Intent intent = new Intent(
																															MainActivity.this,
																															RegistrationIntentService.class
																													);
																													startService( intent );
																													mCustomizedTopicsSetting = true;
																												}
																											}
		).setNegativeButton( R.string.btn_no, null ).setPositiveButton( R.string.btn_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int which ) {
				dismissPb();
				mPb = ProgressDialog.show( MainActivity.this, null, getString( R.string.lbl_registering ) );
				mPb.setCancelable( true );
				Intent intent = new Intent( MainActivity.this, RegistrationIntentService.class );
				startService( intent );
				mCustomizedTopicsSetting = false;
			}
		} ).create().show();
	}

	/**
	 * Remove the progress indicator.
	 */
	private void dismissPb() {
		if( mPb != null && mPb.isShowing() ) {
			mPb.dismiss();
		}
	}

	/**
	 * Exit current account, here unregister all push-elements etc.
	 */
	public void exitAccount() {
		mDrawerLayout.closeDrawers();
		Prefs prefs = Prefs.getInstance();
		if( !TextUtils.isEmpty( prefs.getPushToken() ) ) {
			Intent intent = new Intent( this, UnregistrationIntentService.class );
			startService( intent );
		}
		prefs.setAskedPush( false );
		prefs.setGoogleId( null );
		prefs.setGoogleThumbUrl( null );
		prefs.setGoogleDisplyName( null );
		ConnectGoogleActivity.showInstance( this );

		mBinding.tabs.removeAllTabs();
		TabLabelManager.getInstance().clean();
		BookmarksManager.getInstance().clean();
	}

}