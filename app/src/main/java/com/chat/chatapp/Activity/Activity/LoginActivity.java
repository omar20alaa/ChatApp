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
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    // bind views

    @BindView(R.id.et_email)
    MaterialEditText et_email;

    @BindView(R.id.et_password)
    MaterialEditText et_password;

    @BindView(R.id.btn_login)
    Button btn_login;

    @BindView(R.id.tool_bar)
    android.support.v7.widget.Toolbar tool_bar;

    // variables
    FirebaseAuth firebaseAuth;
    String user_email = "", user_password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initToolbar();
    }

    @OnClick(R.id.tv_forget_password)
    public void forgetPassword()
    {
        startActivity(new Intent(this,ResetPassword.class));

    } // click on forget password

    @OnClick(R.id.btn_login)
    public void login()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        user_email = et_email.getText().toString().trim();
        user_password = et_password.getText().toString().trim();

        if (TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_password)) {
            Toast.makeText(this, "All fields are required ...", Toast.LENGTH_SHORT).show();
        }
        else if (user_password.length() < 6)
        {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        }
        else {
            firebaseAuth.signInWithEmailAndPassword(user_email , user_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mainActivity);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "Authentication failed ...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        }


    } // on login button click

    private void initToolbar() {
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    } // init tool bar
}
