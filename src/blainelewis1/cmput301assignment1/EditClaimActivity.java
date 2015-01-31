package blainelewis1.cmput301assignment1;

import java.text.DateFormat;
import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;

public class EditClaimActivity extends SerializingActivity {

	//TODO: should a claim be deletable from this page
	//TODO:what happens in a list item if it has a large description
	//TODO: if we change the date range, what do we do with expenses no longer in  the range
	
	private Claim claim;
	
	
	private EditText descriptionTextView;
	//private ListView expensesListView;
	private EditText startDate;
	private EditText endDate;


	private DatePickerDialog startDatePickerDialog;


	private DatePickerDialog endDatePickerDialog;


	//private ExpenseAdapter expensesAdapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_edit_claim);
		
		loadClaim(savedInstanceState);	

		findViewsByIds();
		initViews();
		setListeners();
		update();		
			
	}
	
	private void initViews() {
		//expensesAdapter = new ExpenseAdapter(this, R.layout.expenses_layout, claim.expenses);
		
		//expensesListView.setAdapter(expensesAdapter);		
	}

	@Override
    protected void onResume() {
		super.onResume();
    	update();
    }


	private void setListeners() {
		
		descriptionTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					claim.setDescription(descriptionTextView.getText().toString());
					update();
				}
				
			}
		});
		
		/*expensesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	
                Intent intent = new Intent(EditClaimActivity.this, ExpenseActivity.class);
                
            	Expense expense = (Expense) parent.getItemAtPosition(position);
                intent.putExtra("CLAIM_ID", EditClaimActivity.this.claim.getID());
                intent.putExtra("EXPENSE_ID", expense.getID());
                
                startActivity(intent);
            }
		});*/
		
		Calendar startCalendar = claim.getStartCalendar();
		
		startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				Calendar calendar = claim.getStartCalendar();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				
				startDate.clearFocus();
				
				update();
				
			}
			
		}, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH));
		
		Calendar endCalendar = claim.getEndCalendar();
		
		endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar calendar = claim.getEndCalendar();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

				endDate.clearFocus();
				update();
				
				
			}
			
		}, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));
		
		startDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					startDatePickerDialog.show();
				}
				
			}
			
		});
		
		endDate.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					endDatePickerDialog.show();
				}
				
			}
			
		});
		
	}

	private void loadClaim(Bundle savedInstanceState) {
		
		//Attempt to load the claim from intent or savedInstanceState
				
		ClaimManager claimManager = ClaimManager.getInstance();
		
		if(savedInstanceState == null) {
		
			//Try to get claim from intent
			Intent intent = getIntent();
			claim = claimManager.getClaimById(intent.getStringExtra("CLAIM_ID"));
			
		} else {
			claim = claimManager.getClaimById(savedInstanceState.getString("CLAIM_ID"));
		}
		
		if(claim == null) {
			//TODO: an error occurred, fail gracefully
			finish();
		}
		
	}

	private void findViewsByIds() {
		descriptionTextView = (EditText) findViewById(R.id.edit_claim_description);
		startDate = (EditText) findViewById(R.id.edit_claim_start_date);
		endDate = (EditText) findViewById(R.id.edit_claim_end_date);
		//expensesListView = (ListView) findViewById(R.id.edit_claim_expense_list);
	}
	
	private void update() {
		
		if(claim.getDescription().isEmpty()){
			setTitle("New Claim");
		} else {
			setTitle(claim.getDescription());
		}
		
		descriptionTextView.setText(claim.getDescription());
		//expensesAdapter.notifyDataSetChanged();
		
		
		DateFormat formatter = DateFormat.getDateInstance();
		
		startDate.setText(formatter.format(claim.getStartCalendar().getTime()));
		endDate.setText(formatter.format(claim.getEndCalendar().getTime()));

	}

	
	public void submitEdittedClaim(View view) {
		//TODO: Maybe we should move the "submit" button into the action bar
		finish();
	}
	
//	public void createExpense(View view) {
//
//		ClaimManager claimManager = ClaimManager.getInstance();
//		Expense expense = claimManager.createNewExpense(claim);
//		
//		Intent createExpenseIntent = new Intent(this, ExpenseActivity.class);
//		
//		createExpenseIntent.putExtra("EXPENSE_ID", expense.getId());
//		createExpenseIntent.putExtra("CLAIM_ID", claim.getID());
//		
//		startActivity(createExpenseIntent);		
//	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_claim, menu);
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
			//TODO: Delete from edit, what happens then?
			
			ClaimManager claimManager = ClaimManager.getInstance();
			claimManager.deleteClaim(claim);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}	
}
