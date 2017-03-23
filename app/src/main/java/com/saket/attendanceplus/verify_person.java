package com.saket.attendanceplus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

public class verify_person extends AppCompatActivity {

    Button button;
    ImageView imageView;
    static final int CAM_REQUEST = 1;
    File image_file;
    Bitmap tempImg;
    Kairos myKairos = new Kairos();
    String selector = "FULL";
    String multipleFaces = "false";
    String minHeadScale = "0.25";
    String threshold = "0.75";
    String maxNumResult = "25";
    KairosListener listener = new KairosListener() {
        @Override
        public void onSuccess(String s) {
            Log.d("KAIROS DEMO", s);
            TextView textView = (TextView) findViewById(R.id.response_textView);
            String output_response = "";
            //when matched with multiple candidates
            try {
                JSONObject object = new JSONObject(s);
                if(object.has("Errors")) {
                    JSONArray errors = object.getJSONArray("Errors");
                    JSONObject object0 = errors.getJSONObject(0);
                    output_response += "\n Error: "+object0.getString("Message")+"\n";
                }

                else if(object.has("images"))   {
                    JSONArray images = object.getJSONArray("images");
                    JSONObject object0 = images.getJSONObject(0);
                    JSONObject transaction = object0.getJSONObject("transaction");
                    String status = transaction.getString("status");

                    if(object0.has("candidates"))   {
                        JSONArray candidates = object0.getJSONArray("candidates");
                        output_response = "\nStatus = " + status + "\nMatched with: \n";
                        for (int i = 0; i < candidates.length(); ++i) {
                            output_response += candidates.getJSONObject(i).getString("subject_id") + "\n";
                        }
                    }

                    else    {
                        String message = transaction.getString("message");
                        output_response += "\nStatus = " + status+"\nMessage = "+message+"\n";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            textView.setText(output_response);

        }

        @Override
        public void onFail(String s) {
            Log.d("KAIROS DEMO", s);
            TextView textView = (TextView) findViewById(R.id.response_textView);
            textView.setText(s);
        }
    };
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_person);

        button = (Button) findViewById(R.id.image_Button);
        imageView = (ImageView) findViewById(R.id.image_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //File file = getFile();
                //camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);
            }
        });


        //authentication
        String app_id = "e190939d";
        String api_key = "b6a70d7257457ea99cbcfa949f393477";
        myKairos.setAuthentication(this, app_id, api_key);


    }


    private void saveFile(String fileName, Bitmap Image) {
        File folder = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, fileName + ".png");
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            Boolean success = Image.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(getApplicationContext(), success.toString(), Toast.LENGTH_LONG).show();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        tempImg = (Bitmap) extras.get("data");
        imageView.setImageBitmap(tempImg);
        if (tempImg == null)
            Toast.makeText(getApplicationContext(), "Check Taken Image", Toast.LENGTH_LONG).show();
    }


    public void getEnrolled(View view) {
        EditText editText1 = (EditText) findViewById(R.id.enrollNameEditText);
        String subjectId = editText1.getText().toString();
        if (!subjectId.equals(""))
            try {
                myKairos.enroll(tempImg, subjectId, "myGallery", null, null, null, listener);
                saveFile(subjectId, tempImg);
                imageView.setImageDrawable(null);
                tempImg = null;
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        else if (tempImg == null)
            Toast.makeText(getApplicationContext(), "Check Taken Image", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "Enter valid Enroll name", Toast.LENGTH_LONG).show();

    }

    public void doRecognition(View view) {
        if (tempImg != null)
            try {
                myKairos.recognize(tempImg, "myGallery", null, threshold, null, null, listener);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }
        else
            Toast.makeText(getApplicationContext(), "Check Taken Image", Toast.LENGTH_LONG).show();
    }
}
