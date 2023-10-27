package com.sourav.adminapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.sourav.adminapp.Activity.SingleOrderStatusActivity;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Api.IFCMService;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.DataMessage;
import com.sourav.adminapp.Model.MyResponse;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SingleOrderStatusAdapter extends RecyclerView.Adapter<SingleOrderStatusAdapter.ViewHolder> {

    private List<Order> orderList;
    private Context context;
    private String item;
    private int lastPosition = -1;

    public SingleOrderStatusAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;

    }

    @Override
    public SingleOrderStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_order_status_item, parent, false);
        return new SingleOrderStatusAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SingleOrderStatusAdapter.ViewHolder holder, final int position) {
        final Order order = orderList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        Glide.with(context).load(ApiURL.SERVER_URL + order.getFood_image_url()).into(holder.imageViewItem);



        holder.textViewItemUnit.setText("[" + order.getFood_min_unit_amount() + " " + order.getFood_unit() + "]");
        holder.textViewItemName.setText(order.getFood_name());
        holder.textViewItemPrice.setText("Unit Price: " + context.getResources().getString(R.string.currency_sign) + order.getFood_price());
        holder.textViewItemTotalPrice.setText("Total Price: " + context.getResources().getString(R.string.currency_sign) + order.getFood_total_price());
        holder.textViewItemQuantity.setText("Quantity: " + order.getFood_quantity());
        holder.textViewMenuName.setText(order.getMenu_name());

        /*holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DiagnosticTestActivity.class);
                intent.putExtra("NAME", book.getDiagnostic_center_name());
                intent.putExtra("CENTER_ID", book.getCenter_id());
                Common.CENTER_ID = book.getCenter_id();

                context.startActivity(intent);
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewItem;
        public TextView textViewItemUnit, textViewItemName, textViewItemPrice, textViewItemQuantity,
                textViewMenuName, textViewItemTotalPrice;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            textViewItemUnit = (TextView) itemView.findViewById(R.id.textViewItemUnit);
            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewItemTotalPrice = (TextView) itemView.findViewById(R.id.textViewItemTotalPrice);
            textViewItemQuantity = (TextView) itemView.findViewById(R.id.textViewItemQuantity);
            textViewMenuName = (TextView) itemView.findViewById(R.id.textViewMenuName);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
        }
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
