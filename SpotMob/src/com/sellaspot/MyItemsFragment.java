package com.sellaspot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Dialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sellaspot.SpotsFragment.LoadData;
import com.sellaspot.datamodel.Bid;
import com.sellaspot.datamodel.Item;
import com.sellaspot.datamodel.RESTData;
import com.sellaspot.datamodel.UserRating;
import com.sellaspot.datamodel.UserRating.Ratedspot;
import com.sellaspot.db.DBSession;

public class MyItemsFragment extends ListFragment implements OnQueryTextListener {
 
	private ArrayList<Item> items = new ArrayList<Item>();
	private ItemAdapter itemAdapter;
	private int bidId;
	
	
	Item item;
	private ArrayList<Bid> bids = new ArrayList<Bid>();
	private BidsAdapter bidsAdapter;
	String itemId;
	boolean isItemBidAccepted = false;
	
	private String comments;
	private float rating;
	private String bidUserType;
	private int ratedUserId;
	
	private MyItemsFragment fragment;
	private Dialog bidsByItemDialog;
	private String itemDescription;
	
	private Map<Integer,Float> userRatingMap = new ConcurrentHashMap<Integer, Float>();
	private Map<Integer,Integer> bidIdToUserIdMap = new ConcurrentHashMap<Integer, Integer>();
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_items_layout, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    
    	super.onActivityCreated(savedInstanceState);
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
    	
    	setHasOptionsMenu(true);

    	fragment = this;
		
    	
		new MySpotsLoader().execute();
		
		items = new ArrayList<Item>();
		this.itemAdapter = new ItemAdapter(this.getActivity().getApplicationContext(), R.layout.my_items_list_row, items);
		setListAdapter(this.itemAdapter);
	}

	private class ItemAdapter extends ArrayAdapter<Item> {

		private ArrayList<Item> items;

		public ItemAdapter(Context context, int textViewResourceId, ArrayList<Item> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.my_items_list_row, null);
			}
			Item item = items.get(position);

			if (item != null) {
				
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				TextView bidsText = (TextView) v.findViewById(R.id.bidsText);
				ImageView imgView = (ImageView)v.findViewById(R.id.icon);
				
				if (tt != null) {
					tt.setText(item.getItemdescription());
				}
				if (bt != null) {
					bt.setText(item.getCity() + "," + item.getState());
				}


				if (bidsText != null) {
					
					bidId = items.get(position).getBidid();
					
					if(bidId != 0) {
						imgView.setBackgroundResource(R.drawable.accepted);
						bidsText.setText(item.getNumofbids() + " bids");
					}
					else {
						bidsText.setText(item.getNumofbids() + " bids");
						imgView.setBackgroundResource(R.drawable.pending);
					}
				}
			}
			return v;
		}
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		
		if(items.get(position).getNumofbids() == 0) {
			 Toast.makeText(this.getActivity(),
			          "No Bids on this spot yet. Please check back later", Toast.LENGTH_LONG).show();
			 
			 return;
		}
		
		bidId = items.get(position).getBidid();
		itemId = String.valueOf(items.get(position).getItemid());
		
		if(bidId == 0) {
			
			//Display list of bids
	    	final Dialog dialog = new Dialog(this.getActivity());
			dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

			dialog.setContentView(R.layout.bids_by_item_layout);
			dialog.setTitle("Bids for your item!");
			dialog.setCancelable(true);
			
			bidsByItemDialog = dialog;
			
			//bidsAdapter.clear();
//			bids = new ArrayList<Bid>();
			this.bidsAdapter = new BidsAdapter(dialog.getContext(), R.layout.bids_by_item_list_row, bids);
			new BidsLoader().execute();

			ListView lst = (ListView)dialog.findViewById(android.R.id.list);
			lst.setAdapter(bidsAdapter);
			
			lst.setOnItemClickListener(new OnItemClickListener() {
				 
				public void onItemClick(AdapterView<?> parent, View view,
				     int position, long id) {
				    // When clicked, show a toast with the TextView text
					
					bidId = bids.get(position).getBidid();
					
		        	Dialog dialogObj = (Dialog)dialog;
		        	dialogObj.setContentView(R.layout.bid_detail_layout);
		    		DBSession dbSession = new DBSession(dialogObj.getContext());
		    		Bid bid = RESTData.getBidById("" + bidId, dbSession.getUserId(), dbSession.getSessionId());
		    		UserRating userRating = RESTData.getRating(dbSession.getUserId(), dbSession.getSessionId(), bid.getUserid());

		    		RatingBar ratingBar = (RatingBar) dialogObj.findViewById(R.id.ratingBar1);
		    		
		    		if(userRating != null)
		    			ratingBar.setRating(userRating.getTotalrating());
		    		
		    		TextView commentsTextView = (TextView)dialogObj.findViewById(R.id.comments);
		    		String comments = "";
		    		
		    		if(userRating != null) {
		    			List<Ratedspot> ratedSpots = userRating.getRatedspot();
		    			
		    			for(Ratedspot r : ratedSpots) {
		    				comments += r.getComments() + "\n"; 
		    			}
		    			
		    		} else {
		    			comments = "No comments for this user yet";
		    		}
		    		
		    		commentsTextView.setText(comments);

		    	       final Button button = (Button) dialog.findViewById(R.id.acceptBidButton);
		    	        button.setOnClickListener(new View.OnClickListener() {
		    	            public void onClick(View v) {
		    	            	
		    	            	DBSession dbSession = new DBSession(dialog.getContext());
		    	            	if(RESTData.acceptBid(dbSession.getUserId(), dbSession.getSessionId(), Integer.parseInt(itemId), bidId)){
		    	            		 Toast.makeText(dialog.getContext(),
		    	         			          "Bid Accepted!", Toast.LENGTH_SHORT).show();
		    	            	} else {
		    	            		Toast.makeText(dialog.getContext(),
		    	       			          "Failed - Bid Not Accepted!", Toast.LENGTH_SHORT).show();
		    	            	}
		    	            	dialog.dismiss();
		    	            }
		    	        });
		    	        
				   }
				  });
			
			dialog.show();
			dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.banner);
			

		}
		else {

	    	final Dialog dialog = new Dialog(this.getActivity());
			dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

			dialog.setContentView(R.layout.rate_bidder_layout);
			dialog.setTitle("Rate this user");

			itemId = String.valueOf(items.get(position).getItemid());
			//bidId = extras.getString("bidid");
			
			DBSession dbSession = new DBSession(this.getActivity());
			//Bid bid = RESTData.getBidById(bidId, dbSession.getUserId(), dbSession.getSessionId());
			Item item = RESTData.getSpotById(itemId, dbSession.getUserId(), dbSession.getSessionId());
			bidUserType = item.getItemusertype();
			
			
			if(bidIdToUserIdMap.get(item.getBidid()) != null) {
				ratedUserId = bidIdToUserIdMap.get(item.getBidid());
			} else {
				Bid b = RESTData.getBidById(String.valueOf(bidId), dbSession.getUserId(), dbSession.getSessionId());
				ratedUserId = b.getUserid();
			}
			
			if (bids != null && bids.size() > 0) {
				
				bidsAdapter.clear();
				bidsAdapter.notifyDataSetChanged();
				for (int i = 0; i < bids.size(); i++)
					bidsAdapter.add(bids.get(i));
			}
			
			//Rate User Button
			Button rateUserButton = (Button)dialog.findViewById(R.id.rateUserButton);
			rateUserButton.setOnClickListener(new View.OnClickListener() {
		           
		       	 public void onClick(View v) {
		       		 
		     		RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.userRatingBar);
		     		TextView messageTextView = (TextView)dialog.findViewById(R.id.messageTextView);
		     		
		    		rating = ratingBar.getRating();
		    		
		    		TextView commentsTextView = (TextView) dialog.findViewById(R.id.comments);
		    		comments = commentsTextView.getText().toString();
		    		
		    		if(!commentsTextView.getText().toString().isEmpty()) {

			    		DBSession dbSession = new DBSession(dialog.getContext());
			    		if(RESTData.rateUser(dbSession.getUserId(), dbSession.getSessionId(), Integer.parseInt(itemId), ratedUserId, (int)rating, bidUserType, comments)) {
			    			
			    			Toast.makeText(dialog.getContext(),
			    			          "User Rated successfully", Toast.LENGTH_SHORT).show();
			    		
			    			dialog.dismiss();
			    		
			    		} else {
			    			
			    			messageTextView.setText("You have already rated this bidder");

			    		}
		    		} else {
		    			
		    			messageTextView.setText("Please enter comments for bidder");

		    		}
		       	 }
			});
			
			//Cancel Bid Button
			Button cancelBidButton = (Button)dialog.findViewById(R.id.cancelBidButton);
			cancelBidButton.setOnClickListener(new View.OnClickListener() {
		           
		       	 public void onClick(View v) {
		       		 
		     		DBSession dbSession = new DBSession(dialog.getContext());
		    		
		    		if(RESTData.cancelBid(dbSession.getUserId(), dbSession.getSessionId(), Integer.parseInt(itemId), bidId)) {
		    			Toast.makeText(dialog.getContext(),
		    			          "Bid Cancelled", Toast.LENGTH_SHORT).show();
		    		} else {
		    			Toast.makeText(dialog.getContext(),
		    			          "Bid could not be cancelled", Toast.LENGTH_SHORT).show();
		    		}
		       	 }
			});
			
			
			//bidsAdapter.notifyDataSetChanged();
			dialog.show();			
			dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.banner);
		}
	}

	private class BidsAdapter extends ArrayAdapter<Bid> {

		private ArrayList<Bid> bids;

		public BidsAdapter(Context context, int textViewResourceId, ArrayList<Bid> bids) {
		
			super(context, textViewResourceId, bids);
			this.bids = bids;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v = convertView;
			
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.bids_by_item_list_row, null);
			}
			
			Bid bid = bids.get(position);
			
	
			if (bid != null && bids.size()!=0) {
			
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				TextView itemDetails = (TextView) v.findViewById(R.id.itemDetails);
				RatingBar ratingBar = (RatingBar)v.findViewById(R.id.customUserRating);

				if(itemDetails != null) {
					itemDetails.setText(item.getItemdescription() + " \nCategory: " + item.getCategory() + "\nLocation: " + item.getCity() + "," + item.getState() + "\nPrice: $" + item.getPrice() + "\nQuantity: " +item.getQuantity());
				}
				
				if (tt != null) {
					tt.setText("A bid of $" + bid.getBidprice() + " on your spot");
				}
				if (bt != null) {
					bt.setText(bid.getUserdescription());
				}


				if (ratingBar != null) {
					
					if(userRatingMap.containsKey(bid.getUserid())) {
						//bidsText.setText("Rated " + userRating.getTotalrating() + "/5");
						ratingBar.setRating(userRatingMap.get(bid.getUserid()));
						
					}
					else {
						//bidsText.setText("Rating : None");
						ratingBar.setRating(0);
					}

				}
			}
			return v;
		}
		

	}
	
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	
	public class MySpotsLoader extends AsyncTask<Void, Void, Void> {
	    
		ProgressDialog progressDialog;
	    
	    //declare other objects as per your need
	    @Override
	    protected void onPreExecute()
	    {
	        progressDialog = ProgressDialog.show(fragment.getActivity(), "Loading", "Getting spots posted by you", true);

	        //do initialization of required objects objects here                
	    };      
	    @Override
	    protected Void doInBackground(Void... params)
	    {   
			DBSession session = new DBSession(fragment.getActivity().getApplicationContext());

			items = (ArrayList<Item>) RESTData.getItems(session.getUserId(), session.getSessionId());		
			
			//Log.d("ITEMS", items.toString());

	         //do loading operation here  
	        return null;
	    }       
	    @Override
	    protected void onPostExecute(Void result)
	    {
	        super.onPostExecute(result);
			
			
			if (items != null && items.size() > 0) {
				itemAdapter.clear();
				itemAdapter.notifyDataSetChanged();
				for (int i = 0; i < items.size(); i++)
					itemAdapter.add(items.get(i));
			}
			itemAdapter.notifyDataSetChanged();
			
	        progressDialog.dismiss();
	    };
	 }
	
	
	
	
	
	public class BidsLoader extends AsyncTask<Void, Void, Void> {
	    
		ProgressDialog progressDialog;
	    
	    //declare other objects as per your need
	    @Override
	    protected void onPreExecute()
	    {
			TextView itemDetailsTextView = (TextView)bidsByItemDialog.findViewById(R.id.itemDetails);
			itemDetailsTextView.setText("Loading bids..Please wait");
			
	       // progressDialog = ProgressDialog.show(bidsByItemDialog.getContext(), "Loading", "Getting spots posted by you", true);

	        if(bids != null) {
	        	bidsAdapter.clear();
	        	bidsAdapter.notifyDataSetChanged();
	        }
			
	        //do initialization of required objects objects here                
	    };      
	    @Override
	    protected Void doInBackground(Void... params)
	    {   
			DBSession session = new DBSession(getActivity());
			bids = (ArrayList<Bid>) RESTData.getAllBidsById(itemId, session.getUserId(), session.getSessionId());

			if(bids != null) {
				
				for(Bid bid : bids) {
					
					UserRating userRating = RESTData.getRating(session.getUserId(), session.getSessionId(), bid.getUserid());
					
					if(userRating != null) {
						userRatingMap.put(bid.getUserid(), userRating.getTotalrating());
					}
					
					bidIdToUserIdMap.put(bid.getBidid(), bid.getUserid());
				}
				
				item = RESTData.getSpotById(itemId, session.getUserId(), session.getSessionId());
				
				if(item.getBidid() != 0) {
					isItemBidAccepted = true;
				} else {
					isItemBidAccepted = false;
				}
				itemDescription = item.getItemdescription();
				
			} 
			
	        return null;
	    }       
	    @Override
	    protected void onPostExecute(Void result)
	    {
	        super.onPostExecute(result);
			
	        if (bids != null && bids.size() > 0) {
				
				bidsAdapter.clear();
				bidsAdapter.notifyDataSetChanged();
				
				for (int i = 0; i < bids.size(); i++) {
					
					bidsAdapter.add(bids.get(i));
				}
			}

			bidsAdapter.notifyDataSetChanged();
			
			TextView itemDetailsTextView = (TextView)bidsByItemDialog.findViewById(R.id.itemDetails);
			itemDetailsTextView.setText(itemDescription);
			
			//progressDialog.dismiss();
	    };
	    
	    
	 }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
		
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(this);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.refresh:
	        new MySpotsLoader().execute();
	        return true;
	    case R.id.about:
	    	final Dialog dialog = new Dialog(this.getActivity());
			dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

			dialog.setContentView(R.layout.about_dialog);
			
			dialog.setTitle("About SpotMob");
			dialog.show();
			dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.banner);
	        return true;
	    case R.id.myProfile:
			Intent intent = new Intent(this.getActivity(), MyProfileActivity.class);
			this.startActivity(intent);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	
	public class SearchSpots extends AsyncTask<String, Void, Void> {
	    
		ProgressDialog progressDialog;
	    
	    //declare other objects as per your need
	    @Override
	    protected void onPreExecute()
	    {
	        progressDialog = ProgressDialog.show(fragment.getActivity(), "Searching", "Searching for spots", true);

	        //do initialization of required objects objects here                
	    };   

	    
	    @Override
	    protected Void doInBackground(String... params)
	    {   
	    	String searchString = params[0];
	    	
			DBSession session = new DBSession(fragment.getActivity().getApplicationContext());

			items = (ArrayList<Item>) RESTData.searchSpots(session.getUserId(), session.getSessionId(), searchString);
			
			//Log.d(TAG, spots.toString());

	         //do loading operation here  
	        return null;
	    }       
	    @Override
	    protected void onPostExecute(Void result)
	    {
	        super.onPostExecute(result);
			if (items != null && items.size() > 0) {
				
				itemAdapter.clear();
				itemAdapter.notifyDataSetChanged();
				
				for (int i = 0; i < items.size(); i++)
					itemAdapter.add(items.get(i));
			} else {
				itemAdapter.clear();
			}
			// progressDialog.dismiss();
			itemAdapter.notifyDataSetChanged();    
	        
			progressDialog.dismiss();
	    };
	 }

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		new SearchSpots().execute(query);
		return false;
	}
}