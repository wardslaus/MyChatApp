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
import com.me.priya.mychatapp.MessageActivity;
import com.me.priya.mychatapp.R;
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

  public UserAdapter(Context context, List<User> userList) {
    this.context = context;
    this.userList = userList;
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
    myViewHolder.itemView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(context,MessageActivity.class);
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

    TextView txt_user_name;
    CircleImageView profile_image;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      txt_user_name = itemView.findViewById(R.id.user_name);
      profile_image = itemView.findViewById(R.id.profile_image);
    }
  }
}
