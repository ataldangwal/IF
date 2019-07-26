package com.openogy.www.anif;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openogy.classes.Ads;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.ProfileAdsAdapter;
import com.openogy.classes.Users;

import org.json.JSONArray;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class User_Profile_Activity extends AppCompatActivity {

    ImageView imageview;
    Button btnAds;
    String token;
    TextView tvBadge, tvSold, tvBids, tvName, tvCity,tvRating, tvUserFeedBack, tvUserFeedBackValue;
    String returnValue;
    ListView AdListView;
    private int offset = 0;
    private int responseCode;
    private int counter = 0;
    public View footer;
    public Handler mHandler;
    public boolean isLoading = false;
    public ProfileAdsAdapter adbAds;
    private GetAdsForCityAsyncTask adData;
    private String currentUserToken, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile_);

        imageview = findViewById(R.id.ProfileImageView);
        btnAds = findViewById(R.id.btnAd);
        tvBadge = findViewById(R.id.tvBadge);
        tvRating = findViewById(R.id.tvUserRating);
        tvUserFeedBack = findViewById(R.id.tvUserFeedBack);
        tvUserFeedBackValue = findViewById(R.id.tvUserFeedBackValue);
        tvSold = findViewById(R.id.tvSold);
        tvBids = findViewById(R.id.tvBids);
        tvName = findViewById(R.id.tvName);
        tvCity = findViewById(R.id.tvCity);
        AdListView = findViewById(R.id.AdListView);
        footer = getLayoutInflater().inflate(R.layout.listview_footer,AdListView,false);

        Intent intent = getIntent();
        token = intent.getStringExtra("Token");

        String url = CommonFunctions.API_URL + "User/UserDetailsForProfile";
        new HTTPAsyncTaskGetProfileData().execute(url);

        String urlForGettingAds = CommonFunctions.API_URL + "Ad/GetAdsForUser";
        adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(urlForGettingAds);

        mHandler = new MyHandler();

        String userFeedbackString = getText(R.string.txt_userfeedback).toString();

        SpannableString spannableString  = new SpannableString(userFeedbackString);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(User_Profile_Activity.this,UserFeedback_Activity.class);

                intent.putExtra("currentUserToken", currentUserToken);
                intent.putExtra("currentUserName", currentUserName);

                startActivity(intent);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(Color.parseColor("#5BC0DE"));
            }
        };

        spannableString.setSpan(clickableSpan,0,spannableString.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUserFeedBack.setText(spannableString);
        tvUserFeedBack.setMovementMethod(LinkMovementMethod.getInstance());
        tvUserFeedBack.setHighlightColor(Color.TRANSPARENT);
        /*tvUserFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_Profile_Activity.this,UserFeedback_Activity.class);

                intent.putExtra("currentUserToken", currentUserToken);
                intent.putExtra("currentUserName", currentUserName);

                startActivity(intent);
            }
        });*/

        AdListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Ads adControl = (Ads)adapterView.getItemAtPosition(i);
            }
        });


        AdListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView,  int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(absListView.getLastVisiblePosition() == totalItemCount - 1 && AdListView.getCount() >= 10 && isLoading == false)
                {
                    isLoading = true;
                    offset = offset + 10;

                    String url = CommonFunctions.API_URL + "Ad/GetAdsForUser";
                    adData = (GetAdsForCityAsyncTask) new GetAdsForCityAsyncTask().execute(url);
                }
            }
        });
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    AdListView.addFooterView(footer,null,false);
                    break;
                case 1:
                    //Update data adapter and UI
                    adbAds.addListItemToAdapter((ArrayList<Ads>)msg.obj);
                    //Renove Footer after update
                    AdListView.removeFooterView(footer);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            super.run();
            // mHandler.sendEmptyMessage(0);

            String adsList = null;
            try {

                adsList = adData.get();

                if(adsList != null)
                {
                    ArrayList<Ads> arrayList = null;
                    Gson gson = new Gson();
                    Type collectionType = new TypeToken<Collection<Ads>>() {
                    }.getType();
                    Collection<Ads> adsCollection = gson.fromJson(adsList, collectionType);
                    Ads[] adsArray = adsCollection.toArray(new Ads[adsCollection.size()]);

                    arrayList = new ArrayList<Ads>(Arrays.asList(adsArray));

                    for(int i = 0; i < arrayList.size(); i ++)
                    {
                        byte[] bytes = android.util.Base64.decode(arrayList.get(i).AdImageOne, 0);

                        Ads a =  arrayList.get(i);
                        a.AdImageBitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }

                    Thread.sleep(2000);

                    Message msg = mHandler.obtainMessage(1, arrayList);
                    mHandler.sendMessage(msg);
                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    private void LoadingData() throws InterruptedException, ExecutionException {
        String adsList = adData.get();
        ArrayList<Ads> arrayList = null;
        if(adsList == null)
            Toast.makeText(User_Profile_Activity.this,"No Ads",Toast.LENGTH_SHORT).show();
        else {

            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Ads>>() {
            }.getType();
            Collection<Ads> adsCollection = gson.fromJson(adsList, collectionType);
            Ads[] adsArray = adsCollection.toArray(new Ads[adsCollection.size()]);

            arrayList = new ArrayList<Ads>(Arrays.asList(adsArray));

            for(int i = 0; i < arrayList.size(); i ++)
            {
                byte[] bytes = android.util.Base64.decode(arrayList.get(i).AdImageOne, 0);

                Ads a =  arrayList.get(i);
                a.AdImageBitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

            //then populate myListItems
            adbAds= new ProfileAdsAdapter(User_Profile_Activity.this, 0, arrayList, User_Profile_Activity.this);
            if(adbAds == null)
            {
                Toast.makeText(User_Profile_Activity.this,"No Ads",Toast.LENGTH_SHORT).show();
            }
            else
            {
                //AdListView.setDivider(null);
                AdListView.setAdapter(adbAds);
            }

        }
    }

    private class HTTPAsyncTaskGetProfileData extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(User_Profile_Activity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(User_Profile_Activity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "Error!";
                }
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        private String HttpPost(String myUrl) throws IOException, JSONException {
            String result = "";

            URL url = new URL(myUrl);

            // 1. create HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            // 2. build JSON object
            JSONObject jsonObject = buidJsonObject();

            // 3. add JSON content to POST request body
            setPostRequestContent(conn, jsonObject);

            // 4. make POST request to the given URL
            conn.connect();

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk = null;


            while((bufferedStrChunk = bufferedReader.readLine()) != null){
                stringBuilder.append(bufferedStrChunk);
            }

            String output = stringBuilder.toString();
            returnValue = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1);
            // 5. return response message
            return conn.getResponseMessage();

        }

        private JSONObject buidJsonObject() throws JSONException {

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("Token",token);
            return jsonObject;
        }

        private void setPostRequestContent(HttpURLConnection conn,
                                           JSONObject jsonObject) throws IOException {

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            Log.i(Registration_Activity.class.toString(), jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
        }

        protected void onPostExecute(String result) {
            if(result.equalsIgnoreCase("ok"))
            {
                Users userlist = null;
                try {
                    JSONObject json = null;
                    JSONObject data = null;
                    json = new JSONObject(returnValue);
                    data = json.getJSONObject("CurrentUser");
                    Iterator x = data.keys();
                    JSONArray jsonArray = new JSONArray();
                    while (x.hasNext()) {
                        String key = (String) x.next();

                        jsonArray.put(data.get(key));

                    }

                    //JSONArray array = data.toJSONArray();
                    userlist = Users.GetUserFromJson(data);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                if(userlist!=null)
                {
                    currentUserName = userlist.FullName;
                    currentUserToken = userlist.login_token;
                    if (userlist.UserBadge.equals("0"))
                    {
                        tvBadge.setText("NONE");
                        tvBadge.setTextColor(Color.parseColor("#FFFFFF"));
                    }

                    if (userlist.UserBadge.equals("1"))
                    {
                        tvBadge.setText("BRONZE");
                        tvBadge.setTextColor(Color.parseColor("#6C541E"));
                    }

                    if (userlist.UserBadge.equals("2"))
                    {
                        tvBadge.setText("SILVER");
                        tvBadge.setTextColor(Color.parseColor("#808080"));
                    }

                    if (userlist.UserBadge.equals("3"))
                    {
                        tvBadge.setText("GOLD");
                        tvBadge.setTextColor(Color.parseColor("#DAA520"));
                    }

                    if (userlist.UserBadge.equals("4"))
                    {
                        tvBadge.setText("ELITE");
                        tvBadge.setTextColor(Color.parseColor("#2E75F9"));
                    }


                    tvSold.setText(userlist.NumberOfSales);
                    tvBids.setText(userlist.NumberOfBids);
                    tvName.setText(userlist.FullName);
                    tvCity.setText(userlist.City.toUpperCase());
                    tvRating.setText(getText(R.string.txt_Rating) + " : " +      userlist.Rating.toUpperCase());
                    tvUserFeedBackValue.setText(userlist.FeedbackRating + getText(R.string.txt_out_of_five));

                    if(!userlist.UserProfileImage.equalsIgnoreCase("null"))
                    {
                        byte[] bytes = android.util.Base64.decode(userlist.UserProfileImage, 0);
                        userlist.UserImageBitmap  = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    }
                    else
                    userlist.UserImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.default_user1);

                    imageview.setImageBitmap(userlist.UserImageBitmap);
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }


        }
    }

    public class GetAdsForCityAsyncTask extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(User_Profile_Activity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(counter == 0)
            {
                View view = LayoutInflater.from(User_Profile_Activity.this).inflate(R.layout.layout_progressbar, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(view);
                dialog.show();
            }
            else
                AdListView.addFooterView(footer,null,false);


        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPost(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }

        private  String HttpPost(String myUrl) throws IOException, JSONException {

            URL url = new URL(myUrl);

            // 1. create HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            // 2. build JSON object
            JSONObject jsonObject = buidJsonObject();

            // 3. add JSON content to POST request body
            setPostRequestContent(conn, jsonObject);

            // 4. make POST request to the given URL
            conn.connect();

            InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String bufferedStrChunk = null;

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(line);
            }

            responseCode = conn.getResponseCode();
            conn.disconnect();
            bufferedReader.close();
            inputStreamReader.close();


            // 5. return response message
            return stringBuilder.toString();

        }

        private  JSONObject buidJsonObject() throws JSONException {

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("Token",token);
            jsonObject.accumulate("Offset",offset);

            return jsonObject;
        }

        private  void setPostRequestContent(HttpURLConnection conn,
                                            JSONObject jsonObject) throws IOException {

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());
            Log.i(Registration_Activity.class.toString(), jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(responseCode == 200)
            {
                if(counter == 0)
                {
                    try {
                        LoadingData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    counter++;
                    // If task execution completed

                }
                else if(s != null)
                {
                    Thread thread = new ThreadGetMoreData();
                    thread.start();
                    //listview.removeFooterView(footer);
                }
                else if(s == null)
                    AdListView.removeFooterView(footer);
            }

            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

        }


    }




}
