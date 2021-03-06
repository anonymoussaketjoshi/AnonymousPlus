package com.saket.attendanceplus;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class settings_course extends AppCompatActivity {
    akashDBhelper dBhelper;
    String textViewText = "Courses enrolled: \n";
    List<Course> courseList;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_course);
        dBhelper = ((myCustomApplication)getApplication()).dBhelper;
        textView = (TextView)findViewById(R.id.course_details);
        courseList = dBhelper.getCourses();
        for(int i=0;i<courseList.size();i++) {
            textViewText += courseList.get(i).getCourseName()+"\n";
        }
        textView.setText(textViewText);
    }

    void deleteRecursive(File file) {
        if(file.isDirectory())
            for(File child : file.listFiles())
                deleteRecursive(child);
        file.delete();
    }

    //Creates a folder when clicked on add with given name
    public void add_course(View view)  {
        EditText editText1 = (EditText) findViewById(R.id.course_name);
        String courseName = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.course_link);
        String courseLink = editText2.getText().toString();
        if(!courseName.equals("") && !courseLink.equals("")) {
            dBhelper.addCourse(new Course(courseName,courseLink));
            editText1.setText("");
            editText2.setText("");
            //File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),courseName);
            File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+courseName);
            if(f!=null && !f.exists()) {
                f.mkdir();
            }
            else {
                Toast.makeText(this, "Folder already existed", Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(this,"Enter valid Name and Link!",Toast.LENGTH_LONG).show();
        courseList = dBhelper.getCourses();
        textViewText = "Courses enrolled: \n";
        for(int i=0;i<courseList.size();i++) {
            textViewText += courseList.get(i).getCourseName()+"\n";
        }
        textView.setText(textViewText);
    }

    // No code added to delete the folder
    public void delete_course(View view)   {
        EditText editText1 = (EditText) findViewById(R.id.course_name);
        String courseName = editText1.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.course_link);
        String courseLink = editText2.getText().toString();
        Boolean courseExists = false;
        if(!courseName.equals("")) {
            for(int i = 0;i<courseList.size();++i)  {
                Course course = courseList.get(i);
                if(course.getCourseName().toLowerCase().equals(courseName.toLowerCase()))
                    courseExists = true;
            }
            if(courseExists) {
                dBhelper.deleteCourse(new Course(courseName, courseLink));
                editText1.setText("");
                editText2.setText("");
                File courseFolder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+courseName);
                deleteRecursive(courseFolder);

            }
            else
                Toast.makeText(this,"Course details invalid, Course doesn't exist!",Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this,"Enter valid Name and Link!",Toast.LENGTH_LONG).show();

        courseList = dBhelper.getCourses();
        textViewText = "Courses enrolled: \n";
        for(int i=0;i<courseList.size();i++) {
            textViewText += courseList.get(i).getCourseName()+"\n";
        }
        textView.setText(textViewText);
    }
}
