package com.cusnews.bus;

import com.cusnews.ds.Entry;

/**
 * Open detail of a news {@link com.cusnews.ds.Entry}.
 *
 * @author Xinyue Zhao
 */
public final class OpenEntryEvent {
	private Entry mEntry;

	public OpenEntryEvent( Entry entry ) {
		mEntry = entry;
	}


	public Entry getEntry() {
		return mEntry;
	}
}
