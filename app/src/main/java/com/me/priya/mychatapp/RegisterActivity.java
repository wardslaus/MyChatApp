package com.me.priya.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

  MaterialEditText username, email, password;
  Button btn_register;
  FirebaseAuth auth;
  DatabaseReference reference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Register");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    username = findViewById(R.id.username);
    email = findViewById(R.id.email);
    password = findViewById(R.id.password);
    btn_register = findViewById(R.id.btn_register);
    auth = FirebaseAuth.getInstance();
    btn_register.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String txt_username = username.getText().toString();
        String txt_email = email.getText().toString();
        String txt_password = password.getText().toString();
        if(TextUtils.isEmpty(txt_username) ||TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
          Toast.makeText(RegisterActivity.this, "All fields are required.", Toast.LENGTH_SHORT)
              .show();
        }else if(txt_password.length() < 6){
          Toast.makeText(RegisterActivity.this, "Password must be atleast 6 characters",
              Toast.LENGTH_SHORT).show();
        }
        register(txt_username,txt_email,txt_password);
      }
    });
  }

  private void register(final String username, String email, String password) {
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
        new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              FirebaseUser firebaseUser = auth.getCurrentUser();
              assert firebaseUser != null;
              String user_id = firebaseUser.getUid();
              reference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
              HashMap<String, String> hashMap = new HashMap<>();
              hashMap.put("id", user_id);
              hashMap.put("username", username);
              hashMap.put("imageUrl", "default");
              hashMap.put("status", "offline");
              hashMap.put("search", username.toLowerCase());
              reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                  if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                  }
                }
              });
            }else{
              Toast.makeText(RegisterActivity.this,
                  "You can't register with this email and password.", Toast.LENGTH_SHORT).show();
            }
          }
        });
  }
}
