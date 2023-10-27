package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sourav.adminapp.Adapter.DeliverySlotAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.DeliverySlot;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.List;

public class SlotActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView recyclerView;
    List<DeliverySlot> deliverySlotList;
    private DeliverySlotAdapter adapter;
    private MaterialEditText edtMaxDay, edtOrderTime, edtDelCharge, edtDelFreeAmount;
    private Spinner spinner_start_time, spinner_end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);

        // Change status bar color
        changeStatusBarColor("#008577");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Delivery Slots");

        //getting views
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddSlotDialog();
            }
        });

        loadSlots();
    }

    private void showAddSlotDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SlotActivity.this);
        alertDialog.setTitle("Add new Slot");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_slot_layout = inflater.inflate(R.layout.add_new_slot_layout, null);


        edtMaxDay = add_slot_layout.findViewById(R.id.edtMaxDay);
        edtOrderTime = add_slot_layout.findViewById(R.id.edtOrderTime);
        edtDelCharge = add_slot_layout.findViewById(R.id.edtDelCharge);
        edtDelFreeAmount = add_slot_layout.findViewById(R.id.edtDelFreeAmount);
        spinner_start_time = (Spinner) add_slot_layout.findViewById(R.id.spinner_start_time);
        spinner_end_time = (Spinner) add_slot_layout.findViewById(R.id.spinner_end_time);

        alertDialog.setView(add_slot_layout);
        //alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                final android.app.AlertDialog waitingDialog = new SpotsDialog(SlotActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");


                //Defining retrofit api service
                ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                Call<Result> call = service.addSlot(
                        edtMaxDay.getText().toString(),
                        spinner_start_time.getSelectedItem().toString(),
                        spinner_end_time.getSelectedItem().toString(),
                        edtOrderTime.getText().toString(),
                        edtDelCharge.getText().toString(),
                        edtDelFreeAmount.getText().toString());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        loadSlots();
                    }
                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadSlots() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(SlotActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getSlots();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                deliverySlotList = response.body().getSlotList();

                if (deliverySlotList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new DeliverySlotAdapter(deliverySlotList, SlotActivity.this);
                    recyclerView.setAdapter(adapter);
                    fab.setVisibility(View.GONE);
                    waitingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });

        //adapter = new DiagnosticTestAdapter(initDiagnosticTest(), DiagnosticTestActivity.this);
        //diagnosticRecyclerView.setAdapter(adapter);
        //recycler_view_cat.addItemDecoration(new SpacesItemDecoration(10));
        //ViewCompat.setNestedScrollingEnabled(diagnosticRecyclerView, false);
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

    @Override
    public void onRefresh() {
        loadSlots();
    }
}
