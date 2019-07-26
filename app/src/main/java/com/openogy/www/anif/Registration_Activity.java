package com.openogy.www.anif;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.openogy.classes.CommonFunctions;
import com.openogy.classes.Globals;
import com.openogy.classes.UserLoginTokenDb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Registration_Activity extends AppCompatActivity {

    //Initializing controls variables
    EditText txtFullName;
    EditText txtEmail;
    EditText txtPassword;
    EditText txtConfirmPassword;
    Button btnSignUp;
    TextView txtSignIn, txtTnC;
    TextView txtPhoneNumber;
    private UserLoginTokenDb db;
    AutoCompleteTextView CityACTV;
    boolean doubleBackToExitPressedOnce = false;
    HttpGetCities getCities;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        findViewById(R.id.mainlayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
            //assigning controls to the variables
            txtSignIn = findViewById(R.id.txtsignin);
            txtTnC = findViewById(R.id.txtAgreeTnC);
            txtFullName = findViewById(R.id.txtFullName);
            txtEmail = findViewById(R.id.txtEmail);
            txtPassword = findViewById(R.id.txtPassword);
            txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
            txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
            btnSignUp = findViewById(R.id.btnSignUp);
            CityACTV = findViewById(R.id.CityACTV);

            getCities = (HttpGetCities) new HttpGetCities().execute();


        CityACTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CityACTV.showDropDown();
                return false;
            }
        });


        final boolean isConnected = checkNetworkConnection();



        //txtView on click event to go back to the login page
       //Button on click event to register
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Calling registration method
                //RegisterUser();
                if(isConnected)
                {
                    if(txtFullName.getText().toString().isEmpty())
                        Toast.makeText(Registration_Activity.this,"FullName Required", Toast.LENGTH_SHORT).show();
                    else if(txtEmail.getText().toString().isEmpty())
                        Toast.makeText(Registration_Activity.this,"Email Required", Toast.LENGTH_SHORT).show();
                    else if(txtPassword.getText().toString().isEmpty())
                        Toast.makeText(Registration_Activity.this,"Password Required", Toast.LENGTH_SHORT).show();
                    else if(txtPhoneNumber.getText().toString().isEmpty())
                        Toast.makeText(Registration_Activity.this,"Phone Number Required", Toast.LENGTH_SHORT).show();
                    else if(CityACTV.getText().toString().isEmpty())
                        Toast.makeText(Registration_Activity.this,"City Required", Toast.LENGTH_SHORT).show();
                    else if(!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString()))
                        Toast.makeText(Registration_Activity.this,"Passwords doesn't match", Toast.LENGTH_SHORT).show();
                    else if(!isValidEmail(txtEmail.getText()))
                        Toast.makeText(Registration_Activity.this,"Invalid Email", Toast.LENGTH_SHORT).show();
                    else
                    {
                        String url = CommonFunctions.API_URL + "User/UserRegistration";
                        new HTTPUserRegistration().execute(url);
                    }

                }
                else
                {
                    Toast.makeText(Registration_Activity.this,"No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration_Activity.this, Login_Activity.class);
                startActivity(intent);
            }
        });

        String signupStringwithTnC = getString(R.string.signup_with_tnc);
        SpannableString spannableString = new SpannableString(signupStringwithTnC);

        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(Registration_Activity.this,terms_and_conditions_activity.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(Color.BLACK);
            }
        };

       spannableString.setSpan(clickableSpan,signupStringwithTnC.indexOf("terms"),signupStringwithTnC.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

       txtTnC.setText(spannableString);
       txtTnC.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private class HTTPUserRegistration extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(Registration_Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(Registration_Activity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(result.equalsIgnoreCase("ok"))
            {
                db = new UserLoginTokenDb(getApplicationContext());
                db.addToken(token);

                Globals g = Globals.getInstance();
                g.setData(db.getToken());
                Toast.makeText(Registration_Activity.this,"Registration Successful", Toast.LENGTH_SHORT).show();

                //Starting homeactivity if successful
                Intent intent = new Intent(Registration_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else if(result.equalsIgnoreCase("Multiple Choices"))
                Toast.makeText(Registration_Activity.this,"User Already Exists.", Toast.LENGTH_SHORT).show();

            else
                Toast.makeText(Registration_Activity.this,"Registration not Successful", Toast.LENGTH_SHORT).show();

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

        InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String bufferedStrChunk = null;

        while((bufferedStrChunk = bufferedReader.readLine()) != null){
            stringBuilder.append(bufferedStrChunk);
        }

        //Setting login_token in global variable to fetch if it's a success
        token = stringBuilder.toString();



        // 5. return response message
        return conn.getResponseMessage();

    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("Name", txtFullName.getText().toString());
        jsonObject.accumulate("Email",  txtEmail.getText().toString());
        jsonObject.accumulate("Password",  txtPassword.getText().toString());
        jsonObject.accumulate("City",CityACTV.getText().toString());
        jsonObject.accumulate("PhoneNumber",txtPhoneNumber.getText().toString());

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

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
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

    private class HttpGetCities extends AsyncTask<Void, Void, List<String>>{

        private Dialog dialog = new Dialog(Registration_Activity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(Registration_Activity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            SetCities();
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        private void SetCities() {
            List<String> jsonCitiesData = null;
            try {
                jsonCitiesData = getCities.get();

            String cityString = jsonCitiesData.toString().substring(3,jsonCitiesData.toString().length() - 3).replace('"',' ');
            String[] array = cityString.split(",");

            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (Registration_Activity.this, android.R.layout.select_dialog_item, array);


            CityACTV.setThreshold(1);
            CityACTV.setAdapter(adapter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected List<String> doInBackground(Void... params) {


            String str = CommonFunctions.API_URL + "City";
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



            // ProductModel productmodel = new ProductModel();
            // return productmodel.findAll();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}