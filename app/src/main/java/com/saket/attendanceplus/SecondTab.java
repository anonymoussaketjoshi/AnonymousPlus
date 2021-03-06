package com.saket.attendanceplus;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by @K@sh on 1/29/2017.
 */

public class SecondTab extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    Kairos myKairos = new Kairos();

    KairosListener listener = new KairosListener() {
        @Override
        public void onSuccess(String s) {
            Log.d("KAIROS DEMO", s);
            TextView textView = (TextView) getView().findViewById(R.id.response_textView_tab2);
            String output_response = "\n";
            try {
                JSONObject object = new JSONObject(s);
                if(object.has("Errors")) {
                    JSONArray errors = object.getJSONArray("Errors");
                    JSONObject object0 = errors.getJSONObject(0);
                    output_response += "\n Error: "+object0.getString("Message")+"\n";
                }

                else if(object.has("status"))   {
                    String status = object.getString("status");
                    String message = object.getString("message");
                    output_response += "Status = "+status+"\nMessage : "+message+"\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            textView.setText(output_response);

        }
        @Override
        public void onFail(String s) {
            Log.d("KAIROS DEMO", s);
            TextView textView = (TextView) getView().findViewById(R.id.response_textView_tab2);
            textView.setText(s);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View v = inflater.inflate(R.layout.second_tab, container, false);
        String app_id = "e190939d";
        String api_key = "b6a70d7257457ea99cbcfa949f393477";
        myKairos.setAuthentication(getActivity(), app_id, api_key);

        Button delete_button = (Button) v.findViewById(R.id.KairosDelete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = checkConnection();
                if(isConnected) {
                    showSnack(isConnected);
                    EditText editText1 = (EditText) v.findViewById(R.id.deleteEditText);
                    String subjectId = editText1.getText().toString();
                    if (!subjectId.equals(""))
                        try {
                            myKairos.deleteSubject(subjectId, "myGallery", listener);
                            editText1.setText("");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    else
                        Toast.makeText(getActivity(), "Enter valid Subject ID", Toast.LENGTH_LONG).show();
                }
                else
                    showSnack(isConnected);
            }
        });

        return v;
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
