package com.cusnews.ds;

import java.util.ArrayList;
import java.util.List;

import com.cusnews.R;
import com.cusnews.utils.Prefs;

/**
 * The topic-list can be subscribed as pushed by Google.
 *
 * @author Xinyue Zhao
 */
public final class TopicsFactory {
	/**
	 * Create the list of push-topics .
	 * @return {@link List} of {@link Topic}s.
	 */
	 public static  List<Topic> create() {
		 String lang = Prefs.getInstance().getLanguage();
		 List<Topic> list = new ArrayList<>();
		 list.add(new Topic(lang, "global"  , R.string.setting_push_news, Prefs.KEY_PUSH_NEWS));
		 list.add(new Topic(lang, "football" , R.string.setting_push_football, Prefs.KEY_PUSH_FOOTBALL));
		 list.add(new Topic(lang, "IT" , R.string.setting_push_internet, Prefs.KEY_PUSH_INTERNET));
		 list.add(new Topic(lang, "Google" , R.string.setting_push_google, Prefs.KEY_PUSH_GOOGLE));
		 list.add(new Topic(lang, "Apple"  , R.string.setting_push_apple, Prefs.KEY_PUSH_APPLE));
		 return list;
	 }

	/**
	 * Clear all subscription.
	 */
	public static void clear() {
		Prefs prefs = Prefs.getInstance();
		prefs.setPushSelections(null);
		prefs.setPush(Prefs.KEY_PUSH_NEWS, false);
		prefs.setPush(Prefs.KEY_PUSH_FOOTBALL, false);
		prefs.setPush(Prefs.KEY_PUSH_INTERNET, false);
		prefs.setPush(Prefs.KEY_PUSH_GOOGLE, false);
		prefs.setPush(Prefs.KEY_PUSH_APPLE, false);
	}
}
