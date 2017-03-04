package com.goskincare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.goskincare.R;
import com.goskincare.custom.CustomActivity;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends CustomActivity {
    EditText etNickName, etFirstName, etSurName, etEmailAddress, etPassword, etCompany, etAddressStreet, etAddressStreet2, etAddressSuburb, etAddressState, etAddressPostCode, etCountry;
    static final int PICK_COUNTRY_REQUEST = 1;
    JSONObject countryInfo = null;
    int nSelectedCountryIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        APIManager.getInstance().mContext = getBaseContext();
        setUI();
    }

    private void setUI() {
        etNickName = (EditText)findViewById(R.id.etNickName);
        etSurName = (EditText)findViewById(R.id.etSurName);
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etEmailAddress = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etCompany = (EditText)findViewById(R.id.etCompany);
        etAddressStreet = (EditText)findViewById(R.id.etAddressStreet);
        etAddressStreet2 = (EditText)findViewById(R.id.etAddressStreet2);
        etAddressSuburb = (EditText)findViewById(R.id.etAddressSuburb);
        etAddressState = (EditText)findViewById(R.id.etAddressState);
        etAddressPostCode = (EditText)findViewById(R.id.etAddressPostCode);
        etCountry = (EditText)findViewById(R.id.etCountry);

        etCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    gotoCountryActivity();
                    etCountry.clearFocus();
                }
            }
        });

        setClick(R.id.btnCreate);
        setClick(R.id.tvCancel);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnCreate){
            onTapCreate();
        } else if (v.getId() == R.id.tvCancel) {
            finish();
            overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        }

    }

    private void onTapCreate() {
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
            Common.getInstance().showAlert("Error", "Firstname should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etFirstName.setFocusableInTouchMode(true);
                    etFirstName.requestFocus();
                }
            });

            return;
        }

        if(strSurName.length() < 1) {
            Common.getInstance().showAlert("Error", "Surname should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etSurName.setFocusableInTouchMode(true);
                    etSurName.requestFocus();
                }
            });

            return;
        }

        if(strEmail.length() < 1) {
            Common.getInstance().showAlert("Error", "Email address should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etEmailAddress.setFocusableInTouchMode(true);
                    etEmailAddress.requestFocus();
                }
            });

            return;
        }

        if(!Common.getInstance().isEmailValid(strEmail)) {
            Common.getInstance().showAlert("Error", "Email address is not valid", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etEmailAddress.setFocusableInTouchMode(true);
                    etEmailAddress.requestFocus();
                }
            });

            return;
        }

        if(!Common.getInstance().isPasswordValid(strPswd)) {
            Common.getInstance().showAlert("Error", "Password should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etPassword.setFocusableInTouchMode(true);
                    etPassword.requestFocus();
                }
            });

            return;
        }

        if(strAddressStreet.length() < 1) {
            Common.getInstance().showAlert("Error", "Street should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etAddressStreet.setFocusableInTouchMode(true);
                    etAddressStreet.requestFocus();
                }
            });

            return;
        }


        if(strAddressSuburb.length() < 1) {
            Common.getInstance().showAlert("Error", "Suburb should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etAddressSuburb.setFocusableInTouchMode(true);
                    etAddressSuburb.requestFocus();
                }
            });

            return;
        }

        if(strAddressState.length() < 1) {
            Common.getInstance().showAlert("Error", "State should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etAddressState.setFocusableInTouchMode(true);
                    etAddressState.requestFocus();
                }
            });

            return;
        }

        if(strAddressPostCode.length() < 1) {
            Common.getInstance().showAlert("Error", "Post code should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etAddressPostCode.setFocusableInTouchMode(true);
                    etAddressPostCode.requestFocus();
                }
            });

            return;
        }

        if(countryInfo == null) {
            Common.getInstance().showAlert("Error", "Country should not be empty", SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {

                }
            });

            return;
        }

        try {
            final String strCountryCode = countryInfo.getString(Constant.CODE);

            Common.getInstance().showProgressDialog(SignupActivity.this, "Creating...");

            APIManager.getInstance().verifyAddress(strCountryCode, strAddressStreet, strAddressStreet2, strAddressSuburb, strAddressState, strAddressPostCode, new APIManager.OnSuccessListener() {
                @Override
                public void onSuccess(String strJson) {
                    try {
                        JSONObject jsonResponse = new JSONObject(strJson);
                        boolean isValidAddress = jsonResponse.getBoolean(Constant.key_isValidAddress);

                        if (isValidAddress) {
                            signup(strFirstName, strSurName, strNickName, strEmail, strPswd, strCompany, strCountryCode, strAddressStreet, strAddressStreet2, strAddressSuburb, strAddressState, strAddressPostCode);
                        } else {
                            Common.getInstance().hideProgressDialog();
                            Common.getInstance().showAlert("Invalid Address", "Your address doesn't seem to be valid", SignupActivity.this, new Common.OnOkListener() {
                                @Override
                                public void onOk() {

                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.getInstance().showAlert("Error", e.getLocalizedMessage(), SignupActivity.this, new Common.OnOkListener() {
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
                    Common.getInstance().showAlert("Error", strErr, SignupActivity.this, new Common.OnOkListener() {
                        @Override
                        public void onOk() {

                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Common.getInstance().showAlert("Error", e.getLocalizedMessage(), SignupActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {

                }
            });
        }
    }

    public void signup(String firstname, String surname, String nickname, String email, final String password, final String company, String countryCode, String street, String street2, String suburb, String state, String postCode) {
        APIManager.getInstance().signup(firstname, surname, nickname, email, password, company, countryCode, street, street2, suburb, state, postCode, new APIManager.OnSuccessListener() {
            @Override
            public void onSuccess(String strJson) {
                Common.getInstance().hideProgressDialog();
                try {
                    JSONObject jsonResponse = new JSONObject(strJson);
                    JSONObject jsonUserInfo = jsonResponse.getJSONObject(Constant.key_user);

                    jsonUserInfo.put(Constant.key_password, password);
                    jsonUserInfo.put(Constant.key_company, company);

                    APIManager.getInstance().saveUserDetails(jsonUserInfo);

                    finish();
                    overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Common.getInstance().showAlert("Error", e.getLocalizedMessage(), SignupActivity.this, new Common.OnOkListener() {
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
                Common.getInstance().showAlert("Signup Failed", strErr, SignupActivity.this, new Common.OnOkListener() {
                    @Override
                    public void onOk() {

                    }
                });
            }
        });
    }
    private void gotoCountryActivity() {

        Intent intent = new Intent(this, CountryActivity.class).putExtra(Constant.SelectedCountryIndex, nSelectedCountryIndex);
        startActivityForResult(intent, PICK_COUNTRY_REQUEST);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == PICK_COUNTRY_REQUEST){
                nSelectedCountryIndex = data.getIntExtra(Constant.SelectedCountryIndex, -1);
                if(nSelectedCountryIndex > -1) {
                    try {
                        countryInfo = APIManager.getInstance().jsonArrayCountryInfo.getJSONObject(nSelectedCountryIndex);
                        etCountry.setText(countryInfo.getString(Constant.LABEL));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
