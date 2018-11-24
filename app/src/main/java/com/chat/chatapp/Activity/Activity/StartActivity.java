package com.chat.chatapp.Activity.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chat.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    // vars
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        ButterKnife.bind(this);
        initFirebase();
    }

    private void initFirebase() {

        if (firebaseUser != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    } // initialize firebase

    @OnClick(R.id.btn_login)
    public void login() {
        Intent login = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(login);
    } // on login click

    @OnClick(R.id.btn_register)
    public void register() {
        Intent login = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(login);
    } // on login click

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    } // on start
}
