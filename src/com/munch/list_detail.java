package com.munch;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class list_detail extends Activity{
    /** Called when the activity is first created. */  
	String addr, str1, str2;
	double lat, lng;
	int lat1, lng1;
	TextView t1,t2,t3;
	Intent mapIntent;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.list_details);
        Bundle bundle= getIntent().getExtras();
        long id = bundle.getLong("g");
        DBAdapter db = new DBAdapter(this);
        db.open();
        Cursor c =db.getEat(id);
        str1 = c.getString(1);
		str2 = c.getString(2);
		lat = c.getDouble(4);
		lng = c.getDouble(5);
		addr = c.getString(2)+"\n"+c.getString(3);
		t1=(TextView)findViewById(R.id.Tv_Name);
		t1.setText(str1);
		t2=(TextView)findViewById(R.id.Tv_Address);
		t2.setText(addr);
		t2=(TextView)findViewById(R.id.Tv_cntct);
		t2.setText(c.getString(6));
		c.close();
	}
	
	public void mapClick(View v){
		lat1=(int)(lat*1000000);
    	lng1=(int)(lng*1000000);
    	mapIntent= new Intent(list_detail.this,MapClass.class);
    	mapIntent.putExtra("name", str1);
    	mapIntent.putExtra("address", str2);
    	mapIntent.putExtra("l1", lat1);
    	mapIntent.putExtra("l2", lng1);
        startActivity(mapIntent);
	}
	
}