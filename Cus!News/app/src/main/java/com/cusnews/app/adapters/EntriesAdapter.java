package com.cusnews.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.bus.ShareEvent;
import com.cusnews.bus.ShareFBEvent;
import com.cusnews.ds.Bookmark;
import com.cusnews.ds.Entry;
import com.cusnews.utils.BookmarksManager;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.DynamicShareActionProvider;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;
import com.tinyurl4j.Api;
import com.tinyurl4j.data.Response;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * The adapter for the list of {@link com.cusnews.ds.Entry}s.
 *
 * @author Xinyue Zhao
 */
public final class EntriesAdapter extends RecyclerView.Adapter<EntriesAdapter.ViewHolder> {
	/**
	 * The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 */
	private int         mLayoutResId;
	/**
	 * Data-source.
	 */
	private List<Entry> mEntries;
	/**
	 * Constructor of {@link EntriesAdapter}
	 *
	 * @param viewType
	 * 		The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 */
	public EntriesAdapter( ViewType viewType ) {
		setData( new ArrayList<Entry>() );
		mLayoutResId = viewType.getLayoutResId();
	}


	/**
	 * Constructor of {@link EntriesAdapter}
	 *
	 * @param viewType
	 * 		The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
	 * @param entries
	 * 		Data-source.
	 */
	public EntriesAdapter( ViewType viewType, List<Entry> entries ) {
		setData( entries );
		mLayoutResId = viewType.getLayoutResId();
	}

	/**
	 * @return Data-source.
	 */
	public List<Entry> getData() {
		return mEntries;
	}

	/**
	 * Set data-source.
	 *
	 * @param entries
	 */
	public void setData( List<Entry> entries ) {
		mEntries = entries;
	}

	@Override
	public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
		Context cxt = parent.getContext();
		//		boolean landscape = cxt.getResources().getBoolean(R.bool.landscape);
		LayoutInflater inflater = LayoutInflater.from( cxt );
		if( !Prefs.getInstance()
				  .showAllImages() ) {
			mLayoutResId = R.layout.item_vertical_no_image_entry;
		}
		ViewDataBinding binding = DataBindingUtil.inflate(
				inflater,
				mLayoutResId,
				parent,
				false
		);
		return new EntriesAdapter.ViewHolder( binding );
	}

	@Override
	public void onBindViewHolder( final ViewHolder holder, final int position ) {
		final Entry entry = getData().get( position );
		holder.mBinding.setVariable(
				BR.entry,
				entry
		);
		holder.mBinding.setVariable(
				BR.handler,
				new ItemHandler(
						holder,
						this
				)
		);
		holder.mBinding.executePendingBindings();
	}


	@Override
	public int getItemCount() {
		return mEntries == null ? 0 : mEntries.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private ViewDataBinding mBinding;
		private Toolbar         mToolbar;

		public ViewHolder( ViewDataBinding binding ) {
			super( binding.getRoot() );
			mBinding = binding;
			mToolbar = (Toolbar) binding.getRoot()
										.findViewById( R.id.entry_toolbar );
			if( mToolbar != null ) {
				mToolbar.inflateMenu( R.menu.menu_item );
			}
		}
	}


	public static final class ItemHandler {
		private ViewHolder     mViewHolder;
		private EntriesAdapter mAdapter;

		public ItemHandler( ViewHolder viewHolder, EntriesAdapter adapter ) {
			mViewHolder = viewHolder;
			mAdapter = adapter;


			if(mViewHolder.mToolbar != null) {
				int pos = mViewHolder.getAdapterPosition();
				final Entry entry = mAdapter.getData()
											.get( pos );
				boolean bookmarked = BookmarksManager.getInstance().isBookmarked( entry );
				MenuItem bookmarkMi = mViewHolder.mToolbar.getMenu()
														.findItem( R.id.action_bookmark_item )
														.setIcon( bookmarked ? R.drawable.ic_item_bookmarked : R.drawable.ic_item_not_bookmarked );
				bookmarkMi.setOnMenuItemClickListener( new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick( MenuItem item ) {
						Bookmark      bookmark = BookmarksManager.getInstance().findBookmarked( entry );
						if( bookmark != null ) {
							Bookmark delBookmark = new Bookmark( entry );
							delBookmark.setObjectId( bookmark.getObjectId() );
							BookmarksManager.getInstance().removeRemoteBookmark( delBookmark  );
							item.setIcon( R.drawable.ic_item_not_bookmarked   );
						} else {
							BookmarksManager.getInstance().addNewRemoteBookmark(
									new Bookmark( Prefs.getInstance().getGoogleId(), entry )  );
							item.setIcon( R.drawable.ic_item_bookmarked   );
						}
						return true;
					}
				} );
				MenuItem shareMi = mViewHolder.mToolbar.getMenu()
													   .findItem( R.id.action_share_item );
				DynamicShareActionProvider shareLaterProvider = (DynamicShareActionProvider) MenuItemCompat.getActionProvider( shareMi );
				shareLaterProvider.setShareDataType( "text/plain" );
				shareLaterProvider.setOnShareLaterListener( new DynamicShareActionProvider.OnShareLaterListener() {
					@Override
					public void onShareClick( final Intent shareIntent ) {
						Api.getTinyUrl(
								entry.getUrl(),
								new Callback<Response>() {
									@Override
									public void success( Response response, retrofit.client.Response response2 ) {
										String shortUrl = TextUtils.isEmpty( response.getResult() ) ? entry.getUrl() : response.getResult();
										String subject = App.Instance.getString(
												R.string.lbl_share_entry_title,
												App.Instance.getString( R.string.application_name ),
												entry.getTitle()
										);
										String text = App.Instance.getString(
												R.string.lbl_share_entry_content,
												entry.getKwic(),
												shortUrl,
												Prefs.getInstance()
													 .getAppDownloadInfo()
										);

										shareIntent.putExtra(
												Intent.EXTRA_SUBJECT,
												subject
										);
										shareIntent.putExtra(
												Intent.EXTRA_TEXT,
												text
										);
										EventBus.getDefault()
												.post( new ShareEvent( shareIntent ) );
									}

									@Override
									public void failure( RetrofitError error ) {
										String subject = App.Instance.getString(
												R.string.lbl_share_entry_title,
												App.Instance.getString( R.string.application_name ),
												entry.getTitle()
										);
										String text = App.Instance.getString(
												R.string.lbl_share_entry_content,
												entry.getKwic(),
												entry.getUrl(),
												Prefs.getInstance()
													 .getAppDownloadInfo()
										);

										shareIntent.putExtra(
												Intent.EXTRA_SUBJECT,
												subject
										);
										shareIntent.putExtra(
												Intent.EXTRA_TEXT,
												text
										);
										EventBus.getDefault()
												.post( new ShareEvent( shareIntent ) );
									}
								}
						);
					}
				} );


				MenuItem shareFBItem = mViewHolder.mToolbar.getMenu()
														   .findItem( R.id.action_fb_item );
				shareFBItem.setOnMenuItemClickListener( new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick( MenuItem item ) {
						Api.getTinyUrl(
								entry.getUrl(),
								new Callback<Response>() {
									@Override
									public void success( Response response, retrofit.client.Response response2 ) {
										String shortUrl = TextUtils.isEmpty( response.getResult() ) ? entry.getUrl() : response.getResult();
										EventBus.getDefault()
												.post( new ShareFBEvent(
														entry,
														shortUrl
												) );
									}

									@Override
									public void failure( RetrofitError error ) {
										EventBus.getDefault()
												.post( new ShareFBEvent(
														entry,
														entry.getUrl()
												) );
									}
								}
						);
						return true;
					}
				} );
			}
		}
	}
}
