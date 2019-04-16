package com.munch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.database.Cursor;

public class munch extends Activity{
    /** Called when the activity is first created. */

	LocationManager locationManager;
	TextView t1;
	Button b1,b_ser;
	EditText et1;
	TextView desc_tv, caption_tv;
	
	Intent updateIntent;
	Intent listIntent;
	Intent thisIntent;
	Intent searchInt;
	
	MenuItem menuItem1;
	MenuItem menuItem2;
	
	Location location;
	double lat = 0, lng = 0;
	
	ListView lv;
	DBAdapter db = new DBAdapter(this);
	private ArrayList<String> list_cap = new ArrayList<String>();
	private ArrayList<String> list_desc = new ArrayList<String>();
	private ArrayList<String> list_id = new ArrayList<String>();
	
	static final private int SEARCH = Menu.FIRST;
	static final private int UPDATE = Menu.FIRST +2;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        b1 = (Button) findViewById(R.id.Button01);

    	db.open();
        Cursor cursors = db.getAllEats();
		if (cursors.moveToNext()) {
			b1.setVisibility(View.GONE);
		} else {
			b1.setVisibility(View.VISIBLE);
		}
		db.close();
		
        String location_context= Context.LOCATION_SERVICE;
    	locationManager = (LocationManager)getSystemService(location_context);
    	GPSLocation();
	    db.open();
        Cursor c = db.getAllEats();
    	getList(c, 5000);
    }
    
    //display list
    public void display_list(){
    	lv = (ListView)findViewById(R.id.myList);
    	String[] captionArray = (String[]) list_cap.toArray(
				new String[list_cap.size()]);
    	final ItemsAdapter itemsAdapter = new ItemsAdapter(
    	    	   munch.this, R.layout.list_item,
    	    	    captionArray);
    	lv.setAdapter(itemsAdapter);
    	lv.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView<?> parent, View v, int position,
    		long id) {
    			listIntent = new Intent(munch.this,list_detail.class);
    			long i=Long.valueOf(list_id.get(position));
    			listIntent.putExtra("g",i);
    			startActivity(listIntent);
    		}
    		});
    }
    	
	//Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	int groupId = 0;
	menuItem1 = menu.add(groupId, SEARCH, Menu.NONE,"Search");
	menuItem1.setIcon(R.drawable.search1);
	SubMenu submenu = menu.addSubMenu(0, Menu.FIRST+1, Menu.NONE, "Distance");
	submenu.setIcon(R.drawable.dist);
    submenu.add(0, 10, Menu.NONE, "Less than 1 km");
    submenu.add(0, 15, Menu.NONE, "Less than 3 km");
    submenu.add(0, 20, Menu.NONE, "Less than 5 km");
    submenu.add(0, 25, Menu.NONE, "Less than 7 km");
	menuItem2 = menu.add(groupId, UPDATE, Menu.NONE, "Update");
	menuItem2.setIcon(R.drawable.update);
	return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        Cursor c;
	    switch(item.getItemId()) {
	    case SEARCH:
	    	searchInt= new Intent(munch.this,search_list.class);
	        startActivity(searchInt);
	        return true;
	    case 10:
		    db.open();
	        c = db.getAllEats();
        	getList(c, 1000);
            return true;
        case 15:
        	db.open();
	        c = db.getAllEats();
        	getList(c, 3000);
            return true;
        case 20:
        	db.open();
	        c = db.getAllEats();
        	getList(c,5000);
            return true;
        case 25:
        	db.open();
	        c = db.getAllEats();
        	getList(c,7000);
        	return true;
  
        case UPDATE:
	    	updateIntent= new Intent(munch.this,update_db.class);
	        startActivity(updateIntent);
	        return true;
	        
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
	    	
	
	//GPS
	public void GPSLocation(){
		TextView tv = (TextView)findViewById(R.id.myTextView);
		Geocoder gc;
		StringBuilder sb = new StringBuilder();
		locationManager.requestLocationUpdates("network", 1000, 0,
		new LocationListener() {
		public void onLocationChanged(Location location) {}
		public void onProviderDisabled(String provider){}
		public void onProviderEnabled(String provider){}
		public void onStatusChanged(String provider, int status,
		Bundle extras){}
		});
		location = locationManager.getLastKnownLocation("network");
		if (location != null) {
		lat = location.getLatitude();
		lng = location.getLongitude();
		gc = new Geocoder(this, Locale.getDefault());
		try {
			List<Address> myList =gc.getFromLocation(lat, lng, 1);
			if(myList.size()>0){
			Address address = myList.get(0);
			for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
				sb.append(address.getAddressLine(i)).append("\n");
			}
			else{
			sb.append("Location details not found");
			}
		}catch (IOException e) {
			sb.append("Unable to display Location Details\nNetwork connection required!\n");
		}
		} 
		else
		sb.append("No Location");
		tv.setText(sb);
		}
	
	//calculate distance between 2 points by proximity
	public float calc_dist(Cursor c)
	{
		double lat2= c.getDouble(4);
		double lng2= c.getDouble(5);
		float results[] = new float[3];
		Location.distanceBetween(lat, lng, lat2, lng2, results);
		return results[0];
	}
	
	//button to populate
	public void handleClick(View v) {
		insert();
		thisIntent=new Intent(munch.this,munch.class);
        startActivity(thisIntent);
	}
	
	//filter by restaurant type
	public void onFilter(View v){
		et1=(EditText)findViewById(R.id.filter);
		db.open();
        Cursor c = db.getEatByType(et1.getText().toString());
        getList(c, 5000);
	}
	//populates the db
	private void insert(){
		db.open();
		
		db.insertEat("Quick bites","Near VIT University, Vellore", "Tamil Nadu, India",12.970414 ,79.154345, "+919876533221", "Fast food, italian" );
		
	    db.insertEat("Mandarai foods","VIT University, Vellore", "Tamil Nadu 632014, India",12.969473,79.154259, "+91 92 44 905908", "hotel, south indian");
	    		 
	    db.insertEat("Dhaba express","Thiruvalam Road, Katpadi", "Near VIT, Vellore, Tamil Nadu 632014, India", 12.96849,79.155139, "+91 94 43 687398","dhaba, north indian, punjabi");
	   
	    db.insertEat("Super King Fast Food","Chennai Salai, Katpadi", "Opposite VIT, Vellore, Tamil Nadu 632014, India", 12.968469,79.15604, "+91 416 303 5312", "Fast food, continental");

	    db.insertEat("Punjabi Fast Food","Chennai Salai, Katpadi", "Opposite VIT, Vellore, Tamil Nadu 632014, India", 12.968553,79.156212, "+91 93 44 889981", "Fast food, punjabi" );

	    db.insertEat("Apna dhaba","Chennai Salai, Near V.I.T. College", "Vellore, Tamil Nadu 632006, India", 12.967674,79.156898, "+91 90 65 932121","Dhaba, north indian");

	    db.insertEat("Flo Cafe", "Gaurang Mall,SH-59" ,"Tiruvalam Road,Katpadi,Vellore, Opp vellore institue of Technology, Vellore, Tamil Nadu 632014, India",12.968155,79.157542, "+91 80 15 014154","Coffee, italian, Fast food, continental");

	    db.insertEat("China Valley Restaurant","Chennai Salai, Katpadi", "Near VIT, Vellore, Tamil Nadu 632014, India",12.967444,79.157758, "+91 90 95 321623","chinese, Fast food");

	    db.insertEat("Calcutta Roll Center" ,"Chennai Salai, Katpadi", "Near VIT College, Vellore, Tamil Nadu 632014, India",12.967277,79.158209, "+91 98 94 975698","chinese, non veg, rolls");

	    db.insertEat("S.B.R Biryani store", "VIT University, Opposite VIT Gate 3", "Katpadi, Vellore, Tamil Nadu 632014, India", 12.967246,79.158381, "+919095621731","Biriyani, indian");
	    
	    db.insertEat("Hotel palm tree","Officers Line (Next to Corporation Office), Kosapet", "No-10, Thennamara Street, Vellore, Tamil Nadu 632001, India ",12.914661,79.133878, "+91 416 222 2960","indian,continental,non veg");
	    
	    db.insertEat("Guc Canteen"," Officer's Line, Opposite BSNL Office", "Vellore, Tamil Nadu 632001, India",12.915686,79.131732, "+91 416 222 7927","Fast food,indian");
	    
	    db.insertEat("Hotel Ranga","No. 5, Thennamara Street", "Opposite Indian Fancy, Vellore, Tamil Nadu 632001, India",12.914347,79.132762, "+91 416 223 3688 ","south indian,veg" );
	    
	    db.insertEat("Hotel Shelom","Officer's Line, Vellore", " Tamil Nadu 632001, India",12.917338,79.132783, "9665172341", "chinese, continental, indian,non veg");
	    
	    db.insertEat("Hotel Arya Bhavan","No. 2, Near By Raja Theatre", "Vellore, Tamil Nadu 632001, India",12.914368,79.13244, "9566807754","south indian,chinese,veg");
	    
	    db.insertEat("Hotel Krishna Vaibhav","No 10 officers Line (opp to post office)", "Vellore, Tamil Nadu 632001, India",12.916188,79.132655, "+91 416 222 1486 ","south indian,veg");
	    
	    db.insertEat("Mohan Biryani Special","No 29/4D, Old Bus Stand", "Vellore, Tamil Nadu 632004, India",12.923319,79.13274, "+91 416 222 7580","biriyani,non veg");
	    
	    db.insertEat("New Tamilnadu Hotel","No. 16, New Sitting Bazaar", "Near Old Bus stand, Vellore, Tamil Nadu 632004, India",12.921416,79.132719, "+91 92 45 106965 ","south indian,veg");
	    
	    db.insertEat("Hotel Alankar","SH 9, Vellore", "Tamil Nadu, India",12.921991,79.131914, "+91 416 420 2013 ","indian, dhaba, veg");
	    
	    db.insertEat("Royal Fast Food","Bangalore Road", "Next to H.M.S Lorry Office, Vellore, Tamil Nadu 632004, India",12.924208,79.131303, "+91 416 221 3855","continental,non veg,dhaba");

	    db.insertEat("Hotel River View","New Katpadi Road","Vellore Dist, Vellore, 632064",12.97599,79.120954,"9334693820","continental,non veg,indian");
	    
	    db.insertEat("Hotel Anusuya","Chittoor High Road","Katpadi Police Station, Vellore Tamil Nadu 632007",12.97516,79.136875,"0416 2295266","south indian,veg");
            
	    db.insertEat("Hotel Balaji","No. 119, Chittoor Road","Sandhya Medicals, Chenguttai Vellore, Tamil Nadu 632007",12.977168,79.136704,"09442645119","south indian,veg");    
            
        db.insertEat("Hotel Sri Lakshmi","Chittoor Bus Stop","Near State Bank, Katpadi Vellore, Tamil Nadu 632007",12.967633,79.138206,"9321893992","south indian,veg");

        db.insertEat("Darling Residency","#11/8, Officers Line, Anna Salai","Vellore, Tamil Nadu 632 001",12.91372,79.131921,"0416 2213001","continental,indian,veg,non veg");
 
        db.insertEat("Hundreds Heritage","No. 10, 14th East Cross Road, Gandhinagar","Vellore, Tamil Nadu 632006",12.953037,79.139646,"0416 224 8100","continental,indian,non veg");
             
        db.insertEat("Baby Residency","No. 7, Officers Line, Near By Lakshmi Theatre","Vellore Tamil Nadu 632001",12.909035,79.132779,"0416 2243565","continental,indian,veg");
 
        db.insertEat("New VDM Guest House","Opp. Vellore Bus Stand, Katpadi Road","Vellore, Tamil Nadu 632004",12.939318,79.139646,"099 94 086014","south indian,veg");
    
        db.insertEat("Hotel Saravana Bhavan","25B, 25C, Jeevarathnam, Maligai, Arcot Road","Thottapalayam, Vellore, Tamil Nadu 632004",12.934634,79.140993,"0416 221 7433","south indian,veg");

        db.insertEat("Hotel Sams AC","Katpadi Road, Silk Mill", "Near Wine Shop, Gandhi Nagar,Vellore, Tamil Nadu 632007",12.963409,79.135843,"09364277447","continental,non veg");

	    db.insertEat("Hotel Surabi","34, Officers Line,","Near Sangamam Hall, Officer's Line Vellore, Tamil Nadu 632001",12.91556,79.131723,"09790393631","continental,indian,non veg");

        db.insertEat("Hotel Archana","SH 9","Vellore, Tamil Nadu 632006",12.953873,79.137414,"","south indian,veg");

        db.insertEat("Hotel Aavanaa Inn","144, Arcot Road (Opp. CMC Hospital)","Vellore, Tamil Nadu 632 004",12.927189,79.138113,"0416 2215074","north indian,non veg");

        db.insertEat("Lalit Vihar","No. 42, K.V.S Chetty Street","K V S Chetty St Gandhi Road, Near M.S. Lodge, Gandhi RoadVellore, Tamil Nadu 632004",12.923801,79.135245,"9576822351","north indian,veg");

        db.insertEat("Aahar Punjabi dhaba","Dr Ida Scudder Road","Vellore, Tamil Nadu",12.928443,79.133981,"0416 2244191","punjabi,non veg");
		
	    db.close();
	   }
	 
	 public void getList(Cursor c, int dist){
	        list_cap = new ArrayList<String>();
	    	list_desc = new ArrayList<String>();
	    	list_id = new ArrayList<String>();
	        if (c.moveToFirst())
	        {
	        do {
	        	String temp_id = c.getString(0);
	    		String temp_caption = c.getString(1);
	    		String temp_description = c.getString(2);
	    		float d=calc_dist(c);
	    		if(d<=dist)
	    		{
	    		String s;
	    		d=Math.round(d);
	    		if(d>1000){
	    			d=d/1000;
	    			s=" km";
	    		}
	    		else s=" m";
	    		list_cap.add(temp_caption);
	    		list_desc.add(temp_description+"\n"+d+ s);
	    		list_id.add(temp_id);
	    	    }
	        } while (c.moveToNext());
	        }
	        db.close();
	        display_list();
	        return;
	 }

	 //custom adapter for list view
	 private class ItemsAdapter extends BaseAdapter {
	  String[] items;

	  public ItemsAdapter(Context context, int textViewResourceId,
	    String[] items) {
	   this.items = items;
	  }

	  @Override
	  public View getView(final int position, View convertView,
	    ViewGroup parent) {
	   View view = convertView;
	   if (view == null) {
	    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = vi.inflate(R.layout.list_item, null);
	   }
	   caption_tv = (TextView) view.findViewById(R.id.caption);
	   desc_tv= (TextView)view.findViewById(R.id.description);
	   caption_tv.setText(list_cap.get(position));
	   desc_tv.setText(list_desc.get(position));
	   return view;
	  }

	  public int getCount() {
	   return items.length;
	  }

	  public Object getItem(int position) {
	   return position;
	  }

	  public long getItemId(int position) {
	   return position;
	  }
	 }
	}

