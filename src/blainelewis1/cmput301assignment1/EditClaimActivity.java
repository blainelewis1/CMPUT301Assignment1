package blainelewis1.cmput301assignment1;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class EditClaimActivity extends SerializingActivity {		
		
		private Claim claim;
		
		
		private EditText descriptionEditText;
		private EditText startDate;
		private EditText endDate;
		
		private DatePickerDialog startDatePickerDialog;
		private DatePickerDialog endDatePickerDialog;


		private Button finishButton;
		
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setContentView(R.layout.activity_edit_claim);
		
		claim = ClaimManager.getInstance().extractClaim(savedInstanceState, getIntent());

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
		
		descriptionEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				validate();				
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
				
				
				//TODO: also hacky
				startDate.clearFocus();
				
				validate();
								
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

				//TODO: also hacky
				endDate.clearFocus();
				
				
			}
			
		}, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));
		
		startDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					
					Calendar calendar = extractDateFromEditText(startDate);
					
					
					startDatePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					startDatePickerDialog.show();
				}
				
			}
			
		});
		
		endDate.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					
					Calendar calendar = extractDateFromEditText(endDate);
					
					endDatePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					endDatePickerDialog.show();
				}
				
			}
			
		});
		
	}



	private void findViewsByIds() {
		descriptionEditText = (EditText) findViewById(R.id.edit_claim_description);
		startDate = (EditText) findViewById(R.id.edit_claim_start_date);
		endDate = (EditText) findViewById(R.id.edit_claim_end_date);
		finishButton = (Button) findViewById(R.id.action_finish_edit_claim);
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
		
		if(validate()) {
			
			claim.setDescription(descriptionEditText.getText().toString());
			claim.setEndCalendar(extractDateFromEditText(endDate));
			claim.setStartCalendar(extractDateFromEditText(startDate));
			
		}		
	}

	private Calendar extractDateFromEditText(EditText editText) {
		DateFormat formatter = DateFormat.getInstance();
		Date date = null;
		try {
			date = formatter.parse(editText.getText().toString());
		} catch (ParseException e) {}
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		
		return calendar;
	}

	private boolean validate() {
		boolean valid = true;
		
		if(descriptionEditText.getText().toString().isEmpty()) {
			descriptionEditText.setError("Description cannot be empty!");
			valid = false;
		}

		if(claim.isDateRangeValid(extractDateFromEditText(startDate), extractDateFromEditText(endDate))) {
			
		}
				
		if(!valid) {
			finishButton.setVisibility(View.GONE);
		} else {
			finishButton.setVisibility(View.VISIBLE);	
		}
		
		invalidateOptionsMenu();
		
		return valid;
	}

}


