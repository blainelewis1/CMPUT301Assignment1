package blainelewis1.cmput301assignment1;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//TODO: validate is being called too often
//TODO: description shouldn't be validated the instant you enter... 
//TODO: dates are formatted with the time, thats wrong

public class EditClaimActivity extends Activity {		
		
		private Claim claim;
		
		
		private EditText descriptionEditText;
		private EditText startDateEditText;
		private EditText endDateEditText;
		
		private DatePickerDialog startDatePickerDialog;
		private DatePickerDialog endDatePickerDialog;


		private boolean valid;


		private LinearLayout layout;


		private boolean isNew;		
	
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.activity_edit_claim);
		
		ClaimManager claimManager = ClaimManager.getInstance();
		
		claim = claimManager.extractClaim(savedInstanceState, getIntent());
		isNew = claimManager.extractIsNew(savedInstanceState, getIntent());
		
		findViewsByIds();
		initViews();
		setListeners();
	
		validate();
	}
		
	@Override
	public void onBackPressed() {
		if(isNew) {
			ClaimManager.getInstance().deleteClaim(claim);
		}
		super.onBackPressed();
	}
	
	private void initViews() {
		DateFormat formatter = DateFormat.getDateInstance();
		
		startDateEditText.setText(formatter.format(claim.getStartCalendar().getTime()));
		endDateEditText.setText(formatter.format(claim.getEndCalendar().getTime()));
		
		descriptionEditText.setText(claim.getDescription());

	}

	private void setListeners() {
		
		TextWatcher validatingTextWatcher = new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				validate();
			}
			
						
		};
	
		startDateEditText.addTextChangedListener(validatingTextWatcher);
		descriptionEditText.addTextChangedListener(validatingTextWatcher);
		endDateEditText.addTextChangedListener(validatingTextWatcher);
				
		Calendar startCalendar = claim.getStartCalendar();
		
		startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				startDateEditText.setText(formatDate(year, monthOfYear, dayOfMonth));
				
				//TODO: less hacky but it really doesn't work...
				startDateEditText.clearFocus();
				layout.requestFocus();
												
			}
			
		}, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH));
		
		Calendar endCalendar = claim.getEndCalendar();
		
		endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				endDateEditText.setText(formatDate(year, monthOfYear, dayOfMonth));

				startDateEditText.clearFocus();
				layout.requestFocus();								
			}
			
		}, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));
		
		startDateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					
					Calendar calendar = extractDateFromEditText(startDateEditText);
					
					startDatePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					startDatePickerDialog.show();
				}
				
			}
			
		});
		
		endDateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					
					Calendar calendar = extractDateFromEditText(endDateEditText);
					
					endDatePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					endDatePickerDialog.show();
				}
				
			}
			
		});
		
	}

	private String formatDate(int year, int monthOfYear,
			int dayOfMonth) {
		Calendar calendar = claim.getStartCalendar();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		
		return DateFormat.getDateInstance().format(calendar.getTime());
	}

	private void findViewsByIds() {
		descriptionEditText = (EditText) findViewById(R.id.edit_claim_description);
		startDateEditText = (EditText) findViewById(R.id.edit_claim_start_date);
		endDateEditText = (EditText) findViewById(R.id.edit_claim_end_date);
		layout = (LinearLayout) findViewById(R.id.edit_claim_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_claim, menu);		
		
		menu.findItem(R.id.action_finish_edit_claim).setVisible(valid);

		
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
		} else if(id == android.R.id.home && isNew) {
			ClaimManager.getInstance().deleteClaim(claim);
		}
		return super.onOptionsItemSelected(item);
	}	

	private void submitEdits() {	
		
		if(validate()) {
			
			claim.setDescription(descriptionEditText.getText().toString());
			claim.setDateRange(extractDateFromEditText(startDateEditText), extractDateFromEditText(endDateEditText));
			
			ClaimManager.getInstance().serialize(this);
						
			finish();
			
		} else {
			//TODO: remove me
			Toast toast = Toast.makeText(this, "yoooo", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private Calendar extractDateFromEditText(EditText editText) {
		DateFormat formatter = DateFormat.getDateInstance();
		Date date = null;
		try {
			date = formatter.parse(editText.getText().toString());
		} catch (ParseException e) {}
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		
		return calendar;
	}

	private boolean validate() {
		valid = true;
		
		if(descriptionEditText.getText().toString().isEmpty()) {
			descriptionEditText.setError("Description cannot be empty!");

			valid = false;
		}

		if(!claim.isDateRangeValid(extractDateFromEditText(startDateEditText), extractDateFromEditText(endDateEditText))) {
			//TODO: set error text etc, find a way to signal the error.

			valid = false;
		}
		
		if(valid == false) {
		}
		
		invalidateOptionsMenu();
		
		return valid;
	}

}


