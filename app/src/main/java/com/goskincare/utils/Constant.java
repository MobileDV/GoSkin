package com.goskincare.utils;

/**
 * Created by Morning on 11/25/2015.
 */
public class Constant {

    public static final String API_TOKEN = "DJG93HFQ7";
    public static final String prefName = "GoSkinCare";

    public static final String PAYPAL_LIVE_CLIENT_ID = "AQ6_uhnuoSuC17mfHdmcTIxj5PYyGlv0Vkp2RNlXe3R_MAzXr32PPju1fhE6hwku_g0on2zBILTEM2Ae";
    public static final String PAYPAL_SANDBOX_CLIENT_ID = "AQq6gYTF1MVJ8zRXRCNZHsgifm44YND0jsd45gBfomaiakJHASTdTrZq9ZIUXeBRi4gBhoRUst6E8jt2";
    public static final String PAYPAL_MARCHANT_NAME = "Flydigital";
    public static final String PAYPAL_MARCHANT_PRIVACY_POLICY_URL = "https://www.paypal.com/webapps/mpp/ua/privacy-full";
    public static final String PAYPAL_MARCHANT_USER_AGREEMENT_URL = "https://www.paypal.com/webapps/mpp/ua/useragreement-full";

    public static final String ServerSandboxUrl = "http://staging.gotoskincare.com/apiv2";
    public static final String ServerProductionUrl = "https://www.gotoskincare.com/apiv2";

    public static final String ServerUrl = Common.getServerURL();
    public static final String LoginUrl = ServerUrl + "/user/login/";
    public static final String SignupUrl = ServerUrl + "/user/register/";
    public static final String VerifyAddressUrl = ServerUrl + "/user/verifyaddress";
    public static final String UpdateProfileUrl = ServerUrl + "/user/updateProfile/";
    public static final String ResetPasswordUrl = ServerUrl + "/user/resetPassword/";
    public static final String ProductListUrl = ServerUrl + "/product/list";
    public static final String OrderCalculateUrl = ServerUrl + "/order/calculate/";
    public static final String OrderPlaceUrl = ServerUrl + "/order/placeOrder/";
    public static final String OrderListUrl = ServerUrl + "/order/list/";
    public static final String CountryListUrl = ServerUrl + "/library/getCountryCodes";
    public static final String PlaceIncompleteOrderUrl = ServerUrl + "/order/placeIncompleteOrder/";
    public static final String PlaceConfirmOrderUrl = ServerUrl + "/order/confirmOrder/";
    public static final String PayByTokenUrl = ServerUrl + "/order/payByToken/";
    public static final String TermsPageUrl = "http://www.gotoskincare.com/apiv2/pages/terms/";
    public static final String HelpPageUrl = "http://www.gotoskincare.com/apiv2/pages/help/";
    public static final String MagicOrderHelpPageUrl = "http://www.gotoskincare.com/apiv2/pages/magicOrder/";
    public static final String CreditCardListUrl = ServerUrl + "/user/creditCardList/";
    public static final String CreditCardAddUrl = ServerUrl + "/user/creditCardAdd/";
    public static final String CreditCardDeleteUrl = ServerUrl + "/user/creditCardDelete/";
    public static final String CreditCardMakeFavoriteUrl = ServerUrl + "/user/creditCardMakeFavourite/";
    public static final String GetMagicOrderUrl = ServerUrl + "/magicOrders/getMagicOrder/";
    public static final String AddMagicOrderUrl = ServerUrl + "/magicOrders/addMagicOrder/";


    public static final int SPLASH_TIME = 1500;

    public static final String FRAGMENT_FAST_ORDER = "fast_order";
    public static final String FRAGMENT_MAGIC_ORDER = "magic_order";
    public static final String FRAGMENT_UPDATE_PROFILE = "update_profile";
    public static final String FRAGMENT_ORDER_HISTORY = "order_history";
    public static final String FRAGMENT_HISTORY_DETAIL = "history_detail";
    public static final String FRAGMENT_TERMS_USE = "terms_use";
    public static final String FRAGMENT_HELP = "help";
    public static final String FRAGMENT_ORDER_DETAIL = "order_detail";
    public static final String FRAGMENT_ORDER_RESULT = "order_result";
    public static final String FRAGMENT_CREDIT_CARD = "credit_card";
    public static final String FRAGMENT_MAGIC_ORDER_HELP = "magic_order_help";

    public static final String GA_SCREENNAME_FAST_ORDER = "Fast Order";
    public static final String GA_SCREENNAME_MAGIC_ORDER = "Magic Order";
    public static final String GA_SCREENNAME_MY_PROFILE = "My Profile";
    public static final String GA_SCREENNAME_ORDER_HISTORY = "Order History";
    public static final String GA_SCREENNAME_TERMS_OF_USE = "Terms of Use";
    public static final String GA_SCREENNAME_HELP = "Help";

    public static final String SelectedCountryIndex = "selectedCountryIndex";
    public static final String TRUE = "true";

    public static final String CODE = "code";
    public static final String LABEL = "label";

    public static String gsc_user_details = "gsc_user_details";
    public static String gsc_user_is_guest_login = "gsc_user_is_guest_login";

    public static String key_success          ="success";
    public static String key_message          ="message";

    public static String key_apitoken         ="apitoken";
    public static String key_user             ="user";
    public static String key_userId           ="userId";
    public static String key_userid           ="userid";

    public static String key_nickname         ="nickname";
    public static String key_email            ="email";
    public static String key_password         ="password";
    public static String key_name             ="name";
    public static String key_firstname        ="firstname";
    public static String key_surname          ="surname";
    public static String key_company          ="company";

    public static String key_address          ="address";
    public static String key_address_country  ="address_country";
    public static String key_address_street   ="address_street";
    public static String key_address_street_2 ="address_street_2";
    public static String key_address_suburb   ="address_suburb";
    public static String key_address_state    ="address_state";
    public static String key_address_postcode ="address_postcode";

    public static String key_isValidAddress   ="isValidAddress";
    public static String key_recommendations  ="recommendations";

    public static String key_countries        ="countries";
    public static String key_country          ="country";
    public static String key_code             ="code";
    public static String key_label            ="label";

    public static String key_products         ="products";
    public static String key_productName      ="productName";
    public static String key_productId        ="productId";
    public static String key_price            ="price";
    public static String key_priceCurrency    ="priceCurrency";
    public static String key_priceCurrencySymbol = "priceCurrencySymbol";
    public static String key_detail           ="detail";
    public static String key_imageUrlSmall    ="imageUrlSmall";
    public static String key_imageUrlMedium   ="imageUrlMedium";
    public static String key_imageUrlLarge    ="imageUrlLarge";

    public static String key_orders           ="orders";
    public static String key_order            ="order";
    public static String key_orderId          ="orderId";
    public static String key_orderid          ="orderid";
    public static String key_items            ="items";
    public static String key_amount           ="amount";
    public static String key_sendExpress      ="sendExpress";
    public static String key_isGift           ="isGift";
    public static String key_itemPrice        ="itemPrice";
    public static String key_productsPrice    ="productsPrice";
    public static String key_shippingPrice    ="shippingPrice";
    public static String key_shippingName     ="shippingName";
    public static String key_shippingCode     ="shippingCode";
    public static String key_totalPrice       ="totalPrice";
    public static String key_status           ="status";
    public static String key_placeDate        ="placeDate";
    public static String key_countryCode      ="countryCode";

    public static String key_sku              ="sku";
    public static String key_payPal_reference ="payPal_reference";
    public static String key_response         ="response";
    public static String key_id               ="id";
    public static String key_address_to       ="address_to";

    public static String key_cardnumber       ="cardnumber";
    public static String key_UserId           ="UserId";
    public static String key_expirymonth      ="expirymonth";
    public static String key_expiryyear       ="expiryyear";
    public static String key_cardid           ="cardid";
    public static String key_pan              ="pan";
    public static String key_favourite        ="favourite";
    public static String key_cards            ="cards";

    public static String key_frequency        ="frequency";
    public static String key_magicOrders      ="magicOrders";
    public static String key_nextorderdate    ="nextorderdate";
    public static String key_orderDetails     ="orderDetails";

    public static String isPopUp              ="isPopUp";
}
