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
import com.sourav.adminapp.Activity.FoodActivity;
import com.sourav.adminapp.Activity.UpdateBannerActivity;
import com.sourav.adminapp.Activity.UpdateMenuActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Category;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private List<Category> menuList;
    private Context mCtx;

    public MenuAdapter(List<Category> menuList, Context mCtx) {
        this.menuList = menuList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_banner, parent, false);
        return new MenuAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MenuAdapter.ViewHolder holder, final int position) {
        final Category menu = menuList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewName.setText(menu.getName());
        Glide.with(mCtx)
                .load(ApiURL.SERVER_URL + menu.getImage_url())
                .into(holder.imageView);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, FoodActivity.class);
                intent.putExtra("MENU_ID", menu.getId());
                intent.putExtra("MENU_NAME", menu.getName());
                Common.menu_id = menu.getName();

                mCtx.startActivity(intent);
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
                alertDialog.setTitle("Update Menu");
                alertDialog.setMessage("Press update or delete for action");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMenu(menu.getId(), menu.getName(), holder.getAdapterPosition());
                    }
                });
                alertDialog.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mCtx, UpdateMenuActivity.class);
                        intent.putExtra("ID", menu.getId());
                        intent.putExtra("NAME", menu.getName());
                        intent.putExtra("IMAGE_URL", menu.getImage_url());
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


    private void deleteMenu(final String id, String name, final int position) {
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
                Call<Result> call = service.deleteMenu(id);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        menuList.remove(position);
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
        return menuList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.item_name);
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