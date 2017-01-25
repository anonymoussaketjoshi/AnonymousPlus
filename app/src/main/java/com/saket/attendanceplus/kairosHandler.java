package com.saket.attendanceplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by @K@sh on 1/24/2017.
 */

public class kairosHandler {
    public String response;
    Kairos kairos;
    private String app_id = "e190939d";
    private String api_key = "b6a70d7257457ea99cbcfa949f393477";
    String gallery = "MyGallery";
    Context context;
    public void kairosHandler(){
        kairos = new Kairos();
        this.context = context;
        //authenticate(context);
    }

    KairosListener listener = new KairosListener() {
        @Override
        public void onSuccess(String s) {
            response = s;
        }

        @Override
        public void onFail(String s) {
            response = s;
            Toast.makeText(context,"KAIROS FAILED: "+response,Toast.LENGTH_SHORT).show();
        }
    };


    /* Methods defined
    * 1) authenticate
    * 2) enroll
    * 3) recognize
    * 4) getSubjects
    * 5) delete a subject*/

    public void authenticate (Context context) {
        kairos.setAuthentication(context,app_id,api_key);
    }

    public void enroll(Bitmap tempImg,String subjectId,String GalleryName)  {
        try {
            kairos.enroll(tempImg, subjectId, GalleryName, null, null, null, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
  }

    public void recognize(Bitmap tempImg,String GalleryName) {
        try {
            kairos.recognize(tempImg, GalleryName, null, null, null, null, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void getSubjects(String galleryName)    {
        try {
            kairos.listSubjectsForGallery(galleryName, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void deleteSubject(String galleryName, String subjectId) {
        try {
            kairos.deleteSubject(subjectId, galleryName, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
