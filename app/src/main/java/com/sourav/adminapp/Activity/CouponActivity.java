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

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sourav.adminapp.Adapter.BannerAdapter;
import com.sourav.adminapp.Adapter.CouponsAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.Banner;
import com.sourav.adminapp.Model.Coupon;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.List;

public class CouponActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{


    FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView recyclerViewCoupon;
    List<Coupon> couponList;
    private CouponsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        // Change status bar color
        changeStatusBarColor("#008577");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Coupons");

        //getting views
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));

        recyclerViewCoupon = (RecyclerView) findViewById(R.id.recyclerViewCoupon);
        recyclerViewCoupon.setHasFixedSize(false);
        recyclerViewCoupon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCoupon.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CouponActivity.this, AddCouponActivity.class));
            }
        });

        loadListCoupons();
    }

    private void loadListCoupons() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(CouponActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getCoupons();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                couponList = response.body().getCouponList();

                Log.d("SIZE: ", String.valueOf(couponList.size()));

                if (couponList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new CouponsAdapter(couponList, CouponActivity.this);
                    recyclerViewCoupon.setAdapter(adapter);
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
        loadListCoupons();
    }
}
