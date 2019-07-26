package com.openogy.www.anif;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.openogy.classes.Globals;
import com.openogy.classes.UserLoginTokenDb;

public class Loading_Screen_Activity extends AppCompatActivity {

    private UserLoginTokenDb db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading__screen);

        db = new UserLoginTokenDb(this);
        String token = db.getToken();
        db.close();
        if(token == null || token == "")
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(Loading_Screen_Activity.this, Login_Activity.class);
                    Loading_Screen_Activity.this.startActivity(mainIntent);
                    Loading_Screen_Activity.this.finish();
                }
            }, 3000);
        }
        else
        {
            // set global variable login_token
            Globals g = Globals.getInstance();
            g.setData(token);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(Loading_Screen_Activity.this, HomeActivity.class);
                    Loading_Screen_Activity.this.startActivity(mainIntent);
                    Loading_Screen_Activity.this.finish();
                }
            }, 3000);
        }


    }
}
