package com.example.distanceapp;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.example.distanceapp.TrackFragment.distance;
import static java.lang.System.arraycopy;

public class DistancePanel
{
    DecimalFormat format = new DecimalFormat("0.00");

    double lat1;
    double long1;
    double lat2;
    double long2;

    public static double totalDistance;
    static double partialDistance;
    public static double distanceLeft = 1;

    static double latArray [] = new double[2];
    static double longArray[] = new double[2];

    public static final double R = 3958.8;

    public DistancePanel()
    {
        arraycopy(latArray, 1, latArray, 0, 1);
        arraycopy(longArray, 1, longArray, 0, 1);

        totalDistance += partialDistance;
        distanceLeft -= partialDistance;

        format.setRoundingMode(RoundingMode.DOWN);

        if(totalDistance <= 1 && totalDistance != 0)
        {
            distance.setText(format.format(totalDistance) + " mile");
        }
        else
        {
            distance.setText(format.format(totalDistance) + " miles");
        }
    }

    public void coords(double latitude, double longitude)
    {

        latArray [1] = latitude;
        longArray[1] = longitude;

        if(latArray[0] != 0 && longArray[0] != 0)
        {

            lat1 = latArray[0];
            long1 = longArray[0];
            lat2 = latArray[1];
            long2 = longArray[1];

            partialDistance = haversine(lat1, long1, lat2, long2);
        }
    }

    public double haversine(double lat1, double long1, double lat2, double long2)
    {

            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(long2 - long1);

            lat1 = Math.toRadians(lat1);
            lat2 = Math.toRadians(lat2);

            double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
            double c = 2 * Math.asin(Math.sqrt(a));

            return R * c;
    }

    public static double getDistanceLeft()
    {
        return distanceLeft;
    }

}
