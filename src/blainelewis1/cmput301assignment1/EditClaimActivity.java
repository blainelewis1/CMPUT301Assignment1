package blainelewis1.cmput301assignment1;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//TODO: validate is being called too often

public class EditClaimActivity extends Activity {		
		
		private Claim claim;
		
		
		private EditText descriptionEditText;
		private EditText startDate;
		private EditText endDate;
		
		private DatePickerDialog startDatePickerDialog;
		private DatePickerDialog endDatePickerDialog;


		private boolean valid;


		private LinearLayout layout;		
	
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.activity_edit_claim);
		
		claim = ClaimManager.getInstance().extractClaim(savedInstanceState, getIntent());

		findViewsByIds();
		initViews();
		setListeners();
	
		validate();
	}
	
	private void initViews() {
		DateFormat formatter = DateFormat.getInstance();
		
		startDate.setText(formatter.format(claim.getStartCalendar().getTime()));
		endDate.setText(formatter.format(claim.getEndCalendar().getTime()));
		
		descriptionEditText.setText(claim.getDescription());

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
				
				startDate.setText(formatDate(year, monthOfYear, dayOfMonth));
				
				//TODO: less hacky but it really doesn't work...
				startDate.clearFocus();
				layout.requestFocus();
				
				validate();
								
			}
			
		}, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH));
		
		Calendar endCalendar = claim.getEndCalendar();
		
		endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				endDate.setText(formatDate(year, monthOfYear, dayOfMonth));

				startDate.clearFocus();
				layout.requestFocus();				
				validate();
				
			}
			
		}, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH));
		
		startDate.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					
					Calendar calendar = extractDateFromEditText(startDate);
					
					
					startDatePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					startDatePickerDialog.show();
					validate();
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
					validate();
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
		
		return DateFormat.getInstance().format(calendar.getTime());
	}

	private void findViewsByIds() {
		descriptionEditText = (EditText) findViewById(R.id.edit_claim_description);
		startDate = (EditText) findViewById(R.id.edit_claim_start_date);
		endDate = (EditText) findViewById(R.id.edit_claim_end_date);
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
		}
		return super.onOptionsItemSelected(item);
	}	

	private void submitEdits() {	
		
		if(validate()) {
			
			claim.setDescription(descriptionEditText.getText().toString());
			claim.setEndCalendar(extractDateFromEditText(endDate));
			claim.setStartCalendar(extractDateFromEditText(startDate));
			
			ClaimManager.getInstance().serialize(this);
						
			finish();
			
		} else {
			//TODO: remove me
			Toast toast = Toast.makeText(this, "yoooo", Toast.LENGTH_SHORT);
			toast.show();
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
		valid = true;
		
		if(descriptionEditText.getText().toString().isEmpty()) {
			descriptionEditText.setError("Description cannot be empty!");
			Log.e("uhhh", "description!!!!");

			valid = false;
		}

		if(!claim.isDateRangeValid(extractDateFromEditText(startDate), extractDateFromEditText(endDate))) {
			//TODO: set error text etc.
			Log.e("uhhh", "claim range....!!!!");

			valid = false;
		}
		
		if(valid == false) {
		}
		
		invalidateOptionsMenu();
		
		return valid;
	}

}


