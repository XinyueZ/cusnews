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
		 list.add(new Topic(lang, "global"  , R.string.setting_push_news));
		 list.add(new Topic(lang, "football" , R.string.setting_push_football));
		 list.add(new Topic(lang, "IT" , R.string.setting_push_internet));
		 list.add(new Topic(lang, "Google" , R.string.setting_push_google));
		 list.add(new Topic(lang, "Apple"  , R.string.setting_push_apple));
		 return list;
	 }
}
