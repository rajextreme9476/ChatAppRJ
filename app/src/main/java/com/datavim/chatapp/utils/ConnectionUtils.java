package com.datavim.chatapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by apple on 26/05/15.
 */
public class ConnectionUtils {

    /**
     * Check internet connection.
     *
     * @param parent the parent
     * @return true, if successful
     */
    public static boolean isInternetAvailable(Context parent)
    {
        ConnectivityManager conMgr = (ConnectivityManager) parent
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

}
