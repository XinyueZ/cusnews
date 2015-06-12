package com.cusnews.utils;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Helper class.
 *
 * @author Xinyue Zhao
 */
public final class Utils {
	/**
	 * Convert uri-str to {@link URI}.
	 * @param uriStr The original uri-str.
	 * @return {@link URI}.
	 */
	public static URI uriStr2URI(String uriStr) {
		Uri uri = Uri.parse(uriStr);
		String host = uri.getHost();
		String body = uri.getEncodedPath();
		URI ui = null;
		try {
			ui = new URI(
					"http",
					host,
					body,
					null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return ui;
	}


	/**
	 * Standard sharing app for sharing on actionbar.
	 */
	public static Intent getDefaultShareIntent(android.support.v7.widget.ShareActionProvider provider, String subject,
			String body) {
		if (provider != null) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			i.putExtra(android.content.Intent.EXTRA_TEXT, body);
			provider.setShareIntent(i);
			return i;
		}
		return null;
	}

	/**
	 * Helper to close keyboard.
	 * @param editText {@link EditText} the host of keyboard.
	 */
	public static void closeKeyboard(EditText editText) {
		InputMethodManager imm = (InputMethodManager)editText.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
