package com.example.smartintercom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.smartintercom.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    protected Button doorbtn;
    protected Button logoutbtn;
    protected WebView title;
    String url;
    String port;
    User users;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home_page);
        initView();

        databaseReference.child("users")
                .orderByChild("username")
                .equalTo(DataHolder.currentUser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.e("snap",snapshot+"");
                if (!snapshot.hasChildren()){

                }else{
                    for(DataSnapshot object:snapshot.getChildren()){
                        User user=object.getValue(User.class);

                            DataHolder.currentUser=user;
                            Gson gson=new Gson();
                            String userJson= gson.toJson(user);
                            saveString("user",userJson);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        url = DataHolder.currentUser.getLiveFeed();
        port = DataHolder.currentUser.getPort();

        title.getSettings().setLoadWithOverviewMode(true);
        title.getSettings().setUseWideViewPort(true);
        Log.e("ip", "http://" + url + ":" + port + "/stream.mjpg");
        title.loadUrl("http://" + url + ":" + port + "/stream.mjpg");

        title.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(final WebView view,
                                           final SslErrorHandler handler,
                                           final SslError error) {
                handler.proceed();
            }
        });

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.doorbtn) {


            databaseReference.child("home/doorstatus").setValue("True");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseReference.child("home/doorstatus").setValue("False");
                }
            }, 3000);
        } else if (view.getId() == R.id.logoutbtn) {
            saveString("user", null);
            startActivity(new Intent(HomePage.this, login.class));
            finish();
        } else if (view.getId() == R.id.title) {

        }

    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor =
                getSharedPreferences("LoginTest", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getstring(String key) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("LoginTest", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    private void initView() {
        doorbtn = (Button) findViewById(R.id.doorbtn);
        doorbtn.setOnClickListener(HomePage.this);
        logoutbtn = (Button) findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(HomePage.this);
        title = findViewById(R.id.title);
        title.setOnClickListener(HomePage.this);
    }
}
