package com.cusnews.bus;


import com.cusnews.ds.Entry;

/**
 * Common and native sharing  FB event .
 *
 * @author Xinyue Zhao
 */
public final class ShareFBEvent {
	private Entry  mEntry;
	private String mShareLink;


	public ShareFBEvent( Entry entry, String shareLink ) {
		mEntry = entry;
		mShareLink = shareLink;
	}

	public Entry getEntry() {
		return mEntry;
	}

	public String getShareLink() {
		return mShareLink;
	}
}
