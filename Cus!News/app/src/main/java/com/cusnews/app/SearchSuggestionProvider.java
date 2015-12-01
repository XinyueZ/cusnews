package com.cusnews.app;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Saves the recent search queries.
 *
 * @author glamprecht
 * @see http://developer.android.com/guide/topics/search/adding-recent-query-suggestions .html
 */
public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {
	public final static  int    MODE      = DATABASE_MODE_QUERIES;
	private final static String AUTHORITY = "com.cusnews.app.SearchSuggestionProvider";

	public SearchSuggestionProvider() {
		setupSuggestions( AUTHORITY, MODE );
	}
}
