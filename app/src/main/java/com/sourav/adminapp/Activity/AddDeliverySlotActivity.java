package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import dmax.dialog.SpotsDialog;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

public class AddDeliverySlotActivity extends AppCompatActivity {

    private Button buttonUpload;
    private Spinner spinner_day, spinner_start_time, spinner_end_time;
    private TextInputEditText editTextAllocation;
    private String SPINNER_DAY, SPINNER_START_TIME, SPINNER_END_TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_slot);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add new Coupon");

        // getting views
        editTextAllocation = (TextInputEditText) findViewById(R.id.editTextAllocation);
        spinner_day = (Spinner) findViewById(R.id.spinner_day);
        spinner_start_time = (Spinner) findViewById(R.id.spinner_start_time);
        spinner_end_time = (Spinner) findViewById(R.id.spinner_end_time);
        buttonUpload = (Button) findViewById(R.id.btnUpload);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextAllocation.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty", Toast.LENGTH_LONG).show();
                } else {
                    uploadData();
                }
            }
        });
    }

    private void uploadData() {

        SPINNER_DAY = spinner_day.getSelectedItem().toString();
        SPINNER_START_TIME = spinner_start_time.getSelectedItem().toString();
        SPINNER_END_TIME = spinner_end_time.getSelectedItem().toString();


        final android.app.AlertDialog waitingDialog = new SpotsDialog(AddDeliverySlotActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");


        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.addDeliverySlot(
                SPINNER_DAY,
                SPINNER_START_TIME,
                SPINNER_END_TIME,
                editTextAllocation.getText().toString());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if (!response.body().getError()) {
                    editTextAllocation.setText("");
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
