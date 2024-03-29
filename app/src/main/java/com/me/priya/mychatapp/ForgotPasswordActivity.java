package com.me.priya.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

  EditText send_email;
  Button btn_reset;

  FirebaseAuth firebaseAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Forgot Password");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    send_email = findViewById(R.id.send_email);
    btn_reset = findViewById(R.id.btn_reset);
    firebaseAuth = FirebaseAuth.getInstance();
    btn_reset.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String email = send_email.getText().toString();
        if (email.equals("")) {
          Toast
              .makeText(ForgotPasswordActivity.this, "Email field is required.", Toast.LENGTH_SHORT)
              .show();
        } else {
          firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(
              new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your Email.",
                        Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                  } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                  }
                }
              });
        }
      }
    });

  }
}
