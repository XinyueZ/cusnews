
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

import android.app.Application;

import com.cusnews.api.Api;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;

/**
 * Application object.
 *
 * @author Xinyue Zhao
 */
public final class App extends Application {
	public  static App Instance;
    /**
     * Api-key.
     */
    private String mApiKey;

    ///-----
    /**
     * TODO
     * Temp place to save view-types before {@link android.content.SharedPreferences} will be imported.
     */
    private ViewType mViewType = ViewType.VERTICAL;


    public ViewType getViewType() {
        return mViewType;
    }

    public void setViewType(ViewType viewType) {
        mViewType = viewType;
    }
    ///-----

    @Override
	public void onCreate() {
		super.onCreate();
		Instance = this;

        Properties prop = new Properties();
        InputStream input = null;
        String value = null;
        try {
			/*From "resources".*/
            input = getClassLoader().getResourceAsStream("key.properties");
            if (input != null) {
                // load a properties file
                prop.load(input);
                mApiKey = prop.getProperty("appkey");
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
	}

    /**
     * Get the Api-key.
     * @return Api-key.
     */
    public String getApiKey() {
        return mApiKey;
    }
}
