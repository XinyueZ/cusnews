package com.cusnews.api;

import android.content.Context;

/**
 * Error when Api is not initialized.
 * <p/>
 * <p/>
 * The {@link Api} must call {@link Api#initialize(Context, String, String, long)} first before any using.
 *
 * @author Xinyue Zhao
 */
public final class ApiNotInitializedException extends Exception {
	@Override
	public String getMessage() {
		return "The {@link Api} must call {@link Api#initialize(Context, String, long)} first before any using.";
	}
}
