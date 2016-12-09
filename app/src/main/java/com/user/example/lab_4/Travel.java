package com.user.example.lab_4;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by User on 09.12.2016.
 */

public class Travel {
    private PolylineOptions options;
    private LatLng latLngStart;
    private LatLng latLngEnd;
    private String title;
    //private int color;

    public Travel(PolylineOptions options,String title, int k){
        int color = -1;
        if(k==0) color = Color.BLACK;
        else if(k==1) color = Color.GREEN;
        else if(k==2) color = Color.YELLOW;
        else if(k==3) color = Color.MAGENTA;
        else if(k==4) color = Color.RED;
        this.options = options.color(color);
        this.title = title;

    }

    public PolylineOptions getOptions(){
        return options;
    }

    public String getTitle(){
        return title;
    }

}
