package com.ballidaku.etracking.mainScreens.beatScreens;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.adapters.NotificationsAdapter;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.RecyclerViewEmptySupport;
import com.ballidaku.etracking.dataModels.NotificationModel;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity
{

    Context context;
    RecyclerViewEmptySupport recyclerViewNotifications;
    NotificationsAdapter notificationsAdapter;

    ArrayList<NotificationModel> arrayListImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        context=this;

        setUpViews();
        getFirebaseNotifications(true);
    }

    private void setUpViews()
    {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        ((TextView)findViewById(R.id.textViewTitle)).setText("Notifications");
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setEmptyView(findViewById(R.id.textViewEmpty));

        notificationsAdapter = new NotificationsAdapter(context, arrayListImages);
        recyclerViewNotifications.setAdapter(notificationsAdapter);

    }

    void getFirebaseNotifications(boolean b)
    {
        MyFirebase.getInstance().getAllNotifications(context, b,new Interfaces.GetNotificationListener()
        {
            @Override
            public void callback(ArrayList<NotificationModel> arrayList)
            {
                notificationsAdapter.addData(arrayList);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
