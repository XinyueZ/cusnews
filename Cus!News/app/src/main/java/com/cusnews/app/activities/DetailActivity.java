package com.cusnews.app.activities;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.app.adapters.DetailPagerAdapter;
import com.cusnews.databinding.ActivityDetailBinding;
import com.cusnews.ds.Entry;

/**
 * Detail news. Contains a general detail view and a {@link android.webkit.WebView}.
 *
 * @author Xinyue Zhao
 */
public final class DetailActivity extends CusNewsActivity   {
	private static final String EXTRAS_QUERY = DetailActivity.class.getName() + ".EXTRAS.query";
	private static final String EXTRAS_ENTRY = DetailActivity.class.getName() + ".EXTRAS.entry";
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_detail;
	/**
	 * Data-binding.
	 */
	private ActivityDetailBinding mBinding;

	/**
	 * Show single instance of {@link DetailActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 * @param entry
	 * 		A news {@link Entry}.
	 * 	@param query
	 * 	The query to the {@code entry}.
	 */
	public static void showInstance(Context cxt, Entry entry, @Nullable String query) {
		Intent intent = new Intent(cxt, DetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(EXTRAS_ENTRY, (Serializable) entry);
		intent.putExtra(EXTRAS_QUERY, query == null ? "" :query);
		cxt.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, LAYOUT);
		Entry entry = (Entry) getIntent().getSerializableExtra(EXTRAS_ENTRY);
		String query = getIntent().getStringExtra(EXTRAS_QUERY);

		//Init adapter
		mBinding.setEntry(entry);
		mBinding.setDetailPagerAdapter(new DetailPagerAdapter(this, getSupportFragmentManager(), entry, query));

		//Init actionbar
		setSupportActionBar(mBinding.toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mBinding.collapsingToolbar.setTitle(entry.getDomain());

		//Init ImageView
		ViewCompat.setElevation(mBinding.thumbIv, getResources().getDimensionPixelSize(R.dimen.common_elevation));
		DisplayMetrics metrics = new DisplayMetrics();
		DisplayManagerCompat.getInstance(App.Instance).getDisplay(0).getMetrics(metrics);
		mBinding.thumbIv.getLayoutParams().height = metrics.heightPixels / 2;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_detail, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			ActivityCompat.finishAfterTransition(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}




}
