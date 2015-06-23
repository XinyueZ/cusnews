package com.cusnews.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Three different methods to get unique identifier on Android, they are all not the perfect solutions however could be
 * used under project restriction limit.
 *
 * @author Xinyue Zhao
 */
public class DeviceUniqueUtil {
	/**
	 * Get device unique identifier.
	 */
	@Nullable
	public static String getDeviceIdent(Context c) throws NoSuchAlgorithmException {
		Prefs prefs = Prefs.getInstance( );
		if (TextUtils.isEmpty(prefs.getDeviceIdent())) {
			String code = DeviceId.getDeviceId(c);
			prefs.setDeviceIdent(code);
		}
		return prefs.getDeviceIdent();
	}


	/**
	 * Fetch a MD5-Hash by getting IMEI or MAC or ANDROID's Secure-ID.
	 */
	private static class DeviceId {
		/**
		 * requires permissions TELEPHONY_SERVICE on cell phones and WIFI_SERVICE on WiFi-only devices
		 *
		 * @return String
		 *
		 * @throws Exception
		 * 		when there is no unique ID
		 */
		@Nullable
		private static String getDeviceId(Context _cxt) throws NoSuchAlgorithmException {
			String readableId;
			// Requires READ_PHONE_STATE
			TelephonyManager tm = (TelephonyManager) _cxt.getSystemService(Context.TELEPHONY_SERVICE);
			// gets the imei (GSM) or MEID/ESN (CDMA)
			String imei = tm.getDeviceId();
			if (null != imei && imei.length() > 0 && Long.parseLong(imei) > 0) { // 000000000000000 for emulator
				readableId = imei;
			} else {
				// devices without SIM card (e.g. WiFi-only tablets)
				// requires ACCESS_WIFI_STATE
				WifiManager wm = (WifiManager) _cxt.getSystemService(Context.WIFI_SERVICE);
				// gets the MAC address
				String mac = wm.getConnectionInfo().getMacAddress();
				if (null != mac && mac.length() > 0) {
					readableId = mac;
				} else {
					// gets the android-assigned id
					// unfortunately, this is not unique on some devices:
					// http://groups.google.com/group/android-developers/browse_thread/thread/53898e508fab44f6/84e54feb28272384?pli=1
					// so it's only a fallback for emulators
					String androidId = Secure.getString(_cxt.getContentResolver(), Secure.ANDROID_ID);
					if (null != androidId && androidId.length() > 0) {
						readableId = androidId;
					} else {
						return null;
					}
				}
			}
			return md5(readableId);
		}

		private static String md5(String _plaintext) throws NoSuchAlgorithmException {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(_plaintext.getBytes());
			byte[] hash = digest.digest();
			BigInteger bigInt = new BigInteger(1, hash);
			return String.format("%1$032X", bigInt).toLowerCase();
		}
	}
}
