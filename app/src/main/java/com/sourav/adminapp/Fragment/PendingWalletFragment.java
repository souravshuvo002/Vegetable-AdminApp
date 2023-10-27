package com.sourav.adminapp.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.List;

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

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

public class PendingWalletFragment extends Fragment {

    private String USER_ID, DELIVERY_DATE = "", currentDate = "";
    private EditText edit_search;
    private LinearLayout layEmpty;
    private TextView textViewNo;
    private RecyclerView walletRecyclerView;
    private List<User> allShippersList;
    private AllWalletShippersAdapter adapter;

    public PendingWalletFragment() {
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


        layEmpty = (LinearLayout) view.findViewById(R.id.empty_view);
        textViewNo = (TextView) view.findViewById(R.id.textViewNo);

        edit_search = (EditText) view.findViewById(R.id.edit_search);
        walletRecyclerView = (RecyclerView) view.findViewById(R.id.walletRecyclerView);
        walletRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        walletRecyclerView.setHasFixedSize(true);
        walletRecyclerView.setItemAnimator(new DefaultItemAnimator());
        walletRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


        if (!isViewShown) {
            loadShippers();
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
    }

    private void loadShippers() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getShippersForTransPending(Common.DATE);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.body().getAllWalletShippers().size() <= 0) {
                    waitingDialog.dismiss();
                    walletRecyclerView.setVisibility(View.GONE);
                    layEmpty.setVisibility(View.VISIBLE);
                    textViewNo.setVisibility(View.GONE);
                    edit_search.setVisibility(View.GONE);
                    //Toasty.info(getContext(), "No pending list", Toast.LENGTH_SHORT, true).show();
                } else {
                    waitingDialog.dismiss();
                    walletRecyclerView.setVisibility(View.VISIBLE);
                    layEmpty.setVisibility(View.GONE);
                    textViewNo.setVisibility(View.VISIBLE);
                    textViewNo.setText(response.body().getAllWalletShippers().size() + " users found!");
                    edit_search.setVisibility(View.VISIBLE);
                    allShippersList = response.body().getAllWalletShippers();

                    adapter = new AllWalletShippersAdapter(allShippersList, getContext(), "PendingWalletFragment");
                    walletRecyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void filter(String text) {
        List<User> temp = new ArrayList();
        for (User d : allShippersList) {
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
            isViewShown = true; // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
            loadShippers();
        } else {
            isViewShown = false;
        }
    }
}
