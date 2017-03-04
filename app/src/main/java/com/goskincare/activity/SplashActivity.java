package com.goskincare.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.goskincare.Preference.UserPreference;
import com.goskincare.R;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class SplashActivity will launched at the start of the application. It will
 * be displayed for 3 seconds and than finished automatically and it will also
 * start the next activity of app.
 */
public class SplashActivity extends Activity
{
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		APIManager.getInstance().mContext = getBaseContext();
		initSetting();
		getCountryInfo();
	}

	void initSetting() {
		UserPreference.getInstance().pref = getSharedPreferences(Constant.prefName, Context.MODE_PRIVATE);

		ImageLoaderConfiguration defaultConfiguration
				= new ImageLoaderConfiguration.Builder(this)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();

		ImageLoader.getInstance().init(defaultConfiguration);

		APIManager.getInstance().isProduction = getResources().getBoolean(R.bool.is_production);
	}

	void getCountryInfo() {
		APIManager.getInstance().getCountries(new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				try {
					JSONObject jsonResponse = new JSONObject(strJson);
					APIManager.getInstance().jsonArrayCountryInfo = jsonResponse.getJSONArray(Constant.key_countries);
					gotoLoginActivity();
				} catch (JSONException e) {
					e.printStackTrace();
					Common.getInstance().showAlert("Error", "country data is not correct", SplashActivity.this, new Common.OnOkListener() {
						@Override
						public void onOk() {
							finish();
						}
					});
				}
			}
		}, new APIManager.OnFailListener() {
			@Override
			public void onFail(String strErr) {
				Common.getInstance().showAlert("Error", strErr, SplashActivity.this, new Common.OnOkListener() {
					@Override
					public void onOk() {
						finish();
					}
				});
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void gotoLoginActivity() {
		Intent intnet = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intnet);
		finish();
	}
}