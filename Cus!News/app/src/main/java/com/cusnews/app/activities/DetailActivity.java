package com.cusnews.app.activities;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.app.adapters.DetailPagerAdapter;
import com.cusnews.bus.BookmarksInitEvent;
import com.cusnews.bus.DetailScrollDownEvent;
import com.cusnews.bus.DetailScrollUpEvent;
import com.cusnews.bus.OpenRelatedEvent;
import com.cusnews.databinding.ActivityDetailBinding;
import com.cusnews.ds.Bookmark;
import com.cusnews.ds.Entry;
import com.cusnews.utils.BookmarksManager;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.software.shell.fab.ActionButton;

/**
 * Detail news. Contains a general detail view and a {@link android.webkit.WebView}.
 *
 * @author Xinyue Zhao
 */
public final class DetailActivity extends CusNewsActivity {
	public static final  String EXTRAS_QUERY = DetailActivity.class.getName() + ".EXTRAS.query";
	public static final  String EXTRAS_ENTRY = DetailActivity.class.getName() + ".EXTRAS.entry";
	/**
	 * Main layout for this component.
	 */
	private static final int    LAYOUT       = R.layout.activity_detail;


	/**
	 * The interstitial ad.
	 */
	private InterstitialAd mInterstitialAd;
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Handler for {@link com.cusnews.bus.OpenRelatedEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.OpenRelatedEvent}.
	 */
	public void onEvent( OpenRelatedEvent e ) {
		DetailActivity.showInstance( this, e.getEntry(), e.getKeyword() );
	}

	/**
	 * Handler for {@link com.cusnews.bus.DetailScrollDownEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.DetailScrollDownEvent}.
	 */
	public void onEvent( DetailScrollDownEvent e ) {
		if( !mBinding.bookmarkBtn.isHidden() ) {
			mBinding.bookmarkBtn.hide();
		}
	}

	/**
	 * Handler for {@link com.cusnews.bus.DetailScrollUpEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.DetailScrollUpEvent}.
	 */
	public void onEvent( DetailScrollUpEvent e ) {
		if( mBinding.bookmarkBtn.isHidden() ) {
			mBinding.bookmarkBtn.show();
		}
	}

	/**
	 * Handler for {@link com.cusnews.bus.BookmarksInitEvent}.
	 *
	 * @param e
	 * 		Event {@link com.cusnews.bus.BookmarksInitEvent}.
	 */
	public void onEvent( BookmarksInitEvent e ) {
		mBinding.bookmarkBtn.show();
	}
	//------------------------------------------------

	/**
	 * Data-binding.
	 */
	private ActivityDetailBinding mBinding;

	/**
	 * Show single instance of {@link DetailActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 * @param entry
	 * 		A news {@link Entry}.
	 * @param query
	 * 		The query to the {@code entry}.
	 */
	public static void showInstance( Context cxt, Entry entry, @Nullable String query ) {
		Intent intent = new Intent( cxt, DetailActivity.class );
		intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP );
		intent.putExtra( EXTRAS_ENTRY, (Serializable) entry );
		intent.putExtra( EXTRAS_QUERY, query == null ? "" : query );
		cxt.startActivity( intent );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		if( TextUtils.isEmpty( Prefs.getInstance().getGoogleId() ) ) {
			//Not login, must do it again.
			ConnectGoogleActivity.showInstance( this );
			ActivityCompat.finishAfterTransition( this );
		} else {
			ViewType vt = Prefs.getInstance().getViewType();
			switch( vt ) {
				case GRID:
					setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
					break;
				default:
					//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					break;
			}
			mBinding = DataBindingUtil.setContentView( this, LAYOUT );
			setUpErrorHandling( (ViewGroup) findViewById( R.id.error_content ) );
			setData();


			int curTime  = App.Instance.getAdsShownTimes();
			int adsTimes = 7;
			if( curTime % adsTimes == 0 ) {
				// Create an ad.
				mInterstitialAd = new InterstitialAd( this );
				mInterstitialAd.setAdUnitId( getString( R.string.interstitial_ad_unit_id ) );
				// Create ad request.
				AdRequest adRequest = new AdRequest.Builder().build();
				// Begin loading your interstitial.
				mInterstitialAd.setAdListener( new AdListener() {
					@Override
					public void onAdLoaded() {
						super.onAdLoaded();
						displayInterstitial();
					}
				} );
				mInterstitialAd.loadAd( adRequest );
			}
			curTime++;
			App.Instance.setAdsShownTimes( curTime );

			//Bookmark-btn
			if( BookmarksManager.getInstance().isInit() ) {
				mBinding.bookmarkBtn.show();
			}
			mBinding.bookmarkBtn.setOnClickListener( new OnClickListener() {
				@Override
				public void onClick( View v ) {
					Entry    entry    = (Entry) getIntent().getSerializableExtra( EXTRAS_ENTRY );
					Bookmark bookmark = BookmarksManager.getInstance().findBookmarked( entry );
					if( bookmark != null ) {
						Bookmark delBookmark = new Bookmark( entry );
						delBookmark.setObjectId( bookmark.getObjectId() );
						BookmarksManager.getInstance().removeRemoteBookmark( delBookmark, (ActionButton) v, findViewById( R.id.error_content ) );
					} else {
						BookmarksManager.getInstance().addNewRemoteBookmark(
								new Bookmark( Prefs.getInstance().getGoogleId(), entry ), (ActionButton) v, findViewById( R.id.error_content ) );
					}
				}
			} );
			Entry entry = (Entry) getIntent().getSerializableExtra( EXTRAS_ENTRY );
			if( BookmarksManager.getInstance().isBookmarked( entry ) ) {
				mBinding.bookmarkBtn.setImageResource( R.drawable.ic_bookmarked );
			}
		}
	}

	/**
	 * Invoke displayInterstitial() when you are ready to display an interstitial.
	 */
	public void displayInterstitial() {
		if( mInterstitialAd.isLoaded() ) {
			mInterstitialAd.show();
		}
	}

	/**
	 * Set data on UI.
	 */
	private void setData() {
		Entry  entry = (Entry) getIntent().getSerializableExtra( EXTRAS_ENTRY );
		String query = getIntent().getStringExtra( EXTRAS_QUERY );
		//Init adapter
		mBinding.setDetailPagerAdapter( new DetailPagerAdapter( this, getSupportFragmentManager(), entry, query ) );
	}

	@Override
	protected void onNewIntent( Intent intent ) {
		super.onNewIntent( intent );
		setIntent( intent );
		setData();
	}

	@Override
	public void onBackPressed() {
		if( mBinding.detailPager.getCurrentItem() == 1 ) {
			mBinding.detailPager.setCurrentItem( 0, true );
		} else {
			super.onBackPressed();
		}
	}
}
