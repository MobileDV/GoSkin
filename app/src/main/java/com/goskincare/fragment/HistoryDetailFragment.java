package com.goskincare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.activity.MainActivity;
import com.goskincare.custom.CustomFragment;
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
public class HistoryDetailFragment extends CustomFragment
{


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public JSONObject jsonOrder;
	View mView;
	LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_history_detail, null);
		mInflater = inflater;

		setUI();

		return mView;
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	void setUI() {
		try {
			((TextView)mView.findViewById(R.id.tvOrderID)).setText(jsonOrder.getString(Constant.key_orderId));
			((TextView)mView.findViewById(R.id.tvOrderStatus)).setText("Status: " + jsonOrder.getString(Constant.key_status));

			Date date = Common.getInstance().dateFromString(jsonOrder.getString(Constant.key_placeDate), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZ"));
			((TextView)mView.findViewById(R.id.tvOrderDate)).setText(Common.getInstance().stringFromDate(date, new SimpleDateFormat("d MMMM yyyy HH:mm:ss")));

			String strShippingName = "Shipping";

			if(jsonOrder.has(Constant.key_shippingName) && jsonOrder.getString(Constant.key_shippingName).length() > 0) strShippingName = jsonOrder.getString(Constant.key_shippingName);

			((TextView)mView.findViewById(R.id.tvShippingTitle)).setText(strShippingName);
			((TextView)mView.findViewById(R.id.tvShippingValue)).setText(jsonOrder.getString(Constant.key_priceCurrencySymbol) + " " + jsonOrder.getString(Constant.key_shippingPrice));

			String strTotalPrice = String.format("%s %.2f", jsonOrder.getString(Constant.key_priceCurrencySymbol), jsonOrder.getDouble("totalPrice:"));

			((TextView)mView.findViewById(R.id.tvTotal)).setText(strTotalPrice);

			JSONArray jsonArrayOrderProducts = jsonOrder.getJSONArray(Constant.key_items);
			LinearLayout lyOrderContainer = (LinearLayout)mView.findViewById(R.id.lyOrderContainer);

			for(int i = 0; i < jsonArrayOrderProducts.length(); i ++){
				JSONObject jsonObject = jsonArrayOrderProducts.getJSONObject(i);

				String strTitle = jsonObject.getString(Constant.key_productName);
				int nAmount = jsonObject.getInt(Constant.key_amount);
				String strCurrencySymbol = jsonOrder.getString(Constant.key_priceCurrencySymbol);
				double nTotalPrice = jsonObject.getDouble(Constant.key_itemPrice);

				View cell = mInflater.inflate(R.layout.cell_order_detail, null);

				((TextView)cell.findViewById(R.id.tvTitle)).setText(nAmount + " x " + strTitle);
				((TextView)cell.findViewById(R.id.tvValue)).setText(String.format("%s%.2f", strCurrencySymbol, nTotalPrice));

				lyOrderContainer.addView(cell);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		mView.findViewById(R.id.lytBack).setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		if(v.getId() == R.id.lytBack) {
			((MainActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate();
		}
	}
}
