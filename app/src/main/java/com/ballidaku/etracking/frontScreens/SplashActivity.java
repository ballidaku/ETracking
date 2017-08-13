package com.ballidaku.etracking.frontScreens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.ballidaku.etracking.R;

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

            /*if(!MySharedPreference.getInstance().isLogined(context))
            {
                editTextPassword = MyDialogs.getInstance().checkPasswordDialog(context, onClickListener);
            }
            else
            {*/
                goToNextScreen();
//            }


        }
    };

    public void goToNextScreen()
    {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        finish();
    }
}
