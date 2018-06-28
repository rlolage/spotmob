//package com.sellaspot.spots;
//
//import java.util.ArrayList;
//
//import android.R;
//import android.app.ListActivity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.sellaspot.MainActivity;
//import com.sellaspot.datamodel.Item;
//import com.sellaspot.datamodel.RESTData;
//import com.sellaspot.db.DBSession;
//
//public class SellerSpotsActivity extends ListActivity {
//
//	private ArrayList<Item> spots = null;
//	private SpotsAdapter itemAdapter;
//
//	SellerSpotsActivity sellerSpotsActivity;
//
//	private static final String TAG = "SellerSpotsActivity";
//	
//	public void onCreate(Bundle savedInstanceState) {
//
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.seller_spots_layout);
//		sellerSpotsActivity = this;
//
//		spots = new ArrayList<Item>();
//		this.itemAdapter = new SpotsAdapter(this, R.layout.list_row, spots);
//		setListAdapter(this.itemAdapter);
//
//		updateUI();
//	}
//
//	private Runnable itemsThread = new Runnable() {
//
//		@Override
//		public void run() {
//			
//			if (spots != null && spots.size() > 0) {
//				
//				itemAdapter.clear();
//				itemAdapter.notifyDataSetChanged();
//				
//				for (int i = 0; i < spots.size(); i++)
//					itemAdapter.add(spots.get(i));
//			}
//			// progressDialog.dismiss();
//			itemAdapter.notifyDataSetChanged();
//		}
//	};
//
//	private class SpotsAdapter extends ArrayAdapter<Item> {
//
//		private ArrayList<Item> items;
//
//		public SpotsAdapter(Context context, int textViewResourceId,
//				ArrayList<Item> items) {
//			super(context, textViewResourceId, items);
//			this.items = items;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			
//			View v = convertView;
//			
//			if (v == null) {
//			
//				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				v = vi.inflate(R.layout.list_row, null);
//			}
//			
//			Item item = items.get(position);
//			
//			if (item != null) {
//				TextView tt = (TextView) v.findViewById(R.id.toptext);
//				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
//				TextView bidsText = (TextView) v.findViewById(R.id.bidsText);
//				TextView timeLeftText = (TextView) v.findViewById(R.id.timeLeftText);
//
//				if (tt != null) {
//					tt.setText(item.getItemdescription());
//				}
//				if (bt != null) {
//					bt.setText(item.getVenuename() + "\n" + item.getCity() + "," + item.getState());
//				}
//
//				if (bidsText != null) {
//					bidsText.setText(item.getNumofbids() + " bids");
//				}
//				if(timeLeftText != null) {
//					
//					//timeLeftText.setText(timeLeft);
//				}
//			}
//			return v;
//		}
//	}
//
//	@Override
//	public void onListItemClick(ListView lv, View v, int position, long id) {
//
//		Intent intent = new Intent(sellerSpotsActivity, MainActivity.class);
//		intent.putExtra("itemid", "" + spots.get(position).getItemid());
//		sellerSpotsActivity.startActivity(intent);
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		updateUI();
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//	}
//	
//	private void updateUI() {
//
//		DBSession session = new DBSession(this.getApplicationContext());
//
//		this.spots = (ArrayList<Item>) RESTData.getSpots(session.getUserId(), session.getSessionId(), "buyer");
//		
//		Log.d(TAG, spots.toString());
//		
//		itemsThread.run();
//	}
//	
//	public void sellASpot(View view) {
//		
//		Intent intent = new Intent(sellerSpotsActivity, MainActivity.class);
//		intent.putExtra("type", "seller");
//		sellerSpotsActivity.startActivity(intent);
//	}
//}
