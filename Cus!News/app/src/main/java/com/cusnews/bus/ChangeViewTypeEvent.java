package com.cusnews.bus;

import com.cusnews.widgets.ViewTypeActionProvider.ViewType;

/**
 * Change view-type. {@link com.cusnews.widgets.ViewTypeActionProvider.ViewType}.
 *
 * @author Xinyue Zhao
 */
public final class ChangeViewTypeEvent {
	private ViewType mViewType;

	public ChangeViewTypeEvent(ViewType viewType) {
		mViewType = viewType;
	}

	public ViewType getViewType() {
		return mViewType;
	}
}
