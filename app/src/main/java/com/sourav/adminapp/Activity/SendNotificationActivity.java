package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sourav.adminapp.Api.IFCMService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.DataMessage;
import com.sourav.adminapp.Model.MyResponse;
import com.sourav.adminapp.R;

import java.util.HashMap;
import java.util.Map;

public class SendNotificationActivity extends AppCompatActivity {

    private RadioGroup radioTargetUser;
    private RadioButton radioPatient, radioDoctor, radioPharmacy, radioAll;
    private EditText editTextTitle, editTextBody;
    private Button buttonSend;
    private String targetUser = "";
    IFCMService mFcmService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        // Change status bar color
        changeStatusBarColor("#008577");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Send Notification");
        //getting views
        radioTargetUser = (RadioGroup) findViewById(R.id.radioTargetUser);
        radioPatient = (RadioButton) findViewById(R.id.radioPatient);
        radioDoctor = (RadioButton) findViewById(R.id.radioDoctor);
        //radioPharmacy = (RadioButton) findViewById(R.id.radioPharmacy);
        radioAll = (RadioButton) findViewById(R.id.radioAll);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextBody = (EditText) findViewById(R.id.editTextBody);
        buttonSend = (Button) findViewById(R.id.btn_send);

        mFcmService = Common.getFCMService();

        radioTargetUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioPatient:
                        targetUser = "customer";
                        break;
                    case R.id.radioDoctor:
                        targetUser = "shipper";
                        break;
                    case R.id.radioAll:
                        targetUser = "all";
                        break;
                }
            }
        });


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTextTitle.getText().toString()) || TextUtils.isEmpty(editTextBody.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Field can't be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (targetUser.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please select a target user type.", Toast.LENGTH_LONG).show();
                    return;
                }
                SendNotification(editTextTitle.getText().toString(), editTextBody.getText().toString());
            }
        });
    }

    private void SendNotification(String title, String body) {
        String Str_to = "/topics/" + targetUser;
        Map<String, String> content = new HashMap<>();
        content.put("title", title);
        content.put("message", body);
        content.put("type", "Notification");
        //content.put("image-url", "http://h5.4j.com/thumb/Ninja-Run.jpg");
        DataMessage dataMessage = new DataMessage(Str_to, content);

        mFcmService.sendNotification(dataMessage).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                if (response.body().success != 1) {
                    Toast.makeText(SendNotificationActivity.this, "Successfully Send", Toast.LENGTH_SHORT).show();
                    editTextTitle.setText(null);
                    editTextBody.setText(null);
                } else {
                    //Toast.makeText(SendNotificationActivity.this, response.body().success, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
            }
        });
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
