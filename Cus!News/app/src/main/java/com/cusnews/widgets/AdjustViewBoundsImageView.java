package com.cusnews.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


public class AdjustViewBoundsImageView extends ImageView {

	public AdjustViewBoundsImageView( Context _context, AttributeSet _attrs, int _defStyle ) {
		super( _context, _attrs, _defStyle );
	}


	public AdjustViewBoundsImageView( Context _context, AttributeSet _attrs ) {
		super( _context, _attrs );
	}


	public AdjustViewBoundsImageView( Context _context ) {
		super( _context );
	}

	public static byte[] getInitVector2() {
		return new byte[] { 118 , -26 , 14 , 93 , 18 , };
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
		Drawable drawable = getDrawable();
		if( drawable != null ) {
			int width = MeasureSpec.getSize( widthMeasureSpec );
			int diw   = drawable.getIntrinsicWidth();
			if( diw > 0 ) {
				int height = width * drawable.getIntrinsicHeight() / diw;
				setMeasuredDimension( width, height );
			} else {
				super.onMeasure( widthMeasureSpec, heightMeasureSpec );
			}
		} else {
			super.onMeasure( widthMeasureSpec, heightMeasureSpec );
		}
	}
}
