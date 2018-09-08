package com.ballidaku.etracking.commonClasses;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

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


    /*public void selectBeatDialog(Context context, ArrayList<GuardDataModel> arrayList)
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



  /*  public void zoomableImageViewDialog(Context context, String path)
    {
        dialog = new Dialog(context, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_zoomable_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


        ZoomableImageView zoomableImageView = (ZoomableImageView) dialog.findViewById(R.id.imageViewZoomable);
       // ImageView zoomableImageView = (ImageView) dialog.findViewById(R.id.imageViewZoomable);


        //CommonMethods.getInstance().showImageGlide2(context,zoomableImageView,path);

        dialog.show();

    }*/


    public void showImage(Context context,String image, String title)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
        final ImageView cou_bck_iv = dialog.findViewById(R.id.cou_bck_iv);
        final ImageView imageview = dialog.findViewById(R.id.imageview);
        TextView name = dialog.findViewById(R.id.name);
        final ProgressBar progress = dialog.findViewById(R.id.progress);
        name.setText(title != null ? title : "");
        /* Glide.with(context).load(image).apply(RequestOptions.placeholderOf(R.mipmap.picture)).into(chat_iv);*/
        Glide.with(context).load(image).listener(new RequestListener<Drawable>()
        {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
            {
                progress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource)
            {
                progress.setVisibility(View.GONE);
                return false;
            }

        }).into(imageview);
        cou_bck_iv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
    }
}
