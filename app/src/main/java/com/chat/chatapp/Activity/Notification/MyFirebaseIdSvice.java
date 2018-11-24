package com.chat.chatapp.Activity.Notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseIdSvice extends FirebaseInstanceIdService {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String refreshToken;
    Token token;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null)
        {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        token = new Token(refreshToken);
        databaseReference.child(firebaseUser.getUid()).setValue(token);



    } // update token
}
