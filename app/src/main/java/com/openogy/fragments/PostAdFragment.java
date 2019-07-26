package com.openogy.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.www.anif.BuildConfig;
import com.openogy.www.anif.R;
import com.openogy.www.anif.Registration_Activity;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PostAdFragment extends Fragment {

    ImageButton imageButton1;
    ImageView imageview1;
    ImageView imageview2;
    ImageView imageview3;
    ImageView imageview4;
    ImageView imageview5;
    String token;
    EditText title;
    AutoCompleteTextView ACTVCategory;
    AutoCompleteTextView ACTVLocality;
    EditText description;
    EditText sellingPrice;
    CheckBox adCheckBox;
    CheckBox wishCheckBox;
    CheckBox oldCheckBox;
    CheckBox newCheckBox;
    Button submit;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 0;
    byte[] image1Byte, image2Byte, image3Byte, image4Byte, image5Byte = null;
    Uri imageUri;
    String imageFilePath;
    HttpGetCategories getCategories;
    HTTPGetLocalitiesForCurrentCity getLocalitiesForCurrentCity;
    View view;
    CheckBox isDisplayPhoneNumber;
    TextView tvTitle, tvSellingPrice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Globals g = Globals.getInstance();
        token = g.getData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayout mainlayout = getView().findViewById(R.id.mainlayout);

        mainlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        //Setting Icon to filled when redirects from post wish page
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Menu bottomNavigationMenu =  bottomNavigationView.getMenu();

        //Changing icons of the bottomnavigationitems
        MenuItem item =   bottomNavigationMenu.findItem(R.id.navigation_postad);
        MenuItem adItem = bottomNavigationMenu.findItem(R.id.navigation_home);
        MenuItem notificationItem = bottomNavigationMenu.findItem(R.id.navigation_notifications);
        MenuItem wishItem = bottomNavigationMenu.findItem(R.id.navigation_wishlist);
        MenuItem profileItem = bottomNavigationMenu.findItem(R.id.navigation_userprofile);
        item.setIcon(R.drawable.icons_add_filled);
        adItem.setIcon(R.drawable.icons_market);
        //notificationItem.setIcon(R.drawable.icons_notification);
        wishItem.setIcon(R.drawable.icons_wish_list);
        profileItem.setIcon(R.drawable.icons_profile);

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

            ACTVCategory = getView().findViewById(R.id.ACTVCategory);
            ACTVCategory.setThreshold(1);
            ACTVLocality = getView().findViewById(R.id.ACTVLocality);
            ACTVLocality.setThreshold(1);
            boolean isConnected = checkNetworkConnection();
            if (isConnected)
            {
                getCategories = (HttpGetCategories) new HttpGetCategories().execute();
                String url = CommonFunctions.API_URL + "Locality/GetLocalities";
                getLocalitiesForCurrentCity = (HTTPGetLocalitiesForCurrentCity) new HTTPGetLocalitiesForCurrentCity().execute(url);
            }



            //Getting all controls
            imageButton1 = getView().findViewById(R.id.imageButton1);
            imageview1 = getView().findViewById(R.id.imageView1);
            imageview2 = getView().findViewById(R.id.imageView2);
            imageview3 = getView().findViewById(R.id.imageView3);
            imageview4 = getView().findViewById(R.id.imageView4);
            imageview5 = getView().findViewById(R.id.imageView5);
            title = getView().findViewById(R.id.TitleEditText);
            tvTitle = getView().findViewById(R.id.tvTitleHeading);
            tvSellingPrice = getView().findViewById(R.id.tvTitlePrice);
            description = getView().findViewById(R.id.DescriptionEditText);
            sellingPrice = getView().findViewById(R.id.PriceEditText);
            adCheckBox = getView().findViewById(R.id.PostAdCheckBox);
            wishCheckBox = getView().findViewById(R.id.WishCheckBox);
            oldCheckBox = getView().findViewById(R.id.OldCheckBox);
            newCheckBox = getView().findViewById(R.id.NewCheckBox);
        isDisplayPhoneNumber = getView().findViewById(R.id.DisplayPhoneNumberCheckBox);
            submit = getView().findViewById(R.id.btnSubmit);


        ACTVCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ACTVCategory.showDropDown();
                return false;
            }
        });

        ACTVLocality.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ACTVLocality.showDropDown();
                return false;
            }
        });
        adCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adCheckBox.isChecked()) {
                    wishCheckBox.setChecked(false);
                    setAdFormValues();
                } else {
                    wishCheckBox.setChecked(true);
                    setWishFormValues();
                }

            }
        });

        wishCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wishCheckBox.isChecked()) {
                    adCheckBox.setChecked(false);
                    setWishFormValues();
                } else {
                    adCheckBox.setChecked(true);
                    setAdFormValues();
                }
            }
        });

        newCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newCheckBox.isChecked()) {
                    oldCheckBox.setChecked(false);
                } else {
                    oldCheckBox.setChecked(true);
                }

            }
        });

        oldCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (oldCheckBox.isChecked()) {
                    newCheckBox.setChecked(false);
                } else {
                    newCheckBox.setChecked(true);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkNetworkConnection())
                {
                    if (adCheckBox.isChecked()) {
                        if (title.getText().toString().isEmpty())
                            Toast.makeText(getContext(), "Title Required", Toast.LENGTH_SHORT).show();
                        else if (ACTVCategory.getText().toString().isEmpty())
                            Toast.makeText(getContext(), "Category Required", Toast.LENGTH_SHORT).show();
                        else if (ACTVLocality.getText().toString().isEmpty())
                            Toast.makeText(getContext(), "Locality Required", Toast.LENGTH_SHORT).show();
                        else if (imageview1.getDrawable() == null)
                            Toast.makeText(getContext(), "At least one image Required", Toast.LENGTH_SHORT).show();
                        else if (description.getText().toString().isEmpty())
                            Toast.makeText(getContext(), "Description Required", Toast.LENGTH_SHORT).show();
                        else
                        {
                            String url = CommonFunctions.API_URL + "Ad/PostAd";
                            new HTTPPostAdorWish().execute(url);
                        }

                    } else if (wishCheckBox.isChecked()) {
                        if (ACTVCategory.getText().toString().isEmpty())
                            Toast.makeText(getContext(), "Category Required", Toast.LENGTH_SHORT).show();
                        else if (ACTVLocality.getText().toString().isEmpty())
                            Toast.makeText(getContext(), "Locality Required", Toast.LENGTH_SHORT).show();
                        else if (description.getText().toString().isEmpty())
                            Toast.makeText(getContext(), "Description Required", Toast.LENGTH_SHORT).show();
                        else {
                            String url = CommonFunctions.API_URL + "WishList/PostWish";
                            new HTTPPostAdorWish().execute(url);
                        }
                    }
                }
                else
                    Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_SHORT).show();



            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adCheckBox.isChecked())
                {
                    //Checking if 5 images are already selected or not
                    if (imageview1.getDrawable() == null ||
                            imageview2.getDrawable() == null ||
                            imageview3.getDrawable() == null ||
                            imageview4.getDrawable() == null ||
                            imageview5.getDrawable() == null)
                    {

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
                    } else
                        Toast.makeText(getContext(), "Only 5 image allowed", Toast.LENGTH_SHORT).show();
                }

                else if(wishCheckBox.isChecked())
                {
                    if (imageview1.getDrawable() == null) {


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

                    } else
                        Toast.makeText(getContext(), "Only 1 image allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        imageview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageview1.setImageDrawable(null);
            }
        });

        imageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageview2.setImageDrawable(null);
            }
        });

        imageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageview3.setImageDrawable(null);
            }
        });

        imageview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageview4.setImageDrawable(null);
            }
        });

        imageview5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageview5.setImageDrawable(null);
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

            Bitmap b;

            if(width < 1000)
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
            }

            if (imageview1.getDrawable() == null)
                imageview1.setImageBitmap(b);
            else if (imageview2.getDrawable() == null)
                imageview2.setImageBitmap(b);
            else if (imageview3.getDrawable() == null)
                imageview3.setImageBitmap(b);
            else if (imageview4.getDrawable() == null)
                imageview4.setImageBitmap(b);
            else if (imageview5.getDrawable() == null)
                imageview5.setImageBitmap(b);
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

    private void setAdFormValues() {
        title.setVisibility(View.VISIBLE);
        sellingPrice.setVisibility(View.VISIBLE);
        oldCheckBox.setVisibility(View.VISIBLE);
        newCheckBox.setVisibility(View.VISIBLE);
        newCheckBox.setChecked(true);
        oldCheckBox.setChecked(false);
        description.setHint("A good description attracts more attention");
        ACTVLocality.setText(null);
        ACTVCategory.setText(null);
        description.setText(null);
        sellingPrice.setText(null);
        title.requestFocus();
        title.setText(null);
        imageview1.setImageDrawable(null);
        imageview2.setImageDrawable(null);
        imageview3.setImageDrawable(null);
        imageview4.setImageDrawable(null);
        imageview5.setImageDrawable(null);
        tvTitle.setVisibility(View.VISIBLE);
        tvSellingPrice.setVisibility(View.VISIBLE);
    }

    private void setWishFormValues() {
        title.setVisibility(View.GONE);
        sellingPrice.setVisibility(View.GONE);
        oldCheckBox.setVisibility(View.GONE);
        newCheckBox.setVisibility(View.GONE);
        description.setHint("What are you looking for?");
        ACTVLocality.setText(null);
        ACTVCategory.setText(null);
        description.setText(null);
        ACTVCategory.requestFocus();
        imageview1.setImageDrawable(null);
        imageview2.setImageDrawable(null);
        imageview3.setImageDrawable(null);
        imageview4.setImageDrawable(null);
        imageview5.setImageDrawable(null);
        tvTitle.setVisibility(View.GONE);
        tvSellingPrice.setVisibility(View.GONE);
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = false;
        if (networkInfo != null && (isConnected = networkInfo.isConnected())) {
            isConnected = true;


        } else {
            isConnected = false;
        }

        return isConnected;
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



        view = inflater.inflate(R.layout.layout_postadfragment,container,false);
        return view;

    }

    private class HTTPPostAdorWish extends AsyncTask<String, Void, String> {

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        Menu bottomNavigationMenu =  bottomNavigationView.getMenu();
        MenuItem item =   bottomNavigationMenu.findItem(R.id.navigation_postad);

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = null;
            if(adCheckBox.isChecked())
                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
            else if(wishCheckBox.isChecked())
                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
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

        protected void onPostExecute(String result) {


            if (result.equalsIgnoreCase("ok")) {
                if(adCheckBox.isChecked())
                {
                    setAdFormValues();
                    Toast.makeText(getContext(), "Ad Posted", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.icons_add);
                }

                else if(wishCheckBox.isChecked())
                {
                    setWishFormValues();
                    Toast.makeText(getContext(), "Wish Posted", Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.icons_add);
                }

            }
            else
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private String HttpPost(String myUrl) throws IOException, JSONException {

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

        return conn.getResponseMessage();
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
        return stream.toByteArray();
    }

    private JSONObject buidJsonObject() throws JSONException, UnsupportedEncodingException {

        JSONObject jsonObject = new JSONObject();
        if(adCheckBox.isChecked())
        {
            jsonObject.accumulate("Token", token);
            jsonObject.accumulate("AdTitle", title.getText().toString().trim());
            jsonObject.accumulate("AdCategory", ACTVCategory.getText().toString().trim());
            jsonObject.accumulate("AdLocality", ACTVLocality.getText().toString().trim());

            Bitmap imag1bm = ((BitmapDrawable)imageview1.getDrawable()).getBitmap();
            image1Byte = getBytesFromBitmap(imag1bm);
            jsonObject.accumulate("AdImage1", android.util.Base64.encodeToString(image1Byte,0));


            if(imageview2.getDrawable() != null)
            {
                Bitmap imag2bm = ((BitmapDrawable)imageview2.getDrawable()).getBitmap();
                image2Byte = getBytesFromBitmap(imag2bm);
                jsonObject.accumulate("AdImage2", android.util.Base64.encodeToString(image2Byte,0));
            }

            if(imageview3.getDrawable() != null)
            {
                Bitmap imag2bm = ((BitmapDrawable)imageview3.getDrawable()).getBitmap();
                image3Byte = getBytesFromBitmap(imag2bm);
                jsonObject.accumulate("AdImage3", android.util.Base64.encodeToString(image3Byte,0));
            }
            if(imageview4.getDrawable() != null)
            {
                Bitmap imag2bm = ((BitmapDrawable)imageview4.getDrawable()).getBitmap();
                image4Byte = getBytesFromBitmap(imag2bm);
                jsonObject.accumulate("AdImage4", android.util.Base64.encodeToString(image4Byte,0));
            }
            if(imageview5.getDrawable() != null)
            {
                Bitmap imag2bm = ((BitmapDrawable)imageview5.getDrawable()).getBitmap();
                image5Byte = getBytesFromBitmap(imag2bm);
                jsonObject.accumulate("AdImage5", android.util.Base64.encodeToString(image5Byte,0));
            }

            jsonObject.accumulate("AdDescription", description.getText().toString().trim());

            if(sellingPrice.getText().toString().trim().isEmpty())
                jsonObject.accumulate("AdSellingPrice", "0");
            else
                jsonObject.accumulate("AdSellingPrice", sellingPrice.getText().toString().trim());

            if(oldCheckBox.isChecked())
            {
                jsonObject.accumulate("Filter","Used");
            }
            else if(newCheckBox.isChecked())
            {
                jsonObject.accumulate("Filter","New");
            }
            jsonObject.accumulate("DisplayPhoneNumber",isDisplayPhoneNumber.isChecked());
        }

        else if(wishCheckBox.isChecked())
        {
            jsonObject.accumulate("Token", token);
            jsonObject.accumulate("WishCategory", ACTVCategory.getText().toString().trim());
            jsonObject.accumulate("WishLocality", ACTVLocality.getText().toString().trim());

            if(imageview1.getDrawable() != null)
            {
                Bitmap imag1bm = ((BitmapDrawable)imageview1.getDrawable()).getBitmap();
                image1Byte = getBytesFromBitmap(imag1bm);
                jsonObject.accumulate("WishImage", android.util.Base64.encodeToString(image1Byte,0));
            }
            jsonObject.accumulate("WishDescription", description.getText().toString());
            jsonObject.accumulate("DisplayPhoneNumber",isDisplayPhoneNumber.isChecked());
        }
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

    private class HttpGetCategories extends AsyncTask<Void, Void, List<String>> {

        private Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected List<String> doInBackground(Void... params) {

            String str = CommonFunctions.API_URL + "Category";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(str);
                urlConn = url.openConnection();
                HttpURLConnection httpurlconnection = (HttpURLConnection) urlConn;
                bufferedReader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));


                int responsecode = httpurlconnection.getResponseCode();
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    List<String> Data = new ArrayList<String>();
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        Data.add(line);
                    }
                    return Data;
                } else
                    return null;

            } catch (Exception ex) {
                Log.e("App", "yourDataTask", ex);
                return null;
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            try {
                List<String> jsonCategories = null;
                jsonCategories = getCategories.get();

                if (jsonCategories != null) {
                    String categoryString = jsonCategories.toString().substring(3, jsonCategories.toString().length() - 3).replace('"', ' ');
                    String[] array = categoryString.split(",");

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (getContext(), android.R.layout.select_dialog_item, array);
                    ACTVCategory.setAdapter(adapter);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    private class HTTPGetLocalitiesForCurrentCity extends AsyncTask<String, Void, List<String>> {

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
        protected List<String> doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                try {
                    return HttpPostToGetLocalities(urls[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            List<String> jsonLocalities = null;
            try {
                jsonLocalities = getLocalitiesForCurrentCity.get();


                if (jsonLocalities != null) {
                    String LocalitiesString = jsonLocalities.toString().substring(3, jsonLocalities.toString().length() - 3).replace('"', ' ');
                    String[] localityArray = LocalitiesString.split(",");

                    ArrayAdapter<String> localityAdapter = new ArrayAdapter<String>
                            (getContext(), android.R.layout.select_dialog_item, localityArray);

                    ACTVLocality.setAdapter(localityAdapter);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private List<String> HttpPostToGetLocalities(String myUrl) throws IOException, JSONException {

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buildJsonObjectToGetLocalities();

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String bufferedStrChunk = null;

        int responsecode = conn.getResponseCode();
        if (responsecode == HttpURLConnection.HTTP_OK) {
            List<String> Data = new ArrayList<String>();
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Data.add(line);
            }

            return Data;
        }
        else
            return null;
    }

    private JSONObject buildJsonObjectToGetLocalities() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Token", token);
        return jsonObject;
    }
}
