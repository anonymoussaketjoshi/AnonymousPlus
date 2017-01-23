package com.saket.attendanceplus;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by @K@sh on 1/23/2017.
 */

public class myCustomApplication extends Application{
    akashDBhelper dBhelper;
    @Override
    public void onCreate()  {
        super.onCreate();
        dBhelper = new akashDBhelper(getApplicationContext());
        dBhelper.addProfessor(new Professor("tsn","tsn100","tsn"));
        dBhelper.addProfessor(new Professor("prp","prp100","prp"));
    }
}
