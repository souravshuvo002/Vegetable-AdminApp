package com.sourav.adminapp.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.adminapp.Activity.SummaryActivity;
import com.sourav.adminapp.Adapter.AllOrderHistoryAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TotalSummaryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextView textViewRevenue, textViewCompletedOrder, textViewCancelOrder;
    private String YEAR, MONTH;
    private Date cDate;
    private SwipeRefreshLayout swipeRefreshLayout;

    public TotalSummaryFragment() {
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
        return inflater.inflate(R.layout.layout_total_summary_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        cDate = new Date();
        YEAR = new SimpleDateFormat("yyyy").format(cDate);
        MONTH = new SimpleDateFormat("MM").format(cDate);

        // getting views
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        textViewRevenue = (TextView) view.findViewById(R.id.textViewRevenue);
        textViewCompletedOrder = (TextView) view.findViewById(R.id.textViewCompletedOrder);
        textViewCancelOrder = (TextView) view.findViewById(R.id.textViewCancelOrder);

        loadSummary();
    }

    private void loadSummary() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getSummary(YEAR, MONTH);

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                textViewRevenue.setText(getContext().getResources().getString(R.string.currency_sign) + response.body().getSaleSummary().getTotal_sale_amount());
                textViewCompletedOrder.setText(response.body().getSaleSummary().getTotal_completed_order());
                textViewCancelOrder.setText(response.body().getSaleSummary().getTotal_cancelled_order());

                waitingDialog.dismiss();

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });
    }

    @Override
    public void onRefresh() {
        loadSummary();
    }
    
}