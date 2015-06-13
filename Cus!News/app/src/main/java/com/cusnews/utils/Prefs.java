package com.cusnews.utils;

import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;

import com.chopping.application.BasicPrefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;

/**
 * Store app and device information.
 *
 * @author Chris.Xinyue Zhao
 */
public final class Prefs extends BasicPrefs {
	/**
	 * Storage. Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 * {@code true} if EULA has been shown and agreed.
	 */
	private static final String KEY_EULA_SHOWN = "key.eula.shown";
	/**
	 * Device ident.
	 */
	private static final String KEY_DEVICE_IDENT = "key.device.ident";
	/**
	 * Type of different views, like horizontal, vertical, grid, etc.
	 */
	private static final String KEY_VIEW_TYPE = "key.view.type";
	/**
	 * Download-info of application.
	 */
	private static final String KEY_APP_DOWNLOAD = "key.app.download";
	/**
	 * Feeds-language.
	 */
	private static final String KEY_LANG = "key.lang";
	/**
	 * Feeds-language(value: 0, 1, 2).
	 */
	public static final String KEY_LANG_VALUE = "key.lang.value";
	/**
	 * Show all images or not.
	 */
	public static final String KEY_SHOW_IMAGES = "key.show.images";
	/**
	 * Suggestion on a dialog before adding tab to UI.
	 */
	private static final String KEY_ADD_TAB_TIP = "key.add.tab.tip";
	/**
	 * Home-page of API-provider.
	 */
	private static final String FAROO_HOME = "faroo_home";
	/**
	 * Blog-page of API-provider.
	 */
	private static final String FAROO_BLOG = "faroo_blog";
	/**
	 * The Instance.
	 */
	private static Prefs sInstance;

	private Prefs() {
		super(null);
	}

	/**
	 * Created a DeviceData storage.
	 *
	 * @param context
	 * 		A context object.
	 */
	private Prefs(Context context) {
		super(context);
	}

	/**
	 * Singleton method.
	 *
	 * @param context
	 * 		A context object.
	 *
	 * @return single instance of DeviceData
	 */
	public static Prefs createInstance(Context context) {
		if (sInstance == null) {
			synchronized (Prefs.class) {
				if (sInstance == null) {
					sInstance = new Prefs(context);
				}
			}
		}
		return sInstance;
	}

	/**
	 * Singleton getInstance().
	 *
	 * @return The instance of Prefs.
	 */
	public static Prefs getInstance() {
		return sInstance;
	}


	/**
	 * Whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @return {@code true} if EULA has been shown and agreed.
	 */
	public boolean isEULAOnceConfirmed() {
		return getBoolean(KEY_EULA_SHOWN, false);
	}

	/**
	 * Set whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @param isConfirmed
	 * 		{@code true} if EULA has been shown and agreed.
	 */
	public void setEULAOnceConfirmed(boolean isConfirmed) {
		setBoolean(KEY_EULA_SHOWN, isConfirmed);
	}


	/**
	 * Set device identifier for remote storage.
	 *
	 * @param ident
	 * 		An identifier.
	 */
	public void setDeviceIdent(String ident) {
		setString(KEY_DEVICE_IDENT, ident);
	}

	/**
	 * @return The device identifier for remote storage.
	 */
	public String getDeviceIdent() {
		return getString(KEY_DEVICE_IDENT, null);
	}

	/**
	 * @return  Home-page of API-provider.
	 */
	public String getFarooHome() {
		return getString(FAROO_HOME, "http://www.faroo.com/#q=&s=1&l=en&src=web");
	}

	/**
	 * @return  Home-Blog of API-provider.
	 */
	public String getFarooBlog() {
		return getString(FAROO_BLOG, "http://blog.faroo.com/");
	}

	/**
	 *
	 * @return {@link ViewType}:  like horizontal, vertical, grid, etc.
	 */
	public ViewType getViewType() {
		return ViewType.fromValue(getInt(KEY_VIEW_TYPE, 1));
	}

	/**
	 * Set view-type horizontal, vertical, grid, etc.
	 * @param viewType  {@link ViewType}.
	 */
	public void setViewType(ViewType viewType) {
		setInt(KEY_VIEW_TYPE, viewType.getValue());
	}

	/**
	 * @return Download-info of application.
	 */
	public String getAppDownloadInfo() {
		return getString(KEY_APP_DOWNLOAD, null);
	}

	/**
	 * Set download-info of application.
	 */
	public void setAppDownloadInfo(String appDownloadInfo) {
		setString(KEY_APP_DOWNLOAD, appDownloadInfo);
	}
	/**
	 * @return Feeds-language.
	 */
	public String getLanguage() {
		return getString(KEY_LANG, Locale.getDefault().getLanguage());
	}
	/**
	 * Set feeds-language.
	 * @param  language Feeds-language.
	 */
	public void setLanguage(String language) {
		setString(KEY_LANG, language);
	}


	/**
	 * @return Feeds-language(value: 0-en, 1-de, 2-zh).
	 */
	public String getLanguageValue() {
		return getString(KEY_LANG_VALUE, initLanguageValue());
	}
	/**
	 * Set feeds-language(value: 0-en, 1-de, 2-zh).
	 * @param  languageValue Feeds-language(value: 0-en, 1-de, 2-zh).
	 */
	public void setLanguageValue(String languageValue) {
		setString(KEY_LANG_VALUE, languageValue);
	}

	private String initLanguageValue() {
		if(TextUtils.equals(Locale.getDefault().getLanguage(), "en")) {
			return "0";
		}
		if(TextUtils.equals(Locale.getDefault().getLanguage(), "de")) {
			return "1";
		}
		if(TextUtils.equals(Locale.getDefault().getLanguage(), "zh")) {
			return "2";
		}
		return "0";
	}

	/**
	 *
	 * @return Show all images or not. {@code true} if show, otherwise not show.
	 */
	public boolean showAllImages() {
		return getBoolean(KEY_SHOW_IMAGES, true);
	}
	/**
	 * Set whether the suggestion on a dialog before adding tab to UI has been shown.
	 * <p/>
	 * {@code true} if the tip has been shown before.
	 */
	public void setAddTabTip(boolean shown) {
		setBoolean(KEY_ADD_TAB_TIP, shown);
	}
	/**
	 * Suggestion on a dialog before adding tab to UI.
	 * @return  {@code true} if the tip has been shown before.
	 */
	public boolean addTabTip() {
		return getBoolean(KEY_ADD_TAB_TIP, false);
	}
}
