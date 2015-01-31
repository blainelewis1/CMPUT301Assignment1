package blainelewis1.cmput301assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListClaimsActivity extends SerializingActivity {

	//TODO: add an empty message to all the list views
	//TODO: what to do if a claim has empty fields
	
	private ListView claimsListView;
	private ClaimAdapter claimsListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_claims);
		
		findViewsByIds();
		initViews();
		setListeners();
	
	}


	private void setListeners() {
		claimsListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	
            	Claim claim = (Claim) parent.getItemAtPosition(position);
                Intent intent = new Intent(ListClaimsActivity.this, ClaimActivity.class);
                intent.putExtra("CLAIM_ID", claim.getID());
                startActivity(intent);
            }
		});		
	}


	private void initViews() {
		ClaimManager claimManager = ClaimManager.getInstance();
		claimsListAdapter = new ClaimAdapter(this, R.layout.claim_layout, claimManager.getClaimsSortedByStartDate());
		claimsListView.setAdapter(claimsListAdapter);
	}


	private void findViewsByIds() {
		claimsListView = (ListView)findViewById(R.id.claimsList);
	}

	private void update() {
		claimsListAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_claims, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_new_claim) {
			
			Intent createClaimIntent = new Intent(this, EditClaimActivity.class);
			ClaimManager claimManager = ClaimManager.getInstance();
					
			Claim claim = claimManager.createNewClaim(); 
			
			createClaimIntent.putExtra("CLAIM_ID", claim.getID());
	    	startActivity(createClaimIntent);	 
	    	
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		
		update();
	}
}
