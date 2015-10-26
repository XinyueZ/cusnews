/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG, Never BUG.
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱。
package com.cusnews.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.chopping.net.TaskHelper;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.utils.Prefs;
import com.tinyurl4j.data.Response;

import cn.bmob.v3.Bmob;
import io.fabric.sdk.android.Fabric;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Application object.
 *
 * @author Xinyue Zhao
 */
public final class App extends MultiDexApplication {
	/**
	 * Singleton.
	 */
	public static App Instance;
	/**
	 * Api-key.
	 */
	private String mApiKey;
	/**
	 * The id of push-sender.
	 */
	private String mSenderId;


	/**
	 * Times that the AdMob shown before, it under App-process domain. When process killed, it recounts
	 */
	private int mAdsShownTimes;

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
		Instance = this;

//		Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(
//				Stetho.defaultDumperPluginsProvider(this)).enableWebKitInspector(Stetho.defaultInspectorModulesProvider(
//				this)).build());


		TaskHelper.init(getApplicationContext());
		Prefs.createInstance(this);

		Properties prop = new Properties();
		InputStream input = null;
		try {
			/*From "resources".*/
			input = getClassLoader().getResourceAsStream("key.properties");
			if (input != null) {
				// load a properties file
				prop.load(input);
				mApiKey = prop.getProperty("appkey");
				mSenderId = prop.getProperty("senderId");
				Bmob.initialize(this, prop.getProperty("bmobkey"));
			}
		} catch (IOException ex) {
			mApiKey = null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		Api.initialize(this, "http://www.faroo.com/");


		String url = Prefs.getInstance().getAppDownloadInfo();
		if (TextUtils.isEmpty(url) || !url.contains("tinyurl")) {
			com.tinyurl4j.Api.getTinyUrl(getString(R.string.lbl_store_url, getPackageName()), new Callback<Response>() {
				@Override
				public void success(Response response, retrofit.client.Response response2) {
					Prefs.getInstance().setAppDownloadInfo(getString(R.string.lbl_share_download_app, getString(
							R.string.application_name), response.getResult()));
				}

				@Override
				public void failure(RetrofitError error) {
					Prefs.getInstance().setAppDownloadInfo(getString(R.string.lbl_share_download_app, getString(
							R.string.application_name), getString(R.string.lbl_store_url, getPackageName())));
				}
			});
		}
	}

	/**
	 * Get the Api-key.
	 *
	 * @return Api-key.
	 */
	public String getApiKey() {
		return mApiKey;
	}

	/**
	 * @return How much times that the AdMob has shown before, it under App-process domain. When process killed, it
	 * recounts.
	 */
	public int getAdsShownTimes() {
		return mAdsShownTimes;
	}

	/**
	 * Set how much times that the AdMob has shown before, it under App-process domain.
	 *
	 * @param adsShownTimes
	 * 		Times that AdMob has shown.
	 */
	public void setAdsShownTimes(int adsShownTimes) {
		mAdsShownTimes = adsShownTimes;
	}

	/**
	 *
	 * @return The id of push-sender.
	 */
	public String getSenderId() {
		return mSenderId;
	}
}
