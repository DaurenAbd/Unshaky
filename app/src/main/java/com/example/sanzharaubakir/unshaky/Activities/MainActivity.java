package com.example.sanzharaubakir.unshaky.Activities;

import android.app.Activity;
import android.os.Bundle;

import com.example.sanzharaubakir.unshaky.R;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}