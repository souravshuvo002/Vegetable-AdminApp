package com.sourav.adminapp.Fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sourav.adminapp.Activity.SingleOrderStatusActivity;
import com.sourav.adminapp.Adapter.AllOrderHistoryAdapter;
import com.sourav.adminapp.Adapter.AssignOrderToShipperAdapter;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Api.IFCMService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.DataMessage;
import com.sourav.adminapp.Model.MyResponse;
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
import androidx.core.content.ContextCompat;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AssignOrderToShipperFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String USER_ID, DELIVERY_DATE = "", currentDate = "";
    private EditText edit_search;
    private LinearLayout layEmpty, linearLayAreaRider;
    private TextView textViewNo, textViewDate, textViewShowAllFoods;
    private Spinner spinnerArea, spinnerRider;
    private RecyclerView newOrderRecyclerView;
    private Button btn_assign;
    public AssignOrderToShipperAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Order> orderList, orderedFoodQuantity;
    private List<User> shipperList;
    private Date cDate;
    private String shipperName, shipperID;
    private ArrayList<String> areaList;


    public AssignOrderToShipperFragment() {
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
        return inflater.inflate(R.layout.layout_assign_order_to_shipper_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        linearLayAreaRider = (LinearLayout) view.findViewById(R.id.linearLayAreaRider);
        textViewNo = (TextView) view.findViewById(R.id.textViewNo);
        textViewDate = (TextView) view.findViewById(R.id.textViewDate);
        textViewShowAllFoods = (TextView) view.findViewById(R.id.textViewShowAllFoods);
        spinnerArea = (Spinner) view.findViewById(R.id.spinner_area);
        spinnerRider = (Spinner) view.findViewById(R.id.spinner_rider);
        btn_assign = (Button) view.findViewById(R.id.btn_assign);
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

        btn_assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignOrderToShipper();
            }
        });

    }

    private void assignOrderToShipper() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(getContext());
        waitingDialog.show();
        waitingDialog.setMessage("Placing ...");

        // Delaying action for 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final List<Order> stList = ((AssignOrderToShipperAdapter) adapter).getAssignedShipper();
                for (int i = 0; i < stList.size(); i++) {
                    //Update Order Status
                    //building retrofit object
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ApiURL.SERVER_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    //Defining retrofit api service
                    ApiService service = retrofit.create(ApiService.class);
                    Call<Result> call = service.updateOrderStatus(
                            stList.get(i).getId_order(),
                            shipperID,
                            "4",
                            "Unpaid",
                            Common.getDateTime());

                    final int finalI = i;
                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {

                            String status = "Shipping";

                            sendOrderUpdateNotification(stList.get(finalI).getId_user(), stList.get(finalI).getId_order(), status);
                            sendOrderUpdateNotificationToShipper(stList.get(finalI).getId_order(), status, shipperID);
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                        }
                    });
                }
                waitingDialog.dismiss();
                loadOrderStatusItems();
            }
        }, 1000);
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
                    orderList = getAcceptedList(response.body().getOrderList());

                    if(orderList.size() <= 0)
                    {
                        Toasty.error(getActivity(), "No accepted order found!", Toasty.LENGTH_LONG).show();
                        linearLayAreaRider.setVisibility(View.GONE);
                        newOrderRecyclerView.setVisibility(View.GONE);
                    }
                    else
                    {
                        linearLayAreaRider.setVisibility(View.VISIBLE);
                        loadArea(orderList);
                        loadShipper();
                        if (orderList.size() > 1) {
                            textViewNo.setText(orderList.size() + " orders found!");
                        } else {
                            textViewNo.setText(orderList.size() + " order found!");
                        }
                        newOrderRecyclerView.setVisibility(View.VISIBLE);
                        adapter = new AssignOrderToShipperAdapter(response.body().getOrderList(), getContext());
                        newOrderRecyclerView.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadArea(List<Order> orderList) {
        areaList = new ArrayList<String>();
        for (int i = 0; i < orderList.size(); i++) {
            areaList.add(orderList.get(i).getArea());
        }

        HashSet hs = new HashSet();
        hs.addAll(areaList);
        areaList.clear();
        areaList.addAll(hs);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areaList);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerArea.setAdapter(dataAdapter);

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String string = spinnerArea.getSelectedItem().toString();
                /*shipperName = string;
                shipperID = shipperMap.get(string);*/
                filterArea(string);

                //textViewNo.setText(temp.size() + " items found!");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                spinnerRider.setAdapter(dataAdapter);

                spinnerRider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String string = spinnerRider.getSelectedItem().toString();
                        shipperName = string;
                        shipperID = shipperMap.get(string);
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

    void filterArea(String text) {
        List<Order> temp = new ArrayList();

        for (int i = 0; i < orderList.size(); i++) {
            orderList.get(i).setSelected(false);
        }

        for (Order d : orderList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getArea().toLowerCase().contains(text.toLowerCase())) {
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

                if (orderedFoodQuantity.size() > 0) {
                    for (int i = 0; i < orderedFoodQuantity.size(); i++) {
                        TextView text = new TextView(getContext());
                        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        text.setText(i + 1 + ". " + orderedFoodQuantity.get(i).getFood_name() + " - " + orderedFoodQuantity.get(i).getFood_quantity() + " (" + orderedFoodQuantity.get(i).getFood_min_unit_amount() + " " + orderedFoodQuantity.get(i).getFood_unit() + ")");
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        text.setPadding(10, 10, 10, 10);
                        text.setTextColor(ContextCompat.getColor(getContext(), R.color.toastMessageColor));
                        linearLayout.addView(text);
                    }
                } else {
                    TextView text = new TextView(getContext());
                    text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    text.setText("No Food Items");
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    text.setPadding(10, 10, 10, 10);
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

    private void sendOrderUpdateNotification(final String id_user, final String id_order, final String status) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiURL.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiService service = retrofit.create(ApiService.class);

        //defining the call
        Call<Result> call = service.getUserToken(id_user, "0");
        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Map<String, String> datasend = new HashMap<>();
                datasend.put("title", getActivity().getResources().getString(R.string.main_app_name));
                datasend.put("message", "Your order #" + id_order + " has been " + status);
                datasend.put("type", "Order");


                DataMessage dataMessage = new DataMessage();
                if (response.body().getToken().getToken() != null) {
                    dataMessage.setTo(response.body().getToken().getToken());
                }
                dataMessage.setData(datasend);
                IFCMService ifcmService = Common.getFCMService();
                ifcmService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                if (response.code() == 200) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(getContext(), "Order Updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Send Notification failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Msg: ", "CheckOut");
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendOrderUpdateNotificationToShipper(final String id_order, final String status, final String id_shipper) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiURL.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiService service = retrofit.create(ApiService.class);

        //defining the call
        Call<Result> call = service.getUserToken(id_shipper, "2");
        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                Map<String, String> datasend = new HashMap<>();
                datasend.put("title", getActivity().getResources().getString(R.string.main_app_name));
                datasend.put("message", "You have new Order no: " + id_order);

                DataMessage dataMessage = new DataMessage();
                if (response.body().getToken().getToken() != null) {
                    dataMessage.setTo(response.body().getToken().getToken());
                }
                dataMessage.setData(datasend);
                IFCMService ifcmService = Common.getFCMService();
                ifcmService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                if (response.code() == 200) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(getContext(), "Order Updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Send Notification failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Msg: ", "CheckOut");
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<Order> getAcceptedList(List<Order> acceptedList) {
        orderList = new ArrayList<Order>();
        for (Order order : acceptedList) {
            if (order.getOrder_status().equalsIgnoreCase("2")) {
                orderList.add(order);
            }
        }

        return orderList;
    }
}
