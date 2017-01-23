package com.saket.attendanceplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class select_course extends AppCompatActivity {
    ListView courseList;
    TextView greetPerson;
    customListAdapter myAdapter;
    SharedPreferences settings;
    akashDBhelper dBhelper;
    List<Course> courses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);
        android.support.v7.app.ActionBar myAction = getSupportActionBar();
        myAction.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#028900")));
        setTitle("Select Course");
        courseList = (ListView) findViewById(R.id.courseListid);
        greetPerson = (TextView) findViewById(R.id.welcome_user_text);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        greetPerson.setText("Welcome " + settings.getString("PROF_NAME",""));
        dBhelper = ((myCustomApplication)getApplication()).dBhelper;
        populateList();
        //initiateList(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS));
    }
    public void course_click(View view){
        Intent nextPage;
        Bundle myextras = getIntent().getExtras();
        if(myextras.containsKey("PURPOSE") && myextras.getString("PURPOSE").equals("START_SESSION")) {
            nextPage = new Intent(this, attendance_session.class);
            startActivity(nextPage);
        }
    }
    private void initiateList(File location){
        File [] myfiles = location.listFiles();
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

    private void populateList(){
        courses = dBhelper.getCourses();
        String [] courses_names = new String[courses.size()];
        for(int i=0;i<courses.size();++i)   {
            courses_names[i] = courses.get(i).getCourseName();
        }
        Toast.makeText(this, Integer.toString(courses.size()),Toast.LENGTH_LONG).show();
        customListAdapter myAdapter = new customListAdapter(this,R.layout.list_button_view,courses_names);
        courseList.setAdapter(myAdapter);
    }
}
