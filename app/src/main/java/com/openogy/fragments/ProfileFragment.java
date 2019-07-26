package com.openogy.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.classes.Users;
import com.openogy.www.anif.BuildConfig;
import com.openogy.www.anif.R;
import com.openogy.www.anif.Registration_Activity;
import com.openogy.www.anif.UserFeedback_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ProfileFragment extends Fragment {

    ImageView imageview;
    Button btnAds;
    Button btnWishList;
    Button btnBids;
    Fragment fragment;
    String token;
    TextView tvBadge, tvSold, tvBids, tvName, tvCity,tvRating, tvUserFeedBack,tvUserFeedBackValue;
    String returnValue;
    View view;
    ImageButton imageButtonEditProfile, imageButtonRemovePic;
    Users userlist;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 0;
    Uri imageUri;
    String imageFilePath;
    boolean isRemoveProfilePicture = false;
    private String currentUserToken, currentUserName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Globals g = Globals.getInstance();
        token = g.getData();

        String url = CommonFunctions.API_URL + "User/UserDetailsForProfile";
        new HTTPAsyncTask().execute(url);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageview =  getView().findViewById(R.id.ProfileImageView);
        btnAds = getView().findViewById(R.id.btnAd);
        btnWishList = getView().findViewById(R.id.btnWishList);
        btnBids = getView().findViewById(R.id.btnBids);
        tvBadge = getView().findViewById(R.id.tvBadge);
        tvRating = getView().findViewById(R.id.tvUserRating);
        tvUserFeedBack = getView().findViewById(R.id.tvUserFeedBack);
        tvUserFeedBackValue = getView().findViewById(R.id.tvUserFeedBackValue);
        tvSold = getView().findViewById(R.id.tvSold);
        tvBids = getView().findViewById(R.id.tvBids);
        tvName = getView().findViewById(R.id.tvName);
        tvCity = getView().findViewById(R.id.tvCity);
        imageButtonEditProfile = getView().findViewById(R.id.imageButtonEditProfile);
        imageButtonRemovePic = getView().findViewById(R.id.imageButtonRemovePic);

        //Setting Icon to filled when redirects from post wish page
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Menu bottomNavigationMenu =  bottomNavigationView.getMenu();

        //Changing icons of the bottomnavigationitems
        MenuItem item =   bottomNavigationMenu.findItem(R.id.navigation_userprofile);
        MenuItem adItem = bottomNavigationMenu.findItem(R.id.navigation_home);
        MenuItem notificationItem = bottomNavigationMenu.findItem(R.id.navigation_notifications);
        MenuItem wishItem = bottomNavigationMenu.findItem(R.id.navigation_wishlist);
        MenuItem postadItem = bottomNavigationMenu.findItem(R.id.navigation_postad);
        item.setIcon(R.drawable.icons_profile_filled);
        adItem.setIcon(R.drawable.icons_market);

        wishItem.setIcon(R.drawable.icons_wish_list);
        postadItem.setIcon(R.drawable.icons_add);

        Globals g = Globals.getInstance();

        if (g.getFlagForBackPressed())
        {
            g.setFlagForBackPressed(false);
            return;
        };

        if(g.getFlagForNewNotification())
        {
            notificationItem.setIcon(R.drawable.iconss_notification_alarm);
        }
        else
            notificationItem.setIcon(R.drawable.icons_notification);

        String userFeedbackString = getText(R.string.txt_userfeedback).toString();

        SpannableString spannableString = new SpannableString(userFeedbackString);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(getContext(),UserFeedback_Activity.class);

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

        //@TODO
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogChooseSource = new Dialog(getContext());
                dialogChooseSource.setContentView(R.layout.choose_imagesource_popup);
                Button btnCamera = dialogChooseSource.findViewById(R.id.btnCamera);
                Button btnGallery = dialogChooseSource.findViewById(R.id.btnGallery);

                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent pictureIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        if(pictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
                            //Create a file to store the image
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File

                            }
                            if (photoFile != null) {
                                imageUri = FileProvider.getUriForFile(getContext(),BuildConfig.APPLICATION_ID+".provider", photoFile);
                                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        imageUri);
                                startActivityForResult(pictureIntent,
                                        CAMERA_REQUEST);
                            }
                        }
                        dialogChooseSource.dismiss();
                    }
                });

                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , GALLERY_REQUEST);//one can be replaced with any action code
                        dialogChooseSource.dismiss();
                    }
                });

                dialogChooseSource.show();

            }
        });

        btnAds.setTextSize(18);
        getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1,new UserAdsFragment()).addToBackStack(null).commit();

        btnAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAds.setTextSize(18);
                btnWishList.setTextSize(15);
                btnBids.setTextSize(15);
                fragment = new UserAdsFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1,fragment).addToBackStack(null).commit();
            }
        });
        btnWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAds.setTextSize(15);
                btnWishList.setTextSize(18);
                btnBids.setTextSize(15);
                fragment = new UserWishListFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1,fragment).addToBackStack(null).commit();
            }
        });
        btnBids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAds.setTextSize(15);
                btnWishList.setTextSize(15);
                btnBids.setTextSize(18);
                fragment = new UserBidsFragment();
                getChildFragmentManager().beginTransaction().replace(R.id.Framelayout1,fragment).addToBackStack(null).commit();
            }
        });

        imageButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialogForEditProfile = new Dialog(getContext());

                dialogForEditProfile.setContentView(R.layout.edit_profile_layout);
                final AutoCompleteTextView ACTVCity = dialogForEditProfile.findViewById(R.id.ACTVUpdateCity);
                final EditText editTextPhoneNumber = dialogForEditProfile.findViewById(R.id.etEditPhoneNumber);
                final EditText editTextName = dialogForEditProfile.findViewById(R.id.etEditFullName);
                final EditText editTextPassword = dialogForEditProfile.findViewById(R.id.etEditPassword);

                ACTVCity.setText(userlist.City.trim());
                editTextPhoneNumber.setText(userlist.PhoneNumber);
                editTextName.setText(userlist.FullName);

                List<String> jsonCitiesData;
                try {
                    jsonCitiesData = new HttpGetCities().execute().get();

                    String cityString = jsonCitiesData.toString().substring(3,jsonCitiesData.toString().length() - 3).replace('"',' ');
                    String[] array = cityString.split(",");

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getContext(), android.R.layout.select_dialog_item, array);


                    ACTVCity.setThreshold(1);
                    ACTVCity.setAdapter(adapter);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                ACTVCity.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        ACTVCity.showDropDown();
                        return false;
                    }
                });

                Button btnSubmit = dialogForEditProfile.findViewById(R.id.btnSubmit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String city = ACTVCity.getText().toString().trim();
                        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                        String name = editTextName.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        if(city.isEmpty() || phoneNumber.isEmpty() || name.isEmpty())
                        {
                            Toast.makeText(getContext(),"Enter the value for all the fields",Toast.LENGTH_LONG).show();
                            return;
                        }

                        new HttpUpdateProfile().execute(city,phoneNumber,name,password);
                        dialogForEditProfile.dismiss();
                    }
                });
                Button btnCancel = dialogForEditProfile.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogForEditProfile.dismiss();
                    }
                });

                dialogForEditProfile.show();
            }
        });

        imageButtonRemovePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRemoveProfilePicture = true;
                new HttpUpdateProfilePicture().execute();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap thumbnail = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {


                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), imageUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    thumbnail = MediaStore.Images.Media.getBitmap(
                            getActivity().getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int width = thumbnail.getWidth();
            int height = thumbnail.getHeight();

            Bitmap b = thumbnail;

            /*if(width < 1000)
            {
                b = thumbnail;
            }
            else if(width < 2000)
            {
                b = getResizedBitmap(thumbnail, width / 2, height / 2);
            }
            else
            {
                b = getResizedBitmap(thumbnail, width / 4, height / 4);
            }*/



            b = Bitmap.createScaledBitmap(b, 512, height * 512/width, false);

            imageview.setImageResource(android.R.color.transparent);
            imageview.setImageBitmap(b);

            new HttpUpdateProfilePicture().execute();
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;

    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getActivity().
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view != null) {
            if (view.getParent() != null) {
                ((ViewGroup)view.getParent()).removeView(view);
            }
            return view;
        }

        view = inflater.inflate(R.layout.layout_profilefragment,container,false);
        return view;

    }

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
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
                userlist = null;
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

                            //tvBadge.setText(userlist.rating);
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
                                userlist.UserImageBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.add_icon);

                            imageview.setImageBitmap(userlist.UserImageBitmap);
                        }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }


        }
    }

    private class HttpGetCities extends AsyncTask<Void, Void, List<String>>{

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }



        @Override
        protected List<String> doInBackground(Void... params) {


            String str = CommonFunctions.API_URL + "City";;
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(str);
                urlConn = url.openConnection();
                HttpURLConnection httpurlconnection = (HttpURLConnection)urlConn;
                bufferedReader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));




                int responsecode = httpurlconnection.getResponseCode();
                if(responsecode == HttpURLConnection.HTTP_OK)
                {
                    List<String> Data = new ArrayList<String>();
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        Data.add(line);
                    }


                    return Data;
                }
                else
                    return null;

            }
            catch(Exception ex)
            {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class HttpUpdateProfile extends AsyncTask<String,Void,String>{

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String apiUrl = CommonFunctions.API_URL + "User/UpdateUserProfile";
            try {
                URL url = new URL(apiUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json; charset=utf-8");

                JSONObject jsonObject = new JSONObject();
                Globals g = Globals.getInstance();
                String token = g.getData();

                jsonObject.accumulate("Token",token);
                jsonObject.accumulate("City",strings[0]);
                jsonObject.accumulate("PhoneNumber",strings[1]);
                jsonObject.accumulate("Name", strings[2]);
                jsonObject.accumulate("Password", strings[3]);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }

                return stringBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!s.isEmpty())
            {
                userlist = null;
                try {
                    JSONObject json = null;
                    JSONObject data = null;
                    json = new JSONObject(s);
                    data = json.getJSONObject("User");
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
                    tvBadge.setText(userlist.Rating);
                    tvSold.setText(userlist.NumberOfSales);
                    tvBids.setText(userlist.NumberOfBids);
                    tvName.setText(userlist.FullName);
                    tvCity.setText(userlist.City.toUpperCase());
                }

                Toast.makeText(getContext(),"Profile updated",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"Failed to update profile",Toast.LENGTH_SHORT).show();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }

    private class HttpUpdateProfilePicture extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {

            String apiUrl = CommonFunctions.API_URL + "User/UpdateUserProfilePicture";
            try {
                URL url = new URL(apiUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json; charset=utf-8");

                JSONObject jsonObject = new JSONObject();
                Globals g = Globals.getInstance();
                String token = g.getData();
                jsonObject.accumulate("Token",token);

                if(isRemoveProfilePicture)
                    jsonObject.accumulate("ProfileImage", " ");
                else
                {
                    Bitmap imag1bm = ((BitmapDrawable)imageview.getDrawable()).getBitmap();
                    byte[] profileImageByte = getBytesFromBitmap(imag1bm);
                    jsonObject.accumulate("ProfileImage", android.util.Base64.encodeToString(profileImageByte,0));
                }



                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String bufferedStrChunk = null;

                while((bufferedStrChunk = bufferedReader.readLine()) != null){
                    stringBuilder.append(bufferedStrChunk);
                }

                return conn.getResponseMessage();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("ok"))
            {
                if(isRemoveProfilePicture)
                    Toast.makeText(getContext(),"Profile picture removed",Toast.LENGTH_SHORT).show();
                else
                Toast.makeText(getContext(),"Profile picture updated",Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(),"Failed to update profile picture",Toast.LENGTH_SHORT).show();


            if(isRemoveProfilePicture)
            {
                imageview.setImageBitmap(null);
                imageview.setImageResource(R.drawable.add_icon);
                isRemoveProfilePicture = false;
            }


        }
    }
}
