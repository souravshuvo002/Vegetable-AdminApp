package com.sourav.adminapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
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
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sourav.adminapp.Activity.AreaActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Model.Area;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder>{

    private List<Area> areaList;
    private Context mCtx;

    public AreaAdapter(List<Area> areaList, Context mCtx) {
        this.areaList = areaList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public AreaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_area, parent, false);
        return new AreaAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AreaAdapter.ViewHolder holder, final int position) {
        final Area area = areaList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewCityName.setText(area.getCity_name());
        holder.textViewAreaName.setText(area.getArea_name());

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Remove item from Cart
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
                alertDialog.setTitle("Update Area");
                alertDialog.setMessage("Press update or delete for action");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteArea(area.getId(), area.getArea_name(), holder.getAdapterPosition());
                    }
                });
                alertDialog.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUpdateAreaDialog(area.getId(), area.getCity_name(), area.getArea_name());
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

    private void deleteArea(final String id, String name, final int position) {
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
                Call<Result> call = service.deleteArea(id);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        areaList.remove(position);
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
        return areaList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewCityName, textViewAreaName;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewCityName = (TextView) itemView.findViewById(R.id.item_city_name);
            textViewAreaName = (TextView) itemView.findViewById(R.id.item_area_name);
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

    private void showUpdateAreaDialog(final String id, String city_name, String area_name) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
        alertDialog.setTitle("Update Area");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View add_area_layout = inflater.inflate(R.layout.add_new_area_layout, null);

        final MaterialEditText edtCityName = add_area_layout.findViewById(R.id.edtCityName);
        final MaterialEditText edtAreaName = add_area_layout.findViewById(R.id.edtAreaName);
        
        edtAreaName.setText(area_name);
        edtCityName.setText(city_name);


        alertDialog.setView(add_area_layout);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                final android.app.AlertDialog waitingDialog = new SpotsDialog(mCtx);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");

                //Defining retrofit api service
                ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                Call<Result> call = service.updateArea(id, edtCityName.getText().toString(), edtAreaName.getText().toString());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        waitingDialog.dismiss();
                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        ((AreaActivity)mCtx).loadListArea();
                    }
                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

}