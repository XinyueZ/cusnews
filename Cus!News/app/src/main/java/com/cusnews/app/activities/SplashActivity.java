package com.cusnews.app.activities;

import android.Manifest.permission;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.cusnews.R;

import permissions.dispatcher.DeniedPermissions;
import permissions.dispatcher.NeedsPermissions;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AppCompatActivity {


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		// delegate the permission handling to generated method
		SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}


	@NeedsPermissions({permission.READ_PHONE_STATE, permission.WRITE_EXTERNAL_STORAGE})
	void getReadPhoneStatePermission() {
		MainActivity.showInstance(this);
	}


	@DeniedPermissions({permission.READ_PHONE_STATE, permission.WRITE_EXTERNAL_STORAGE})
	void noReadPhoneStatePermission() {
		Snackbar.make(findViewById(R.id.splash_v), R.string.msg_permission_prompt, Snackbar.LENGTH_INDEFINITE).setAction(
				R.string.btn_agree, new OnClickListener() {
					@Override
					public void onClick(View v) {
						ActivityCompat.finishAffinity(SplashActivity.this);
					}
				}).show();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		SplashActivityPermissionsDispatcher.getReadPhoneStatePermissionWithCheck(this);
	}
}
