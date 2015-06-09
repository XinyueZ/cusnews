package com.cusnews.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.View;

import com.cusnews.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * See. <a href="http://stackoverflow.com/questions/15617210/android-html-fromhtml-with-imagesF">Stackoverflow</a>
 */
public final class URLImageParser implements ImageGetter {
	Context c;
	View container;
	int size;

	/**
	 * Construct the URLImageParser which will execute AsyncTask and refresh the container
	 *
	 * @param t
	 * @param c
	 */
	public URLImageParser(Context c, View t) {
		this.c = c;
		this.container = t;
		size = c.getResources().getDimensionPixelSize(R.dimen.msg_net_icon_size);
	}

	public Drawable getDrawable(String source) {
		URLDrawable urlDrawable = new URLDrawable();

		// get the actual source
		ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);

		asyncTask.execute(source);

		// return reference to URLDrawable where I will change with actual image from
		// the src tag
		return urlDrawable;
	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
		URLDrawable urlDrawable;

		public ImageGetterAsyncTask(URLDrawable d) {
			this.urlDrawable = d;
		}

		@Override
		protected Drawable doInBackground(String... params) {
			String source = params[0];
			return fetchDrawable(source);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			// set the correct bound according to the result from HTTP call
			urlDrawable.setBounds(0, 0, size, size);

			// change the reference of the current drawable to the result
			// from the HTTP call
			urlDrawable.drawable = result;

			// redraw the image by invalidating the container
			URLImageParser.this.container.invalidate();
		}

		/**
		 * Get the Drawable from URL
		 *
		 * @param urlString
		 *
		 * @return
		 */
		public Drawable fetchDrawable(String urlString) {
			try {
				InputStream is = fetch(urlString);
				Drawable drawable = Drawable.createFromStream(is, "src");
				drawable.setBounds(0, 0, size, size);
				return drawable;
			} catch (Exception e) {
				return null;
			}
		}

		private InputStream fetch(String urlString) throws IOException {


			Request request = new Request.Builder().url(urlString).get().build();
			Response response = new OkHttpClient().newCall(request).execute();

			return response.body().byteStream();
		}
	}
}
