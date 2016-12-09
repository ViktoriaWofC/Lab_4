package com.user.example.lab_4;

import android.graphics.Bitmap;
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
    //private Marker marker;
    private Uri uri;
    private String title;
    private String travel;
    private int icon;//BitmapDescriptorFactory.fromAsset("@android:drawable/ic_menu_edit")
    private MarkerOptions options;
    private Bitmap bitmap;

    /*public PhotoMarker(Marker marker){
        this.marker = marker;//new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromAsset("@android:drawable/ic_menu_edit"));
        this.title = "Место N";
        this.travel = "";
        this.color = 0;
    }*/

    public PhotoMarker(MarkerOptions options){
        this.options = options;//new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromAsset("@android:drawable/ic_menu_edit"));
        this.title = "Место N";
        this.travel = "";
        this.icon = 0;
        this.uri = null;
        this.bitmap = null;
    }

    public void setMarkerOptions(MarkerOptions options){
        this.options = options;
    }

    public MarkerOptions getMarkerOptions(){
        BitmapDescriptor bdf;
        if(icon==0)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.icon_del);
        else if(icon==1)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.icon_home);
        else if(icon==2)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.icon_photo);
        else if(icon==3)
            bdf = BitmapDescriptorFactory.fromResource(R.drawable.icon_bet);
        else bdf = BitmapDescriptorFactory.fromResource(R.drawable.icon_pirat);
        options.icon(bdf).title(title);
        return options;
    }

    /*public void setMarker(Marker marker){
        this.marker = marker;
    }

    public Marker getMarker(){
        return marker;
    }
    */

    public void setIcon(int k){
        this.icon = k;
        if(k==0)
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_del));
        else if(k==1)
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_home));
        else if(k==2)
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_photo));
        else if(k==3)
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bet));
        else options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pirat));
    }

    public int getIconInt(){
        return icon;
    }

    public BitmapDescriptor getIconBitm(){
        if(icon==0)
           return BitmapDescriptorFactory.fromResource(R.drawable.icon_del);
        else if(icon==1)
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_home);
        else if(icon==2)
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_photo);
        else if(icon==3)
            return BitmapDescriptorFactory.fromResource(R.drawable.icon_bet);
        else return BitmapDescriptorFactory.fromResource(R.drawable.icon_pirat);
    }

    public void setUri(Uri uri){
        this.uri = uri;
    }

    public Uri getUri(){
        return uri;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(){
        return bitmap;
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
