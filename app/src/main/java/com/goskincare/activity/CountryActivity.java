package com.goskincare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goskincare.R;
import com.goskincare.custom.CustomActivity;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class CountryActivity extends CustomActivity {
    LinearLayout lyCotainer;
    int nSelectedCountryIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        initData();
        setUI();
        presentCountry();
    }

    public void initData() {
        nSelectedCountryIndex = getIntent().getIntExtra(Constant.SelectedCountryIndex, -1);
    }

    private void setUI() {
        lyCotainer = (LinearLayout)findViewById(R.id.lyContainerCountry);
    }

    private void presentCountry() {
        lyCotainer.removeAllViews();

        for(int i = 0; i < APIManager.getInstance().jsonArrayCountryInfo.length(); i ++){
            LayoutInflater inflater = LayoutInflater.from(this);
            View viewItem = inflater.inflate(R.layout.cell_item_country, null);

            TextView tvTitle = (TextView)viewItem.findViewById(R.id.tvTitle);
            final ImageView imgvCheck = (ImageView)viewItem.findViewById(R.id.imgvCheck);

            try {
                JSONObject jsonObject = APIManager.getInstance().jsonArrayCountryInfo.getJSONObject(i);
                tvTitle.setText(jsonObject.getString(Constant.LABEL));

                if(i == nSelectedCountryIndex){
                    imgvCheck.setVisibility(View.VISIBLE);
                }else{
                    imgvCheck.setVisibility(View.INVISIBLE);
                }

                final int nIndex = i;

                viewItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constant.SelectedCountryIndex, nIndex);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                    }
                });

                lyCotainer.addView(viewItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
