package com.project.diyetikserver.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.project.diyetikserver.Common.Common;
import com.project.diyetikserver.Model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            updateTokenSever(refreshedToken);
    }

    private void updateTokenSever(String refreshedToken) {
        if (Common.currentUser != null){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(refreshedToken, true);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }}
}
