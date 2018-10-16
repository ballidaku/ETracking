package com.ballidaku.etracking.mainScreens.adminScreens.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.adapters.NotificationsAdapter;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.commonClasses.RecyclerViewEmptySupport;
import com.ballidaku.etracking.dataModels.NotificationModel;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;

import java.util.ArrayList;

public class PostNotificationFragment extends Fragment implements View.OnClickListener
{
    String TAG = PostNotificationFragment.class.getSimpleName();
    View view = null;
    Context context;
    RecyclerViewEmptySupport recyclerViewNotifications;
    NotificationsAdapter notificationsAdapter;

    EditText editTextNewsFeed;

    ArrayList<NotificationModel> arrayListImages = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (view == null)
        {
            view = inflater.inflate(R.layout.fragment_post_notifications, container, false);
            context = getActivity();
            setUpViews();
            getFirebaseNotifications(true);
        }
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ((MainActivity) context).toolbar.setTitle("Notifications");
        ((MainActivity) context).refreshMenu(5);
    }

    private void setUpViews()
    {
        editTextNewsFeed = view.findViewById(R.id.editTextNewsFeed);
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        recyclerViewNotifications.setEmptyView(view.findViewById(R.id.textViewEmpty));

        notificationsAdapter = new NotificationsAdapter(context, arrayListImages);
        recyclerViewNotifications.setAdapter(notificationsAdapter);

        view.findViewById(R.id.imageViewPost).setOnClickListener(this);
    }

    void getFirebaseNotifications(boolean b)
    {
        MyFirebase.getInstance().getAllNotifications(context, b, arrayList -> notificationsAdapter.addData(arrayList));
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.imageViewPost:

                String text = editTextNewsFeed.getText().toString().trim();

                if (!text.isEmpty())
                {
                    editTextNewsFeed.getText().clear();

                    MyFirebase.getInstance().createNotification(context, text, () -> getFirebaseNotifications(false));
                }
                break;
        }
    }
}
