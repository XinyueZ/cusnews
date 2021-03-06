package com.cusnews.app.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.hardware.display.DisplayManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.chopping.utils.Utils;
import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.bus.ShareFBEvent;
import com.cusnews.databinding.DetailInfoBinding;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Prefs;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Show basic information of a news.
 *
 * @author Xinyue Zhao
 */
public final class DetailInfoFragment extends CusNewsFragment {
	private static final String EXTRAS_ENTRY = DetailInfoFragment.class.getName() + ".EXTRAS.entry";
	private static final String EXTRAS_QUERY = DetailInfoFragment.class.getName() + ".EXTRAS.query";
	/**
	 * Main layout for this component.
	 */
	private static final int    LAYOUT       = R.layout.fragment_detail_info;
	/**
	 * A tinyurl to the {@link Entry}.
	 */
	private String mSharedEntryUrl;

	private DetailInfoBinding mBinding;

	@Override
	public void onCreate( @Nullable Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );


		if( savedInstanceState != null ) {
			mSharedEntryUrl = savedInstanceState.getString( "tinyurl" );
		} else {
			final Entry entry = (Entry) getArguments().getSerializable( EXTRAS_ENTRY );
			Api.getTinyUrl( entry.getUrl(), new Callback<Response>() {
				@Override
				public void success( Response response, retrofit.client.Response response2 ) {
					mSharedEntryUrl = TextUtils.isEmpty( response.getResult() ) ? entry.getUrl() : response.getResult();
					createShare( entry );
				}

				@Override
				public void failure( RetrofitError error ) {
					mSharedEntryUrl = entry.getUrl();
					createShare( entry );
				}
			} );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		return inflater.inflate( LAYOUT, container, false );
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState ) {
		super.onViewCreated( view, savedInstanceState );
		final Entry entry = (Entry) getArguments().getSerializable( EXTRAS_ENTRY );
		if( entry != null ) {
			mBinding = DataBindingUtil.bind( view.findViewById( R.id.coordinator_layout ) );
			mBinding.setEntry( entry );
			mBinding.setQuery( getArguments().getString( EXTRAS_QUERY ) );


			//Init actionbar
			mBinding.toolbar.setNavigationIcon( R.drawable.ic_arrow_back_white_24dp );
			mBinding.toolbar.setNavigationOnClickListener( new OnClickListener() {
				@Override
				public void onClick( View v ) {
					ActivityCompat.finishAfterTransition( getActivity() );
				}
			} );


			mBinding.collapsingToolbar.setTitle( entry.getDomain() );

			//Init ImageView
			DisplayMetrics metrics = new DisplayMetrics();
			DisplayManagerCompat.getInstance( App.Instance ).getDisplay( 0 ).getMetrics( metrics );
			mBinding.thumbIv.getLayoutParams().height = metrics.heightPixels / 2;


			mBinding.thumbRl.hide();
			mBinding.thumbRl.post( new Runnable() {
				@Override
				public void run() {
					mBinding.thumbRl.show( 1500 );
				}
			} );
		}
	}

	/**
	 * Initialize an {@link  DetailInfoFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 * @param entry
	 * 		A news {@link Entry}.
	 * @param query
	 * 		The query to the {@code entry}.
	 *
	 * @return An instance of {@link DetailInfoFragment}.
	 */
	public static DetailInfoFragment newInstance( Context context, Entry entry, @Nullable String query ) {
		Bundle args = new Bundle();
		args.putSerializable( EXTRAS_ENTRY, entry );
		args.putSerializable( EXTRAS_QUERY, query );
		return (DetailInfoFragment) Fragment.instantiate( context, DetailInfoFragment.class.getName(), args );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		outState.putString( "tinyurl", mSharedEntryUrl );
	}

	private void createShare( Entry entry ) {
		mBinding.toolbar.inflateMenu( R.menu.menu_detail );
		MenuItem menuShare = mBinding.toolbar.getMenu().findItem( R.id.action_share );
		android.support.v7.widget.ShareActionProvider provider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(
				menuShare );

		String subject = App.Instance.getString(
				R.string.lbl_share_entry_title, App.Instance.getString( R.string.application_name ), entry.getTitle() );
		String text = App.Instance.getString( R.string.lbl_share_entry_content, entry.getKwic(), mSharedEntryUrl,
											  Prefs.getInstance().getAppDownloadInfo()
		);

		provider.setShareIntent( Utils.getDefaultShareIntent( provider, subject, text ) );


		createFBShare( entry );
	}


	private void createFBShare( final Entry entry ) {
		MenuItem menuItem = mBinding.toolbar.getMenu().findItem( R.id.action_fb );
		menuItem.setOnMenuItemClickListener( new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick( MenuItem item ) {
				EventBus.getDefault().post( new ShareFBEvent( entry, mSharedEntryUrl ) );
				return true;
			}
		} );
	}

}
