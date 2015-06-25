package com.cusnews.app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cusnews.R;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;


/**
 * The webview of application.
 *
 * @author Xinyue Zhao
 */
public final class WebViewActivity extends  CusNewsActivity{

	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_webview;
	/**
	 * The menu to this view.
	 */
	private static final int MENU = R.menu.webview;
	/**
	 * Extras for webview's url.
	 */
	private static final String EXTRAS_URL = WebViewActivity.class.getName() + ".EXTRAS.url";
	/**
	 * Extras for webview's title
	 */
	private static final String EXTRAS_TITLE = WebViewActivity.class.getName() + ".EXTRAS.title";
	/**
	 * WebView .
	 */
	private WebView mWebView;
	/**
	 * Height of action-bar general.
	 */
	private int mActionBarHeight;
	/**
	 * Pull-2-Load.
	 */
	private SwipeRefreshLayout mSwipeRefreshLayout;

	/**
	 * Show single instance of {@link WebViewActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 * @param title
	 * 		Title shows on actionbar.
	 * @param url
	 * 		Url to show.
	 */
	public static void showInstance(Context cxt, String title, String url) {
		Intent intent = new Intent(cxt, WebViewActivity.class);
		intent.putExtra(EXTRAS_URL, url);
		intent.putExtra(EXTRAS_TITLE, title);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		cxt.startActivity(intent);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		calcActionBarHeight();

		Prefs prefs = Prefs.getInstance();
		ViewType vt = prefs.getViewType();
		switch (vt) {
		case GRID:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		default:
			//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}


		//Actionbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(getIntent().getStringExtra(EXTRAS_TITLE));

		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.web_view_content_srl);
		mSwipeRefreshLayout.setColorSchemeResources(R.color.green_1, R.color.green_2, R.color.green_3, R.color.green_4);
		mSwipeRefreshLayout.setProgressViewEndTarget(true, mActionBarHeight * 2);
		mSwipeRefreshLayout.setProgressViewOffset(false, 0, mActionBarHeight * 2);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mWebView.reload();
			}
		});

		mWebView = (WebView) findViewById(R.id.webView);
		WebSettings settings = mWebView.getSettings();
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false);
		settings.setDomStorageEnabled(true);
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mSwipeRefreshLayout.setRefreshing(true);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mSwipeRefreshLayout.setRefreshing(false);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(getIntent());
	}

	/**
	 * Handle intent to show web.
	 *
	 * @param intent
	 * 		Data for the activity.
	 */
	private void handleIntent(Intent intent) {
		String url = intent.getStringExtra(EXTRAS_URL);
		if (!TextUtils.isEmpty(url)) {
			mWebView.loadUrl(url);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(MENU, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ActivityCompat.finishAfterTransition(this);
			break;
		case R.id.action_forward:
			if (mWebView.canGoForward()) {
				mWebView.goForward();
			}
			break;
		case R.id.action_backward:
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
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
		TypedArray a = obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);
	}

}
