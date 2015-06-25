package com.cusnews.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.chopping.utils.Utils;
import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.databinding.ActivityConnectGoogleBinding;
import com.cusnews.utils.Prefs;
import com.cusnews.widgets.ViewTypeActionProvider.ViewType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.google.android.gms.plus.model.people.Person;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;

/**
 * Login on Google.
 *
 * @author Xinyue Zhao
 */
public final class ConnectGoogleActivity extends CusNewsActivity {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.activity_connect_google;
	/**
	 * Request-id of this  {@link android.app.Activity}.
	 */
	public static final int REQ = 0x91;
	/**
	 * Data-binding.
	 */
	private ActivityConnectGoogleBinding mBinding;
	/**
	 * The Google-API.
	 */
	private GoogleApiClient mGoogleApiClient;
	/**
	 * Connection-status.
	 */
	private ConnectionResult mConnectionResult;
	/**
	 * Login-error.
	 */
	private static int REQUEST_CODE_RESOLVE_ERR = 0x98;

	private boolean mStop;
	/**
	 * Show single instance of {@link ConnectGoogleActivity}
	 *
	 * @param cxt
	 * 		{@link Context}.
	 */
	public static void showInstance(Activity cxt) {
		Intent intent = new Intent(cxt, ConnectGoogleActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		ActivityCompat.startActivityForResult(cxt, intent, REQ, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mStop = false;
		ViewType vt = Prefs.getInstance().getViewType();
		switch (vt) {
		case GRID:
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		default:
			//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		}

		mBinding = DataBindingUtil.setContentView(this, LAYOUT);
		setUpErrorHandling((ViewGroup) findViewById(R.id.error_content));
		mBinding.googleLoginBtn.setSize(SignInButton.SIZE_WIDE);
		mBinding.helloTv.setText(getString(R.string.lbl_welcome, getString(R.string.application_name)));
		mGoogleApiClient = new GoogleApiClient.Builder(App.Instance, new GoogleApiClient.ConnectionCallbacks() {
			@Override
			public void onConnected(Bundle bundle) {
				//				String account = Plus.AccountApi.getAccountName(mGoogleApiClient);
				//				LL.d("G-Account:" + account);
				Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(
						new ResultCallback<LoadPeopleResult>() {
							@Override
							public void onResult(LoadPeopleResult loadPeopleResult) {
								if(!mStop) {
									if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
										Prefs prefs = Prefs.getInstance();
										Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
										if (person != null) {
											prefs.setGoogleId(person.getId());
											prefs.setGoogleDisplyName(person.getDisplayName());

											Picasso picasso = Picasso.with(App.Instance);
											if (person.getImage() != null && person.getImage().hasUrl()) {
												picasso.load(Utils.uriStr2URI(person.getImage().getUrl())
														.toASCIIString()).into(mBinding.thumbIv);
												prefs.setGoogleThumbUrl(person.getImage().getUrl());
											}
											ViewPropertyAnimator.animate(mBinding.thumbIv).cancel();
											ViewPropertyAnimator.animate(mBinding.thumbIv).alpha(1).setDuration(500)
													.start();


											mBinding.helloTv.setText(getString(R.string.lbl_hello, person.getDisplayName()));
											mBinding.loginPb.setVisibility(View.GONE);
											mBinding.closeBtn.setVisibility(View.VISIBLE);
											Animation shake = AnimationUtils.loadAnimation(App.Instance, R.anim.shake);
											mBinding.closeBtn.startAnimation(shake);
										}
									} else {
										com.chopping.utils.Utils.showShortToast(App.Instance,
												"no person, status: " + loadPeopleResult.getStatus());
									}
								}
							}
						});
			}

			@Override
			public void onConnectionSuspended(int i) {

			}
		}, new GoogleApiClient.OnConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult connectionResult) {
				if (connectionResult.hasResolution()) {
					try {
						connectionResult.startResolutionForResult(ConnectGoogleActivity.this, REQUEST_CODE_RESOLVE_ERR);
					} catch (SendIntentException e) {
						mGoogleApiClient.connect();
					}
				} else {
					Snackbar.make(mBinding.loginContentLl, R.string.lbl_loading_error, Snackbar.LENGTH_LONG).setAction(
							R.string.btn_close_app, new OnClickListener() {
						@Override
						public void onClick(View v) {
							ActivityCompat.finishAffinity(ConnectGoogleActivity.this);
						}
					}).show();
				}
			}
		}).addApi(Plus.API, PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();

		mBinding.googleLoginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBinding.googleLoginBtn.setVisibility(View.GONE);
				mBinding.loginPb.setVisibility(View.VISIBLE);
				mBinding.helloTv.setText(R.string.lbl_connect_google);
				ViewPropertyAnimator.animate(mBinding.thumbIv).cancel();
				ViewPropertyAnimator.animate(mBinding.thumbIv).alpha(0.3f).setDuration(500).start();
				loginGPlus();
			}
		});


		mBinding.closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				ActivityCompat.finishAfterTransition(ConnectGoogleActivity.this);
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
			mConnectionResult = null;
			mGoogleApiClient.connect();
		} else if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_CANCELED ) {
			mConnectionResult = null;
			mBinding.helloTv.setText(getString(R.string.lbl_welcome, getString(R.string.application_name)));
			mBinding.loginPb.setVisibility(View.GONE);
			ViewPropertyAnimator.animate(mBinding.thumbIv).cancel();
			ViewPropertyAnimator.animate(mBinding.thumbIv).alpha(1).setDuration(500)
					.start();
			mBinding.googleLoginBtn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onStop() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}


	/**
	 * Login Google+
	 */
	private void loginGPlus() {
		if (mConnectionResult == null) {
			mGoogleApiClient.connect();
		} else {
			try {
				mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mConnectionResult = null;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mStop = true;
	}
}
