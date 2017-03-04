package com.goskincare.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.activity.MainActivity;
import com.goskincare.custom.CustomFragment;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;
import com.kyleduo.switchbutton.SwitchButton;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class OrderDetailFragment extends CustomFragment
{
	public JSONArray jsonArrayData;
	JSONObject jsonCalcResult;

	SwitchButton sbGift, sbExpress;
	LayoutInflater mInflater;
	LinearLayout lyOrderContainer, lyCardContainer;
	View mView;
	String strCountryCode = "AU", strOrderId;
	int isExpressOn = 0, isGitftOn = 0;
	Context mContext;
	TextView tvShippingTitle, tvShippingValue, tvTotal, tvAddress, tvName;
	private static final int REQUEST_CODE_PAYMENT = 1;

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_order_detail, null);
		mInflater = inflater;
		mContext = (MainActivity)getActivity();

		setUI();
		loadCreditCards();

		return mView;
	}

	void setUI() {

		lyOrderContainer = (LinearLayout)mView.findViewById(R.id.lyOrderContainer);
		lyCardContainer = (LinearLayout)mView.findViewById(R.id.lyCardContainer);

		sbGift = (SwitchButton)mView.findViewById(R.id.sbGift);

		sbGift.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) isGitftOn = 1; else isGitftOn = 0;
				calculateOrder();
			}
		});

		sbExpress = (SwitchButton)mView.findViewById(R.id.sbExpress);

		JSONObject jsonUserInfo = APIManager.getInstance().getUserDetails();
		try {
			strCountryCode = jsonUserInfo.getString(Constant.key_address_country);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if(strCountryCode.equals("AU")) {
			sbExpress.setVisibility(View.VISIBLE);

			sbExpress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked) isExpressOn = 1; else isExpressOn = 0;
					calculateOrder();
				}
			});
		} else {
			sbExpress.setVisibility(View.INVISIBLE);
		}

		mView.findViewById(R.id.lytBack).setOnClickListener(this);
		mView.findViewById(R.id.rlyButtonPayPal).setOnClickListener(this);
		mView.findViewById(R.id.btnChange).setOnClickListener(this);
		mView.findViewById(R.id.tvEditCards).setOnClickListener(this);

		tvShippingTitle = (TextView)mView.findViewById(R.id.tvShippingTitle);
		tvShippingValue = (TextView)mView.findViewById(R.id.tvShippingValue);
		tvTotal = (TextView)mView.findViewById(R.id.tvTotal);
		tvAddress = (TextView)mView.findViewById(R.id.tvAddress);
		tvName = (TextView)mView.findViewById(R.id.tvName);
	}

	JSONArray getOrderItems() {
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < jsonArrayData.length(); i++){
			try {
				JSONObject jsonObject = jsonArrayData.getJSONObject(i);
				JSONObject otherJsonObject = new JSONObject();

				otherJsonObject.put(Constant.key_productId, jsonObject.getString(Constant.key_productId));
				otherJsonObject.put(Constant.key_amount, jsonObject.getInt(Constant.key_amount));
				otherJsonObject.put(Constant.key_sku, jsonObject.getString(Constant.key_productId));

				jsonArray.put(otherJsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return jsonArray;
	}

	void loadCreditCards() {
		Common.getInstance().showProgressDialog(mContext, "Loading...");
		APIManager.getInstance().getCreditCardList(new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				try {
					Common.getInstance().hideProgressDialog();

					JSONObject jsonResponse = new JSONObject(strJson);
					APIManager.getInstance().arrayListCreditCards = new ArrayList<JSONObject>();

					for(int i = 0; i < jsonResponse.getJSONArray(Constant.key_cards).length(); i ++) {
						JSONObject jsonCardInfo = jsonResponse.getJSONArray(Constant.key_cards).getJSONObject(i);
						if(jsonCardInfo.has(Constant.key_favourite) && jsonCardInfo.getInt(Constant.key_favourite) == 1)
							APIManager.getInstance().nFavoriteCardIndex = i;
						APIManager.getInstance().arrayListCreditCards.add(jsonCardInfo);
					}

					if(APIManager.getInstance().arrayListCreditCards.size() == 1)
						APIManager.getInstance().nFavoriteCardIndex = 0;

					calculateOrder();

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

	void calculateOrder() {
		JSONObject orderInfo = new JSONObject();
		JSONObject finalInfo = new JSONObject();

		try {
			orderInfo.put(Constant.key_items, getOrderItems());
			orderInfo.put(Constant.key_sendExpress, isExpressOn);
			orderInfo.put(Constant.key_isGift, isGitftOn);
			orderInfo.put(Constant.key_countryCode, strCountryCode);

			finalInfo.put(Constant.key_order, orderInfo);
			Common.getInstance().showProgressDialog(mContext, "Calculating...");

			APIManager.getInstance().calculateOrderPrice(finalInfo, new APIManager.OnSuccessListener() {
				@Override
				public void onSuccess(String strJson) {
					Common.getInstance().hideProgressDialog();

					try {
						JSONObject jsonResult = new JSONObject(strJson);
						jsonCalcResult = jsonResult.getJSONObject(Constant.key_order);
						presentOrderDetail();

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
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void refreshData() {
		tvAddress.setText(APIManager.getInstance().formattedAddress());
	}

	void presentOrderDetail() {
		lyOrderContainer.removeAllViews();
		String strCurrencySymbol = "$";
		try {
			strCurrencySymbol = jsonCalcResult.getString(Constant.key_priceCurrencySymbol);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < jsonArrayData.length(); i ++){
			try {
				JSONObject jsonObject = jsonArrayData.getJSONObject(i);

				String strTitle = jsonObject.getString(Constant.key_productName);

				int nAmount = jsonObject.getInt(Constant.key_amount);
				double nPrice = jsonObject.getDouble(Constant.key_price);
				double nTotalPrice = nAmount * nPrice;

				View cell = mInflater.inflate(R.layout.cell_order_detail, null);

				((TextView)cell.findViewById(R.id.tvTitle)).setText(nAmount + " x " + strTitle);
				((TextView)cell.findViewById(R.id.tvValue)).setText(String.format("%s%.2f", strCurrencySymbol, nTotalPrice));

				lyOrderContainer.addView(cell);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		try {

			double dblShippingValue = jsonCalcResult.getDouble(Constant.key_shippingPrice);
			double dblTotal = jsonCalcResult.getDouble(Constant.key_totalPrice);
			tvShippingTitle.setText(jsonCalcResult.getString(Constant.key_shippingName));
			tvShippingValue.setText(String.format("%s%.2f", strCurrencySymbol, dblShippingValue));
			tvTotal.setText(String.format("%s%.2f", strCurrencySymbol,dblTotal));
			tvName.setText(APIManager.getInstance().formattedUserName());
			tvAddress.setText(APIManager.getInstance().formattedAddress());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		lyCardContainer.removeAllViews();
		for(int i = 0;i < APIManager.getInstance().arrayListCreditCards.size(); i ++) {
			final JSONObject jsonCardInfo = APIManager.getInstance().arrayListCreditCards.get(i);
			View cell = mInflater.inflate(R.layout.cell_item_credit_card_other, null);

			try {
				((TextView)cell.findViewById(R.id.tvCardInfo)).setText(jsonCardInfo.getString(Constant.key_pan));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			cell.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						String strCardId = jsonCardInfo.getString(Constant.key_cardid);
						placeIncompleteOrderWithCard(strCardId);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			lyCardContainer.addView(cell);
		}
	}

	void placeIncompleteOrderWithCard(final String strCardId) {
		if(jsonArrayData.length() > 0) {
			JSONObject jsonUserInfo = APIManager.getInstance().getUserDetails();
			JSONObject jsonOrderInfo = new JSONObject();
			JSONObject jsonFinal = new JSONObject();
			try {
				jsonOrderInfo.put(Constant.key_items, getOrderItems());
				jsonOrderInfo.put(Constant.key_shippingCode, jsonCalcResult.getString(Constant.key_shippingCode));
				jsonOrderInfo.put(Constant.key_shippingPrice, jsonCalcResult.getString(Constant.key_shippingPrice));
				jsonOrderInfo.put(Constant.key_shippingName, jsonCalcResult.getString(Constant.key_shippingName));
				jsonOrderInfo.put(Constant.key_totalPrice, jsonCalcResult.getString(Constant.key_totalPrice));
				jsonOrderInfo.put(Constant.key_sendExpress, isExpressOn);
				jsonOrderInfo.put(Constant.key_isGift, isGitftOn);
				jsonOrderInfo.put(Constant.key_userId, jsonUserInfo.getString(Constant.key_userId));
				jsonOrderInfo.put(Constant.key_address_street, jsonUserInfo.getString(Constant.key_address_street));
				jsonOrderInfo.put(Constant.key_address_street_2, jsonUserInfo.getString(Constant.key_address_street_2));
				jsonOrderInfo.put(Constant.key_address_state, jsonUserInfo.getString(Constant.key_address_state));
				jsonOrderInfo.put(Constant.key_address_suburb, jsonUserInfo.getString(Constant.key_address_suburb));
				jsonOrderInfo.put(Constant.key_address_country, jsonUserInfo.getString(Constant.key_address_country));
				jsonOrderInfo.put(Constant.key_address_postcode, jsonUserInfo.getString(Constant.key_address_postcode));
				jsonOrderInfo.put(Constant.key_payPal_reference, "");
				jsonOrderInfo.put(Constant.key_address_to, APIManager.getInstance().formattedUserName());

				jsonFinal.put(Constant.key_order, jsonOrderInfo);

				Common.getInstance().showProgressDialog(mContext, "Preparing...");

				APIManager.getInstance().placeIncompleteOrder(jsonFinal, new APIManager.OnSuccessListener() {
					@Override
					public void onSuccess(String strJson) {
						try {
							Common.getInstance().hideProgressDialog();
							JSONObject jsonResult = new JSONObject(strJson);
							strOrderId = jsonResult.getString(Constant.key_orderid);

							if(strCardId.length() > 0) {
								payNowWithCardId(strCardId);
							}else {
								doPay();
							}
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

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	void payNowWithCardId(String strCardId) {
		Common.getInstance().showProgressDialog(mContext, "Processing...");
		APIManager.getInstance().payByTokenWithOrderID(strOrderId, strCardId, new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				try {
					Common.getInstance().hideProgressDialog();
					JSONObject jsonResult = new JSONObject(strJson);
					Boolean success = jsonResult.getBoolean(Constant.key_success);
					if (success) {
						OrderResultFragment f = new OrderResultFragment();
						f.strOrderId = strOrderId;
						String title = Constant.FRAGMENT_ORDER_RESULT;

						FragmentTransaction transaction = ((MainActivity)mContext).getSupportFragmentManager()
								.beginTransaction();

						transaction.setCustomAnimations(R.anim.left_in,
								R.anim.left_out, R.anim.right_in,
								R.anim.right_out);

						transaction.add(R.id.content_frame, f).addToBackStack(title).commit();
					}
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

	void doPay() {
		PayPalItem[] arrItems = new PayPalItem[jsonArrayData.length()];

		try {
			String strCurrency = jsonCalcResult.getString(Constant.key_priceCurrencySymbol);

			for(int i = 0 ; i < jsonArrayData.length(); i ++){
				JSONObject jsonOrder = jsonArrayData.getJSONObject(i);
				PayPalItem payPalItem = new PayPalItem(jsonOrder.getString(Constant.key_productName), jsonOrder.getInt(Constant.key_amount),
						new BigDecimal(jsonOrder.getString(Constant.key_price)), jsonOrder.getString(Constant.key_priceCurrency),
						jsonOrder.getString(Constant.key_productId));

				arrItems[i] = payPalItem;

				strCurrency = jsonOrder.getString(Constant.key_priceCurrency);
			}

			BigDecimal productsPrice = PayPalItem.getItemTotal(arrItems);

			if(productsPrice.doubleValue() <= 0.f) return;

			BigDecimal shippingPrice = BigDecimal.valueOf(Double.valueOf(jsonCalcResult.getString(Constant.key_shippingPrice)));
			BigDecimal totalPrice = BigDecimal.valueOf(Double.valueOf(jsonCalcResult.getString(Constant.key_totalPrice)));
			totalPrice = productsPrice.add(shippingPrice);

			PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shippingPrice, productsPrice, BigDecimal.ZERO);

			BigDecimal amount = totalPrice;

			PayPalPayment payment = new PayPalPayment(amount, strCurrency, "Go-To", PayPalPayment.PAYMENT_INTENT_SALE);
			payment.items(arrItems).paymentDetails(paymentDetails);

			//--- set other optional fields like invoice_number, custom field, and soft_descriptor
//			payment.custom("This is text that will be associated with the payment that the app can use.");
			Intent intent = new Intent(getActivity(), PaymentActivity.class);

			// send the same configuration for restart resiliency
			intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, ((MainActivity)mContext).payPalConfiguration);

			intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

			startActivityForResult(intent, REQUEST_CODE_PAYMENT);


		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	void sendCompletedPaymentToServer(PaymentConfirmation confirmation) {
		confirmOrder(confirmation);
	}

	void confirmOrder(PaymentConfirmation confirmation){
		if(strOrderId != null && confirmation!= null){
			String strPaypalReference = confirmation.getProofOfPayment().getPaymentId();

			if(strPaypalReference != null) {
				Common.getInstance().showProgressDialog(mContext, "Uploading...");
				APIManager.getInstance().confirmOrder(strOrderId, strPaypalReference, new APIManager.OnSuccessListener() {
					@Override
					public void onSuccess(String strJson) {
						Common.getInstance().hideProgressDialog();
						OrderResultFragment f = new OrderResultFragment();
						f.strOrderId = strOrderId;
						String title = Constant.FRAGMENT_ORDER_RESULT;

						FragmentTransaction transaction = ((MainActivity)mContext).getSupportFragmentManager()
								.beginTransaction();

						transaction.setCustomAnimations(R.anim.left_in,
								R.anim.left_out, R.anim.right_in,
								R.anim.right_out);

						transaction.add(R.id.content_frame, f).addToBackStack(title).commit();
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
		}
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.lytBack){
			((MainActivity)getActivity()).getSupportFragmentManager().popBackStackImmediate();
		} else if (v.getId() == R.id.rlyButtonPayPal) {
			placeIncompleteOrderWithCard("");
		} else if (v.getId() == R.id.btnChange) {
			UpdateProfileFragment f = new UpdateProfileFragment();
			String title = Constant.FRAGMENT_UPDATE_PROFILE;

			f.isPopUp = true;

			FragmentTransaction transaction = ((MainActivity)getActivity()).getSupportFragmentManager()
					.beginTransaction();

			transaction.setCustomAnimations(R.anim.abc_slide_in_bottom,
					R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom,
					R.anim.abc_slide_out_bottom);

			transaction.add(R.id.content_frame, f).addToBackStack(title).commit();
		} else if(v.getId() == R.id.tvEditCards) {
			Fragment f = new CreditCardFragment();
			String title = Constant.FRAGMENT_CREDIT_CARD;

			FragmentTransaction transaction = ((MainActivity)getActivity()).getSupportFragmentManager()
					.beginTransaction();

			transaction.setCustomAnimations(R.anim.left_in,
					R.anim.left_out, R.anim.right_in,
					R.anim.right_out);

			transaction.add(R.id.content_frame, f).addToBackStack(title).commit();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm =
						data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//				ProofOfPayment proofOfPayment = confirm.getProofOfPayment();
//
//				String strPaymentId = proofOfPayment.getPaymentId();
				if (confirm != null) {
					sendCompletedPaymentToServer(confirm);
				}
			}
		}
	}
}
