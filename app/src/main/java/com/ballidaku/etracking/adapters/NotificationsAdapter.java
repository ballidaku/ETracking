package com.ballidaku.etracking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.dataModels.NotificationModel;

import java.util.ArrayList;

public class NotificationsAdapter  extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>
{
    Context context;
    ArrayList<NotificationModel> notificationModelArrayList;

    public NotificationsAdapter(Context context, ArrayList<NotificationModel> notificationModelArrayList)
    {
        this.context=context;
        this.notificationModelArrayList=notificationModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {

        NotificationModel notificationModel = notificationModelArrayList.get(position);
        holder.textviewUserName.setText("Post By : "+notificationModel.getSenderName());
        holder.textViewDateTime.setText(notificationModel.getNotificationDateTime());
        holder.textViewNotification.setText(notificationModel.getNotification());

    }

    @Override
    public int getItemCount()
    {
        return notificationModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView textviewUserName;
        TextView textViewDateTime;
        TextView textViewNotification;

        MyViewHolder(View view)
        {
            super(view);

            textviewUserName = view.findViewById(R.id.textviewUserName);
            textViewDateTime = view.findViewById(R.id.textViewDateTime);
            textViewNotification = view.findViewById(R.id.textViewNotification);
        }
    }

    public void addData(ArrayList<NotificationModel> notificationModelArrayList)
    {
        this.notificationModelArrayList=notificationModelArrayList;
        notifyDataSetChanged();
    }

}
