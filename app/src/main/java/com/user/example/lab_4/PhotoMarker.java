package com.user.example.lab_4;

import android.net.Uri;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by User on 07.12.2016.
 */

public class PhotoMarker {
    private Marker marker;
    private Uri uri;
    private String title;
    private String travel;
    private int color;//BitmapDescriptorFactory.fromAsset("@android:drawable/ic_menu_edit")
    private MarkerOptions options;

    public PhotoMarker(Marker marker){
        this.marker = marker;//new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromAsset("@android:drawable/ic_menu_edit"));
        this.title = "Место N";
        this.travel = "";
        this.color = 0;
    }

    public void setMarkerOptions(MarkerOptions options){
        this.options = options;
    }

    public MarkerOptions getMarkerOptions(){
        BitmapDescriptor bdf;
        if(color==0)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.icon_menu);
        else if(color==1)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.del);
        else if(color==2)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.logo);
        else if(color==3)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.menu);
        else bdf = BitmapDescriptorFactory.fromResource(R.drawable.points);
        options.icon(bdf);
        return options;
    }

    public void setMarker(Marker marker){
        this.marker = marker;
    }

    public Marker getMarker(){
        return marker;
    }

    public void setColor(int k){
        this.color = k;
        if(k==0)
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_menu));
        else if(k==1)
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.del));
        else if(k==2)
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.logo));
        else if(k==3)
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.menu));
        else marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.points));
    }

    public int getColor(){
        return color;
    }

    public void setUri(Uri uri){
        this.uri = uri;
    }

    public Uri getUri(){
        return uri;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setTravel(String travel){
        this.travel = travel;
    }

    public String getTravel(){
        return travel;
    }

}
