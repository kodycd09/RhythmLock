package com.kodydavis.bogusandroidapp;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.graphics.Color.rgb;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class LockScreenConfig extends AppCompatActivity {

    List<Integer> activePassword = new ArrayList<>();
    List<Integer> newPassword = new ArrayList<>();
    List<Integer> confirmPassword =new ArrayList<>();
    final long halfSecond = MILLISECONDS.convert(320, MILLISECONDS); //500 is half second... feels too long

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = this.getSharedPreferences(
                "com.kodydavis.bogusandroidapp", Context.MODE_PRIVATE);

        startService(new Intent(this,LockScreenService.class));
        setContentView(R.layout.lock_screen_config);

        final TextView textSet = findViewById(R.id.textSet);
        Button setRhythmButton = findViewById(R.id.setRhythm);

        final TextView textConfirm = findViewById(R.id.textConfirm);
        final Button setConfirmButton = findViewById(R.id.confirmRhythm);

        setRhythmButton.setOnClickListener(new View.OnClickListener(){
            long lastTap = 0;
            int curNum = 0;
            List<Integer> curNewPassword = new ArrayList<>();
            @Override
            public void onClick(View v) {
                setConfirmButton.setVisibility(View.INVISIBLE);
                textConfirm.setVisibility(View.INVISIBLE);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                    if((Math.abs(currentTime() - lastTap) >= (halfSecond * 3.9))) {
                        Log.d("setPassword", "Adding last curNum = " + curNum);
                        curNewPassword.add(curNum);

                        Log.d("setPassword", "Password is " + curNewPassword.toString());
                        setNewPassword(clipPassword(curNewPassword));

                        textSet.postDelayed(new Runnable() {
                            public void run()
                            {
                                textSet.setTextColor(rgb(0,0,0));
                                textSet.setText(curNewPassword.toString());
                                curNewPassword.clear();
                                textConfirm.setText("");
                                textConfirm.setVisibility(View.VISIBLE);
                                setConfirmButton.setVisibility(View.VISIBLE);
                            }
                        },0);
                        lastTap = 0;
                    }
                    }
                }, halfSecond * 4); //320 * 5 = 1600 is 1.6s

                if (lastTap == 0) { // first tap - creates first number
                    lastTap = currentTime();
                    Log.d("setPassword", "first tap at " + lastTap);
                    curNum = 1;
                }
                else if (Math.abs(currentTime() - lastTap) < halfSecond) { //end of previous number, create new number
                    Log.d("setPassword", "curNum = " + curNum);
                    long diff = lastTap - currentTime();
                    Log.d("setPassword", "diff = " + diff);
                    Log.d("setPassword", "halfSecond = " + halfSecond);
                    lastTap = currentTime();
                    curNum++;
                }
                else {
                    Log.d("setPassword", "Adding curNum = " + curNum);
                    long diff = lastTap - currentTime();
                    lastTap = currentTime();
                    curNewPassword.add(curNum);
                    // Add zeros for each second not tapped
                    int zerosToAdd = (int)(Math.abs(lastTap - currentTime()))/1000;
                    Log.d("setPassword", "diff = " + diff);
                    Log.d("setPassword", "halfSecond = " + halfSecond);
                    Log.d("setPassword","Want to add " + zerosToAdd + " zeros");
                    //
                    curNum = 1;
                }
            }
        });

        setConfirmButton.setOnClickListener(new View.OnClickListener() {
            long lastTap = 0;
            int curNum = 0;
            List<Integer> curConfirmPassword = new ArrayList<>();

            @Override
            public void onClick(View v) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if((Math.abs(currentTime() - lastTap) >= (halfSecond * 3.9))) {
                            Log.d("setPassword", "Adding last curNum = " + curNum);
                            curConfirmPassword.add(curNum);
                            Log.d("setPassword", "Password is " + curConfirmPassword.toString());
                            setConfirmPassword(clipPassword(curConfirmPassword));

                            textSet.postDelayed(new Runnable() {
                                public void run()
                                {
                                    textConfirm.setText(curConfirmPassword.toString());
                                }
                            },0);

                            if (newPassword.equals(confirmPassword)) {
                                textSet.postDelayed(new Runnable() {
                                    public void run()
                                    {
                                        textSet.setTextColor(rgb(0,200,20));
                                        textSet.setText(getString(R.string.password_confirmed) + " " + newPassword.toString());
                                        setConfirmButton.setVisibility(View.INVISIBLE);
                                        textConfirm.setVisibility(View.INVISIBLE);
                                        textConfirm.setText("");
                                        setActivePassword(newPassword);
                                        prefs.edit().putString("activePassword", activePassword.toString()).apply();
                                        clearPasswords();
                                        lastTap = 0;
                                    }
                                },0);
                            } else {
                                textSet.postDelayed(new Runnable() {
                                    public void run()
                                    {
                                        textConfirm.setTextColor(Color.RED);
                                        textConfirm.setText(getString(R.string.wrong_password));
                                        curConfirmPassword.clear();
                                        lastTap = 0;
                                    }
                                },0);
                            }
                        }
                    }
                }, halfSecond * 4); //10000 is 10s
//                }

                long halfSecond = MILLISECONDS.convert(320, MILLISECONDS); //500 is half second... feels too long

                if (lastTap == 0) { // first tap
                    lastTap = currentTime();
                    Log.d("setPassword", "first tap at " + lastTap);
                    curNum = 1;
                } else if (Math.abs(currentTime() - lastTap) < halfSecond) {
                    Log.d("setPassword", "curNum = " + curNum);
                    long diff = lastTap - currentTime();
                    Log.d("setPassword", "diff = " + diff);
                    Log.d("setPassword", "halfSecond = " + halfSecond);
                    lastTap = currentTime();
                    curNum++;
                } else {
                    Log.d("setPassword", "Adding curNum = " + curNum);
                    long diff = lastTap - currentTime();
                    lastTap = currentTime();
                    curConfirmPassword.add(curNum);
                    // Add zeros for each second not tapped
                    int zerosToAdd = (int) (Math.abs(lastTap - currentTime())) / 1000;
                    Log.d("setPassword", "diff = " + diff);
                    Log.d("setPassword", "halfSecond = " + halfSecond);
                    Log.d("setPassword", "Want to add " + zerosToAdd + " zeros");
                    //
                    curNum = 1;
                }
            }
        });
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

    private long currentTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    private void setNewPassword(List<Integer> password) {
        newPassword.clear();
        newPassword.addAll(password);
        Log.d("tempPassword","firstPassword = " + password.toString());
    }

    private void setConfirmPassword(List<Integer> password) {
        confirmPassword.clear();
        confirmPassword.addAll(password);
        Log.d("tempPassword","confirmPassword = " + password.toString());
    }

    private void setActivePassword(List<Integer> password) {
        Log.d("tempPassword", "activePassword was = " + activePassword.toString());
        activePassword.clear();
        activePassword.addAll(password);
        Log.d("tempPassword","activePassword now = " + password.toString());
    }

    private void clearPasswords() {
        newPassword.clear();
        confirmPassword.clear();
        Log.d("tempPassword","Passwords cleared");
    }
}
