package com.example.myapplication;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //GET VALUES


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        System.out.println("I made it boys");
        System.out.println("I made it boys PART2");


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

        System.out.println("PAST SCANREQUEST!");
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

            lightArray[i] = new BlueLight(Integer.valueOf(number),Double.valueOf(lat),Double.valueOf(longi));
            i++;
        }
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}