package com.cusnews.app.adapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.cusnews.R;
import com.cusnews.bus.OpenEntryEvent;
import com.cusnews.ds.Entry;
import com.cusnews.utils.Utils;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

public final class EntriesAdapterBinder {


	@SuppressWarnings("unchecked")
	@BindingAdapter("entriesAdapter")
	public static void setEntriesBinder( RecyclerView recyclerView, RecyclerView.Adapter adp ) {
		recyclerView.setAdapter( adp );
	}


	@SuppressWarnings("unchecked")
	@BindingAdapter("entryClickListener")
	public static void setEntryClickListener( View view, final Entry entry ) {
		view.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				EventBus.getDefault()
						.post( new OpenEntryEvent( entry ) );
			}
		} );
	}


	//@BindingAdapter({ "bind:imageUrl", "bind:error" })
	//public static void loadImage(ImageView view, String url, Drawable error) {
	@BindingAdapter({ "imageUrl" })
	public static void loadImage( ImageView view, String url ) {
		if( TextUtils.isEmpty( url ) ) {
			url = "http://www.faroo.com/hp/api/faroo_attribution.png";
		}

		try {
			Picasso picasso = Picasso.with( view.getContext() );
			picasso.load( Utils.uriStr2URI( url )
							   .toASCIIString() )
				   .placeholder( R.drawable.placeholder )
				   .tag( view.getContext() )
				   .into( view );
		} catch( NullPointerException e ) {
			loadImage(
					view,
					"http://www.faroo.com/hp/api/faroo_attribution.png"
			);
		}
	}
}
