package com.sourav.adminapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sourav.adminapp.Activity.AreaActivity;
import com.sourav.adminapp.Activity.ShipperManagementActivity;
import com.sourav.adminapp.Activity.SingleOrderStatusActivity;
import com.sourav.adminapp.Activity.SingleShipperActivity;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Helper.CheckPermission;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.User;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CALL_PHONE;

public class ShipperAdapter extends RecyclerView.Adapter<ShipperAdapter.ViewHolder> {

    private List<User> userList;
    private Context mCtx;
    private String item, status;

    public ShipperAdapter(List<User> userList, Context mCtx) {
        this.userList = userList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public ShipperAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_shipper_items, parent, false);
        return new ShipperAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ShipperAdapter.ViewHolder holder, final int position) {
        final User user = userList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewUsername.setText(user.getUsername());
        holder.textViewPhone.setText("Cell: " + user.getPhone());
        holder.textViewAddress.setText("Address: " + user.getAddress());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.ic_person_black_24dp);

        Glide.with(mCtx)
                .setDefaultRequestOptions(requestOptions)
                .load(ApiURL.SERVER_URL + user.getImage_url()).into(holder.imageViewUser);

        if (user.getStatus().equalsIgnoreCase("1")) {
            holder.buttonStatus.setText("Active");
            holder.buttonStatus.setBackgroundResource(R.color.colorPrimary);
        } else {
            holder.buttonStatus.setText("Suspend");
            holder.buttonStatus.setBackgroundResource(R.color.colorPrimary1);
        }

        holder.buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPermission checkPermission = new CheckPermission(mCtx);

                if (checkPermission.checkSinglePermission(CALL_PHONE)) {
                    Intent dialIntent = new Intent();
                    dialIntent.setAction(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + user.getPhone()));
                    mCtx.startActivity(dialIntent);

                } else {
                    checkPermission.requestForSinglePermission(CALL_PHONE);
                }
            }
        });

        holder.buttonStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
                alertDialog.setTitle("Activity Status");
                alertDialog.setMessage("Please Choose a status for Shipper");

                View myView = LayoutInflater.from(mCtx).inflate(R.layout.update_shipper_status_layout, null);

                String[] arraySpinner = new String[]{
                        "Active", "Suspend"};
                final Spinner spinner = (Spinner) myView.findViewById(R.id.spinner_ShipperStatus);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mCtx, android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                spinner.setAdapter(adapter);

                alertDialog.setView(myView);
                alertDialog.setCancelable(false);

                if (user.getStatus().equalsIgnoreCase("1")) {
                    spinner.setSelection(0);
                } else if (user.getStatus().equalsIgnoreCase("0")) {
                    spinner.setSelection(1);
                }

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        item = String.valueOf(spinner.getSelectedItem());

                        if (item.equalsIgnoreCase("Active")) {
                            status = "1";
                        } else {
                            status = "0";
                        }
                        //building retrofit object
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ApiURL.SERVER_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        //Defining retrofit api service
                        ApiService service = retrofit.create(ApiService.class);
                        Call<Result> call = service.updateShipperStatus(user.getId(), status);

                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                ((ShipperManagementActivity) mCtx).loadListShippers();
                            }
                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {

                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final android.app.AlertDialog dialog = alertDialog.create();
                alertDialog.show();
                Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (b != null) {
                    b.setTextColor(Color.parseColor("#FF8A65"));
                }
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, SingleShipperActivity.class);
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewUsername, textViewPhone, textViewAddress;
        public ImageView imageViewUser;
        public Button buttonCall, buttonStatus;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewUser = (ImageView) itemView.findViewById(R.id.imageViewUser);
            textViewUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            textViewPhone = (TextView) itemView.findViewById(R.id.textViewPhone);
            textViewAddress = (TextView) itemView.findViewById(R.id.textViewAddress);
            buttonCall = (Button) itemView.findViewById(R.id.buttonCall);
            buttonStatus = (Button) itemView.findViewById(R.id.buttonStatus);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}