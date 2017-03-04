package com.goskincare.custom;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * The Class CustomFragment is the base Fragment class. You can extend your
 * Fragment classes with this class in case you want to apply common set of
 * rules for those Fragments.
 */
public class CustomFragment extends Fragment implements OnClickListener
{
	/** The Constant THEME. */
	private static final String THEME = "appTheme";

	/** The Constant THEME_BLACK. */
	public static final int THEME_WHITE = 1;

	/** The Constant THEME_GREEN. */
	public static final int THEME_GRAY = 2;

	/** The Constant THEME_RED. */
	public static final int THEME_RED = 3;

	public CustomActivity mContext;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * Set the touch and click listener for a View.
	 * 
	 * @param v
	 *            the view
	 * @return the same view
	 */
	public View setTouchNClick(View v)
	{

		v.setOnClickListener(this);
		v.setOnTouchListener(CustomActivity.TOUCH);
		return v;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{

	}

	/**
	 * Save the theme of the app.
	 * 
	 * @param theme
	 *            the theme to save
	 */
	protected void saveAppTheme(int theme)
	{
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit()
				.putInt(THEME, theme).commit();
	}

	/**
	 * Returns the current theme of the app. The return value can be one of
	 * following: THEME_BLACK THEME_BLUE THEME_GREEN THEME_RED
	 * 
	 * @return the app theme
	 */
	protected int getAppTheme()
	{
		return PreferenceManager.getDefaultSharedPreferences(getActivity())
				.getInt(THEME, THEME_RED);
	}

	/**
	 * show progress
	 */

	public void showProgress() {
		mContext.showProgress();
	}

	public void closeProgress() {
		mContext.closeProgress();
	}

	/**
	 * show toast
	 * @param strMsg
	 */
	public void showToast(String strMsg) {
		mContext.showToast(strMsg);
	}

}
