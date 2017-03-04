package com.goskincare.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.activity.MainActivity;
import com.goskincare.application.GoSkinCareApplication;
import com.goskincare.custom.CustomFragment;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class HistoryFragment extends CustomFragment
{


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	View mView;
	MainActivity mContext;
	LayoutInflater mInflater;
	JSONArray jsonArrayOrders;
	LinearLayout lyContainer;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_history, null);
		mContext = (MainActivity)getActivity();
		mInflater = inflater;

		setUI();
		loadOrderHistory();

		GoSkinCareApplication.getInstance().trackingScreenView(Constant.GA_SCREENNAME_ORDER_HISTORY);

		return mView;
	}

	void setUI() {
		mView.findViewById(R.id.imgvMenu).setOnClickListener(this);
		lyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);
	}

	void loadOrderHistory() {
		Common.getInstance().showProgressDialog(mContext, "Loading...");
		APIManager.getInstance().getOrderList(new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				Common.getInstance().hideProgressDialog();
				try {
					JSONObject jsonResponse = new JSONObject(strJson);
					jsonArrayOrders = jsonResponse.getJSONArray(Constant.key_orders);
					presentData();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new APIManager.OnFailListener() {
			@Override
			public void onFail(String strErr) {
				Common.getInstance().hideProgressDialog();
				Common.getInstance().showAlert("GoSkinCare", strErr, mContext, new Common.OnOkListener() {
					@Override
					public void onOk() {

					}
				});
			}
		});
	}

	void presentData() {
		lyContainer.removeAllViews();

		for(int i = 0; i < jsonArrayOrders.length(); i ++) {
			try {
				final JSONObject jsonOrder = jsonArrayOrders.getJSONObject(i);
				View cell = mInflater.inflate(R.layout.cell_history_item, null);

				((TextView)cell.findViewById(R.id.tvOrder)).setText(jsonOrder.getString(Constant.key_orderId));
				Date date = Common.getInstance().dateFromString(jsonOrder.getString(Constant.key_placeDate), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZ"));
				String strDate = Common.getInstance().stringFromDate(date, new SimpleDateFormat("d MMMM yyyy"));
				String strTime = Common.getInstance().stringFromDate(date, new SimpleDateFormat("h:mm a"));

				strDate = strDate + " " + strTime;

				((TextView)cell.findViewById(R.id.tvDate)).setText(strDate);

				cell.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						HistoryDetailFragment f = new HistoryDetailFragment();
						String title = Constant.FRAGMENT_HISTORY_DETAIL;
						f.jsonOrder = jsonOrder;

						FragmentTransaction transaction = ((MainActivity)getActivity()).getSupportFragmentManager()
								.beginTransaction();

						transaction.setCustomAnimations(R.anim.left_in,
								R.anim.left_out, R.anim.right_in,
								R.anim.right_out);

						transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
					}
				});

				lyContainer.addView(cell);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.imgvMenu){
			((MainActivity)getActivity()).toggleMenu();
		}
	}
}
