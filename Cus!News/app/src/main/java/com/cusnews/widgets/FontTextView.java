package com.cusnews.widgets;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cusnews.R;


/**
 * A TextView that allows a custom font to be defined in a layout. The font must
 * be in the assets folder.
 * <a href="http://stackoverflow.com/questions/2376250/custom-fonts-and-xml-layouts-android">Stackoverflow</a>
 */
public class FontTextView extends TextView {
	public FontTextView(Context context) {
		super(context);
	}

	public FontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public FontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		String font;

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.FontButton);
		int fontIndex = a.getInt(R.styleable.FontButton_font, -1);

		// defined in attrs_fontui.xml
		switch (fontIndex) {
		case 1:
			font = Fonts.FONT_CN;
			break;
		default:
			font = Fonts.FONT_BOLD_CN;
			break;
		}

		a.recycle();

		if (font != null) {
			setFont(font);
		}
	}

	public void setFont(String font) {
		if (!isInEditMode()) {
			Typeface tf = Fonts.getFont(getContext(), font);
			setTypeface(tf);
		}
	}

	/**
	 * A cache for Fonts. Works around a known memory leak in
	 * <code>Typeface.createFromAsset</code>.
	 * 
	 * <a href="http://code.google.com/p/android/issues/detail?id=9904">Google Code</a>
	 */
	public final static class Fonts {
		private static final ConcurrentHashMap<String, Typeface> sTypefaces = new ConcurrentHashMap<String, Typeface>();

		public static final String FONT_CN = "FrutigerLTStd-Cn.otf";
		public static final String FONT_BOLD_CN = "FrutigerLTStd-BoldCn.otf";

		public static Typeface getFont(Context context, String assetPath) {
			Typeface font = sTypefaces.get(assetPath);
			if (font == null) {
				font = Typeface.createFromAsset(context.getAssets(), "fonts/"
						+ assetPath);
				sTypefaces.put(assetPath, font);
			}
			return font;
		}

	}
}
