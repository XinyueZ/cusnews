package com.cusnews.app.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.ds.Entries;
import com.github.johnpersano.supertoasts.SuperToast.OnClickListener;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends CusNewsActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		Api.getEntries("", 1, "en", App.Instance.getApiKey(), new Callback<Entries>() {
			@Override
			public void success(Entries entries, Response response) {
				showInfoToast("Success");
			}

			@Override
			public void failure(RetrofitError error) {
				showErrorToast("RetrofitError", new OnClickListener() {
					@Override
					public void onClick(View view, Parcelable parcelable) {

					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
