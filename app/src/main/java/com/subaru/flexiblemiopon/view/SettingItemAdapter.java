package com.subaru.flexiblemiopon.view;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.subaru.flexiblemiopon.R;

import java.util.List;

/**
 * Created by shiny_000 on 2015/03/08.
 */
public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.ViewHolder> {
    private List mDataList;


    public SettingItemAdapter(List dataList) {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText("position : " + Integer.toString(position));
        holder.mCardView.setCardBackgroundColor(Color.parseColor("#bbbbbb"));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CardView cardView = (CardView) view;
                    cardView.setCardBackgroundColor(Color.parseColor("#bbbb33"));
                }
            });
            mTextView = (TextView) v.findViewById(R.id.text_setting);
        }
    }
}
