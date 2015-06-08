package com.cusnews.app.activities;

import java.io.Serializable;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.cusnews.R;
import com.cusnews.app.adapters.DetailPagerAdapter;
import com.cusnews.databinding.ActivityDetailBinding;
import com.cusnews.ds.Entry;

/**
 * Detail news. Contains a general detail view and a {@link android.webkit.WebView}.
 *
 * @author Xinyue Zhao
 */
public final class DetailActivity extends CusNewsActivity {
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
	 */
	public static void showInstance(Context cxt, Entry entry) {
		Intent intent = new Intent(cxt, DetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(EXTRAS_ENTRY, (Serializable) entry);
		cxt.startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBinding = DataBindingUtil.setContentView(this, LAYOUT);

		//Init adapter
		mBinding.setDetailPagerAdapter(new DetailPagerAdapter(this, getSupportFragmentManager(), (Entry)getIntent().getSerializableExtra(EXTRAS_ENTRY)));
	}
}
