package com.cusnews.bus;

import com.cusnews.ds.Entry;

/**
 * Open detail of a related  {@link Entry}.
 *
 * @author Xinyue Zhao
 */
public final class OpenRelatedEvent {
	private Entry  mEntry;
	private String mKeyword;

	public OpenRelatedEvent( Entry entry, String keyword ) {
		mEntry = entry;
		mKeyword = keyword;
	}

	public Entry getEntry() {
		return mEntry;
	}

	public String getKeyword() {
		return mKeyword;
	}
}
