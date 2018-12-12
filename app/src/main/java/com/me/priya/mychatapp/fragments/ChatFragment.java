package com.me.priya.mychatapp.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.priya.mychatapp.R;
import com.me.priya.mychatapp.adapters.UserAdapter;
import com.me.priya.mychatapp.model.Chat;
import com.me.priya.mychatapp.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

  private RecyclerView recyclerView;
  private UserAdapter userAdapter;
  private List<User> mUsers;
  FirebaseUser firebaseUser;
  DatabaseReference reference;
  private List<String> usersList;

  public ChatFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_chat, container, false);

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(linearLayoutManager);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    usersList = new ArrayList<>();
    reference = FirebaseDatabase.getInstance().getReference("Chats");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        usersList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          Chat chat = snapshot.getValue(Chat.class);
          if (chat.getSender().equals(firebaseUser.getUid())) {
            usersList.add(chat.getReceiver());
          }
          //TODO: Some changes needed.
//           if (chat.getReceiver().equals(firebaseUser.getUid())) {
//            usersList.add(chat.getSender());
//          }
        }
        readChats();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
    return view;
  }

  private void readChats() {
    mUsers = new ArrayList<>();
    reference = FirebaseDatabase.getInstance().getReference("Users");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mUsers.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          User user = snapshot.getValue(User.class);
          //display user with we chat in chat window
          for (String id : usersList) {
            if (user.getId().equals(id)) {
              if (mUsers.size() != 0) {
                for (User user1 : mUsers) {
                  if (!user.getId().equals(user1.getId())) {
                    Log.i("TAG :", "onDataChange: IF "+ user.getUsername());
                    mUsers.add(user);
                  }
                }
              } else {
                mUsers.add(user);
                Log.i("TAG :", "onDataChange: ELSE");

              }
            }
          }
        }
        userAdapter = new UserAdapter(getContext(), mUsers,true);
        recyclerView.setAdapter(userAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }
}
