package com.me.priya.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.priya.mychatapp.model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.HashMap;

public class MessageActivity extends AppCompatActivity {

  CircleImageView profileImageView;
  TextView txt_username;

  FirebaseUser firebaseUser;
  DatabaseReference reference;
  Intent intent;

  ImageButton send_btn;
  EditText message_edt;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    toolbar.setNavigationOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    profileImageView = findViewById(R.id.profile_image);
    txt_username = findViewById(R.id.user_name);

    message_edt = findViewById(R.id.edt_message);
    send_btn = findViewById(R.id.btn_send);

    intent = getIntent();
    final String uId = intent.getStringExtra("USER_ID");
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    send_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String msg = message_edt.getText().toString();
        if(!msg.equals("")){
          sendMessage(firebaseUser.getUid(),uId,msg);
        }else{
          Toast.makeText(MessageActivity.this, "You can't send empty text", Toast.LENGTH_SHORT)
              .show();
        }
        message_edt.setText("");
      }
    });

    reference = FirebaseDatabase.getInstance().getReference("Users").child(uId);
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        txt_username.setText(user.getUsername());
        if (user.getImageUrl().equals("default")) {
          profileImageView.setImageResource(R.mipmap.ic_launcher);
        } else {
          Glide.with(MessageActivity.this).load(user.getImageUrl()).into(profileImageView);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }
  private void sendMessage(String sender, String receiver, String message){
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("sender",sender);
    hashMap.put("receiver",receiver);
    hashMap.put("message",message);
    reference.child("Chats").push().setValue(hashMap);

  }
}
