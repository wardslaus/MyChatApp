package com.me.priya.mychatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.me.priya.mychatapp.adapters.MessageAdapter;
import com.me.priya.mychatapp.fragments.APIService;
import com.me.priya.mychatapp.model.Chat;
import com.me.priya.mychatapp.model.User;
import com.me.priya.mychatapp.notifications.Client;
import com.me.priya.mychatapp.notifications.Data;
import com.me.priya.mychatapp.notifications.MyResponse;
import com.me.priya.mychatapp.notifications.Sender;
import com.me.priya.mychatapp.notifications.Token;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

  CircleImageView profileImageView;
  TextView txt_username;

  FirebaseUser firebaseUser;
  DatabaseReference reference;
  Intent intent;

  ImageButton send_btn;
  EditText message_edt;

  MessageAdapter messageAdapter;
  List<Chat> chats;
  RecyclerView recyclerView;
  ValueEventListener seenListener;
  String uId;
  APIService apiService;
  boolean notify = false;


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
        startActivity(new Intent(MessageActivity.this, MainActivity.class));
      }
    });

    apiService = Client.getClient("https://fcm.googleapis.com/")
        .create(APIService.class);

    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
    linearLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(linearLayoutManager);

    profileImageView = findViewById(R.id.profile_image);
    txt_username = findViewById(R.id.user_name);

    message_edt = findViewById(R.id.edt_message);
    send_btn = findViewById(R.id.btn_send);

    intent = getIntent();
    uId = intent.getStringExtra("USER_ID");
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    send_btn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        notify = true;
        String msg = message_edt.getText().toString();
        if (!msg.equals("")) {
          sendMessage(firebaseUser.getUid(), uId, msg);
        } else {
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
          Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profileImageView);
        }
        readMessage(firebaseUser.getUid(), user.getId(), user.getImageUrl());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
    seenMessage(uId);
  }

  private void seenMessage(final String userid) {
    reference = FirebaseDatabase.getInstance().getReference("Chats");
    seenListener = reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Chat chat = snapshot.getValue(Chat.class);
          if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("isseen", true);
            snapshot.getRef().updateChildren(hashMap);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void sendMessage(String sender, final String receiver, String message) {
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("sender", sender);
    hashMap.put("receiver", receiver);
    hashMap.put("message", message);
    hashMap.put("isseen", false);
    reference.child("Chats").push().setValue(hashMap);

    //add user to chat fragment
    final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
        .child(firebaseUser.getUid())
        .child(uId);
    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()) {
          chatRef.child("id").setValue(uId);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
    final String msg = message;
    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        if(notify){
          sendNotification(receiver, user.getUsername(), msg);

        }
        notify = false;
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

  }

  private void sendNotification(String receiver, final String username, final String msg) {
    DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
    Query query = tokens.orderByKey().equalTo(receiver);
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Log.i("TAG :: ", "onDataChange: "+ snapshot);
          Token token = snapshot.getValue(Token.class);
          Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + msg,
              "New Message", uId);
          Sender sender = new Sender(data, token.getToken());
          apiService.sendNotification(sender)
              .enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                  if (response.code() == 200) {
                    if (response.body().success != 1) {
                      Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                  }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {

                }
              });
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void readMessage(final String myid, final String userid, final String imageUrl) {
    chats = new ArrayList<>();
    reference = FirebaseDatabase.getInstance().getReference("Chats");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        chats.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Chat chat = snapshot.getValue(Chat.class);
          if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid)
              || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
            chats.add(chat);
          }
          messageAdapter = new MessageAdapter(MessageActivity.this, chats, imageUrl);
          recyclerView.setAdapter(messageAdapter);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void currentUser(String uid){
    SharedPreferences.Editor editor =getSharedPreferences("PREFS", MODE_PRIVATE).edit();
    editor.putString("currentuser", uid);
    editor.apply();
  }

  private void status(String status) {
    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("status", status);
    reference.updateChildren(hashMap);
  }

  @Override
  protected void onResume() {
    super.onResume();
    status("online");
    currentUser(uId);
  }

  @Override
  protected void onPause() {
    super.onPause();
    reference.removeEventListener(seenListener);
    status("offline");
    currentUser("none");
  }

}
