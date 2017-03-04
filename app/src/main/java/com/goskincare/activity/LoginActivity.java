package com.goskincare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.goskincare.Preference.UserPreference;
import com.goskincare.R;
import com.goskincare.custom.CustomActivity;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends CustomActivity {
    EditText etEmail, etPswd;
    TextView tvJoin, tvGuest, tvCancel;
    boolean isPopUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        isPopUp = getIntent().getBooleanExtra(Constant.isPopUp, false);
        APIManager.getInstance().mContext = getBaseContext();
        setUI();
    }

    private void setUI() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPswd = (EditText) findViewById(R.id.etPswd);

        tvJoin = (TextView) setClick(R.id.tvJoin);
        tvGuest = (TextView) setClick(R.id.tvGuest);
        tvCancel = (TextView) setClick(R.id.tvCancel);

        if(isPopUp) {
            tvGuest.setVisibility(View.GONE);
            tvCancel.setVisibility(View.VISIBLE);
        } else {
            tvGuest.setVisibility(View.VISIBLE);
            tvCancel.setVisibility(View.GONE);
        }

        tvGuest.setText(Html.fromHtml("<b><font color=gray>JUST BROWSING?</font><br><font color=black>TROT ON IN AS A GUEST</font></b>"));
        tvJoin.setText(Html.fromHtml("<b><font color=gray>NOT A GOCONUT?</font>&nbsp;<font color=black>JOIN NOW!</font></b>"));

        setClick(R.id.btnSignIn);
        setClick(R.id.btnForgot);

        JSONObject userInfo = APIManager.getInstance().getUserDetails();

        if(userInfo.has(Constant.key_email)){
            try {
                etEmail.setText(userInfo.getString(Constant.key_email));
                etPswd.setText(userInfo.getString(Constant.key_password));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignIn) {
            UserPreference.getInstance().putSharedPreference(Constant.gsc_user_is_guest_login, false);

            final String strEmail = etEmail.getText().toString().trim();
            final String strPswd = etPswd.getText().toString().trim();

            if(strEmail.length() < 1){
                Common.getInstance().showAlert("Error", "Email address should not be empty", LoginActivity.this, new Common.OnOkListener() {
                    @Override
                    public void onOk() {
                        etEmail.setFocusableInTouchMode(true);
                        etEmail.requestFocus();
                    }
                });

                return;
            }

            if(!Common.getInstance().isEmailValid(strEmail)) {
                Common.getInstance().showAlert("Error", "Email address is not valid", LoginActivity.this, new Common.OnOkListener() {
                    @Override
                    public void onOk() {
                        etEmail.setFocusableInTouchMode(true);
                        etEmail.requestFocus();
                    }
                });

                return;
            }

            if(!Common.getInstance().isPasswordValid(strPswd)){
                Common.getInstance().showAlert("Error", "Password should not be empty", LoginActivity.this, new Common.OnOkListener() {
                    @Override
                    public void onOk() {
                        etPswd.setFocusableInTouchMode(true);
                        etPswd.requestFocus();
                    }
                });

                return;
            }

            Common.getInstance().showProgressDialog(LoginActivity.this, "Authenticating...");

            APIManager.getInstance().login(strEmail, strPswd, new APIManager.OnSuccessListener() {
                @Override
                public void onSuccess(String strJson) {
                    Common.getInstance().hideProgressDialog();

                    try {
                        JSONObject jsonResponose = new JSONObject(strJson);
                        JSONObject jsonUserInfo = jsonResponose.getJSONObject(Constant.key_user);

                        jsonUserInfo.put(Constant.key_password, strPswd);
                        APIManager.getInstance().saveUserDetails(jsonUserInfo);

                        UserPreference.getInstance().putSharedPreference(Constant.gsc_user_is_guest_login, false);

                        if(isPopUp) {
                            goBack(true);
                        } else {
                            gotoMainActivity();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new APIManager.OnFailListener() {
                @Override
                public void onFail(String strErr) {
                    Common.getInstance().hideProgressDialog();
                    Common.getInstance().showAlert("Login Failed", strErr, LoginActivity.this, new Common.OnOkListener() {
                        @Override
                        public void onOk() {

                        }
                    });
                }
            });
        } else if (v.getId() == R.id.btnForgot) {
            gotoResetActivity();
        } else if (v.getId() == R.id.tvJoin) {
            gotoSignupActivity();
        } else if (v.getId() == R.id.tvGuest) {
            UserPreference.getInstance().putSharedPreference(Constant.gsc_user_is_guest_login, true);
            gotoMainActivity();
        } else if (v.getId() == R.id.tvCancel) {
            goBack(false);
        }
    }

    private void gotoResetActivity() {

        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
    }

    private void gotoSignupActivity() {

        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    private void goBack(boolean isSuccess) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constant.key_success, isSuccess);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
    }

}
