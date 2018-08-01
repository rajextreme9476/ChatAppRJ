package com.datavim.chatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by apple on 27/05/15.
 */
public class PreferenceManager {
    public static final String APPLICATION_PREFERENCE = "SportOPreference";

    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_TOKEN_TYPE = "token_type";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";
    public static final String KEY_APP_VERSION = "app_version";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_JID = "jid";
    public static final String KEY_FILENAME = "file_name";
    public static final String KEY_DEVICE_TYPE = "device_type";
    public static final String KEY_USERNAME = "user_name";

    public static final String KEY_CONTACTUS_URL = "contactUsUrl";
    public static final String KEY_FAQ_URL = "faqsUrl";
    public static final String KEY_PRIVACY_POLICY_URL = "privacyPolicyUrl";
    public static final String KEY_TERMSOFUSE_URL = "termsOfUseUrl";
    public static final String KEY_USER_PASSWORD = "password";
    public static final String KEY_USER_SPORTS_LEVEL = "sport_level";
    public static final String KEY_LONGITUDE = "key_longitude";
    public static final String KEY_LATITUDE = "key_lotitude";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NAME = "name";
    public static final String KEY_GENDER = "gender";
    public static final String READ_RECEIPT = "Read_Receipt";
    public static final String KEY_SPORT_NAME = "sport_name";
    public static final String KEY_FIRST_INSTALATION = "firstInstalation";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CHAT_PASSWORD = "chatPassword";
    public static final String USER_RATING = "user_rting";
    public static final String KEY_CONNECTING = "connecting";

    public static final String KEY_GET_APP_SHARE_TEXT = "getAppShareText";
    public static final String KEY_GET_APP_SHARE_URL = "getAppShareUrl";
    public static final String KEY_HOME_INSTALATION = "home_installation";
    public static final String KEY_TERM_CONDITION_URL = "termAndConditionUrl";

    public static final String KEY_PLAYSTORE_RATEAPP = "rateApp";
    public static final String KEY_CURRENT_RADIUS = "radius";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_IS_LOGIN = "login";
    public static final String KEY_IS_CALL_API = "api";
    public static final String KEY_APP_DOWNLOAD_URL = "appShareUrl";
    public static final String KEY_ABOUT_US = "about_us";
    public static final String KEY_FEED_KEYWORD_Filter = "feedkeywordfilter";
    public static final String KEY_HOST_MIN_TIME = "host_min_time";

    public static final String KEY_CURRENT_LOCATION = "location";
    public static final String KEY_SERVICE_TYPE = "service_type";
    public static final String KEY_PROFILE_PIC_URL = "profile_pic_url";
    public static final String KEY_PAGE_LIMIT = "page_limit";

    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_GENDER = "user_gender";
    public static final String KEY_USER_MOBILENO = "user_mobile";
    public static final String KEY_USER_EMAILID = "user_emailid";
    public static final String KEY_EVENT_TIME_LIMIT = "event_time_limit";

    public static final String KEY_USER_STATUS = "key_user_status";

    public static boolean savePreference(Context context, String key, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static boolean savePreference(Context context, String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    public static String getPreference(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);
        return value;

    }

    public static int getPreferenceInt(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        int value = sharedPref.getInt(key, 0);
        return value;

    }

    public static boolean savePreference(Context context, String key, boolean value) {
        SharedPreferences sharedPref = context.getSharedPreferences(APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    public static boolean getPreferenceBoolean(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Boolean value = sharedPref.getBoolean(key, false);
        return value;
    }

    public static void ClearPreference(Context mcontext) {
        SharedPreferences preferences = mcontext.getSharedPreferences(APPLICATION_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

    }
}
