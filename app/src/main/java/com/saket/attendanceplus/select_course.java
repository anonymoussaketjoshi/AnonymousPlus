package com.saket.attendanceplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class select_course extends AppCompatActivity {
    ListView courseList;
    TextView greetPerson;
    SharedPreferences settings;
    String username = "";
    customListAdapter myAdapter;
    String purpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);
        //purpose = getIntent().getExtras().getString("purpose","NONE");
        android.support.v7.app.ActionBar myAction = getSupportActionBar();
        myAction.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#028900")));
        courseList = (ListView) findViewById(R.id.courseListid);
        greetPerson = (TextView) findViewById(R.id.welcome_user_text);

        setTitle("Select Course");
        greetPerson.setText("Welcome " + username);

        initiateList(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS));

    }
    public void course_click(View view){
        Intent nextPage;
        nextPage = new Intent(this,attendance_session.class);
        startActivity(nextPage);
        /*if(purpose=="PROCESS")
            nextPage = new Intent(this,processImages.class);
        else if(purpose=="NONE")
            finish();
        String courseName = (String) view.getTag();
        nextPage.putExtra("course_name",courseName.substring(0,courseName.length()-3));
        startActivity(nextPage);)*/
    }
    private void initiateList(File location){
        File [] myfiles = location.listFiles();
        /*
        /*List<String> course_list = new ArrayList();
        for(int i=0;i<myfiles.length;++i)
            if(myfiles[i].isDirectory())
                course_list.add(myfiles[i].getName());*/

        int valid_dir = 0;
        for(int i=0;i<myfiles.length;++i)
        if(myfiles[i].isDirectory())
            valid_dir++;
        if(valid_dir==0){
            Toast.makeText(this,"NO COURSES FOUND IN DIRECTORIES"+ Integer.toString(valid_dir),Toast.LENGTH_SHORT).show();
            return;
        }
        String [] files = new String[valid_dir];
        for(int i=0,ptr=0;i<myfiles.length;++i)
            if(myfiles[i].isDirectory())
                files[ptr++] = myfiles[i].getName();
        customListAdapter myAdapter = new customListAdapter(this,R.layout.list_button_view,files);
        courseList.setAdapter(myAdapter);
    }
}
