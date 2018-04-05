package com.kodydavis.bogusandroidapp;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class LockScreenConfig extends AppCompatActivity {

    List<Integer> activePassword = new ArrayList<Integer>();
    List<Integer> newPassword = new ArrayList<Integer>();
    List<Integer> confirmPassword =new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this,LockScreenService.class));
        setContentView(R.layout.lock_screen_config);

        activePassword.add(9); //Darth Vader's Theme

        final TextView textSet = (TextView) findViewById(R.id.textSet);
        Button setRhythmButton = (Button) findViewById(R.id.setRhythm);

        final TextView textConfirm = (TextView) findViewById(R.id.textConfirm);
        Button setConfirmButton = (Button) findViewById(R.id.confirmRhythm);

        final Button clearCurRhythmsButton = (Button) findViewById(R.id.clearRhythms);

        setRhythmButton.setOnClickListener(new View.OnClickListener(){
            long lastTap = 0;
            int curNum = 0;
            List<Integer> curNewPassword = new ArrayList<Integer>();
            boolean firstTap = true;

            @Override
            public void onClick(View v) {
                if (firstTap) {
                    firstTap = false;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 30 seconds
                            Log.d("setPassword", "Adding last curNum = " + curNum);
                            curNewPassword.add(curNum);
                            Log.d("setPassword", "Password is " + curNewPassword.toString());
                            setNewPassword(clipPassword(curNewPassword));
                            textSet.setText(curNewPassword.toString());
                            firstTap = true;
                            curNewPassword.clear();
                            lastTap = 0;
                        }
                    }, 10000); //10000 is 10s
                }

                long halfSecond = MILLISECONDS.convert(320,MILLISECONDS); //500 is half second... feels too long

                if (lastTap == 0) { // first tap
                    lastTap = currentTime();
                    Log.d("setPassword", "first tap at " + lastTap);
                    curNum = 1;
                }
                else if (Math.abs(currentTime() - lastTap) < halfSecond) {
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
            List<Integer> curConfirmPassword = new ArrayList<Integer>();
            boolean firstTap = true;

            @Override
            public void onClick(View v) {
                if (firstTap) {
                    firstTap = false;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // this code will be executed after 30 seconds
                            Log.d("setPassword", "Adding last curNum = " + curNum);
                            curConfirmPassword.add(curNum);
                            Log.d("setPassword", "Password is " + curConfirmPassword.toString());
                            setConfirmPassword(clipPassword(curConfirmPassword));
                            textConfirm.setText(curConfirmPassword.toString());
                            firstTap = true;
                            curConfirmPassword.clear();
                            lastTap = 0;

                            if (newPassword.equals(confirmPassword)) {
                                textSet.setText("Password Confirmed");
                                setActivePassword(newPassword);
                                clearPasswords();

                                Context context = getApplicationContext();
                                CharSequence text = "SUCCESS!";
                                int duration = Toast.LENGTH_LONG;

//                                Toast toast = Toast.makeText(context, text, duration);
//                                toast.show();
                            }
                            else {
                                curConfirmPassword.clear();
                                textConfirm.setText("Retry: Password does not match");

                                Context context = getApplicationContext();
                                CharSequence text = "Passwords do not match!";
                                int duration = Toast.LENGTH_LONG;

//                                Toast toast = Toast.makeText(context, text, duration);
//                                toast.show();
                            }
                        }
                    }, 10000); //10000 is 10s
                }

                long halfSecond = MILLISECONDS.convert(320,MILLISECONDS); //500 is half second... feels too long

                if (lastTap == 0) { // first tap
                    lastTap = currentTime();
                    Log.d("setPassword", "first tap at " + lastTap);
                    curNum = 1;
                }
                else if (Math.abs(currentTime() - lastTap) < halfSecond) {
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
                    curConfirmPassword.add(curNum);
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

        clearCurRhythmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                    clearPasswords();
                    textSet.setText("");
                    textConfirm.setText("");
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
