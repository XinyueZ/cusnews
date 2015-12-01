package com.cusnews.utils;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.net.Uri;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cusnews.R;

/**
 * Helper class.
 *
 * @author Xinyue Zhao
 */
public final class Utils {
	/**
	 * Convert uri-str to {@link URI}.
	 *
	 * @param uriStr
	 * 		The original uri-str.
	 *
	 * @return {@link URI}.
	 */
	public static URI uriStr2URI( String uriStr ) {
		Uri    uri  = Uri.parse( uriStr );
		String host = uri.getHost();
		String body = uri.getEncodedPath();
		URI    ui   = null;
		try {
			ui = new URI( "http", host, body, null );
		} catch( URISyntaxException e ) {
			e.printStackTrace();
		}
		return ui;
	}


	/**
	 * Helper to close keyboard.
	 *
	 * @param editText
	 * 		{@link EditText} the host of keyboard.
	 */
	public static void closeKeyboard( EditText editText ) {
		InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService( Context.INPUT_METHOD_SERVICE );
		imm.hideSoftInputFromWindow( editText.getWindowToken(), 0 );
	}

	/**
	 * Test whether the {@link EditText} has a valid value or not, if invalidate, shakes it.
	 *
	 * @param editText
	 * 		{@link EditText}.
	 *
	 * @return {@code true} if {@link EditText} has a valid value.
	 */
	public static boolean validateKeyword( EditText editText ) {
		boolean val;
		Context cxt  = editText.getContext();
		String  text = editText.getText().toString();
		if( text.matches( ".*[/=():;].*" ) ) {
			val = false;
			com.chopping.utils.Utils.showLongToast( cxt, R.string.lbl_exclude_chars );
		} else {
			val = true;
		}

		if( !val ) {
			Animation shake = AnimationUtils.loadAnimation( cxt, R.anim.shake );
			editText.startAnimation( shake );
		}
		return val;
	}


	/**
	 * Test whether the {@link String} is  valid value or not, if invalidate, shakes it.
	 */
	public static boolean validateStr( Context cxt, String s ) {
		boolean val;
		if( s.matches( ".*[/=():;].*" ) ) {
			val = false;
			com.chopping.utils.Utils.showLongToast( cxt, R.string.lbl_exclude_chars );
		} else {
			val = true;
		}
		return val;
	}
}
