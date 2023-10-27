package com.sourav.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sourav.adminapp.Activity.SingleOrderStatusActivity;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AllOrderHistoryAdapter extends RecyclerView.Adapter<AllOrderHistoryAdapter.ViewHolder> {

    private List<Order> orderList;
    private Context context;
    private int lastPosition = -1;


    public AllOrderHistoryAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public AllOrderHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_all_order_history_items, parent, false);
        return new AllOrderHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AllOrderHistoryAdapter.ViewHolder holder, final int position) {
        final Order order = orderList.get(position);

        /**
         *  Animation Part
         */
        /*Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        holder.itemView.startAnimation(animation);
        lastPosition = position;*/

        holder.textViewOrderID.setText(order.getId_order());

        String strCurrentDate = order.getOrder_date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String date = format.format(newDate);

        holder.textViewOrderDate.setText("O.date: " + date);

        String strCurrentDate2 = order.getDelivery_date();
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate2 = null;
        try {
            newDate2 = format2.parse(strCurrentDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format2 = new SimpleDateFormat("MMM dd, yyyy");
        String date2 = format2.format(newDate2);

        holder.textViewDelDate.setText("D.date: " + date2);

        holder.textViewItemPrice.setText(context.getResources().getString(R.string.currency_sign)+ order.getTotal_price());
        holder.textViewOrderStatus.setText(Common.convertCodeToStatus(order.getOrder_status()));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleOrderStatusActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ID_ORDER", order.getId_order());
                Common.id_order = order.getId_order();
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewOrderID, textViewOrderDate, textViewDelDate, textViewItemPrice, textViewOrderStatus;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewOrderID = (TextView) itemView.findViewById(R.id.textViewOrderID);
            textViewOrderDate = (TextView) itemView.findViewById(R.id.textViewOrderDate);
            textViewDelDate = (TextView) itemView.findViewById(R.id.textViewDelDate);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewOrderStatus = (TextView) itemView.findViewById(R.id.textViewOrderStatus);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void updateList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }
}