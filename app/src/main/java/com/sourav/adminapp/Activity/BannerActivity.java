package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import info.hoang8f.widget.FButton;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sourav.adminapp.Adapter.BannerAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Helper.CheckPermission;
import com.sourav.adminapp.Model.Banner;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BannerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView recyclerViewBanner;
    List<Banner> bannerList;
    private BannerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);

        // Change status bar color
        changeStatusBarColor("#008577");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Banners");

        //getting views
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));

        recyclerViewBanner = (RecyclerView) findViewById(R.id.recyclerViewBanner);
        recyclerViewBanner.setHasFixedSize(false);
        recyclerViewBanner.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewBanner.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BannerActivity.this, AddBannerActivity.class));
            }
        });

        loadListBanner();
    }

    private void loadListBanner() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(BannerActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getBanners();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                bannerList = response.body().getBanners();

                Log.d("SIZE: ", String.valueOf(bannerList.size()));

                if (bannerList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new BannerAdapter(bannerList, BannerActivity.this);
                    recyclerViewBanner.setAdapter(adapter);
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
        loadListBanner();
    }
}
