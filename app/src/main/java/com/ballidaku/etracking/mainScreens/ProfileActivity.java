package com.ballidaku.etracking.mainScreens;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.MySharedPreference;

public class ProfileActivity extends AppCompatActivity
{

    Context context;

    Toolbar toolbar;

    TextView textViewUserType;
    TextView textViewRange;
    TextView textViewBlock;
    TextView textViewBeat;
    TextView textViewHeadquater;

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPhone;

    LinearLayout linearLayoutBeat;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = this;

        setUpView();
    }

    private void setUpView()
    {


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        linearLayoutBeat=(LinearLayout)findViewById(R.id.linearLayoutBeat);

        ((TextView) findViewById(R.id.textViewTitle)).setText("Profile");
        ((TextView) findViewById(R.id.textViewVersion)).setVisibility(View.VISIBLE);

        textViewRange = (TextView) findViewById(R.id.textViewRange);
        textViewBlock = (TextView) findViewById(R.id.textViewBlock);
        textViewBeat = (TextView) findViewById(R.id.textViewBeat);
        textViewHeadquater = (TextView) findViewById(R.id.textViewHeadquater);

        textViewUserType = (TextView) findViewById(R.id.textViewUserType);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);

        setValues();
    }

    private void setValues()
    {

        String userType = MySharedPreference.getInstance().getUserType(context).toUpperCase();
        textViewUserType.setText(userType);
        editTextName.setText(MySharedPreference.getInstance().getUserName(context));
        editTextEmail.setText(MySharedPreference.getInstance().getUserEmail(context));
        editTextPhone.setText(MySharedPreference.getInstance().getUserPhone(context));

        editTextName.setKeyListener(null);
        editTextEmail.setKeyListener(null);
        editTextPhone.setKeyListener(null);


        if (userType.equals("BEAT"))
        {
            textViewRange.setText(MySharedPreference.getInstance().getRange(context));
            textViewBlock.setText(MySharedPreference.getInstance().getBlock(context));
            textViewBeat.setText(MySharedPreference.getInstance().getBeat(context));
            textViewHeadquater.setText(MySharedPreference.getInstance().getHeadquater(context));

        }
        else
        {
            linearLayoutBeat.setVisibility(View.GONE);
        }
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
