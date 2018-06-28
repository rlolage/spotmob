package com.sellaspot;

import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sellaspot.SpotsFragment.LoadData;
import com.sellaspot.datamodel.Bid;
import com.sellaspot.datamodel.Item;
import com.sellaspot.datamodel.RESTData;
import com.sellaspot.datamodel.UserDetailResponse;
import com.sellaspot.datamodel.UserRating;
import com.sellaspot.db.DBSession;

public class MyBidsFragment extends ListFragment {
 
	private ArrayList<Bid> bids = new ArrayList<Bid>();
	private ArrayList<Item> items = new ArrayList<Item>();
	
	private BidAdapter bidAdapter;

	private String userId;
	private String sessionId;
	private String bidId;
	private int itemId;
	private Bid bid;
	
	private MyBidsFragment fragment;
	private DBSession dbSession;
	
	private Map<Integer, Item> bidIdToItemMap = new ConcurrentHashMap<Integer, Item>();
	private Map<Integer, Float> userIdToRatingMap = new ConcurrentHashMap<Integer, Float>();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_bids_layout, container, false);
    }
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    
    	super.onActivityCreated(savedInstanceState);
    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    	StrictMode.setThreadPolicy(policy);
    	
    	setHasOptionsMenu(true);

    	fragment = this;
		
		new LoadData().execute();
		
		bids = new ArrayList<Bid>();
		this.bidAdapter = new BidAdapter(this.getActivity().getApplicationContext(), R.layout.my_bids_list_row, bids);
		setListAdapter(this.bidAdapter);
		
    }
 
    
	private class BidAdapter extends ArrayAdapter<Bid> {

		private ArrayList<Bid> bids;

		public BidAdapter(Context context, int textViewResourceId,
				ArrayList<Bid> bids) {
			super(context, textViewResourceId, bids);
			this.bids = bids;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.my_bids_list_row, null);
			}
			Bid bid = bids.get(position);
			
			
			if (bid != null) {
				
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				TextView bidsText = (TextView) v.findViewById(R.id.bidsText);
				ImageView imgView = (ImageView)v.findViewById(R.id.icon);
				
				if (tt != null) {
					tt.setText(bid.getItemdescription());
				}
				if (bt != null) {
					if(bid.getAccepted() != null && bid.getAccepted().equalsIgnoreCase("false")) {
						
						bt.setText("Your bid is PENDING");
					}
					else {
						bt.setText("Tap for details");
					}
					
				}

				if (bidsText != null) {
					
					if(bid.getAccepted() != null && bid.getAccepted().equalsIgnoreCase("false")) {
						//bidStatus = "Bid status";
						imgView.setBackgroundResource(R.drawable.pending);
					}
					else {
						//bidStatus = "Bid Status";
						imgView.setBackgroundResource(R.drawable.accepted);

					}
					bidsText.setText("You bid $" + bid.getBidprice());
					
				}

			}
			return v;
		}
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {

		
		bidId = String.valueOf(bids.get(position).getBidid());
		
		DBSession session = new DBSession(this.getActivity());
		
		this.userId = session.getUserId();
		this.sessionId = session.getSessionId();
		
		bid = bids.get(position);
		
		if(bid.getAccepted().equalsIgnoreCase("true")) {
			
	    	final Dialog dialog = new Dialog(this.getActivity());
			dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

			dialog.setContentView(R.layout.rate_seller_layout);
			
			dialog.setTitle("Bid Status");
			
			TextView textView = (TextView)dialog.findViewById(R.id.sellerPhoneNumberTextView);
			UserDetailResponse userDetailResponse = RESTData.getUserDetails(dbSession.getUserId(), dbSession.getSessionId(), String.valueOf(bid.getSellerid()));
			textView.setText("Your bid is ACCEPTED\n Please call " + userDetailResponse.getPhone() + " to proceed");
			
			//Rate User Button
			Button rateUserButton = (Button)dialog.findViewById(R.id.rateUserButton);
			rateUserButton.setOnClickListener(new View.OnClickListener() {
		           
		       	 public void onClick(View v) {
		       		 
		     		RatingBar ratingBar = (RatingBar) dialog.findViewById(R.id.userRatingBar);
		    		float rating = ratingBar.getRating();
		    		
		    		TextView commentsTextView = (TextView) dialog.findViewById(R.id.comments);
		    		TextView messageTextView = (TextView) dialog.findViewById(R.id.messageTextView);
		    		
		    		String comments = commentsTextView.getText().toString();
		    		
		    		if(!commentsTextView.getText().toString().isEmpty()) {
			    		DBSession dbSession = new DBSession(dialog.getContext());
			    		if(RESTData.rateUser(dbSession.getUserId(), dbSession.getSessionId(), bid.getItemid(), bid.getSellerid(), (int)rating, "seller", comments)) {
			    			
			    			Toast.makeText(dialog.getContext(),
			    			          "User Rated Successfully", Toast.LENGTH_SHORT).show();
			    		
			    			dialog.dismiss();
			    		
			    		} else {
			    			
			    			messageTextView.setText("You have already rated this seller");
			    		}
		    		} else {
		    			messageTextView.setText("Please enter comments for seller");
		    		}
		       	 }
			});


			dialog.show();
			dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.banner);
		} else {
			
			//Show the bid detail
	    	final Dialog dialog = new Dialog(this.getActivity());
			dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

			dialog.setContentView(R.layout.bid_status);
			RatingBar ratingBar = (RatingBar)dialog.findViewById(R.id.customUserRating);
			TextView bidStatusTextView = (TextView)dialog.findViewById(R.id.bidStatus);
			
			TextView itemDescriptionTextView = (TextView)dialog.findViewById(R.id.itemDescription);
			String itemDescription = bid.getItemdescription();
			itemDescriptionTextView.setText(itemDescription);
			
			dialog.setTitle("Bid Status");

			UserRating userRating = RESTData.getRating(dbSession.getUserId(), dbSession.getSessionId(), bid.getUserid());
			
			if(userRating != null) {
				userIdToRatingMap.put(bid.getUserid(), userRating.getTotalrating());
			} else {
				userIdToRatingMap.put(bid.getUserid(), 0f);
			}
			
			if(ratingBar != null) {
				ratingBar.setRating(userIdToRatingMap.get(bid.getUserid()));
			}

			if (bidStatusTextView != null) {
				if(bid.getAccepted() != null && bid.getAccepted().equalsIgnoreCase("false")) {
					
					bidStatusTextView.setText("Your bid is PENDING");
				}
				else {
//					UserDetailResponse userDetailResponse = RESTData.getUserDetails(dbSession.getUserId(), dbSession.getSessionId(), String.valueOf(bid.getSellerid()));
//					bidStatusTextView.setText("Your bid is ACCEPTED\n Please call " + userDetailResponse.getPhone() + "to proceed");
					
				}
				
			}

			dialog.show();
			dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.banner);
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
	
	public class LoadData extends AsyncTask<Void, Void, Void> {
	    
		ProgressDialog progressDialog;
	    
	    @Override
	    protected void onPreExecute()
	    {
	        progressDialog = ProgressDialog.show(fragment.getActivity(), "Loading", "Getting bids placed by you", true);

	        //do initialization of required objects objects here                
	    };      
	    @Override
	    protected Void doInBackground(Void... params)
	    {   
			DBSession session = new DBSession(fragment.getActivity().getApplicationContext());
			dbSession = session;
			
			bids = (ArrayList<Bid>) RESTData.getMyBids(session.getUserId(), session.getSessionId());

//			if(bids!=null) {
//				
//				for(Bid bid : bids) {
//					Item item = RESTData.getSpotById("" + bid.getItemid(), dbSession.getUserId(), dbSession.getSessionId());
//					
//					items.add(item);
//					bidIdToItemMap.put(bid.getBidid(), item);
//				}
//			}
			
			//Log.d("BIDS", bids.toString());

	         //do loading operation here  
	        return null;
	    }       
	    @Override
	    protected void onPostExecute(Void result)
	    {
	        super.onPostExecute(result);
			
			if (bids != null && bids.size() > 0) {
				bidAdapter.clear();
				bidAdapter.notifyDataSetChanged();
				for (int i = 0; i < bids.size(); i++)
					bidAdapter.add(bids.get(i));
			}
			// progressDialog.dismiss();
			bidAdapter.notifyDataSetChanged();	
			
	        progressDialog.dismiss();
	    };
	 }
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.refresh:
	        new LoadData().execute();
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
}