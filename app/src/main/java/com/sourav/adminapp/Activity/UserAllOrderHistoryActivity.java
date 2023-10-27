package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.adminapp.Adapter.AllOrderHistoryAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserAllOrderHistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private EditText edit_search;
    private LinearLayout layEmpty;
    private TextView textViewNo, textViewDate;
    private RecyclerView allOrderRecyclerView;
    public AllOrderHistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_all_order_history);

        // Change status bar color
        changeStatusBarColor("#00574B");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Order History");

        // getting views
        layEmpty = (LinearLayout) findViewById(R.id.empty_view);
        textViewNo = (TextView) findViewById(R.id.textViewNo);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark), getResources().getColor(android.R.color.holo_red_dark), getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_dark));

        edit_search = (EditText) findViewById(R.id.edit_search);
        allOrderRecyclerView = (RecyclerView) findViewById(R.id.allOrderRecyclerView);
        allOrderRecyclerView.setLayoutManager(new LinearLayoutManager(UserAllOrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false));
        allOrderRecyclerView.setHasFixedSize(true);
        allOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        allOrderRecyclerView.addItemDecoration(new DividerItemDecoration(UserAllOrderHistoryActivity.this, LinearLayoutManager.VERTICAL));

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadOrderStatusItems();
                    }
                }
        );

        loadOrderStatusItems();

        UserAllOrderHistoryActivity.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
    }

    private void loadOrderStatusItems() {

        swipeRefreshLayout.setRefreshing(true);
        final android.app.AlertDialog waitingDialog = new SpotsDialog(UserAllOrderHistoryActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getAllOrder(Common.User_ID);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.body().getError()) {
                    waitingDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "No order list", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    waitingDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    adapter = new AllOrderHistoryAdapter(response.body().getOrderList(), getApplicationContext());
                    allOrderRecyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void filter(String text) {
        List<Order> temp = new ArrayList();
        for (Order d : orderList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getId_order().toLowerCase().contains(text.toLowerCase())
                    || d.getTotal_price().toLowerCase().contains(text.toLowerCase())
                    || d.getAddress().toLowerCase().contains(text.toLowerCase())
                    || d.getOrder_date().toLowerCase().contains(text.toLowerCase())
                    || Common.convertCodeToStatus(d.getOrder_status()).toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
        textViewNo.setText(temp.size() + " items found!");
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
    public void onRefresh() {
        loadOrderStatusItems();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
