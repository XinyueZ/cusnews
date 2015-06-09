package com.cusnews.utils;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * See. <a href="http://stackoverflow.com/questions/15617210/android-html-fromhtml-with-imagesF">Stackoverflow</a>
 */
public final class URLDrawable extends BitmapDrawable {
	// the drawable that you need to set, you could set the initial drawing
	// with the loading image if you need to
	protected Drawable drawable;

	@Override
	public void draw(Canvas canvas) {
		// override the draw to facilitate refresh function later
		if (drawable != null) {
			drawable.draw(canvas);
		}
	}
}
