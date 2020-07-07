package com.example.distanceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CompleteActivity extends AppCompatActivity
{
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    PrintWriter printWriter;

    Intent intentGet;
    Point point;
    Display display;

    Button backButton;
    ImageView outerCircle;
    ImageView innerCircle;

    static TextView time;
    TextView distance;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    DecimalFormat decimalFormat;

    String endTime;
    int screenWidth;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        run();
    }

    public void run()
    {
        init();

        setColors();

        changeLocations();
    }

    public void init()
    {
        backButton = (Button)findViewById(R.id.backButton);
        outerCircle = (ImageView)findViewById(R.id.outer_circle);
        innerCircle = (ImageView)findViewById(R.id.inner_circle);
        time = (TextView)findViewById(R.id.time);
        distance = (TextView)findViewById(R.id.distance);

        point = new Point();
        display = getWindowManager().getDefaultDisplay();
        display.getSize(point);
        screenWidth = point.x;

        intentGet = getIntent();
        endTime = intentGet.getStringExtra(getString(R.string.end_time));

        preferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        editor = preferences.edit();
        color = preferences.getInt("color", R.color.blue);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        decimalFormat = new DecimalFormat("0.00");

        outerCircle.getLayoutParams().width = (int) (screenWidth * 0.85);
        outerCircle.getLayoutParams().height = (int) (screenWidth * 0.85);
        innerCircle.getLayoutParams().width = (int) (screenWidth * 0.78);
        innerCircle.getLayoutParams().height = (int) (screenWidth * 0.78);

        time.setText(endTime);
        distance.setText(decimalFormat.format(intentGet.getDoubleExtra(getString(R.string.end_distance), 0)) + " miles");

        initFileIO();
    }

    public void setColors()
    {
        backButton.setBackgroundTintList(this.getResources().getColorStateList(color));
        outerCircle.setColorFilter(ContextCompat.getColor(this, color));
        time.setTextColor(ContextCompat.getColor(this, color));
    }

    public void changeLocations()
    {
        time.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        outerCircle.setY((time.getY() + (time.getHeight() / 2)) - (outerCircle.getHeight() / 2));
                        innerCircle.setY((outerCircle.getY() + outerCircle.getHeight() / 2) - (innerCircle.getHeight() / 2));
                        time.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
        );
    }

    public void onClickBack(View v)
    {
        DistancePanel.latArray = new double[2];
        DistancePanel.longArray = new double[2];

        double distance = intentGet.getDoubleExtra(getString(R.string.end_distance), 0);
        String time = intentGet.getStringExtra(getString(R.string.end_time));
        String date = dateFormat.format(calendar.getTime());

        addEntry(distance, time, date);
        closeFileIO();
        onBackPressed();
    }

    public void onClickDiscard(View v)
    {
        DistancePanel.latArray = new double[2];
        DistancePanel.longArray = new double[2];
        
        new MaterialAlertDialogBuilder(this)
                .setTitle("Discard Run")
                .setMessage("Do you want to discard this run?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        float distance = intentGet.getFloatExtra(getString(R.string.end_distance), 0.0f);
                        float totalDistance = preferences.getFloat(getString(R.string.total_distance), 0.0f);
                        editor.putFloat(getString(R.string.total_distance), distance + totalDistance);

                        Intent intent = new Intent(CompleteActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public void initFileIO()
    {
        try
        {
            new File(this.getFilesDir() + "/" + getString(R.string.statsFileIO));
            printWriter = new PrintWriter(new FileWriter(this.getFilesDir() + "/" + getString(R.string.statsFileIO), true));
        }
        catch(IOException e){e.printStackTrace();}
    }

    public void addEntry(double distance, String time, String date)
    {
        printWriter.println(decimalFormat.format(distance) + "," + time + "," + date);
    }

    public void closeFileIO()
    {
        printWriter.close();
    }
}
