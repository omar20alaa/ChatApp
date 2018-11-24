package com.chat.chatapp.Activity.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.chat.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    // bind views

    @BindView(R.id.et_user_name)
    MaterialEditText et_user_name;

    @BindView(R.id.et_email)
    MaterialEditText et_email;

    @BindView(R.id.et_password)
    MaterialEditText et_password;

    @BindView(R.id.btn_register)
    Button btn_register;

    @BindView(R.id.tool_bar)
    android.support.v7.widget.Toolbar tool_bar;

    // variables
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String user_id = "", user_name, user_email = "", user_password = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initToolbar();
        initFirebase();
    }

    private void initToolbar() {
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    } // init tool bar

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
    } // init firebase auth

    @OnClick(R.id.btn_register)
    public void registerNewAccount() {

        user_name = et_user_name.getText().toString().trim();
        user_email = et_email.getText().toString().trim();
        user_password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password)) {
            Toast.makeText(this, "All fields are required ...", Toast.LENGTH_SHORT).show();
        } else if (user_password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            registration(user_name, user_email, user_password);
        }

    } // register a new user


    private void registration(final String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            assert firebaseUser != null;
                            user_id = firebaseUser.getUid();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", user_id);
                            hashMap.put("username", username);
                            hashMap.put("imageUrl", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("search", username.toLowerCase());

                            databaseReference.child(user_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(mainActivity);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "You cant register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        {
        }
    } // registration in firebase functions
}
