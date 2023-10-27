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
import com.bumptech.glide.request.RequestOptions;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sourav.adminapp.Activity.UnitActivity;
import com.sourav.adminapp.Activity.UpdateCouponActivity;
import com.sourav.adminapp.Activity.UserAllOrderHistoryActivity;
import com.sourav.adminapp.Api.ApiClient;
import com.sourav.adminapp.Api.ApiService;
import com.sourav.adminapp.Api.ApiURL;
import com.sourav.adminapp.Common.Common;
import com.sourav.adminapp.Model.Result;
import com.sourav.adminapp.Model.User;
import com.sourav.adminapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private Context mCtx;

    public UserAdapter(List<User> userList, Context mCtx) {
        this.userList = userList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user_items, parent, false);
        return new UserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, final int position) {
        final User user = userList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.ic_person_black_24dp);

        Glide.with(mCtx)
                .setDefaultRequestOptions(requestOptions)
                .load(ApiURL.SERVER_URL + user.getImage_url()).into(holder.imageViewUser);

        holder.textViewUsername.setText(user.getUsername());
        holder.textViewPhone.setText(user.getPhone());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewAddress.setText(user.getAddress());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, UserAllOrderHistoryActivity.class);
                Common.User_ID = user.getId();
                mCtx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewUser;
        public TextView textViewUsername, textViewPhone, textViewEmail, textViewAddress;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewUser = (ImageView) itemView.findViewById(R.id.imageViewUser);
            textViewUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            textViewPhone = (TextView) itemView.findViewById(R.id.textViewPhone);
            textViewEmail = (TextView) itemView.findViewById(R.id.textViewEmail);
            textViewAddress = (TextView) itemView.findViewById(R.id.textViewAddress);

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
