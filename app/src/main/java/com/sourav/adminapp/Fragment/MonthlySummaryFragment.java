package com.sourav.adminapp.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.sourav.adminapp.Activity.SummaryActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonthlySummaryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextView textViewRevenue, textViewRevenueMonth, textViewCompletedOrder, textViewCancelOrder;
    private TextView textViewDate;
    private String YEAR, MONTH;
    private Date cDate;
    private SwipeRefreshLayout swipeRefreshLayout;

    int yearSelected;
    int monthSelected;
    String month;

    public MonthlySummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_monthly_summary_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // getting views
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewRevenue = (TextView) view.findViewById(R.id.textViewRevenue);
        textViewRevenueMonth = (TextView) view.findViewById(R.id.textViewRevenueMonth);
        textViewCompletedOrder = (TextView) view.findViewById(R.id.textViewCompletedOrder);
        textViewCancelOrder = (TextView) view.findViewById(R.id.textViewCancelOrder);

        //Set default values
        Calendar calendar = Calendar.getInstance();
        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH);
        if (monthSelected + 1 < 10) {
            month = "0" + String.valueOf(monthSelected + 1);
        } else {
            month = String.valueOf(monthSelected + 1);
        }
        String monthName = getMonthName(month);
        textViewDate.setText(monthName + " - " + yearSelected);

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMonthYearDialog();
            }
        });

        loadSummary();
    }

    private void loadSummary() {
        swipeRefreshLayout.setRefreshing(false);

        Log.d("Year: ", String.valueOf(yearSelected));
        Log.d("Month: ", month);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getSummary(String.valueOf(yearSelected), month);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                textViewRevenue.setText(getContext().getResources().getString(R.string.currency_sign) + response.body().getSaleSummary().getTotal_sale_amount());
                textViewCompletedOrder.setText(response.body().getSaleSummary().getTotal_month_completed_order());
                textViewCancelOrder.setText(response.body().getSaleSummary().getTotal_month_cancelled_order());
                textViewRevenueMonth.setText(getContext().getResources().getString(R.string.currency_sign) + response.body().getSaleSummary().getTotal_month_sale_amount());

                waitingDialog.dismiss();

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });
    }

    private void showMonthYearDialog() {
        //Set default values
        Calendar calendar = Calendar.getInstance();
        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH);

        MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                .getInstance(monthSelected, yearSelected);

        dialogFragment.show(getFragmentManager(), null);

        dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                if (monthOfYear + 1 < 10) {
                    month = "0" + String.valueOf(monthOfYear + 1);
                } else {
                    month = String.valueOf(monthOfYear + 1);
                }
                String monthName = getMonthName(month);
                textViewDate.setText(monthName + " - " + year);
                loadSummary();
            }
        });
    }

    private String getMonthName(String month) {
        if (month.equalsIgnoreCase("01")) {
            return "January";
        } else if (month.equalsIgnoreCase("02")) {
            return "February";
        } else if (month.equalsIgnoreCase("03")) {
            return "March";
        } else if (month.equalsIgnoreCase("04")) {
            return "April";
        } else if (month.equalsIgnoreCase("05")) {
            return "May";
        } else if (month.equalsIgnoreCase("06")) {
            return "Jun";
        } else if (month.equalsIgnoreCase("07")) {
            return "July";
        } else if (month.equalsIgnoreCase("08")) {
            return "August";
        } else if (month.equalsIgnoreCase("09")) {
            return "September";
        } else if (month.equalsIgnoreCase("10")) {
            return "October";
        } else if (month.equalsIgnoreCase("11")) {
            return "November";
        } else {
            return "December";
        }
    }


    @Override
    public void onRefresh() {
        //Set default values
        Calendar calendar = Calendar.getInstance();
        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH);
        if (monthSelected + 1 < 10) {
            month = "0" + String.valueOf(monthSelected + 1);
        } else {
            month = String.valueOf(monthSelected + 1);
        }
        String monthName = getMonthName(month);
        textViewDate.setText(monthName + " - " + yearSelected);
        loadSummary();
    }

}