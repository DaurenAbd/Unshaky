package com.example.sanzharaubakir.unshaky.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by sanzharaubakir on 20.04.18.
 */

public class Utils {

    public static void lowPassFilter(float[] input, float[] output, float alpha) {
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
    }

    public static float rangeValue(float value, float min, float max) {
        if (value > max) {
            return max;
        }
        if (value < min) {
            return min;
        }
        return value;
    }

    public static float fixNanOrInfinite(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return 0;
        }
        return value;
    }

    public static boolean checkPermission(Context context, String permission){
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
}