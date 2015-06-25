package com.cusnews.app.fragments;

import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cusnews.BR;
import com.cusnews.R;
import com.cusnews.api.Api;
import com.cusnews.app.App;
import com.cusnews.databinding.CustomizedTopicsBinding;
import com.cusnews.ds.PushToken;
import com.cusnews.ds.Trends;
import com.cusnews.utils.DeviceUniqueUtil;
import com.cusnews.utils.Prefs;
import com.cusnews.utils.Utils;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
	 * "menu" of current trends from
	 * <p/>
	 * <a href="http://www.faroo.com">Faroo.com</a>
	 */
	private static final int TRENDS_SELECTION = R.menu.trends_selection;
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
		//Click "ok" to save current values.
		mBinding.closeVg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!Utils.validateKeyword(mBinding.oneEt) || !Utils.validateKeyword(mBinding.twoEt) ||
						!Utils.validateKeyword(mBinding.threeEt) || !Utils.validateKeyword(mBinding.fourEt) ||
						!Utils.validateKeyword(mBinding.fiveEt)) {
					return;
				}

				mBinding.closeBtn.setVisibility(View.INVISIBLE);
				mBinding.savePb.setVisibility(View.VISIBLE);


				mBinding.oneEt.setEnabled(false);
				mBinding.twoEt.setEnabled(false);
				mBinding.threeEt.setEnabled(false);
				mBinding.fourEt.setEnabled(false);
				mBinding.fiveEt.setEnabled(false);
				
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
							pushToken.setCustomizedTopic4(mBinding.fourEt.getText().toString());
							pushToken.setCustomizedTopic5(mBinding.fiveEt.getText().toString());
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
									mBinding.closeBtn.setText(R.string.btn_retry);
									mBinding.closeBtn.setVisibility(View.VISIBLE);
									mBinding.savePb.setVisibility(View.INVISIBLE);

									mBinding.oneEt.setEnabled(true);
									mBinding.twoEt.setEnabled(true);
									mBinding.threeEt.setEnabled(true);
									mBinding.fourEt.setEnabled(true);
									mBinding.fiveEt.setEnabled(true);
								}
							});
						}
					}

					@Override
					public void onError(int i, String s) {
						mBinding.closeBtn.setText(R.string.btn_retry);
						mBinding.closeBtn.setVisibility(View.VISIBLE);
						mBinding.savePb.setVisibility(View.INVISIBLE);

						mBinding.oneEt.setEnabled(true);
						mBinding.twoEt.setEnabled(true);
						mBinding.threeEt.setEnabled(true);
						mBinding.fourEt.setEnabled(true);
						mBinding.fiveEt.setEnabled(true);
					}
				});
			}
		});

		//Get data from backend to refresh UI.
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
				mBinding.fourEt.setEnabled(true);
				mBinding.fiveEt.setEnabled(true);
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
				mBinding.fourEt.setEnabled(true);
				mBinding.fiveEt.setEnabled(true);

				mBinding.closeBtn.setVisibility(View.VISIBLE);
				mBinding.savePb.setVisibility(View.INVISIBLE);
			}
		});

		//Get trends.
		Api.getTopTrends("", Prefs.getInstance().getLanguage(), App.Instance.getApiKey(), new Callback<Trends>() {
			@Override
			public void success(Trends trends, Response response) {
				Activity activity = getActivity();
				if (activity != null) {
					List<String> listOfTrends = trends.getList();

					mBinding.trendsOneBtn.setVisibility(View.VISIBLE);
					final PopupMenu oneMenu = new PopupMenu(activity, mBinding.trendsOneBtn);
					oneMenu.inflate(TRENDS_SELECTION);
					initMenu(oneMenu,listOfTrends, mBinding.oneEt );
					mBinding.trendsOneBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							oneMenu.show();
						}
					});

					mBinding.trendsTwoBtn.setVisibility(View.VISIBLE);
					final PopupMenu twoMenu =  new PopupMenu(activity, mBinding.trendsTwoBtn);
					twoMenu.inflate(TRENDS_SELECTION);
					initMenu(twoMenu,listOfTrends, mBinding.twoEt );
					mBinding.trendsTwoBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							twoMenu.show();
						}
					});

					mBinding.trendsThreeBtn.setVisibility(View.VISIBLE);
					final PopupMenu threeMenu =  new PopupMenu(activity, mBinding.trendsThreeBtn);
					threeMenu.inflate(TRENDS_SELECTION);
					initMenu(threeMenu,listOfTrends, mBinding.threeEt );
					mBinding.trendsThreeBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							threeMenu.show();
						}
					});

					mBinding.trendsFourBtn.setVisibility(View.VISIBLE);
					final PopupMenu fourMenu =  new PopupMenu(activity, mBinding.trendsFourBtn);
					fourMenu.inflate(TRENDS_SELECTION);
					initMenu(fourMenu,listOfTrends, mBinding.fourEt );
					mBinding.trendsFourBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							fourMenu.show();
						}
					});

					mBinding.trendsFiveBtn.setVisibility(View.VISIBLE);
					final PopupMenu fiveMenu =  new PopupMenu(activity, mBinding.trendsFiveBtn);
					fiveMenu.inflate(TRENDS_SELECTION);
					initMenu(fiveMenu,listOfTrends, mBinding.fiveEt );
					mBinding.trendsFiveBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							fiveMenu.show();
						}
					});
				}
			}

			private void initMenu(PopupMenu popupMenu, List<String> listOfTrends, EditText targetEt) {
				final WeakReference<EditText> etwp = new WeakReference<>(targetEt);
				for(String trend : listOfTrends) {
					popupMenu.getMenu().add(trend).setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							if(etwp.get() != null) {
								EditText editText = etwp.get();
								editText.setText(item.getTitle());
							}
							return false;
						}
					});
				}
			}
			@Override
			public void failure(RetrofitError error) {

			}
		});
	}
}
