package com.saket.attendanceplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 1/23/2017.
 */

public class akashDBhelper extends SQLiteOpenHelper {
    Context context;
    private static final int DATABASE_VERSION = 1;
    List<Professor> professorList;
    //Database Name
    private static final String DATABASE_NAME = "credentials";

    //Table Names
    private static final String TABLE_PROFS = "professors";
    private static final String TABLE_COURSES = "courses";

    //Table colmun names for TABLE_PROFS
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PASSWORD = "password";

    //Table column names for TABLE_COURSES
    private static final String KEY_COURSENAME = "courseName";
    private static final String KEY_COURSELINK = "courseLink";
    SQLiteDatabase db;

    public akashDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = getWritableDatabase();
        professorList = new ArrayList<Professor>();

        //addProfessor(new Professor("@","Akash","fgh"));
        //addCourse(new Course("LS","http..ls//"));
        //addCourse(new Course("BEE","http..bee//"));
        /*if(checkTable(TABLE_PROFS))
            Toast.makeText(context,"exists",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,"not exists",Toast.LENGTH_LONG).show();
        */
    }

    @Override
    public void onCreate(SQLiteDatabase dbo) {
            String CREATE_PROFS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFS + " (" +
                    KEY_NAME + " TEXT," + KEY_ID + " TEXT," + KEY_PASSWORD +
                    " TEXT)";
            String CREATE_COURSES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES + " ( " +
                    KEY_COURSENAME + " TEXT, " + KEY_COURSELINK + " TEXT)";
            dbo.execSQL(CREATE_PROFS_TABLE);
            dbo.execSQL(CREATE_COURSES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        onCreate(db);
    }

  /* Methods defined:
   * 1) add prof
   * 2) del prof
   * 3) change password
   * 4) add course
   * 5) del course
   * 6) get course list
   * 7) get prof list
   * 8) check table
   * 9) get Link on courseName*/

    //adding a new professor
    public void addProfessor(Professor p) {

        ContentValues values = new ContentValues();
        values.put(KEY_ID, p.getId());
        values.put(KEY_NAME, p.getName());
        values.put(KEY_PASSWORD, p.getPassword());

        db.insert(TABLE_PROFS, null, values);
    }

    //add checks to see whether given prof existed or not
    public void deleteProfessor(Professor p) {
        db.delete(TABLE_PROFS, KEY_ID + " = ?", new String[]{String.valueOf(p.getId())});
    }

    //password change done via delete prof->add again as new prof
    //This saves us from calling onUpgrade method which re-creates full table
    public void changePassword(Professor p) {
        deleteProfessor(p);
        addProfessor(p);
    }

    public Boolean checkTable(String table_name){
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master", null);

        if(cursor!=null) {
            Toast.makeText(context,Integer.toString(cursor.getCount()),Toast.LENGTH_SHORT).show();
            if (cursor.getCount() > 0) {

                cursor.moveToFirst();
                String var = cursor.getString(0);
                cursor.moveToNext();
                var += cursor.getString(0);
                Toast.makeText(context,var,Toast.LENGTH_SHORT).show();
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    public void addCourse(Course c) {
        ContentValues values = new ContentValues();
        values.put(KEY_COURSENAME, c.getCourseName());
        values.put(KEY_COURSELINK, c.getCourseLink());

        db.insert(TABLE_COURSES, null, values);
    }

    public void deleteCourse(Course c) {
        db.delete(TABLE_COURSES, KEY_COURSENAME + " = ?", new String[]{String.valueOf(c.getCourseName())});
    }

    public List<Professor> getProfessors() {
        List<Professor> professorList = new ArrayList<Professor>();

        String selectQuery = "SELECT * FROM " + TABLE_PROFS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                   Professor professor = new Professor();
                    professor.setName(cursor.getString(0));
                    professor.setId(cursor.getString(1));
                    professor.setPassword(cursor.getString(2));

                    professorList.add(professor);

            }while (cursor.moveToNext()) ;
        }
        return professorList;
    }

    public List<Course> getCourses() {
        List<Course> courseList = new ArrayList<Course>();

        String selectQuery = "SELECT * FROM " + TABLE_COURSES;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                    Course course = new Course();
                    course.setCourseName(cursor.getString(0));
                    course.setCourseLink(cursor.getString(1));

                    courseList.add(course);

            }while (cursor.moveToNext()) ;
        }
        return courseList;

    }



    public String getLink(String courseName)    {
        List<Course> courseList = getCourses();
        Course course;
        for(int i=0;i<courseList.size();i++)    {
            course = courseList.get(i);
            if(course.getCourseName().equals(courseName))
                return course.getCourseLink();
        }
        return null;
    }


    public Professor verifyProfessor(String id, String password){
        professorList = this.getProfessors();
        Professor prof;
        for(int i=0;i<professorList.size();++i){
            prof = professorList.get(i);
            if(prof.getId().toLowerCase().equals(id.toLowerCase()) && prof.getPassword().equals(password))
                return prof;
        }
        return null;
    }


}
