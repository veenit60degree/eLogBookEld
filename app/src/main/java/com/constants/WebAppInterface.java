package com.constants;

import android.annotation.SuppressLint;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WebAppInterface {

    public WebAppInterface() {
        super();

    }
        @JavascriptInterface
    public String loadData() {
//            return "[{\"letter\": \"A\", \"frequency\": \".09\" },{\"letter\": \"B\", \"frequency\": \".05\" }]";
        return createDataSet();
    }

    @SuppressLint("RestrictedApi")
    private final String createDataSet() {
        final Random rand = new Random(System.currentTimeMillis());
        final String[] x = new String[] {
                "A", "B", "C", "D", "E", "F",
                "G", "H", "I", "J", "K", "L",
                "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X",
                "Y", "Z"};
        final List<DataPoint> set = new ArrayList<DataPoint>();
        for (int i = 0; i < x.length ; i++) {
            set.add( new DataPoint(x[i], rand.nextFloat()));
        }
        final DataPoint[] pts = set.toArray( new DataPoint[]{} );
        return new Gson().toJson(pts, DataPoint[].class );
    }
}

