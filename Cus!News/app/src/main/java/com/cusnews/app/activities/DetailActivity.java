package com.cusnews.app.activities;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cusnews.R;
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
		ActivityDetailBinding binding = DataBindingUtil.setContentView(this, LAYOUT);
		Entry entry = (Entry) getIntent().getSerializableExtra(EXTRAS_ENTRY);
		String query = getIntent().getStringExtra(EXTRAS_QUERY);

		//Init adapter
		binding.setDetailPagerAdapter(new DetailPagerAdapter(this, getSupportFragmentManager(), entry, query));
	}
}
