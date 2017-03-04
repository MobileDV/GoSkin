package com.goskincare.activity;

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

public class ResetPasswordActivity extends CustomActivity {
    EditText etEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        APIManager.getInstance().mContext = getBaseContext();
        setUI();
    }

    private void setUI() {
        setClick(R.id.btnReset);
        setClick(R.id.tvCancel);

        etEmail = (EditText)findViewById(R.id.etEmail);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnReset) {
            reset();
        } else {
            finish();
            overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        }

    }

    public void reset() {
        final String strEmail = etEmail.getText().toString().trim();

        if(strEmail.length() < 1) {
            Common.getInstance().showAlert("Error", "Email address should not be empty", ResetPasswordActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etEmail.setFocusableInTouchMode(true);
                    etEmail.requestFocus();
                }
            });

            return;
        }

        if(!Common.getInstance().isEmailValid(strEmail)) {
            Common.getInstance().showAlert("Error", "Email address is not valid", ResetPasswordActivity.this, new Common.OnOkListener() {
                @Override
                public void onOk() {
                    etEmail.setFocusableInTouchMode(true);
                    etEmail.requestFocus();
                }
            });

            return;
        }

        Common.getInstance().showProgressDialog(ResetPasswordActivity.this, "Resetting...");

        APIManager.getInstance().resetPassword(strEmail, new APIManager.OnSuccessListener() {
            @Override
            public void onSuccess(String strJson) {
                Common.getInstance().hideProgressDialog();

                try {
                    JSONObject jsonResponse = new JSONObject(strJson);
                    String strMsg = jsonResponse.getString(Constant.key_message);

                    if(strMsg.length() < 1) strMsg = "Your password is reset. Please check your email.";

                    Common.getInstance().showAlert("", strMsg, ResetPasswordActivity.this, new Common.OnOkListener() {
                        @Override
                        public void onOk() {
                            finish();
                            overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    Common.getInstance().showAlert("Error", e.getLocalizedMessage(), ResetPasswordActivity.this, new Common.OnOkListener() {
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
                Common.getInstance().showAlert("Failed to reset password", strErr, ResetPasswordActivity.this, new Common.OnOkListener() {
                    @Override
                    public void onOk() {

                    }
                });
            }
        });
    }
}
