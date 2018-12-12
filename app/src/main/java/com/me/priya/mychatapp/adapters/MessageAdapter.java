package com.me.priya.mychatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

  public static final int MSG_TYPE_LEFT = 0;
  public static final int MSG_TYPE_RIGHT = 1;

  private Context context;
  private List<Chat> chats;
  String image_url;
  FirebaseUser firebaseUser;

  public MessageAdapter(Context context, List<Chat> chats, String image_url) {
    this.context = context;
    this.chats = chats;
    this.image_url = image_url;
  }

  @NonNull
  @Override
  public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if(viewType == MSG_TYPE_RIGHT){
      View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
      return new MessageAdapter.MyViewHolder(view);

    }else{
      View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
      return new MessageAdapter.MyViewHolder(view);

    }
  }

  @Override
  public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder myViewHolder, int position) {
    final Chat chat = chats.get(position);
    myViewHolder.show_message.setText(chat.getMessage());
    if (image_url.equals("default")) {
      Glide.with(context).load(R.mipmap.ic_launcher).into(myViewHolder.profile_image);
    } else {
      Glide.with(context).load(image_url).into(myViewHolder.profile_image);
    }
  }

  @Override
  public int getItemCount() {
    return chats.size();
  }

  public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView show_message;
    CircleImageView profile_image;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      show_message = itemView.findViewById(R.id.show_message);
      profile_image = itemView.findViewById(R.id.profile_image);
    }
  }

  @Override
  public int getItemViewType(int position) {
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    if(chats.get(position).getSender().equals(firebaseUser.getUid())){
      return MSG_TYPE_RIGHT;
    }else{
      return MSG_TYPE_LEFT;
    }
  }
}
