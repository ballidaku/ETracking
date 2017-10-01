package com.ballidaku.etracking.commonClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ballidaku.etracking.R;

/**
 * Created by sharanpalsingh on 15/09/17.
 */

public class CommonDialogs
{

    public Dialog dialog;

    public static CommonDialogs instance = new CommonDialogs();

    public static CommonDialogs getInstance()
    {
        return instance;
    }



    public void forgotPasswordDialog(final Context context, final View view, final Interfaces.ForgotPasswordListener forgotPasswordListener)
    {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        final EditText editTextEmail = (EditText) dialog.findViewById(R.id.editTextEmail);

        TextView textViewNegative = (TextView) dialog.findViewById(R.id.textViewNegative);
        textViewNegative.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        TextView textViewPositive = (TextView) dialog.findViewById(R.id.textViewPositive);
        textViewPositive.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String email = editTextEmail.getText().toString().trim();

                if (email.isEmpty())
                {
                    CommonMethods.getInstance().show_snackbar(view, context, "Please enter email.");
                }
                else if (!CommonMethods.getInstance().isValidEmail(email))
                {
                    CommonMethods.getInstance().show_snackbar(view, context, "Please enter valid email.");
                }
                else
                {
                    dialog.dismiss();
                    forgotPasswordListener.callback(email);

                }

            }
        });

        dialog.show();
    }


    public void progressDialog(Context context)
    {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }


    /*public void selectBeatDialog(Context context, ArrayList<BeatDataModel> arrayList)
    {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_beat_selection);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        RecyclerView recycleViewBeat=(RecyclerView)dialog.findViewById(R.id.recycleViewBeat);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recycleViewBeat.setLayoutManager(layoutManager);
        recycleViewBeat.setItemAnimator(new DefaultItemAnimator());
        recycleViewBeat.addItemDecoration(new SimpleDividerItemDecoration(context));

        GuardsSelectionAdapter beatSelectionAdapter=new GuardsSelectionAdapter(this, context,arrayList);

        recycleViewBeat.setAdapter(beatSelectionAdapter);

        dialog.show();
    }*/

   /* public void selectBeatDateDialog(Context context,String beatName, ArrayList<BeatLocationModel> arrayList)
    {


        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_beat_date_location);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        TextView textViewTitle=(TextView)dialog.findViewById(R.id.textViewTitle);

        textViewTitle.setText(beatName);

        RecyclerView recycleViewBeatDateLocation=(RecyclerView)dialog.findViewById(R.id.recycleViewBeatDateLocation);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recycleViewBeatDateLocation.setLayoutManager(layoutManager);
        recycleViewBeatDateLocation.setItemAnimator(new DefaultItemAnimator());
        recycleViewBeatDateLocation.addItemDecoration(new SimpleDividerItemDecoration(context));

        GuardTrackDetailsAdapter beatDateLocationAdapter=new GuardTrackDetailsAdapter(this, context,arrayList);

        recycleViewBeatDateLocation.setAdapter(beatDateLocationAdapter);

        dialog.show();
    }*/



    public void zoomableImageViewDialog(Context context, String path)
    {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_zoomable_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


        ZoomableImageView zoomableImageView = (ZoomableImageView) dialog.findViewById(R.id.imageViewZoomable);
       // ImageView zoomableImageView = (ImageView) dialog.findViewById(R.id.imageViewZoomable);


        CommonMethods.getInstance().showImageGlide2(context,zoomableImageView,path);

        dialog.show();

    }
}
