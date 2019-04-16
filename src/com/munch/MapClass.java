package com.munch;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapClass extends MapActivity {

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.maps);
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    Bundle bundle= getIntent().getExtras();
        int lat1 = bundle.getInt("l1");
        int lng1 = bundle.getInt("l2");
        String str1= bundle.getString("name");
        String str2= bundle.getString("address");
	    mapView.setBuiltInZoomControls(true);
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
	    MapOverlay itemizedoverlay = new MapOverlay(drawable, this);
	    GeoPoint point = new GeoPoint(lat1,lng1);
	    OverlayItem overlayitem = new OverlayItem(point, str1, str2);
	    itemizedoverlay.addOverlay(overlayitem);
	    mapOverlays.add(itemizedoverlay);
	}
}
