package com.me.priya.mychatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

  Button btn_login, btn_register;
  FirebaseUser firebaseUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    if (firebaseUser != null) {
      Intent intent = new Intent(StartActivity.this, MainActivity.class);
      startActivity(intent);
      finish();
    }
    btn_register = findViewById(R.id.btn_register);
    btn_login = findViewById(R.id.btn_login);
    btn_register.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(intent);
      }
    });
    btn_login.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
      }
    });
  }
}
