package com.example.smartintercom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smartintercom.FireBase.UserDao;
import com.example.smartintercom.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Guideline;

public class Register extends AppCompatActivity implements View.OnClickListener {

    protected TextView medica;
    protected Guideline guidelineOfCardView;
    protected EditText usernameEdt;
    protected EditText email;
    protected EditText password;
    protected EditText rePassword;
    protected Button signIn;
    protected EditText port;
    User user;
    MaterialDialog dialog;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_register);
        initView();


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.signIn) {

            final String sName = usernameEdt.getText().toString();
            final String LiveFeed = email.getText().toString();
            final String spassword = password.getText().toString();
            final String Status = rePassword.getText().toString();
            final String Port = port.getText().toString();


            user = new User();
            user.setLiveFeed(LiveFeed);
            user.setUsername(sName);
            user.setPassword(spassword);
            user.setPort(Port);
            user.setStatus(Status);


                databaseReference.child("users")
                        .orderByChild("username")
                        .equalTo(sName).addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    usernameEdt.requestFocus();
                                    usernameEdt.setError("FIELD CANNOT BE EMPTY");
                                } else {



                                    if (sName.length() < 4) {
                                        usernameEdt.requestFocus();
                                        usernameEdt.setError("USERNAME IS TOO SHORT");
                                    } else if (LiveFeed.length() == 0) {
                                        email.requestFocus();
                                        email.setError("FIELD CANNOT BE EMPTY");
                                    } else if (spassword.length() == 0) {
                                        password.requestFocus();
                                        password.setError("FIELD CANNOT BE EMPTY");
                                    } else {
                                        UserDao.getUserByEmail(sName).addListenerForSingleValueEvent
                                                (new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChildren()) {
                                                    showMessage(R.string.error, R.string.email_register_before, R.string.ok);
                                                } else {
                                                    showProgressBar(R.string.loading);
                                                    UserDao.InsertUser(user, onSuccessListener, onFailureListener);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                hideProgressBar();
                                                showMessage(getString(R.string.error), databaseError.getMessage(), getString(R.string.ok));
                                            }
                                        });


                                    }
                                    // User Not Yet Exists
                                    // Do your stuff here if user not yet exists


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );

            }
        }



    OnSuccessListener onSuccessListener = new OnSuccessListener() {
        @Override
        public void onSuccess(Object o) {
            hideProgressBar();

            Intent intent=new Intent(Register.this, login.class);
            intent.putExtra("key",user.getId());
            startActivity(intent);
            finish();


        }
    };
    OnFailureListener onFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            hideProgressBar();
            showMessage(getString(R.string.error), e.getMessage(), getString(R.string.ok));

        }
    };

    private void initView() {

        usernameEdt = (EditText) findViewById(R.id.username_edt);
        email = (EditText) findViewById(R.id.streamlink);
        password = (EditText) findViewById(R.id.password);
        rePassword = (EditText) findViewById(R.id.status);
        signIn = (Button) findViewById(R.id.signIn);
        signIn.setOnClickListener(Register.this);
        port = (EditText) findViewById(R.id.port);
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

}
