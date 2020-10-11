package com.example.earthquakeapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.earthquakeapp.Model.AppController;
import com.example.earthquakeapp.Model.EarthQuake;
import com.example.earthquakeapp.R;
import com.example.earthquakeapp.Util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuakesListActivity extends AppCompatActivity {
    private ArrayList<String> arrayList;
    private ListView listView;
    //private RequestQueue requestQueue;
    private ArrayAdapter arrayAdapter;
    private List<EarthQuake> quakeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);

        quakeList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_view);

        arrayList = new ArrayList<>();

        getAllQuakes(Constants.URL);

    }

    public void getAllQuakes(String url){
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
                                arrayList.add(earthQuake.getPlace());
                            }
                            arrayAdapter = new ArrayAdapter<>(QuakesListActivity.this,android.R.layout.simple_list_item_1,
                                    android.R.id.text1,arrayList);
                            listView.setAdapter(arrayAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            });
                            arrayAdapter.notifyDataSetChanged();
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
}