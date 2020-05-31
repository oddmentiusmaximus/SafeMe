package com.example.bmcc_locationapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bmcc_locationapp.Interface.IRecyclerItemClickListener;
import com.example.bmcc_locationapp.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

public TextView txt_user_email;

public ImageView btn_accept,btn_decline;
public FriendRequestViewHolder(@NonNull View itemView) {
        super(itemView);
btn_accept=itemView.findViewById(R.id.btn_accept);
btn_decline=itemView.findViewById(R.id.btn_decline);

        txt_user_email=itemView.findViewById(R.id.txt_user_email);

        }




}
