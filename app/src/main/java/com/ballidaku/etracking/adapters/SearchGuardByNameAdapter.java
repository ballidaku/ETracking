package com.ballidaku.etracking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ballidaku.etracking.R;
import com.ballidaku.etracking.commonClasses.CommonMethods;
import com.ballidaku.etracking.dataModels.GuardDataModel;

import java.util.ArrayList;

/**
 * Created by sharanpalsingh on 02/10/17.
 */

public class SearchGuardByNameAdapter extends RecyclerView.Adapter<SearchGuardByNameAdapter.MyViewHolder>
{

    String TAG = "SearchGuardByNameAdapter";

    private Context context;

    private ArrayList<GuardDataModel> arrayList;


    public SearchGuardByNameAdapter(Context context, ArrayList<GuardDataModel> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }


    public void refresh(ArrayList<GuardDataModel> arrayList)
    {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @NonNull
    @Override
    public SearchGuardByNameAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_search_guard_by_name_item, parent, false);

        return new SearchGuardByNameAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchGuardByNameAdapter.MyViewHolder holder, int position)
    {

        holder.textViewBeatName.setText(arrayList.get(position).getBeatName());
        holder.textViewPhoneNumber.setText(arrayList.get(position).getBeatPhoneNumber());
        holder.textViewEmail.setText(arrayList.get(position).getBeatEmail());
        holder.textViewRange.setText(arrayList.get(position).getBeatRange());
        holder.textViewBlock.setText(arrayList.get(position).getBeatBlock());
        holder.textViewBeat.setText(arrayList.get(position).getBeatBeat());
        holder.textViewHeadquater.setText(arrayList.get(position).getBeatHeadquater());

        if(arrayList.get(position).getBeatPhoto()!=null && !arrayList.get(position).getBeatPhoto().isEmpty())
        {
            CommonMethods.getInstance().showImageGlide(context, holder.imageViewProfile, arrayList.get(position).getBeatPhoto());
        }
        else
        {
            CommonMethods.getInstance().showImageGlide(context, holder.imageViewProfile, "");
        }


    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewBeatName;
        public TextView textViewPhoneNumber;
        public TextView textViewEmail;
        public TextView textViewRange;
        public TextView textViewBlock;
        public TextView textViewBeat;
        public TextView textViewHeadquater;
        ImageView imageViewProfile;

        MyViewHolder(View view)
        {
            super(view);

            textViewBeatName = view.findViewById(R.id.textViewBeatName);
            textViewPhoneNumber = view.findViewById(R.id.textViewPhoneNumber);
            textViewEmail = view.findViewById(R.id.textViewEmail);
            textViewRange = view.findViewById(R.id.textViewRange);
            textViewBlock = view.findViewById(R.id.textViewBlock);
            textViewBeat = view.findViewById(R.id.textViewBeat);
            textViewHeadquater = view.findViewById(R.id.textViewHeadquater);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);


            textViewPhoneNumber.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    CommonMethods.getInstance().call(context, textViewPhoneNumber.getText().toString().trim());
                }
            });

        }
    }


    /*public void getBeatDateLocation(final String beatName, String beatId)
    {
        Bundle bundle=new Bundle();
        bundle.putString(MyConstant.BEAT_NAME,beatName);
        bundle.putString(MyConstant.BEAT_ID,beatId);

        GuardTrackDetailFragment guardTrackDetails=new GuardTrackDetailFragment();
        guardTrackDetails.setArguments(bundle);

        CommonMethods.getInstance().switchfragment(searchGuardByCategoryFragment,guardTrackDetails);

        searchGuardByCategoryFragment.getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }*/


}
