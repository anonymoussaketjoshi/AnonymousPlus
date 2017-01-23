package com.saket.attendanceplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 1/23/2017.
 */

public class akashDBhelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

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

    public akashDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFS_TABLE = "CREATE TABLE " + TABLE_PROFS + "(" +
                KEY_NAME + " TEXT," + KEY_ID + " TEXT," + KEY_PASSWORD +
                " TEXT)";
        String CREATE_COURSES_TABLE = "CREATE TABLE " + TABLE_COURSES + "(" +
                KEY_COURSENAME + " TEXT," + KEY_COURSELINK + " TEXT)";
        db.execSQL(CREATE_PROFS_TABLE);
        db.execSQL(CREATE_COURSES_TABLE);
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
   * 7) get prof list */

    //adding a new professor
    public void addProfessor(Professor p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, p.getId());
        values.put(KEY_NAME, p.getName());
        values.put(KEY_PASSWORD, p.getPassword());

        db.insert(TABLE_PROFS, null, values);
        db.close();
    }

    //add checks to see whether given prof existed or not
    public void deleteProfessor(Professor p) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROFS, KEY_ID + " = ?", new String[]{String.valueOf(p.getId())});
        db.close();
    }

    //password change done via delete prof->add again as new prof
    //This saves us from calling onUpgrade method which re-creates full table
    public void changePassword(Professor p) {
        deleteProfessor(p);
        addProfessor(p);
    }

    public void addCourse(Course c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_COURSENAME, c.getCourseName());
        values.put(KEY_COURSELINK, c.getCourseLink());

        db.insert(TABLE_COURSES, null, values);
        db.close();
    }

    public void deleteCourse(Course c) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_COURSES, KEY_COURSENAME + " = ?", new String[]{String.valueOf(c.getCourseName())});
        db.close();
    }

    public List<Professor> getProfessors() {
        List<Professor> professorList = new ArrayList<Professor>();

        String selectQuery = "SELECT * FROM " + TABLE_PROFS;
        SQLiteDatabase db = this.getWritableDatabase();
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
        SQLiteDatabase db = this.getWritableDatabase();
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


}
