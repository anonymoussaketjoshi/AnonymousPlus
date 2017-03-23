package com.saket.attendanceplus;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
    }
    void click_start(View view){
        Intent tab_view_activity = new Intent(this,tabViewActivity.class);
        startActivity(tab_view_activity);
    }

    void click_verify(View view)    {
        Intent verify_activity = new Intent(this,verify_person.class);
        startActivity(verify_activity);
    }
}
