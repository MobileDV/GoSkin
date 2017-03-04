package com.goskincare.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.manager.APIManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morning on 11/26/2015.
 */
public class Common {
    /**
     * Progress dialog
     */

    static Common instance = null;

    public static Common getInstance() {
        if(instance == null){
            instance = new Common();
        }

        return instance;
    }

    public interface OnOkListener {
        public void onOk();
    }

    Dialog mProgressDialog;

    public  void showProgressDialog(Context context, String message) {
        mProgressDialog = new Dialog(context, R.style.Dialog_No_Border);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);

        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(dialogView);

        ((TextView)dialogView.findViewById(R.id.tvMsg)).setText(message);

        ImageView imgvCreamIcon = (ImageView)dialogView.findViewById(R.id.imgvCreamIcon);
        imgvCreamIcon.setBackgroundResource(R.drawable.progress_dialog_spinner);

        AnimationDrawable animationDrawable = (AnimationDrawable)imgvCreamIcon.getBackground();

        animationDrawable.start();

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showAlertOther(String title, String msg, Context context, final OnOkListener okListener){
        final Dialog dialog;

        dialog = new Dialog(context, R.style.Dialog_No_Border);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        ((TextView)dialogView.findViewById(R.id.tvMsg)).setText(msg);

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okListener.onOk();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showAlert(String title, String msg, Context context, final OnOkListener okListener){
        final Dialog dialog;

        dialog = new Dialog(context, R.style.Dialog_No_Border);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.alert_dialog_common, null);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        ((TextView)dialogView.findViewById(R.id.tvMsg)).setText(msg);

        dialogView.findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okListener.onOk();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid(String str) {

        return str.length() > 0;
    }

    public String stringFromDate(Date date, SimpleDateFormat format) {

        String strDate = format.format(date);

        return strDate;
    }

    public Date dateFromString(String strDate, SimpleDateFormat format) {
        try {
            Date date = format.parse(strDate);

            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getServerURL(){

        if(APIManager.getInstance().isProduction){
            return Constant.ServerProductionUrl;
        }

        return Constant.ServerSandboxUrl;
    }
    
}
