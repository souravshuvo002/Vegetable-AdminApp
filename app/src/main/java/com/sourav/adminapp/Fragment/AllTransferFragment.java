package com.sourav.adminapp.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.adminapp.Adapter.CompletedOrderWalletAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.User;
import com.sourav.adminapp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
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

public class AllTransferFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String USER_ID, currentDate, DATE, PENDING, TRANSFERRED, VERIFIED;
    private EditText edit_search;
    private LinearLayout layEmpty;
    private Spinner spinnerShipper;
    private List<User> shipperList;
    private String shipperName, shipperID;
    private TextView textViewNo, textViewDate, textViewPending, textViewTransferred, textViewVerified;
    private RecyclerView recyclerView;
    public CompletedOrderWalletAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Order> foodsList, totalReceivedAmountList;
    private Date cDate;

    public AllTransferFragment() {
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
        return inflater.inflate(R.layout.layout_daily_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        layEmpty = (LinearLayout) view.findViewById(R.id.empty_view);
        spinnerShipper = (Spinner) view.findViewById(R.id.spinner_shipper);
        textViewNo = (TextView) view.findViewById(R.id.textViewNo);
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewPending = (TextView) view.findViewById(R.id.textViewPending);
        textViewTransferred = (TextView) view.findViewById(R.id.textViewTransferred);
        textViewVerified = (TextView) view.findViewById(R.id.textViewVerified);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark), getResources().getColor(android.R.color.holo_red_dark), getResources().getColor(android.R.color.holo_green_light), getResources().getColor(android.R.color.holo_orange_dark));

        edit_search = (EditText) view.findViewById(R.id.edit_search);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        textViewDate.setVisibility(View.GONE);

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadShipper();

                    }
                }
        );

        loadShipper();

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
    }

    private void loadShipper() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getShippers();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                shipperList = response.body().getAllShippers();
                final List<String> shipperNameID = new ArrayList<>();
                final Map<String, String> shipperMap = new HashMap<String, String>();

                for (int i = 0; i < response.body().getAllShippers().size(); i++) {
                    shipperMap.put(response.body().getAllShippers().get(i).getUsername(), response.body().getAllShippers().get(i).getId());
                    shipperNameID.add(response.body().getAllShippers().get(i).getUsername());
                }

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, shipperNameID);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinnerShipper.setAdapter(dataAdapter);

                spinnerShipper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String string = spinnerShipper.getSelectedItem().toString();
                        shipperName = string;
                        shipperID = shipperMap.get(string);
                        loadCompletedOrder();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void loadCompletedOrder() {
        swipeRefreshLayout.setRefreshing(true);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getAllCompletedOrderForShipper(shipperID);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.body().getError()) {
                    waitingDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "No Order list", Toast.LENGTH_SHORT).show();
                } else {
                    foodsList = response.body().getOrderList();
                    if (foodsList.size() > 1) {
                        textViewNo.setText(foodsList.size() + " orders found!");
                    } else {
                        textViewNo.setText(foodsList.size() + " order found!");
                    }
                    waitingDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    adapter = new CompletedOrderWalletAdapter(response.body().getOrderList(), getContext());
                    recyclerView.setAdapter(adapter);
                    calculateAmount();
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
        for (Order d : foodsList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getId_order().toLowerCase().contains(text.toLowerCase())
                    || d.getTotal_price().toLowerCase().contains(text.toLowerCase())
                    || d.getAddress().toLowerCase().contains(text.toLowerCase())
                    || d.getOrder_date().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
    }

    @Override
    public void onRefresh() {
        loadShipper();
    }

    private void calculateAmount() {
        // Calculate Total Price
        double pending = 0.0, transferred = 0.0, verified = 0.0;
        for (Order foods : foodsList) {
            // 0 - Pending, 1 - Transfer, 2 - Verified.
            if (foods.getTransfer_state().equalsIgnoreCase(String.valueOf("0"))) {
                pending += Double.parseDouble(foods.getTotal_price());
            } else if (foods.getTransfer_state().equalsIgnoreCase(String.valueOf("1"))) {
                transferred += Double.parseDouble(foods.getTotal_price());
            } else if (foods.getTransfer_state().equalsIgnoreCase(String.valueOf("2"))) {
                verified += Double.parseDouble(foods.getTotal_price());
            }
        }

        PENDING = String.valueOf(pending);
        TRANSFERRED = String.valueOf(transferred);
        VERIFIED = String.valueOf(verified);

        textViewPending.setText(this.getResources().getString(R.string.currency_sign) + String.format("%.2f", pending));
        textViewTransferred.setText(this.getResources().getString(R.string.currency_sign) + String.format("%.2f", transferred));
        textViewVerified.setText(this.getResources().getString(R.string.currency_sign) + String.format("%.2f", verified));
    }
}