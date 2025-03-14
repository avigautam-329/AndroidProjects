package com.example.earthquakeapp.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquakeapp.Model.EarthQuake;
import com.example.earthquakeapp.Model.AppController;
import com.example.earthquakeapp.R;
import com.example.earthquakeapp.UI.CustomInfoWindow;
import com.example.earthquakeapp.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private static final int LOCAION_GRANTED = 1111;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private BitmapDescriptor[] iconColors;
    private Button showListBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        showListBtn = (Button) findViewById(R.id.showListBtn);
        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this,QuakesListActivity.class));
            }
        });

        iconColors = new BitmapDescriptor[]{
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        };

        
        getEarthQuakes();
    }

    private void getEarthQuakes() {
        final EarthQuake earthQuake = new EarthQuake();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constants.URL,
                (JSONObject) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            for(int i =0 ; i<Constants.LIMIT;++i){
                                JSONObject properties = features.getJSONObject(i).getJSONObject("properties");
                                Log.d("properties:",properties.getString("place"));
                                JSONObject geometry = features.getJSONObject(i).getJSONObject("geometry");
                                //Get Coordinates Array
                                JSONArray coordinates = geometry.getJSONArray("coordinates");

                                double lng = coordinates.getDouble(0);
                                double lat = coordinates.getDouble(1);

                                //Log.d("Position : ", lng + " , " + lat);

                                //Now we can create an Earthquake object;
                                earthQuake.setPlace(properties.getString("place"));
                                earthQuake.setType(properties.getString("type"));
                                earthQuake.setTime(properties.getLong("time"));
                                earthQuake.setMagnitude(properties.getLong("mag"));
                                earthQuake.setDetailLink(properties.getString("detail"));
                                earthQuake.setLng(lng);
                                earthQuake.setLat(lat);
                                java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
                                String dateFormatted =  dateFormat.format(new Date(Long.valueOf(properties.getLong("time"))).getTime());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.icon(iconColors[Constants.randomInt(iconColors.length,0)]);
                                markerOptions.title(earthQuake.getPlace());
                                markerOptions.position(new LatLng(earthQuake.getLat(),earthQuake.getLng()));
                                markerOptions.snippet("Magnitude: "+earthQuake.getMagnitude()+"\n"+"Date: "+dateFormatted);

                                if(earthQuake.getMagnitude() >= 2.0){
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(new LatLng(earthQuake.getLat(),earthQuake.getLng()));
                                    circleOptions.radius(30000);
                                    circleOptions.strokeWidth(3.6f);
                                    circleOptions.fillColor(Color.RED);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    mMap.addCircle(circleOptions);
                                }

                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(earthQuake.getDetailLink());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(earthQuake.getLat(),earthQuake.getLng()),4));
                                Log.d("Detail Link:", marker.getTag().toString());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
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
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCAION_GRANTED);
                return;
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                mMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                .title("Hello"));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,4));
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getQuakeDetails(marker.getTag().toString());
//        Toast.makeText(getApplicationContext(),marker.getTag().toString() ,Toast.LENGTH_SHORT).show();

    }

    public void getQuakeDetails(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,(JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String detailsUrl = "";
                try {
                    JSONObject properties = response.getJSONObject("properties");
                    JSONObject products = properties.getJSONObject("products");

                    JSONArray nearbyCities = products.getJSONArray("nearby-cities");
                    if(nearbyCities.length() != 0) {
                        for (int i = 0; i < nearbyCities.length(); ++i) {
                            JSONObject contents = nearbyCities.getJSONObject(i);
                            JSONObject nearbyJson = contents.getJSONObject("nearby-cities.json");
                            detailsUrl = nearbyJson.getString("url");

                            Log.d("URL :", detailsUrl);
                        }
                    }else{


                    }
                    Log.d("URL :", detailsUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void getMoreDetails(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                        View view = getLayoutInflater().inflate(R.layout.popup_layout,null);
                        Button dismissButton = (Button) view.findViewById(R.id.dismissPop);
                        Button dismissButtonTop = (Button) view.findViewById(R.id.dismissPopTop);
                        TextView popList = (TextView) view.findViewById(R.id.popList);
                        WebView htmlPop = (WebView) view.findViewById(R.id.htmlWebView);

                        StringBuilder stringBuilder = new StringBuilder();

                        try {
                            if(response.has("tectonicSummary") && response.getString("tectonicSummary") != null){
                                JSONObject tectonic = response.getJSONObject("tectonicSummary");
                                if(tectonic.has("text") && tectonic.getString("text") != null){
                                    String text = tectonic.getString("text");
                                    //
                                    htmlPop.loadDataWithBaseURL(null,text,"text/html","UTF-8",null);

                                }
                            }
                            JSONArray cities = response.getJSONArray("cities");
                            for(int i=0;i<cities.length();i++){
                                JSONObject citiesObj = cities.getJSONObject(i);
                                stringBuilder.append("City: "+citiesObj.getString("name")
                                + "\n"+"Distance: "+citiesObj.getString("distance")
                                +"\n"+"Population: "+citiesObj.getString("population"));

                                stringBuilder.append("\n\n");


                            }
                            popList.setText(stringBuilder);
                            dismissButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dismissButtonTop.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialogBuilder.setView(view);
                            dialog = dialogBuilder.create();
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}