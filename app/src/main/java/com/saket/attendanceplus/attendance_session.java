package com.saket.attendanceplus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class attendance_session extends AppCompatActivity {
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    public boolean imageSet = false;
    private ImageView mImageView ;
    Bitmap tempImage = null;
    View focusView = null;
    Bitmap curImage = null;
    SharedPreferences settings;
    File session_folder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_session);

        android.support.v7.app.ActionBar myAction = getSupportActionBar();
        myAction.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#028900")));
        getSupportActionBar().setTitle(" Attendance Session");                                      //SET BETTER TITLE

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mImageView = (ImageView) findViewById(R.id.imageViewid);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        Toast.makeText(this,settings.getString("SESSION_ID","--"),Toast.LENGTH_SHORT).show();
        focusView = mEmailView;
        focusView.requestFocus();
        if(!createFolder())
            Toast.makeText(this,"UNABLE TO INITIALIZE SESSION DIRECTORY",Toast.LENGTH_SHORT).show();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){                                                  //CORRECT
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                           //DIRECT CORRECTLY
        Intent newPage = new Intent(this,main_login.class);
        startActivity(newPage);
        return true;
    }
    @Override
    public void onBackPressed(){
        finish();
        //Toast.makeText(this,"Back? I don't think so",Toast.LENGTH_LONG).show();
    }

    public void takePhoto(View view) throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent,1);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            tempImage = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(tempImage);
            imageSet = true;
            curImage = tempImage;
        }
        if(requestCode == 3) {                                                                      //ON SIGNOUT
            /*appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (appPreferences.getBoolean("session_status", true) == false){
                finish();
                return;
            }*/
        }
    }

    public Boolean createFolder()   {
        String course_name = settings.getString("COURSE_NAME","--").replaceAll(" ","_");
        String session_id = settings.getString("SESSION_ID","--");
        File coursef = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+course_name);
        if(coursef.exists()) {
            session_folder = new File(coursef.getAbsolutePath() + "/" + session_id);
            session_folder.mkdir();
        }
        else
            return false;
        return true;
    }
    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
        }
        else if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
        } else if (false){                                                                          //CHECK ID??
            mPasswordView.setError("Incorrect ID or Password");
            mEmailView.setError("Incorrect ID or Password");
            focusView = mImageView;
        } else if(!imageSet){
            Toast.makeText(this,"Take image",Toast.LENGTH_SHORT).show();
            focusView = mImageView;
        }
        else{
            mImageView.setImageResource(getResources()
                    .getIdentifier("@drawable/camera",null,getPackageName()));
            mEmailView.setText("");
            mPasswordView.setText("");
            focusView = mEmailView;
            if(submit(email))
                Toast.makeText(this,"Saved Sucessfully ID: "+email,Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,"SAVING FAILED. Contact Admin",Toast.LENGTH_SHORT).show();
        }
        focusView.requestFocus();
    }
    private boolean submit(String id){
        if(imageSet==false) return false;                                                         //SUBMIT ATTENDANCE
        try{
            File imageFile = new File(session_folder,id+".png");
            FileOutputStream fileStream = new FileOutputStream(imageFile);
            curImage.compress(Bitmap.CompressFormat.PNG,100,fileStream);
            fileStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
        //return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
        //  return password.length() >=3;
    }
}