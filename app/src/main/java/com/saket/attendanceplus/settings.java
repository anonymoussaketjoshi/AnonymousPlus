package com.saket.attendanceplus;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class settings extends AppCompatActivity {
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
        setTitle("SETTINGS");
        //setContentView(R.layout.activity_settings);
    }
    @Override
    public void onBackPressed(){
        if(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean("awesomeness_check",true))
            finish();
        else
            Toast.makeText(this,"Confirm that this app is awesome to go back :D",Toast.LENGTH_LONG).show();
    }
}
