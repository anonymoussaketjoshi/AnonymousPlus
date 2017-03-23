package com.saket.attendanceplus;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.InsertDimensionRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class googleapipage extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,ConnectivityReceiver.ConnectivityReceiverListener {
    GoogleAccountCredential mCredential;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    String spreadsheetId;
    private static final String BUTTON_TEXT = "Call Google Sheets API";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };

    private TextView mOutputText;
    private Button mCallApiButton;
    private Button processImagesButton;
    ProgressDialog mProgress;
    SharedPreferences settings;
    File session_file;
    Context context;

    //Kairos stuff
    String [] images;
    int imgno = 0;
    Kairos myKairos;
    Bitmap img;
    String gallery = "myGallery";
    //Google stuff
    String keyColumn;
    String insertColumn;

    //List of matched persons
    List<String> matchedPersons = new ArrayList<String>();

    akashDBhelper dBhelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googleapipage);
        mOutputText = (TextView) findViewById(R.id.mOutputText);
        mCallApiButton = (Button) findViewById(R.id.mCallApiButton) ;
        processImagesButton = (Button)findViewById(R.id.process_image_button);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        context = this;
        dBhelper = ((myCustomApplication)getApplication()).dBhelper;
        keyColumn = settings.getString("KEY_COLUMN","A");
        insertColumn = settings.getString("INSERT_COLUMN","B");
        //spreadsheetId = "1u6f5CySr98DB7mdIGDopmRIErGFmmrzbofUniASErCo";                             //

        spreadsheetId = dBhelper.getLink(settings.getString("COURSE_NAME",""));
        /////////////////////////////KAIROS
        Toast.makeText(this,settings.getString("SESSION_ID",""),Toast.LENGTH_SHORT).show();
        session_file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath()+"/"+
                settings.getString("COURSE_NAME","")+"/"+
                settings.getString("SESSION_ID",""));
        if(!session_file.exists())
            Toast.makeText(this,"SESSION FILE DOESN'T EXIST",Toast.LENGTH_LONG).show();
        else
            images = session_file.list();

        //Kairos authentication
        myKairos = new Kairos();
        String kairos_id = settings.getString("KAIROS_ID","");
        String kairos_key = settings.getString("KAIROS_KEY","");
        if(kairos_id.equals("") || kairos_key.equals(""))
            Toast.makeText(this,"KAIROS ID OR KEY NOT PASSED Contact Admin",Toast.LENGTH_LONG).show();
        else
            myKairos.setAuthentication(this,kairos_id,kairos_key);


        //onClick listener for process images
        processImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isConnected = checkConnection();
                if(isConnected){
                    showSnack(isConnected);
                    processImage();
                }
                else
                    showSnack(isConnected);
            }
        });
        ////////
        mCallApiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isConnected = checkConnection();
                if(isConnected){
                    showSnack(isConnected);
                mCallApiButton.setEnabled(false);
                mOutputText.setText("");
                getResultsFromApi();
                mCallApiButton.setEnabled(true);}
                else
                    showSnack(isConnected);
            }
        });
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Sheets API ...");
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }
    //----------------------------end of on create------------------

    KairosListener list_listener = new KairosListener() {
        @Override
        public void onSuccess(String response) {
            mOutputText.setText(response);
        }
        @Override
        public void onFail(String response) {
            Log.d("KAIROS DEMO", response);
        }
    };

    KairosListener listener = new KairosListener() {
        @Override
        public void onSuccess(String s) {
            /*if(response!=null && response.substring(0,8).equals("{\"Errors"))
                mOutputText.setText(mOutputText.getText().toString()+"\n"+"No face in image");
            else {
                File img = new File(session_file,images[imgno]);
                if(true)//img.delete())
                    mOutputText.setText(mOutputText.getText().toString() + "\n" + "Face Found => Image Deleted");
                else
                    mOutputText.setText(mOutputText.getText().toString() + "\n" + "Face Found => Image couldn't be Deleted");
            }
            mOutputText.setText(response);
            //updateTime((imgno+1)*100/images.length);
            imgno=imgno+1;
            if(imgno<images.length)
                processImage(mOutputText);
        */
            String output_response = "";
            try {
                JSONObject object = new JSONObject(s);
                if(object.has("Errors")) {
                    JSONArray errors = object.getJSONArray("Errors");
                    JSONObject object0 = errors.getJSONObject(0);
                    output_response += "\n Error: "+object0.getString("Message")+"\n";
                    matchedPersons.add("No face found");
                }

                else if(object.has("images"))   {
                    JSONArray images = object.getJSONArray("images");
                    JSONObject object0 = images.getJSONObject(0);
                    JSONObject transaction = object0.getJSONObject("transaction");
                    String status = transaction.getString("status");

                    if(object0.has("candidates"))   {
                        JSONArray candidates = object0.getJSONArray("candidates");
                        output_response = "\nStatus = " + status + "\nMatched with: \n";
                        matchedPersons.add(candidates.getJSONObject(0).getString("subject_id").toLowerCase());
                        for (int i = 0; i < candidates.length(); ++i) {
                            output_response += candidates.getJSONObject(i).getString("subject_id") + "\n";
                        }
                    }

                    else    {
                        String message = transaction.getString("message");
                        output_response += "\nStatus = " + status+"\nMessage = "+message+"\n";
                        matchedPersons.add("No match");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mOutputText.setText("Matching Status = "+matchedPersons.get(matchedPersons.size()-1));
            ImageView imageView = (ImageView) findViewById(R.id.image_view_process_images);
            imageView.setImageBitmap(img);
            imgno=imgno+1;
            if(imgno<images.length) {
                processImage();
            }
            else {
                imgno = 0;
                //new MakeRequestTask(mCredential).execute();
                imageView.setImageDrawable(null);
                mOutputText.setText("Done with matching!\nYou can Update Excel Sheet now!");
            }
        }
        @Override
        public void onFail(String response) {
            //Toast.makeText(null,"FAILED RESPONSE",Toast.LENGTH_SHORT).show();
            // your code here!
            Log.d("KAIROS DEMO", response);
        }
    };

    //////////////////////KAIROS
    public void processImage(){
        if(images!=null && imgno<images.length) {
            img = BitmapFactory.decodeFile(session_file.getPath() + "/" + images[imgno]);

        }
        else
        Toast.makeText(this,"process image invalid index",Toast.LENGTH_SHORT).show();
        //filesview.setText(filesview.getText().toString() + "\n"+"Processing image: "+images[imgno]);
        //imageView.setImageBitmap(img);
        if(img==null)
            Toast.makeText(this,"no image",Toast.LENGTH_SHORT).show();
        else
            sendPOST();
    }
    public void sendPOST(){
        try {
            myKairos.recognize(img,gallery, null, null, null, null, listener);
            //myKairos.detect(img, null, null, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public void click_listGallery(View view) throws UnsupportedEncodingException, JSONException {
        myKairos.listSubjectsForGallery(gallery, list_listener);
        return;
    }

    /////////////Method to sync matched photos and sheet student id's
    public String[] markAttendance(List<String> namesList)   {
        String[] attendanceList = new String[namesList.size()];
        attendanceList[0] = settings.getString("SESSION_ID","--");
        for(int i=1;i<namesList.size();++i) {
            if(matchedPersons.contains(namesList.get(i).toLowerCase()))
                attendanceList[i] = "Present";
            else
                attendanceList[i] = "Absent";
        }
        return attendanceList;
    }

    /////////////////////GOOGLE SDK OVERRIDDED FUNCTIONS

    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null){
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                googleapipage.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Sheets API Android Quickstart")
                    .build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                List<String> idListFromSheet = getColumnFromApi(keyColumn);
                insertColToApi(insertColumn,markAttendance(idListFromSheet));
                return idListFromSheet;
                /*List<String> fakeList = getColumnFromApi('A');//getDataFromApi();
                String [] newcolarray = new String[3];
                newcolarray[0]="saket";
                newcolarray[1]="akash";
                newcolarray[2]="akash";
                insertColToApi('C',newcolarray);
                //fakeList.add(Integer.toString(getColCountFromApi()));
                return fakeList;*/
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }
        private int getColCountFromApi() throws IOException {
            String range = "1:1";
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            List<List<Object>> values = response.getValues();
            if(values.size()==1)
                return values.get(0).size();
            return 0;
        }
        private void insertColToApi(String columnIndex,String[] data) throws IOException {
            DimensionRange newColumnDimensions = new DimensionRange();
            newColumnDimensions
                    .setSheetId(0)
                    .setDimension("COLUMNS")
                    .setStartIndex(columnStringToIndex(columnIndex))
                    .setEndIndex(columnStringToIndex(columnIndex) + 1);
            InsertDimensionRequest myRequest = new InsertDimensionRequest()
                    .setRange(newColumnDimensions)
                    .setInheritFromBefore(false);
            List<Request> requests = new ArrayList<>();
            requests.add(new Request()
                            .setInsertDimension(myRequest));
            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(spreadsheetId,body).execute();
            /////////////////////////////////
            String range = columnIndex+":"+columnIndex;
            List<List<Object>> newColumnData = new ArrayList<>();
            for(int i=0;i<data.length;++i){
                List<Object> newlist = new ArrayList<>();
                newlist.add(data[i]);
                newColumnData.add(newlist);
            }
            ValueRange oRange = new ValueRange();
            oRange.setRange(range);
            oRange.setValues(newColumnData);
            List<ValueRange> oList = new ArrayList<>();
            oList.add(oRange);
            BatchUpdateValuesRequest oRequest = new BatchUpdateValuesRequest();
            oRequest.setValueInputOption("RAW");
            oRequest.setData(oList);
            BatchUpdateValuesResponse oResp1 =
                    this.mService.spreadsheets().values().batchUpdate(spreadsheetId,oRequest).execute();

        }
        private List<String> getDataFromApi() throws IOException {
            String range = "A1:E";
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values != null) {
                results.add("Name, Major");
                for (List row : values) {
                    //results.add(Integer.toString(row.size()));
                    //SAKET NOTE: REMEMBER TO CHECK SIZE
                    results.add(row.get(0) + ", " + row.get(1));
                }
            }
            return results;
        }
        private List<String> getColumnFromApi(String columnIndex) throws IOException {
            String range = (columnIndex + ":" + columnIndex).toUpperCase();
            List<String> results = new ArrayList<String>();
            ValueRange response = this.mService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            List<List<Object>> values = response.getValues();
            if (values != null) {
                for (List row : values) {
                    results.add(row.get(0).toString());
                }
            }
            return results;
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                //output.add(0, "Data retrieved using the Google Sheets API:");
                mOutputText.setText("Course Spreadsheet updated.");
                //mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            googleapipage.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
    //////////////////////////////OWN DECLARED LITTLE FUNCTIONS
    public int columnStringToIndex(String columnString){
        return columnString.charAt(0)-'A';                                                          //CORRECT
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

        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_googleapipage),message,Snackbar.LENGTH_LONG);
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
