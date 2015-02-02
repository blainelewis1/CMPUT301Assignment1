package blainelewis1.cmput301assignment1;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import blainelewis1.cmput301assignment1.Claim.Status;

/*
 * This activity acts to display a claim as well as provides 
 * a starting point to edit it or add expenses to it.
 * 
 * 
 */

public class ViewClaimActivity extends Activity {
	
	private Claim claim;
	
	private Button submitClaim;
	private Button approveClaim;
	private Button returnClaim;

	private TextView descriptionTextView;
	private TextView datesTextView;
	private TextView statusTextView;
	private ListView expensesList;

	private ExpenseAdapter expensesAdapter;
	
	/*
	 * On creation we begin by loading the claim
	 * Then we attach all views by their ids
	 * Initialize them 
	 * and apply listeners to them
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_claim);
		
		claim = ActivityCommunicationUtils.extractClaim(savedInstanceState, getIntent());
		
		//An unexpected error occurred, exit quietly
		if(claim == null) {
			finish();
		}

		findViewsByIds();
		initViews();
		setListeners();
	}
	
	/*
	 * Set a listener to the expenses list 
	 */
	
	private void setListeners() {
		
		//Clicking on an expense should go to the edit expense, unless the claim isn't in an editable state
		expensesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	
				if(ViewClaimActivity.this.claim.isEditable()) {
		            Intent intent = new Intent(ViewClaimActivity.this, EditExpenseActivity.class);
		            
		        	Expense expense = (Expense) parent.getItemAtPosition(position);
		            intent.putExtra("CLAIM_ID", ViewClaimActivity.this.claim.getId());
		            intent.putExtra("EXPENSE_ID", expense.getId());
		            
		            startActivity(intent);
				} else {
					String addin = "";
					if(claim.getStatus() == Status.APPROVED){
						addin = "after";
					} else {
						addin = "while awaiting";
					}
					
					Toast toast = Toast.makeText(ViewClaimActivity.this, "Expenses aren't editable " + addin + " approval.", Toast.LENGTH_SHORT);
					toast.show();
				}
	        }
		});
	}

	/*
	 * Initialize the expenses adapter, toast if it is empty
	 */
	
	private void initViews() {
		expensesAdapter = new ExpenseAdapter(this, R.layout.expenses_layout, claim.getExpenses());
		
		expensesList.setAdapter(expensesAdapter);
		
		if(expensesAdapter.isEmpty()) {
			Toast toast = Toast.makeText(this, "Click + to add an expense to this claim!", Toast.LENGTH_LONG);
			toast.show();
		}

	}
	
	/*
	 * Fill al fields from the claim model
	 */
	private void updateFieldsFromClaim() {
		setTitle(claim.getDescription());
		
		descriptionTextView.setText(claim.getDescription());
		
		
		datesTextView.setText(claim.getFormattedDateRange());
		statusTextView.setText(claim.getStatusString());
			
		
		expensesAdapter.notifyDataSetChanged();
				
		toggleButtonVisibilities();
		
	}
	
	/*
	 * In case the claim was changed or expenses were, then we should
	 * update the fields
	 */
	
	@Override
	protected void onResume() {
		super.onResume();
		updateFieldsFromClaim();
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
		
		descriptionTextView = (TextView) findViewById(R.id.claim_description);
		datesTextView = (TextView) findViewById(R.id.claim_dates);
		//claimTotalList = (ListView) findViewById(R.id.totalCostsList);
		statusTextView = (TextView) findViewById(R.id.claimStatus);
		expensesList = (ListView) findViewById(R.id.claim_expense_list);
		
	}
	
	
	/*
	 * Chooses what buttons to show depending on the status of the claim
	 */
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
	
	/*
	 * On click listener for the approve button, sets as approved and serializes
	 */
	
	public void setClaimApproved(View view) {
		claim.setStatus(Status.APPROVED);

		ClaimManager.getInstance().serialize(this);
		
		updateFieldsFromClaim();
	}	
	
	/*
	 * On click listener for the return button, sets as returned and serializes
	 */
	
	public void setClaimReturned(View view) {
		claim.setStatus(Status.RETURNED);

		ClaimManager.getInstance().serialize(this);

		
		updateFieldsFromClaim();
	}	
	
	/*
	 * On click listener for the submit button, sets as submitted and serializes
	 */
	
	public void setClaimSubmitted(View view) {
		claim.setStatus(Status.SUBMITTED);
		
		ClaimManager.getInstance().serialize(this);

		updateFieldsFromClaim();		
	}
	
	/*
	 * If the claim is in an editable state, launches an EditClaimActivity in order
	 * to edit this claim
	 */
	
	private void editClaim() {	
		if(claim.isEditable()) {
			
			//Pass the intent		
			Intent intent = ActivityCommunicationUtils.getEditClaimIntent(this, claim);			
	    	startActivity(intent);	 
	    	
		} else {
			String addin = "";
			if(claim.getStatus() == Status.APPROVED){
				addin = "after";
			} else {
				addin = "while awaiting";
			}
		
			Toast toast = Toast.makeText(ViewClaimActivity.this, "Claims aren't editable " + addin + " approval.", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	/*
	 * Creates an expense for this claim and launches the EditExpenseIntent
	 */
	
	public void createExpense() {
		
		Intent createExpenseIntent = ActivityCommunicationUtils.getCreateExpenseIntent(this, claim);
		startActivity(createExpenseIntent);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.claim, menu);
		
		return true;
	}

}
