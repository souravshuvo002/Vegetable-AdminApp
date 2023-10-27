package com.sourav.adminapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.sourav.adminapp.Adapter.AllOrderHistoryAdapter;
import com.sourav.adminapp.Adapter.AllWalletShippersAdapter;
import com.sourav.adminapp.Adapter.AreaAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Fragment.AllOrderFragment;
import com.sourav.adminapp.Fragment.NewOrderFragment;
import com.sourav.adminapp.Fragment.PendingWalletFragment;
import com.sourav.adminapp.Fragment.TransferredWalletFragment;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.User;
import com.sourav.adminapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class WalletActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerViewShippers;
    private TextView textViewTotal, textViewReceived, txtMonth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Order> totalWalletAmountList, totalReceivedAmountList;
    private Date cDate;
    private String currentDate, REC_DATE, DATE, TOTAL, RECEIVED;
    private List<User> allShippersList;
    private AllWalletShippersAdapter adapter;

    private TabLayout tabLayout;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        // Change status bar color
        changeStatusBarColor("#00574B");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Balance transfer");

        // getting views
        textViewTotal = (TextView) findViewById(R.id.textViewTotal);
        textViewReceived = (TextView) findViewById(R.id.textViewReceived);
        txtMonth = (TextView) findViewById(R.id.txtMonth);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        cDate = new Date();
        DATE = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        currentDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(cDate);
        Common.DATE = DATE;
        txtMonth.setText(currentDate);

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadTotalWalletAmount();
                        loadReceivedAmount();
                    }
                }
        );

        txtMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog();
            }
        });

        loadTotalWalletAmount();
        loadReceivedAmount();
    }

    private void datePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(WalletActivity.this, new DatePickerDialog.OnDateSetListener() {
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

                DATE = year + "-" + month + "-" + day;
                Common.DATE = DATE;
                //Common.sellDate = sell_date;
                DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                try {
                    date1 = inputFormatter1.parse(DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat outputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                String output1 = outputFormatter1.format(date1);
                txtMonth.setText(output1);
                loadReceivedAmount();
                loadTotalWalletAmount();
                viewPager.setCurrentItem(0);
            }
        }, yy, mm, dd);
        datePicker.show();
    }

    private void loadReceivedAmount() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(WalletActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getRecTotalWalletAmount(DATE);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                totalReceivedAmountList = response.body().getOrderList();

                if (totalReceivedAmountList.size() <= 0) {
                    textViewReceived.setText(WalletActivity.this.getResources().getString(R.string.currency_sign) + "0.00 Tk");
                    waitingDialog.dismiss();
                } else {
                    // Calculate Total Price
                    double total = 0.0;
                    for (Order order : totalReceivedAmountList) {
                        total += Double.parseDouble(order.getAmount());
                    }
                    RECEIVED = String.valueOf(total);
                    textViewReceived.setText(WalletActivity.this.getResources().getString(R.string.currency_sign) + String.format("%.2f Tk", total));
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

    private void loadTotalWalletAmount() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(WalletActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getTotalWalletAmount(DATE, "5");

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                totalWalletAmountList = response.body().getOrderList();

                if (totalWalletAmountList.size() <= 0) {
                    textViewTotal.setText(WalletActivity.this.getResources().getString(R.string.currency_sign) + "0.00 Tk");
                    textViewReceived.setText(WalletActivity.this.getResources().getString(R.string.currency_sign) + "0.00 Tk");
                    waitingDialog.dismiss();
                } else {
                    // Calculate Total Price
                    double total = 0.0;
                    for (Order order : totalWalletAmountList) {
                        total += Double.parseDouble(order.getTotal_price());
                    }
                    TOTAL = String.valueOf(total);
                    textViewTotal.setText(WalletActivity.this.getResources().getString(R.string.currency_sign) + String.format("%.2f Tk", total));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_history)
            startActivity(new Intent(WalletActivity.this, WalletTransferHistoryActivity.class));

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        final WalletActivity.ViewPagerAdapter adapter = new WalletActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PendingWalletFragment(), "Pending");
        adapter.addFragment(new TransferredWalletFragment(), "Transferred");
        //adapter.addFragment(new CompletedFragment(), "Completed");
        viewPager.setAdapter(adapter);

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int CurrentPosition = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                CurrentPosition = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE && CurrentPosition != 0){
                    //Toast.makeText(getBaseContext(),"finished" , Toast.LENGTH_SHORT).show();
                    try{
                        new TransferredWalletFragment().loadShippers();;
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });*/
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
        /*DATE = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        currentDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(cDate);
        txtMonth.setText(currentDate);*/
        viewPager.setCurrentItem(0);
        loadTotalWalletAmount();
        loadReceivedAmount();
        //loadShippers();
    }
}
