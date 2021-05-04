package com.example.smartintercom.FireBase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyDataBase {
    private static FirebaseDatabase firebaseDatabase;

    public  static FirebaseDatabase getInstance(){
        if(firebaseDatabase==null)
            firebaseDatabase= FirebaseDatabase.getInstance();
        return firebaseDatabase;
    }

    final static String USER="users";

    public static DatabaseReference getUsersBranch(){
        return getInstance().getReference(USER);
    }



}
