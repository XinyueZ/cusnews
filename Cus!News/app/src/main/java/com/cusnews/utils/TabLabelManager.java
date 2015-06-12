package com.cusnews.utils;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

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
	public interface TabLabelManagerHelper {
		/**
		 * Add customized , default, first {@link Tab}.
		 */
		void addDefaultTab();

		/**
		 * Add customized  {@link Tab}.
		 *
		 * @param tabLabel
		 * 		{@link TabLabel}.
		 */
		void addTab(TabLabel tabLabel);

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
	private List<TabLabel> mTabLabels = new LinkedList<>();
	/**
	 * Singleton.
	 */
	private static TabLabelManager sInstance = new TabLabelManager();

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
	 * 		{@link com.cusnews.utils.TabLabelManager.TabLabelManagerHelper}.
	 * @param loadDefault
	 * 		{@code true} if the first default will also be loaded.
	 */
	public void init(final TabLabelManagerHelper helper, boolean loadDefault) {
		//Default page.
		if (loadDefault) {
			helper.addDefaultTab();
		}
		//Load from cache.
		for (TabLabel cached : mTabLabels) {
			helper.addTab(cached);
		}
		//Load from backend and refresh tabs.
		try {
			/*To get all labels*/
			BmobQuery<TabLabel> queryTabLabels = new BmobQuery<>();
			queryTabLabels.addWhereEqualTo("mUID", DeviceUniqueUtil.getDeviceIdent(App.Instance));
			queryTabLabels.findObjects(App.Instance, new FindListener<TabLabel>() {
				@Override
				public void onSuccess(List<TabLabel> list) {
					for (TabLabel tabLabel : list) {
						boolean found = false;
						for (TabLabel cached : mTabLabels) {
							if (TextUtils.equals(cached.getObjectId(), tabLabel.getObjectId())) {
								found = true;
								break;
							}
						}
						if (!found) {
							mTabLabels.add(tabLabel);
							helper.addTab(tabLabel);
						}

					}
				}

				@Override
				public void onError(int i, String s) {


				}
			});
		} catch (NoSuchAlgorithmException e) {
			//TODO Error when can not get device id.
		}
	}


	public void addNewRemoteTab(final TabLabel newTabLabel, final TabLabelManagerHelper helper,
			final View viewForSnack) {
		helper.addTab(newTabLabel);
		mTabLabels.add(newTabLabel);
		addNewRemoteTabInternal(newTabLabel, viewForSnack);
	}


	private void addNewRemoteTabInternal(final TabLabel newTabLabel, final View viewForSnack) {
		newTabLabel.save(App.Instance, new SaveListener() {
			@Override
			public void onSuccess() {
				Snackbar.make(viewForSnack, viewForSnack.getContext().getString(R.string.lbl_sync_label_added,
						newTabLabel.getLabel()), Snackbar.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int i, String s) {
				Snackbar.make(viewForSnack, R.string.lbl_sync_fail, Snackbar.LENGTH_SHORT).setAction(R.string.btn_retry,
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								addNewRemoteTabInternal(newTabLabel, viewForSnack);
							}
						}).show();
			}
		});
	}


	public void removeRemoteTab(final Tab tab, final TabLabel tabLabel, final TabLabelManagerHelper helper,
			final View viewForSnack) {
		helper.removeTab(tab);
		for (TabLabel cached : mTabLabels) {
			if (TextUtils.equals(cached.getObjectId(), tabLabel.getObjectId())) {
				mTabLabels.remove(cached);
				removeRemoteTabInternal(tabLabel, viewForSnack);
				break;
			}
		}
	}


	private void removeRemoteTabInternal(final TabLabel tabLabel, final View viewForSnack) {
		tabLabel.delete(App.Instance, new DeleteListener() {
			@Override
			public void onSuccess() {
				Snackbar.make(viewForSnack, R.string.lbl_sync_label_removed, Snackbar.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int i, String s) {
				Snackbar.make(viewForSnack, R.string.lbl_sync_fail, Snackbar.LENGTH_SHORT).setAction(R.string.btn_retry,
						new OnClickListener() {
							@Override
							public void onClick(View v) { 
								removeRemoteTabInternal(tabLabel, viewForSnack);
							}
						}).show();
			}
		});
	}

}
