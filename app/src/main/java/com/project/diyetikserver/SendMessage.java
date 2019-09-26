package com.project.diyetikserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.project.diyetikserver.Common.Common;
import com.project.diyetikserver.Model.MyResponse;
import com.project.diyetikserver.Model.Notification;
import com.project.diyetikserver.Model.Sender;
import com.project.diyetikserver.Remote.APIService;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {

    MaterialEditText edtMessage,edtTitle;
    FButton btnSend;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        edtMessage=(MaterialEditText)findViewById(R.id.edtMessage);
        edtTitle=(MaterialEditText)findViewById(R.id.edtTitle);

        btnSend=(FButton)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification notification=new Notification(edtTitle.getText().toString(),edtMessage.getText().toString());

                Sender toTopic=new Sender();
                toTopic.to=new StringBuilder("/topics/").append(Common.topicName).toString();
                toTopic.notification= notification;

                mService.sendNotification(toTopic).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if(response.isSuccessful())
                            Toast.makeText(SendMessage.this,"Mesajı Gönder",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                        Toast.makeText(SendMessage.this,""+t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
