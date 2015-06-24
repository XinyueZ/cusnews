package com.cusnews.app.fragments;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.app.App;
import com.cusnews.databinding.CustomizedTopicsBinding;
import com.cusnews.ds.PushToken;
import com.cusnews.utils.DeviceUniqueUtil;
import com.cusnews.utils.Prefs;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A dialog to define customized topics.
 *
 * @author Xinyue Zhao
 */
public final class CustomizedTopicsFragment extends DialogFragment {
	/**
	 * Main layout for this component.
	 */
	private static final int LAYOUT = R.layout.fragment_customized_topics;
	/**
	 * Data-binding.
	 */
	private CustomizedTopicsBinding mBinding;

	public static CustomizedTopicsFragment newInstance(Context context) {
		return (CustomizedTopicsFragment) Fragment.instantiate(context, CustomizedTopicsFragment.class.getName());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(LAYOUT, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBinding = DataBindingUtil.bind(view.findViewById(R.id.topics_fl));
		mBinding.closeVg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBinding.closeBtn.setVisibility(View.INVISIBLE);
				mBinding.savePb.setVisibility(View.VISIBLE);
				
				String deviceId = "0000000000";
				try {
					deviceId = DeviceUniqueUtil.getDeviceIdent(App.Instance);
				} catch (NoSuchAlgorithmException e) {

				}
				Prefs prefs = Prefs.getInstance();
				BmobQuery<PushToken> query = new BmobQuery<>();
				query.addWhereEqualTo("mGoogleId", prefs.getGoogleId());
				query.addWhereEqualTo("mDeviceId", deviceId);
				query.findObjects(App.Instance, new FindListener<PushToken>() {
					@Override
					public void onSuccess(List<PushToken> list) {
						if(list.size()>0) {
							PushToken pushToken = list.get(0);
							pushToken.setCustomizedTopic1(mBinding.oneEt.getText().toString());
							pushToken.setCustomizedTopic2(mBinding.twoEt.getText().toString());
							pushToken.setCustomizedTopic3(mBinding.threeEt.getText().toString());
							pushToken.update(App.Instance, pushToken.getObjectId(), new UpdateListener() {
								@Override
								public void onSuccess() {
									Activity activity = getActivity();
									if (activity != null) {
										ActivityCompat.finishAfterTransition(activity);
									}
								}

								@Override
								public void onFailure(int i, String s) {
									mBinding.closeBtn.setVisibility(View.VISIBLE);
									mBinding.savePb.setVisibility(View.INVISIBLE);
								}
							});
						}
					}

					@Override
					public void onError(int i, String s) {
						mBinding.closeBtn.setVisibility(View.VISIBLE);
						mBinding.savePb.setVisibility(View.INVISIBLE);
					}
				});
			}
		});

		String deviceId = "0000000000";
		try {
			deviceId = DeviceUniqueUtil.getDeviceIdent(App.Instance);
		} catch (NoSuchAlgorithmException e) {

		}
		Prefs prefs = Prefs.getInstance();
		BmobQuery<PushToken> query = new BmobQuery<>();
		query.addWhereEqualTo("mGoogleId", prefs.getGoogleId());
		query.addWhereEqualTo("mDeviceId", deviceId);
		query.findObjects(App.Instance, new FindListener<PushToken>() {
			@Override
			public void onSuccess(List<PushToken> list) {
				mBinding.oneEt.setEnabled(true);
				mBinding.twoEt.setEnabled(true);
				mBinding.threeEt.setEnabled(true);
				if (list.size() > 0) {
					mBinding.setVariable(BR.pushToken, list.get(0));
				}

				mBinding.closeBtn.setVisibility(View.VISIBLE);
				mBinding.savePb.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onError(int i, String s) {
				mBinding.oneEt.setEnabled(true);
				mBinding.twoEt.setEnabled(true);
				mBinding.threeEt.setEnabled(true);

				mBinding.closeBtn.setVisibility(View.VISIBLE);
				mBinding.savePb.setVisibility(View.INVISIBLE);
			}
		});
	}
}
