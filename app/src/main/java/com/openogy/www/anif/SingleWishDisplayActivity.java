package com.openogy.www.anif;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openogy.classes.AdapterWishComments;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.classes.WishComments;
import com.openogy.classes.Wishes;

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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static android.content.ContentValues.TAG;

public class SingleWishDisplayActivity extends AppCompatActivity {

    Button btnAddComment;
    EditText editTextAddComment;
    int AdId;
    String comment;
    ListView commentListView;
    String returnValue;
    TextView tvSellerName, tvSellerLocality, tvDescription, tvCall, tvReport ;
    public int responseCode;
    AdapterWishComments adbWishComments;
    View header;
    ImageView wishImageView;
    private int WishId;
    private String userPhoneNumber;
    private int getResponseCode;
    ImageButton ImageButtonCall, ImageButtonReport;
    private Dialog dialogForReport;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_comments_listview);

        Intent intent = getIntent();
        WishId = intent.getIntExtra("WishId",0);
        userPhoneNumber = intent.getStringExtra("PhoneNumber");
        token = intent.getStringExtra("Token");


        commentListView = findViewById(R.id.AdOrCommentListView);

        header = getLayoutInflater().inflate(R.layout.activity_single_wish_display, commentListView, false);


        tvSellerName = header.findViewById(R.id.tvSellerName);
        tvSellerLocality = header.findViewById(R.id.tvSellerLocality);
        tvDescription = header.findViewById(R.id.tvDescription);
        tvCall = header.findViewById(R.id.tvCall);
        tvReport = header.findViewById(R.id.tvReport);
        wishImageView = header.findViewById(R.id.IvWishImage);
        ImageButtonCall = header.findViewById(R.id.ImageButtonCall);
        ImageButtonReport = header.findViewById(R.id.ImageButtonReport);

        Globals g = Globals.getInstance();
        if(g.getData().equals(token))
        {
            ImageButtonReport.setVisibility(View.GONE);
            ImageButtonCall.setVisibility(View.GONE);
            tvCall.setVisibility(View.GONE);
            tvReport.setVisibility(View.GONE);
        }
        else
        {
            ImageButtonReport.setVisibility(View.VISIBLE);
            ImageButtonCall.setVisibility(View.VISIBLE);
            tvCall.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.VISIBLE);
        }

        ImageButtonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Use format with "tel:" and phone number to create phoneNumber.
                String phoneNumber = String.format("tel: %s",
                        userPhoneNumber);
                // Create the intent.
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                // Set the data for the intent as the phone number.
                dialIntent.setData(Uri.parse(phoneNumber));
                // If package resolves to an app, send intent.
                if (dialIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(dialIntent);
                } else {
                    Log.e(TAG, "Can't resolve app for ACTION_DIAL Intent.");
                }
            }
        });

        ImageButtonReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForReport = new Dialog(SingleWishDisplayActivity.this);
                dialogForReport.setContentView(R.layout.report_popup);
                //dialog.setCancelable(false);
                final EditText editText = dialogForReport.findViewById(R.id.editTextReason);
                Button btnSubmit = dialogForReport.findViewById(R.id.btnSubmit);
                final TextView txtResult = dialogForReport.findViewById(R.id.txtResult);
                txtResult.setText("");
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString().trim().isEmpty())
                            txtResult.setText(R.string.error_report);
                        else {
                            new SubmitReportTask(editText.getText().toString().trim()).execute();

                        }
                    }
                });
                Button btnCancel = dialogForReport.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogForReport.dismiss();
                    }
                });

                dialogForReport.show();
            }
        });


        new HttpAsyncTaskGetWishDetails().execute();
        new HttpAsyncTaskGetComment().execute();

        btnAddComment = header.findViewById(R.id.btnAddComment);

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextAddComment = findViewById(R.id.EditTextAddComment);
                comment = editTextAddComment.getText().toString().trim();
                if(comment.isEmpty())
                    Toast.makeText(SingleWishDisplayActivity.this, "Please type something in comment", Toast.LENGTH_SHORT).show();
                else new HttpAsyncTaskAddComment().execute(comment);
            }
        });
    }

    public class HttpAsyncTaskAddComment extends AsyncTask<String,Void,String> {

        private Dialog dialog = new Dialog(SingleWishDisplayActivity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleWishDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "Comment/PostCommentForWish";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("WishId",WishId);
                jsonObject.accumulate("WishComment",strings[0]);
                Globals g = Globals.getInstance();
                jsonObject.accumulate("Token",g.getData());

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



                getResponseCode =  conn.getResponseCode();

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

            if (getResponseCode == 200)
            {
                if (!s.isEmpty())
                {
                    String postedComment =  "[" +s +"]";
                    ArrayList<WishComments> arrayList = null;
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<WishComments>>(){}.getType();
                    Collection<WishComments> adsCollection = gson.fromJson(postedComment, collectionType);

                    WishComments[] adsArray = adsCollection.toArray(new WishComments[adsCollection.size()]);

                    arrayList = new ArrayList<WishComments>(Arrays.asList(adsArray));

                    adbWishComments.addListItemToAdapter(arrayList);
                    editTextAddComment.setText("");

                }
                Toast.makeText(SingleWishDisplayActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(SingleWishDisplayActivity.this, s, Toast.LENGTH_SHORT).show();

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

        private Dialog dialog = new Dialog(SingleWishDisplayActivity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleWishDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "Comment/GetCommentsForWish";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("WishId",WishId);

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
                    ArrayList<WishComments> arrayList = null;

                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<WishComments>>() {
                    }.getType();
                    Collection<WishComments> adsCollection = gson.fromJson(s, collectionType);
                    WishComments[] adsArray = adsCollection.toArray(new WishComments[adsCollection.size()]);

                    arrayList = new ArrayList<WishComments>(Arrays.asList(adsArray));

                    //then populate myListItems
                    adbWishComments = new AdapterWishComments(SingleWishDisplayActivity.this, 0, arrayList, SingleWishDisplayActivity.this);

                    if(adbWishComments !=null)
                    {
                        commentListView.setDivider(null);
                        commentListView.setAdapter(adbWishComments);
                    }
                }

            }
            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }
        }
    }

    public class HttpAsyncTaskGetWishDetails extends AsyncTask<Void,Void,String>{

        private Dialog dialog = new Dialog(SingleWishDisplayActivity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleWishDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "WishList/GetSingleWishDetails";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("WishId",WishId);

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

                String output = stringBuilder.toString();
                returnValue = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1);
                return conn.getResponseMessage();
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

            if(s.equalsIgnoreCase("ok"))
            {
                Gson gson = new Gson();
                Type wishType = new TypeToken<Wishes>() {
                }.getType();
                Wishes wishesCollection = gson.fromJson(returnValue, wishType);

                if(wishesCollection!=null)
                {
                    tvSellerName.setText(wishesCollection.UserName.trim());
                    tvSellerLocality.setText(wishesCollection.WishLocality.trim());
                    tvDescription.setText(wishesCollection.WishDescription.trim());
                    if(wishesCollection.WishImage!=null)
                    {
                        byte[] bytesForWishImage = android.util.Base64.decode(wishesCollection.WishImage, 0);
                        Bitmap bitmapForWishImage = BitmapFactory.decodeByteArray(bytesForWishImage, 0, bytesForWishImage.length);
                        wishImageView.setImageBitmap(bitmapForWishImage);
                    }

                    commentListView.addHeaderView(header);

                }
            }

            if(dialog.isShowing())
            {
                dialog.dismiss();
            }
        }
    }

    public class SubmitReportTask extends AsyncTask<Integer, Void, String> {

        private final String reportReason;
        private int position;

        private Dialog dialog = new Dialog(SingleWishDisplayActivity.this, android.R.style.Theme_Black);

        private SubmitReportTask(String reportReason) {
            this.reportReason = reportReason;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleWishDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            try {
                String urlAPI = CommonFunctions.API_URL + "WishList/SubmitReportForWish";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("ReportReason", reportReason);
                jsonObject.accumulate("WishId", WishId);
                Globals g = Globals.getInstance();
                String token = g.getData();
                jsonObject.accumulate("Token", token);

                //Post json content
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                Log.i("Report Reason", jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                return conn.getResponseMessage();

            } catch (ProtocolException e) {
                return e.toString();
            } catch (IOException e) {
                return e.toString();
            } catch (JSONException e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {

            if (s.equalsIgnoreCase("ok"))
                Toast.makeText(SingleWishDisplayActivity.this, "Report submitted", Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if(dialogForReport.isShowing())
            {
                dialogForReport.dismiss();
            }
        }
    }
    }

