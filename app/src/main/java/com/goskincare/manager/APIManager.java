package com.goskincare.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.goskincare.CustomHttpClient.SSLSocketFactoryEx;
import com.goskincare.Preference.UserPreference;
import com.goskincare.utils.Constant;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 6/10/2016.
 */
public class APIManager {
    static APIManager instance = null;
    private OnSuccessListener successListener;
    private OnFailListener failListener;
    public JSONArray jsonArrayCountryInfo;
    public ArrayList<JSONObject> arrayListCreditCards;
    public int nFavoriteCardIndex;
    public boolean isProduction;
    public Context mContext;

    public interface OnSuccessListener {
        public void onSuccess(String strJson);
    }

    public interface OnFailListener {
        public void onFail(String strErr);
    }

    public static APIManager getInstance() {
        if(instance == null){
            instance = new APIManager();
        }

        return instance;
    }

    public int indexOfCountry(String countryCode) {
        int idx = 0;

        for(idx = 0; idx < jsonArrayCountryInfo.length(); idx ++) {
            try {
                JSONObject jsonItem = jsonArrayCountryInfo.getJSONObject(idx);
                if(countryCode.equals(jsonItem.getString(Constant.CODE))) break;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return idx;
    }

    public String formattedUserName() {
        JSONObject jsonUserInfo = getUserDetails();

        try {
            return jsonUserInfo.getString(Constant.key_firstname) + " " + jsonUserInfo.getString(Constant.key_surname);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String formattedAddress() {
        JSONObject jsonUserInfo = getUserDetails();

        try {
            return jsonUserInfo.getString(Constant.key_address_street) + ", " + jsonUserInfo.getString(Constant.key_address_suburb) + ", " + jsonUserInfo.getString(Constant.key_address_state) + " " + jsonUserInfo.getString(Constant.key_address_postcode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public JSONObject getFavoriteCreditCard() {
        if(arrayListCreditCards.size() > 0 && nFavoriteCardIndex >= 0){
            return arrayListCreditCards.get(nFavoriteCardIndex);
        }

        return null;
    }

    String getUrlWith(String mainUrl){
        return String.format("%s?%s=%s", mainUrl, Constant.key_apitoken, Constant.API_TOKEN);
    }

    public JSONObject getUserDetails() {
        return UserPreference.getInstance().getSharedPreferences(Constant.gsc_user_details, new JSONObject());
    }

    public void saveUserDetails(JSONObject jsonObject) {
        if(jsonObject == null) return;

        try {
            if(jsonObject.has("userid") && !jsonObject.has(Constant.key_userId)) {
                String strVal = jsonObject.getString("userid");
                jsonObject.put(Constant.key_userId, strVal);

                UserPreference.getInstance().putSharedPreference(Constant.gsc_user_details, jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String getUserId() {
        JSONObject userDetails = getUserDetails();

        if(userDetails!= null) {
            try {
                return userDetails.getString(Constant.key_userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return "111";
    }

    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    List<NameValuePair> getUrlParamsFromJson(String strHeader, Object object){
        List<NameValuePair> ans = new ArrayList<NameValuePair>();

        if(object instanceof String) {
            String strVal = (String)object;
            ans.add(new BasicNameValuePair(strHeader, strVal));
        } else if(object instanceof Integer) {
            Integer nVal = (Integer)object;
            ans.add(new BasicNameValuePair(strHeader, nVal.toString()));
        } else if(object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject)object;
            ArrayList<String> arrayList = new ArrayList<String>();

            for(int i = 0; i < jsonObject.names().length(); i ++)
            {
                try {
                    arrayList.add(jsonObject.names().getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(arrayList);

            for(String key : arrayList) {
                try {
                    Object childObj = jsonObject.get(key);
                    String strNextHeader = key;
                    if(strHeader.length() > 0) strNextHeader = strHeader + "[" + key + "]";
                    ans.addAll(getUrlParamsFromJson(strNextHeader, childObj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else if(object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray)object;

            for(int i = 0; i < jsonArray.length(); i ++){
                try {
                    Object childObj = jsonArray.get(i);
                    ans.addAll(getUrlParamsFromJson(strHeader + "[]", childObj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return ans;
    }

    void doHttpPost(String strUrl, JSONObject params, OnSuccessListener successListener, OnFailListener failListener){
        this.successListener = successListener;
        this.failListener = failListener;

        List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

        if(params != null) {
            Iterator<String> iter = params.keys();
            while (iter.hasNext()) {
                String key = iter.next();

                try {
                    String value = params.getString(key);
                    nameValuePairList.add(new BasicNameValuePair(key, value));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            new PostRequestTask().execute(strUrl, new UrlEncodedFormEntity(nameValuePairList));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    void doOtherHttpPost(String strUrl, JSONObject params, OnSuccessListener successListener, OnFailListener failListener){
        this.successListener = successListener;
        this.failListener = failListener;

        List<NameValuePair> nameValuePairList = getUrlParamsFromJson("", params);

        try {
            new PostRequestTask().execute(strUrl, new UrlEncodedFormEntity(nameValuePairList));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    void doHttpGet(String strUrl, JSONObject paramas, OnSuccessListener successListener, OnFailListener failListener){
        String strParams = "";
        boolean isFirstParam = !strUrl.contains("?");
        this.successListener = successListener;
        this.failListener = failListener;


        if(paramas != null) {
            Iterator<String> iter = paramas.keys();

            while (iter.hasNext()) {
                String key = iter.next();

                try {
                    String value = URLEncoder.encode(paramas.getString(key), "utf-8");
                    if (isFirstParam) {
                        strParams = "?" + key + "=" + value;
                        isFirstParam = false;
                    } else {
                        strParams = strParams + "&" + key + "=" + value;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        new GetRequestTask().execute(strUrl + strParams);


    }

    class GetRequestTask extends AsyncTask<String, Integer, Boolean> {
        String strResponse;

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    String strResult = jsonObject.getString(Constant.key_success);

                    if(strResult.equals(Constant.TRUE)){
                        successListener.onSuccess(strResponse);
                    } else {
                        failListener.onFail(jsonObject.getString(Constant.key_message));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    failListener.onFail("Data is incorrect");
                }
            }else{
                failListener.onFail("Network Error");
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpClient httpClient = getNewHttpClient();

            try {
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    strResponse = EntityUtils.toString(httpResponse.getEntity());
                    return true;
                }
            } catch (ClientProtocolException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    class PostRequestTask extends AsyncTask<Object, Object, Boolean> {
        String strResponse;

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                try {
                    JSONObject jsonObject = new JSONObject(strResponse);

                    if(jsonObject.has(Constant.key_magicOrders)){
                        JSONObject jsonMain = jsonObject.getJSONObject(Constant.key_magicOrders);
                        String strResult = jsonMain.getString(Constant.key_success);

                        if(strResult.equals(Constant.TRUE)){
                            successListener.onSuccess(jsonMain.getString(Constant.key_message));
                        } else {
                            failListener.onFail(jsonMain.getString(Constant.key_message));
                        }

                    }else{
                        String strResult = jsonObject.getString(Constant.key_success);

                        if(strResult.equals(Constant.TRUE)){
                            successListener.onSuccess(strResponse);
                        } else {
                            failListener.onFail(jsonObject.getString(Constant.key_message));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    failListener.onFail("Network Error");
                }
            }else{
                failListener.onFail("Network Error");
            }
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                HttpClient httpClient = getNewHttpClient();
                String strUrl = (String)params[0];
                HttpPost httpPost = new HttpPost(strUrl);
                UrlEncodedFormEntity urlEncodedFormEntity = (UrlEncodedFormEntity)params[1];
                httpPost.setEntity(urlEncodedFormEntity);

                httpPost.setHeader("Accept", "application/x-www-form-urlencoded");
                httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

                HttpResponse httpResponse = httpClient.execute(httpPost);

                if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    strResponse = EntityUtils.toString(httpResponse.getEntity());
                    return true;
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }
    }


    public void login(String strEmail, String strPswd, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_email, strEmail);
            jsonObject.put(Constant.key_password, strPswd);

            doHttpPost(Constant.LoginUrl, jsonObject, successListener, failListener);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void verifyAddress (String countryCode, String street, String street2, String suburb, String state, String postCode, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_address_country, countryCode);
            jsonObject.put(Constant.key_address_street, street);
            jsonObject.put(Constant.key_address_street_2, street2);
            jsonObject.put(Constant.key_address_suburb, suburb);
            jsonObject.put(Constant.key_address_state, state);
            jsonObject.put(Constant.key_address_postcode, postCode);

            doHttpGet(getUrlWith(Constant.VerifyAddressUrl), jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void signup(String firstname, String surname, String nickname, String email, String password, String company, String countryCode, String street, String street2, String suburb, String state, String postCode, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_firstname, firstname);
            jsonObject.put(Constant.key_surname, surname);
            jsonObject.put(Constant.key_nickname, nickname);
            jsonObject.put(Constant.key_email, email);
            jsonObject.put(Constant.key_password, password);
            jsonObject.put(Constant.key_company, company);
            jsonObject.put(Constant.key_address_country, countryCode);
            jsonObject.put(Constant.key_address_street, street);
            jsonObject.put(Constant.key_address_street_2, street2);
            jsonObject.put(Constant.key_address_suburb, suburb);
            jsonObject.put(Constant.key_address_state, state);
            jsonObject.put(Constant.key_address_postcode, postCode);

            doHttpPost(Constant.SignupUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void updateProfile(String firstname, String surname, String nickname, String email,String password, String company, String countryCode, String street, String street2, String suburb, String state, String postCode, boolean createProfile, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        if (createProfile) {
            try {
                jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
                jsonObject.put(Constant.key_firstname, firstname);
                jsonObject.put(Constant.key_surname, surname);
                jsonObject.put(Constant.key_nickname, nickname);
                jsonObject.put(Constant.key_email, email);
                jsonObject.put(Constant.key_password, password);
                jsonObject.put(Constant.key_company, company);
                jsonObject.put(Constant.key_address_country, countryCode);
                jsonObject.put(Constant.key_address_street, street);
                jsonObject.put(Constant.key_address_street_2, street2);
                jsonObject.put(Constant.key_address_suburb, suburb);
                jsonObject.put(Constant.key_address_state, state);
                jsonObject.put(Constant.key_address_postcode, postCode);

                doHttpPost(Constant.SignupUrl, jsonObject, successListener, failListener);
            } catch (JSONException e) {
                e.printStackTrace();
                failListener.onFail("Data is incorrect");
            }
        } else {
            try {
                jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
                jsonObject.put(Constant.key_userId, getUserId());
                jsonObject.put(Constant.key_firstname, firstname);
                jsonObject.put(Constant.key_surname, surname);
                jsonObject.put(Constant.key_nickname, nickname);
                jsonObject.put(Constant.key_email, email);
                jsonObject.put(Constant.key_password, password);
                jsonObject.put(Constant.key_company, company);
                jsonObject.put(Constant.key_address_country, countryCode);
                jsonObject.put(Constant.key_address_street, street);
                jsonObject.put(Constant.key_address_street_2, street2);
                jsonObject.put(Constant.key_address_suburb, suburb);
                jsonObject.put(Constant.key_address_state, state);
                jsonObject.put(Constant.key_address_postcode, postCode);

                String oldPassword = getUserDetails().getString(Constant.key_password);
                if(password == null || password.length() < 1 || password.equals(oldPassword)){
                    jsonObject.remove(Constant.key_password);
                }

                doHttpPost(Constant.UpdateProfileUrl, jsonObject, successListener, failListener);
            } catch (JSONException e) {
                e.printStackTrace();
                failListener.onFail("Data is incorrect");
            }

        }
    }

    public void resetPassword(String email, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_email, email);

            doHttpPost(Constant.ResetPasswordUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void getProductList(OnSuccessListener successListener, OnFailListener failListener) {
        doHttpGet(getUrlWith(Constant.ProductListUrl), null, successListener, failListener);
    }

    public void calculateOrderPrice(JSONObject order, OnSuccessListener successListener, OnFailListener failListener) {
        try {
            order.put(Constant.key_apitoken, Constant.API_TOKEN);
            doOtherHttpPost(Constant.OrderCalculateUrl, order, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void placeOrderPrice(JSONObject order, OnSuccessListener successListener, OnFailListener failListener) {
        try {
            order.put(Constant.key_apitoken, Constant.API_TOKEN);
            doHttpPost(Constant.OrderPlaceUrl, order, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void payByTokenWithOrderID(String orderId, String cardId, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonParams.put(Constant.key_userid, getUserId());
            jsonParams.put(Constant.key_orderId, orderId);
            jsonParams.put(Constant.key_cardid, cardId);

            doHttpPost(Constant.PayByTokenUrl, jsonParams, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void placeIncompleteOrder(JSONObject order, OnSuccessListener successListener, OnFailListener failListener) {
        try {
            order.put(Constant.key_apitoken, Constant.API_TOKEN);
            doOtherHttpPost(Constant.PlaceIncompleteOrderUrl, order, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void confirmOrder(String orderId, String paypalReference, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_orderId, orderId);
            jsonObject.put(Constant.key_payPal_reference, paypalReference);

            doHttpPost(Constant.PlaceConfirmOrderUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void getOrderList(OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_userId, getUserId());

            doHttpPost(Constant.OrderListUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void getCountries(OnSuccessListener successListener, OnFailListener failListener) {
        doHttpGet(getUrlWith(Constant.CountryListUrl), null, successListener, failListener);
    }

    public void getCreditCardList(OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_UserId, getUserId());

            doHttpPost(Constant.CreditCardListUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void addCreditCard(String cardNumber, String expiryMonth, String expiryYear, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_UserId, getUserId());
            jsonObject.put(Constant.key_cardnumber, cardNumber);
            jsonObject.put(Constant.key_expirymonth, expiryMonth);
            jsonObject.put(Constant.key_expiryyear, expiryYear);

            doHttpPost(Constant.CreditCardAddUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void deleteCreditCard(String cardId, String pan, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_UserId, getUserId());
            jsonObject.put(Constant.key_cardid, cardId);
            jsonObject.put(Constant.key_pan, pan);

            doHttpPost(Constant.CreditCardDeleteUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void makeFavoriteCreditCard(String cardId, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_UserId, getUserId());
            jsonObject.put(Constant.key_cardid, cardId);

            doHttpPost(Constant.CreditCardMakeFavoriteUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void getMagicOrder(OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_UserId, getUserId());

            doHttpGet(Constant.GetMagicOrderUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }

    public void addMagicOrder(JSONArray orderDetails, String cardId, OnSuccessListener successListener, OnFailListener failListener) {
        JSONObject jsonObject = new JSONObject();
        JSONObject magicOrderInfo = new JSONObject();

        try {
            magicOrderInfo.put(Constant.key_UserId, getUserId());
            magicOrderInfo.put(Constant.key_cardid, cardId);
            magicOrderInfo.put(Constant.key_orderDetails, orderDetails);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(magicOrderInfo);

            jsonObject.put(Constant.key_apitoken, Constant.API_TOKEN);
            jsonObject.put(Constant.key_magicOrders, jsonArray);

            doOtherHttpPost(Constant.AddMagicOrderUrl, jsonObject, successListener, failListener);
        } catch (JSONException e) {
            e.printStackTrace();
            failListener.onFail("Data is incorrect");
        }
    }
}
