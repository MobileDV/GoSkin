package com.goskincare.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.activity.MainActivity;
import com.goskincare.custom.CustomFragment;
import com.goskincare.custom.pickerview.OptionsPickerView;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class CreditCardFragment extends CustomFragment
{


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	public boolean isPopUp = false;

	View mView;
	LayoutInflater mInflater;
	LinearLayout mLyContainer;
	OptionsPickerView pvOptionsMonth, pvOptionsYear;
	String[] mArrayMonth, mArrayYear;
	TextView tvMonth, tvYear;
	RelativeLayout mRlySpace;
	int nSelectedCardIndex;
	MainActivity mContext;
	EditText etNo, etCSC;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_credit_card, null);
		mInflater = inflater;
		mContext = (MainActivity)getActivity();

		nSelectedCardIndex = APIManager.getInstance().nFavoriteCardIndex;

		setUI();
		setOptionsPickerView();
		presentData();

		return mView;
	}

	void setUI() {
		mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);
		tvMonth = (TextView)mView.findViewById(R.id.tvMonth);
		tvYear = (TextView)mView.findViewById(R.id.tvYear);
		mRlySpace = (RelativeLayout)mView.findViewById(R.id.rlySpace);
		etNo = (EditText)mView.findViewById(R.id.etNo);
		etCSC = (EditText)mView.findViewById(R.id.etCSC);

		mView.findViewById(R.id.btnMake).setOnClickListener(this);
		mView.findViewById(R.id.btnDelete).setOnClickListener(this);
		mView.findViewById(R.id.btnAdd).setOnClickListener(this);
		mView.findViewById(R.id.rlySpinnerMonth).setOnClickListener(this);
		mView.findViewById(R.id.rlySpinnerYear).setOnClickListener(this);
		mView.findViewById(R.id.imgvEmpty).setOnClickListener(this);

		if(isPopUp) {
			mView.findViewById(R.id.tvDone).setVisibility(View.VISIBLE);
			mView.findViewById(R.id.tvDone).setOnClickListener(this);
			mView.findViewById(R.id.lytBack).setVisibility(View.INVISIBLE);
		} else {
			mView.findViewById(R.id.tvDone).setVisibility(View.INVISIBLE);
			mView.findViewById(R.id.lytBack).setVisibility(View.VISIBLE);
			mView.findViewById(R.id.lytBack).setOnClickListener(this);
		}
	}

	void setOptionsPickerView() {

		pvOptionsMonth = new OptionsPickerView(getActivity());
		pvOptionsYear = new OptionsPickerView(getActivity());

		mArrayMonth = getResources().getStringArray(R.array.arr_Months);

		pvOptionsMonth.setPicker(new ArrayList<String>(Arrays.asList(mArrayMonth)));
		pvOptionsMonth.setTitle("");
		pvOptionsMonth.setCancelTitle("");

		mArrayYear = new String[20];

		int nThisYear = Calendar.getInstance().get(Calendar.YEAR);

		for(int i = 0; i < 20; i ++){
			mArrayYear[i] = String.valueOf(nThisYear + i);
		}

		pvOptionsYear.setPicker(new ArrayList<String>(Arrays.asList(mArrayYear)));
		pvOptionsYear.setTitle("");
		pvOptionsYear.setCancelTitle("");

		pvOptionsMonth.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				tvMonth.setText(mArrayMonth[options1].substring(0, 3));
				mRlySpace.setVisibility(View.GONE);
			}
		});

		pvOptionsYear.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int option2, int options3) {
				tvYear.setText(mArrayYear[options1]);
				mRlySpace.setVisibility(View.GONE);
			}
		});
	}

	void presentData() {
		mLyContainer.removeAllViews();

		for(int i = 0; i < APIManager.getInstance().arrayListCreditCards.size(); i ++) {
			JSONObject jsonCardInfo = APIManager.getInstance().arrayListCreditCards.get(i);
			View cell = mInflater.inflate(R.layout.cell_item_credit_card, null);

			try {
				if(APIManager.getInstance().nFavoriteCardIndex == i) {
					((TextView)cell.findViewById(R.id.tvCardInfo)).setText(jsonCardInfo.getString(Constant.key_pan) + " (favourite)");
				} else {
					((TextView)cell.findViewById(R.id.tvCardInfo)).setText(jsonCardInfo.getString(Constant.key_pan));
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			if(i == nSelectedCardIndex) {
				((ImageView)cell.findViewById(R.id.imgvSelected)).setImageResource(R.drawable.ic_option_selected);
			} else {
				((ImageView)cell.findViewById(R.id.imgvSelected)).setImageResource(R.drawable.ic_option);
			}

			final int nPos = i;

			cell.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					nSelectedCardIndex = nPos;
					presentData();
				}
			});

			mLyContainer.addView(cell);
		}
	}

	void favoriteCreditCard() {
		if(APIManager.getInstance().arrayListCreditCards.size() < 1) return;

		JSONObject jsonCardInfo = APIManager.getInstance().arrayListCreditCards.get(nSelectedCardIndex);
		try {
			String strCardId = jsonCardInfo.getString(Constant.key_cardid);

			Common.getInstance().showProgressDialog(mContext, "Working...");
			APIManager.getInstance().makeFavoriteCreditCard(strCardId, new APIManager.OnSuccessListener() {
				@Override
				public void onSuccess(String strJson) {
					Common.getInstance().hideProgressDialog();
					try {
						JSONObject jsonResponse = new JSONObject(strJson);
						APIManager.getInstance().nFavoriteCardIndex = nSelectedCardIndex;
						presentData();
						Common.getInstance().showAlert("GoSkinCare", jsonResponse.getString(Constant.key_message), mContext, new Common.OnOkListener() {
							@Override
							public void onOk() {

							}
						});
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

	void deleteCreditCard() {
		if(APIManager.getInstance().arrayListCreditCards.size() < 1) return;

		JSONObject jsonCardInfo = APIManager.getInstance().arrayListCreditCards.get(nSelectedCardIndex);
		try {
			String strCardId = jsonCardInfo.getString(Constant.key_cardid);
			String strPan = jsonCardInfo.getString(Constant.key_pan);

			Common.getInstance().showProgressDialog(mContext, "Deleting...");
			APIManager.getInstance().deleteCreditCard(strCardId, strPan, new APIManager.OnSuccessListener() {
				@Override
				public void onSuccess(String strJson) {
					Common.getInstance().hideProgressDialog();
					try {
						JSONObject jsonResponse = new JSONObject(strJson);
						Common.getInstance().showAlert("GoSkinCare", jsonResponse.getString(Constant.key_message), mContext, new Common.OnOkListener() {
							@Override
							public void onOk() {
								loadCreditCards();
							}
						});
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

	void loadCreditCards() {
		Common.getInstance().showProgressDialog(mContext, "Reloading...");
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

					nSelectedCardIndex = APIManager.getInstance().nFavoriteCardIndex;

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

	void addCreditCard() {
		String strCardNumber = etNo.getText().toString().trim();
		if(strCardNumber.length() < 1) {
			Common.getInstance().showAlert("GoSkinCare", "Please type your card number", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {

				}
			});

			return;
		}

		String strCSC = etCSC.getText().toString().trim();
		if(strCSC.length() < 1) {
			Common.getInstance().showAlert("GoSkinCare", "Please type CSC", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {

				}
			});

			return;
		}

		String strExpMonth = "01";
		String strMonth = tvMonth.getText().toString();
		for(int i = 0; i < mArrayMonth.length; i ++){
			String strMonthName = mArrayMonth[i];
			if(strMonthName.contains(strMonth)){
				strExpMonth = String.format("%02d", i + 1);
			}
		}

		String strExpYear = tvYear.getText().toString();
		strExpYear = strExpYear.substring(2);

		Common.getInstance().showProgressDialog(mContext, "Adding...");
		APIManager.getInstance().addCreditCard(strCardNumber, strExpMonth, strExpYear, new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				Common.getInstance().hideProgressDialog();
				try {
					JSONObject jsonResponse = new JSONObject(strJson);
					Common.getInstance().showAlert("GoSkinCare", jsonResponse.getString(Constant.key_message), mContext, new Common.OnOkListener() {
						@Override
						public void onOk() {
							loadCreditCards();
						}
					});

					return;

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

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */

	void onTapSpinnerMonth() {
		int i = 0;
		String strMonth = tvMonth.getText().toString();

		for(i = 0; i < mArrayMonth.length; i ++){
			String strMonthName = mArrayMonth[i];
			if(strMonthName.contains(strMonth)){
				break;
			}
		}

		pvOptionsMonth.setSelectOptions(i);
		pvOptionsMonth.show();
		mRlySpace.setVisibility(View.VISIBLE);
	}

	void onTapSpinnerYear() {
		int i = 0;
		String strYear = tvYear.getText().toString();

		for(i = 0; i < mArrayYear.length; i ++) {
			String strYearName = mArrayYear[i];
			if(strYear.equals(strYearName)) break;
		}

		pvOptionsYear.setSelectOptions(i);
		pvOptionsYear.show();
		mRlySpace.setVisibility(View.VISIBLE);
	}

	void onBack() {
		MagicOrderFragment fragment = (MagicOrderFragment)mContext.getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_MAGIC_ORDER);

		if(fragment != null){
			fragment.refreshData();
		}

		mContext.getSupportFragmentManager().popBackStackImmediate();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.lytBack) {
			onBack();
		}else if(v.getId() == R.id.tvDone){
			onBack();
		}else if(v.getId() == R.id.btnMake) {
			favoriteCreditCard();
		}else if(v.getId() == R.id.btnDelete){
			deleteCreditCard();
		}else if(v.getId() == R.id.btnAdd){
			addCreditCard();
		}else if(v.getId() == R.id.rlySpinnerMonth){
			onTapSpinnerMonth();
		}else if(v.getId() == R.id.rlySpinnerYear){
			onTapSpinnerYear();
		}else if(v.getId() == R.id.imgvEmpty){

		}
	}
}
