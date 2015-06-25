package com.cusnews.utils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.bus.BookmarksInitEvent;
import com.cusnews.bus.BookmarksLoadingErrorEvent;
import com.cusnews.ds.Bookmark;
import com.cusnews.ds.Entry;
import com.software.shell.fab.ActionButton;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.greenrobot.event.EventBus;

/**
 * A manager to control adding, removing, loading {@link com.cusnews.ds.Bookmark}s.
 *
 * @author Xinyue Zhao
 */
public class BookmarksManager {

	/**
	 * Cached list of all {@link Bookmark}s from backend.
	 */
	private List<Bookmark> mCachedBookmarks = new LinkedList<>();
	/**
	 * Singleton.
	 */
	private static BookmarksManager sInstance = new BookmarksManager();
	/**
	 * {@code true} if init cache-list.
	 */
	private volatile boolean mInit;

	/**
	 * @return The instance of singleton pattern.
	 */
	public static BookmarksManager getInstance() {
		return sInstance;
	}


	/**
	 * No one can create this class.
	 */
	private BookmarksManager() {
	}

	/**
	 * For initialize the manger.
	 */
	public synchronized void init() {
		//Load from backend.
		BmobQuery<Bookmark> queryTabLabels = new BmobQuery<>();
		queryTabLabels.addWhereEqualTo("mUID", Prefs.getInstance().getGoogleId());
		queryTabLabels.findObjects(App.Instance, new FindListener<Bookmark>() {
			@Override
			public void onSuccess(List<Bookmark> list) {
				for (Bookmark bookmark : list) {
					boolean found = false;
					for (Bookmark cached : mCachedBookmarks) {
						if (cached.equals(bookmark)) {
							found = true;
							break;
						}
					}
					if (!found) {
						mCachedBookmarks.add(bookmark);
					}
				}
				mInit = true;
				EventBus.getDefault().post(new BookmarksInitEvent());
			}

			@Override
			public void onError(int i, String s) {
				mInit = false;
				EventBus.getDefault().post(new BookmarksLoadingErrorEvent());
			}
		});
	}

	/**
	 * To check whether the {@link Entry} had been bookmarked or not.
	 *
	 * @param entry
	 * 		{@link Entry}.
	 *
	 * @return {@code true} was  bookmarked.
	 */
	public boolean isBookmarked(Entry entry) {
		for (Bookmark cached : mCachedBookmarks) {
			if (cached.equals(entry)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * To check whether the {@link Entry} had been bookmarked or not.
	 *
	 * @param entry
	 * 		{@link Entry}.
	 *
	 * @return A {@link Bookmark} returned if {@code entry} was bookmarked before, otherwise {@code null} returns.
	 */
	@Nullable
	public Bookmark findBookmarked(Entry entry) {
		for (Bookmark cached : mCachedBookmarks) {
			if (cached.equals(entry)) {
				return cached;
			}
		}
		return null;
	}

	/**
	 * Add new {@link Bookmark} to remote backend.
	 * @param newBookmark A new {@link Bookmark}.
	 * @param btn {@link ActionButton} the button to fire the adding.
	 * @param viewForSnack {@link View} anchor for showing {@link Snackbar} messages.
	 */
	public void addNewRemoteBookmark(Bookmark newBookmark, ActionButton btn, View viewForSnack) {
		//Same bookmark should not be added again.
		for (Bookmark cached : mCachedBookmarks) {
			if (cached.equals(newBookmark)) {
				return;
			}
		}
		mCachedBookmarks.add(newBookmark);
		btn.setImageResource(R.drawable.ic_bookmarked);
		btn.setEnabled(false);
		addNewBookmarkInternal(newBookmark, btn, viewForSnack);
	}

	/**
	 * Add new {@link Bookmark} to backend.
	 * @param newBookmark A new {@link Bookmark}.
	 * @param btn {@link ActionButton} the button to fire the adding.
	 * @param viewForSnack {@link View} anchor for showing {@link Snackbar} messages.
	 */
	private void addNewBookmarkInternal(final Bookmark newBookmark, final ActionButton btn, View viewForSnack) {
		final WeakReference<View> anchor = new WeakReference<>(viewForSnack);
		final WeakReference<ActionButton> actionBtn = new WeakReference<>(btn);
		newBookmark.save(App.Instance, new SaveListener() {
			@Override
			public void onSuccess() {
				View anchorV = anchor.get();
				View btn = actionBtn.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, R.string.lbl_sync_success, Snackbar.LENGTH_SHORT).show();
				}
				if(btn!= null) {
					btn.setEnabled(true);
				}
			}

			@Override
			public void onFailure(int i, String s) {
				View anchorV = anchor.get();
				View btn = actionBtn.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, R.string.lbl_sync_fail, Snackbar.LENGTH_LONG).setAction(R.string.btn_retry,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									addNewBookmarkInternal(newBookmark, actionBtn.get(), anchor.get());
								}
							}).show();
				}
				if(btn!= null) {
					btn.setEnabled(true);
				}
			}
		});
	}

	/**
	 * Remove  a {@link Bookmark} from remote backend.
	 * @param bookmark An old {@link Bookmark}.
	 * @param btn {@link ActionButton} the button to fire the removing.
	 * @param viewForSnack {@link View} anchor for showing {@link Snackbar} messages.
	 */
	public void removeRemoteBookmark(Bookmark bookmark, ActionButton btn, View viewForSnack) {
		for (Bookmark cached : mCachedBookmarks) {
			if (cached.equals(bookmark)) {
				mCachedBookmarks.remove(cached);
				btn.setImageResource(R.drawable.ic_not_bookmarked);
				btn.setEnabled(false);
				removeBookmarkInternal(bookmark, btn, viewForSnack);
				break;
			}
		}
	}

	/**
	 * Remove a {@link Bookmark} from backend.
	 * @param bookmark An old {@link Bookmark}.
	 *                  @param btn {@link ActionButton} the button to fire the removing.
	 * @param viewForSnack {@link View} anchor for showing {@link Snackbar} messages.
	 */
	private void removeBookmarkInternal(final Bookmark bookmark, ActionButton btn, View viewForSnack) {
		final WeakReference<View> anchor = new WeakReference<>(viewForSnack);
		final WeakReference<ActionButton> actionBtn = new WeakReference<>(btn);
		bookmark.delete(App.Instance, new DeleteListener() {
			@Override
			public void onSuccess() {
				View anchorV = anchor.get();
				View btn = actionBtn.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, R.string.lbl_sync_success, Snackbar.LENGTH_SHORT).show();
				}
				if(btn!= null) {
					btn.setEnabled(true);
				}
			}

			@Override
			public void onFailure(int i, String s) {
				View anchorV = anchor.get();
				View btn = actionBtn.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, R.string.lbl_sync_fail, Snackbar.LENGTH_LONG).setAction(R.string.btn_retry,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									removeBookmarkInternal(bookmark, actionBtn.get(), anchor.get());
								}
							}).show();
				}
				if(btn!= null) {
					btn.setEnabled(true);
				}
			}
		});
	}

	/**
	 * Clean all tabs.
	 */
	public void clean() {
		mCachedBookmarks.clear();
	}

	/**
	 *
	 * @return {@code true} if init cache-list.
	 */
	public synchronized  boolean isInit() {
		return mInit;
	}

	/**
	 *
	 * @return All cached  {@link Bookmark}s.
	 */
	public List<Bookmark> getCachedBookmarks() {
		return mCachedBookmarks;
	}
}
