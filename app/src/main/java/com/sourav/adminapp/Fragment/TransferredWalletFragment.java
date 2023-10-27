package com.sourav.adminapp.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.adminapp.Activity.WalletActivity;
import com.sourav.adminapp.Adapter.AllOrderHistoryAdapter;
import com.sourav.adminapp.Adapter.AllWalletShippersAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Common.Common;
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
import java.util.HashMap;
import java.util.HashSet;
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
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferredWalletFragment extends Fragment {

    private String USER_ID, DELIVERY_DATE = "", currentDate = "";
    private String shipperName, shipperID;
    private LinearLayout layoutRider;
    private Button btn_verify;
    private Spinner spinner_rider;
    private EditText edit_search;
    private LinearLayout layEmpty;
    private TextView textViewNo, textViewLongPress;
    private RecyclerView walletRecyclerView;
    private List<User> allTransferList;
    private AllWalletShippersAdapter adapter;

    public TransferredWalletFragment() {
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
        return inflater.inflate(R.layout.layout_wallet_pending_transferred_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        layoutRider = (LinearLayout) view.findViewById(R.id.layoutRider);
        layEmpty = (LinearLayout) view.findViewById(R.id.empty_view);
        btn_verify = (Button) view.findViewById(R.id.btn_verify);
        textViewNo = (TextView) view.findViewById(R.id.textViewNo);
        textViewLongPress = (TextView) view.findViewById(R.id.textViewLongPress);
        edit_search = (EditText) view.findViewById(R.id.edit_search);
        spinner_rider = (Spinner) view.findViewById(R.id.spinner_rider);
        walletRecyclerView = (RecyclerView) view.findViewById(R.id.walletRecyclerView);
        walletRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        walletRecyclerView.setHasFixedSize(true);
        walletRecyclerView.setItemAnimator(new DefaultItemAnimator());
        walletRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        textViewLongPress.setVisibility(View.GONE);
        layoutRider.setVisibility(View.VISIBLE);
        btn_verify.setVisibility(View.VISIBLE);

        if (!isViewShown) {
            loadTransferredData();
        }

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

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Update Transfer status");
                alertDialog.setMessage("Press Accept to verify the transfer of " + spinner_rider.getSelectedItem().toString());
                alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        verifyWalletData();
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
                Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (b != null) {
                    b.setTextColor(Color.parseColor("#000000"));
                }
                Button b2 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (b2 != null) {
                    b2.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
    }

    private void verifyWalletData() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        // Delaying action for 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < allTransferList.size(); i++) {

                    if(allTransferList.get(i).getId_shipper().equalsIgnoreCase(shipperID))
                    {
                        //Defining retrofit api service
                        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                        Call<Result> call = service.updateWalletTransfer(
                                allTransferList.get(i).getId(),
                                allTransferList.get(i).getId_order(),
                                allTransferList.get(i).getId_shipper()
                        );

                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                waitingDialog.dismiss();
                loadTransferredData();
            }
        }, 1000);
    }

    public void loadTransferredData() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getShippersForTransCompleted(Common.DATE);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getAllWalletShippers().size() <= 0) {
                    waitingDialog.dismiss();
                    walletRecyclerView.setVisibility(View.GONE);
                    layEmpty.setVisibility(View.VISIBLE);
                    textViewNo.setVisibility(View.GONE);
                    edit_search.setVisibility(View.GONE);
                    //textViewLongPress.setVisibility(View.GONE);
                    layoutRider.setVisibility(View.GONE);
                    btn_verify.setVisibility(View.GONE);
                    //Toasty.info(getContext(), "No pending list", Toast.LENGTH_SHORT, true).show();
                } else {
                    waitingDialog.dismiss();

                    walletRecyclerView.setVisibility(View.VISIBLE);
                    layEmpty.setVisibility(View.GONE);
                    //textViewLongPress.setVisibility(View.VISIBLE);
                    layoutRider.setVisibility(View.VISIBLE);
                    btn_verify.setVisibility(View.VISIBLE);
                    textViewNo.setVisibility(View.VISIBLE);
                    textViewNo.setText(response.body().getAllWalletShippers().size() + " users found!");
                    edit_search.setVisibility(View.VISIBLE);
                    allTransferList = response.body().getAllWalletShippers();
                    loadRiders(allTransferList);
                    adapter = new AllWalletShippersAdapter(allTransferList, getContext(), "TransferredWalletFragment");
                    walletRecyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRiders(List<User> transferList) {
        final List<String> shipperNameID = new ArrayList<>();
        final Map<String, String> shipperMap = new HashMap<String, String>();

        for (int i = 0; i < transferList.size(); i++) {
            shipperMap.put(transferList.get(i).getUsername(), transferList.get(i).getId_shipper());
            shipperNameID.add(transferList.get(i).getUsername());
        }

        HashSet hs = new HashSet();
        hs.addAll(shipperNameID);
        shipperNameID.clear();
        shipperNameID.addAll(hs);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, shipperNameID);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_rider.setAdapter(dataAdapter);

        spinner_rider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String string = spinner_rider.getSelectedItem().toString();
                shipperName = string;
                shipperID = shipperMap.get(string);
                filterRider(string);

                //textViewNo.setText(temp.size() + " items found!");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    void filterRider(String text) {
        List<User> temp = new ArrayList();

        double total = 0.0;
        for (User d : allTransferList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUsername().toLowerCase().contains(text.toLowerCase())
                    || d.getTotal_price().toLowerCase().contains(text.toLowerCase())
                    || d.getPhone().toLowerCase().contains(text.toLowerCase())) {
                total += Double.parseDouble(d.getTotal_price());
                temp.add(d);
            }

        }
        //update recyclerview
        adapter.updateList(temp);
        textViewNo.setText(temp.size() + " items found - " + this.getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
    }

    void filter(String text) {
        List<User> temp = new ArrayList();
        for (User d : allTransferList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUsername().toLowerCase().contains(text.toLowerCase())
                    || d.getTotal_price().toLowerCase().contains(text.toLowerCase())
                    || d.getPhone().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
        textViewNo.setText(temp.size() + " riders found!");
    }


    private boolean isViewShown = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null && isVisibleToUser) {
            //edit_search.setText("");
            isViewShown = true; // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            loadTransferredData();
        } else {
            isViewShown = false;
        }
    }
}
