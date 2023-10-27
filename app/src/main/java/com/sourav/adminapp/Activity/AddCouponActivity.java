package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddCouponActivity extends AppCompatActivity {

    private Button buttonUpload;
    private TextInputEditText editTextName, editTextCode,
            editTextDiscount, editTextDiscountLimit, editTextTotal,
            editTextUsesTotal, editTextUsesCustomer;
    private TextView textViewStartDate, textViewEndDate;
    private Spinner spinner_type;
    private String COUPON_TYPE, START_DATE, END_DATE, DIS_LIMIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coupon);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add new Coupon");

        // getting views
        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editTextCode = (TextInputEditText) findViewById(R.id.editTextCode);
        editTextDiscount = (TextInputEditText) findViewById(R.id.editTextDiscount);
        editTextDiscountLimit = (TextInputEditText) findViewById(R.id.editTextDiscountLimit);
        editTextTotal = (TextInputEditText) findViewById(R.id.editTextTotal);
        editTextUsesTotal = (TextInputEditText) findViewById(R.id.editTextUsesTotal);
        editTextUsesCustomer = (TextInputEditText) findViewById(R.id.editTextUsesCustomer);
        textViewStartDate = (TextView) findViewById(R.id.textViewStartDate);
        textViewEndDate = (TextView) findViewById(R.id.textViewEndDate);
        spinner_type = (Spinner) findViewById(R.id.spinner_type);
        buttonUpload = (Button) findViewById(R.id.btnUpload);

        textViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDatePickerDialog();
            }
        });

        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endDatePickerDialog();
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextName.getText().toString()) ||
                        TextUtils.isEmpty(editTextCode.getText().toString()) ||
                        TextUtils.isEmpty(editTextDiscount.getText().toString()) ||
                        TextUtils.isEmpty(editTextTotal.getText().toString())||
                        TextUtils.isEmpty(editTextUsesTotal.getText().toString())||
                        TextUtils.isEmpty(editTextUsesCustomer.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty", Toast.LENGTH_LONG).show();
                } else {
                    uploadData();
                }
            }
        });
    }

    private void uploadData() {

        COUPON_TYPE = spinner_type.getSelectedItem().toString();

        if(COUPON_TYPE.equalsIgnoreCase("Percentage"))
        {
            COUPON_TYPE = "P";
        }
        else
        {
            COUPON_TYPE = "F";
        }

        if(editTextDiscountLimit.getText().toString().isEmpty())
        {
            DIS_LIMIT = "0.00";
        }
        else
        {
            DIS_LIMIT = editTextDiscountLimit.getText().toString();
        }

        final android.app.AlertDialog waitingDialog = new SpotsDialog(AddCouponActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");


        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.addCoupon(
                editTextName.getText().toString(),
                editTextCode.getText().toString(),
                COUPON_TYPE,
                editTextDiscount.getText().toString(),
                editTextDiscountLimit.getText().toString(),
                editTextTotal.getText().toString(),
                START_DATE,
                END_DATE,
                editTextUsesTotal.getText().toString(),
                editTextUsesCustomer.getText().toString(),
                Common.getDateTime());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if (!response.body().getError()) {
                    editTextName.setText("");
                    editTextCode.setText("");
                    editTextDiscount.setText("");
                    editTextDiscountLimit.setText("");
                    editTextTotal.setText("");
                    editTextUsesTotal.setText("");
                    editTextUsesCustomer.setText("");
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

    private void startDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(AddCouponActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date, day = null, month = null;

                if (dayOfMonth < 10) {
                    day = "0" + String.valueOf(dayOfMonth);

                } else {
                    day = String.valueOf(dayOfMonth);
                }
                if (monthOfYear + 1 < 10) {
                    month = "0" + String.valueOf(monthOfYear + 1);
                } else {
                    month = String.valueOf(monthOfYear + 1);
                }

                START_DATE = year + "-" + month + "-" + day;
                //Common.sellDate = sell_date;
                DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                try {
                    date1 = inputFormatter1.parse(START_DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat outputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                String output1 = outputFormatter1.format(date1);
                textViewStartDate.setText(output1);
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    private void endDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(AddCouponActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date, day = null, month = null;

                if (dayOfMonth < 10) {
                    day = "0" + String.valueOf(dayOfMonth);

                } else {
                    day = String.valueOf(dayOfMonth);
                }
                if (monthOfYear + 1 < 10) {
                    month = "0" + String.valueOf(monthOfYear + 1);
                } else {
                    month = String.valueOf(monthOfYear + 1);
                }

                END_DATE = year + "-" + month + "-" + day;
                //Common.sellDate = sell_date;
                DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                try {
                    date1 = inputFormatter1.parse(END_DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat outputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                String output1 = outputFormatter1.format(date1);
                textViewEndDate.setText(output1);
            }
        }, yy, mm, dd);
        datePicker.show();
    }


}
