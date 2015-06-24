package com.cusnews.utils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.ds.TabLabel;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * A manager to control adding, removing, loading {@link android.support.design.widget.TabLayout.Tab}s.
 *
 * @author Xinyue Zhao
 */
public class TabLabelManager {
	public interface TabLabelManagerUIHelper {
		/**
		 * Add customized , default, first {@link Tab}.
		 */
		void addDefaultTab();

		/**
		 * Add customized  {@link Tab}.
		 *
		 * @param tabLabel
		 * 		{@link TabLabel}.
		 *
		 * @return The added new {@link Tab}.
		 */
		Tab addTab(TabLabel tabLabel);

		/**
		 * Remove a {@link Tab} from {@link Tab}s.
		 *
		 * @param tab
		 * 		{@link Tab}
		 */
		void removeTab(Tab tab);
	}

	/**
	 * Cached list of all {@link TabLabel}s from backend.
	 */
	private List<TabLabel> mCachedTabLabels = new LinkedList<>();
	/**
	 * Singleton.
	 */
	private static TabLabelManager sInstance = new TabLabelManager();

	private Handler mHandler = new Handler();
	/**
	 * @return The instance of singleton pattern.
	 */
	public static TabLabelManager getInstance() {
		return sInstance;
	}

	/**
	 * No one can create this class.
	 */
	private TabLabelManager() {
	}

	/**
	 * For initialize the {@link TabLayout} when host {@link android.app.Activity} is being created.
	 *
	 * @param helper
	 * 		{@link TabLabelManagerUIHelper}.
	 * @param loadDefault
	 * 		{@code true} if the first default will also be loaded.
	 */
	public void init(final TabLabelManagerUIHelper helper, boolean loadDefault) {
		//Default page.
		if (loadDefault) {
			helper.addDefaultTab();
		}
		//Load from cache.
		for (TabLabel cached : mCachedTabLabels) {
			helper.addTab(cached);
		}
		//Load from backend and refresh tabs.
		BmobQuery<TabLabel> queryTabLabels = new BmobQuery<>();
		queryTabLabels.addWhereEqualTo("mUID", Prefs.getInstance().getGoogleId());
		queryTabLabels.findObjects(App.Instance, new FindListener<TabLabel>() {
			@Override
			public void onSuccess(List<TabLabel> list) {
				for (TabLabel tabLabel : list) {
					boolean found = false;
					for (TabLabel cached : mCachedTabLabels) {
						if (cached.equals(tabLabel)) {
							found = true;
							break;
						}
					}
					if (!found) {
						mCachedTabLabels.add(tabLabel);
						helper.addTab(tabLabel);
					}

				}
			}

			@Override
			public void onError(int i, String s) {


			}
		});

	}

	/**
	 * Add a new {@link TabLabel}.
	 *
	 * @param newTabLabel
	 * 		The  new {@link TabLabel}.
	 * @param helper
	 * 		Use helper to refresh UI before removing  {@link TabLabel}.
	 * @param viewForSnack
	 * 		The anchor for {@link Snackbar} for result-messages.
	 *
	 * @return A {@link Tab} that hosts the new {@link TabLabel}. It might be {@code null} if the {@code newTabLabel}
	 * has same wording(label) equal to label of an existing {@link TabLabel} in  {@link #mCachedTabLabels}.
	 */
	public
	@Nullable
	Tab addNewRemoteTab(TabLabel newTabLabel, TabLabelManagerUIHelper helper, View viewForSnack) {
		//Same label should not be added again.
		for (TabLabel cached : mCachedTabLabels) {
			if (cached.equals(newTabLabel)) {
				Snackbar.make(viewForSnack, viewForSnack.getContext().getString(R.string.lbl_sync_same_label,
						newTabLabel.getLabel()), Snackbar.LENGTH_SHORT).show();
				return null;
			}
		}
		final Tab tab = helper.addTab(newTabLabel);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				tab.select();
			}
		}, 300);
		mCachedTabLabels.add(newTabLabel);
		addNewRemoteTabInternal(newTabLabel, viewForSnack);
		return tab;
	}

	/**
	 * Save a new {@link TabLabel} to backend.
	 *
	 * @param newTabLabel
	 * 		New {@link TabLabel}.
	 * @param viewForSnack
	 * 		The anchor for {@link Snackbar} for result-messages.
	 */
	private void addNewRemoteTabInternal(final TabLabel newTabLabel, View viewForSnack) {
		final WeakReference<View> anchor = new WeakReference<>(viewForSnack);
		newTabLabel.save(App.Instance, new SaveListener() {
			@Override
			public void onSuccess() {
				View anchorV = anchor.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, anchorV.getContext().getString(R.string.lbl_sync_label_added,
							newTabLabel.getLabel()), Snackbar.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int i, String s) {
				View anchorV = anchor.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, R.string.lbl_sync_fail, Snackbar.LENGTH_LONG).setAction(R.string.btn_retry,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									addNewRemoteTabInternal(newTabLabel, anchor.get());
								}
							}).show();
				}
			}
		});
	}

	/**
	 * Remove a {@link TabLabel} and its host {@link Tab}. It delete cached item and them remove from backend.
	 *
	 * @param tab
	 * 		{@link Tab} that hosts {@code tabLabel}.
	 * @param tabLabel
	 * 		{@link TabLabel}   to remove.
	 * @param helper
	 * 		Use helper to refresh UI before removing  {@link TabLabel}.
	 * @param viewForSnack
	 * 		The anchor for {@link Snackbar} for result-messages.
	 */
	public void removeRemoteTab(Tab tab, TabLabel tabLabel, TabLabelManagerUIHelper helper, View viewForSnack) {
		helper.removeTab(tab);
		for (TabLabel cached : mCachedTabLabels) {
			if (TextUtils.equals(cached.getObjectId(), tabLabel.getObjectId())) {
				mCachedTabLabels.remove(cached);
				removeRemoteTabInternal(tabLabel, viewForSnack);
				break;
			}
		}
	}

	/**
	 * Remove a  {@link TabLabel} from backend.
	 *
	 * @param tabLabel
	 * 		Existed {@link TabLabel}.
	 * @param viewForSnack
	 * 		The anchor for {@link Snackbar} for result-messages.
	 */
	private void removeRemoteTabInternal(final TabLabel tabLabel, View viewForSnack) {
		final WeakReference<View> anchor = new WeakReference<>(viewForSnack);
		tabLabel.delete(App.Instance, new DeleteListener() {
			@Override
			public void onSuccess() {
				View anchorV = anchor.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, R.string.lbl_sync_label_removed, Snackbar.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onFailure(int i, String s) {
				View anchorV = anchor.get();
				if (anchorV != null) {
					Snackbar.make(anchorV, R.string.lbl_sync_fail, Snackbar.LENGTH_LONG).setAction(R.string.btn_retry,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									removeRemoteTabInternal(tabLabel, anchor.get());
								}
							}).show();
				}
			}
		});
	}

	/**
	 * Clean all tabs.
	 */
	public void clean() {
		mCachedTabLabels.clear();
	}
}
