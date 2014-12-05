package com.chainsaw.organic.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by Chainsaw on 05-Dec-14.
 */
public class HueSlider extends ValueSlider {

    private int[] mColorList = new int[258];
    private OnValueChangedListener mValueListener = new OnValueChangedListener() {
        @Override
        public void onValueChanged(int value) {
            HueSlider.this.setBackgroundColor(mColorList[value]);
        }
    };

    public HueSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        initValues();
        this.setOnValueChangedListener(mValueListener);
    }

    public int getColor(){
        return mColorList[getValue()];
    }


    private void initValues() {
        setShowJustBubbleIndicator(true);
        this.setMax(257);
        int index = 0;
        for (float i = 0; i < 256; i += 256 / 42) // Red (#f00) to pink
        // (#f0f)
        {
            mColorList[index] = Color.rgb(255, 0, (int) i);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Pink (#f0f) to blue
        // (#00f)
        {
            mColorList[index] = Color.rgb(255 - (int) i, 0, 255);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Blue (#00f) to light
        // blue (#0ff)
        {
            mColorList[index] = Color.rgb(0, (int) i, 255);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Light blue (#0ff) to
        // green (#0f0)
        {
            mColorList[index] = Color.rgb(0, 255, 255 - (int) i);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Green (#0f0) to yellow
        // (#ff0)
        {
            mColorList[index] = Color.rgb((int) i, 255, 0);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Yellow (#ff0) to red
        // (#f00)
        {
            mColorList[index] = Color.rgb(255, 255 - (int) i, 0);
            index++;
        }

    }


}
