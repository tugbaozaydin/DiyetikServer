package com.project.diyetikserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.diyetikserver.Common.Common;
import com.project.diyetikserver.Model.User;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSigin;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edtPasswordServer);
        edtPhone = findViewById(R.id.edtPhoneServer);
        btnSigin = findViewById(R.id.btnSignIn);

        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnSigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sıgnInUser(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void sıgnInUser(String phone, String password) {

        final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
        mDialog.setMessage("Lütfen bekleyiniz...");
        mDialog.show();

        final String localPhone = phone;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                    mDialog.dismiss();
                    User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                    user.setPhone(edtPhone.getText().toString());
                   // if (Boolean.parseBoolean(user.getIsStaff())) { //IsStaff == true

                        if (user.getPassword().equals(localPassword)) {
                            //login ok
                            Intent login = new Intent(SignIn.this, Home.class);
                            //Common.currentUser = user;
                            startActivity(login);
                            finish();
                        } else
                            Toast.makeText(SignIn.this, "Yanlış Parola", Toast.LENGTH_SHORT).show();

                  /* } else
                       Toast.makeText(SignIn.this, "Lütfen admin  oturumu açın", Toast.LENGTH_SHORT).show();
*/
                } else {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "Böyle bir kullanıcı yoktur.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
