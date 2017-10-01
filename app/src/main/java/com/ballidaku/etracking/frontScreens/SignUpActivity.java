package com.ballidaku.etracking.frontScreens;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonDialogs;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.commonClasses.Interfaces;
import com.ballidaku.etracking.commonClasses.MyConstant;
import com.ballidaku.etracking.commonClasses.MyFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity  implements View.OnClickListener
{

    String TAG=LoginActivity.class.getSimpleName();

    View view;

    Context context;

    Toolbar toolbar;

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPhone;
    EditText editTextPassword;
    EditText editTextConfirmPassword;

    TextView textViewHeadquater;


    Spinner spinnerUserType;
    Spinner spinnerRange;
    Spinner spinnerBlock;
    Spinner spinnerBeat;

    LinearLayout linearLayoutBeat;


    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        view = findViewById(R.id.main);
        context = this;

        mAuth = FirebaseAuth.getInstance();

        setUpIds();

        MyFirebase.getInstance().checkIsAdminExists(new Interfaces.MyListener()
        {
            @Override
            public void callback(boolean isAdminThere)
            {

                Log.e(TAG,"YES HAS ADMIN  "+ isAdminThere );


                String[] yourArray;
                if(isAdminThere)
                {
                    yourArray = new String[]{"Sub Admin", "Beat"};
                }
                else
                {
                    yourArray = new String[]{"Admin", "Sub Admin", "Beat"};
                }


                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item_view, yourArray);


                spinnerUserType.setAdapter(dataAdapter);
            }
        });
    }



    private void setUpIds()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);


        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);

        textViewHeadquater = (TextView) findViewById(R.id.textViewHeadquater);


        spinnerUserType = (Spinner) findViewById(R.id.spinnerUserType);
        spinnerRange = (Spinner) findViewById(R.id.spinnerRange);
        spinnerBlock = (Spinner) findViewById(R.id.spinnerBlock);
        spinnerBeat = (Spinner) findViewById(R.id.spinnerBeat);


        linearLayoutBeat = (LinearLayout) findViewById(R.id.linearLayoutBeat);
        linearLayoutBeat.setVisibility(View.GONE);


        findViewById(R.id.textViewContinue).setOnClickListener(this);
        findViewById(R.id.textViewSignUp).setOnClickListener(this);

       // spinnerUserType.setOnItemSelectedListener( CommonMethods.getInstance().new OnSpinnerItemSelected(spinnerUserType));
        //spinnerRange.setOnItemSelectedListener( CommonMethods.getInstance().new OnSpinnerItemSelected(spinnerRange));
        //spinnerBlock.setOnItemSelectedListener( CommonMethods.getInstance().new OnSpinnerItemSelected(spinnerBlock));


        ArrayAdapter<String> rangeAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item_view, getResources().getStringArray(R.array.rangeName));
        spinnerRange.setAdapter(rangeAdapter);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                view.setPadding(0, 0, 0,0);
                TextView textViewSpinner=(TextView)view.findViewById(R.id.textViewSpinner);
                textViewSpinner.setGravity(Gravity.RIGHT);

                String userType=spinnerUserType.getSelectedItem().toString().toLowerCase();

                if(userType.equals(MyConstant.BEAT))
                {
                    linearLayoutBeat.setVisibility(View.VISIBLE);
                }
                else
                {
                    linearLayoutBeat.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        spinnerRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                view.setPadding(0, 0, 0,0);
                TextView textViewSpinner=(TextView)view.findViewById(R.id.textViewSpinner);
                textViewSpinner.setGravity(Gravity.RIGHT);

                String rangeName=spinnerRange.getSelectedItem().toString();

                String[] range;
                if(rangeName.equals(MyConstant.NAGROTA_SURIAN))
                {
                    range=getResources().getStringArray(R.array.nagrotaBlock);
                }
                else
                {
                    range=getResources().getStringArray(R.array.dhametaBlock);
                }

                ArrayAdapter<String> blockAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item_view, range);
                spinnerBlock.setAdapter(blockAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        spinnerBlock.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                view.setPadding(0, 0, 0,0);
                TextView textViewSpinner=(TextView)view.findViewById(R.id.textViewSpinner);
                textViewSpinner.setGravity(Gravity.RIGHT);

                String blockName=spinnerBlock.getSelectedItem().toString();

                String[] beat ;
                if(blockName.equals(MyConstant.DEHRA))
                {
                    beat=getResources().getStringArray(R.array.nagrotaDehraBeat);
                }
                else if(blockName.equals(MyConstant.NAGROTA_SURIAN))
                {
                    beat=getResources().getStringArray(R.array.nagrotaNagrotaBeat);
                }
                else if(blockName.equals(MyConstant.DHAMETA))
                {
                    beat=getResources().getStringArray(R.array.dhametaDhametaBeat);
                }
                else
                {
                    beat=getResources().getStringArray(R.array.dhametaSansarpurBeat);
                }

                ArrayAdapter<String> beatAdapter = new ArrayAdapter<String>(context,R.layout.spinner_item_view, beat);
                spinnerBeat.setAdapter(beatAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });



        spinnerBeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                view.setPadding(0, 0, 0,0);
                TextView textViewSpinner=(TextView)view.findViewById(R.id.textViewSpinner);
                textViewSpinner.setGravity(Gravity.RIGHT);

                String beatName=spinnerBeat.getSelectedItem().toString();

                String headquater="";
                if(beatName.equals(MyConstant.DEHRA))
                {
                    headquater=MyConstant.DEHRA;
                }
                else if(beatName.equals(MyConstant.BHATOLI_PHAKORIAN))
                {
                    headquater=MyConstant.BHATOLI;
                }
                else if(beatName.equals(MyConstant.NAGROTA_SURIAN))
                {
                    headquater=MyConstant.NAGROTA_SURIAN;
                }
                else if(beatName.equals(MyConstant.JAWALI))
                {
                    headquater=MyConstant.LUV;
                }
                else if(beatName.equals(MyConstant.DHAMETA))
                {
                    headquater=MyConstant.DHAMETA;
                }
                else if(beatName.equals(MyConstant.PONG_DAM))
                {
                    headquater=MyConstant.KHATIYAR;
                }
                else if(beatName.equals(MyConstant.SANSARPUR_TERRACE))
                {
                    headquater=MyConstant.SANSARPUR_TERRACE;
                }
                else if(beatName.equals(MyConstant.DADASIBA))
                {
                    headquater=MyConstant.DADASIBA;
                }


                textViewHeadquater.setText(headquater);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }





    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
              case R.id.textViewContinue:

                  checkValidation();

                 break;

            case R.id.textViewSignUp:

                finish();

                break;

        }
    }


    public void checkValidation()
    {
        final String name=editTextName.getText().toString().trim();
        final String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        String confirmPassword=editTextConfirmPassword.getText().toString().trim();
        final String phoneNumber=editTextPhone.getText().toString().trim();



        if(name.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter name.");
        }
        else if(email.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter email.");
        }
        else if(!CommonMethods.getInstance().isValidEmail(email))
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter valid email.");
        }
        else if (phoneNumber.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter phone number.");
        }
        else if (!CommonMethods.getInstance().isValidMobile(phoneNumber))
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter phone number of 10 digits.");
        }
        else if (password.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter password.");
        }
        else if (password.length() < 6)
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Password must be of 6 digits.");
        }
        else if (confirmPassword.isEmpty())
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Please enter confirm password.");
        }
        else if (confirmPassword.length() < 6)
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Confirm password must be of 6 digits.");
        }
        else if (!password.equals(confirmPassword))
        {
            CommonMethods.getInstance().show_snackbar(view,context,"Password & confirm password didn't match.");
        }
        else
        {
            CommonMethods.getInstance().hideKeypad(this);
            CommonDialogs.getInstance().progressDialog(context);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "createUserWithEmail:success");
                               // FirebaseUser user = mAuth.getCurrentUser();

                                HashMap<String,Object> hashMap=new HashMap<String, Object>();
                                hashMap.put(MyConstant.USER_NAME,name);
                                hashMap.put(MyConstant.USER_EMAIL,email);
                                hashMap.put(MyConstant.USER_PHONE,phoneNumber);

                                String userType=(String)spinnerUserType.getSelectedItem();
                                if(userType.equals("Admin"))
                                {
                                    MyFirebase.getInstance().createUser(context,MyConstant.ADMIN, hashMap);
                                }
                                else if(userType.equals("Sub Admin"))
                                {
                                    MyFirebase.getInstance().createUser(context,MyConstant.SUB_ADMIN, hashMap);
                                }
                                else if(userType.equals("Beat"))
                                {
                                    hashMap.put(MyConstant.RANGE,spinnerRange.getSelectedItem());
                                    hashMap.put(MyConstant.BLOCK,spinnerBlock.getSelectedItem());
                                    hashMap.put(MyConstant.BEAT,spinnerBeat.getSelectedItem());
                                    hashMap.put(MyConstant.HEADQUATER,textViewHeadquater.getText().toString());


                                    MyFirebase.getInstance().createUser(context,MyConstant.BEAT, hashMap);
                                }
                            }
                            else
                            {
                                CommonDialogs.getInstance().dialog.dismiss();

                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure  " , task.getException());

                                CommonMethods.getInstance().show_snackbar(view,context,task.getException().getMessage());
                            }


                        }
                    });
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
