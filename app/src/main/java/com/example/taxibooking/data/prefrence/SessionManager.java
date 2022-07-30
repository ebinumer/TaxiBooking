package com.example.taxibooking.data.prefrence;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();

    private final SharedPreferences shpref;
    private final SharedPreferences.Editor editor;
    Context context;
    private static final String PREF_NAME = "TaxiApp";
    private static final String KEY_IS_LOGED_IN = "isLoggedIn";
    private static final String USERID = "userId";
    private static final String USERNAME = "userName";
    private static final String USERTYPE = "userType";
    private static final String DOCUMENT_ID = "documentId";
    private static final String MOBILE = "mobile";
    private static final String REQ_TRIP = "request_a_trip";
    private static final String MY_LAT = "my_lat";
    private static final String MY_LANG = "my_lang";
    private static final String DESTINATION_LAT = "destination_lat";
    private static final String DESTINATION_LANG = "destination_lang";
    private static final String SELECT_ORDER = "select_order";
    private static final String DRIVER_LAT = "driver_lat";
    private static final String DRIVER_LONG = "driver_lang";
    private static final String ORDER_ID = "order_id";
    private static final String IS_DRIVER = "is_driver";
    private static final String DRIVER_STATUS = "driver_status";


    public SessionManager(Context context) {
        this.context = context;
        shpref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = shpref.edit();
    }

    public void setLogin(boolean isLoggedin) {
        editor.putBoolean(KEY_IS_LOGED_IN, isLoggedin);
        editor.apply();

    }

    public void setUserId(String name) {
        editor.putString(USERID, name);
        editor.apply();
    }

    public void setUserName(String name) {
        editor.putString(USERNAME, name);
        editor.apply();
    }
   public void setDriverStatus(String name) {
        editor.putString(DRIVER_STATUS, name);
        editor.apply();
    }

    public void setIsDriver(Boolean name) {
        editor.putBoolean(IS_DRIVER, name);
        editor.apply();
    }

    public void setOrderId(String name) {
        editor.putString(ORDER_ID, name);
        editor.apply();
    }

    public void setMobile(String name) {
        editor.putString(MOBILE, name);
        editor.apply();
    }

    public void setDocumentId(String id) {
        editor.putString(DOCUMENT_ID, id);
        editor.apply();
    }

    public void setUserTypeId(String type) {
        editor.putString(USERTYPE, type);
        editor.apply();
    }
    public void setReqTrip(Boolean type) {
        editor.putBoolean(REQ_TRIP, type);
        editor.apply();
    }

    public void setMyLat(String type) {
        editor.putString(MY_LAT, type);
        editor.apply();
    }
    public void setMyLang(String type) {
        editor.putString(MY_LANG, type);
        editor.apply();
    }
    public void setDestinationLat(String type) {
        editor.putString(DESTINATION_LAT, type);
        editor.apply();
    }
    public void setDestinationLang(String type) {
        editor.putString(DESTINATION_LANG, type);
        editor.apply();
    }
    public void setSelectOrder(Boolean type) {
        editor.putBoolean(SELECT_ORDER, type);
        editor.apply();
    }

    public void setDriverLat(String type) {
        editor.putString(DRIVER_LAT, type);
        editor.apply();
    }
    public void setDriverLong(String type) {
        editor.putString(DRIVER_LONG, type);
        editor.apply();
    }

    public boolean isLoggedin() {
        return shpref.getBoolean(KEY_IS_LOGED_IN, false);
    }

    public Boolean getIsDriver() {
        return shpref.getBoolean(IS_DRIVER, false);
    }
    public String getUserId() {
        return shpref.getString(USERID, "");
    }
    public String getMobile() {
        return shpref.getString(MOBILE, "");
    }

    public String getUserName() {
        return shpref.getString(USERNAME, "");
    }
    public String getOrderId() {
        return shpref.getString(ORDER_ID, "");
    }

    public String getDocumentId() {
        return shpref.getString(DOCUMENT_ID, "");
    }

    public String getUserType() {
        return shpref.getString(USERTYPE, "");
    }

    public Boolean getReqTrip() {
        return shpref.getBoolean(REQ_TRIP, false);
    }
    public Boolean getSelectOrder() {
        return shpref.getBoolean(SELECT_ORDER, false);
    }

    public String getMyLat() {
        return shpref.getString(MY_LAT, "");
    }
    public String getMyLang() {
        return shpref.getString(MY_LANG, "");
    }
    public String getDestinationLat() {
        return shpref.getString(DESTINATION_LAT, "");
    }
    public String getDestinationLang() {
        return shpref.getString(DESTINATION_LANG, "");
    }
    public String getDriverLat() {
        return shpref.getString(DRIVER_LAT, "");
    }
    public String getDriverLong() {
        return shpref.getString(DRIVER_LONG, "");
    }

    public String getDriverStatus() {
        return shpref.getString(DRIVER_STATUS, "no_selected");
    }

    public void clear() {
        editor.clear();
        editor.apply();
    }
}

