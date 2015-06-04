package com.cusnews.app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cusnews.R;
import com.github.johnpersano.supertoasts.SuperCardToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.SuperToast.Animations;
import com.github.johnpersano.supertoasts.SuperToast.Background;
import com.github.johnpersano.supertoasts.SuperToast.IconPosition;
import com.github.johnpersano.supertoasts.SuperToast.Type;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.github.johnpersano.supertoasts.util.Wrappers;

import de.greenrobot.event.EventBus;

/**
 * The basic {@link android.app.Activity} of application.
 *
 * @author Xinyue Zhao
 */
public class CusNewsActivity extends AppCompatActivity {

	/**
	 * Handler for {@link}.
	 *
	 * @param e
	 * 		Event {@link}.
	 */
	public void onEvent(Object e) {

	}
	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Wrappers wrappers = new Wrappers();
		//		wrappers.add(onClickWrapper);
		//		wrappers.add(onDismissWrapper);
		SuperCardToast.onRestoreState(savedInstanceState, this, wrappers);
	}


	protected void showWarningToast(String text, SuperToast.OnClickListener clickListener) {
		SuperCardToast toast = new SuperCardToast(this, Type.BUTTON);
		toast.setAnimations(Animations.POPUP);
		toast.setBackground(Background.BLUE);
		toast.setText(text);
		toast.setDuration(5000);
		toast.setButtonText("Ok");
		toast.setOnClickWrapper(new OnClickWrapper("showWarningToast", clickListener));
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(SuperToast.Icon.Dark.INFO, IconPosition.LEFT);
		toast.show();
	}

	protected void showErrorToast(String text, SuperToast.OnClickListener clickListener) {
		SuperCardToast toast = new SuperCardToast(this, Type.BUTTON);
		toast.setAnimations(Animations.FADE);
		toast.setBackground(Background.RED);
		toast.setText(text);
		toast.setDuration(10000);
		toast.setButtonText(getString(R.string.lbl_retry));
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(SuperToast.Icon.Dark.INFO, IconPosition.LEFT);
		toast.setOnClickWrapper(new OnClickWrapper("showErrorToast", clickListener));
		toast.show();
	}

	protected void showInfoToast(String text) {
		SuperCardToast toast = new SuperCardToast(this, Type.STANDARD);
		toast.setAnimations(Animations.FLYIN);
		toast.setBackground(Background.GREEN);
		toast.setText(text);
		toast.setDuration(5000);
		toast.setTextColor(getResources().getColor(R.color.common_white));
		toast.setIcon(SuperToast.Icon.Dark.INFO, IconPosition.LEFT);
		toast.show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SuperCardToast.onSaveState(outState);

	}
}
