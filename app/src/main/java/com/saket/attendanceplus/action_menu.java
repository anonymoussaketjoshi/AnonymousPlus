package com.saket.attendanceplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class action_menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_menu);
    }

    public void click_start_session(View view){
        Intent nextPage = new Intent(this,select_course.class);
        nextPage.putExtra("PURPOSE","START_SESSION");
        startActivity(nextPage);
        finish();
    }

    public void click_process(View view){
        Intent nextPage = new Intent(this,select_course.class);
        nextPage.putExtra("PURPOSE","PROCESS");
        startActivity(nextPage);
        finish();
    }

}
