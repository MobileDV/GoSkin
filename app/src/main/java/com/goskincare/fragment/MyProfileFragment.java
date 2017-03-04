package com.goskincare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.goskincare.R;
import com.goskincare.application.GoSkinCareApplication;
import com.goskincare.custom.CustomFragment;
import com.goskincare.utils.Constant;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class MyProfileFragment extends CustomFragment
{


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.main_container, null);

		GoSkinCareApplication.getInstance().trackingScreenView(Constant.GA_SCREENNAME_MY_PROFILE);

		return v;
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{

	}
}
