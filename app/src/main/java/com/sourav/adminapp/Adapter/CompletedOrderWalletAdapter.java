package com.sourav.adminapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sourav.adminapp.Activity.SingleOrderStatusActivity;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Helper.CheckPermission;
import com.sourav.adminapp.Model.Order;
import com.sourav.adminapp.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.CALL_PHONE;

public class CompletedOrderWalletAdapter extends RecyclerView.Adapter<CompletedOrderWalletAdapter.ViewHolder> {

    private List<Order> foodList;
    private Context context;
    private int lastPosition = -1;
    private String item;


    public CompletedOrderWalletAdapter(List<Order> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @Override
    public CompletedOrderWalletAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_order_history_2, parent, false);
        return new CompletedOrderWalletAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CompletedOrderWalletAdapter.ViewHolder holder, final int position) {
        final Order food = foodList.get(position);

        /**
         *  Animation Part
         */
        /*Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        holder.itemView.startAnimation(animation);
        lastPosition = position;*/

        holder.textViewOrderID.setText("ID: " + food.getId_order());
        holder.textViewName.setText(food.getUsername());
        holder.textViewAddress.setText(food.getAddress());
        holder.textViewArea.setText("Area: " + food.getArea());

        String strCurrentDate = food.getOrder_date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date newDate = null;
        try {
            newDate = format.parse(strCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        format = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
        String date = format.format(newDate);

        holder.textViewOrderDate.setText(date);

        DecimalFormat df2 = new DecimalFormat("####0.00");
        double price = Double.parseDouble(food.getTotal_price());

        holder.textViewItemPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
        holder.textViewOrderStatus.setText(Common.convertCodeToStatus(food.getOrder_status()));

        if(food.getTransfer_state().equalsIgnoreCase("0"))
        {
            holder.textViewTransferStatus.setText("Pending");
        }
        else if(food.getTransfer_state().equalsIgnoreCase("1"))
        {
            holder.textViewTransferStatus.setText("Transferred");
        }
        else if(food.getTransfer_state().equalsIgnoreCase("2"))
        {
            holder.textViewTransferStatus.setText("Verified");
        }

        holder.textViewPayment.setText(food.getPayment_method() + " - " + food.getPayment_state());


        holder.buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPermission checkPermission = new CheckPermission(context);

                if (checkPermission.checkSinglePermission(CALL_PHONE)) {
                    Intent dialIntent = new Intent();
                    dialIntent.setAction(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + food.getPhone()));
                    context.startActivity(dialIntent);

                } else {
                    checkPermission.requestForSinglePermission(CALL_PHONE);
                }
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SingleOrderStatusActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ID_ORDER", food.getId_order());
                Common.fragment_state = "COMPLETED";
                Common.id_order = food.getId_order();
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName, textViewOrderID, textViewOrderDate, textViewAddress, textViewItemPrice,
                textViewTransferStatus, textViewOrderStatus, textViewArea, textViewPayment;
        public Button buttonCall;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewAddress = (TextView) itemView.findViewById(R.id.textViewAddress);
            textViewArea = (TextView) itemView.findViewById(R.id.textViewArea);
            textViewOrderID = (TextView) itemView.findViewById(R.id.textViewOrderID);
            textViewOrderDate = (TextView) itemView.findViewById(R.id.textViewOrderDate);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewOrderStatus = (TextView) itemView.findViewById(R.id.textViewOrderStatus);
            textViewTransferStatus = (TextView) itemView.findViewById(R.id.textViewTransferStatus);
            textViewPayment = (TextView) itemView.findViewById(R.id.textViewPayment);
            buttonCall = (Button) itemView.findViewById(R.id.buttonCall);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void updateList(List<Order> list) {
        this.foodList = list;
        notifyDataSetChanged();
    }
}