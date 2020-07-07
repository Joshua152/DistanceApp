package com.example.distanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.distanceapp.MainActivity.latitude;
import static com.example.distanceapp.MainActivity.longitude;
import static com.example.distanceapp.TrackFragment.mph;
import static com.example.distanceapp.DistancePanel.distanceLeft;

public class SetFragment extends Fragment
{
    Context context;

    SharedPreferences preferences;

    TimerClass timerClass;
    Timer checkTimer;
    Timer intentTimer;

    Display display;
    Point point;

    Handler checkHandler = new Handler();

    static DecimalFormat format = new DecimalFormat("0.00");
    static DecimalFormat speedFormat = new DecimalFormat("0.0");

    public static TextView txtDistance;
    public static TextView setCoordinates;
    public static TextView setSpeed;
    public static TextView clock;
    ImageView distanceBackground;
    ImageView speedBackground;
    ImageView timeBackground;
    ImageView distanceCircle;
    ImageView speedCircle;
    ImageView timeCircle;

    public static Button btnTimer;
    ImageButton changeDistance;
    ImageButton checkDistance;
    EditText editDistance;

    static boolean clicked;
    static boolean opened;

    static double setLatitude;
    static double setLongitude;
    static double setMph;
    static double setDistance = Double.NaN;
    static double distance;
    static String endTime;
    int backgroundHeight;
    int width;
    int height;
    int color;

    public SetFragment()
    {
        point = new Point();
        endTime = ""; // might have to initialize in field
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_set, container, false);

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

        setInitialViewProperties();

        initListeners();
    }

    public void init(View view)
    {
        distanceBackground = (ImageView)view.findViewById(R.id.set_distance_background);
        speedBackground = (ImageView)view.findViewById(R.id.set_speed_background);
        timeBackground = (ImageView)view.findViewById(R.id.set_time_background);
        distanceCircle = (ImageView)view.findViewById(R.id.distance_circle);
        speedCircle = (ImageView)view.findViewById(R.id.speed_circle);
        timeCircle = (ImageView)view.findViewById(R.id.time_circle);
        changeDistance = (ImageButton)view.findViewById(R.id.change_distance);
        editDistance = (EditText)view.findViewById(R.id.edit_distance);
        checkDistance = (ImageButton)view.findViewById(R.id.check_distance);
        txtDistance = (TextView)view.findViewById(R.id.distance);
        setCoordinates = (TextView)view.findViewById(R.id.coordinate);
        setSpeed = (TextView)view.findViewById(R.id.speed);
        clock = (TextView)view.findViewById(R.id.clock);
        btnTimer = (Button)view.findViewById(R.id.btn_timer);

        preferences = context.getApplicationContext().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        color = preferences.getInt("color", R.color.blue);

        display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(point);
        width = point.x;
        height = point.y;
        backgroundHeight = (int)(height / 7.62);

        opened = true;
        distance = 1;
    }

    public void setColors()
    {
        MainActivity.setColors(context, color);
        distanceCircle.setColorFilter(ContextCompat.getColor(context, color));
        speedCircle.setColorFilter(ContextCompat.getColor(context, color));
        timeCircle.setColorFilter(ContextCompat.getColor(context, color));
        txtDistance.setTextColor(ContextCompat.getColor(context, color));
        setCoordinates.setTextColor(ContextCompat.getColor(context, color));
        setSpeed.setTextColor(ContextCompat.getColor(context, color));
        clock.setTextColor(ContextCompat.getColor(context ,color));
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
        editDistance.getLayoutParams().width = (int) (width * 0.5);
        btnTimer.setWidth((int)(width * 0.42));
        btnTimer.setHeight((int)(height * 0.078));

        distanceCircle.setX(backgroundHeight / 2 * -1);
        speedCircle.setX(backgroundHeight / 2 * -1);
        timeCircle.setX(backgroundHeight / 2 * -1);
    }

    public void setInitialViewProperties()
    {
        btnTimer.setEnabled(true);

        editDistance.setText("1");
        setCoordinates.setText("Latitude : " + latitude + "\nLongitude : " + longitude);
        setSpeed.setText(speedFormat.format(mph) + " mph");
        clock.setText("0:00:00");

        if(distanceLeft > 0)
        {
            txtDistance.setText(format.format(distanceLeft) + " miles left");
        }
        else if(distanceLeft <= 0)
        {
            txtDistance.setText("0 miles left");
            distanceLeft = 0;

            btnTimer.setBackgroundResource(R.drawable.btn_timer);
            btnTimer.setText("Start");
            clicked = false;

            TimerClass.stopSetTimer();
        }

        if(clicked)
        {
            btnTimer.setBackgroundResource(R.drawable.stop_timer);
            btnTimer.setText("Stop");
        }
        else
        {
            btnTimer.setBackgroundResource(R.drawable.btn_timer);
            btnTimer.setText("Start");
        }
    }

    public void initListeners()
    {
        changeDistance.setOnClickListener(new View.OnClickListener()
        {
           public void onClick(View v)
           {
               //change distance
               txtDistance.setVisibility(View.INVISIBLE);
               editDistance.setVisibility(View.VISIBLE);
               changeDistance.setVisibility(View.INVISIBLE);
               checkDistance.setVisibility(View.VISIBLE);

               editDistance.requestFocus();
               editDistance.selectAll();

               InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
               inputMethodManager.showSoftInput(editDistance, InputMethodManager.SHOW_IMPLICIT);

               btnTimer.setEnabled(false);
           }
        });

        checkDistance.setOnClickListener(new View.OnClickListener()
        {
           public void onClick(View v)
           {
               try {
                   distance = Double.parseDouble(editDistance.getText().toString());

                   if (distance <= 1 && distance != 0) {
                       txtDistance.setText(format.format(distance) + " mile left");
                   } else
                       txtDistance.setText(format.format(distance) + " miles left");

                   distanceLeft = distance;
                   setDistance = distanceLeft;
               }catch(Exception e) {
                   distance = distanceLeft;

                   if (distance <= 1 && distance != 0) {
                       txtDistance.setText(format.format(distance) + " mile left");
                   } else
                       txtDistance.setText(format.format(distance) + " miles left");
               }

               //confirm distance
               changeDistance.setVisibility(View.VISIBLE);
               checkDistance.setVisibility(View.INVISIBLE);
               editDistance.setVisibility(View.INVISIBLE);
               txtDistance.setVisibility(View.VISIBLE);

               InputMethodManager mgr = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
               mgr.hideSoftInputFromWindow(editDistance.getWindowToken(), 0);

               btnTimer.setEnabled(true);
           }
        });

        btnTimer.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //start timer

                if (clicked == false) {
                    btnTimer.setBackgroundResource(R.drawable.stop_timer);
                    btnTimer.setText("Stop");
                    clicked = true;

                    distanceLeft = distance;

                    changeDistance.setVisibility(View.INVISIBLE);
                    checkDistance.setVisibility(View.INVISIBLE);

                    timerClass = new TimerClass();
                    timerClass.startSetTimer();

                    checkTimer = new Timer();

                    checkTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            check();
                        }
                    },0, 100);

                } else {

                    //stop timer

                    changeDistance.setVisibility(View.VISIBLE);

                    btnTimer.setBackgroundResource(R.drawable.btn_timer);
                    btnTimer.setText("Start");
                    clicked = false;

                    TimerClass.stopSetTimer();
                }
            }
        });
    }

    public void getCoordinates(double latitude, double longitude){
        setLatitude = latitude;
        setLongitude = longitude;

        if(opened){
            setCoordinates.setText("Latitude : " + setLatitude + "\nLongitude : " + setLongitude);
        }

        if(timerClass.startingSetTimer) {
            setDistance = DistancePanel.getDistanceLeft();
        }

        if(opened){
            if(setDistance <= 1 && setDistance != 0){
                txtDistance.setText(format.format(setDistance) + " mile left");
            }
            else if(setDistance > 1){
                txtDistance.setText(format.format(setDistance) + " miles left");
            }
        }

        if(setDistance <= 0){
            setDistance = 0;
            TimerClass.stopSetTimer();
        }
    }

    public void check() {

        if (setDistance <= 0 && opened) {
            intentTimer = new Timer();

            checkHandler.post(checkRunnable);

            checkTimer.cancel();
            checkTimer.purge();

            intentTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    intentTimer();
                }
            }, 1000, 1000);
        }
    }

    final Runnable checkRunnable = new Runnable(){
        public void run(){
            setDistance = 0;
            txtDistance.setText("0.00 miles left");
            distanceLeft = 0;

            btnTimer.setBackgroundResource(R.drawable.btn_timer);
            btnTimer.setText("Start");
            clicked = false;

            btnTimer.setEnabled(false);
        }
    };

    public void intentTimer(){
        intentTimer.cancel();
        intentTimer.purge();
        checkTimer.cancel();
        checkTimer.purge();

        Intent intent = new Intent(getActivity(), CompleteActivity.class);
        intent.putExtra(getString(R.string.end_time),clock.getText().toString());
        intent.putExtra(getString(R.string.end_distance), distanceLeft);
        startActivity(intent);
    }

    public static void getSpeed(double mph){
        setMph = mph;

        if(opened){
            setSpeed.setText(speedFormat.format(setMph) + " mph");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        btnTimer.setEnabled(true);

        new MainActivity();
    }
}
