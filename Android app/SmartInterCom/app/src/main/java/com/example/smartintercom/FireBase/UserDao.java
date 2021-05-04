package com.example.smartintercom.FireBase;


import com.example.smartintercom.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class UserDao {

    final static String USER="users";

    public static DatabaseReference getUsersBranch(){
        return getInstance().getReference(USER);
    }
    public static void InsertUser(User user, OnSuccessListener onSuccessListener
            , OnFailureListener onFailureListener){

        DatabaseReference userNode= MyDataBase.getUsersBranch().push();
        user.setId(userNode.getKey());
        userNode.setValue(user)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public static Query getUserByEmail(String Email){

        Query query=getUsersBranch().orderByChild("email").equalTo(Email);
        return query;

    }
}
