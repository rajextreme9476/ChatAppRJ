package com.datavim.chatapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.inputmethod.InputMethodManager;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by apple on 27/07/15.
 */
public class UtilityClass {

    Context context;
    /*Dev*/
  //  public static String SENDER_ID="29144251045";

   //*PP*/
    public static String SENDER_ID="29144251045";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";



    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c)* 0.001;
        return dist;

    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null && inputManager != null) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                inputManager.hideSoftInputFromInputMethod(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }



    public static String getDate(long date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM  hh:mm a");
        return dateFormat.format(date);
    }
    public static String getDateNew(long date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(date);
    }
    public static String getUserName(Context context) {
        String userName = PreferenceManager.getPreference(context, PreferenceManager.KEY_JID);
        return userName;
    }

    public static String getToDate(long date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(date);
    }

    public static String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "hh:mm a";
        if(now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ){
            return "" + DateFormat.format(timeFormatString, smsTime);
        }else if(now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 ){
            return "Yesterday ";
        }else if(now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)){
            return DateFormat.format("dd/MM/yyyy", smsTime).toString();
        }else
            return DateFormat.format("dd/MM/yyyy", smsTime).toString();
    }

    @SuppressLint("NewApi")
    public static final void recreateActivityCompat(final Activity a) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            a.recreate();
        } else {
            final Intent intent = a.getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            a.finish();
            a.overridePendingTransition(0, 0);
            a.startActivity(intent);
            a.overridePendingTransition(0, 0);
        }
    }

    public static String getOnlyDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM");
        return dateFormat.format(date);
    }

    public static String getTime(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(date);
    }

    public static String getDateAvalible(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd MMM yyyy");
        return dateFormat.format(date);
    }


    public static String getDateAvalibleGallery(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(date);
    }



    public static String getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(packageInfo.versionName);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


}

