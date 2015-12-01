package com.cusnews.utils;

import java.util.Locale;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chopping.application.BasicPrefs;
import com.cusnews.R;
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
	private static final String KEY_EULA_SHOWN             = "key.eula.shown";
	/**
	 * Device ident.
	 */
	private static final String KEY_DEVICE_IDENT           = "key.device.ident";
	/**
	 * Type of different views, like horizontal, vertical, grid, etc.
	 */
	private static final String KEY_VIEW_TYPE              = "key.view.type";
	/**
	 * Download-info of application.
	 */
	private static final String KEY_APP_DOWNLOAD           = "key.app.download";
	/**
	 * Feeds-language.
	 */
	private static final String KEY_LANG                   = "key.lang";
	/**
	 * Feeds-language(value: 0, 1, 2).
	 */
	public static final  String KEY_LANG_VALUE             = "key.lang.value";
	/**
	 * Show all images or not.
	 */
	public static final  String KEY_SHOW_IMAGES            = "key.show.images";
	/**
	 * The last push-token from Google.
	 */
	private static final String KEY_PUSH_TOKEN             = "key.push.token";
	/**
	 * The switch on/off push.
	 */
	public static final  String KEY_PUSH_ON_OFF            = "key.push.on.off";
	/**
	 * The selections of topics to push, it is the list of api-names sep by ",", see {@link com.cusnews.ds.Topic}.
	 */
	public static final  String KEY_PUSH_TOPICS_SELECTIONS = "key.push.topics.selection";
	/**
	 * Customize push-topics.
	 */
	public static final  String KEY_PUSH_TOPICS_DIY        = "key.push.topics.diy";
	//--------------
	//Different push-newsletters
	public static final  String KEY_PUSH_NEWS              = "key.push.news";
	public static final  String KEY_PUSH_FOOTBALL          = "key.push.football";
	public static final  String KEY_PUSH_INTERNET          = "key.push.internet";
	public static final  String KEY_PUSH_GOOGLE            = "key.push.google";
	public static final  String KEY_PUSH_APPLE             = "key.push.apple";
	//--------------
	/**
	 * Has asked opening push or not.
	 */
	private static final String KEY_ASKED_PUSH             = "key.asked.push";

	/**
	 * Suggestion on a dialog before adding tab to UI.
	 */
	private static final String KEY_ADD_TAB_TIP = "key.add.tab.tip";

	/**
	 * Google's ID
	 */
	private static final String KEY_GOOGLE_ID           = "key.google.id";
	/**
	 * The display-name of Google's user.
	 */
	private static final String KEY_GOOGLE_DISPLAY_NAME = "key.google.display.name";
	/**
	 * Url to user's profile-image.
	 */
	private static final String KEY_GOOGLE_THUMB_URL    = "key.google.thumb.url";
	/**
	 * Home-page of API-provider.
	 */
	private static final String FAROO_HOME              = "faroo_home";
	/**
	 * Blog-page of API-provider.
	 */
	private static final String FAROO_BLOG              = "faroo_blog";
	/**
	 * The Instance.
	 */
	private static Prefs sInstance;

	private Prefs() {
		super( null );
	}

	/**
	 * Created a DeviceData storage.
	 *
	 * @param context
	 * 		A context object.
	 */
	private Prefs( Context context ) {
		super( context );
	}

	/**
	 * Singleton method.
	 *
	 * @param context
	 * 		A context object.
	 *
	 * @return single instance of DeviceData
	 */
	public static Prefs createInstance( Context context ) {
		if( sInstance == null ) {
			synchronized( Prefs.class ) {
				if( sInstance == null ) {
					sInstance = new Prefs( context );
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
		return getBoolean( KEY_EULA_SHOWN, false );
	}

	/**
	 * Set whether the "End User License Agreement" has been shown and agreed at application's first start.
	 * <p/>
	 *
	 * @param isConfirmed
	 * 		{@code true} if EULA has been shown and agreed.
	 */
	public void setEULAOnceConfirmed( boolean isConfirmed ) {
		setBoolean( KEY_EULA_SHOWN, isConfirmed );
	}
	/**
	 * @return The device identifier for remote storage.
	 */
	public String getDeviceIdent() {
		return getString( KEY_DEVICE_IDENT, null );
	}
	/**
	 * Set device identifier for remote storage.
	 *
	 * @param ident
	 * 		An identifier.
	 */
	public void setDeviceIdent( String ident ) {
		setString( KEY_DEVICE_IDENT, ident );
	}
	/**
	 * @return Home-page of API-provider.
	 */
	public String getFarooHome() {
		return getString( FAROO_HOME, "http://www.faroo.com/#q=&s=1&l=en&src=web" );
	}

	/**
	 * @return Home-Blog of API-provider.
	 */
	public String getFarooBlog() {
		return getString( FAROO_BLOG, "http://blog.faroo.com/" );
	}

	/**
	 * @return {@link ViewType}:  like horizontal, vertical, grid, etc.
	 */
	public ViewType getViewType() {
		return ViewType.fromValue( getInt( KEY_VIEW_TYPE, mContext.getResources().getBoolean( R.bool.landscape ) ? 2 : 1 ) );
	}

	/**
	 * Set view-type horizontal, vertical, grid, etc.
	 *
	 * @param viewType
	 * 		{@link ViewType}.
	 */
	public void setViewType( ViewType viewType ) {
		setInt( KEY_VIEW_TYPE, viewType.getValue() );
	}

	/**
	 * @return Download-info of application.
	 */
	public String getAppDownloadInfo() {
		return getString( KEY_APP_DOWNLOAD, null );
	}

	/**
	 * Set download-info of application.
	 */
	public void setAppDownloadInfo( String appDownloadInfo ) {
		setString( KEY_APP_DOWNLOAD, appDownloadInfo );
	}

	/**
	 * @return Feeds-language.
	 */
	public String getLanguage() {
		return getString( KEY_LANG, Locale.getDefault().getLanguage() );
	}

	/**
	 * Set feeds-language.
	 *
	 * @param language
	 * 		Feeds-language.
	 */
	public void setLanguage( String language ) {
		setString( KEY_LANG, language );
	}


	/**
	 * @return Feeds-language(value: 0-en, 1-de, 2-zh).
	 */
	public String getLanguageValue() {
		return getString( KEY_LANG_VALUE, initLanguageValue() );
	}

	/**
	 * Set feeds-language(value: 0-en, 1-de, 2-zh).
	 *
	 * @param languageValue
	 * 		Feeds-language(value: 0-en, 1-de, 2-zh).
	 */
	public void setLanguageValue( String languageValue ) {
		setString( KEY_LANG_VALUE, languageValue );
	}

	private String initLanguageValue() {
		if( TextUtils.equals( Locale.getDefault().getLanguage(), "en" ) ) {
			return "0";
		}
		if( TextUtils.equals( Locale.getDefault().getLanguage(), "de" ) ) {
			return "1";
		}
		if( TextUtils.equals( Locale.getDefault().getLanguage(), "zh" ) ) {
			return "2";
		}
		return "0";
	}

	/**
	 * @return Show all images or not. {@code true} if show, otherwise not show.
	 */
	public boolean showAllImages() {
		return getBoolean( KEY_SHOW_IMAGES, true );
	}

	/**
	 * Set whether the suggestion on a dialog before adding tab to UI has been shown.
	 * <p/>
	 * {@code true} if the tip has been shown before.
	 */
	public void setAddTabTip( boolean shown ) {
		setBoolean( KEY_ADD_TAB_TIP, shown );
	}

	/**
	 * Suggestion on a dialog before adding tab to UI.
	 *
	 * @return {@code true} if the tip has been shown before.
	 */
	public boolean addTabTip() {
		return getBoolean( KEY_ADD_TAB_TIP, false );
	}

	/**
	 * To check whether the push which is named by {@code keyName} has been subscribed or not.
	 *
	 * @param keyName
	 * 		See.
	 * 		<pre>
	 * 														<code>
	 * 														public static final String KEY_PUSH_NEWS = "key.push.news";
	 * 														public static final String KEY_PUSH_FOOTBALL = "key.push.football";by {@code keyName}
	 * 														public static final String KEY_PUSH_INTERNET = "key.push.internet";by {@code keyName}
	 * 														public static final String KEY_PUSH_GOOGLE = "key.push.google";by {@code keyName}
	 * 														public static final String KEY_PUSH_APPLE = "key.push.apple";by {@code keyName}
	 * 														</code>
	 * 												</pre>
	 *
	 * @return {@code true} if the push named by {@code keyName}  is subscribed.
	 */
	public boolean getPush( String keyName ) {
		return getBoolean( keyName, false );
	}


	/**
	 * To set whether the push which is named by {@code keyName} has been subscribed or not.
	 *
	 * @param keyName
	 * 		See.
	 * 		<pre>
	 * 														<code>
	 * 														public static final String KEY_PUSH_NEWS = "key.push.news";
	 * 														public static final String KEY_PUSH_FOOTBALL = "key.push.football";by {@code keyName}
	 * 														public static final String KEY_PUSH_INTERNET = "key.push.internet";by {@code keyName}
	 * 														public static final String KEY_PUSH_GOOGLE = "key.push.google";by {@code keyName}
	 * 														public static final String KEY_PUSH_APPLE = "key.push.apple";by {@code keyName}
	 * 														</code>
	 * 												</pre>
	 */
	public void setPush( String keyName, boolean value ) {
		setBoolean( keyName, value );
	}
	/**
	 * @return Last push-token from Google. It could be {@code null} when the registration is failed or never unregistered before.
	 */
	public
	@Nullable
	String getPushToken() {
		return getString( KEY_PUSH_TOKEN, null );
	}
	/**
	 * Set the last push-token from Google.
	 *
	 * @param token
	 * 		Last push-token from Google. It could be {@code null} when the registration is failed.
	 */
	public void setPushToken( @Nullable String token ) {
		setString( KEY_PUSH_TOKEN, token );
	}
	/**
	 * @return The selections of topics to push, it is the list of api-names sep by ",", see {@link com.cusnews.ds.Topic}. Could be {@code null} if
	 * nothing subscribed.
	 */
	public
	@Nullable
	String getPushSelections() {
		return getString( KEY_PUSH_TOPICS_SELECTIONS, null );
	}
	/**
	 * The selections of topics to push, it is the list of api-names sep by ",", see {@link com.cusnews.ds.Topic}.
	 *
	 * @param nameLis
	 * 		Current selections. Could be {@code null} if nothing subscribed.
	 */
	public void setPushSelections( @Nullable String nameLis ) {
		setString( KEY_PUSH_TOPICS_SELECTIONS, nameLis );
	}
	/**
	 * Set that the App has asked opening push or not.
	 */
	public void setAskedPush( boolean asked ) {
		setBoolean( KEY_ASKED_PUSH, asked );
	}

	/**
	 * Has asked opening push or not.
	 */
	public boolean askedPush() {
		return getBoolean( KEY_ASKED_PUSH, false );
	}
	/**
	 * Google's ID
	 */
	public String getGoogleId() {
		return getString( KEY_GOOGLE_ID, null );
	}
	/**
	 * Google's ID
	 */
	public void setGoogleId( String id ) {
		setString( KEY_GOOGLE_ID, id );
	}
	/**
	 * The display-name of Google's user.
	 */
	public String getGoogleDisplyName() {
		return getString( KEY_GOOGLE_DISPLAY_NAME, null );
	}
	/**
	 * The display-name of Google's user.
	 */
	public void setGoogleDisplyName( String displayName ) {
		setString( KEY_GOOGLE_DISPLAY_NAME, displayName );
	}
	/**
	 * Url to user's profile-image.
	 */
	public String getGoogleThumbUrl() {
		return getString( KEY_GOOGLE_THUMB_URL, null );
	}
	/**
	 * The display-name of Google's user.
	 */
	public void setGoogleThumbUrl( String thumbUrl ) {
		setString( KEY_GOOGLE_THUMB_URL, thumbUrl );
	}
}
