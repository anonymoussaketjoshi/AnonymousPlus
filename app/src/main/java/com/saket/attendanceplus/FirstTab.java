package com.saket.attendanceplus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.UnsupportedEncodingException;

/**
 * Created by @K@sh on 1/29/2017.
 */

public class FirstTab extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener{
    Button button;
    ImageView imageView;
    static final int CAM_REQUEST = 1;
    Bitmap tempImg;
    Kairos myKairos = new Kairos();

    KairosListener listener = new KairosListener() {
        @Override
        public void onSuccess(String s) {
            Log.d("KAIROS DEMO", s);
            TextView textView = (TextView) getView().findViewById(R.id.response_textView);
            String output_response = "\n";
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
                    String subjectId = transaction.getString("subject_id");
                    output_response += "Status = "+status+"\nSubjectId : "+subjectId+" Enrolled!\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            textView.setText(output_response);

        }
        @Override
        public void onFail(String s) {
            Log.d("KAIROS DEMO", s);
            TextView textView = (TextView) getView().findViewById(R.id.response_textView);
            textView.setText(s);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View v = inflater.inflate(R.layout.first_tab, container, false);

            //authentication
            String app_id = "e190939d";
            String api_key = "b6a70d7257457ea99cbcfa949f393477";
            myKairos.setAuthentication(getActivity(), app_id, api_key);

            button = (Button) v.findViewById(R.id.image_Button);
            imageView = (ImageView) v.findViewById(R.id.image_view);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, CAM_REQUEST);
                }
            });

            Button enroll_button = (Button) v.findViewById(R.id.Enroll_button);
            enroll_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isConnected = checkConnection();
                    if(isConnected)   {
                        showSnack(isConnected);
                    EditText editText1 = (EditText) v.findViewById(R.id.enrollNameEditText);
                    String subjectId = editText1.getText().toString();
                    if (!subjectId.equals(""))
                        try {
                            myKairos.enroll(tempImg, subjectId, "myGallery", null, null, null, listener);
                            imageView.setImageDrawable(null);
                            tempImg = null;
                            editText1.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    else if (tempImg == null)
                        Toast.makeText(getActivity(), "Check Taken Image", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getActivity(), "Enter valid Enroll name", Toast.LENGTH_LONG).show();

                }
                    else
                        showSnack(isConnected);
                }
            });
            return v;
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        tempImg = (Bitmap) extras.get("data");
        imageView.setImageBitmap(tempImg);
        if (tempImg == null)
            Toast.makeText(getActivity(), "Check Taken Image", Toast.LENGTH_LONG).show();
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if(isConnected) {
            message = "Connected to Internet!";
            color = Color.WHITE;
        }
        else {
            message = "No internet connection!";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar.make(getView(),message,Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    private boolean checkConnection() {
       return ConnectivityReceiver.isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        // register connection status listener
        myCustomApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

}
