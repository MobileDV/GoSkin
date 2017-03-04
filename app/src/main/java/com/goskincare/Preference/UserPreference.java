package com.goskincare.Preference;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Morning on 11/24/2015.
 */

public class UserPreference {

    public SharedPreferences pref;

    private static UserPreference mInstance = null;

    public static UserPreference getInstance() {
        if(null == mInstance) {
            mInstance = new UserPreference();
        }

        return mInstance;
    }

    public void putSharedPreference(String key, String value) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void putSharedPreference(String key, boolean value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void putSharedPreference(String key, int value) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void putSharedPreference(String key, long value) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void putSharedPreference(String key, JSONObject jsonObject){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, jsonObject.toString());
        editor.commit();
    }

    public String getSharedPreference(String key,
                                      String defaultValue) {
        return pref.getString(key, defaultValue);
    }

    public int getSharedPreference(String key, int defaultValue) {
        return pref.getInt(key, defaultValue);
    }

    public long getSharedPreference(String key,
                                    long defaultValue) {
        return pref.getLong(key, defaultValue);
    }

    public boolean getSharedPreference(String key,
                                       boolean defaultValue) {
        return pref.getBoolean(key, defaultValue);
    }

    public JSONObject getSharedPreferences(String key, JSONObject defaultValue) {
        String strJson = pref.getString(key, "{}");
        if(strJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(strJson);

                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return new JSONObject();
    }
}
