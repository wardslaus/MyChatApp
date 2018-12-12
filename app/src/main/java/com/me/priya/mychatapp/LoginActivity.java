package com.me.priya.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

  MaterialEditText et_email, et_password;
  Button btn_login;
  TextView forgot_password;

  FirebaseAuth auth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    auth = FirebaseAuth.getInstance();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Login");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    et_email = findViewById(R.id.email);
    et_password = findViewById(R.id.password);
    btn_login = findViewById(R.id.btn_login);
    forgot_password = findViewById(R.id.forgot_password);
    btn_login.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String txt_email = et_email.getText().toString();
        String txt_password = et_password.getText().toString();
        if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
          Toast.makeText(LoginActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
        } else {
          auth.signInWithEmailAndPassword(txt_email, txt_password)
              .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                  if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                  } else {
                    Toast.makeText(LoginActivity.this, "Error while login", Toast.LENGTH_SHORT)
                        .show();
                  }
                }

              }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              e.printStackTrace();
            }
          });
        }
      }
    });

    forgot_password.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
      }
    });

  }
}
