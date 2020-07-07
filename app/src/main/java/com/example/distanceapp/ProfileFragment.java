package com.example.distanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ProfileFragment extends Fragment
{
    static Context context;
    
    SharedPreferences preferences;
    BufferedReader reader;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    RecyclerView.Adapter recyclerViewAdapter;
    DividerItemDecoration itemDecoration;

    static ArrayList<String> distanceArray;
    static ArrayList<String> timeArray;
    static ArrayList<String> dateArray;

    int color;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        MainActivity.updateTitleAndNav(this);
        context = getContext();

        setHasOptionsMenu(true);

        init(view);

        return view;
    }

    public void init(View view)
    {
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        preferences = context.getApplicationContext().getSharedPreferences(getString(R.string.pref_key), Context.MODE_PRIVATE);
        distanceArray = new ArrayList<String>();
        timeArray = new ArrayList<String>();
        dateArray = new ArrayList<String>();

        instantiateFileIO();

        color = preferences.getInt("color", R.color.blue);
        itemDecoration = new DividerItemDecoration(context, RecyclerView.VERTICAL);
        recyclerViewLayoutManager = new LinearLayoutManager(context);
        recyclerViewAdapter = new RecyclerViewAdapter(distanceArray.toArray(new String[0]), timeArray.toArray(new String[0]),
                dateArray.toArray(new String[0]), getTotalDistance(), color, context);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public static void changeColor(int color)
    {
        MainActivity.setColors(context, color);
        RecyclerViewAdapter.setColors(context, color);
    }

    public void instantiateFileIO()
    {
        try
        {
            reader = new BufferedReader(new FileReader(context.getFilesDir() + "/" + getString(R.string.statsFileIO)));
            initArrays();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void initArrays()
    {
        try
        {
            String line = reader.readLine();

            while(line != null)
            {
                int index = 0;

                index = line.indexOf(",");
                String distance = line.substring(0, index);
                String time = line.substring(index + 1, line.indexOf(",", index + 1));
                index = line.indexOf(",", index + 1);
                String date = line.substring(index + 1);

                distanceArray.add(0, distance);
                timeArray.add(0, time);
                dateArray.add(0, date);

                line = reader.readLine();
            }
        }
        catch(IOException e){}

        try
        {
            reader.close();
        }
        catch(IOException e){}
    }

    public String getTotalDistance()
    {
        double totalDistance = 0;

        for(int i = 0; i < distanceArray.size(); i++)
            totalDistance += Double.parseDouble(distanceArray.get(i));

        return Double.toString((int)((totalDistance + 0.005) * 100) / 100.0);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_settings :
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
        }

        return false;
    }
}

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    final int TYPE_HEADER = 0;
    final int TYPE_ITEM = 1;

    String[] distanceArray;
    String[] timeArray;
    String[] dateArray;

    Context context;

    String totalDistance;

    int color;

    static class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        static TextView tvDistance;
        static TextView tvDistanceText;
        static TextView tvRunsText;

        public HeaderViewHolder(View itemView)
        {
            super(itemView);

            tvDistance = (TextView) itemView.findViewById(R.id.total_miles);
            tvDistanceText = (TextView) itemView.findViewById(R.id.total_text);
            tvRunsText = (TextView) itemView.findViewById(R.id.runs_text);
        }

        public static void changeColor(Context context, int color)
        {
            tvDistance.setTextColor(ContextCompat.getColor(context, color));
            tvRunsText.setTextColor(ContextCompat.getColor(context, color));
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvDistance;
        TextView tvTime;
        TextView tvDate;

        public ItemViewHolder(View itemView)
        {
            super(itemView);

            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

    public RecyclerViewAdapter(String[] distanceArray, String[] timeArray, String[] dateArray, String totalDistance, int color, Context context)
    {
        this.distanceArray = distanceArray;
        this.timeArray = timeArray;
        this.dateArray = dateArray;
        this.totalDistance = totalDistance;
        this.color = color;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch(viewType)
        {
            case TYPE_ITEM :
                View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_stats, parent, false);
                return new ItemViewHolder(item);
            case TYPE_HEADER :
                View header = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header_stats, parent, false);
                return new HeaderViewHolder(header);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof ItemViewHolder)
        {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            itemHolder.tvDistance.setText(distanceArray[position - 1]);
            itemHolder.tvTime.setText(timeArray[position - 1]);
            itemHolder.tvDate.setText(dateArray[position - 1]);
        }
        else if(holder instanceof HeaderViewHolder)
        {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            headerHolder.tvDistance.setText(totalDistance);

            headerHolder.tvDistance.setTextColor(ContextCompat.getColor(context, color));
            headerHolder.tvRunsText.setTextColor(ContextCompat.getColor(context, color));
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0)
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    @Override
    public int getItemCount()
    {
        return dateArray.length + 1;
    }

    public static void setColors(Context context, int color)
    {
        HeaderViewHolder.changeColor(context, color);
    }
}