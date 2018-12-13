package com.me.priya.mychatapp.notifications;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by Priya Jain on 12,December,2018
 */
public class MyFirebaseIdService extends FirebaseInstanceIdService {

  @Override
  public void onTokenRefresh() {
    super.onTokenRefresh();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    String refreshToken = FirebaseInstanceId.getInstance().getToken();
    if (firebaseUser != null) {
      updateToken(refreshToken);
    }
  }

  private void updateToken(String refreshToken) {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
    Token token = new Token(refreshToken);
    reference.child(firebaseUser.getUid()).setValue(token);
  }
}
