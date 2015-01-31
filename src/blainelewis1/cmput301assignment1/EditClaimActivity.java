package blainelewis1.cmput301assignment1;

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
import android.widget.Toast;

public class EditClaimActivity extends SerializingActivity {

	//TODO: what happens in a list item if it has a large description
	//TODO: if we change the date range, what do we do with expenses no longer in  the range
	//TODO: We could extract a controller from this
	
	
	private Claim claim;
	
	
	private EditText descriptionTextView;
	private EditText startDate;
	private EditText endDate;
	
	private DatePickerDialog startDatePickerDialog;
	private DatePickerDialog endDatePickerDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_edit_claim);
		
		loadClaim(savedInstanceState);	

		findViewsByIds();
		initViews();
		setListeners();
			
	}
	
	private void initViews() {
		
	}

	@Override
    protected void onResume() {
		super.onResume();
    }
	


	private void setListeners() {
		
		descriptionTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					claim.setDescription(descriptionTextView.getText().toString());
				}
				
			}
		});
				
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
				//TODO: Validate, make sure that we still have the thingers i actually don't knwo what this means...
				
								
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
		
		claim = claimManager.extractClaim(savedInstanceState, getIntent());
		
		//TODO: maybe we can extract this to the claimManager
		
		if(savedInstanceState == null) {
		
			//Try to get claim from intent
			Intent intent = getIntent();
			claim = claimManager.getClaimById(intent.getStringExtra("CLAIM_ID"));
			
		} else {
			claim = claimManager.getClaimById(savedInstanceState.getString("CLAIM_ID"));
		}
		
		if(claim == null) {
			//TODO: an error occurred, fail gracefully also add this in for the expenses and other thingermajgers
			finish();
		}
		
	}

	private void findViewsByIds() {
		descriptionTextView = (EditText) findViewById(R.id.edit_claim_description);
		startDate = (EditText) findViewById(R.id.edit_claim_start_date);
		endDate = (EditText) findViewById(R.id.edit_claim_end_date);
		//expensesListView = (ListView) findViewById(R.id.edit_claim_expense_list);
	}

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
		}  else if(id == R.id.action_finish_edit_claim) {
			submitEdits();
		}
		return super.onOptionsItemSelected(item);
	}	

	private void submitEdits() {
		//TODO: only submit edits if the current form is valid
		
		if(validate()) {
			//TODO: submit edits to claims
			
			
		} else {
			Toast toast = Toast.makeText(this, "Make sure all fields contain valid input!", Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}

	private boolean validate() {
		// TODO: Finish me!!!!
		return false;
	}

}


