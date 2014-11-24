package com.chainsaw.organic.math;

import com.chainsaw.organic.MainScreen;

/**
 * Created by Chainsaw on 24-Nov-14.
 */
public class NoiseMap {
    public int[] values;
    public int width;
    public int height;

    public NoiseMap(int x, int y) {
        width = x;
        height = y;
        values = new int[x * y];
    }

    private int getMax() {
        int max = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max)
                max = values[i];
        }
        return max;
    }

    public void normalize(int brightness) {
        float ratio = (float)getMax() / brightness;
        for (int i = 0; i < values.length; i++) {
            values[i] = (int)(values[i] / ratio);
        }
    }

}
