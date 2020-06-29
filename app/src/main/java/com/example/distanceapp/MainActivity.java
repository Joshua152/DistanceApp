package com.example.distanceapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.location.LocationManager.GPS_PROVIDER;
import static com.example.distanceapp.DistancePanel.totalDistance;

public class MainActivity extends AppCompatActivity
{
    public final static int TRACK_FRAGMENT = 0;
    public final static int SET_FRAGMENT = 1;
    public final static int PROFILE = 2;

    static Toolbar toolbar;
    static BottomNavigationView bottomNav;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    static LocationManager locationManager;
    static Location location;

    TrackFragment trackFragment;
    SetFragment setFragment;
    ProfileFragment profileFragment;

    static String provider;
    static String coordinateText;

    public static final int REQUEST_LOCATION = 1;

    int color;

    static double[] distanceArray = new double[2];
    static double latitude;
    static double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        run();
    }

    public void run()
    {
        init();

        setColors();

        startLocation();
    }

    public void init()
    {
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(itemListener);

        preferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        editor = preferences.edit();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = preferences.getString(getString(R.string.provider), GPS_PROVIDER);

        trackFragment = new TrackFragment();
        setFragment = new SetFragment();
        profileFragment = new ProfileFragment();

        coordinateText = "";

        color = preferences.getInt("color", R.color.blue);

        latitude = 0;
        longitude = 0;

        initFragment();
    }

    public void initFragment()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, trackFragment);

        if(!atTop("Track Distance"))
            transaction.addToBackStack("Track Distance");

        transaction.commit();
    }

    public void setColors()
    {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, color));
        bottomNav.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    public void startLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        try
        {
            locationManager.requestLocationUpdates(provider, 0, 2, locationListener);
        }
        catch(Exception e)
        {
            coordinateText = "No location at the moment";

            trackFragment.setCoordinates(coordinateText);
        }

        location = locationManager.getLastKnownLocation(provider);

        try {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }catch(Exception e){
            longitude = Double.NaN;
            latitude = Double.NaN;
        }

        try
        {
            location.setProvider(provider);
        }
        catch(Exception e)
        {
            coordinateText = "Provider Disabled";

            trackFragment.setCoordinates(coordinateText);
        }
    }

    public static void updateTitleAndNav(Fragment fragment)
    {
        String name = fragment.getClass().getName();

        if(name.equals(TrackFragment.class.getName()))
        {
            toolbar.setTitle("Track Distance");

            MenuItem item = bottomNav.getMenu().getItem(TRACK_FRAGMENT);
            item.setChecked(true);
        }
        else if(name.equals(SetFragment.class.getName()))
        {
            toolbar.setTitle("Set Distance");

            MenuItem item = bottomNav.getMenu().getItem(SET_FRAGMENT);
            item.setChecked(true);
        }
        else if(name.equals(ProfileFragment.class.getName()))
        {
            toolbar.setTitle("Profile");

            MenuItem item = bottomNav.getMenu().getItem(PROFILE);
            item.setChecked(true);
        }
    }

    public boolean atTop(String fragment)
    {
        FragmentManager manager = getSupportFragmentManager();

        if(manager.getBackStackEntryCount() > 0)
        {
            if(manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName().equals(fragment))
                return true;
        }

        return false;
    }

    //util method
    public void printBackStack()
    {
        FragmentManager manager = getSupportFragmentManager();

        for(int i = manager.getBackStackEntryCount() - 1; i >=0; i--)
        {
            System.out.println(manager.getBackStackEntryAt(i).getName());
        }

        System.out.println("-----------------------------------------");
    }

    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true)
    {
        @Override
        public void handleOnBackPressed()
        {
            printBackStack();

            FragmentManager manager = getSupportFragmentManager();

            if(manager.getBackStackEntryCount() > 0)
            {
                String name = manager.getBackStackEntryAt(manager.getBackStackEntryCount() - 1).getName();

                System.out.println("FRAGMENT AT TOP : " + name);

                if(name.equals("Track Distance"))
                {
                    Intent exit = new Intent(Intent.ACTION_MAIN);
                    exit.addCategory(Intent.CATEGORY_HOME);
                    exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(exit);
                }
                else
                {
                    setEnabled(false);
                    onBackPressed();
                    setEnabled(true);
                }
            }
        }
    };

    LocationListener locationListener = new LocationListener()
    {
        @Override
        public void onLocationChanged(Location location)
        {
            DistancePanel distancePanel = new DistancePanel();

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            coordinateText = "Latitude : " + latitude + "\nLongitude : " + longitude;
            setFragment.getCoordinates(latitude, longitude);

            distancePanel.coords(latitude, longitude);

            distanceArray[1] = totalDistance;

            trackFragment.setCoordinates(coordinateText);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            coordinateText = "Provider Disabled";

            trackFragment.setCoordinates(coordinateText);
        }
    };

    BottomNavigationView.OnNavigationItemSelectedListener itemListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            printBackStack();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            switch(item.getItemId())
            {
                case R.id.track_distance :
                    fragmentTransaction.replace(R.id.fragment_container, trackFragment);

                    if(!atTop("Track Distance"))
                        fragmentTransaction.addToBackStack("Track Distance");

                    fragmentTransaction.commit();

                    return true;
                case R.id.set_distance :
                    fragmentTransaction.replace(R.id.fragment_container, setFragment);

                    if(!atTop("Set Distance"))
                        fragmentTransaction.addToBackStack("Set Distance");

                    fragmentTransaction.commit();
                    return true;
                case R.id.profile :
                    fragmentTransaction.replace(R.id.fragment_container, profileFragment);

                    if(!atTop("Profile"))
                        fragmentTransaction.addToBackStack("Profile");

                    fragmentTransaction.commit();
                    return true;
            }

            return false;
        }
    };
}
