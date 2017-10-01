package com.ballidaku.etracking.mainScreens.adminScreens.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.ballidaku.etracking.mainScreens.ProfileActivity;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.ReportedImagesFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.SearchGuardByCategoryFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    String TAG = "MainActivity";
    Context context;

    NavigationView navigationView;
    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        setUpViews();
    }

    private void setUpViews()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        setUpData();

        CommonMethods.getInstance().switchfragment(context, currentFragment = new SearchGuardByCategoryFragment());

    }

    private void setUpData()
    {
        View headerLayout = navigationView.getHeaderView(0);
        ((TextView) headerLayout.findViewById(R.id.textViewName)).setText(MySharedPreference.getInstance().getUserName(context));
        ((TextView) headerLayout.findViewById(R.id.textViewEmail)).setText(MySharedPreference.getInstance().getUserEmail(context));
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            int index = getSupportFragmentManager().getBackStackEntryCount() - 1;

            Log.e(TAG, "INDEX " + index);

            if (index == 0)
            {
                finish();
            }
            else
            {
                super.onBackPressed();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.profile:

                startActivity(new Intent(context, ProfileActivity.class));

                break;

            case R.id.signOut:

                MySharedPreference.getInstance().clearUserID(context);
                //MyApplication.getInstance().clearApplicationData();
                finish();

                break;


          /*  case R.id.select_beat:

                MyFirebase.getInstance().getAllBeats(new Interfaces.GetAllBeatListener()
                {
                    @Override
                    public void callback(ArrayList<BeatDataModel> arrayList)
                    {
                        CommonDialogs.getInstance().selectBeatDialog(context,arrayList);
                    }
                });

                break;*/

        }

        return super.onOptionsItemSelected(item);
    }


    Fragment currentFragment;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_track_beats)
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new SearchGuardByCategoryFragment());
        }
        else if (id == R.id.nav_reported_images)
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new ReportedImagesFragment());
        }
        /*else if (id == R.id.nav_share)
        {

        }
        else if (id == R.id.nav_send)
        {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

   /* public void draw(ArrayList<BeatLocationModel.DateLocation> dateLocations)
    {
        ((GuardTrackMapFragment)currentFragment).drawPath(dateLocations);
    }*/

}
