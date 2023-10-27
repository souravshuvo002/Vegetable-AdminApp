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
import com.sourav.adminapp.Adapter.CouponsAdapter;
import com.sourav.adminapp.Adapter.UserAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.Coupon;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.User;
import com.sourav.adminapp.R;

import java.util.List;

public class AllUsersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView recyclerViewUsers;
    private List<User> userList;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        // Change status bar color
        changeStatusBarColor("#008577");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");

        //getting views
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));

        recyclerViewUsers = (RecyclerView) findViewById(R.id.recyclerViewUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        loadUsersData();
    }

    private void loadUsersData() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(AllUsersActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getAllUser();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                userList = response.body().getAllUserList();

                if (userList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new UserAdapter(userList, AllUsersActivity.this);
                    recyclerViewUsers.setAdapter(adapter);
                    waitingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
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

    @Override
    public void onRefresh() {
        loadUsersData();
    }
}
