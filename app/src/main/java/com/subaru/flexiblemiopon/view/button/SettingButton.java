package com.subaru.flexiblemiopon.view.button;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.subaru.flexiblemiopon.R;
import com.subaru.flexiblemiopon.util.Component;
import com.subaru.flexiblemiopon.util.Mediator;

/**
 * Created by shiny_000 on 2015/03/09.
 */
public class SettingButton extends LinearLayout implements Component {
    private String mComponentName;
    private Mediator mMediator;
    private CardView mCardView;
    private ImageView mImageView;
    private TextView mTextView;

    public SettingButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.setting_item, this);
        mCardView = (CardView) findViewById(R.id.card_view);
        mImageView = (ImageView) findViewById(R.id.setting_icon);
        mTextView = (TextView) findViewById(R.id.text_setting);
    }

    public void setComponentName(String mComponentName) {
        this.mComponentName = mComponentName;
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    @Override
    public void setMediator(Mediator mediator) {
        mMediator = mediator;
    }

    @Override
    public boolean isChecked() {
        return mMediator.isChecked(this);
    }

    @Override
    public void onClicked() {
        mMediator.onClicked(this);
    }

    @Override
    public void setChecked(boolean isClicked) {
        if (isClicked) {
            mImageView.setBackgroundColor(Color.RED);
        } else {
            mImageView.setBackgroundColor(Color.BLUE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SettingButton)) {
            return false;
        }
        SettingButton button = (SettingButton) o;
        return mComponentName.equals(button.mComponentName);
    }

    @Override
    public int hashCode() {
        return mComponentName.hashCode();
    }

    @Override
    public String toString() {
        return mComponentName;
    }
}
