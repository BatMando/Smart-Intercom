package com.example.smartintercom;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smartintercom.FireBase.UserDao;
import com.example.smartintercom.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class login extends AppCompatActivity implements View.OnClickListener {

    protected EditText aptId;
    protected EditText aptPass;
    protected Button Login;
    User user;
    MaterialDialog dialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.Login) {
            String id=aptId.getText().toString();
            final String pass=aptPass.getText().toString();
            if (!id.isEmpty()&& !pass.isEmpty())
            {
                if (id.equals("admin")&&pass.equals("admin")){
                    startActivity(new Intent(login.this,Register.class));
                    finish();
                } else{
                databaseReference.child("users")
                        .orderByChild("username")
                        .equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hideProgressBar();
                        Log.e("snap",snapshot+"");
                        if (!snapshot.hasChildren()){
                            showMessage("error","Invalid Id or password","Ok");
                        }else{
                            for(DataSnapshot object:snapshot.getChildren()){
                                User user=object.getValue(User.class);
                                if (user.getPassword().equalsIgnoreCase(pass)){
                                    DataHolder.currentUser=user;
                                    Gson gson=new Gson();
                                    String userJson= gson.toJson(user);
                                    saveString("user",userJson);

                                    Intent intent=new Intent(login.this,HomePage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hideProgressBar();
                        showMessage("error", error.getMessage(),"ok");
                    }
                });
            }
            }
            else {
                Toast.makeText(this,"Please fill the fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public MaterialDialog showMessage(int titleResId, int messageResId, int posResText) {

        new MaterialDialog.Builder(this)
                .title(titleResId)
                .content(messageResId)
                .positiveText(posResText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        return dialog;

    }

    public MaterialDialog showMessage(String title, String message, String posText) {

        dialog = new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(posText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
        return dialog;
    }

    public MaterialDialog showProgressBar(int message) {
        dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(message)
                .cancelable(false)
                .show();

        return dialog;
    }

    public void hideProgressBar() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
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

    private void initView() {
        aptId = (EditText) findViewById(R.id.apt_id);
        aptPass = (EditText) findViewById(R.id.apt_pass);
        Login = (Button) findViewById(R.id.Login);
        Login.setOnClickListener(login.this);
    }
}
