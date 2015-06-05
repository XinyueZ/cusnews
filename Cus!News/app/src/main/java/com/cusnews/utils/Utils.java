package com.cusnews.utils;

import java.net.URI;
import java.net.URISyntaxException;

import android.net.Uri;

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
}
