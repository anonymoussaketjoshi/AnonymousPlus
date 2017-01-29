package com.saket.attendanceplus;

import android.app.Application;
import android.widget.Toast;

/**
 * Created by @K@sh on 1/23/2017.
 */

public class myCustomApplication extends Application{
    public akashDBhelper dBhelper;
    private static myCustomApplication mInstance;

    @Override
    public void onCreate()  {
        super.onCreate();
        dBhelper = new akashDBhelper(this);
        mInstance = this;
        //dBhelper.addProfessor(new Professor("tsn","tsn100","tsn"));
        //dBhelper.addProfessor(new Professor("prp","prp100","prp"));

       /* professorList = dBhelper.getProfessors();
        if(professorList.size() == 0)
            Toast.makeText(getApplicationContext(),"Did not get any list",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(),professorList.get(0).getName()+" "+professorList.get(0).getPassword(),Toast.LENGTH_LONG).show();
        */
    }

    public static synchronized myCustomApplication getInstance()    {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
