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

            lightArray[i] = new BlueLight(Integer.valueOf(number), Double.valueOf(lat), Double.valueOf(longi));
            i++;
        }

        double currentShortest = 200;
        int indexOfShortest = 0;

        for (int x = 0; x < 15; x++) {
            double tempLat = lightArray[x].getLat();
            double tempLongi = lightArray[x].getLongi();
            double latDist = tempLat - 29.65;
            double longDist = tempLongi - 277.659;
            double distance = Math.sqrt((latDist * latDist) + (longDist * longDist));
            if (distance < currentShortest) {
                currentShortest = distance;
                indexOfShortest = x;
            }


        }
        /////////////////////loationaondo
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from your location manager.
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // now get the lat/lon from the location and do something with it.
        System.out.println(location.getLatitude());
        System.out.println(location.getLongitude());

        class MyLocationListener implements LocationListener {
            public void onLocationChanged(Location loc) {
                String message = String.format(
                        "New Location \n Longitude: %1$s \n Latitude: %2$s",
                        loc.getLongitude(), loc.getLatitude()
                );
            }

            public void onProviderDisabled(String arg0) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                lon = location.getLongitude();
                lats = location.getLatitude();

                System.out.println(lats);
                System.out.println(lon);
            }
        }
        Button arrivalButton = (Button)findViewById(R.id.arrived_button);
        arrivalButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(), MapsActivity.class);
                //how to pass information
                startActivity(startIntent);
            }
        });

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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from your location manager.
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.

        }
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lats = location.getLatitude();
        lon = location.getLongitude();
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(lats, lon);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}