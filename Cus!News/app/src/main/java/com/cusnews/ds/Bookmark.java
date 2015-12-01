package com.cusnews.ds;


import android.text.TextUtils;

public final class Bookmark extends Entry {
	private String mUID;

	public Bookmark( String uid, Entry entry ) {
		super( entry.getTitle(), entry.getKwic(), entry.getContent(), entry.getUrl(), entry.getImageUrl(), entry.getDomain(), entry.getAuthor(),
			   entry.isNews(), entry.getVotes(), entry.getDate(), entry.getRelated()
		);
		mUID = uid;
	}

	public Bookmark( Entry entry ) {
		super( entry.getTitle(), entry.getKwic(), entry.getContent(), entry.getUrl(), entry.getImageUrl(), entry.getDomain(), entry.getAuthor(),
			   entry.isNews(), entry.getVotes(), entry.getDate(), entry.getRelated()
		);
	}

	public String getUID() {
		return mUID;
	}


	@Override
	public boolean equals( Object o ) {
		Entry other = (Entry) o;
		return TextUtils.equals( getUrl(), other.getUrl() );
	}
}
