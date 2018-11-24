package com.chat.chatapp.Activity.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.chat.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPassword extends AppCompatActivity {

    // bind views
    @BindView(R.id.tool_bar)
    android.support.v7.widget.Toolbar tool_bar;

    @BindView(R.id.et_send_email)
    MaterialEditText et_send_email;

    @BindView(R.id.btn_reset_passwod)
    Button btn_reset_passwod;

    // vars
    FirebaseAuth firebaseAuth;
    String email = "" , error = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("Reset password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();

    } // init tool bar

    @OnClick(R.id.btn_reset_passwod)
    public void resetPassword()
    {
        email = et_send_email.getText().toString().trim();
        if (email.equals(""))
        {
            Toast.makeText(this, "Field can't be empty !!!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ResetPassword.this, "Please check your email ...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPassword.this , LoginActivity.class));
                    }
                    else
                    {
                        error = task.getException().getMessage();
                        Toast.makeText(ResetPassword.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    } // click on reset password button
}
