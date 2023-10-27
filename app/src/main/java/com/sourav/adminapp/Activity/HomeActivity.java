package com.sourav.adminapp.Activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.sourav.adminapp.Adapter.MenuAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Category;
import com.sourav.adminapp.Model.DeliverySlot;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.Unit;
import com.sourav.adminapp.R;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView txtFullName;
    FloatingActionButton fab;
    private RecyclerView recycler_menu;
    List<Category> menuList;
    private MenuAdapter adapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Change status bar color
        changeStatusBarColor("#008577");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        String USERNAME = sharedPreferences.getString("USERNAME", null);
        txtFullName.setText(USERNAME);

        // getting views
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(false);
        recycler_menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_menu.setItemAnimator(new DefaultItemAnimator());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddMenuActivity.class));
            }
        });

        loadTextScroll();
        loadListMenu();
    }

    private void loadTextScroll() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getTextScroll();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Common.text_scroll = response.body().getTextScroll();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadListMenu() {
        //swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getMenu();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                menuList = response.body().getMenuList();

                Log.d("SIZE: ", String.valueOf(menuList.size()));

                if (menuList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new MenuAdapter(menuList, HomeActivity.this);
                    recycler_menu.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_text_scroll) {
            showTextScrollDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showTextScrollDialog() {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("CHANGE SCROLL TEXT");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Please fill below information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.text_scroll_layout, null);

        final EditText edtTextScroll = (EditText) layout_home.findViewById(R.id.edtTextScroll);
        final EditText edtTextStatus = (EditText) layout_home.findViewById(R.id.edtTextStatus);
        edtTextScroll.setText(Common.text_scroll.getText());
        edtTextStatus.setText(Common.text_scroll.getStatus());
        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Common.text_scroll.setText(edtTextScroll.getText().toString());
                Common.text_scroll.setStatus(edtTextStatus.getText().toString());
                //Defining retrofit api service
                ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                Call<Result> call = service.updateTextScroll("1", edtTextScroll.getText().toString(),
                        edtTextStatus.getText().toString());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Intent orders = new Intent(HomeActivity.this, AllOrderHistoryActivity.class);
            startActivity(orders);
        } else if (id == R.id.nav_wallet) {
            Intent banner = new Intent(HomeActivity.this, WalletActivity.class);
            startActivity(banner);
        } else if (id == R.id.nav_summary) {
            Intent banner = new Intent(HomeActivity.this, SummaryActivity.class);
            startActivity(banner);
        } else if (id == R.id.nav_banner) {
            Intent banner = new Intent(HomeActivity.this, BannerActivity.class);
            startActivity(banner);
        } else if (id == R.id.nav_coupon) {
            Intent unit = new Intent(HomeActivity.this, CouponActivity.class);
            startActivity(unit);
        } else if (id == R.id.nav_add_unit) {
            Intent unit = new Intent(HomeActivity.this, UnitActivity.class);
            startActivity(unit);
        } else if (id == R.id.nav_add_slot) {
            Intent unit = new Intent(HomeActivity.this, SlotActivity.class);
            startActivity(unit);
        } else if (id == R.id.nav_reviews) {
            Intent unit = new Intent(HomeActivity.this, ShowAllReviewsActivity.class);
            startActivity(unit);
        } else if (id == R.id.nav_notification) {
            Intent unit = new Intent(HomeActivity.this, SendNotificationActivity.class);
            startActivity(unit);
        } else if (id == R.id.nav_add_area) {
            Intent area = new Intent(HomeActivity.this, AreaActivity.class);
            startActivity(area);
        } else if (id == R.id.nav_user) {
            Intent shippers = new Intent(HomeActivity.this, AllUsersActivity.class);
            startActivity(shippers);
        } else if (id == R.id.nav_shipper) {
            Intent shippers = new Intent(HomeActivity.this, ShipperManagementActivity.class);
            startActivity(shippers);
        } else if (id == R.id.nav_sign_out) {
            LogOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void LogOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Log out Application");
        alertDialog.setMessage("Do you really want to log out from app?");
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Clearing Data from Shared Preferences
                editor.clear();
                editor.commit();

                /*//Delete Remember user & password
                FirebaseAuth.getInstance().signOut();*/

                // Clear All Activity
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#FF8A65"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTextScroll();
        loadListMenu();
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
    protected void onStart() {
        super.onStart();
        loadTextScroll();
        loadListMenu();
    }
}
