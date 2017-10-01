package com.ballidaku.etracking.frontScreens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MySharedPreference;
import com.ballidaku.etracking.mainScreens.adminScreens.activity.MainActivity;
import com.ballidaku.etracking.mainScreens.beatScreens.BeatActivity;

public class SplashActivity extends AppCompatActivity
{

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        countDownTimer.start();


    }
    CountDownTimer countDownTimer = new CountDownTimer(1000, 1000)
    {

        public void onTick(long millisUntilFinished)
        {

        }

        public void onFinish()
        {

            if(!MySharedPreference.getInstance().getUserID(context).isEmpty())
            {
                goToNextScreen(1);
            }
            else
            {
                goToNextScreen(2);
            }


        }
    };

    public void goToNextScreen(int i)
    {
        String userType =MySharedPreference.getInstance().getUserType(context);
        Intent intent = new Intent(context, i == 1 ?  userType.equals(MyConstant.ADMIN) || userType.equals(MyConstant.SUB_ADMIN) ? MainActivity.class : BeatActivity.class : LoginActivity.class);

//        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();
    }
}
