package com.example.distanceapp;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.location.LocationManager.PASSIVE_PROVIDER;

public class ProviderActivity extends AppCompatActivity {

    MainActivity mainActivity;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Toolbar toolbar;

    View separator1;
    View separator2;
    TextView tvGps;
    TextView tvNetwork;
    TextView tvPassive;
    RadioButton radioGps;
    RadioButton radioNetwork;
    RadioButton radioPassive;

    int color;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Provider");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        setRadio();
    }

    public void init()
    {
        mainActivity = new MainActivity();

        preferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        editor = preferences.edit();

        color = preferences.getInt("color", R.color.blue);
        provider = preferences.getString(getString(R.string.provider), GPS_PROVIDER);

        separator1 = (View)findViewById(R.id.separator_1);
        separator2 = (View)findViewById(R.id.separator_2);
        tvGps = (TextView)findViewById(R.id.tv_gps);
        tvNetwork = (TextView)findViewById(R.id.tv_network);
        tvPassive = (TextView)findViewById(R.id.tv_passive);
        radioGps = (RadioButton)findViewById(R.id.radio_gps);
        radioNetwork = (RadioButton)findViewById(R.id.radio_network);
        radioPassive = (RadioButton)findViewById(R.id.radio_passive);

        toolbar.setBackgroundColor(ContextCompat.getColor(this, color));
        separator1.setBackgroundColor(ContextCompat.getColor(this, color));
        separator2.setBackgroundColor(ContextCompat.getColor(this, color));
        tvGps.setTextColor(ContextCompat.getColor(this, color));
        tvNetwork.setTextColor(ContextCompat.getColor(this, color));
        tvPassive.setTextColor(ContextCompat.getColor(this, color));
    }

    public void setRadio()
    {
        switch(provider)
        {
            case GPS_PROVIDER :
                radioGps.setChecked(true);
                break;
            case NETWORK_PROVIDER :
                radioNetwork.setChecked(true);
                break;
            case PASSIVE_PROVIDER :
                radioPassive.setChecked(true);
                break;
        }
    }

    public void onClickProvider(View v)
    {
        switch(v.getId())
        {
            case R.id.ll_gps :
            case R.id.radio_gps :
                radioGps.setChecked(true);
                editor.putString(getString(R.string.provider), GPS_PROVIDER);
                editor.commit();
                break;
            case R.id.ll_network :
            case R.id.radio_network :
                radioNetwork.setChecked(true);
                editor.putString(getString(R.string.provider), NETWORK_PROVIDER);
                editor.commit();
                break;
            case R.id.ll_passive :
            case R.id.radio_passive :
                radioPassive.setChecked(true);
                editor.putString(getString(R.string.provider), PASSIVE_PROVIDER);
                editor.commit();
                break;
        }
    }
}
