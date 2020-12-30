package com.digiomega.hangrymatesrestaurants.AllConstant;

/**
 * Created by Dinosoftlabs on 10/18/2019.
 */
public class Config {


  /*   public static String baseURL = "http://35.154.147.138/mobileapp_api/api/";
    
    public static String earning_url = "http://35.154.147.138/restaurant/dashboard.php?p=earning&user_id=ID&type=restaurant";*/


    public static String baseURL = "https://rintechaqua.com/mobileapp_api/api/";
 public static String earning_url = "https://rintechaqua.com/restaurant/index1.php";

   public static final String Privacy_policy="https://hangrymates.com.au/privacy-policy.html";
   public static final String Terms_of_Use="https://docs.google.com/document/d/e/2PACX-1vR5dTurJFz7uckPlNOpttBAcfRdo77PUI705c4P9wi6SJ7UikJBubYKqUvrekM2BQ/pub";


   //URL to our login.php file
    public static final String LOGIN_URL = baseURL+"login";

    public static final String CHANGE_PASSWORD = baseURL+"changePassword";
    public static final String SHOW_ORDER_DETAIL = baseURL+"showOrderDetail";


    /// All Admin Apis
    public static final String ACCEPT_DECLINE_STATUS = baseURL+"updateRestaurantOrderStatus";
    public static final String SHOW_REST_COMPLETE_ORDER = baseURL+"showRestaurantCompletedOrders";


    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;






}
