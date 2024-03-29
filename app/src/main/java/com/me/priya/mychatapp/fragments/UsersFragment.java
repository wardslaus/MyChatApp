package com.me.priya.mychatapp.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.me.priya.mychatapp.R;
import com.me.priya.mychatapp.adapters.UserAdapter;
import com.me.priya.mychatapp.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

  private RecyclerView recyclerView;
  private List<User> userList;
  private EditText search_users;
  private UserAdapter userAdapter;

  public UsersFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_users, container, false);

    search_users = view.findViewById(R.id.search_user);
    search_users.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        searchUsers(charSequence.toString().toLowerCase());
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    userList = new ArrayList<>();
    loadUsers();
    return view;
  }

  private void searchUsers(String s) {
    final FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
    Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
        .startAt(s)
        .endAt(s+"\uf8ff");
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userList.clear();
        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
          User user = snapshot.getValue(User.class);
          if(!user.getId().equals(firebaseUser.getUid())){
            userList.add(user);
          }
        }
        userAdapter = new UserAdapter(getContext(),userList,false);
        recyclerView.setAdapter(userAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void loadUsers() {
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if(search_users.getText().toString().equals("")) {
          userList.clear();
          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = snapshot.getValue(User.class);
            assert user != null;
            assert firebaseUser != null;
            if (!user.getId().equals(firebaseUser.getUid())) {
              userList.add(user);
            }
          }
          userAdapter = new UserAdapter(getContext(), userList, false);
          recyclerView.setAdapter(userAdapter);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

}
