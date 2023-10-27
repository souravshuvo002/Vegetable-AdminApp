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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sourav.adminapp.Adapter.AreaAdapter;
import com.sourav.adminapp.Adapter.UnitAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.Area;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.Unit;
import com.sourav.adminapp.R;

import java.util.List;

public class UnitActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout empty_view;
    private RecyclerView recyclerViewUnit;
    List<Unit> unitList;
    private UnitAdapter adapter;
    MaterialEditText edtUnitType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        // Change status bar color
        changeStatusBarColor("#008577");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Unit List");

        //getting views
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));

        recyclerViewUnit = (RecyclerView) findViewById(R.id.recyclerViewUnit);
        recyclerViewUnit.setHasFixedSize(false);
        recyclerViewUnit.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewUnit.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAreaDialog();
            }
        });

        loadListUnit();
    }

    public void loadListUnit() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(UnitActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getUnit();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                unitList = response.body().getUnitList();

                Log.d("SIZE: ", String.valueOf(unitList.size()));

                if (unitList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new UnitAdapter(unitList, UnitActivity.this);
                    recyclerViewUnit.setAdapter(adapter);
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

    private void showAddAreaDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UnitActivity.this);
        alertDialog.setTitle("Add new Area");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_area_layout = inflater.inflate(R.layout.add_new_unit_layout, null);

        edtUnitType = add_area_layout.findViewById(R.id.edtUnitType);


        alertDialog.setView(add_area_layout);
        //alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                final android.app.AlertDialog waitingDialog = new SpotsDialog(UnitActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");


                //Defining retrofit api service
                ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                Call<Result> call = service.addUnit(edtUnitType.getText().toString());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        loadListUnit();
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
        loadListUnit();
    }
}