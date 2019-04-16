package com.munch;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class update_db extends Activity{
	EditText e1, e2, e3, e4, e5, e6;
	TextView t;
	Intent home;
	DBAdapter db = new DBAdapter(this);
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.updat);
        e1=(EditText)findViewById(R.id.name);
        e2=(EditText)findViewById(R.id.spec);
        e3=(EditText)findViewById(R.id.addr1);
        e4=(EditText)findViewById(R.id.addr2);
        e5=(EditText)findViewById(R.id.city);
        e6=(EditText)findViewById(R.id.cntct);
        t=(TextView)findViewById(R.id.msg);
    }
    
    public void onSubmit(View v) {
    	
    	if(e1.getText().toString().length()==0)
    	    e1.setError("Required field");
    	else if(e2.getText().toString().length()==0)
    	    e2.setError("Required field");
    	else if(e3.getText().toString().length()==0)
    		e3.setError("Required field");
    	else if(e4.getText().toString().length()==0)
    		e4.setError("Required field");
    	else if(e5.getText().toString().length()==0)
    		e5.setError("Required field");
    	else{
        String str =e4.getText().toString()+ " "+ e5.getText().toString();
    	Geocoder fwd_gc = new Geocoder(this, Locale.getDefault());
    	List<Address> locations = null;
			try {
				locations=fwd_gc.getFromLocationName(str, 1);
				if (locations.size()==0){
					t.setText("Valid address required!");
				}
				else {
					double lat2= locations.get(0).getLatitude();
					double lng2= locations.get(0).getLongitude();
			    	db.open();
			        db.insertEat(e1.getText().toString(),e3.getText().toString(),str, lat2,lng2, e6.getText().toString(), e2.getText().toString());
			        db.close();
					t.setText("Database updated");
					home= new Intent(update_db.this, munch.class);
					startActivity(home);
				}
				}
			catch (IOException e) {
				t.setText("Unable to update now! check network connection");
			}
    	}
    }
    
    public void onCancel(View v) {
    	finish();
    }
}










