package com.ballidaku.etracking.frontScreens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    String TAG = LoginActivity.class.getSimpleName();

    Context context;

    View view;

    EditText editTextEmail;
    EditText editTextPassword;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        view = findViewById(R.id.main);
        context = this;

        setUpIds();

        mAuth = FirebaseAuth.getInstance();
    }

    private void setUpIds()
    {
//        editTextUserName=(EditText)findViewById(R.id.editTextUserName);
//        spinnerUserType=(Spinner)findViewById(R.id.spinnerUserType);
//        findViewById(R.id.buttonLogin).setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);


        findViewById(R.id.textViewSignIn).setOnClickListener(this);
        findViewById(R.id.textViewSignUp).setOnClickListener(this);
        findViewById(R.id.textViewForgotPassword).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.textViewSignIn:

                check();

                break;


            case R.id.textViewSignUp:

                Intent intent = new Intent(context, SignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);


                break;

            case R.id.textViewForgotPassword:



                CommonDialogs.getInstance().forgotPasswordDialog(context,view, new Interfaces.ForgotPasswordListener()
                {
                    @Override
                    public void callback(String result)
                    {
                        CommonMethods.getInstance().hideKeypad(LoginActivity.this);
                        CommonDialogs.getInstance().progressDialog(context);
                        mAuth.sendPasswordResetEmail(result).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                CommonDialogs.getInstance().dialog.dismiss();

                                if (task.isSuccessful()) {
                                    CommonMethods.getInstance().showToast(context, "We have sent you link to reset your password!");
                                } else {
                                    CommonMethods.getInstance().showToast(context,"Failed to send reset email!");
                                }

                            }
                        });
                    }
                });

                break;
        }
    }




    void check()
    {


        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view, context, "Please enter email.");
        }
        else if (!CommonMethods.getInstance().isValidEmail(email))
        {
            CommonMethods.getInstance().show_snackbar(view, context, "Please enter valid email.");
        }
        else if (password.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view, context, "Please enter password.");
        }
        else if (password.length() < 6)
        {
            CommonMethods.getInstance().show_snackbar(view, context, "Password must be of 6 digits.");
        }
        else
        {

            CommonMethods.getInstance().hideKeypad(this);
            CommonDialogs.getInstance().progressDialog(context);



            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            if (task.isSuccessful())
                            {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "signInWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();


                                MyFirebase.getInstance().logInUser(context, email);

                            }
                            else
                            {
                                CommonDialogs.getInstance().dialog.dismiss();

                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "createUserWithEmail:failure  ", task.getException());

                                CommonMethods.getInstance().show_snackbar(view, context, task.getException().getMessage());
                            }

                        }
                    });



        }

    }
}
