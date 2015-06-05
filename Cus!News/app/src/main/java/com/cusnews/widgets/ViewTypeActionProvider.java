package com.cusnews.widgets;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnDismissListener;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.bus.ChangeViewTypeEvent;

import de.greenrobot.event.EventBus;

/**
 * A popup on app-bar for selection of view-types: horizontal, vertical, or grid.
 *
 * @author Xinyue Zhao
 */
public final class ViewTypeActionProvider extends ActionProvider implements OnDismissListener, OnMenuItemClickListener {
	/**
	 * Layout Id for the provider.
	 */
	private static final int LAYOUT = R.layout.action_provider_view_type;
	/**
	 * Menu-resource of the popup.
	 */
	private static final int MENU_RES = R.menu.menu_view_type;
	/**
	 * A {@link android.view.View} for this provider.
	 */
	private View mProviderV;
	/**
	 * A popup with list of all different types.
	 */
	private PopupMenu mPopupMenu;
	/**
	 * Show/Hidden status of menu.
	 */
	private boolean mShow;

	/**
	 * Constructor of {@link ViewTypeActionProvider}
	 *
	 * @param context
	 * 		{@link Context}.
	 */
	public ViewTypeActionProvider(Context context) {
		super(context);
		mProviderV = LayoutInflater.from(context).inflate(LAYOUT, null, false);
		mProviderV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mShow) {
					mShow = true;
					mPopupMenu.show();
					updateMenuItems(mPopupMenu.getMenu());
				} else {
					mPopupMenu.dismiss();
				}
			}
		});
		mPopupMenu = new PopupMenu(context, mProviderV);
		mPopupMenu.inflate(MENU_RES);
		mPopupMenu.setOnDismissListener(this);
		mPopupMenu.setOnMenuItemClickListener(this);
		updateMenuItems(mPopupMenu.getMenu());
	}


	@Override
	public View onCreateActionView() {
		return mProviderV;
	}


	@Override
	public void onDismiss(PopupMenu popupMenu) {
		mShow = false;
	}

	@Override
	public boolean onMenuItemClick(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_view_type_horizontal:
			App.Instance.setViewType(ViewType.HORIZONTAL);
			break;
		case R.id.action_view_type_vertical:
			App.Instance.setViewType(ViewType.VERTICAL);
			break;
		}
		updateMenuItems(mPopupMenu.getMenu());
		EventBus.getDefault().post(new ChangeViewTypeEvent(App.Instance.getViewType()));
		return true;
	}

	/**
	 * Update check-status for menu.
	 *
	 * @param menu
	 * 		The host of all menu-items.
	 */
	private void updateMenuItems(Menu menu) {
		menu.findItem(R.id.action_view_type_horizontal).setChecked(ViewType.HORIZONTAL == App.Instance.getViewType());
		menu.findItem(R.id.action_view_type_vertical).setChecked(ViewType.VERTICAL == App.Instance.getViewType());
	}


	public enum ViewType {
		HORIZONTAL(R.layout.item_horizontal_entry), VERTICAL(R.layout.item_vertical_entry), GRID(-1);
		/**
		 * The view-type,  {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
		 */
		private int mLayoutResId;

		ViewType(int layoutResId) {
			mLayoutResId = layoutResId;
		}

		public int getLayoutResId() {
			return mLayoutResId;
		}
	}
}
