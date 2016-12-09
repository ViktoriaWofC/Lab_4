package com.user.example.lab_4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Context context;
    List<PhotoMarker> photoMarkers = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();
    List<Marker> markersFree = new ArrayList<>();
    int id = -1;
    List<Travel> travels = new ArrayList<>();
    List<Polyline> polylines = new ArrayList<>();
    int idTravel = -1;
    boolean travel = false;
    EditText editTitle;
    EditText editAdress;
    EditText editTravelName;
    Button saveMarker;
    Button deleteMarker;
    Button addPhoto;
    Button addCurrentPlace;
    Button addAdressPlace;
    Button addTravel;
    Button printAll;
    Button delTravel;
    Spinner iconSpinner;
    Spinner colorSpinner;
    LatLng latLngStart;
    LatLng latLngEnd;

    AlertDialog al;
    AlertDialog.Builder ad;

    RecyclerView recyclerTravel;
    RecyclerView recyclerMarker;


    GoogleApiClient googleApiClient;
    Geocoder geocoder;
    public static final String TAG = MapsActivity.class.getSimpleName();

    LatLng myPosition;

    final int PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        setContentView(R.layout.main_activity);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        geocoder = new Geocoder(this, Locale.getDefault());

        editTitle = (EditText) findViewById(R.id.edit_title);
        editTitle.setEnabled(false);
        saveMarker = (Button) findViewById(R.id.save_marker);
        saveMarker.setEnabled(false);
        saveMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoMarkers.get(id).setTitle(editTitle.getText().toString());
                photoMarkers.get(id).setIcon(iconSpinner.getSelectedItemPosition());

                markers.get(id).setTitle(editTitle.getText().toString());
                markers.get(id).setIcon(photoMarkers.get(id).getIconBitm());
                markers.get(id).hideInfoWindow();

                recyclerMarker.getAdapter().notifyDataSetChanged();
            }
        });

        deleteMarker = (Button) findViewById(R.id.delete_marker);
        deleteMarker.setEnabled(false);
        deleteMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Travel> idd = new ArrayList<>();
                for(int i = 0; i<travels.size();i++)
                    if(travels.get(i).getLatLngStart().equals(markers.get(id).getPosition())) idd.add(travels.get(i));
                    else if(travels.get(i).getLatLngEnd().equals(markers.get(id).getPosition())) idd.add(travels.get(i));
                if(idd.size()>0){
                    for(Travel tr: idd) {
                        travels.remove(tr);
                        //polyline.remove ?
                    }
                }

                photoMarkers.remove(id);
                //markers.get(id).setVisible(false);
                markers.remove(id);
                //markers.get(id).getMarker().setVisible(false);
                //markers.remove(id);
                editTitle.setText("");
                editTitle.setEnabled(false);
                saveMarker.setEnabled(false);
                deleteMarker.setEnabled(false);
                addPhoto.setEnabled(false);
                iconSpinner.setEnabled(false);
                id = -1;

                recyclerTravel.getAdapter().notifyDataSetChanged();

                updateMap();
                recyclerMarker.getAdapter().notifyDataSetChanged();
            }
        });

        addPhoto = (Button) findViewById(R.id.add_photo);
        addPhoto.setEnabled(false);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
                startActivityForResult(intent, PHOTO);
            }
        });

        addCurrentPlace = (Button) findViewById(R.id.add_current_place);
        addCurrentPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(context,"Геолокация не включена!",Toast.LENGTH_LONG).show();
                    //return;
                }else {
                    //mMap.setMyLocationEnabled(true);

                    Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    if (location != null) {

                        //Toast.makeText(context,"location !=null",Toast.LENGTH_LONG).show();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        PhotoMarker pm;
                        MarkerOptions mo;
                        mo = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
                        pm = new PhotoMarker(mo);
                        photoMarkers.add(pm);
                        //pm.setMarkerOptions(mo);
                        markers.add(mMap.addMarker(mo));

                        myPosition = latLng;
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                        updateMap();
                        recyclerMarker.getAdapter().notifyDataSetChanged();
                    }
                    else Toast.makeText(context,"Геолокация не включена!",Toast.LENGTH_LONG).show();
                }
            }
        });

        editAdress = (EditText)findViewById(R.id.edit_adress);
        editAdress.setText("ул. Волгина, 132Б");

        addAdressPlace = (Button)findViewById(R.id.add_adress_place);
        addAdressPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = "";
                address = editAdress.getText().toString();
                if(address.length()==0){
                    Toast.makeText(context,"Введите адрес!",Toast.LENGTH_LONG).show();
                }
                else{
                    try {
                        List<Address> addrsses = null;
                        addrsses = geocoder.getFromLocationName(address,1);
                        if(addrsses.size()>0) {
                            LatLng latLng = new LatLng(addrsses.get(0).getLatitude(), addrsses.get(0).getLongitude());
                            //Toast.makeText(context, "Метка добавлена", Toast.LENGTH_LONG).show();

                            PhotoMarker pm;
                            MarkerOptions mo;
                            mo = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
                            pm = new PhotoMarker(mo);
                            photoMarkers.add(pm);
                            //pm.setMarkerOptions(mo);

                            markers.add(mMap.addMarker(mo));

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            //mMap.moveCamera(CameraUpdateFactory.zoomTo(14));//

                            updateMap();
                            recyclerMarker.getAdapter().notifyDataSetChanged();
                        }
                        else Toast.makeText(context, "Неверный адрес", Toast.LENGTH_LONG).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        iconSpinner = (Spinner) findViewById(R.id.icon_spinner);
        iconSpinner.setEnabled(false);
        ///////////////////////////////////////////////////////////////
        editTravelName = (EditText)findViewById(R.id.edit_name_travel);
        editTravelName.setText("Путешествие");

        addTravel = (Button)findViewById(R.id.add_travel);
        addTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travel = true;
                addTravel.setEnabled(false);
            }
        });

        colorSpinner = (Spinner)findViewById(R.id.color_spinner);

        recyclerTravel = (RecyclerView)findViewById(R.id.travel_recycler);
        recyclerTravel.setLayoutManager(new LinearLayoutManager(this));
        recyclerTravel.setAdapter(new TravelAdapter());
        recyclerTravel.getAdapter().notifyDataSetChanged();

        recyclerMarker = (RecyclerView)findViewById(R.id.marker_recycler);
        recyclerMarker.setLayoutManager(new LinearLayoutManager(this));
        recyclerMarker.setAdapter(new MarkerAdapter());
        getFreeMarkers();
        recyclerMarker.getAdapter().notifyDataSetChanged();

        printAll = (Button)findViewById(R.id.print_all);
        printAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMap();
                recyclerMarker.getAdapter().notifyDataSetChanged();
                delTravel.setEnabled(false);
            }
        });

        delTravel = (Button)findViewById(R.id.del_travel);
        delTravel.setEnabled(false);
        delTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                travels.remove(idTravel);
                delTravel.setEnabled(false);
                updateMap();
                recyclerTravel.getAdapter().notifyDataSetChanged();
                recyclerMarker.getAdapter().notifyDataSetChanged();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = MapsActivity.this;


    }

    public void setMarkers() {
        PhotoMarker pm;
        MarkerOptions mo;
        mo = new MarkerOptions().position(new LatLng(34, 151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
        pm = new PhotoMarker(mo);
        photoMarkers.add(pm);
        //pm.setMarkerOptions(mo);
        markers.add(mMap.addMarker(mo));

        mo = new MarkerOptions().position(new LatLng(-34, 151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
        pm = new PhotoMarker(mo);
        photoMarkers.add(pm);
        //pm.setMarkerOptions(mo);
        markers.add(mMap.addMarker(mo));

        mo = new MarkerOptions().position(new LatLng(-34, -151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
        pm = new PhotoMarker(mo);
        photoMarkers.add(pm);
        //pm.setMarkerOptions(mo);
        markers.add(mMap.addMarker(mo));

        mo = new MarkerOptions().position(new LatLng(34, -151)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
        pm = new PhotoMarker(mo);
        photoMarkers.add(pm);
        //pm.setMarkerOptions(mo);
        markers.add(mMap.addMarker(mo));

        mo = new MarkerOptions().position(new LatLng(-34, 51)).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
        pm = new PhotoMarker(mo);
        photoMarkers.add(pm);
        //pm.setMarkerOptions(mo);
        markers.add(mMap.addMarker(mo));

        photoMarkers.get(1).setIcon(2);
        photoMarkers.get(2).setIcon(4);
        markers.get(1).setIcon(photoMarkers.get(1).getIconBitm());
        markers.get(2).setIcon(photoMarkers.get(2).getIconBitm());

    }

    public void updateMap() {

        markers.clear();
        mMap.clear();
        for (PhotoMarker pm : photoMarkers)
            markers.add(mMap.addMarker(pm.getMarkerOptions()));

        polylines.clear();
        for (Travel tr : travels)
            polylines.add(mMap.addPolyline(tr.getOptions()));

        getFreeMarkers();
    }

    public void getFreeMarkers(){
        markersFree.clear();
        boolean b;
        for(int i = 0; i<markers.size();i++) {
            b = true;
            for (int j = 0; j < travels.size(); j++)
                if (travels.get(j).getLatLngStart().equals(markers.get(i).getPosition())) b = false;
                else if (travels.get(j).getLatLngEnd().equals(markers.get(i).getPosition())) b = false;

            if(b==true) markersFree.add(markers.get(i));
        }
        //Toast.makeText(context,"markersFree.size = "+markersFree.size(),Toast.LENGTH_LONG).show();
    }

    class TravelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tv;

        public TravelViewHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.text_travel_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            idTravel = this.getLayoutPosition();
            mMap.clear();

            int i1 = -1;
            int i2 = -1;

            for(int i = 0; i<markers.size();i++)
                if(markers.get(i).getPosition().equals(travels.get(idTravel).getLatLngStart())) i1 = i;
                else if(markers.get(i).getPosition().equals(travels.get(idTravel).getLatLngEnd())) i2 = i;

            mMap.addPolyline(travels.get(idTravel).getOptions());
            mMap.addMarker(photoMarkers.get(i1).getMarkerOptions());
            mMap.addMarker(photoMarkers.get(i2).getMarkerOptions());

            delTravel.setEnabled(true);
        }
    }

    class TravelAdapter extends RecyclerView.Adapter<TravelViewHolder>{

        @Override
        public TravelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayuotItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.travel_item, parent, false);
            TravelViewHolder viewHolder = new TravelViewHolder(itemLayuotItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TravelViewHolder holder, int position) {
            holder.tv.setText(travels.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return travels.size();
        }
    }

    class MarkerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tv;

        public MarkerViewHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.text_marker_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    class MarkerAdapter extends RecyclerView.Adapter<MarkerViewHolder>{

        @Override
        public MarkerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayuotItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.marker_item, parent, false);
            MarkerViewHolder viewHolder = new MarkerViewHolder(itemLayuotItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MarkerViewHolder holder, int position) {
            holder.tv.setText(markersFree.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return markersFree.size();
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

        UiSettings uis = mMap.getUiSettings();
        uis.setZoomControlsEnabled(true);



        setMarkers();

        updateMap();





        LatLng ll = new LatLng(0,0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));


        mMap.setInfoWindowAdapter(new MarkerInfoAdapter());

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                PhotoMarker pm;
                MarkerOptions mo;
                mo = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
                pm = new PhotoMarker(mo);
                photoMarkers.add(pm);
                //pm.setMarkerOptions(mo);
                markers.add(mMap.addMarker(mo));

                updateMap();
                recyclerMarker.getAdapter().notifyDataSetChanged();

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
                editTitle.setText(photoMarkers.get(id).getTitle());
                saveMarker.setEnabled(true);
                deleteMarker.setEnabled(true);
                addPhoto.setEnabled(true);
                iconSpinner.setEnabled(true);
                iconSpinner.setSelection(photoMarkers.get(id).getIconInt());
            }
        });

        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
            @Override
            public void onInfoWindowClose(Marker marker) {
                editTitle.setText("");
                editTitle.setEnabled(false);
                saveMarker.setEnabled(false);
                deleteMarker.setEnabled(false);
                addPhoto.setEnabled(false);
                iconSpinner.setEnabled(false);
                id = -1;
                //idTravel = -1;
            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG,"location connect");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"location suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        googleApiClient.connect();
    }

    class MarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.info_window_layout,null);
            TextView b = (TextView)layout.findViewById(R.id.title_text);
            ImageView iv = (ImageView)layout.findViewById(R.id.image_view);
            b.setText("!!!!");

            for(int i = 0; i< markers.size();i++)
                if(marker.getPosition().equals(markers.get(i).getPosition()))
                    id = i;

            b.setText(photoMarkers.get(id).getTitle());

            if(photoMarkers.get(id).getBitmap()!=null) {
                //Toast.makeText(context,"bitmap != null",Toast.LENGTH_LONG).show();
                iv.setImageBitmap(photoMarkers.get(id).getBitmap());//setImageURI(photoMarkers.get(id).getUri());
            }

            if(travel){
                if(latLngStart==null) latLngStart = marker.getPosition();
                else {
                    latLngEnd = marker.getPosition();

                    Travel tr;
                    //PolylineOptions po;
                    //po = new PolylineOptions().add(latLngStart).add(latLngEnd);
                    tr = new Travel(latLngStart,latLngEnd,editTravelName.getText().toString(),colorSpinner.getSelectedItemPosition());
                    travels.add(tr);
                    //pm.setMarkerOptions(mo);
                    polylines.add(mMap.addPolyline(tr.getOptions()));

                    latLngStart = null;
                    latLngEnd = null;
                    travel = false;

                    recyclerTravel.getAdapter().notifyDataSetChanged();
                    editTravelName.setText("Путешествие");
                    //marker.hideInfoWindow();

                    addTravel.setEnabled(true);
                    getFreeMarkers();
                    recyclerMarker.getAdapter().notifyDataSetChanged();
                }
            }

            return layout;

        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHOTO)
            if(resultCode == RESULT_OK)
                if(data != null){
                    //Toast.makeText(context,"save photo",Toast.LENGTH_LONG).show();
                    //Uri uri = data.getData();
                    Bitmap bitmap = data.getParcelableExtra("data");
                    //photoMarkers.get(id).setUri(uri);
                    photoMarkers.get(id).setBitmap(bitmap);
                    markers.get(id).showInfoWindow();
                }



    }
}
