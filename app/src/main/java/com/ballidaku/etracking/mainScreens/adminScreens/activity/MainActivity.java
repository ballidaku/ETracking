package com.ballidaku.etracking.mainScreens.adminScreens.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.AbsRuntimeMarshmallowPermission;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.ballidaku.etracking.frontScreens.LoginActivity;
import com.ballidaku.etracking.mainScreens.ProfileActivity;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.PostNotificationFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.ReportedImagesFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.ReportedOffenceFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.ReportedVideosFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.SearchGuardByCategoryFragment;
import com.ballidaku.etracking.mainScreens.adminScreens.fragment.SearchGuardByNameFragment;

public class MainActivity extends AbsRuntimeMarshmallowPermission implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    String TAG = "MainActivity";
    Context context;

    NavigationView navigationView;
    public Toolbar toolbar;

    ImageView imageViewProfilePic;
    TextView textViewName;
    TextView textViewEmail;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        setUpViews();

//        Log.e(TAG,"Hello I am in onCreate");

        checkIfComesFromNotification(getIntent());
    }

    private void checkIfComesFromNotification(Intent intent)
    {
        if(intent.getExtras()!= null)
        {
            String fromWhere=intent.getStringExtra(MyConstant.FROM_WHERE);

            if(fromWhere.equals(MyConstant.NOTIFICATIONS))
            {
                CommonMethods.getInstance().switchfragment(context, currentFragment = new PostNotificationFragment());
            }
        }
    }

    @Override
    public void onPermissionGranted(int requestCode)
    {
        if (requestCode == 54) // comming from the ReportedOffenceAdapter
        {
            if (currentFragment instanceof ReportedOffenceFragment)
            {
                ((ReportedOffenceFragment) currentFragment).reportedOffenceAdapter.getBitmapOfView();
            }
            else if (currentFragment instanceof ReportedImagesFragment)
            {
                ((ReportedImagesFragment) currentFragment).reportedImagesAdapter.getBitmapOfView();
            }

        }
    }

    private void setUpViews()
    {
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                  this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        setUpData();

        if(getIntent().getExtras()== null)
        CommonMethods.getInstance().switchfragment(context, currentFragment = new SearchGuardByCategoryFragment());

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        checkIfComesFromNotification(intent);
//        Log.e(TAG,"Hello I am in onNewIntent");
    }

    private void setUpData()
    {
        View headerLayout = navigationView.getHeaderView(0);

        textViewName= headerLayout.findViewById(R.id.textViewName);
        textViewEmail=headerLayout.findViewById(R.id.textViewEmail);

        imageViewProfilePic=headerLayout.findViewById(R.id.imageViewProfilePic);

        headerLayout.findViewById(R.id.linearLayoutMainHeader).setOnClickListener(this);

        drawer = findViewById(R.id.drawer_layout);

        drawer.addDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset)
            {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView)
            {
                textViewName.setText(MySharedPreference.getInstance().getUserName(context));
                textViewEmail.setText(MySharedPreference.getInstance().getUserEmail(context));

                String userPhoto=MySharedPreference.getInstance().getUserPhoto(context);
                if(!userPhoto.isEmpty())
                {
                    CommonMethods.getInstance().showImageGlide(context, imageViewProfilePic, MySharedPreference.getInstance().getUserPhoto(context));
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView)
            {

            }

            @Override
            public void onDrawerStateChanged(int newState)
            {

            }
        });


    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
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
                if ((currentFragment instanceof ReportedOffenceFragment) &&
                          (((ReportedOffenceFragment) currentFragment).reportedOffenceAdapter.showShareIcon || ((ReportedOffenceFragment) currentFragment).reportedOffenceAdapter.showDeleteIcon))
                {
                    ((ReportedOffenceFragment) currentFragment).refreshMenus();
                }
                else if ((currentFragment instanceof ReportedImagesFragment) &&
                        (((ReportedImagesFragment) currentFragment).reportedImagesAdapter.showShareIcon || ((ReportedImagesFragment) currentFragment).reportedImagesAdapter.showDeleteIcon))
                {
                    ((ReportedImagesFragment) currentFragment).refreshMenus();
                }
                else if ((currentFragment instanceof ReportedVideosFragment) &&
                        (((ReportedVideosFragment) currentFragment).reportedVideosAdapter.showShareIcon || ((ReportedVideosFragment) currentFragment).reportedVideosAdapter.showDeleteIcon))
                {
                    ((ReportedVideosFragment) currentFragment).refreshMenus();
                }
                else
                {
                    super.onBackPressed();
                }
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
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                finish();


                break;

            case R.id.share:

                if (currentFragment instanceof ReportedOffenceFragment)
                {
                    ((ReportedOffenceFragment) currentFragment).showShareOption();
                }
                else if (currentFragment instanceof ReportedImagesFragment)
                {
                    ((ReportedImagesFragment) currentFragment).showShareOption();
                }
                else if (currentFragment instanceof ReportedVideosFragment)
                {
                    ((ReportedVideosFragment) currentFragment).showShareOption();
                }

                break;

            case R.id.delete:

                if (currentFragment instanceof ReportedOffenceFragment)
                {
                    ((ReportedOffenceFragment) currentFragment).showDeleteOption();
                }
                else if (currentFragment instanceof ReportedImagesFragment)
                {
                    ((ReportedImagesFragment) currentFragment).showDeleteOption();
                }
                else if (currentFragment instanceof ReportedVideosFragment)
                {
                    ((ReportedVideosFragment) currentFragment).showDeleteOption();
                }
                break;




          /*  case R.id.select_beat:

                MyFirebase.getInstance().getAllBeats(new Interfaces.GetAllBeatListener()
                {
                    @Override
                    public void callback(ArrayList<GuardDataModel> arrayList)
                    {
                        CommonDialogs.getInstance().selectBeatDialog(context,arrayList);
                    }
                });

                break;*/

        }

        return super.onOptionsItemSelected(item);
    }


    int whichOne = 0;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();

        navigationView.getMenu().getItem(whichOne).setChecked(true);

        if (whichOne == 0 )
        {
          //  getMenuInflater().inflate(R.menu.admin_menu, menu);
        }
        else if (whichOne == 1)
        {
            getMenuInflater().inflate(R.menu.menu_search, menu);

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            searchEditText.setHintTextColor(ContextCompat.getColor(context, R.color.colorWhiteDim));
            searchEditText.setHint("Search Guard Name");


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {

                @Override
                public boolean onQueryTextSubmit(String s)
                {
                    // Log.e(TAG, "onQueryTextSubmit "+s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s)
                {
                    ((SearchGuardByNameFragment) currentFragment).filterList(s);
                    return false;
                }
            });
        }
        else if (whichOne == 3 || whichOne == 2)
        {
            getMenuInflater().inflate(R.menu.share_delete, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    public void refreshMenu(int i)
    {
        whichOne = i;
        invalidateOptionsMenu();
    }


    Fragment currentFragment;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_track_beats && !(currentFragment instanceof SearchGuardByCategoryFragment))
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new SearchGuardByCategoryFragment());
        }
        else if (id == R.id.nav_search_guard && !(currentFragment instanceof SearchGuardByNameFragment))
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new SearchGuardByNameFragment());
        }
        else if (id == R.id.nav_reported_images && !(currentFragment instanceof ReportedImagesFragment))
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new ReportedImagesFragment());
        }
        else if (id == R.id.nav_reported_videos && !(currentFragment instanceof ReportedVideosFragment))
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new ReportedVideosFragment());
        }
        else if (id == R.id.nav_reported_offence && !(currentFragment instanceof ReportedOffenceFragment))
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new ReportedOffenceFragment());
        }
        else if (id == R.id.nav_post_notification && !(currentFragment instanceof PostNotificationFragment))
        {
            CommonMethods.getInstance().switchfragment(context, currentFragment = new PostNotificationFragment());
        }
        else if (id == R.id.nav_sign_out)
        {
            MySharedPreference.getInstance().clearUserID(context);
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            finish();
        }


        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.linearLayoutMainHeader:

                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(context, ProfileActivity.class));

                break;
        }
    }
}
