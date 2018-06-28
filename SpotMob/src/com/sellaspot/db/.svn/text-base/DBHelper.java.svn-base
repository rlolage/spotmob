package com.sellaspot.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {

	private static final String TAG = DBHelper.class.getName();
	
	private SQLiteDatabase sellASpotLocalDB = null;
	private String tableName = "local_user_table";
	private String databaseName = "sell_a_spot_local";

	public void openLocalDatabase(Context context) {

		try {
			sellASpotLocalDB = context.openOrCreateDatabase(
					databaseName, Context.MODE_PRIVATE, null);

			sellASpotLocalDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName
					+ " (user_id VARCHAR, session_id VARCHAR, email VARCHAR, name VARCHAR);");
		
		} catch (Exception e) {
			Log.e(TAG, "Error creating/opening database");
			e.printStackTrace();
		}
	}

	public void insertIntoLocalDB(String userName, String sessionId, String email, String name) {

		sellASpotLocalDB.execSQL("INSERT INTO " + tableName
				+ " (user_id, session_id, email, name)" + " VALUES ('" + userName + "', '"
				+ sessionId +  "', '"  + email+  "', '"  + name + "');");
	}

	public String getSessionIdFromDB() {
		
		Cursor cursor = sellASpotLocalDB.rawQuery("SELECT * FROM " + tableName, null);
		String sessionId = null;
		int sessionIdIndex = cursor.getColumnIndex("session_id");

		cursor.moveToFirst();

		if (cursor != null && cursor.getCount() != 0) {
			
			do {
				sessionId = cursor.getString(sessionIdIndex);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return sessionId;
	}
	
	public String getEmailFromDB() {
		
		Cursor cursor = sellASpotLocalDB.rawQuery("SELECT * FROM " + tableName, null);
		String email = null;
		int emailIndex = cursor.getColumnIndex("email");

		cursor.moveToFirst();

		if (cursor != null && cursor.getCount() != 0) {
			
			do {
				email = cursor.getString(emailIndex);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return email;
	}
	
	public String getNameFromDB() {
		
		Cursor cursor = sellASpotLocalDB.rawQuery("SELECT * FROM " + tableName, null);
		String name = null;
		int nameIndex = cursor.getColumnIndex("name");

		cursor.moveToFirst();

		if (cursor != null && cursor.getCount() != 0) {
			
			do {
				name = cursor.getString(nameIndex);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return name;
	}
	
	public String getUserIdFromDB() {
		
		Cursor cursor = sellASpotLocalDB.rawQuery("SELECT * FROM " + tableName, null);
		String userId = null;
		int userNameIndex = cursor.getColumnIndex("user_id");

		cursor.moveToFirst();

		if (cursor != null && cursor.getCount() != 0) {
			
			do {
				userId = "" + cursor.getInt(userNameIndex);
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return userId;
	}
	
	public void deleteUserFromLocalDB() {
		sellASpotLocalDB.execSQL("DELETE from " + tableName);
	}
	
	public void closeDBConnection() {
		if (sellASpotLocalDB != null)
			sellASpotLocalDB.close();
	}
}
