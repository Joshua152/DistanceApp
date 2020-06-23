package com.example.distanceapp;

import android.os.Handler;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.distanceapp.TrackFragment.mph;
import static com.example.distanceapp.TrackFragment.speed;
import static com.example.distanceapp.TrackFragment.time;
import static com.example.distanceapp.TrackFragment.timerStart;
import static com.example.distanceapp.SetFragment.clock;
import static com.example.distanceapp.SetFragment.txtDistance;


public class TimerClass
{

    DecimalFormat format = new DecimalFormat("0.0");
    DecimalFormat distanceFormat = new DecimalFormat("0.00");

    int mainMin = 0;
    int mainHr = 0;
    int mainSec = 0;

    int setMin = 0;
    int setHr = 0;
    int setSec = 0;

    static boolean startingSetTimer = false;

    Handler mainHandler = new Handler();
    Handler setHandler = new Handler();

    public static Timer mainTimer = new Timer();
    public static Timer setTimer = new Timer();

    private void updateMainTimer()
    {

        if(mainSec < 59)
        {
            mainSec++;
        }
        else if(mainMin < 59)
        {
            mainMin++;
            mainSec = 0;
        }
        else
        {
            mainHr++;
            mainSec = 0;
            mainMin = 0;
        }

        mainHandler.post(runnable);

    }

    final Runnable runnable = new Runnable() {
        public void run() {
            if(mainSec < 10)
            {
                time.setText(mainHr + ":" + mainMin + ":" + "0" + mainSec);
            }

            if(mainMin < 10)
            {
                time.setText(mainHr + ":" + "0" + mainMin + ":" + mainSec);
            }

            if(mainSec < 10 && mainMin < 10)
            {
                time.setText(mainHr + ":0" + mainMin + ":0" + mainSec);
            }

            if(mainSec >= 10 && mainMin >= 10)
            {
                time.setText(mainHr + ":" + mainMin + ":" + mainSec);
            }

            SetFragment setFragment = new SetFragment();
            speed.setText(format.format(mph) + " mph");
            setFragment.getSpeed(mph);

        }
    };

    public void startMainTimer()
    {

        mainTimer = new Timer();

        mainSec = -1;
        mainMin = 0;
        mainHr = 0;

        mainTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateMainTimer();
            }
        }, 0, 1000);

        timerStart.setBackgroundResource(R.drawable.stop_timer);
        timerStart.setText("Stop");
    }

    public static void stopMainTimer()
    {
        mainTimer.cancel();
        mainTimer.purge();

        timerStart.setBackgroundResource(R.drawable.btn_timer);
        timerStart.setText("Start");
    }

    private void updateSetTimer()
    {

        if(setSec < 59)
        {
            setSec++;
        }
        else if(setMin < 59)
        {
            setMin++;
            setSec = 0;
        }
        else
        {
            setHr++;
            setSec = 0;
            setMin = 0;
        }
        setHandler.post(setRunnable);

    }

    final Runnable setRunnable = new Runnable() {
        public void run() {
            if(setSec < 10)
            {
                clock.setText(setHr + ":" + setMin + ":" + "0" + setSec);
            }

            if(setMin < 10)
            {
                clock.setText(setHr + ":" + "0" + setMin + ":" + setSec);
            }

            if(setSec < 10 && setMin < 10)
            {
                clock.setText(setHr + ":0" + setMin + ":0" + setSec);
            }

            if(setSec >= 10 && setMin >= 10)
            {
                clock.setText(setHr + ":" + setMin + ":" + setSec);
            }

            SetFragment setFragment = new SetFragment();
            speed.setText(format.format(mph) + " mph");
            setFragment.getSpeed(mph);

        }
    };

    public boolean startSetTimer()
    {
        SetFragment.distance = DistancePanel.distanceLeft;
        txtDistance.setText(distanceFormat.format(DistancePanel.distanceLeft) + " miles left");

        setTimer = new Timer();

        setSec = -1;
        setMin = 0;
        setHr = 0;

        setTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateSetTimer();
            }
        }, 0, 1000);

        startingSetTimer = true;

        return startingSetTimer;
    }

    public static boolean stopSetTimer()
    {
        setTimer.cancel();
        setTimer.purge();

        startingSetTimer = false;

        return startingSetTimer;
    }

}
