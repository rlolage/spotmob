package com.sellaspot.db;

import android.content.Context;
import android.util.Log;

public class DBSession {

	private static final String TAG = DBSession.class.getName();
	private Context activity;

	public DBSession(Context activity) {
		this.activity = activity;
	}

	public void insertIntoDB(String userName, String sessionId, String email, String name) {

		try {

			DBHelper dbHelper = new DBHelper();
			dbHelper.openLocalDatabase(this.activity);
			dbHelper.deleteUserFromLocalDB();
			dbHelper.insertIntoLocalDB(userName, sessionId, email, name);
			dbHelper.closeDBConnection();
		} catch (Exception e) {
			Log.e(TAG, "Error while inserting username and session id in db");
			e.printStackTrace();
		}

	}

	public String getSessionId() {

		String sessionId = null;
		DBHelper dbHelper = new DBHelper();

		try {

			dbHelper.openLocalDatabase(this.activity);
			sessionId = dbHelper.getSessionIdFromDB();
			dbHelper.closeDBConnection();

		} catch (Exception e) {

			Log.e(TAG, "Error while getting session id");
			e.printStackTrace();
		}

		return sessionId;
	}

	public String getEmail() {

		String email = null;
		DBHelper dbHelper = new DBHelper();

		try {

			dbHelper.openLocalDatabase(this.activity);
			email = dbHelper.getEmailFromDB();
			dbHelper.closeDBConnection();

		} catch (Exception e) {

			Log.e(TAG, "Error while getting session id");
			e.printStackTrace();
		}

		return email;
	}
	
	public String getName() {

		String name = null;
		DBHelper dbHelper = new DBHelper();

		try {

			dbHelper.openLocalDatabase(this.activity);
			name = dbHelper.getNameFromDB();
			dbHelper.closeDBConnection();

		} catch (Exception e) {

			Log.e(TAG, "Error while getting name");
			e.printStackTrace();
		}

		return name;
	}
	
	public String getUserId() {

		String userName = null;
		DBHelper dbHelper = new DBHelper();

		try {

			dbHelper.openLocalDatabase(this.activity);
			// dbHelper.insertIntoLocalDB("rlolage", "1111");
			//dbHelper.deleteUserFromLocalDB();
			userName = dbHelper.getUserIdFromDB();
			dbHelper.closeDBConnection();

		} catch (Exception e) {

			Log.e(TAG, "Error while getting session id");
			e.printStackTrace();
		}

		return userName;
	}

	public void clearLocalDB() {

		try {

			DBHelper dbHelper = new DBHelper();
			dbHelper.openLocalDatabase(this.activity);
			dbHelper.deleteUserFromLocalDB();
			dbHelper.closeDBConnection();
		} catch (Exception e) {

			Log.e(TAG, "Error while inserting username and session id in db");
			e.printStackTrace();
		}
	}
}
