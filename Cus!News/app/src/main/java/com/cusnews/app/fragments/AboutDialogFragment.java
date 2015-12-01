package com.cusnews.app.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

import com.cusnews.R;
import com.cusnews.app.activities.WebViewActivity;
import com.cusnews.bus.EULAConfirmedEvent;
import com.cusnews.bus.EULARejectEvent;
import com.cusnews.utils.Prefs;

import de.greenrobot.event.EventBus;


/**
 * Dialog popup a "About", include legal text, open source licenses etc.
 * <p/>
 * A lot inspired by App <a href="https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples
 * /apps/iosched/util/HelpUtils.java">Google I/O 2014</a>
 *
 * @author Xinyue Zhao
 */
public final class AboutDialogFragment extends DialogFragment {
	/**
	 * Error-handling.
	 */
	private static final String VERSION_UNAVAILABLE = "N/A";

	/**
	 * Initialize an {@link  AboutDialogFragment}.
	 *
	 * @param context
	 * 		A {@link Context} object.
	 *
	 * @return An instance of {@link AboutDialogFragment}.
	 */
	public static DialogFragment newInstance( Context context ) {
		return (DialogFragment) Fragment.instantiate( context, AboutDialogFragment.class.getName() );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setCancelable( false );
	}

	@Override
	public Dialog onCreateDialog( Bundle savedInstanceState ) {
		// Get app version
		PackageManager pm          = getActivity().getPackageManager();
		String         packageName = getActivity().getPackageName();
		String         versionName;
		try {
			PackageInfo info = pm.getPackageInfo( packageName, 0 );
			versionName = info.versionName;
		} catch( PackageManager.NameNotFoundException e ) {
			versionName = VERSION_UNAVAILABLE;
		}

		// About.
		SpannableStringBuilder aboutBody = new SpannableStringBuilder();
		aboutBody.append( Html.fromHtml( getString( R.string.about_body, getString( R.string.application_name ), versionName ) ) );

		// Licenses.
		SpannableString licensesLink = new SpannableString( getString( R.string.about_licenses ) );
		licensesLink.setSpan( new ClickableSpan() {
			@Override
			public void onClick( View view ) {
				showOpenSourceLicenses( getActivity() );
			}
		}, 0, licensesLink.length(), 0 );
		aboutBody.append( "\n\n" );
		aboutBody.append( licensesLink );

		// End User License Agreement.
		SpannableString eulaLink = new SpannableString( getString( R.string.about_eula ) );
		eulaLink.setSpan( new ClickableSpan() {
			@Override
			public void onClick( View view ) {
				showEula( getActivity() );
			}
		}, 0, eulaLink.length(), 0 );
		aboutBody.append( "\n\n" );
		aboutBody.append( eulaLink );


		// Show "About" dialog.
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View           dialogV        = layoutInflater.inflate( R.layout.fragment_dialog_about, null );
		;
		TextView aboutBodyView = (TextView) dialogV.findViewById( R.id.dialog_text_tv );
		aboutBodyView.setText( aboutBody );
		aboutBodyView.setMovementMethod( new LinkMovementMethod() );
		dialogV.findViewById( R.id.powered_by_ll ).setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				WebViewActivity.showInstance( getActivity(), "Faroo", Prefs.getInstance().getFarooHome() );
			}
		} );
		return new AlertDialog.Builder( getActivity() ).setTitle( R.string.action_about ).setView( dialogV ).setPositiveButton(
				R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog, int whichButton ) {
						dialog.dismiss();
					}
				} ).create();
	}

	/**
	 * To open showing open source licenses.
	 *
	 * @param activity
	 * 		Host {@link android.support.v4.app.FragmentActivity}.
	 */
	private static void showOpenSourceLicenses( FragmentActivity activity ) {
		FragmentManager     fm   = activity.getSupportFragmentManager();
		FragmentTransaction ft   = fm.beginTransaction();
		Fragment            prev = fm.findFragmentByTag( "dialog_licenses" );
		if( prev != null ) {
			ft.remove( prev );
		}
		ft.addToBackStack( null );
		new OpenSourceLicensesDialog().show( ft, "dialog_licenses" );
	}

	/**
	 * Dialog to open showing open source licenses.
	 *
	 * @author Xinyue Zhao
	 */
	public static class OpenSourceLicensesDialog extends DialogFragment {

		@Override
		public void onCreate( Bundle savedInstanceState ) {
			super.onCreate( savedInstanceState );
			setCancelable( false );
		}

		@Override
		public Dialog onCreateDialog( Bundle savedInstanceState ) {
			WebView webView = new WebView( getActivity() );
			webView.loadUrl( "file:///android_asset/licenses.html" );

			return new AlertDialog.Builder( getActivity() ).setTitle( R.string.about_licenses ).setView( webView ).setPositiveButton(
					R.string.btn_confirm, new DialogInterface.OnClickListener() {
						public void onClick( DialogInterface dialog, int whichButton ) {
							dialog.dismiss();
						}
					} ).create();
		}
	}

	/**
	 * Dialog to open showing the "End User License Agreement".
	 *
	 * @author Xinyue Zhao
	 */
	public static void showEula( FragmentActivity activity ) {
		FragmentManager     fm   = activity.getSupportFragmentManager();
		FragmentTransaction ft   = fm.beginTransaction();
		Fragment            prev = fm.findFragmentByTag( "dialog_eula" );
		if( prev != null ) {
			ft.remove( prev );
		}
		ft.addToBackStack( null );

		new EulaDialog().show( ft, "dialog_eula" );
	}

	/**
	 * Dialog to open showing the "End User License Agreement".
	 *
	 * @author Xinyue Zhao
	 */
	public static class EulaDialog extends DialogFragment {

		@Override
		public Dialog onCreateDialog( Bundle savedInstanceState ) {
			int      padding      = getResources().getDimensionPixelSize( R.dimen.padding_eula );
			TextView eulaTextView = new TextView( getActivity() );
			eulaTextView.setText( Html.fromHtml( getString( R.string.about_eula_legal_text ) ) );
			eulaTextView.setMovementMethod( LinkMovementMethod.getInstance() );
			eulaTextView.setPadding( padding, padding, padding, padding );
			return new AlertDialog.Builder( getActivity() ).setTitle( R.string.about_eula ).setView( eulaTextView ).setPositiveButton(
					R.string.btn_ok, new DialogInterface.OnClickListener() {
						public void onClick( DialogInterface dialog, int whichButton ) {
							dialog.dismiss();
						}
					} ).create();
		}
	}


	/**
	 * Dialog to open showing the "End User License Agreement".
	 * <p/>
	 * When user does not agree, user can not use the application.
	 *
	 * @author Xinyue Zhao
	 */
	public static class EulaConfirmationDialog extends DialogFragment {


		@Override
		public void onCreate( Bundle savedInstanceState ) {
			super.onCreate( savedInstanceState );
			setCancelable( false );
		}

		@Override
		public Dialog onCreateDialog( Bundle savedInstanceState ) {
			int      padding      = getResources().getDimensionPixelSize( R.dimen.padding_eula );
			TextView eulaTextView = new TextView( getActivity() );
			eulaTextView.setText( Html.fromHtml( getString( R.string.about_eula_legal_text ) ) );
			eulaTextView.setBackgroundColor( getResources().getColor( R.color.common_white ) );
			eulaTextView.setTextColor( getResources().getColor( R.color.common_black ) );
			eulaTextView.setMovementMethod( LinkMovementMethod.getInstance() );
			eulaTextView.setPadding( padding, padding, padding, padding );
			return new AlertDialog.Builder( getActivity() ).setTitle( R.string.about_eula ).setView( eulaTextView ).setPositiveButton(
					R.string.btn_agree, new DialogInterface.OnClickListener() {
						public void onClick( DialogInterface dialog, int whichButton ) {
							Prefs.getInstance().setEULAOnceConfirmed( true );
							dismiss();
							EventBus.getDefault().post( new EULAConfirmedEvent() );
						}
					} ).setNegativeButton( R.string.btn_not_agree, new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog, int whichButton ) {
					Prefs.getInstance().setEULAOnceConfirmed( false );
					dismiss();
					EventBus.getDefault().post( new EULARejectEvent() );
				}
			} ).create();
		}
	}

}


