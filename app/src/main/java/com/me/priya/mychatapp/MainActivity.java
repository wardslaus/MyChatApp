package com.me.priya.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.me.priya.mychatapp.fragments.ChatFragment;
import com.me.priya.mychatapp.fragments.ProfileFragment;
import com.me.priya.mychatapp.fragments.UsersFragment;
import com.me.priya.mychatapp.model.User;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

  TextView user_name;
  CircleImageView profile_image;
  FirebaseUser firebaseUser;
  DatabaseReference reference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("");

    user_name = findViewById(R.id.user_name);
    profile_image = findViewById(R.id.profile_image);

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    reference = FirebaseDatabase.getInstance().
        getReference("Users").child(firebaseUser.getUid());
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        user_name.setText(user.getUsername());
        if (user.getImageUrl().equals("default")) {
          profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
          Glide.with(getApplicationContext())
              .load(user.getImageUrl()).into(profile_image);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    TabLayout tabLayout = findViewById(R.id.tab_layout);
    ViewPager viewPager = findViewById(R.id.view_pager);
    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    viewPagerAdapter.addFragment(new ChatFragment(),"Chat");
    viewPagerAdapter.addFragment(new UsersFragment(),"Users");
    viewPagerAdapter.addFragment(new ProfileFragment(),"Profile");
    viewPager.setAdapter(viewPagerAdapter);
    tabLayout.setupWithViewPager(viewPager);
  }

  @Override
  protected void onStart() {
    super.onStart();
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    if (firebaseUser == null) {
      Log.i("TAG", "onStart: ");
      startActivity(new Intent(MainActivity.this, StartActivity.class));
      finish();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_logout:
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, StartActivity.class).
        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        return true;
    }
    return false;
  }

  class ViewPagerAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments;
    ArrayList<String> titles;

    public ViewPagerAdapter(FragmentManager fm) {
      super(fm);
      this.fragments = new ArrayList<>();
      this.titles = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
      return fragments.get(position);
    }

    @Override
    public int getCount() {
      return fragments.size();
    }

    public void addFragment(Fragment fragment, String title) {
      fragments.add(fragment);
      titles.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
      return titles.get(position);
    }
  }
  private void status(String status){
    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    HashMap<String,Object> hashMap = new HashMap<>();
    hashMap.put("status",status);
    reference.updateChildren(hashMap);
  }

  @Override
  protected void onResume() {
    super.onResume();
    status("online");
  }

  @Override
  protected void onPause() {
    super.onPause();
    status("offline");
  }
}
