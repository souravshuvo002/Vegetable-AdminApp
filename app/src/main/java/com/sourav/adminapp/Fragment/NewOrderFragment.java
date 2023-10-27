package com.sourav.adminapp.Fragment;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sourav.adminapp.Activity.AllOrderHistoryActivity;
import com.sourav.adminapp.Adapter.AllOrderHistoryAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String USER_ID, DELIVERY_DATE = "", currentDate = "";
    private EditText edit_search;
    private LinearLayout layEmpty;
    private TextView textViewNo, textViewDate, textViewShowAllFoods;
    private RecyclerView newOrderRecyclerView;
    public AllOrderHistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Order> orderList, orderedFoodQuantity;
    private Date cDate;

    public NewOrderFragment() {
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
        return inflater.inflate(R.layout.layout_new_order_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        layEmpty = (LinearLayout) view.findViewById(R.id.empty_view);
        textViewNo = (TextView) view.findViewById(R.id.textViewNo);
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewShowAllFoods = (TextView) view.findViewById(R.id.textViewShowAllFoods);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark), getResources().getColor(android.R.color.holo_red_dark), getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_dark));

        edit_search = (EditText) view.findViewById(R.id.edit_search);
        newOrderRecyclerView = (RecyclerView) view.findViewById(R.id.newOrderRecyclerView);
        newOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newOrderRecyclerView.setHasFixedSize(true);
        newOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        newOrderRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadOrderStatusItems();
                    }
                }
        );

        cDate = new Date();
        DELIVERY_DATE = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        currentDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(cDate);
        textViewDate.setText(currentDate);

        loadOrderStatusItems();

        getActivity().getWindow().setSoftInputMode(
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

        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog();
            }
        });

        textViewShowAllFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

    }

    private void loadOrderStatusItems() {

        swipeRefreshLayout.setRefreshing(true);
        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getAllOrderForAdminByDelDate(DELIVERY_DATE);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.body().getError()) {
                    waitingDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No Order list", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    waitingDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    orderList = response.body().getOrderList();
                    if(orderList.size() > 1)
                    {
                        textViewNo.setText(orderList.size() + " orders found!");
                    }
                    else
                    {
                        textViewNo.setText(orderList.size() + " order found!");
                    }
                    adapter = new AllOrderHistoryAdapter(response.body().getOrderList(), getContext());
                    newOrderRecyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRefresh() {
        DELIVERY_DATE = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
        currentDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(cDate);
        textViewDate.setText(currentDate);
        loadOrderStatusItems();
    }

    private void datePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

                DELIVERY_DATE = year + "-" + month + "-" + day;
                //Common.sellDate = sell_date;
                DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                try {
                    date1 = inputFormatter1.parse(DELIVERY_DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat outputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                String output1 = outputFormatter1.format(date1);
                textViewDate.setText(output1);
                loadOrderStatusItems();
            }
        }, yy, mm, dd);
        //datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis() + 1000*60*60*24*DAY);
        datePicker.show();
    }

    private void showBottomSheetDialog() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(view);

        dialog.show();

        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayName);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getOrderedFoodQuantity(DELIVERY_DATE);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                orderedFoodQuantity = response.body().getOrderedFoodQuantity();
                swipeRefreshLayout.setRefreshing(false);
                waitingDialog.dismiss();

                if(orderedFoodQuantity.size() > 0)
                {
                    for (int i = 0; i<orderedFoodQuantity.size();i++)
                    {
                        TextView text = new TextView(getContext());
                        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        text.setText(i+1 + ". " + orderedFoodQuantity.get(i).getFood_name() + " - " + orderedFoodQuantity.get(i).getFood_quantity() + " (" + orderedFoodQuantity.get(i).getFood_min_unit_amount() + " " + orderedFoodQuantity.get(i).getFood_unit() + ")");
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                        text.setPadding(10,10,10,10);
                        text.setTextColor(ContextCompat.getColor(getContext(), R.color.toastMessageColor));
                        linearLayout.addView(text);
                    }
                }
                else
                {
                    TextView text = new TextView(getContext());
                    text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setText("No Food Items");
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                    text.setPadding(10,10,10,10);
                    text.setTextColor(ContextCompat.getColor(getContext(), R.color.toastMessageColor));
                    linearLayout.addView(text);
                }


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                waitingDialog.dismiss();
            }
        });
    }
}
