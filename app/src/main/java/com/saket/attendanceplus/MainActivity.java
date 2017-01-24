package com.saket.attendanceplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    void click_start(View view){
        Intent loginpage = new Intent(this,main_login.class);
        startActivity(loginpage);
    }
    void click_google_api(View view){
        startActivity(new Intent(this,googleapipage.class));
    }
    void click_settings(View view){
        startActivity(new Intent(this,settings.class));
    }
}
