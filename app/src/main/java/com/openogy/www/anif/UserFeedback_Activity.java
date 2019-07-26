package com.openogy.www.anif;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openogy.classes.AdapterUserFeedback;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.classes.UserFeedback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class UserFeedback_Activity extends AppCompatActivity {

    View header;
    ListView UserFeedBackListView;
    EditText EditTextAddFeedback;
    Button btnAddFeedback;
    Spinner SpinnerRating;
    int responseCodeForPostComment;
    String postedComment;
    AdapterUserFeedback adbAdComments;
    String currentUserToken, currentUserName;
    int responseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_feedback);

        Intent intent = getIntent();
        currentUserToken = intent.getStringExtra("currentUserToken");
        currentUserName = intent.getStringExtra("currentUserName");

        UserFeedBackListView = findViewById(R.id.UserFeedBackListView);
        UserFeedBackListView.setAdapter(null);

        header = getLayoutInflater().inflate(R.layout.layout_user_feedback_header, UserFeedBackListView, false);
        EditTextAddFeedback = header.findViewById(R.id.EditTextAddFeedback);
        btnAddFeedback = header.findViewById(R.id.btnAddFeedback);
        SpinnerRating = header.findViewById(R.id.SpinnerRating);

        ArrayList<Integer> spinnerList = new ArrayList<Integer>();
        spinnerList.add(1);
        spinnerList.add(2);
        spinnerList.add(3);
        spinnerList.add(4);
        spinnerList.add(5);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,spinnerList );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerRating.setAdapter(spinnerAdapter);
        SpinnerRating.setDropDownVerticalOffset(75);
        UserFeedBackListView.addHeaderView(header);

        new HttpAsyncTaskGetComment().execute();

        btnAddFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = EditTextAddFeedback.getText().toString().trim();
                if(comment.isEmpty())
                    Toast.makeText(UserFeedback_Activity.this, "Please type something for feedback", Toast.LENGTH_SHORT).show();
               else
                    new HttpAsyncTaskAddFeedback().execute(comment, SpinnerRating.getSelectedItem().toString());
            }
        });


    }

    public class HttpAsyncTaskAddFeedback extends AsyncTask<String,Void,String> {

        private Dialog dialog = new Dialog(UserFeedback_Activity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(UserFeedback_Activity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "User/PostFeedbackForUser";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("CurrentUserToken",currentUserToken);
                jsonObject.accumulate("CurrentUserName",currentUserName);
                jsonObject.accumulate("UserFeedback",strings[0]);
                jsonObject.accumulate("UserFeedbackRating",strings[1]);
                Globals g = Globals.getInstance();
                jsonObject.accumulate("ReviewerUserToken",g.getData());

                //Writing data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                //Getting data
                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                responseCodeForPostComment = conn.getResponseCode();

                return stringBuilder.toString();
                /*String output = stringBuilder.toString();
                postedComment = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1);*/

                // return conn.getResponseMessage();
            } catch (MalformedURLException e) {
                return e.toString();
            } catch (IOException e) {
                return e.toString();
            } catch (JSONException e) {
                return e.toString();
            }

        }

        @Override
        protected void onPostExecute(String s) {

            if (responseCodeForPostComment == 200)
            {
                if (!s.isEmpty())
                {
                    postedComment =  "[" +s +"]";
                    ArrayList<UserFeedback> arrayList = null;
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<UserFeedback>>(){}.getType();
                    Collection<UserFeedback> adsCollection = gson.fromJson(postedComment, collectionType);

                    UserFeedback[] adsArray = adsCollection.toArray(new UserFeedback[adsCollection.size()]);

                    arrayList = new ArrayList<UserFeedback>(Arrays.asList(adsArray));

                    adbAdComments.addListItemToAdapter(arrayList);
                    EditTextAddFeedback.setText("");
                    SpinnerRating.setSelection(0);



                }
                Toast.makeText(UserFeedback_Activity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(UserFeedback_Activity.this, s, Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if(dialog.isShowing())
            {
                dialog.dismiss();
            }
        }
    }

    public class HttpAsyncTaskGetComment extends AsyncTask<String,Void,String>{

        private Dialog dialog = new Dialog(UserFeedback_Activity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(UserFeedback_Activity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "User/GetFeedbackForUsers";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Token",currentUserToken);

                //Writing data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                //Getting data
                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                responseCode = conn.getResponseCode();
                conn.disconnect();
                bufferedReader.close();
                inputStreamReader.close();
                return stringBuilder.toString();

            } catch (MalformedURLException e) {
                return e.toString();
            } catch (IOException e) {
                return e.toString();
            } catch (JSONException e) {
                return e.toString();
            }

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            if(responseCode == 200)
            {
                {
                    ArrayList<UserFeedback> arrayList = null;

                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<UserFeedback>>() {
                    }.getType();
                    Collection<UserFeedback> adsCollection = gson.fromJson(s, collectionType);
                    UserFeedback[] adsArray = adsCollection.toArray(new UserFeedback[adsCollection.size()]);

                    arrayList = new ArrayList<UserFeedback>(Arrays.asList(adsArray));

                    //then populate myListItems
                    adbAdComments= new AdapterUserFeedback(UserFeedback_Activity.this, 0, arrayList, UserFeedback_Activity.this);

                    if(adbAdComments!=null)
                    {
                        //UserFeedBackListView.setDivider(null);
                        UserFeedBackListView.setAdapter(adbAdComments);
                    }
                }

            }
            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }
        }
    }
}
