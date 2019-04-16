package com.munch;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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

public class search_list extends Activity{
	
	LocationManager locationManager;
	TextView t1;
	EditText et;
	Button b1;
	TextView desc_tv, caption_tv;
	
	Intent regIntent;
	Intent updateIntent;
	Intent listIntent;
	Intent homeIntent;
	
	MenuItem menuItem1;
	MenuItem menuItem2;
	
	Location location;
	double lat = 0, lng = 0;
	
	ListView lv;
	DBAdapter db = new DBAdapter(this);
	private ArrayList<String> list_cap = new ArrayList<String>();
	private ArrayList<String> list_desc = new ArrayList<String>();
	private ArrayList<String> list_id = new ArrayList<String>();
	
	static final private int HOME = Menu.FIRST;
	static final private int UPDATE = Menu.FIRST +2;
	
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.search_list);
        et= (EditText)findViewById(R.id.et_search);
	}
	
	public void onSearch(View v){
		String str =et.getText().toString()+ " Vellore";
		TextView t=(TextView)findViewById(R.id.TextView01);
		Geocoder fwd_gc = new Geocoder(this, Locale.getDefault());
        List<Address> locations = null;			
        		try {
        				locations = fwd_gc.getFromLocationName(str, 1);
        				if(locations.size()==0){
        					t.setText("enter valid location");
        					list_cap = new ArrayList<String>();
        			    	list_desc = new ArrayList<String>();
        			    	list_id = new ArrayList<String>();
        			    	display_list();
        				}
        				else{
        					lat= locations.get(0).getLatitude();
        					lng= locations.get(0).getLongitude();
        					t.setText("");
        					getList(5000);
        					}
        				
        			} catch (IOException e) {
        				t.setText("network connection required");
        			}

		}
	
	    
	    //display list
	    public void display_list(){
	    	lv = (ListView)findViewById(R.id.searchList);
	    	String[] captionArray = (String[]) list_cap.toArray(
					new String[list_cap.size()]);
	    	final ItemsAdapter itemsAdapter = new ItemsAdapter(
	    	    	   search_list.this, R.layout.list_item,
	    	    	    captionArray);
	    	lv.setAdapter(itemsAdapter);
	    	lv.setOnItemClickListener(new OnItemClickListener() {
	    		public void onItemClick(AdapterView<?> parent, View v, int position,
	    		long id) {
	    			listIntent = new Intent(search_list.this,list_detail.class);
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
		menuItem1 = menu.add(groupId, HOME, Menu.NONE, "Home");
		menuItem1.setIcon(R.drawable.home);
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
		    switch(item.getItemId()) {
		    case HOME:
		    	homeIntent= new Intent(search_list.this,munch.class);
		        startActivity(homeIntent);
		    case 10:
	        	getList(1000);
	            return true;
	        case 15:
	        	getList(3000);
	            return true;
	        case 20:
	        	getList(5000);
	            return true;
	        case 25:
	        	getList(7000);
	        	return true;
	        case UPDATE:
		    	updateIntent= new Intent(search_list.this,update_db.class);
		        startActivity(updateIntent);
	        default:
	            return super.onOptionsItemSelected(item);
		    }
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

		 
		 public void getList(int dist){
		        list_cap = new ArrayList<String>();
		    	list_desc = new ArrayList<String>();
		    	list_id = new ArrayList<String>();
			    db.open();
		        Cursor c = db.getAllEats();
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
