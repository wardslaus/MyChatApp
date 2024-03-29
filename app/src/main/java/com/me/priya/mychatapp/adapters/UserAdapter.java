package com.me.priya.mychatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.priya.mychatapp.MessageActivity;
import com.me.priya.mychatapp.R;
import com.me.priya.mychatapp.model.Chat;
import com.me.priya.mychatapp.model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Priya Jain on 10,December,2018
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

  Context context;
  List<User> userList;
  private boolean isChat;
  String theLastMessage;

  public UserAdapter(Context context, List<User> userList, Boolean isChat) {
    this.context = context;
    this.userList = userList;
    this.isChat = isChat;
  }

  @NonNull
  @Override
  public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
    return new UserAdapter.MyViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder myViewHolder, int position) {
    final User user = userList.get(position);
    myViewHolder.txt_user_name.setText(user.getUsername());
    if (user.getImageUrl().equals("default")) {
      Glide.with(context).load(R.mipmap.ic_launcher).into(myViewHolder.profile_image);
    } else {
      Glide.with(context).load(user.getImageUrl()).into(myViewHolder.profile_image);
    }
    if(isChat){
      lastMessage(user.getId(),myViewHolder.last_msg);
    }else{
      myViewHolder.last_msg.setVisibility(View.GONE);
    }
    if (isChat) {
      if (user.getStatus().equals("online")) {
        myViewHolder.status_on.setVisibility(View.VISIBLE);
        myViewHolder.status_off.setVisibility(View.GONE);
      } else {
        myViewHolder.status_on.setVisibility(View.GONE);
        myViewHolder.status_off.setVisibility(View.VISIBLE);
      }
    }else{
      myViewHolder.status_on.setVisibility(View.GONE);
      myViewHolder.status_off.setVisibility(View.GONE);

    }
    myViewHolder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra("USER_ID", user.getId());
        context.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return userList.size();
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView txt_user_name;
    private CircleImageView profile_image;
    private ImageView status_on;
    private ImageView status_off;
    private TextView last_msg;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      txt_user_name = itemView.findViewById(R.id.user_name);
      profile_image = itemView.findViewById(R.id.profile_image);
      status_on = itemView.findViewById(R.id.status_on);
      status_off = itemView.findViewById(R.id.status_off);
      last_msg = itemView.findViewById(R.id.last_message);
    }
  }
  private void lastMessage(final String userId, final TextView last_msg){
    theLastMessage = "default";
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
          Chat chat = snapshot.getValue(Chat.class);
          if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
              chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())){
            theLastMessage = chat.getMessage();
          }
        }
        switch (theLastMessage){
          case "default":
            last_msg.setText("No Message");
            break;
          default:
            last_msg.setText(theLastMessage);
            break;
        }
        theLastMessage = "default";
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }
}
