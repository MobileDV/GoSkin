package com.goskincare.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.goskincare.R;
import com.goskincare.activity.MainActivity;
import com.goskincare.custom.CustomFragment;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class OrderResultFragment extends CustomFragment
{
	View mView;
	TextView tvConfirmation, tvEmail;
	public String strOrderId;
	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_order_result, null);

		setUI();

		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("GoSkinCare", "Setting screen name: " + "Product Payment");
		((MainActivity)getActivity()).mTracker.setScreenName("Image~" + "Product Payment");
		((MainActivity)getActivity()).mTracker.send(new HitBuilders.ScreenViewBuilder().build());

		((MainActivity)getActivity()).mTracker.send(new HitBuilders.EventBuilder()
				.setCategory("Action")
				.setAction("Share")
				.build());
	}

	void setUI(){
		tvConfirmation = (TextView)mView.findViewById(R.id.tvConfirmation);
		tvEmail = (TextView)mView.findViewById(R.id.tvEmail);
		mView.findViewById(R.id.btnOK).setOnClickListener(this);

		tvConfirmation.setText("Corfirmation " + strOrderId);

		JSONObject jsonUserInfo = APIManager.getInstance().getUserDetails();

		try {
			tvEmail.setText(jsonUserInfo.getString(Constant.key_email));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.btnOK) {
			((MainActivity)getActivity()).launchFragment(0);
		}
	}
}
