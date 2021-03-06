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
import com.ballidaku.etracking.adapters.ReportedImagesAdapter;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.ballidaku.etracking.dataModels.ImageDataModel;

import java.util.ArrayList;

public class ReportedImagesActivity extends AppCompatActivity
{
    Context context;

    Toolbar toolbar;

    TextView textViewTitle;

    RecyclerView recyclerViewReportedImages;
    ReportedImagesAdapter reportedImagesAdapter;

    ArrayList<ImageDataModel> arrayListImages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_images);

        context = this;

        setUpView();


        getFirebaseImages();
    }



    private void setUpView()
    {


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText("Reported Images");

        recyclerViewReportedImages=(RecyclerView)findViewById(R.id.recyclerViewReportedImages);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerViewReportedImages.setLayoutManager(layoutManager);
        recyclerViewReportedImages.setItemAnimator(new DefaultItemAnimator());

        int spanCount = 2; // 3 columns
        int spacing = 20; // 50px
        boolean includeEdge = true;

        recyclerViewReportedImages.addItemDecoration( CommonMethods.getInstance().new SpacesItemDecoration(spanCount,spacing,includeEdge));

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewReportedImages.getContext(),
//                layoutManager.getOrientation());

//        recyclerViewReportedImages.addItemDecoration(dividerItemDecoration);

        reportedImagesAdapter=new ReportedImagesAdapter(context,arrayListImages);
        recyclerViewReportedImages.setAdapter(reportedImagesAdapter);
    }


    private void getFirebaseImages()
    {
        CommonDialogs.getInstance().progressDialog(context);
        MyFirebase.getInstance().getFirebaseImages(context, new Interfaces.ReportedImagesListener()
        {
            @Override
            public void callback(ArrayList<ImageDataModel> arrayList)
            {
                reportedImagesAdapter.refresh(arrayList);
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
