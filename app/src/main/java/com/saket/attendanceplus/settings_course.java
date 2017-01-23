package com.saket.attendanceplus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class settings_course extends AppCompatActivity {
    akashDBhelper dBhelper;
    String textViewText = "You can only add or delete a course.\n\n";
    List<Course> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_course);
        dBhelper = ((myCustomApplication)getApplication()).dBhelper;
        TextView textView = (TextView)findViewById(R.id.course_details);
        courseList = dBhelper.getCourses();
        for(int i=0;i<courseList.size();i++) {
            textViewText += courseList.get(i).getCourseName()+"\n";
        }
        textView.setText(textViewText);
    }


    public void add_course(View view)  {
        EditText editText1 = (EditText) findViewById(R.id.course_name);
        String courseName = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.course_link);
        String courseLink = editText2.getText().toString();
        if(!courseName.equals("") && !courseLink.equals("")) {
            dBhelper.addCourse(new Course(courseName,courseLink));
            editText1.setText("");
            editText2.setText("");
        }
        else
            Toast.makeText(this,"Enter valid Name and Link!",Toast.LENGTH_LONG).show();
    }

    public void delete_course(View view)   {
        EditText editText1 = (EditText) findViewById(R.id.course_name);
        String courseName = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.course_link);
        String courseLink = editText2.getText().toString();
        Boolean courseExists = false;
        if(!courseName.equals("") && !courseLink.equals("")) {
            for(int i = 0;i<courseList.size();++i)  {
                Course course = courseList.get(i);
                if(course.getCourseName().equals(courseName) && course.getCourseLink().equals(courseLink))
                    courseExists = true;
            }
            if(courseExists) {
                dBhelper.deleteCourse(new Course(courseName, courseLink));
                editText1.setText("");
                editText2.setText("");
            }
            else
                Toast.makeText(this,"Course details invalid, Course doesn't exist!",Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this,"Enter valid Name and Link!",Toast.LENGTH_LONG).show();


    }
}
