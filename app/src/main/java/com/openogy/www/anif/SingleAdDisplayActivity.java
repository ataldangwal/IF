package com.openogy.www.anif;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openogy.classes.AdComments;
import com.openogy.classes.AdWithBids;
import com.openogy.classes.AdapterAdComments;
import com.openogy.classes.AdapterViewPager;
import com.openogy.classes.Ads;
import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.viewpagerindicator.CirclePageIndicator;

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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class SingleAdDisplayActivity extends AppCompatActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<Bitmap> ImagesArray = new ArrayList<Bitmap>();
    Button btnAddComment;
    EditText editTextAddComment;
    int AdId;
    String comment;
    ListView commentListView;
    String returnValue;
    TextView tvAdTitle, tvSellerName, tvSellerLocality, tvSellingPrice, tvHighestBid, tvSecondHighestBid, tvThirdHighestBid, tvDescription, tvNoOfBids, tvCall,tvBid,tvReport,tvDate ;
    CirclePageIndicator indicator;
    public int responseCode;
    AdapterAdComments adbAdComments;
    View header;
    String postedComment;
    int responseCodeForPostComment;
    HttpAsyncTaskAddComment httpAsyncTaskAddComment;
    ImageButton ImageButtonCall, ImageButtonBid, ImageButtonReport;
    String VendorPhoneNumber;
    private Dialog dialogForMakingBid;
    private Dialog dialogForReport;
    private ArrayList<Ads> Ads;
    private int position;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ad_comments_listview);

        Globals g = Globals.getInstance();
        g.setFlagForNewBid(false);

        Intent intent = getIntent();
        AdId = intent.getIntExtra("AdId",0);
        VendorPhoneNumber = intent.getStringExtra("PhoneNumber");
        position = intent.getIntExtra("Position", 0);
        token = intent.getStringExtra("Token");



        commentListView = findViewById(R.id.AdOrCommentListView);

        header = getLayoutInflater().inflate(R.layout.activity_single_ad_display, commentListView, false);
        tvAdTitle = header.findViewById(R.id.tvAdTitle);
        tvSellerName = header.findViewById(R.id.tvSellerName);
        tvSellerLocality = header.findViewById(R.id.tvSellerLocality);
        tvSellingPrice = header.findViewById(R.id.tvSellingPrice);
        tvHighestBid = header.findViewById(R.id.tvHighestBid);
        tvNoOfBids = header.findViewById(R.id.tvNoOfBids);
        tvSecondHighestBid = header.findViewById(R.id.tvSecondHighestBid);
        tvDescription = header.findViewById(R.id.tvDescription);
        tvThirdHighestBid = header.findViewById(R.id.tvThirdHighestBid);
        ImageButtonCall = header.findViewById(R.id.ImageButtonCall);
        ImageButtonBid = header.findViewById(R.id.ImageButtonBid);
        ImageButtonReport = header.findViewById(R.id.ImageButtonReport);
        mPager = header.findViewById(R.id.pager);
        indicator = header.findViewById(R.id.indicator);
        indicator.setVisibility(View.GONE);
        tvBid = header.findViewById(R.id.tvBid);
        tvCall = header.findViewById(R.id.tvCall);
        tvReport = header.findViewById(R.id.tvReport);
        tvDate = header.findViewById(R.id.tvDate);

        if(token.equals(g.getData()))
        {
            ImageButtonReport.setVisibility(View.GONE);
            ImageButtonBid.setVisibility(View.GONE);
            ImageButtonCall.setVisibility(View.GONE);
            tvBid.setVisibility(View.GONE);
            tvCall.setVisibility(View.GONE);
            tvReport.setVisibility(View.GONE);
        }

        else
        {
            ImageButtonReport.setVisibility(View.VISIBLE);
            ImageButtonBid.setVisibility(View.VISIBLE);
            ImageButtonCall.setVisibility(View.VISIBLE);
            tvBid.setVisibility(View.VISIBLE);
            tvCall.setVisibility(View.VISIBLE);
            tvReport.setVisibility(View.VISIBLE);
        }

       ImageButtonCall.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               // Use format with "tel:" and phone number to create phoneNumber.
               String phoneNumber = String.format("tel: %s",
                       VendorPhoneNumber);
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

       ImageButtonBid.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               dialogForMakingBid = new Dialog(SingleAdDisplayActivity.this);
               dialogForMakingBid.setContentView(R.layout.bid_popup);
               // dialog.setCancelable(false);
               final EditText editText = dialogForMakingBid.findViewById(R.id.editTextBidAmount);
               Button btnSubmit = dialogForMakingBid.findViewById(R.id.btnSubmit);
               final TextView txtResult = dialogForMakingBid.findViewById(R.id.txtResult);
               final CheckBox displayPhoneNumbercheckbox = dialogForMakingBid.findViewById(R.id.displayPhoneNumbercheckbox);
               txtResult.setText("");
               btnSubmit.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       if (editText.getText().toString().trim().isEmpty())
                           txtResult.setText(R.string.error_bid);
                       else {
                           new MakeBidTask(editText.getText().toString().trim(),(displayPhoneNumbercheckbox.isChecked())).execute();

                       }
                   }
               });
               Button btnCancel = dialogForMakingBid.findViewById(R.id.btnCancel);
               btnCancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       dialogForMakingBid.dismiss();
                   }
               });

               dialogForMakingBid.show();

           }
       });

       ImageButtonReport.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               dialogForReport = new Dialog(SingleAdDisplayActivity.this);
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
                           new SubmitReportTask(editText.getText().toString().trim()).execute(position);

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



        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });


        new HttpAsyncTaskGetAdDetailsWithBids().execute();
        new HttpAsyncTaskGetComment().execute();

        btnAddComment = header.findViewById(R.id.btnAddComment);

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextAddComment = findViewById(R.id.EditTextAddComment);
                comment = editTextAddComment.getText().toString().trim();
                if(comment.isEmpty())
                    Toast.makeText(SingleAdDisplayActivity.this, "Please type something in comment", Toast.LENGTH_SHORT).show();
                else
                    httpAsyncTaskAddComment = (HttpAsyncTaskAddComment)new HttpAsyncTaskAddComment().execute(comment);
            }
        });
    }

    public class HttpAsyncTaskAddComment extends AsyncTask<String,Void,String>{

        private Dialog dialog = new Dialog(SingleAdDisplayActivity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleAdDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "Comment/PostCommentForAds";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AdId",AdId);
                jsonObject.accumulate("AdComment",strings[0]);
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
                        ArrayList<AdComments> arrayList = null;
                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<Collection<AdComments>>(){}.getType();
                        Collection<AdComments> adsCollection = gson.fromJson(postedComment, collectionType);

                        AdComments[] adsArray = adsCollection.toArray(new AdComments[adsCollection.size()]);

                        arrayList = new ArrayList<AdComments>(Arrays.asList(adsArray));

                        adbAdComments.addListItemToAdapter(arrayList);
                        editTextAddComment.setText("");



                }
                Toast.makeText(SingleAdDisplayActivity.this, "Comment Posted", Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(SingleAdDisplayActivity.this, s, Toast.LENGTH_SHORT).show();

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

        private Dialog dialog = new Dialog(SingleAdDisplayActivity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleAdDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "Comment/GetCommentsForAds";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AdId",AdId);

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
                    ArrayList<AdComments> arrayList = null;

                        Gson gson = new Gson();
                        Type collectionType = new TypeToken<Collection<AdComments>>() {
                        }.getType();
                        Collection<AdComments> adsCollection = gson.fromJson(s, collectionType);
                        AdComments[] adsArray = adsCollection.toArray(new AdComments[adsCollection.size()]);

                        arrayList = new ArrayList<AdComments>(Arrays.asList(adsArray));

                        //then populate myListItems
                    adbAdComments= new AdapterAdComments(SingleAdDisplayActivity.this, 0, arrayList, SingleAdDisplayActivity.this);

                        if(adbAdComments!=null)
                        {
                            //commentListView.setDivider(null);
                            commentListView.setAdapter(adbAdComments);
                        }
                }

            }
            if(dialog.isShowing()){
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }
        }
    }

    public class HttpAsyncTaskGetAdDetailsWithBids extends AsyncTask<Void,Void,String>{

        private Dialog dialog = new Dialog(SingleAdDisplayActivity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleAdDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... strings) {
            try {

                String urlAPI = CommonFunctions.API_URL + "ad/GetSingleAdDetails";
                URL url =  new URL(urlAPI);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Creating Json Object

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("AdId",AdId);

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
                Ads currentAd = null;
                Gson gson = new Gson();
                Type adwithBidsType = new TypeToken<AdWithBids>() {
                }.getType();
                AdWithBids adsCollection = gson.fromJson(returnValue, adwithBidsType);

                if(adsCollection!=null)
                {
                    tvAdTitle.setText(adsCollection.AdTitle.trim());
                    tvSellerName.setText(adsCollection.SellerName.trim());
                    tvSellerLocality.setText(adsCollection.SellerLocality.trim());
                    String sellingPrice = getFormatedAmount(Integer.parseInt(adsCollection.SellingPrice));
                    if(sellingPrice.equals("0"))
                        tvSellingPrice.setText(getText(R.string.single_ad_no_selling_price_txt));
                    else
                        tvSellingPrice.setText(sellingPrice);

                    String firstBidAmount = getFormatedAmount(Integer.parseInt(adsCollection.FirstBidAmount));
                    tvHighestBid.setText(firstBidAmount);
                    String secondBidAmount = getFormatedAmount(Integer.parseInt(adsCollection.SecondBidAmount));
                    tvSecondHighestBid.setText(secondBidAmount);
                    String thirdBidAmount = getFormatedAmount(Integer.parseInt(adsCollection.ThirdBidAmount));
                    tvThirdHighestBid.setText(thirdBidAmount);
                    tvDescription.setText(adsCollection.AdDescription.trim());
                    if(adsCollection.NoOfBids.equals("0"))
                        tvNoOfBids.setVisibility(View.GONE);
                    else
                    {
                        tvNoOfBids.setVisibility(View.VISIBLE);
                        tvNoOfBids.setText(getText(R.string.txt_number_of_bids) + " : " + adsCollection.NoOfBids);
                    }

                    Globals g = Globals.getInstance();
                    if(!token.equals(g.getData()))
                    {
                        if(adsCollection.isDisplayPhoneNumber)
                        {
                            ImageButtonCall.setVisibility(View.VISIBLE);
                            tvCall.setVisibility(View.VISIBLE);
                        }

                        else
                        {
                            tvCall.setVisibility(View.GONE);
                            ImageButtonCall.setVisibility(View.GONE);
                        }

                    }
                    try {
                    String date = adsCollection.Date;

                    //Specify the get date format
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date updateLast = null;

                        updateLast = format.parse(date);


                    //Convert into desired format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
                    String dateTime = dateFormat.format(updateLast);

           /* DateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date updateLast = format.parse(date);
            String newDate = format.format(updateLast);*/


                    tvDate.setText(dateTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    byte[] bytesForAdImageOne = android.util.Base64.decode(adsCollection.AdImageOne, 0);
                    Bitmap bitmapForAdImageOne = BitmapFactory.decodeByteArray(bytesForAdImageOne, 0, bytesForAdImageOne.length);
                    ImagesArray.add(bitmapForAdImageOne);

                    if(adsCollection.AdImageTwo!=null)
                    {
                        indicator.setVisibility(View.VISIBLE);
                        byte[] bytesForAdImageTwo = android.util.Base64.decode(adsCollection.AdImageTwo, 0);
                        Bitmap bitmapForAdImageTwo = BitmapFactory.decodeByteArray(bytesForAdImageTwo, 0, bytesForAdImageTwo.length);
                        ImagesArray.add(bitmapForAdImageTwo);
                    }

                    if(adsCollection.AdImageThree!=null)
                    {
                        byte[] bytesForAdImageThree = android.util.Base64.decode(adsCollection.AdImageThree, 0);
                        Bitmap bitmapForAdImageThree = BitmapFactory.decodeByteArray(bytesForAdImageThree, 0, bytesForAdImageThree.length);
                        ImagesArray.add(bitmapForAdImageThree);
                    }
                    if(adsCollection.AdImageFour!=null)
                    {
                        byte[] bytesForAdImageFour = android.util.Base64.decode(adsCollection.AdImageFour, 0);
                        Bitmap bitmapForAdImageFour = BitmapFactory.decodeByteArray(bytesForAdImageFour, 0, bytesForAdImageFour.length);
                        ImagesArray.add(bitmapForAdImageFour);
                    }
                    if(adsCollection.AdImageFive!=null)
                    {
                        byte[] bytesForAdImageFive = android.util.Base64.decode(adsCollection.AdImageFive, 0);
                        Bitmap bitmapForAdImageFive = BitmapFactory.decodeByteArray(bytesForAdImageFive, 0, bytesForAdImageFive.length);
                        ImagesArray.add(bitmapForAdImageFive);
                    }

                    mPager.setAdapter(new AdapterViewPager(SingleAdDisplayActivity.this,ImagesArray));
                    mPager.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    indicator.setViewPager(mPager);

                    final float density = getResources().getDisplayMetrics().density;

                    //Set circle indicator radius
                    indicator.setRadius(5 * density);

                    NUM_PAGES =ImagesArray.size();

                    // Auto start of viewpager
                    final Handler handler = new Handler();
                    final Runnable Update = new Runnable() {
                        public void run() {
                            if (currentPage == NUM_PAGES) {
                                currentPage = 0;
                            }
                            mPager.setCurrentItem(currentPage++, true);
                        }
                    };

                    commentListView.addHeaderView(header);

                }
            }

            if(dialog.isShowing())
            {
                dialog.dismiss();
            }
        }
    }

    private String getFormatedAmount(int amount){
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);
    }

    public class MakeBidTask extends AsyncTask<Integer, Void, String> {

        private final String bidAmount;
        private final boolean isDisplayPhoneNumber;


        private Dialog dialog = new Dialog(SingleAdDisplayActivity.this, android.R.style.Theme_Black);

        private MakeBidTask(String bidAmount,boolean isDisplayPhoneNumber) {
            this.bidAmount = bidAmount;
            this.isDisplayPhoneNumber = isDisplayPhoneNumber;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleAdDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            try {
                String urlAPI = CommonFunctions.API_URL + "Ad/MakeBidForAds";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("BidAmount", bidAmount);
                jsonObject.accumulate("AdId", AdId);
                jsonObject.accumulate("isDisplayPhoneNumber",isDisplayPhoneNumber);
                Globals g = Globals.getInstance();
                String token = g.getData();
                jsonObject.accumulate("Token", token);

                //Post json content
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                Log.i("Bid Amount", jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

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
            {
                    if(bidAmount != null)
                    {
                        int currentBid = Integer.parseInt(bidAmount.replace(",",""));
                        int highestBid = Integer.parseInt(tvHighestBid.getText().toString().replace(",","")) ;
                        int secondHighestBid = Integer.parseInt(tvSecondHighestBid.getText().toString().replace(",","")) ;
                        int thirdHighestBid = Integer.parseInt(tvThirdHighestBid.getText().toString().replace(",",""));

                        if(currentBid > highestBid)
                        {
                            tvThirdHighestBid.setText(tvSecondHighestBid.getText().toString());
                            tvSecondHighestBid.setText(tvHighestBid.getText().toString());
                            tvHighestBid.setText(getFormatedAmount (Integer.parseInt(bidAmount)));
                        }

                        else if(currentBid > secondHighestBid)
                        {
                            tvThirdHighestBid.setText(tvSecondHighestBid.getText().toString());
                            tvSecondHighestBid.setText(getFormatedAmount (Integer.parseInt(bidAmount)));
                        }

                        else if(currentBid > thirdHighestBid)
                            tvThirdHighestBid.setText(getFormatedAmount (Integer.parseInt(bidAmount)));

                        Globals g = Globals.getInstance();
                        g.setFlagForNewBid(true);
                    }

                Toast.makeText(SingleAdDisplayActivity.this, "Bid Placed", Toast.LENGTH_SHORT).show();
            }

            else
                Toast.makeText(SingleAdDisplayActivity.this, s, Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                // Dismiss/hide the progress dialog
                dialog.dismiss();
            }

            if(dialogForMakingBid.isShowing())
            {
                dialogForMakingBid.dismiss();
            }
        }
    }

    public class SubmitReportTask extends AsyncTask<Integer, Void, String> {

        private final String reportReason;
        private int position;

        private Dialog dialog = new Dialog(SingleAdDisplayActivity.this, android.R.style.Theme_Black);

        private SubmitReportTask(String reportReason) {
            this.reportReason = reportReason;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(SingleAdDisplayActivity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {

            position = params[0];
            try {
                String urlAPI = CommonFunctions.API_URL + "Ad/SubmitReportForAds";
                URL url = new URL(urlAPI);
                //Create URL connection
                HttpURLConnection conn = null;
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                //Create Json Object
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("ReportReason", reportReason);
                jsonObject.accumulate("AdId", AdId);
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
                Toast.makeText(SingleAdDisplayActivity.this, "Report submitted", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Globals g = Globals.getInstance();
        boolean isNewBid = g.getFlagForNewBid();
        if(isNewBid)
        {

        ArrayList<Ads> ads = g.getArrayData();
        if(ads!=null)
        {
            Ads a = ads.get(position);
            a.setAdHighestBid(tvHighestBid.getText().toString().replace(",",""));
        }

        }

    }
}
