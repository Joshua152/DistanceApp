package com.example.distanceapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import static android.location.LocationManager.GPS_PROVIDER;
import static com.example.distanceapp.DistancePanel.totalDistance;
import static java.lang.System.arraycopy;

public class TrackFragment extends Fragment
{
    Context context;

    MainActivity mainActivity;
    SetFragment setActivity;
    TimerClass timerClass;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Display display;
    Point point;

    Toolbar toolbar;
    BottomNavigationView bottomNav;

    DecimalFormat format = new DecimalFormat("0.0");

    public static Button timerStart;
    public static TextView distance;
    public static TextView time;
    public static TextView speed;
    TextView coordinate;
    ImageView distanceBackground;
    ImageView speedBackground;
    ImageView timeBackground;
    ImageView distanceCircle;
    ImageView speedCircle;
    ImageView timeCircle;

    final Handler speedHandler = new Handler();

    Timer speedTimer;

    HashSet<String> hsDistance;
    HashSet<String> hsTime;
    HashSet<String> hsDate;

 //   double distanceArray[] = new double[2];

    static double mph;

    public static int sec = 0;
    public static int min = 0;
    public static int hr = 0;
    int width;
    int height;
    int backgroundHeight;
    int color;

    static String provider;

    private static boolean clicked = false;

    public TrackFragment()
    {
        point = new Point();
        speedTimer = new Timer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_track, container, false);

        MainActivity.updateTitleAndNav(this);
        context = getContext();

        run(view);

        return view;
    }

    public void run(View view)
    {
        init(view);

        setColors();

        fixViewLayouts();

        setInitialTexts();

        scheduleTimer();
    }

    public void init(View view)
    {
        mainActivity = new MainActivity();
        setActivity = new SetFragment();

        distanceBackground = (ImageView)view.findViewById(R.id.distance_background);
        speedBackground = (ImageView)view.findViewById(R.id.speed_background);
        timeBackground = (ImageView)view.findViewById(R.id.time_background);
        distanceCircle = (ImageView)view.findViewById(R.id.distance_circle);
        speedCircle = (ImageView)view.findViewById(R.id.speed_circle);
        timeCircle = (ImageView)view.findViewById(R.id.time_circle);
        coordinate = (TextView)view.findViewById(R.id.coordinate);
        distance = (TextView)view.findViewById(R.id.distance);
        time = (TextView)view.findViewById(R.id.clock);
        speed = (TextView)view.findViewById(R.id.speed);
        timerStart = (Button)view.findViewById(R.id.btn_timer);

        preferences = context.getApplicationContext().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        editor = preferences.edit();
        color = preferences.getInt("color", R.color.blue);
        provider = preferences.getString(getString(R.string.provider), GPS_PROVIDER);
        hsDistance = new HashSet<String>();
        hsTime = new HashSet<String>();
        hsDate = new HashSet<String>();

        display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(point);
        width = point.x;
        height = point.y;
        backgroundHeight = (int)(height / 7.62);

        initListeners();
    }

    public void setColors()
    {
        MainActivity.setColors(context, color);
        distanceBackground.setBackgroundColor(ContextCompat.getColor(context, color));
        speedBackground.setBackgroundColor(ContextCompat.getColor(context, color));
        timeBackground.setBackgroundColor(ContextCompat.getColor(context, color));
        distanceBackground.setBackgroundColor(ContextCompat.getColor(context, color));
        distanceCircle.setColorFilter(ContextCompat.getColor(context, color));
        speedCircle.setColorFilter(ContextCompat.getColor(context, color));
        timeCircle.setColorFilter(ContextCompat.getColor(context, color));
        coordinate.setTextColor(ContextCompat.getColor(context, color));
        distance.setTextColor(ContextCompat.getColor(context, color));
        time.setTextColor(ContextCompat.getColor(context, color));
        speed.setTextColor(ContextCompat.getColor(context, color));
    }

    public void fixViewLayouts()
    {
        distanceBackground.getLayoutParams().height = backgroundHeight;
        speedBackground.getLayoutParams().height = backgroundHeight;
        timeBackground.getLayoutParams().height = backgroundHeight;
        distanceCircle.getLayoutParams().height = backgroundHeight;
        distanceCircle.getLayoutParams().width = backgroundHeight;
        speedCircle.getLayoutParams().height = backgroundHeight;
        speedCircle.getLayoutParams().width = backgroundHeight;
        timeCircle.getLayoutParams().height = backgroundHeight;
        timeCircle.getLayoutParams().width = backgroundHeight;

        distanceCircle.setX(backgroundHeight / 2 * -1);
        speedCircle.setX(backgroundHeight / 2 * -1);
        timeCircle.setX(backgroundHeight / 2 * -1);

        timerStart.setWidth((int)(width * 0.42));
        timerStart.setHeight((int)(height * 0.078));
    }

    public void setInitialTexts()
    {
        if(mph >= 0)
            speed.setText(format.format(mph) + " mph");

        coordinate.setText("Latitude : " + MainActivity.latitude + "\nLongitude : " + MainActivity.longitude);
        distance.setText(format.format(totalDistance) + " miles");
        time.setText("0:00:00");

        if (clicked)
        {
            timerStart.setBackgroundResource(R.drawable.stop_timer);
            timerStart.setText("Stop");
        }
        else
        {
            timerStart.setBackgroundResource(R.drawable.btn_timer);
            timerStart.setText("Start");
        }
    }

    public void initListeners()
    {
        timerStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //start timer

                if(clicked == false) {
                    timerStart.setBackgroundResource(R.drawable.stop_timer);
                    timerStart.setText("Stop");
                    clicked = true;

                    sec = -1;
                    min = 0;
                    hr = 0;

                    distance.setText("0.00 miles");
                    totalDistance = 0;

                    MainActivity.distanceArray[1] = 0;
                    MainActivity.distanceArray[0] = 0;

                    mph = 0;

                    timerClass = new TimerClass();
                    timerClass.startMainTimer();
                }
                else
                {

                    //stop timer

                    timerStart.setBackgroundResource(R.drawable.btn_timer);
                    timerStart.setText("Start");
                    clicked = false;

                    TimerClass.stopMainTimer();

                    Intent completeIntent = new Intent(getActivity(), CompleteActivity.class);
                    completeIntent.putExtra(getString(R.string.end_time), time.getText().toString());
                    completeIntent.putExtra(getString(R.string.end_distance), totalDistance);
                    startActivity(completeIntent);
                }
            }
        });
    }

    public void scheduleTimer()
    {
        speedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateSpeed();
            }
        }, 0, 2000);
    }

    public void setCoordinates(String coordinates)
    {
        coordinate.setText(coordinates);
    }

    public void updateSpeed()
    {
        if (MainActivity.distanceArray[0] != 0)
        {
            double distanceDif = MainActivity.distanceArray[1] - MainActivity.distanceArray[0];
            mph = distanceDif * 1800;

            arraycopy(MainActivity.distanceArray, 1, MainActivity.distanceArray, 0, 1);
        }
        else
        {
            arraycopy(MainActivity.distanceArray, 1, MainActivity.distanceArray, 0, 1);
        }

        speedHandler.post(speedRunnable);
    }

    final Runnable speedRunnable = new Runnable(){
        public void run(){
            speed.setText(format.format(mph) + " mph");
            SetFragment.getSpeed(mph);
        }
    };
}
