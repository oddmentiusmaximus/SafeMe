package com.example.bmcc_locationapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.bmcc_locationapp.Interface.IRecyclerItemClickListener;
import com.example.bmcc_locationapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  public TextView txt_user_email;

    public void setiRecyclerItemClickListener(IRecyclerItemClickListener iRecyclerItemClickListener) {
        this.iRecyclerItemClickListener = iRecyclerItemClickListener;
    }

    IRecyclerItemClickListener iRecyclerItemClickListener;


    public UserViewHolder(@NonNull View itemView) {
        super(itemView);


        txt_user_email=itemView.findViewById(R.id.txt_user_email);
        itemView.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
iRecyclerItemClickListener.onItemClickListener(view,getAdapterPosition());
    }
}
