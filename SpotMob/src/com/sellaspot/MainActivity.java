package com.sellaspot;

import java.io.IOException;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.sellaspot.datamodel.RESTData;
import com.sellaspot.db.DBSession;

public class MainActivity extends Activity implements LocationListener {

	//Location updates
	private LocationManager locationManager;
	private String provider;
	private double latitude;
	private double longitude;
	private String streetAddress;
	private String city;
	private String state;
	private String country;
	private String zipcode;
	
	
	public Context appContext;
	private String category;
	private DBSession session;
	
	private static final String SENDER_ID = "444009856297";
	
	private static String TAG = "MainActivity";
	public MainActivity() {
		
		session = new DBSession(this);

	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);

    	
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		
		final String regId = GCMRegistrar.getRegistrationId(this);
	
		if (regId.equals("")) {
		  
			GCMRegistrar.register(this, SENDER_ID);

		} else {
		  Log.v(TAG, "Already registered");
		}
		
		//Send registration Id to server
		RESTData.registerDevice(session.getUserId(), session.getSessionId(), regId);
		
	    // Get the location manager
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    // Define the criteria how to select the locatioin provider -> use
	    // default
	    Criteria criteria = new Criteria();
	    provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);

	    // Initialize the location fields
	    if (location != null) {
	      System.out.println("Provider " + provider + " has been selected.");
	      onLocationChanged(location);
	    } else {
	    	System.out.println("Location not available");
	    	System.out.println("Location not available");
	    }
	  
		
		if (session.getSessionId() != null && RESTData.validateSession(session.getUserId(), session.getSessionId())) {

	    	setContentView(R.layout.main);
	        appContext = this.getApplicationContext();
	        
	        //ActionBar gets initiated
	        ActionBar actionbar = getActionBar();
	        
	        //Tell the ActionBar we want to use Tabs.
	        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        
	        Drawable d=getResources().getDrawable(R.drawable.banner);
	        //actionbar.setBackgroundDrawable(d);
	        actionbar.setIcon(d);
	        actionbar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
	        actionbar.setTitle("SpotMob");
	        //actionbar.setSubtitle("Search for spots...");
	        
	        //initiating both tabs and set text to it.
	        ActionBar.Tab SpotsTab = actionbar.newTab().setText("Spots");
	        ActionBar.Tab MyBidsTab = actionbar.newTab().setText("My Bids");
	        ActionBar.Tab MyItemsTab = actionbar.newTab().setText("My Spots");
	 
	        //create the two fragments we want to use for display content
	        Fragment spotsFragment = new SpotsFragment();
	        Fragment myBidsFragment = new MyBidsFragment();
	        Fragment myItemsFragment = new MyItemsFragment();
	 
	        //set the Tab listener. Now we can listen for clicks.
	        SpotsTab.setTabListener(new MyTabsListener(spotsFragment));
	        //SpotsTab.setIcon(R.drawable.ic_action_achievement);
	        
	        MyBidsTab.setTabListener(new MyTabsListener(myBidsFragment));
	        //MyBidsTab.setIcon(R.drawable.ic_action_clock);
	        
	        MyItemsTab.setTabListener(new MyTabsListener(myItemsFragment));
	        //MyItemsTab.setIcon(R.drawable.ic_action_circles);
	        
	        //add the two tabs to the actionbar
	        actionbar.addTab(SpotsTab);
	        actionbar.addTab(MyBidsTab);
	        actionbar.addTab(MyItemsTab);



		} else {

			Intent intent = new Intent(this, LoginActivity.class);
			this.startActivity(intent);
			finish();
		}    	
    	
    }
    
    public void sellASpot(View view) {
    
    	final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

		dialog.setContentView(R.layout.sellaspot_dialog);
		
		dialog.setTitle("Sell A Spot!");

		// set the custom dialog components - text, image and button
	    final Spinner spinner4 = (Spinner) dialog.findViewById(R.id.categorySpinner);
	    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
	    		dialog.getContext(), R.array.category_array, android.R.layout.simple_spinner_item);
	    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner4.setAdapter(adapter4);
	    spinner4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        	category = spinner4.getSelectedItem().toString();
	        }
	        public void onNothingSelected(AdapterView<?> parent) {
	        }
	    });

		Button spotItButton = (Button) dialog.findViewById(R.id.spotItButton);
		// if button is clicked, close the custom dialog
		spotItButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//Make the API call to create spot
	           	TextView venueNameTextView = (TextView)dialog.findViewById(R.id.venue);
	         	TextView itemDescriptionTextView = (TextView)dialog.findViewById(R.id.item_description);
	         	TextView priceTextView = (TextView)dialog.findViewById(R.id.price);
	         	TextView quantityTextView = (TextView)dialog.findViewById(R.id.quantity);
	         	TextView messageTextView = (TextView)dialog.findViewById(R.id.messageTextView);
	         	CheckBox checkBox = (CheckBox)dialog.findViewById(R.id.shareCheckBox);
	         	
	         	if(!(venueNameTextView.getText().toString().isEmpty() ||
	         			itemDescriptionTextView.getText().toString().isEmpty() ||
	         			priceTextView.getText().toString().isEmpty() ||
	         			quantityTextView.getText().toString().isEmpty()
	         			)) {
		         	
		         	DBSession session = new DBSession(dialog.getContext());
	
		         	if(RESTData.sellaspot(session.getUserId(), 
		         			session.getSessionId(), 
		         			venueNameTextView.getText().toString(), 
		       				itemDescriptionTextView.getText().toString(), 
		       				category,  
		       				priceTextView.getText().toString(), 
		       				quantityTextView.getText().toString(), 
		       				String.valueOf(latitude), String.valueOf(longitude), city, state, streetAddress)) {
		       			
		         		Toast.makeText(dialog.getContext(), "Spot Added!!", Toast.LENGTH_LONG).show();
		          		 //Go to Main Screen
		         		
		         		//if Share is selected prompt with the options
		         		if(checkBox.isChecked()) {
			          	
		         			String statusText = "Spot : " + itemDescriptionTextView.getText().toString() + "\nVenue : " +
			         				venueNameTextView.getText().toString() + "\nPrice : $" +
			         				priceTextView.getText().toString() + "\nQuantity : " +
			         				quantityTextView.getText().toString() + "\n";
							Intent intent = new Intent(Intent.ACTION_SEND);
						    intent.setType("text/plain");
						    intent.putExtra(Intent.EXTRA_TEXT, statusText);
						    startActivity(Intent.createChooser(intent, "Share with friends"));
		         		}
		         		
		       		} else {
		         		Toast.makeText(dialog.getContext(), "Please enter correct information", Toast.LENGTH_LONG).show();
		       		}
					
					dialog.dismiss();
	         	} else {
	    			messageTextView.setText("Please enter all the information");
	         	}
			}
		});
     	
		dialog.show();
		dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.banner);

    }
    
    class MyTabsListener implements ActionBar.TabListener {
    	
    	public Fragment fragment;
    	 
    	public MyTabsListener(Fragment fragment) {
    		this.fragment = fragment;
    	}
    	 
    	@Override
    	public void onTabReselected(Tab tab, FragmentTransaction ft) {
    		//Toast.makeText(appContext, "Reselected!", Toast.LENGTH_LONG).show();
    	}
    	 
    	@Override
    	public void onTabSelected(Tab tab, FragmentTransaction ft) {
    		ft.replace(R.id.fragment_container, fragment);
    	}
    	 
    	@Override
    	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    		ft.remove(fragment);
    	}
    	 
    }
    
    /* Request updates at startup */
    @Override
    protected void onResume() {
      super.onResume();
      locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
      this.latitude = location.getLatitude();
      this.longitude = location.getLongitude();
      
      CurrentLocation.latitude = String.valueOf(this.latitude);
      CurrentLocation.longitude = String.valueOf(this.longitude);
      
      Geocoder gCoder = new Geocoder(this);
      List<Address> addresses;
	try {
		addresses = gCoder.getFromLocation(latitude, longitude, 1);
		if (addresses != null && addresses.size() > 0) {
	          
	          this.streetAddress = addresses.get(0).getAddressLine(0);
	          this.city = addresses.get(0).getLocality();
	          this.state = addresses.get(0).getAdminArea();
	          this.zipcode = addresses.get(0).getPostalCode();
	          
	      }
		
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//locationManager.removeUpdates(this);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
      Toast.makeText(this, "Enabled new provider " + provider,
          Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
      Toast.makeText(this, "Disabled provider " + provider,
          Toast.LENGTH_SHORT).show();
    }
    
    public void shareOnFacebook(View v) {
    	Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.setType("text/plain");
	    intent.putExtra(Intent.EXTRA_TEXT, "The status update text");
	    startActivity(Intent.createChooser(intent, "Dialog title text"));
		
    }
}