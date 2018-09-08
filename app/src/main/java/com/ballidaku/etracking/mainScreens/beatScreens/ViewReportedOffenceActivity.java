package com.ballidaku.etracking.mainScreens.beatScreens;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.adapters.ReportedOffenceAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.OffenceDataModel;

import java.util.ArrayList;

public class ViewReportedOffenceActivity extends AppCompatActivity
{

    String TAG = ViewReportedOffenceActivity.class.getSimpleName();

    View view = null;

    Context context;

    Toolbar toolbar;

    TextView textViewTitle;

    RecyclerView recyclerViewReportedOffence;
    ReportedOffenceAdapter reportedOffenceAdapter;

    ArrayList<OffenceDataModel> arrayListOffence=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reported_offence);

        context = this;


        setUpViews();

        getFirebaseOffenceData();

    }


    private void setUpViews()
    {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Select Images");
//        toolbar.setTitleTextColor(ContextCompat.getColor(context,R.color.colorWhite));
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Reported Offence");


        recyclerViewReportedOffence=(RecyclerView)findViewById(R.id.recyclerViewReportedOffence);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewReportedOffence.setLayoutManager(layoutManager);
        recyclerViewReportedOffence.setItemAnimator(new DefaultItemAnimator());


//        int spanCount = 2; // 3 columns
//        int spacing = 20; // 50px
//        boolean includeEdge = true;
//
//        recyclerViewReportedOffence.addItemDecoration( CommonMethods.getInstance().new SpacesItemDecoration(spanCount,spacing,includeEdge));

        reportedOffenceAdapter=new ReportedOffenceAdapter(context,arrayListOffence);
        recyclerViewReportedOffence.setAdapter(reportedOffenceAdapter);
    }


    private void getFirebaseOffenceData()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getFirebaseOffence(context, new Interfaces.ReportedOffenceListener()
        {
            @Override
            public void callback(ArrayList<OffenceDataModel> arrayList)
            {
                reportedOffenceAdapter.refresh(arrayList);
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
