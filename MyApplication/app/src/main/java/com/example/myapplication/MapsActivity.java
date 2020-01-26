package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.SourceTableDetails;
import com.amazonaws.services.dynamodbv2.util.Tables;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.myapplication.MessageActivity.sendSMSMessage;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /*
    public static double distanceForm(int number, double currentLat, double CurrenLongi, ){
        lat = do
        distance = Math.sqrt((latDist * latDist) + (longDist * longDist));
        return distance
    }
     */

    private GoogleMap mMap;
    public static double lats;
    public static double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //GET VALUES


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        final Button arrivedButton = (Button)findViewById(R.id.arrived_button);
        arrivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA5JHKAC45NZNEFFGV", "OR8EOGojz5k/vaIUqio3Qlx8YlauxgLitmwMxRvH");
                final AmazonSNSClient snsClient = new AmazonSNSClient(awsCreds);
                final String message = "Grant has arrived safely at his destination! Thanks for being his trusted companion :)";
                final String phoneNumber = "+19045027748";
                final Map<String, MessageAttributeValue> smsAttributes = new HashMap<String, MessageAttributeValue>();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });


    }
    public static void sendSMSMessage(AmazonSNSClient snsClient, String message, String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(result);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAYO4MKXNF6QPMVBV3", "MiuoNvqvtGE9/xpnzQhIQGbjejGiWxD9xW3ECfYJ");
        final AmazonDynamoDB ddb = new AmazonDynamoDBClient(awsCreds);
        String table_name = "Blue-Light-Locations";
        System.out.println("PAST CREDENTIALS");

        final ScanRequest scanRequest1 = new ScanRequest()
                .withTableName("Blue-Light-Locations")
                .withAttributesToGet("Blue-Light-Number", "Location");
        System.out.println("created the scan request");


        class BasicallyAThread implements Runnable {
            private volatile ScanResult result;

            @Override
            public void run() {
                result = ddb.scan(scanRequest1);
            }

            public ScanResult getValue() {
                return result;
            }
        }

        BasicallyAThread foo = new BasicallyAThread();
        Thread thread = new Thread(foo);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ScanResult result = foo.getValue();

        BlueLight[] lightArray = new BlueLight[15];

        int i = 0;
        int counter = 0;
        String number = "";
        String longi = "";
        String lat = "";
        List<String> coord = new ArrayList<>();

        for (Map<String, AttributeValue> item : result.getItems()) {
            Set<String> locations = item.keySet();
            counter = 0;
            for (String location : locations) {
                if (counter == 0) {
                    number = item.get(location).getN();
                } else if (counter == 1) {
                    coord = item.get(location).getSS();

                }
                counter++;
            }
            longi = coord.get(0);
            lat = coord.get(1);

            lightArray[i] = new BlueLight(Integer.valueOf(number), Double.valueOf(lat), Double.valueOf(longi));
            i++;
        }

        double currentShortest = 200;
        int indexOfShortest = 0;

        for (int x = 0; x < 15; x++) {
            double tempLat = lightArray[x].getLat();
            double tempLongi = lightArray[x].getLongi();
            double latDist = tempLat - 29.6; //5
            double longDist = tempLongi - 277.6; //59
            double distance = Math.sqrt((latDist * latDist) + (longDist * longDist));
            if (distance < currentShortest) {
                currentShortest = distance;
                indexOfShortest = x;
            }


        }

        int helper = 1;
        if (helper == indexOfShortest) {
            LatLng light1 = new LatLng(29.64997, 277.65287);
            mMap.addMarker(new MarkerOptions().position(light1).title("Light 1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light1));
        }
        else {
            LatLng light1 = new LatLng(29.64997, 277.65287);
            mMap.addMarker(new MarkerOptions().position(light1).title("Light 1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light1));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light2 = new LatLng(29.6504, 277.65254);
            mMap.addMarker(new MarkerOptions().position(light2).title("Light 2").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light2));
        }
        else {
            LatLng light2 = new LatLng(29.6504, 277.65254);
            mMap.addMarker(new MarkerOptions().position(light2).title("Light 2").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light2));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light3 = new LatLng(29.64967, 277.65354);
            mMap.addMarker(new MarkerOptions().position(light3).title("Light 3").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light3));
        }
        else {
            LatLng light3 = new LatLng(29.64967, 277.65354);
            mMap.addMarker(new MarkerOptions().position(light3).title("Light 3").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light3));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light4 = new LatLng(29.65036, 277.65408);
            mMap.addMarker(new MarkerOptions().position(light4).title("Light 4").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light4));
        }
        else {
            LatLng light4 = new LatLng(29.65036, 277.65408);
            mMap.addMarker(new MarkerOptions().position(light4).title("Light 4").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light4));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light5 = new LatLng(29.65021, 277.65454);
            mMap.addMarker(new MarkerOptions().position(light5).title("Light 5").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light5));
        }
        else {
            LatLng light5 = new LatLng(29.65021, 277.65454);
            mMap.addMarker(new MarkerOptions().position(light5).title("Light 5").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light5));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light6 = new LatLng(29.6516, 277.65582);
            mMap.addMarker(new MarkerOptions().position(light6).title("Light 6").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light6));
        }
        else {
            LatLng light6 = new LatLng(29.6516, 277.65582);
            mMap.addMarker(new MarkerOptions().position(light6).title("Light 6").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light6));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light7 = new LatLng(29.65137, 277.65608);
            mMap.addMarker(new MarkerOptions().position(light7).title("Light 7").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light7));
        }
        else {
            LatLng light7 = new LatLng(29.65137, 277.65608);
            mMap.addMarker(new MarkerOptions().position(light7).title("Light 7").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light7));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light8 = new LatLng(29.65035, 277.65708);
            mMap.addMarker(new MarkerOptions().position(light8).title("Light 8").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light8));
        }
        else {
            LatLng light8 = new LatLng(29.65035, 277.65708);
            mMap.addMarker(new MarkerOptions().position(light8).title("Light 8").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light8));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light9 = new LatLng(29.64873, 277.65535);
            mMap.addMarker(new MarkerOptions().position(light9).title("Light 9").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light9));
        }
        else {
            LatLng light9 = new LatLng(29.64873, 277.65535);
            mMap.addMarker(new MarkerOptions().position(light9).title("Light 9").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light9));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light10 = new LatLng(29.6513, 277.65847);
            mMap.addMarker(new MarkerOptions().position(light10).title("Light 10").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light10));
        }
        else {
            LatLng light10 = new LatLng(29.6513, 277.65847);
            mMap.addMarker(new MarkerOptions().position(light10).title("Light 10").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light10));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light11 = new LatLng(29.65194, 277.65821);
            mMap.addMarker(new MarkerOptions().position(light11).title("Light 11").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light11));
        }
        else {
            LatLng light11 = new LatLng(29.65194, 277.65821);
            mMap.addMarker(new MarkerOptions().position(light11).title("Light 11").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light11));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light12 = new LatLng(29.65069, 277.65938);
            mMap.addMarker(new MarkerOptions().position(light12).title("Light 12").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light12));
        }
        else {
            LatLng light12 = new LatLng(29.65069, 277.65938);
            mMap.addMarker(new MarkerOptions().position(light12).title("Light 12").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light12));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light13 = new LatLng(29.64971, 277.65881);
            mMap.addMarker(new MarkerOptions().position(light13).title("Light 13").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light13));
        }
        else {
            LatLng light13 = new LatLng(29.64971, 277.65881);
            mMap.addMarker(new MarkerOptions().position(light13).title("Light 13").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light13));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light14 = new LatLng(29.65099, 277.6601);
            mMap.addMarker(new MarkerOptions().position(light14).title("Light 14").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light14));
        }
        else {
            LatLng light14 = new LatLng(29.65099, 277.6601);
            mMap.addMarker(new MarkerOptions().position(light14).title("Light 14").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light14));
        }
        helper++;
        if (helper == indexOfShortest) {
            LatLng light15 = new LatLng(29.65191, 277.66037);
            mMap.addMarker(new MarkerOptions().position(light15).title("Light 15").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light15));
        }
        else {
            LatLng light15 = new LatLng(29.65191, 277.66037);
            mMap.addMarker(new MarkerOptions().position(light15).title("Light 15").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(light15));
        }
        mMap.setMinZoomPreference(15);
    }
}