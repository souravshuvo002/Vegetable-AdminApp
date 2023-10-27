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
import com.sourav.adminapp.Activity.UpdateFoodsActivity;
import com.sourav.adminapp.Activity.UpdateMenuActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Banner;
import com.sourav.adminapp.Model.Foods;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>{

    private List<Foods> foodsList;
    private Context mCtx;

    public FoodAdapter(List<Foods> foodsList, Context mCtx) {
        this.foodsList = foodsList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_foods, parent, false);
        return new FoodAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FoodAdapter.ViewHolder holder, final int position) {
        final Foods foods = foodsList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewName.setText(foods.getName());
        holder.textViewPrice.setText(foods.getPrice());
        Glide.with(mCtx)
                .load(ApiURL.SERVER_URL + foods.getImage_url())
                .into(holder.imageView);
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
                alertDialog.setTitle("Update Foods");
                alertDialog.setMessage("Press update or delete for action");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFood(foods.getId(), foods.getName(), holder.getAdapterPosition());
                    }
                });
                alertDialog.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mCtx, UpdateFoodsActivity.class);
                        intent.putExtra("ID", foods.getId());
                        intent.putExtra("FOOD_NAME", foods.getName());
                        intent.putExtra("FOOD_PRICE", foods.getPrice());
                        intent.putExtra("FOOD_DISCOUNT_PRICE", foods.getDiscount_price());
                        intent.putExtra("FOOD_DESC", foods.getDescription());
                        intent.putExtra("FOOD_MIN_UNIT", foods.getMin_unit_amount());
                        intent.putExtra("FOOD_STATUS", foods.getStatus());
                        intent.putExtra("FOOD_IMAGE_URL", foods.getImage_url());
                        Common.menu_id = foods.getId_menu();

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

    private void deleteFood(final String id, String name, final int position) {
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
                Call<Result> call = service.deleteFoodItem(id);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        foodsList.remove(position);
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
        return foodsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName, textViewPrice;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.item_name);
            textViewPrice = (TextView) itemView.findViewById(R.id.item_price);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
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