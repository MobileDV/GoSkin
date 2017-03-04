package com.goskincare.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.activity.CountryActivity;
import com.goskincare.activity.MainActivity;
import com.goskincare.custom.CustomFragment;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * The Class FastOrderFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class UpdateProfileFragment extends CustomFragment
{
	public boolean isPopUp = false;
	View mView;
	MainActivity mContext;
	TextView tvCancel;
	ImageView imgvMenu;
	EditText etNickName, etFirstName, etSurName, etEmailAddress, etPassword, etCompany, etAddressStreet, etAddressStreet2, etAddressSuburb, etAddressState, etAddressPostCode, etCountry;
	JSONObject jsonCountryInfo;
	int nSelectedCountryIndex;
	static final int PICK_COUNTRY_REQUEST = 1;
	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mView = inflater.inflate(R.layout.fragment_update_profile, null);
		mContext = (MainActivity)getActivity();

		setUI();
		resetProfileInfo();

		return mView;
	}

	private void setUI() {
		etNickName = (EditText)mView.findViewById(R.id.etNickName);
		etSurName = (EditText)mView.findViewById(R.id.etSurName);
		etFirstName = (EditText)mView.findViewById(R.id.etFirstName);
		etEmailAddress = (EditText)mView.findViewById(R.id.etEmail);
		etPassword = (EditText)mView.findViewById(R.id.etPassword);
		etCompany = (EditText)mView.findViewById(R.id.etCompany);
		etAddressStreet = (EditText)mView.findViewById(R.id.etAddressStreet);
		etAddressStreet2 = (EditText)mView.findViewById(R.id.etAddressStreet2);
		etAddressSuburb = (EditText)mView.findViewById(R.id.etAddressSuburb);
		etAddressState = (EditText)mView.findViewById(R.id.etAddressState);
		etAddressPostCode = (EditText)mView.findViewById(R.id.etAddressPostCode);
		etCountry = (EditText)mView.findViewById(R.id.etCountry);

		etCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					gotoCountryActivity();
				}
			}
		});

		tvCancel = (TextView)mView.findViewById(R.id.tvCancel);
		imgvMenu = (ImageView)mView.findViewById(R.id.imgvMenu);

		mView.findViewById(R.id.btnUpdate).setOnClickListener(this);

		etFirstName.setEnabled(!isPopUp);
		etSurName.setEnabled(!isPopUp);
		etNickName.setEnabled(!isPopUp);
		etEmailAddress.setEnabled(!isPopUp);
		etPassword.setEnabled(!isPopUp);
		
		if(isPopUp){
			tvCancel.setVisibility(View.VISIBLE);
			tvCancel.setOnClickListener(this);
			imgvMenu.setVisibility(View.INVISIBLE);

			etFirstName.setTextColor(Color.LTGRAY);
			etSurName.setTextColor(Color.LTGRAY);
			etNickName.setTextColor(Color.LTGRAY);
			etEmailAddress.setTextColor(Color.LTGRAY);
			etPassword.setTextColor(Color.LTGRAY);

			etPassword.setHint("Type new password if want to change");
		}else{
			tvCancel.setVisibility(View.INVISIBLE);
			imgvMenu.setVisibility(View.VISIBLE);
			imgvMenu.setOnClickListener(this);

			etFirstName.setTextColor(Color.BLACK);
			etSurName.setTextColor(Color.BLACK);
			etNickName.setTextColor(Color.BLACK);
			etEmailAddress.setTextColor(Color.BLACK);
			etPassword.setTextColor(Color.BLACK);
		}

	}

	private void resetProfileInfo() {
		JSONObject jsonUserInfo = APIManager.getInstance().getUserDetails();
		try {
			String strCountryCode = jsonUserInfo.getString(Constant.key_address_country);
			nSelectedCountryIndex = APIManager.getInstance().indexOfCountry(strCountryCode);
			jsonCountryInfo = APIManager.getInstance().jsonArrayCountryInfo.getJSONObject(nSelectedCountryIndex);

			etFirstName.setText(jsonUserInfo.getString(Constant.key_firstname));
			etSurName.setText(jsonUserInfo.getString(Constant.key_surname));
			etNickName.setText(jsonUserInfo.getString(Constant.key_nickname));
			etEmailAddress.setText(jsonUserInfo.getString(Constant.key_email));
			etPassword.setText("");
			etCompany.setText(jsonUserInfo.getString(Constant.key_company));
			etAddressStreet.setText(jsonUserInfo.getString(Constant.key_address_street));
			etAddressStreet2.setText(jsonUserInfo.getString(Constant.key_address_street_2));
			etAddressSuburb.setText(jsonUserInfo.getString(Constant.key_address_suburb));
			etAddressState.setText(jsonUserInfo.getString(Constant.key_address_state));
			etAddressPostCode.setText(jsonUserInfo.getString(Constant.key_address_postcode));
			etCountry.setText(jsonCountryInfo.getString(Constant.LABEL));

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
             * @see com.imate.custom.CustomFragment#onClick(android.view.View)
             */
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnUpdate){
			onUpdate();
		} else if (v.getId() == R.id.tvCancel) {
			mContext.getSupportFragmentManager().popBackStackImmediate();
		} else if (v.getId() == R.id.imgvMenu) {
			mContext.toggleMenu();
		}

	}

	void onUpdate() {
		final String strNickName = etNickName.getText().toString().trim();
		final String strSurName = etSurName.getText().toString().trim();
		final String strFirstName = etFirstName.getText().toString().trim();
		final String strEmail = etEmailAddress.getText().toString().trim();
		final String strPswd = etPassword.getText().toString().trim();
		final String strCompany = etCompany.getText().toString().trim();
		final String strAddressStreet = etAddressStreet.getText().toString().trim();
		final String strAddressStreet2 = etAddressStreet2.getText().toString().trim();
		final String strAddressSuburb = etAddressSuburb.getText().toString().trim();
		final String strAddressState = etAddressState.getText().toString().trim();
		final String strAddressPostCode = etAddressPostCode.getText().toString().trim();

		if(strFirstName.length() < 1) {
			Common.getInstance().showAlert("Error", "Firstname should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etFirstName.setFocusableInTouchMode(true);
					etFirstName.requestFocus();
				}
			});

			return;
		}

		if(strSurName.length() < 1) {
			Common.getInstance().showAlert("Error", "Surname should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etSurName.setFocusableInTouchMode(true);
					etSurName.requestFocus();
				}
			});

			return;
		}

		if(strEmail.length() < 1) {
			Common.getInstance().showAlert("Error", "Email address should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etEmailAddress.setFocusableInTouchMode(true);
					etEmailAddress.requestFocus();
				}
			});

			return;
		}

		if(!Common.getInstance().isEmailValid(strEmail)) {
			Common.getInstance().showAlert("Error", "Email address is not valid", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etEmailAddress.setFocusableInTouchMode(true);
					etEmailAddress.requestFocus();
				}
			});

			return;
		}

		if(strAddressStreet.length() < 1) {
			Common.getInstance().showAlert("Error", "Street should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etAddressStreet.setFocusableInTouchMode(true);
					etAddressStreet.requestFocus();
				}
			});

			return;
		}


		if(strAddressSuburb.length() < 1) {
			Common.getInstance().showAlert("Error", "Suburb should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etAddressSuburb.setFocusableInTouchMode(true);
					etAddressSuburb.requestFocus();
				}
			});

			return;
		}

		if(strAddressState.length() < 1) {
			Common.getInstance().showAlert("Error", "State should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etAddressState.setFocusableInTouchMode(true);
					etAddressState.requestFocus();
				}
			});

			return;
		}

		if(strAddressPostCode.length() < 1) {
			Common.getInstance().showAlert("Error", "Post code should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {
					etAddressPostCode.setFocusableInTouchMode(true);
					etAddressPostCode.requestFocus();
				}
			});

			return;
		}

		if(jsonCountryInfo == null) {
			Common.getInstance().showAlert("Error", "Country should not be empty", mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {

				}
			});

			return;
		}

		try {
			final String strCountryCode = jsonCountryInfo.getString(Constant.CODE);

			Common.getInstance().showProgressDialog(mContext, "Updating...");

			APIManager.getInstance().verifyAddress(strCountryCode, strAddressStreet, strAddressStreet2, strAddressSuburb, strAddressState, strAddressPostCode, new APIManager.OnSuccessListener() {
				@Override
				public void onSuccess(String strJson) {
					try {
						JSONObject jsonResponse = new JSONObject(strJson);
						boolean isValidAddress = jsonResponse.getBoolean(Constant.key_isValidAddress);

						if (isValidAddress) {
							updateProfile(strFirstName, strSurName, strNickName, strEmail, strPswd, strCompany, strCountryCode, strAddressStreet, strAddressStreet2, strAddressSuburb, strAddressState, strAddressPostCode);
						} else {
							Common.getInstance().hideProgressDialog();
							Common.getInstance().showAlert("Invalid Address", "Your address doesn't seem to be valid", mContext, new Common.OnOkListener() {
								@Override
								public void onOk() {

								}
							});
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Common.getInstance().showAlert("Error", e.getLocalizedMessage(), mContext, new Common.OnOkListener() {
							@Override
							public void onOk() {

							}
						});
					}
				}
			}, new APIManager.OnFailListener() {
				@Override
				public void onFail(String strErr) {
					Common.getInstance().hideProgressDialog();
					Common.getInstance().showAlert("Error", strErr, mContext, new Common.OnOkListener() {
						@Override
						public void onOk() {

						}
					});
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
			Common.getInstance().showAlert("Error", e.getLocalizedMessage(), mContext, new Common.OnOkListener() {
				@Override
				public void onOk() {

				}
			});
		}
	}

	private void updateProfile(String firstname, String surname, String nickname, String email, final String password, final String company, String countryCode, final String street, String street2, String suburb, String state, String postCode) {
		APIManager.getInstance().updateProfile(firstname, surname, nickname, email, password, company, countryCode, street, street2, suburb, state, postCode, false, new APIManager.OnSuccessListener() {
			@Override
			public void onSuccess(String strJson) {
				Common.getInstance().hideProgressDialog();
				try {
					JSONObject jsonResponse = new JSONObject(strJson);
					JSONObject jsonUserInfo = jsonResponse.getJSONObject(Constant.key_user);

					String strOldPassword = APIManager.getInstance().getUserDetails().getString(Constant.key_password);

					if(password.length() == 0 || password.equals(strOldPassword)){
						jsonUserInfo.put(Constant.key_password, strOldPassword);
					}

					jsonUserInfo.put(Constant.key_company, company);
					APIManager.getInstance().saveUserDetails(jsonUserInfo);

					if(isPopUp) {
						OrderDetailFragment fragment = (OrderDetailFragment)mContext.getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_ORDER_DETAIL);

						if(fragment != null){
							fragment.refreshData();
						}

						MagicOrderFragment fragment1 = (MagicOrderFragment)mContext.getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_MAGIC_ORDER);

						if(fragment1 != null) {
							fragment1.refreshData();
						}

						mContext.getSupportFragmentManager().popBackStackImmediate();
					} else {
						Common.getInstance().showAlert("Go-To", "Update successful", mContext, new Common.OnOkListener() {
							@Override
							public void onOk() {

							}
						});
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new APIManager.OnFailListener() {
			@Override
			public void onFail(String strErr) {
				Common.getInstance().hideProgressDialog();
				Common.getInstance().showAlert("Error", strErr, mContext, new Common.OnOkListener() {
					@Override
					public void onOk() {

					}
				});
			}
		});
	}

	private void gotoCountryActivity() {

		Intent intent = new Intent(getActivity(), CountryActivity.class).putExtra(Constant.SelectedCountryIndex, nSelectedCountryIndex);
		startActivityForResult(intent, PICK_COUNTRY_REQUEST);
		getActivity().overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK) {
			if(requestCode == PICK_COUNTRY_REQUEST){
				nSelectedCountryIndex = data.getIntExtra(Constant.SelectedCountryIndex, -1);
				if(nSelectedCountryIndex > -1) {
					try {
						jsonCountryInfo = APIManager.getInstance().jsonArrayCountryInfo.getJSONObject(nSelectedCountryIndex);
						etCountry.setText(jsonCountryInfo.getString(Constant.LABEL));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
