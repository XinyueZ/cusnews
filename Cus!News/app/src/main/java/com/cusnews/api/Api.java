package com.cusnews.api;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cusnews.ds.Entries;
import com.cusnews.ds.Trends;
import com.squareup.okhttp.OkHttpClient;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Api to get all Faroo-feeds.
 *
 * @author Xinyue Zhao
 */
public final class Api {
	/**
	 * For header, cache before request will be out.
	 */
	private final static RequestInterceptor sInterceptor = new RequestInterceptor() {
		@Override
		public void intercept(RequestFacade request) {
			request.addHeader("Content-Type", "application/json");
		}
	};
	private static final String TAG = Api.class.getSimpleName();
	/**
	 * Response-cache.
	 */
	private static com.squareup.okhttp.Cache sCache;
	/**
	 * The host of API.
	 */
	private static String sHost = null;
	/**
	 * Response-cache size with default value.
	 */
	private static long sCacheSize = 1024 * 10;

	/**
	 * Http-client.
	 */
	private static OkClient sClient = null;
	/**
	 * API methods.
	 */
	private static S s;

	/**
	 * Init the http-client and cache.
	 */
	private static void initClient(Context cxt) {
		// Create an HTTP client that uses a cache on the file system. Android applications should use
		// their Context to get a cache directory.
		OkHttpClient okHttpClient = new OkHttpClient();
		File cacheDir = new File(cxt != null ? cxt.getCacheDir().getAbsolutePath() : System.getProperty(
				"java.io.tmpdir"), UUID.randomUUID().toString());
		try {
			sCache = new com.squareup.okhttp.Cache(cacheDir, sCacheSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		okHttpClient.setCache(sCache);
		okHttpClient.setReadTimeout(3600, TimeUnit.SECONDS);
		okHttpClient.setConnectTimeout(3600, TimeUnit.SECONDS);
		sClient = new OkClient(okHttpClient);
	}

	/**
	 * Init the http-client and cache.
	 */
	private static void initClient() {
		initClient(null);
	}

	/**
	 * To initialize API.
	 *
	 * @param host
	 * 		The host of API.
	 * @param cacheSz
	 * 		Response-cache size .
	 */
	public static void initialize(Context cxt, String host, long cacheSz) {
		sHost = host;
		sCacheSize = cacheSz;
		initClient(cxt);
	}


	/**
	 * To initialize API.
	 *
	 * @param cacheSz
	 * 		Response-cache size.
	 */
	public static void initialize(Context cxt, long cacheSz) {
		sCacheSize = cacheSz;
		initClient(cxt);
	}


	/**
	 * To initialize API.
	 *
	 * @param host
	 * 		The host of API.
	 */
	public static void initialize(Context cxt, String host) {
		sHost = host;
		initClient(cxt);
	}

	/**
	 * Api port.
	 */
	static private interface S {
		/**
		 * API method to get all news-entries.
		 *
		 * @param query
		 * 		The keyword to query.
		 * @param start
		 * 		Page.
		 * @param lang
		 * 		Language.
		 * @param src
		 * 		Different type: News: news, Search: web, Topics: topics
		 * @param key
		 * 		The API-key.
		 * @param callback
		 * 		The callback after getting feeds.
		 */
		@GET("/api?length=10&f=json&c=true")
		void getEntries(
				@Query("q") String  query,
				@Query("start") int  start,
				@Query("l") String  lang,
				@Query("src") String  src,
				@Query(value = "key" , encodeName = false, encodeValue=false) String  key,
				Callback<Entries> callback);
		/**
		 * API method to get top hot-queries.
		 *
		 * @param query
		 * 		The keyword to query.
		 * @param lang
		 * 		Language.
		 * @param key
		 * 		The API-key.
		 * @param callback
		 * 		The callback after getting feeds.
		 */
		@GET("/api?length=10&f=json&c=true&src=trends&start=1")
		void getTopTrends(
				@Query("q") String  query,
				@Query("l") String  lang,
				@Query(value = "key" , encodeName = false, encodeValue=false) String  key,
				Callback<Trends> callback);
	}

	/**
	 * API method to get all news-entries.
	 *
	 * @param query
	 * 		The keyword to query.
	 * @param start
	 * 		Page.
	 * @param lang
	 * 		Language: en(English), de(German), zh(Chinese), the default is en(English).
	 * @param src
	 * 		Different type: News: news, Search: web, Topics: topics
	 * @param key
	 * 		The API-key.
	 * @param callback
	 * 		The callback after getting feeds.
	 */
	public static final void getEntries(
		  String  query,
	      int  start,
		  String  lang,
			String  src,
		  String  key,
		  Callback<Entries> callback){
		assertCall();
		if (s == null) {
			RestAdapter adapter = new RestAdapter.Builder().setClient(sClient).setRequestInterceptor(
					sInterceptor).setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(sHost).build();
			s = adapter.create(S.class);
		}
		if(TextUtils.isEmpty(lang)) {
			lang = "en";
		}
		s.getEntries(
				  query,
				  start,
				  lang,
				  src,
				  key,
				  callback);
	}



	/**
	 * API method to get top hot-queries.
	 *
	 * @param query
	 * 		The keyword to query.
	 * @param lang
	 * 		Language: en(English), de(German), zh(Chinese), the default is en(English).
	 * @param key
	 * 		The API-key.
	 * @param callback
	 * 		The callback after getting feeds.
	 */
	public static final void getTopTrends(
			String  query,
			String  lang,
			String  key,
			Callback<Trends> callback){
		assertCall();
		if (s == null) {
			RestAdapter adapter = new RestAdapter.Builder().setClient(sClient).setRequestInterceptor(
					sInterceptor).setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(sHost).build();
			s = adapter.create(S.class);
		}
		if(TextUtils.isEmpty(lang)) {
			lang = "en";
		}
		s.getTopTrends(query, lang, key, callback);
	}



	/**
	 * Assert before calling api.
	 */
	private static void assertCall() {
		if (sClient == null) {//Create http-client when needs.
			initClient();
		}
		if (sHost == null) {//Default when needs.
			sHost = "http://www.faroo.com/";
		}
		Log.i(TAG, String.format("Host:%s, Cache:%d", sHost, sCacheSize));
		if (sCache != null) {
			Log.i(TAG, String.format("RequestCount:%d", sCache.getRequestCount()));
			Log.i(TAG, String.format("NetworkCount:%d", sCache.getNetworkCount()));
			Log.i(TAG, String.format("HitCount:%d", sCache.getHitCount()));
		}
	}

}
