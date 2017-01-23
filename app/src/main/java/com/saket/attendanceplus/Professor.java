package com.saket.attendanceplus;

/**
 * Created by USER on 1/23/2017.
 */

public class Professor {
    private String id;
    private String name;
    private String password;

    public Professor()  {}

    public Professor (String id,String name,String password)    {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId()   {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    // Need not be defined
    public String getPassword() {
        return this.password;
    }

    public void setId(String id)    {
        this.id = id;
    }

    public void setName(String name)    {
        this.name = name;
    }

    public void setPassword(String password)    {
        this.password = password;
    }
}



