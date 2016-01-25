package com.cusnews.app.activities;

import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.chopping.activities.BaseActivity;
import com.chopping.application.BasicPrefs;
import com.cusnews.R;
import com.cusnews.app.fragments.AboutDialogFragment.EulaConfirmationDialog;
import com.cusnews.bus.ShareFBEvent;
import com.cusnews.utils.Prefs;
import com.facebook.FacebookException;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * The basic {@link android.app.Activity} of application.
 *
 * @author Xinyue Zhao
 */
public class CusNewsActivity extends BaseActivity {
	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------


	/**
	 * Handler for {@link ShareFBEvent}.
	 *
	 * @param e
	 * 		Event {@link ShareFBEvent}.
	 */
	public void onEvent( final ShareFBEvent e ) {
		Bundle postParams = new Bundle();
		final WebDialog fbDlg = new WebDialog.FeedDialogBuilder( CusNewsActivity.this, getString( R.string.applicationId ), postParams )
				.setCaption(
						e.getEntry().getTitle() ).setDescription( e.getEntry().getKwic() ).setLink( e.getShareLink() ).build();
		fbDlg.setOnCompleteListener( new OnCompleteListener() {
			@Override
			public void onComplete( Bundle bundle, FacebookException e ) {
				fbDlg.dismiss();
			}
		} );
		fbDlg.show();
	}

	//------------------------------------------------


	/**
	 * Show  {@link android.support.v4.app.DialogFragment}.
	 *
	 * @param _dlgFrg
	 * 		An instance of {@link android.support.v4.app.DialogFragment}.
	 * @param _tagName
	 * 		Tag name for dialog, default is "dlg". To grantee that only one instance of {@link android.support.v4.app.DialogFragment} can been seen.
	 */
	protected void showDialogFragment( DialogFragment _dlgFrg, String _tagName ) {
		try {
			if( _dlgFrg != null ) {
				DialogFragment      dialogFragment = _dlgFrg;
				FragmentTransaction ft             = getSupportFragmentManager().beginTransaction();
				// Ensure that there's only one dialog to the user.
				Fragment prev = getSupportFragmentManager().findFragmentByTag( "dlg" );
				if( prev != null ) {
					ft.remove( prev );
				}
				try {
					if( TextUtils.isEmpty( _tagName ) ) {
						dialogFragment.show( ft, "dlg" );
					} else {
						dialogFragment.show( ft, _tagName );
					}
				} catch( Exception _e ) {
				}
			}
		} catch( Exception _e ) {
		}
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayService();
	}

	/**
	 * To confirm whether the validation of the Play-service of Google Inc.
	 */
	private void checkPlayService() {
		final int isFound = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
		if( isFound == ConnectionResult.SUCCESS ) {//Ignore update.
			//The "End User License Agreement" must be confirmed before you use this application.
			if( !Prefs.getInstance().isEULAOnceConfirmed() ) {
				showDialogFragment( new EulaConfirmationDialog(), null );
			}
		} else {
			new Builder( this ).setTitle( R.string.application_name ).setMessage( R.string.lbl_play_service ).setCancelable( false )
					.setPositiveButton( R.string.btn_yes, new DialogInterface.OnClickListener() {
						public void onClick( DialogInterface dialog, int whichButton ) {
							dialog.dismiss();
							Intent intent = new Intent( Intent.ACTION_VIEW );
							intent.setData( Uri.parse( getString( R.string.play_service_url ) ) );
							try {
								startActivity( intent );
							} catch( ActivityNotFoundException e0 ) {
								intent.setData( Uri.parse( getString( R.string.play_service_web ) ) );
								try {
									startActivity( intent );
								} catch( Exception e1 ) {
									//Ignore now.
								}
							} finally {
								finish();
							}
						}
					} ).create().show();
		}
	}
}
