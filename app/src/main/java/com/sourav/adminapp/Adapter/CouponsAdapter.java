package com.sourav.adminapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sourav.adminapp.Activity.UpdateCouponActivity;
import com.sourav.adminapp.Activity.UpdateMenuActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Model.Coupon;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder>{

    private List<Coupon> couponList;
    private Context mCtx;

    public CouponsAdapter(List<Coupon> couponList, Context mCtx) {
        this.couponList = couponList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public CouponsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_coupon_items, parent, false);
        return new CouponsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CouponsAdapter.ViewHolder holder, final int position) {
        final Coupon coupon = couponList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewName.setText("Coupon name: " + coupon.getName());
        holder.textViewCodeType.setText("Code: " + coupon.getCode() + " - " + coupon.getType());
        holder.textViewDiscountPrice.setText("Discount Price: " + coupon.getDiscount());

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
                alertDialog.setTitle("Update Coupon");
                alertDialog.setMessage("Press update or delete for action");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCoupon(coupon.getCoupon_id(), coupon.getName(), holder.getAdapterPosition());
                    }
                });
                alertDialog.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mCtx, UpdateCouponActivity.class);
                        intent.putExtra("ID", coupon.getCoupon_id());
                        intent.putExtra("NAME", coupon.getName());
                        intent.putExtra("CODE", coupon.getCode());
                        intent.putExtra("DISCOUNT", coupon.getDiscount());
                        intent.putExtra("DISCOUNT_LIMIT", coupon.getDiscount_limit());
                        intent.putExtra("TOTAL", coupon.getTotal());
                        intent.putExtra("TOTAL_USES", coupon.getUses_total());
                        intent.putExtra("TOTAL_CUSTOMER_USES", coupon.getUses_customer());
                        intent.putExtra("START_DATE_IN", coupon.getStart_date());
                        intent.putExtra("END_DATE_IN", coupon.getEnd_date());

                        mCtx.startActivity(intent);
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

    private void deleteCoupon(final String id, String name, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
        alertDialog.setTitle("Remove Item");
        alertDialog.setMessage("Are you sure you want to remove " + name);
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Defining retrofit api service
                ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                Call<Result> call = service.deleteCoupon(id);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        couponList.remove(position);
                        notifyItemRemoved(position);
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#FF8A65"));
        }
    }


    @Override
    public int getItemCount() {
        return couponList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName, textViewCodeType, textViewDiscountPrice;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.item_name);
            textViewCodeType = (TextView) itemView.findViewById(R.id.item_code_type);
            textViewDiscountPrice = (TextView) itemView.findViewById(R.id.item_discount);
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
