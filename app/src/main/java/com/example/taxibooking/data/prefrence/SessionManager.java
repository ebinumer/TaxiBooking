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


    public SessionManager(Context context) {
        this.context = context;
        shpref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = shpref.edit();
    }

    public void setLogin(boolean isLoggedin) {
        editor.putBoolean(KEY_IS_LOGED_IN, isLoggedin);
        editor.apply();
        Log.e(TAG, "userlogin session");
    }

    public void setUserId(String name) {
        editor.putString(USERID, name);
        editor.apply();
    }

    public void setUserName(String name) {
        editor.putString(USERNAME, name);
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


    public boolean isLoggedin() {
        return shpref.getBoolean(KEY_IS_LOGED_IN, false);
    }

    public String getUserId() {
        return shpref.getString(USERID, "");
    }

    public String getUserName() {
        return shpref.getString(USERNAME, "");
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


    public void clear() {
        editor.clear();
        editor.apply();
    }
}
