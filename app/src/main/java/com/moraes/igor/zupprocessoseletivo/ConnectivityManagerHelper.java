package com.moraes.igor.zupprocessoseletivo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class ConnectivityManagerHelper {
    private static ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private static NetworkInfo getActivieNetworkInfo(Context context) {
        ConnectivityManager manager = getConnectivityManager(context);
        if (manager == null)
            return null;

        return manager.getActiveNetworkInfo();
    }

    /*-
     * Only use this method to retrieve error reason
     */
    static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo networkInfo = getActivieNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
