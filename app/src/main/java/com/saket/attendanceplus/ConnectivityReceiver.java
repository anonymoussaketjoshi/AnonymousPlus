package com.saket.attendanceplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by @K@sh on 1/29/2017.
 */

public class ConnectivityReceiver extends BroadcastReceiver {

    public static  ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver()   {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1)    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(connectivityReceiverListener!=null)  {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) myCustomApplication.getInstance()
                .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}
