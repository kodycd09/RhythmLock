package com.kodydavis.bogusandroidapp;
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
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class LockScreenConfig extends AppCompatActivity {

    List<Integer> actualPassword = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this,LockScreenService.class));
        setContentView(R.layout.lock_screen_config);

        actualPassword.add(9); //Darth Vader's Theme

        final TextView textSet = (TextView) findViewById(R.id.textSet);

        Button setRhythmButton = (Button) findViewById(R.id.setRhythm);
        setRhythmButton.setOnClickListener(new View.OnClickListener(){
            long lastTap = 0;
            int curNum = 0;
            List<Integer> password = new ArrayList<Integer>();
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
                            password.add(curNum);
                            Log.d("setPassword", "Password is " + password.toString());
                            setPassword(clipPassword(password));
                            textSet.setText(password.toString());
                            firstTap = true;
                            password.clear();
                            lastTap = 0;
                        }
                    }, 10000); //10000 is 10s
                }

                long halfSecond = MILLISECONDS.convert(400,MILLISECONDS); //500 is half second... feels too long

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
                    password.add(curNum);
                    // Add zeros for each second not tapped
                    int zerosToAdd = (int)(Math.abs(lastTap - currentTime()))/(int)(MILLISECONDS.convert(1000,SECONDS));
                    Log.d("setPassword", "diff = " + diff);
                    Log.d("setPassword", "halfSecond = " + halfSecond);
                    Log.d("setPassword","Want to add " + zerosToAdd + " zeros");
                    //
                    curNum = 1;
                }

            }

            /*
            lastTap = null;
            curNum = 0;
            password<int>[] // where
            for thirty seconds:
                onClick()
                    if lastTap == null // first tap
                        curNum = 1
                    else if lastTap - time.now < 0.5 seconds // tapped within 1/2 second
                        curNum++
                    else // tapped after 1/2 seconds from last tap or first tap
                        password.pushback(curNum);
                        curNum = 1
                    lastTap = time.now
                on each second:
                    buzz
                    if lastTap != null && lastTap - time.now > 1.0 seconds // one beat without tap after first tap
                        curNum = 0;
                        password.pushback(curNum);
            while password != empty
                if password.end == 0
			        password.remove(end) // remove trailing zeros
             */
        });
    }

    private List<Integer> clipPassword(List<Integer> password) {
        for (int i = 0; i < password.size(); i++) {
            if (password.get(password.size() - 1) == 0) {
                password.remove(password.size() - 1); // Clip trailing zeros
            }
        }
        return password;
    }

    private long currentTime() {
        return Calendar.getInstance().getTime().getTime();
    }

    private void setPassword(List<Integer> password) {
        actualPassword.clear();
        actualPassword.addAll(password);
    }
}
