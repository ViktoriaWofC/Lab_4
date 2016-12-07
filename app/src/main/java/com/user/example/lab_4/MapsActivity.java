package com.user.example.lab_4;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context;
    List<PhotoMarker> markers = new ArrayList<>();
    int id = -1;
    EditText editTitle;
    Button saveMarker;
    Button deleteMarker;
    Spinner colorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        setContentView(R.layout.main_activity);


        editTitle = (EditText)findViewById(R.id.edit_title);
        editTitle.setEnabled(false);
        saveMarker = (Button)findViewById(R.id.save_marker);
        saveMarker.setEnabled(false);
        saveMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markers.get(id).setTitle(editTitle.getText().toString());
                markers.get(id).setColor(colorSpinner.getSelectedItemPosition());
                markers.get(id).getMarker().hideInfoWindow();
            }
        });

        deleteMarker = (Button)findViewById(R.id.delete_marker);
        deleteMarker.setEnabled(false);
        deleteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markers.get(id).getMarker().setVisible(false);
                //markers.remove(id);
                editTitle.setText("");
                editTitle.setEnabled(false);
                saveMarker.setEnabled(false);
                deleteMarker.setEnabled(false);
                colorSpinner.setEnabled(false);
                id = -1;
                //updateMap();
            }
        });

        colorSpinner = (Spinner)findViewById(R.id.color_spinner);
        colorSpinner.setEnabled(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = MapsActivity.this;
    }

    public void setMarkers(){
        PhotoMarker pm;
        MarkerOptions mo;
        mo = new MarkerOptions().position(new LatLng(34, 151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_menu));
        pm = new PhotoMarker(mMap.addMarker(mo));
        pm.setMarkerOptions(mo);
        markers.add(pm);

        mo = new MarkerOptions().position(new LatLng(34, 151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_menu));
        pm = new PhotoMarker(mMap.addMarker(mo));
        pm.setMarkerOptions(mo);
        markers.add(pm);

        mo = new MarkerOptions().position(new LatLng(-34, -151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_menu));
        pm = new PhotoMarker(mMap.addMarker(mo));
        pm.setMarkerOptions(mo);
        markers.add(pm);

        mo = new MarkerOptions().position(new LatLng(34, -151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_menu));
        pm = new PhotoMarker(mMap.addMarker(mo));
        pm.setMarkerOptions(mo);
        markers.add(pm);

        mo = new MarkerOptions().position(new LatLng(-34, 51)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_menu));
        pm = new PhotoMarker(mMap.addMarker(mo));
        pm.setMarkerOptions(mo);
        markers.add(pm);

        markers.get(1).setColor(2);
        markers.get(2).setColor(4);

    }

    public void updateMap(){

        //mMap.clear();
        for(PhotoMarker pm: markers)
            mMap.addMarker(pm.getMarkerOptions());

        //add all travel

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

        UiSettings uis = mMap.getUiSettings();
        //uis.setMapToolbarEnabled(true);
        //UiSettings.setMapToolbarEnabled(true);k

        // Add a marker in Sydney and move the camera

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        setMarkers();

        //for(PhotoMarker pm: markers)
        //    mMap.addMarker(pm.getMarker());


        LatLng ll = new LatLng(0,0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));

        mMap.setInfoWindowAdapter(new MarkerInfoAdapter());

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                PhotoMarker pm;
                MarkerOptions mo;
                mo = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_menu));
                pm = new PhotoMarker(mMap.addMarker(mo));
                pm.setMarkerOptions(mo);
                markers.add(pm);

                /*markers.add(new PhotoMarker(new MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))));
                mMap.addMarker(markers.get(markers.size()-1).getMarker());
                */
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                editTitle.setEnabled(true);
                editTitle.setText(markers.get(id).getTitle());
                saveMarker.setEnabled(true);
                deleteMarker.setEnabled(true);
                colorSpinner.setEnabled(true);
                colorSpinner.setSelection(markers.get(id).getColor());
                //Toast.makeText(MapsActivity.this, "open", Toast.LENGTH_LONG).show();

            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                editTitle.setText("");
                editTitle.setEnabled(false);
                saveMarker.setEnabled(false);
                deleteMarker.setEnabled(false);
                colorSpinner.setEnabled(false);
                id = -1;
            }
        });


    }

    class MarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.info_window_layout,null);
            TextView b = (TextView)layout.findViewById(R.id.title_text);
            b.setText("!!!!");

            for(int i = 0; i< markers.size();i++)
                if(marker.getPosition().equals(markers.get(i).getMarker().getPosition()))
                    id = i;

            b.setText(markers.get(id).getTitle());

            return layout;

        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }


}
