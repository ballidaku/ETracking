package com.ballidaku.etracking.mainScreens.beatScreens;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.adapters.ReportedVideosAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.VideoDataModel;

import java.util.ArrayList;

public class ReportedVideosActivity extends AppCompatActivity
{

    Context context;
    Toolbar toolbar;
    TextView textViewTitle;
    RecyclerView recyclerViewReportedVideos;
    ReportedVideosAdapter reportedVideosAdapter;

    ArrayList<VideoDataModel> arrayListImages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_videos);

        context = this;

        setUpView();


        getFirebaseVideos();
    }

    private void setUpView()
    {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Select Images");
//        toolbar.setTitleTextColor(ContextCompat.getColor(context,R.color.colorWhite));
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Reported Videos");

        recyclerViewReportedVideos=findViewById(R.id.recyclerViewReportedVideos);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerViewReportedVideos.setLayoutManager(layoutManager);
        recyclerViewReportedVideos.setItemAnimator(new DefaultItemAnimator());

        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;

        recyclerViewReportedVideos.addItemDecoration( CommonMethods.getInstance().new SpacesItemDecoration(spanCount,spacing,includeEdge));

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewReportedImages.getContext(),
//                layoutManager.getOrientation());

//        recyclerViewReportedImages.addItemDecoration(dividerItemDecoration);

        reportedVideosAdapter=new ReportedVideosAdapter(context,arrayListImages);
        recyclerViewReportedVideos.setAdapter(reportedVideosAdapter);
    }


    private void getFirebaseVideos()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getFirebaseVideos(context, new Interfaces.ReportedVideosListener()
        {
            @Override
            public void callback(ArrayList<VideoDataModel> arrayList)
            {
                reportedVideosAdapter.refresh(arrayList);
                CommonDialogs.getInstance().dialog.dismiss();
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
