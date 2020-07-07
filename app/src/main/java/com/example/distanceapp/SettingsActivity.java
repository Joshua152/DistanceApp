package com.example.distanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity
{

    SharedPreferences preferences;
    Toolbar toolbar;
    ColorActivity colorActivity;
    MyAdapter adapter;
    ListView listView;
    String mTitle[];
    String mDescription[];
    int image[];
    int color;

    public SettingsActivity()
    {
        colorActivity = new ColorActivity();
        mTitle = new String[]{"Provider", "Color"};
        mDescription= new String[]{"Change How You Get Your Location", "Change the Colors of the App"};
        image = new int[]{R.drawable.settings_provider_asset, R.drawable.settings_color_asset};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        setColor();
    }

    public void init()
    {
        listView = findViewById(R.id.listView);

        preferences = getApplicationContext().getSharedPreferences(getString(R.string.pref_key), MODE_PRIVATE);
        color = preferences.getInt("color", R.color.blue);

        adapter = new MyAdapter(this, mTitle, mDescription, image);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(position == 0)
                {
                    Intent providerIntent = new Intent(SettingsActivity.this, ProviderActivity.class);
                    startActivity(providerIntent);

                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_out_left);
                }

                if(position == 1)
                {
                    Intent colorIntent = new Intent(SettingsActivity.this, ColorActivity.class);
                    startActivity(colorIntent);

                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_out_left);
                }
            }
        });
    }

    public void setColor()
    {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, color));
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        getSupportFragmentManager().popBackStack("Profile", 0);
        onBackPressed();
        ProfileFragment.changeColor(color);

        return true;
    }

    class MyAdapter extends ArrayAdapter<String>
    {
        Context context;
        String rTitle[];
        String rDescription[];
        int rImages[];

        public MyAdapter(Context c, String title[], String description[], int images[])
        {
            super(c, R.layout.row, R.id.main_title, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImages = images;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView title = row.findViewById(R.id.main_title);
            TextView description = row.findViewById(R.id.subtitle);

            images.setImageResource(rImages[position]);
            title.setText(rTitle[position]);
            description.setText(rDescription[position]);

            title.setTextColor(ContextCompat.getColor(getContext(), preferences.getInt("color", R.color.blue)));
            images.setColorFilter(ContextCompat.getColor(getContext(), preferences.getInt("color", R.color.blue)));

            return row;
        }
    }
}
