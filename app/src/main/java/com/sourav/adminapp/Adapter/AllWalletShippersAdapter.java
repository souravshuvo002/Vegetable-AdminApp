package com.sourav.adminapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sourav.adminapp.Activity.UpdateMenuActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.User;
import com.sourav.adminapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AllWalletShippersAdapter extends RecyclerView.Adapter<AllWalletShippersAdapter.ViewHolder> {

    private List<User> userList;
    private Context mCtx;
    private String item, status, fragmentName;

    public AllWalletShippersAdapter(List<User> userList, Context mCtx, String fragmentName) {
        this.userList = userList;
        this.mCtx = mCtx;
        this.fragmentName = fragmentName;

        setHasStableIds(true);
    }

    @Override
    public AllWalletShippersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_wallet_shippers_items, parent, false);
        return new AllWalletShippersAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllWalletShippersAdapter.ViewHolder holder, final int position) {
        final User user = userList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        Glide.with(mCtx).load(ApiURL.SERVER_URL + user.getImage_url()).into(holder.imageViewProfile);
        holder.shipper_name.setText(user.getUsername());
        holder.shipper_phone.setText(user.getPhone());
        holder.textViewAmount.setText(mCtx.getResources().getString(R.string.currency_sign) + String.format("%.2f", Double.parseDouble(user.getTotal_price())));

        if (user.getStatus().equalsIgnoreCase("0")) {
            holder.textViewStatus.setText("Transferred");

        } else if (user.getStatus().equalsIgnoreCase("1")) {
            holder.textViewStatus.setText("Verified");
        }

        if(fragmentName.equalsIgnoreCase("TransferredWalletFragment"))
        {
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
                    alertDialog.setTitle("Update Transfer status");
                    alertDialog.setMessage("Press Accept for transfer received");
                    alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final android.app.AlertDialog waitingDialog = new SpotsDialog(mCtx);
                            waitingDialog.show();
                            waitingDialog.setMessage("Please wait ...");

                            //Defining retrofit api service
                            ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                            Call<Result> call = service.updateWalletTransfer(user.getId(), user.getId_order(), user.getId_shipper());

                            call.enqueue(new Callback<Result>() {
                                @Override
                                public void onResponse(Call<Result> call, Response<Result> response) {
                                    waitingDialog.dismiss();
                                    Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Result> call, Throwable t) {
                                    waitingDialog.dismiss();
                                    Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
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
                    return false;
                }
            });
        }
        else
        {
            holder.textViewStatus.setText("Pending");
        }


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewProfile;
        public TextView shipper_name, shipper_phone, textViewAmount, textViewStatus;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewProfile = (ImageView) itemView.findViewById(R.id.imageViewUser);
            shipper_name = (TextView) itemView.findViewById(R.id.textViewUsername);
            shipper_phone = (TextView) itemView.findViewById(R.id.textViewPhone);
            textViewAmount = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewStatus = (TextView) itemView.findViewById(R.id.textViewStatus);
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

    public void updateList(List<User> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }
}