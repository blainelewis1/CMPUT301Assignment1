/*

Copyright 2015 Blaine Lewis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/



package blainelewis1.cmput301assignment1;

import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


/*
 * 
 * This activity is the entry point to the entire application
 * It handles initialization tasks such as deserialzation of the date 
 * 
 * I decided to handle serialization here as a form of RAII because we only have one entry point to the application
 * 
 * It displays a list of claims which can be accessed by tapping on them
 * Finally it provides an action bar button to allow creating a new claim
 * 
 */


public class ListClaimsActivity extends Activity {
	
	private ListView claimsListView;
	private ClaimAdapter claimsListAdapter;
	
	/*
	 * Attach ids from components, apply listeners to various
	 * views and initialize them with data
	 * 
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_claims);
		
		ClaimManager.getInstance().deserialize(this);
		
		findViewsByIds();
		initViews();
		setListeners();
	}

	/*
	 * Applies a listener to the listView which opens the viewclaim activity
	 */
	private void setListeners() {
		claimsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	
            	Claim claim = (Claim) parent.getItemAtPosition(position);
                Intent intent = ActivityCommunicationUtils.getViewClaimIntent(ListClaimsActivity.this, claim);

                startActivity(intent);
            }
		});		
	}


	/*
	 * Attaches the claims to the claims list and a sort function
	 * Displays a toast if the list is empty
	 */
	
	private void initViews() {
	
		ClaimManager claimManager = ClaimManager.getInstance();
		claimsListAdapter = new ClaimAdapter(this, R.layout.claim_layout, claimManager.getClaims());
		claimsListView.setAdapter(claimsListAdapter);
		
		claimsListAdapter.sort(new Comparator<Claim>() {
			@Override
			public int compare(Claim claim1, Claim claim2) {
				return claim1.getStart().compareTo(
						claim2.getStart());
			}
		});
		
		if(claimsListAdapter.isEmpty()) {
			Toast toast = Toast.makeText(this, "Click + to add a claim!", Toast.LENGTH_LONG);
			toast.show();
		}
	
	}

	/* 
	 * Find all views in this layout and attach their id
	 */

	private void findViewsByIds() {
		claimsListView = (ListView)findViewById(R.id.list_claims_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_claims, menu);
		return true;
	}

	/*
	 * Handles clicking the plus button in the action bar by starting 
	 * a new activity 
	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_new_claim) {		
			Intent intent = ActivityCommunicationUtils.getCreateClaimIntent(this);
			startActivity(intent);	 
	    	
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * We need to reset the listview data, just in case
	 */
	@Override 
	public void onResume() {		
		super.onResume();

		claimsListAdapter.notifyDataSetChanged();
	}
}
