package com.sellaspot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;

import com.sellaspot.datamodel.RESTData;
import com.sellaspot.db.DBSession;

public class LoginActivity extends Activity {

	private LoginActivity loginActivity;
	private boolean isOnline = true;
	private boolean isValidCredentials = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		loginActivity = this;
		setContentView(R.layout.login);
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		ActionBar actionbar = getActionBar();
        
        Drawable d=getResources().getDrawable(R.drawable.banner);
        //actionbar.setBackgroundDrawable(d);
        actionbar.setTitle("SpotMob");
        actionbar.setIcon(d);
		
	}
	
	/**
	 * Click on forgot password.
	 * 
	 * @param view
	 */
	public void clickForgotPassword(View view) {

    	String url = "http://www.spotmob.mobi/SpotMobResetPassword/";
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setData(Uri.parse(url));
    	startActivity(i);
	}
	
	/**
	 * Click on Register Button.
	 * 
	 * @param view
	 */
	public void clickRegisterButton(View view) {
		
		Intent intent = new Intent(this, RegisterActivity.class);
		this.startActivity(intent);
		finish();
	}
	
	
	
	/**
	 * Click on Login Button.
	 * 
	 * @param view
	 */
	public void clickLoginButton(View view) {
		
		TextView messageTextView = (TextView)findViewById(R.id.messageTextView);
		
		//Validate the text fields
		if(validateFields()) {
			new LoadData().execute();
		} else {
			
			messageTextView.setText("Please enter correct credentials");

//			final Dialog dialog = new Dialog(loginActivity);
//			dialog.setContentView(R.layout.check_internet);
//			
//			dialog.setTitle("Login Error");
//			TextView statusTV = (TextView)dialog.findViewById(R.id.connectionStatus);
//			statusTV.setText("Please enter email and password");
//			
//			dialog.show();
		}
	}
	
	
	public class LoadData extends AsyncTask<Void, Void, Void> {
	    
		ProgressDialog progressDialog;
	    
	    //declare other objects as per your need
	    @Override
	    protected void onPreExecute()
	    {
	        progressDialog = ProgressDialog.show(loginActivity, "Logging In", "Please wait while we spothenticate you", true);

	        //do initialization of required objects objects here                
	    };      
	    @Override
	    protected Void doInBackground(Void... params)
	    {   
	    	TextView emailTextView = (TextView)findViewById(R.id.loginUsernameText);
	    	TextView passwordTextView = (TextView)findViewById(R.id.loginPasswordText);
	    	
	    	if(NetworkConnection.isOnline(loginActivity)) {
		    	String userData = RESTData.login(emailTextView.getText().toString(), passwordTextView.getText().toString());
				
		    	if(userData != null) {
					
		    		String[] data = userData.split(",");
		    		DBSession dbSession = new DBSession(loginActivity);
					dbSession.insertIntoDB(data[0], data[1], emailTextView.getText().toString(), data[2]);
					
					Intent intent = new Intent(loginActivity, MainActivity.class);
					loginActivity.startActivity(intent);
					finish();
				
		    	} else {
		    		isValidCredentials = false;
				}
	    	} else {
	    		isOnline = false;
	    	}
	         //do loading operation here  
	        return null;
	    }       
	    @Override
	    protected void onPostExecute(Void result)
	    {
	        super.onPostExecute(result);
	        progressDialog.dismiss();
	        
	    	TextView messageTextView = (TextView)findViewById(R.id.messageTextView);

	    	
			if(!isOnline) {
				
				messageTextView.setText("No Internet Connection");
				
//		    	final Dialog dialog = new Dialog(loginActivity);
//				dialog.setContentView(R.layout.check_internet);
//				
//				dialog.setTitle("Connectivity Problem");
//				TextView statusTV = (TextView)dialog.findViewById(R.id.connectionStatus);
//				statusTV.setText("Please check Internet Connection");
//				
//				dialog.show();
			}
			
			if(!isValidCredentials) {
				messageTextView.setText("Please enter correct credentials");
//				final Dialog dialog = new Dialog(loginActivity);
//				dialog.setContentView(R.layout.check_internet);
//				
//				dialog.setTitle("Login Error");
//				TextView statusTV = (TextView)dialog.findViewById(R.id.connectionStatus);
//				statusTV.setText("Please enter correct credentials");
//				
//				dialog.show();

			}
	    };
	 }
	
	public boolean validateFields() {
		
    	TextView emailTextView = (TextView)findViewById(R.id.loginUsernameText);
    	TextView passwordTextView = (TextView)findViewById(R.id.loginPasswordText);

    	return (!(emailTextView.getText().toString().equalsIgnoreCase("") || passwordTextView.getText().toString().equalsIgnoreCase("")));
	}
}
