package com.openogy.www.anif;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.openogy.classes.CommonFunctions;
import com.openogy.classes.GMail;
import com.openogy.classes.Globals;
import com.openogy.classes.UserLoginTokenDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Login_Activity extends AppCompatActivity {

    EditText txtEmail;
    EditText txtPassword;
    Button btnSignIn;
    TextView txtSignUp;
    private UserLoginTokenDb db;
    boolean doubleBackToExitPressedOnce = false;
    TextView txtForgotPassword;
    Dialog dialogForForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.mainlayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });

        txtSignUp = findViewById(R.id.txtsignup);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        dialogForForgotPassword = new Dialog(Login_Activity.this);

        txtSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final Intent mainIntent = new Intent(Login_Activity.this, Registration_Activity.class);
                Login_Activity.this.startActivity(mainIntent);
                Login_Activity.this.finish();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForForgotPassword.setContentView(R.layout.layout_forgot_password);
                // dialog.setCancelable(false);
                final EditText editText = dialogForForgotPassword.findViewById(R.id.editTextEmail);
                Button btnSubmit = dialogForForgotPassword.findViewById(R.id.btnSubmit);
                final TextView txtResult = dialogForForgotPassword.findViewById(R.id.txtResult);
                txtResult.setText("");
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText.getText().toString().trim().isEmpty() || !isValidEmail(editText.getText().toString().trim()))
                            txtResult.setText(R.string.error_enter_email);
                        else {

                           String userEmail = editText.getText().toString().trim();
                           new GetPasswordAsyncTask(userEmail).execute();

                        }
                    }
                });
                Button btnCancel = dialogForForgotPassword.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogForForgotPassword.dismiss();
                    }
                });

                dialogForForgotPassword.show();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isConnected = checkNetworkConnection();
                if(isConnected)
                {
                    String email = txtEmail.getText().toString();
                    String password = txtPassword.getText().toString();
                    if(email.isEmpty() || password.isEmpty() || email == null || password == null)
                    {
                        Toast.makeText(Login_Activity.this,"Email and password required.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String url = CommonFunctions.API_URL + "User/UserLogin";
                        new HTTPAsyncTask().execute(url);
                    }

                }

                else
                {
                    Toast.makeText(Login_Activity.this,"No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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

    private class HTTPAsyncTask extends AsyncTask<String, Void, String> {

        private Dialog dialog = new Dialog(Login_Activity.this, android.R.style.Theme_Black);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(Login_Activity.this).inflate(R.layout.layout_progressbar, null);
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
                Intent intent = new Intent(Login_Activity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            else
                Toast.makeText(Login_Activity.this,"Invalid Details.", Toast.LENGTH_SHORT).show();

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
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

        String responseMessage = conn.getResponseMessage();
        if(responseMessage.equalsIgnoreCase("OK"))
        {
            db = new UserLoginTokenDb(this);
            String token = db.getToken();
            if(token == null)
                db.addToken(stringBuilder.toString()); //saving login_token in local database
            // set global variable login_token
            Globals g = Globals.getInstance();
            g.setData(db.getToken());
        }
        // 5. return response message
        return conn.getResponseMessage();

    }

    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        db = new UserLoginTokenDb(this);
        String token = db.getToken();
        jsonObject.accumulate("Token",  token == null ? "" : token);
        jsonObject.accumulate("Email",  txtEmail.getText().toString());
        jsonObject.accumulate("Password",  txtPassword.getText().toString());
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

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "tap BACK twice to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public class GetPasswordAsyncTask extends AsyncTask<String,Void,String>{

        private String Email;

        private GetPasswordAsyncTask(String EmailAddress)
        {
            this.Email = EmailAddress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            String getPasswordUrl = CommonFunctions.API_URL + "User/ForgotPassword";
            try {
                URL url = new URL(getPasswordUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("content-type","application/json; charset=utf-8");

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Email",Email);

                connection.connect();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder stringBuilder = new StringBuilder();
                String bufferChunk;

                while((bufferChunk = bufferedReader.readLine()) !=  null)
                    stringBuilder.append(bufferChunk);

                return stringBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null)
            {
                String password = s.trim();

                String fromEmail = "ifadvertisings@gmail.com";
                String fromPassword = "Sagar123!";
                String toEmails = Email;
                List<String> toEmailList = Arrays.asList(toEmails
                        .split("\\s*,\\s*"));
                Log.i("SendMailActivity", "To List: " + toEmailList);
                String emailSubject = "Your IF password request";
                String emailBody = "Hello, Current password for your registered IF account is " + password;
                new SendMailTask(Login_Activity.this).execute(fromEmail,
                        fromPassword, toEmailList, emailSubject, emailBody);
            }
            super.onPostExecute(s);
        }
    }

    public class SendMailTask extends AsyncTask {

        private Dialog dialog = new Dialog(Login_Activity.this);
        private Activity sendMailActivity;

        public SendMailTask(Activity activity) {
            sendMailActivity = activity;

        }

        protected void onPreExecute() {
            super.onPreExecute();
            View view = LayoutInflater.from(Login_Activity.this).inflate(R.layout.layout_progressbar, null);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(view);
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object... args) {
            try {
                Log.i("SendMailTask", "About to instantiate GMail...");
                //publishProgress("Processing input....");
                GMail androidEmail = new GMail(args[0].toString(),
                        args[1].toString(), (List) args[2], args[3].toString(),
                        args[4].toString());
               // publishProgress("Preparing mail message....");
                androidEmail.createEmailMessage();
               // publishProgress("Sending email....");
                androidEmail.sendEmail();
               // publishProgress("Email Sent.");
                Log.i("SendMailTask", "Mail Sent.");

            } catch (Exception e) {
               // publishProgress(e.getMessage());
                Log.e("SendMailTask", e.getMessage(), e);
            }
            return null;
        }

        @Override
        public void onPostExecute(Object result) {
            Toast.makeText(Login_Activity.this, "Password sent to Email address", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            dialogForForgotPassword.dismiss();
        }

    }
}
