package com.cusnews.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.chopping.application.LL;
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
import com.squareup.picasso.Picasso;

/**
 * Login on Google.
 *
 * @author Xinyue Zhao
 */
public final class ConnectGoogleActivity extends AppCompatActivity {
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
		mBinding.googleLoginBtn.setSize(SignInButton.SIZE_WIDE);
		mGoogleApiClient = new GoogleApiClient.Builder(App.Instance, new GoogleApiClient.ConnectionCallbacks() {
			@Override
			public void onConnected(Bundle bundle) {
				String account = Plus.AccountApi.getAccountName(mGoogleApiClient);
				LL.d("G-Account:" + account);
				Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(
						new ResultCallback<LoadPeopleResult>() {
							@Override
							public void onResult(LoadPeopleResult loadPeopleResult) {
								if (loadPeopleResult.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
									Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
									if (person != null) {
										Picasso picasso = Picasso.with(App.Instance);
										if (person.getImage() != null && person.getImage().hasUrl()) {
											picasso.load(Utils.uriStr2URI(person.getImage().getUrl()).toASCIIString())
													.into(mBinding.thumbIv);
										}
										mBinding.helloTv.setText(getString(R.string.lbl_hello,
												person.getDisplayName()));

										Prefs.getInstance().setGoogleId(person.getId());
										LL.d("G-Id:" + person.getId());

										mBinding.loginPb.setVisibility(View.GONE);
										mBinding.closeBtn.setVisibility(View.VISIBLE);
									}
								} else {
									com.chopping.utils.Utils.showShortToast(App.Instance,
											"no person, status: " + loadPeopleResult.getStatus());
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
				//TODO Remove progress
				if (connectionResult.hasResolution()) {
					try {
						connectionResult.startResolutionForResult(ConnectGoogleActivity.this, REQUEST_CODE_RESOLVE_ERR);
					} catch (SendIntentException e) {
						mGoogleApiClient.connect();
					}
				} else {
					// TODO Snack information
					// mSnackBar.show(getString(R.string.lbl_login_fail));
				}
			}
		}).addApi(Plus.API, PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();

		mBinding.googleLoginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBinding.googleLoginBtn.setVisibility(View.GONE);
				mBinding.loginPb.setVisibility(View.VISIBLE);
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
			//Show Progress....
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
}
