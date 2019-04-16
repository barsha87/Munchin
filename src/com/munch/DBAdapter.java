package com.munch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class DBAdapter
{
public static final String KEY_ROWID = "_id";
public static final String KEY_NAME = "name";
public static final String KEY_ADDR = "addr";
public static final String KEY_ADDRESS= "address";
public static final String KEY_LAT = "lat";
public static final String KEY_LNG = "lng";
public static final String KEY_CNTCT= "cntct";
public static final String KEY_FLTR = "fltr";

private static final String TAG = "DBAdapter";
private static final String DATABASE_NAME = "eat_list";
private static final String DATABASE_TABLE = "eat";
private static final int DATABASE_VERSION = 1;

private static final String DATABASE_CREATE =
"CREATE TABLE IF NOT EXISTS eat (_id integer primary key autoincrement, "
+ "name text not null, addr text not null, address text not null, "
+ "lat number, lng number, cntct text not null, fltr text not null);";

private final Context context;
private DatabaseHelper DBHelper;
private static SQLiteDatabase db;
public DBAdapter(Context ctx)
{
this.context = ctx;
DBHelper = new DatabaseHelper(context);
}
private static class DatabaseHelper extends SQLiteOpenHelper
{
DatabaseHelper(Context context)
{
super(context, DATABASE_NAME, null, DATABASE_VERSION);
}
@Override
public void onCreate(SQLiteDatabase db)
{
db.execSQL(DATABASE_CREATE);
}
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion,
int newVersion)
{
	Log.w(TAG, "Upgrading database from version " + oldVersion
	+ " to "
	+ newVersion + ", which will destroy all old data");
	db.execSQL("DROP TABLE IF EXISTS eat");
	onCreate(db);
	}
	}
	//---opens the database---
	public DBAdapter open() throws SQLException
	{
	db = DBHelper.getWritableDatabase();
	return this;
	}
	//---closes the database---
	public void close()
	{
	DBHelper.close();
	}
	//---insert a title into the database---
	public long insertEat(String name, String addr, String address, double lat, 
			double lng, String cntct, String fltr)
	{
	ContentValues initialValues = new ContentValues();
	initialValues.put(KEY_NAME, name);
	initialValues.put(KEY_ADDR, addr);
	initialValues.put(KEY_ADDRESS, address);
	initialValues.put(KEY_LAT, lat);
	initialValues.put(KEY_LNG, lng);
	initialValues.put(KEY_CNTCT, cntct);
    initialValues.put(KEY_FLTR, fltr);
	return db.insert(DATABASE_TABLE, null, initialValues);
	}
	//---deletes a particular garage---
	public boolean deleteEat(long rowId)
	{
	return db.delete(DATABASE_TABLE, KEY_ROWID +
	"=" + rowId, null) > 0;
	}
	
	//---retrieves all the garages---
	public Cursor getAllEats()
	{
	return db.query(DATABASE_TABLE, new String[] {
	KEY_ROWID, KEY_NAME, KEY_ADDR, KEY_ADDRESS, KEY_LAT, KEY_LNG, KEY_CNTCT, KEY_FLTR},
	null,
	null,
	null,
	null,
	null);
	}
	
	// filters restaurants by type	
	public Cursor getEatByType(String str) throws SQLException
	{
		Cursor mCursor;
		if(str.equals("veg"))
		{mCursor=
			db.query(DATABASE_TABLE, new String[] {
					KEY_ROWID, KEY_NAME, KEY_ADDR, KEY_ADDRESS, KEY_LAT, KEY_LNG, KEY_CNTCT, KEY_FLTR},
			KEY_FLTR + "  NOT LIKE '%non veg%' AND "+ KEY_FLTR + " LIKE '%veg%'",
			null,
			null,
			null,
			null,
			null);
			}
		else{
		mCursor =
		db.query(DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_ADDR, KEY_ADDRESS, KEY_LAT, KEY_LNG, KEY_CNTCT, KEY_FLTR},
		KEY_FLTR + " LIKE '%" + str+ "%'",
		null,
		null,
		null,
		null,
		null);
		}
		if (mCursor != null) {
		mCursor.moveToFirst();
		}
		return mCursor;
		}	 
	
	//---retrieves a particular title---
	public Cursor getEat(long rowId) throws SQLException
	{
	Cursor mCursor =
	db.query(true, DATABASE_TABLE, new String[] {
			KEY_ROWID, KEY_NAME, KEY_ADDR, KEY_ADDRESS, KEY_LAT, KEY_LNG, KEY_CNTCT, KEY_FLTR},
	KEY_ROWID + "=" + rowId,
	null,
	null,
	null,
	null,
	null);
	if (mCursor != null) {
	mCursor.moveToFirst();
	}
	return mCursor;
	}	 
	}