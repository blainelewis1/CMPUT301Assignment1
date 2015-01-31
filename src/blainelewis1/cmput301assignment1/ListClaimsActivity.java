package blainelewis1.cmput301assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListClaimsActivity extends SerializingActivity {
	
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
                Intent intent = ClaimManager.getInstance().getEditClaimIntent(ListClaimsActivity.this, claim);

                startActivity(intent);
            }
		});		
	}


	private void initViews() {
		ClaimManager claimManager = ClaimManager.getInstance();
		claimsListAdapter = new ClaimAdapter(this, R.layout.claim_layout, claimManager.getClaimsSortedByStartDate());
		claimsListView.setAdapter(claimsListAdapter);
		
		if(claimsListAdapter.isEmpty()) {
			Toast toast = Toast.makeText(this, "Click + to add a claim!", Toast.LENGTH_LONG);
			toast.show();
		}
	
	}


	private void findViewsByIds() {
		claimsListView = (ListView)findViewById(R.id.claimsList);
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
			
			Intent intent = ClaimManager.getInstance().getCreateClaimIntent(this);
			startActivity(intent);	 
	    	
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override 
	public void onResume() {
		super.onResume();
		
		claimsListAdapter.notifyDataSetChanged();
	}
}
