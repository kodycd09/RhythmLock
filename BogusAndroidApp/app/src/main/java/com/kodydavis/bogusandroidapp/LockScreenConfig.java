package com.kodydavis.bogusandroidapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class LockScreenConfig extends AppCompatActivity {

    final int correctRhythm = 9; //Darth Vader's Theme

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this,LockScreenService.class));
        setContentView(R.layout.lock_screen_config);
    }
}
