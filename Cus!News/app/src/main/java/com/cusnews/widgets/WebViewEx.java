package com.cusnews.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * An extension of standard WebView that we can detect which direction user scrolled.
 *
 * @author Xinyue Zhao
 */
public final class WebViewEx extends WebView {
	/**
	 * A listener hooks the WebView when it scrolled.
	 */
	private OnWebViewExScrolledListener mOnWebViewExScrolledListener;

	public WebViewEx(Context context) {
		super(context);
	}

	public WebViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (t > 0) {
			if (mOnWebViewExScrolledListener != null) {
				mOnWebViewExScrolledListener.onScrollChanged(t > oldt);
			}
		} else {
			if (t == 0) {
				if (mOnWebViewExScrolledListener != null) {
					mOnWebViewExScrolledListener.onScrolledTop();
				}
			}
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * Set listener hooks the WebView when it scrolled.
	 *
	 * @param onWebViewExScrolledListener
	 * 		The instance of listener.
	 */
	public void setOnWebViewExScrolledListener(OnWebViewExScrolledListener onWebViewExScrolledListener) {
		mOnWebViewExScrolledListener = onWebViewExScrolledListener;
	}



	/**
	 * A listener hooks the WebView when it scrolled.
	 *
	 * @author Xinyue Zhao
	 */
	public interface OnWebViewExScrolledListener {
		/**
		 * Event fired when user scrolled the WebView.
		 *
		 * @param isUp
		 * 		True if user scrolled up, false then down.
		 */
		void onScrollChanged(boolean isUp);
		/**
		 * Event fired when user scrolled the WebView onto TOP.
		 */
		void onScrolledTop();
	}
}
