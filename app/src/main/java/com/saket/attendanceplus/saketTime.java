package com.saket.attendanceplus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

/**
 * Created by HP on 24-01-2017.
 */

public class saketTime {
    public String getTimeString(){
        String minutes = Integer.toString(Calendar.getInstance().get(Calendar.MINUTE)),
                hour    = Integer.toString(Calendar.getInstance().get(Calendar.HOUR)),
                date    = Integer.toString(Calendar.getInstance().get(Calendar.DATE)),
                month   = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)),
                year    = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        return date + "-" + month + "-" + year + ":" + minutes + "-" + hour;
    }
    public String getSessionId(String prof_name,String course_name){
        return prof_name.replaceAll(" ","_")+":"+course_name.replaceAll(" ","_")+":"+getTimeString();
    }
}
