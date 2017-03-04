package com.goskincare.custom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.goskincare.R;
import com.goskincare.utils.TouchEffect;

/**
 * This is a common activity that all other activities of the app can extend to
 * inherit the common behaviors like setting a Theme to activity.
 */
public class CustomActivity extends FragmentActivity implements OnClickListener
{

	/**
	 * application context
	 */
	public Context _context = null;

	/**
	 * Apply this Constant as touch listener for views to provide alpha touch
	 * effect. The view must have a Non-Transparent background.
	 */
	public static final TouchEffect TOUCH = new TouchEffect();

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		_context = this;
	}


	/**
	 * Set the touch and click listener for a View.
	 * 
	 * @param id
	 *            the id of view
	 * @return the view
	 */
	public View setTouchNClick(int id)
	{
		View v = setClick(id);
		v.setOnTouchListener(TOUCH);
		return v;
	}

	/**
	 * Set the click listener for a View.
	 * 
	 * @param id
	 *            the id of view
	 * @return the view
	 */
	public View setClick(int id)
	{

		View v = findViewById(id);
		v.setOnClickListener(this);
		return v;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * show progress
	 */
	// show progress dialog
	private ProgressDialog _progressDlg;

	public void showProgress(String strMsg,  boolean cancelable) {

		closeProgress();

		_progressDlg = new ProgressDialog(_context);
		_progressDlg.setMessage(strMsg);
		_progressDlg.setCancelable(cancelable);
		_progressDlg.show();
	}

	public void showProgress() {
		showProgress(_context.getString(R.string.loading), true);
	}

	public void closeProgress() {

		if(_progressDlg == null) {
			return;
		}

		_progressDlg.dismiss();
		_progressDlg = null;
	}

	/**
	 * show alert dialog
	 * @param msg
	 */
	public void showAlertDialog(String msg) {

		AlertDialog alertDialog = new AlertDialog.Builder(_context).create();

		alertDialog.setTitle(_context.getString(R.string.app_name));
		alertDialog.setMessage(msg);

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, _context.getString(R.string.ok),

				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

					}
				});
		alertDialog.setIcon(R.drawable.ic_launcher);
		alertDialog.show();
	}

	/**
	 *  show toast
	 * @param toast_string
	 */
	public void showToast(String toast_string) {

		Toast.makeText(_context, toast_string, Toast.LENGTH_SHORT).show();
	}

}
