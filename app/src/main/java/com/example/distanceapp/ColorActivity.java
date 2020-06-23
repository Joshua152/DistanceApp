package com.example.distanceapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

public class ColorActivity extends AppCompatActivity
{
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Toolbar toolbar;
    Display display;
    Point point;
    Button redIcon;
    Button orangeIcon;
    Button yellowIcon;
    Button greenIcon;
    Button blueIcon;
    Button purpleIcon;

    int screenWidth;
    int screenHeight;
    int buttonDimens;

    static int color;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings - Color");

        init();

        run();
    }

    public void run()
    {
        changeDimensions();
    }

    public void init()
    {
        redIcon = (Button) findViewById(R.id.red_button);
        orangeIcon = (Button) findViewById(R.id.orange_button);
        yellowIcon = (Button) findViewById(R.id.yellow_button);
        greenIcon = (Button) findViewById(R.id.green_button);
        blueIcon = (Button) findViewById(R.id.blue_button);
        purpleIcon = (Button) findViewById(R.id.purple_button);

        display = getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
        buttonDimens = (int)(screenWidth * 0.20278);

        preferences = getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        editor = preferences.edit();

        toolbar.setBackgroundColor(ContextCompat.getColor(this, preferences.getInt("color", R.color.blue)));
    }

    public void changeDimensions()
    {
        redIcon.getLayoutParams().width = redIcon.getLayoutParams().height = buttonDimens;
        orangeIcon.getLayoutParams().width = orangeIcon.getLayoutParams().height = buttonDimens;
        yellowIcon.getLayoutParams().width = yellowIcon.getLayoutParams().height = buttonDimens;
        greenIcon.getLayoutParams().width = greenIcon.getLayoutParams().height = buttonDimens;
        blueIcon.getLayoutParams().width = blueIcon.getLayoutParams().height = buttonDimens;
        purpleIcon.getLayoutParams().width = purpleIcon.getLayoutParams().height = buttonDimens;
    }

    public void colorChangeHandler(View view)
    {
       int id = view.getId();

       if(id == redIcon.getId())
           color = R.color.red;
       else if(id == orangeIcon.getId())
           color = R.color.orange;
       else if(id == yellowIcon.getId())
           color = R.color.yellow;
       else if(id == greenIcon.getId())
           color = R.color.green;
       else if(id == blueIcon.getId())
           color = R.color.blue;
       else if(id == purpleIcon.getId())
           color = R.color.purple;

       editor.putInt("color", color);
       editor.commit();
       toolbar.setBackgroundColor(ContextCompat.getColor(this, color));
    }
}
