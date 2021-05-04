package com.example.smartintercom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.smartintercom.Models.User;
import com.google.gson.Gson;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        String userjson=getstring("user");
        if (userjson!=null){
            Gson gson=new Gson();
            DataHolder.currentUser=gson.fromJson(userjson, User.class);
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(splash.this,HomePage.class));
                            finish();
                        }
                    },2000);
        }
        else {
        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(splash.this,login.class));
                        finish();
                    }
                },2000);
        }
    }


    public  void saveString (String key ,String value){
        SharedPreferences.Editor editor=
                getSharedPreferences("LoginTest",MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();

    }
    public String getstring(String key){
        SharedPreferences sharedPreferences=
                getSharedPreferences("LoginTest",MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }
}
