package com.subaru.flexiblemiopon.view;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.subaru.flexiblemiopon.R;
import com.subaru.flexiblemiopon.util.Mediator;
import com.subaru.flexiblemiopon.util.SettingButton;

import java.util.List;

/**
 * Created by shiny_000 on 2015/03/08.
 */
public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.ViewHolder> {
    private Mediator mMediator;
    private List<Integer> mDataIdList;
    private Resources mRes;

    public SettingItemAdapter(Mediator mediator, List<Integer> dataIdList, Resources res) {
        mMediator = mediator;
        mDataIdList = dataIdList;
        mRes = res;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_button, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mButton.setComponentName(mRes.getString(mDataIdList.get(position)));
        mMediator.setComponent(holder.mButton);
        holder.mButton.setMediator(mMediator);
        holder.mButton.setText(mRes.getString(mDataIdList.get(position)));

        setButtonStatus(holder.mButton);

        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mButton.onClicked();
            }
        });
    }

    private void setButtonStatus(SettingButton button) {
        button.setChecked(button.isChecked());
    }

    @Override
    public int getItemCount() {
        return mDataIdList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SettingButton mButton;

        public ViewHolder(View v) {
            super(v);
            mButton = (SettingButton) v.findViewById(R.id.button_setting);

        }
    }
}
