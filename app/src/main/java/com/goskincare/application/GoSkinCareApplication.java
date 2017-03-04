package com.goskincare.application;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.goskincare.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Morning on 11/24/2015.
 */
public class GoSkinCareApplication extends Application {

    public static final String TAG = GoSkinCareApplication.class.getSimpleName();

    private static GoSkinCareApplication _instance;

    private Tracker mTracker;

    @Override
    public void onCreate() {

        super.onCreate();
        _instance = this;

        getUserInfo();
    }

    /**
     *  return application instance
     * @return
     */
    public static synchronized GoSkinCareApplication getInstance() {
        return _instance;
    }


    // get sha1 and then print logo
    public void getUserInfo(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public void trackingScreenView(String screenName) {
        if (mTracker == null) {
            getDefaultTracker();
        }

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("page visit")
                .setAction("visit")
                .setLabel(screenName)
                .build());
    }

    public String onFormattedPrice (double price) {
        String formattedString = "";
        double priceFormat = price * 100;
        int priceMode = (int) priceFormat % 100;

        if (priceMode > 0) {
            formattedString = String.format("%.2f", price);
        } else {
            formattedString = String.format("%.2f", price);
        }

        return formattedString;
    }
}
