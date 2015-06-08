package com.cusnews.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.databinding.DetailSiteBinding;
import com.cusnews.widgets.WebViewEx.OnWebViewExScrolledListener;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Show basic information of a news.
 *
 * @author Xinyue Zhao
 */
public final class DetailSiteFragment extends CusNewsFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_detail_site;

	/**
	 * Data-binding.
	 */
	private DetailSiteBinding mBinding;
	/**
	 * Height of action-bar general.
	 */
	private int mActionBarHeight;
	/**
	 * Initialize an {@link  DetailSiteFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link DetailSiteFragment}.
	 */
	public static DetailSiteFragment newInstance(Context context) {
		return (DetailSiteFragment) Fragment.instantiate(context, DetailSiteFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		calcActionBarHeight();
		return inflater.inflate(LAYOUT, container, false);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding = DataBindingUtil.bind(view);
		mBinding.setVariable(BR.entry, App.Instance.getOpenedEntry());

		//Init pull2load
		mBinding.contentSrl.setColorSchemeResources(R.color.green_1, R.color.green_2, R.color.green_3, R.color.green_4);
		mBinding.contentSrl.setProgressViewEndTarget(true, mActionBarHeight * 2);
		mBinding.contentSrl.setProgressViewOffset(false, 0, mActionBarHeight * 2);
		mBinding.contentSrl.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mBinding.detailWv.reload();
			}
		});

		//Init webview
		WebSettings settings = mBinding.detailWv.getSettings();
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false);
		settings.setDomStorageEnabled(true);
		mBinding.detailWv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
				mBinding.contentSrl.setRefreshing(true);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mBinding.contentSrl.setRefreshing(false);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});



		//Init web-nav.
		mBinding.backwardBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBinding.detailWv.canGoBack()) {
					mBinding.detailWv.goBack();
				}
			}
		});
		mBinding.forwardBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mBinding.detailWv.canGoForward()) {
					mBinding.detailWv.goForward();
				}
			}
		});
		ViewCompat.setElevation(mBinding.forwardBtn, getResources().getDimensionPixelSize(R.dimen.common_elevation));
		ViewCompat.setElevation(mBinding.backwardBtn, getResources().getDimensionPixelSize(R.dimen.common_elevation));
		mBinding.detailWv.setOnWebViewExScrolledListener(new OnWebViewExScrolledListener() {
			@Override
			public void onScrollChanged(boolean isUp) {
				if (!isUp) {
					ViewPropertyAnimator.animate(mBinding.forwardBtn).scaleX(1).scaleY(1).setDuration(200).start();
					ViewPropertyAnimator.animate(mBinding.backwardBtn).scaleX(1).scaleY(1).setDuration(200).start();
				} else  {
					ViewPropertyAnimator.animate(mBinding.forwardBtn).scaleX(0).scaleY(0).setDuration(200).start();
					ViewPropertyAnimator.animate(mBinding.backwardBtn).scaleX(0).scaleY(0).setDuration(200).start();
				}
			}

			@Override
			public void onScrolledTop() {

			}
		});

	}

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
		TypedArray a = getActivity().obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);
	}

}
