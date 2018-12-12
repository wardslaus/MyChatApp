package com.me.priya.mychatapp.fragments;


import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;
import com.me.priya.mychatapp.R;
import com.me.priya.mychatapp.model.User;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

  ImageView profile_image;
  TextView txt_name;

  DatabaseReference reference;
  FirebaseUser firebaseUser;
  StorageReference storageReference;
  private static final int IMAGE_REQUEST = 1;
  private Uri imageUri;
  private StorageTask uploadTask;


  public ProfileFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    // Inflate the layout for this fragment
    profile_image = view.findViewById(R.id.profile_image);
    txt_name = view.findViewById(R.id.user_name);

    storageReference = FirebaseStorage.getInstance().getReference("uploads");

    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        txt_name.setText(user.getUsername());
        Log.i("TAG :", "onDataChange: "+user.getImageUrl());
        if (user.getImageUrl().equals("default")) {
          profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
          Log.i("TAG", "onDataChange: ");
          Glide.with(getContext()).load(user.getImageUrl()).into(profile_image);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    profile_image.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openImage();
      }
    });

    return view;
  }

  private void openImage() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, IMAGE_REQUEST);
  }

  private String getFileExtension(Uri uri) {
    ContentResolver contentResolver = getContext().getContentResolver();
    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
  }

  private void uploadImage() {
    final ProgressDialog progressDialog = new ProgressDialog(getContext());
    progressDialog.setMessage("Uploading");
    progressDialog.show();
    if (imageUri != null) {
      final StorageReference fileReference = storageReference
          .child(System.currentTimeMillis() +"."+ getFileExtension(imageUri));
      uploadTask = fileReference.putFile(imageUri);
      uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
        @Override
        public Task<Uri> then(@NonNull Task<TaskSnapshot> task) throws Exception {
          if (!task.isSuccessful()) {
            throw task.getException();
          }
          return fileReference.getDownloadUrl();
        }
      }).addOnCompleteListener(new OnCompleteListener<Uri>() {
        @Override
        public void onComplete(@NonNull Task<Uri> task) {
          if (task.isSuccessful()) {
            Uri downloadUri = task.getResult();
            String mUri = downloadUri.toString();
            reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
            HashMap<String, Object> map = new HashMap<>();
            map.put("imageUrl", mUri);
            reference.updateChildren(map);
            progressDialog.dismiss();
          } else {
            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
          }
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.i("TAG ::", "onFailure: "+ e.getMessage());
          Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
          progressDialog.dismiss();
        }
      });
    } else {
      Toast.makeText(getContext(), "No image selected!", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null
        && data.getData() != null) {
      imageUri = data.getData();
      if(uploadTask != null && uploadTask.isInProgress()){
        Toast.makeText(getContext(), "Upload in progress.", Toast.LENGTH_SHORT).show();
      }else{
        uploadImage();
      }
    }
  }
}
