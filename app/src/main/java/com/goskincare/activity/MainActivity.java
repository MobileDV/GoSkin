package com.goskincare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.goskincare.GCMRegistrationIntentService;
import com.goskincare.Preference.UserPreference;
import com.goskincare.R;
import com.goskincare.adapter.LeftNavAdapter;
import com.goskincare.application.GoSkinCareApplication;
import com.goskincare.custom.CustomActivity;
import com.goskincare.fragment.FastOrderFragment;
import com.goskincare.fragment.HelpFragment;
import com.goskincare.fragment.HistoryFragment;
import com.goskincare.fragment.MagicOrderFragment;
import com.goskincare.fragment.TermsFragment;
import com.goskincare.fragment.UpdateProfileFragment;
import com.goskincare.manager.APIManager;
import com.goskincare.utils.Common;
import com.goskincare.utils.Constant;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.ShippingAddress;

import java.math.BigDecimal;

public class MainActivity extends CustomActivity {

    /** The drawer layout. */
    private DrawerLayout drawerLayout;

    /** ListView for left side drawer. */
    private ListView drawerLeft;

    /** The left navigation list adapter. */
    private LeftNavAdapter adapter;

    private boolean isMenuOpened;

    private String PAYPAL_CONFIG_ENVIRONMENT;
    private  String PAYPAL_CONFIG_CLIENT_ID;
    public PayPalConfiguration payPalConfiguration;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private final int LOGIN_REQUEST = 2;
    private static final String TAG = "MainActivity";
    int nPos;

    public Tracker mTracker;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APIManager.getInstance().mContext = getBaseContext();

        // Obtain the shared Tracker instance.
        GoSkinCareApplication application = (GoSkinCareApplication) getApplication();
        mTracker = application.getDefaultTracker();

        setPaypal();
        setupDrawer();
        setupContainer();

        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    String token = intent.getStringExtra("token");
                    //Displaying the token as toast
//                    Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    void setPaypal() {
        if(APIManager.getInstance().isProduction){
            PAYPAL_CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
            PAYPAL_CONFIG_CLIENT_ID = Constant.PAYPAL_LIVE_CLIENT_ID;
        }else{
            PAYPAL_CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
            PAYPAL_CONFIG_CLIENT_ID = Constant.PAYPAL_SANDBOX_CLIENT_ID;
        }

        payPalConfiguration = new PayPalConfiguration()
                .environment(PAYPAL_CONFIG_ENVIRONMENT)
                .clientId(PAYPAL_CONFIG_CLIENT_ID)
                .acceptCreditCards(true)
                        // The following are only used in PayPalFuturePaymentActivity.
                .merchantName(Constant.PAYPAL_MARCHANT_NAME)
                .merchantPrivacyPolicyUri(Uri.parse(Constant.PAYPAL_MARCHANT_PRIVACY_POLICY_URL))
                .merchantUserAgreementUri(Uri.parse(Constant.PAYPAL_MARCHANT_USER_AGREEMENT_URL));

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        startService(intent);
    }

    public void onBuyPressed(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(MainActivity.this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal("0.01"), "USD", "sample item",
                paymentIntent);
    }

    /*
     * This method shows use of optional payment details and item list.
     */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
                                "sku-12345678"),
                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
                                "USD", "sku-zero-price"),
                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
                                "USD", "sku-33333")
                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "USD", "sample item", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    /*
     * Add app-provided shipping address to payment
     */
    private void addAppProvidedShippingAddress(PayPalPayment paypalPayment) {
        ShippingAddress shippingAddress =
                new ShippingAddress().recipientName("Mom Parker").line1("52 North Main St.")
                        .city("Austin").state("TX").postalCode("78729").countryCode("US");
        paypalPayment.providedShippingAddress(shippingAddress);
    }

    public void toggleMenu() {
        isMenuOpened = !isMenuOpened;

        if(isMenuOpened)
            drawerLayout.openDrawer(drawerLeft);
        else
            drawerLayout.closeDrawers();
    }

    private void setupDrawer()
    {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
//                GravityCompat.START);

        drawerLayout.closeDrawers();
        isMenuOpened = false;

        setupLeftNavDrawer();

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isMenuOpened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isMenuOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /**
     * Setup the left navigation drawer/slider. You can add your logic to load
     * the contents to be displayed on the left side drawer. It will also setup
     * the Header and Footer contents of left drawer. This method also apply the
     * Theme for components of Left drawer.
     */
    private void setupLeftNavDrawer()
    {
        drawerLeft = (ListView) findViewById(R.id.left_drawer);

        View header = getLayoutInflater().inflate(R.layout.left_nav_header, null);

        drawerLeft.addHeaderView(header);

        adapter = new LeftNavAdapter(this, getResources().getStringArray(
                R.array.arr_left_nav_list)); // home, my chat, location, setting, invite friend
        drawerLeft.setAdapter(adapter);
        drawerLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                drawerLayout.closeDrawers();
                if (pos != 7) {
                    boolean isMemberLogin = UserPreference.getInstance().getSharedPreference(Constant.gsc_user_is_guest_login, true);

                    if(isMemberLogin && pos > 1 && pos < 5) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class).putExtra(Constant.isPopUp, true);
                        startActivityForResult(intent, LOGIN_REQUEST);
                        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                        nPos = pos - 1;
                        return;
                    }

                    launchFragment(pos - 1);
                } else
                    logout();

                adapter.setSelection(pos - 1);

            }
        });
    }

    private void setupContainer()
    {
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {

                    @Override
                    public void onBackStackChanged() {

                    }
                });
        launchFragment(0);
    }

    private void logout() {
        Common.getInstance().showAlertOther("Error", "Do you want to log out?", MainActivity.this, new Common.OnOkListener() {
            @Override
            public void onOk() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });

    }

    public void launchFragment(int pos){
        Fragment f = null;
        String title = null;

        switch (pos){
            case 0:
                f = new FastOrderFragment();
                title = Constant.FRAGMENT_FAST_ORDER;
                break;

            case 1:
                f = new MagicOrderFragment();
                title = Constant.FRAGMENT_MAGIC_ORDER;
                break;

            case 2:
                f = new UpdateProfileFragment();
                title = Constant.FRAGMENT_UPDATE_PROFILE;
                break;

            case 3:
                f = new HistoryFragment();
                title = Constant.FRAGMENT_ORDER_HISTORY;
                break;

            case 4:
                f = new TermsFragment();
                title = Constant.FRAGMENT_TERMS_USE;
                break;

            default:
                f = new HelpFragment();
                title = Constant.FRAGMENT_HELP;
        }

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame , f, title).addToBackStack(title).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            {
                getSupportFragmentManager().popBackStackImmediate();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == LOGIN_REQUEST){
                boolean isLoginSuccess = data.getBooleanExtra(Constant.key_success, false);
                if(isLoginSuccess) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            launchFragment(nPos);
                            adapter.setSelection(nPos);
                        }
                    }, 100);
                }
            }
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_ORDER_DETAIL);
//        if(fragment != null){
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}
