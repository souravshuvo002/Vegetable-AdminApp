package com.sourav.adminapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.sourav.adminapp.Activity.AreaActivity;
import com.sourav.adminapp.Activity.UnitActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.Unit;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.ViewHolder> {

    private List<Unit> unitList;
    private Context mCtx;

    public UnitAdapter(List<Unit> unitList, Context mCtx) {
        this.unitList = unitList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public UnitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_unit, parent, false);
        return new UnitAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UnitAdapter.ViewHolder holder, final int position) {
        final Unit unit = unitList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.item_unit_name.setText(unit.getUnit_type());

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
                alertDialog.setTitle("Update Unit");
                alertDialog.setMessage("Press update or delete for action");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUnit(unit.getId(), unit.getUnit_type(), holder.getAdapterPosition());
                    }
                });
                alertDialog.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUpdateUnitDialog(unit.getId(), unit.getUnit_type());
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

    private void deleteUnit(final String id, String unit_type, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
        alertDialog.setTitle("Remove Item");
        alertDialog.setMessage("Are you sure you want to remove " + unit_type);
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
                Call<Result> call = service.deleteUnit(id);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {

                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        unitList.remove(position);
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
        return unitList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item_unit_name;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            item_unit_name = (TextView) itemView.findViewById(R.id.item_unit_name);
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

    private void showUpdateUnitDialog(final String id, String unit_type) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
        alertDialog.setTitle("Update Unit");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View add_area_layout = inflater.inflate(R.layout.add_new_unit_layout, null);

        final MaterialEditText edtUnitType = add_area_layout.findViewById(R.id.edtUnitType);

        edtUnitType.setText(unit_type);

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
                Call<Result> call = service.updateUnit(id, edtUnitType.getText().toString());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        waitingDialog.dismiss();
                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        ((UnitActivity) mCtx).loadListUnit();
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