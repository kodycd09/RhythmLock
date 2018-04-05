package com.kodydavis.bogusandroidapp;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class LockScreen extends AppCompatActivity {

    private String correctRhythm = "1"; //Darth Vader's Theme
    final long halfSecond = MILLISECONDS.convert(320, MILLISECONDS); //500 is half second... feels too long

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = this.getSharedPreferences(
                "com.kodydavis.bogusandroidapp", Context.MODE_PRIVATE);

        makeFullScreen();
        startService(new Intent(this,LockScreenService.class));
        setContentView(R.layout.activity_main);

        String activePassword = prefs.getString("activePassword",null);
        Log.d("lockScreen","activePassword = " + activePassword);
        Log.d("lockScreen", "correctRhythm = " + correctRhythm);

        if (activePassword != null){
            correctRhythm = activePassword;
            Log.d("lockScreen", "correctRhythm changed to " + correctRhythm);
        }

        final Button lockScreenButton = findViewById(R.id.lockScreenButton);
        lockScreenButton.setOnClickListener(new View.OnClickListener(){
            long lastTap = 0;
            int curNum = 0;
            List<Integer> enteredPassword = new ArrayList<>();

            @Override
            public void onClick(final View v) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if((Math.abs(currentTime() - lastTap) >= (halfSecond * 3.9))) {
                            Log.d("attemptPassword", "Adding last curNum = " + curNum);
                            enteredPassword.add(curNum);
                            Log.d("attemptPassword", "Password is " + enteredPassword.toString());
                            if ((clipPassword(enteredPassword)).toString().equals(prefs.getString("activePassword", null)) || prefs.getString("activePassword", null) == null || (clipPassword(enteredPassword)).toString().equals(correctRhythm)) {
                                unlockScreen(v.getRootView());
                            } else {
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                    lockScreenButton.setText(getString(R.string.wrong_password));
                                    //lockScreenButton.setText("Correct Answer is: " + curPassword);
                                    }
                                }, halfSecond * 10);

                            }
                            enteredPassword.clear();
                            lastTap = 0;
                        }
                    }
                }, halfSecond * 4); //10000 is 10s
                long halfSecond = MILLISECONDS.convert(320,MILLISECONDS); //500 is half second... feels too long

                if (lastTap == 0) { // first tap
                    lastTap = currentTime();
                    Log.d("attemptPassword", "first tap at " + lastTap);
                    curNum = 1;
                }
                else if (Math.abs(currentTime() - lastTap) < halfSecond) {
                    Log.d("attemptPassword", "curNum = " + curNum);
                    long diff = lastTap - currentTime();
                    Log.d("attemptPassword", "diff = " + diff);
                    Log.d("attemptPassword", "halfSecond = " + halfSecond);
                    lastTap = currentTime();
                    curNum++;
                }
                else {
                    Log.d("attemptPassword", "Adding curNum = " + curNum);
                    long diff = lastTap - currentTime();
                    lastTap = currentTime();
                    enteredPassword.add(curNum);
                    // Add zeros for each second not tapped
                    int zerosToAdd = (int)(Math.abs(lastTap - currentTime()))/1000;
                    Log.d("attemptPassword", "diff = " + diff);
                    Log.d("attemptPassword", "halfSecond = " + halfSecond);
                    Log.d("attemptPassword","Want to add " + zerosToAdd + " zeros");
                    //
                    curNum = 1;
                }
            }
        });
    }

    private long currentTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    private List<Integer> clipPassword(List<Integer> password) {
        for (int i = 0; i < password.size() - 1; i++) {
            if (password.get(password.size() - 1) == 0) {
                password.remove(password.size() - 1); // Clip trailing zeros
            }
            else {
                break;
            }
        }
        return password;
    }

    /**
     * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
     *   the Actionbar and the virtual keys (if they are on the phone)
     */
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onBackPressed() {
        Log.d("Buttons", ": Back button has been clicked");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            windowCloseHandler.postDelayed(windowCloserRunnable, 0);
        }
    }

    private void toggleRecents() {
        Intent closeRecents = new Intent("com.android.systemui.recent.action.TOGGLE_RECENTS");
        closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ComponentName recents = new ComponentName("com.android.systemui", "com.android.systemui.recent.RecentsActivity");
        closeRecents.setComponent(recents);
        this.startActivity(closeRecents);
    }

    private Handler windowCloseHandler = new Handler();

    private Runnable windowCloserRunnable = new Runnable() {
        @Override
        public void run() {
            ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            assert am != null;
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

            if (cn != null && cn.getClassName().equals("com.android.systemui.recent.RecentsActivity")) {
                toggleRecents();
            }
        }
    };

    public void unlockScreen(View view) {
        //Instead of using finish(), this totally destroys the process
        Log.d("unlock","killing Process " + android.os.Process.myPid());
        //android.os.Process.killProcess(android.os.Process.myPid());
        finish();

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
