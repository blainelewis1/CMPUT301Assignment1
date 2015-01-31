package blainelewis1.cmput301assignment1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import blainelewis1.cmput301assignment1.Claim.Status;

public class ClaimActivity extends Activity {
	
	private Claim claim;
	
	private Button submitClaim;
	private Button approveClaim;
	private Button returnClaim;

	private TextView descriptionTextView;
	private TextView datesTextView;
	private ListView claimTotalList;
	private TextView statusTextView;
	private ListView expensesList;

	private ExpenseAdapter expensesAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_claim);
		
		claim = ClaimManager.getInstance().extractClaim(savedInstanceState, getIntent());

		
		findViewsByIds();
		initViews();
		setListeners();
		update();
	}
	
	private void setListeners() {
		
		expensesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	
				if(ClaimActivity.this.claim.isEditable()) {
		            Intent intent = new Intent(ClaimActivity.this, ExpenseActivity.class);
		            
		        	Expense expense = (Expense) parent.getItemAtPosition(position);
		            intent.putExtra("CLAIM_ID", ClaimActivity.this.claim.getId());
		            intent.putExtra("EXPENSE_ID", expense.getId());
		            
		            startActivity(intent);
				} else {
					Toast toast = Toast.makeText(ClaimActivity.this, "Claim isn't editable while awaiting approval.", Toast.LENGTH_SHORT);
					toast.show();
				}
	        }
		});
	}

	private void initViews() {
		expensesAdapter = new ExpenseAdapter(this, R.layout.expenses_layout, claim.getExpenses());
		
		expensesList.setAdapter(expensesAdapter);
		
		
		
		if(expensesAdapter.isEmpty()) {
			Toast toast = Toast.makeText(this, "Click + to add an expense to this claim!", Toast.LENGTH_LONG);
			toast.show();
		}

	}
	
	private void update() {
		//TODO: remove me...
		
		setTitle(claim.getDescription());
		
		descriptionTextView.setText(claim.getDescription());
		
		
		datesTextView.setText(claim.getFormattedDateRange());
		statusTextView.setText(claim.getStatusString());
		
		ArrayList<String> amounts = claim.getTotalsAsStrings();
		
		claimTotalList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, amounts));
		
		expensesAdapter.notifyDataSetChanged();
				
		invalidateOptionsMenu();
		toggleButtonVisibilities();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		update();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.claim, menu);
		
		//TODO: is having the buttons invisible a good idea? How will the user understand that this is possible?
		
		menu.findItem(R.id.action_edit_claim).setVisible(claim.isEditable());
		menu.findItem(R.id.action_add_expense).setVisible(claim.isEditable());
		
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
		} else if(id == R.id.action_delete_claim) {
			deleteClaim();
		} else if(id == R.id.action_edit_claim) {
			editClaim();			
		} else if(id == R.id.action_email_claim) {
			emailClaim();
		} else if(id == R.id.action_add_expense) {
			createExpense();
		}
		return super.onOptionsItemSelected(item);
	}

	private void emailClaim() {
	    Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:")); // only email apps should handle this
		intent.putExtra(Intent.EXTRA_SUBJECT, claim.getDescription());
		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(claim.getHTMLRepresentation()));
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
		
		
	}
	
	private void deleteClaim() {
		ClaimManager claimManager = ClaimManager.getInstance();
		claimManager.deleteClaim(claim);
		claimManager.serialize(this);
		
		finish();
	}
	
	
	private void findViewsByIds() {
	
		submitClaim = (Button) findViewById(R.id.submitClaim);
		approveClaim = (Button) findViewById(R.id.approveClaim);
		returnClaim = (Button) findViewById(R.id.returnClaim);
		
		descriptionTextView = (TextView) findViewById(R.id.claimDescription);
		datesTextView = (TextView) findViewById(R.id.claim_dates);
		claimTotalList = (ListView) findViewById(R.id.totalCostsList);
		statusTextView = (TextView) findViewById(R.id.claimStatus);
		expensesList = (ListView) findViewById(R.id.claim_expense_list);
		
	}
	
	private void toggleButtonVisibilities(){

		switch (claim.getStatus()){
			case IN_PROGRESS:
				submitClaim.setVisibility(View.VISIBLE);
				approveClaim.setVisibility(View.GONE);
				returnClaim.setVisibility(View.GONE);
				break;
			case SUBMITTED:
				approveClaim.setVisibility(View.VISIBLE);
				returnClaim.setVisibility(View.VISIBLE);
				submitClaim.setVisibility(View.GONE);
				break;
			case RETURNED:
				approveClaim.setVisibility(View.GONE);
				returnClaim.setVisibility(View.GONE);
				submitClaim.setVisibility(View.VISIBLE);
				break;
			case APPROVED:
				approveClaim.setVisibility(View.GONE);
				returnClaim.setVisibility(View.GONE);
				submitClaim.setVisibility(View.GONE);
				break;
			default:
				break;
		}
		
	}
	
	public void setClaimApproved(View view) {
		claim.setStatus(Status.APPROVED);

		ClaimManager.getInstance().serialize(this);
		
		update();
	}	
	
	public void setClaimReturned(View view) {
		claim.setStatus(Status.RETURNED);

		ClaimManager.getInstance().serialize(this);

		
		update();
	}	
	
	public void setClaimSubmitted(View view) {
		claim.setStatus(Status.SUBMITTED);
		
		ClaimManager.getInstance().serialize(this);

		update();		
	}
	
	private void editClaim() {		
		//Pass the intent		
		Intent intent = ClaimManager.getInstance().getEditClaimIntent(this, claim);
		
		intent.putExtra("CLAIM_ID", claim.getId());
		
    	startActivity(intent);	 
	}
	
	public void createExpense() {
		
		Intent createExpenseIntent = ClaimManager.getInstance().getCreateExpenseIntent(this, claim);
		startActivity(createExpenseIntent);
	}

}
