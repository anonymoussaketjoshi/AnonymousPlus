package com.saket.attendanceplus;

/**
 * Created by USER on 1/23/2017.
 */

public class Course {
    private String courseName;
    private String courseLink;

    public Course()   {}

    public Course(String courseName,String courseLink)  {
        this.courseLink = courseLink;
        this.courseName = courseName;
    }

    public void setCourseName(String courseName)    {
        this.courseName = courseName;
    }

    public void setCourseLink(String courseLink)    {
        this.courseLink = courseLink;
    }

    public String getCourseName()   {
        return this.courseName;
    }

    public String getCourseLink()   {
        return this.courseLink;
    }
}
