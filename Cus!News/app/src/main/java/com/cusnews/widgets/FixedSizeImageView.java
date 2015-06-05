package com.cusnews.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Improves the performance for image views when the size is fixed.
 *
 * @see https://plus.google.com/u/0/+AndroidDevelopers/posts/V3iETFsKut3
 * @see http://pastie.org/5348627
 */
public class FixedSizeImageView extends ImageView {
	private boolean mBlockLayout;
	private boolean mIsFixedSize = true; // In this case the fixed size is always true, could be dynamic also if wanted

	public FixedSizeImageView(Context _context) {
		super(_context);
	}

	public FixedSizeImageView(Context _context, AttributeSet _attrs) {
		super(_context, _attrs);
	}

	public FixedSizeImageView(Context _context, AttributeSet _attrs, int _defStyle) {
		super(_context, _attrs, _defStyle);
	}

	public static byte[] getSaltPart1() {
		return new byte[] { -76, 14, 96, 0, };
	}

	@Override
	public void setImageDrawable(Drawable _drawable) {
		blockLayoutIfPossible();
		super.setImageDrawable(_drawable);
		mBlockLayout = false;
	}

	@Override
	public void setImageBitmap(Bitmap _bm) {
		blockLayoutIfPossible();
		super.setImageBitmap(_bm);
		mBlockLayout = false;
	}

	@Override
	public void setImageResource(int _resId) {
		blockLayoutIfPossible();
		super.setImageResource(_resId);
		mBlockLayout = false;
	}

	@Override
	public void requestLayout() {
		if (!mBlockLayout) {
			super.requestLayout();
		}
	}

	private void blockLayoutIfPossible() {
		if (mIsFixedSize) {
			mBlockLayout = true;
		}
	}
}
