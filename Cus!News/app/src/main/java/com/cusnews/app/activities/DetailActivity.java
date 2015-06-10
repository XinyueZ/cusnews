package com.cusnews.app.activities;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.app.adapters.DetailPagerAdapter;
import com.cusnews.bus.OpenRelatedEvent;
import com.cusnews.databinding.ActivityDetailBinding;
import com.cusnews.ds.Entry;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Detail news. Contains a general detail view and a {@link android.webkit.WebView}.
 *
 * @author Xinyue Zhao
 */
public final class DetailActivity extends CusNewsActivity {
	private static final String EXTRAS_QUERY = DetailActivity.class.getName() + ".EXTRAS.query";
	private static final String EXTRAS_ENTRY = DetailActivity.class.getName() + ".EXTRAS.entry";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_detail;
	/**
	 * A tinyurl to the {@link Entry}.
	 */
	private String mSharedEntryUrl;

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
	public void onEvent(OpenRelatedEvent e) {
		DetailActivity.showInstance(this, e.getEntry(), e.getKeyword());
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
	public static void showInstance(Context cxt, Entry entry, @Nullable String query) {
		Intent intent = new Intent(cxt, DetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(EXTRAS_ENTRY, (Serializable) entry);
		intent.putExtra(EXTRAS_QUERY, query == null ? "" : query);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, LAYOUT);
		setData();


		int curTime = App.Instance.getAdsShownTimes();
		int adsTimes = 6;
		if (curTime % adsTimes == 0) {
			// Create an ad.
			mInterstitialAd = new InterstitialAd(this);
			mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
			// Create ad request.
			AdRequest adRequest = new AdRequest.Builder().build();
			// Begin loading your interstitial.
			mInterstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					super.onAdLoaded();
					displayInterstitial();
				}
			});
			mInterstitialAd.loadAd(adRequest);
		}
		curTime++;
		App.Instance.setAdsShownTimes(curTime);
	}

	/**
	 * Invoke displayInterstitial() when you are ready to display an interstitial.
	 */
	public void displayInterstitial() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
		}
	}

	/**
	 * Set data on UI.
	 */
	private void setData() {
		Entry entry = (Entry) getIntent().getSerializableExtra(EXTRAS_ENTRY);
		String query = getIntent().getStringExtra(EXTRAS_QUERY);
		//Init adapter
		mBinding.setDetailPagerAdapter(new DetailPagerAdapter(this, getSupportFragmentManager(), entry, query));
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		setData();
	}
}
