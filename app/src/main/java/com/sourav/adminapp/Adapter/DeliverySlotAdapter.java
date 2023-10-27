package com.sourav.adminapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.sourav.adminapp.Activity.SlotActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Model.DeliverySlot;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliverySlotAdapter extends RecyclerView.Adapter<DeliverySlotAdapter.ViewHolder> {

    private List<DeliverySlot> deliverySlotList;
    private Context mCtx;

    public DeliverySlotAdapter(List<DeliverySlot> deliverySlotList, Context mCtx) {
        this.deliverySlotList = deliverySlotList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public DeliverySlotAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_delivery_slot_items, parent, false);
        return new DeliverySlotAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DeliverySlotAdapter.ViewHolder holder, final int position) {
        final DeliverySlot deliverySlot = deliverySlotList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        //holder.item_day.setText(deliverySlot.getDay() + ": " + deliverySlot.getStart_time() + " - " + deliverySlot.getEnd_time());

        holder.item_day.setText("Max Order Days: " + deliverySlot.getMax_day()
                + " days\nDelivery time: " + deliverySlot.getStart_time()
                + " - " + deliverySlot.getEnd_time() + "\nLast Order Time: "
                + deliverySlot.getOrder_time()
                + "\nCharge: " + deliverySlot.getDelivery_charge()
                + "\nDelivery free amount: " + deliverySlot.getDelivery_free_amount());

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showAddSlotDialog();
                return false;
            }
        });
    }

    private void showAddSlotDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mCtx);
        alertDialog.setTitle("Update new Slot");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View add_slot_layout = inflater.inflate(R.layout.add_new_slot_layout, null);

        final MaterialEditText edtMaxDay, edtOrderTime, edtDelCharge, edtDelFreeAmount;
        final Spinner spinner_start_time, spinner_end_time;

        edtMaxDay = add_slot_layout.findViewById(R.id.edtMaxDay);
        edtOrderTime = add_slot_layout.findViewById(R.id.edtOrderTime);
        edtDelCharge = add_slot_layout.findViewById(R.id.edtDelCharge);
        edtDelFreeAmount = add_slot_layout.findViewById(R.id.edtDelFreeAmount);

        spinner_start_time = (Spinner) add_slot_layout.findViewById(R.id.spinner_start_time);
        spinner_end_time = (Spinner) add_slot_layout.findViewById(R.id.spinner_end_time);

        //edtHomeAddress.setText(Common.currentUser.getAddress());


        alertDialog.setView(add_slot_layout);
        //alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

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
                Call<Result> call = service.updateSlot(
                        edtMaxDay.getText().toString(),
                        spinner_start_time.getSelectedItem().toString(),
                        spinner_end_time.getSelectedItem().toString(),
                        edtOrderTime.getText().toString(),
                        edtDelCharge.getText().toString(),
                        edtDelFreeAmount.getText().toString());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        waitingDialog.dismiss();
                        Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        //loadSlots();
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

    @Override
    public int getItemCount() {
        return deliverySlotList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView item_day;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            item_day = (TextView) itemView.findViewById(R.id.item_day);
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