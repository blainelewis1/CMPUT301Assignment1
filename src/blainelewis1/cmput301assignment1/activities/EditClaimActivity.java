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

package blainelewis1.cmput301assignment1.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import models.Claim;
import blainelewis1.cmput301assignment1.ClaimManager;
import blainelewis1.cmput301assignment1.R;
import blainelewis1.cmput301assignment1.R.id;
import blainelewis1.cmput301assignment1.R.layout;
import blainelewis1.cmput301assignment1.R.menu;
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


/*
 * This activity allows for editing of new and existing claims
 * 
 * If the claim is a new claim, and the back button is pressed then it is thrown away
 * 
 * Otherwise you are forwarded to the ViewClaimActivity
 * 
 * But if you are existing an old activity it simply closes and throws away your changes
 * 
 */

public class EditClaimActivity extends Activity {

	private Claim claim;

	private EditText descriptionEditText;
	private EditText startDateEditText;
	private EditText endDateEditText;

	private DatePickerDialog startDatePickerDialog;
	private DatePickerDialog endDatePickerDialog;

	//TODO: should valid really be here?
	//private boolean valid;

	private LinearLayout layout;

	private boolean isNew;

	/*
	 * On creation we begin by loading the claim and determining if it is a new claim
	 * Then we attach all views by their ids
	 * Initialize them 
	 * and apply listeners to them
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit_claim);

		ClaimManager claimManager = ClaimManager.getInstance();

		claim = claimManager.extractClaim(savedInstanceState, getIntent());
		isNew = claimManager.extractIsNew(savedInstanceState, getIntent());

		if(isNew) {
			setTitle("New Claim");
		}
		
		findViewsByIds();
		initViews();
		setListeners();

		//validate();
	}

	/*
	 * When the back button is pressed we need to determine if it was a new claim, 
	 * If it was we delete it.
	 */
	
	@Override
	public void onBackPressed() {
		if (isNew) {
			ClaimManager.getInstance().deleteClaim(claim);
		}
		super.onBackPressed();
	}


	
	/*
	 * 
	 * Apply listeners to the various input fields
	 * 
	 * Date fields to open a datepicker dialog 
	 * 
	 * And a textwatcher to the textviews to allow for on the fly validation
	 * 
	 */
	

	private void setListeners() {

		//TODO: this might be a little bit too much
		
		TextWatcher validatingTextWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.equals(descriptionEditText)) {
					setTitle(descriptionEditText.getText());
				}
				
				validate();
			}

		};
		
		//For validation

		startDateEditText.addTextChangedListener(validatingTextWatcher);
		descriptionEditText.addTextChangedListener(validatingTextWatcher);
		endDateEditText.addTextChangedListener(validatingTextWatcher);

		
		//When focus goes to the date fields, open a datepickerdialog

		startDateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {

					Calendar calendar = extractDateFromEditText(startDateEditText);

					startDatePickerDialog.updateDate(
							calendar.get(Calendar.YEAR),
							calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH));
					startDatePickerDialog.show();
				}

			}

		});

		endDateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {

					Calendar calendar = extractDateFromEditText(endDateEditText);

					endDatePickerDialog.updateDate(calendar.get(Calendar.YEAR),
							calendar.get(Calendar.MONTH),
							calendar.get(Calendar.DAY_OF_MONTH));
					endDatePickerDialog.show();
				}

			}

		});
		
		
		Calendar startCalendar = claim.getStartCalendar();

		//Apply chosen date after the dialog closes
		
		startDatePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

						startDateEditText.setText(formatDate(year, monthOfYear,
								dayOfMonth));

						startDateEditText.clearFocus();
						layout.requestFocus();

					}

				}, startCalendar.get(Calendar.YEAR),
				startCalendar.get(Calendar.MONTH),
				startCalendar.get(Calendar.DAY_OF_MONTH));

		Calendar endCalendar = claim.getEndCalendar();

		endDatePickerDialog = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

						endDateEditText.setText(formatDate(year, monthOfYear,
								dayOfMonth));

						startDateEditText.clearFocus();
						layout.requestFocus();
					}

				}, endCalendar.get(Calendar.YEAR),
				endCalendar.get(Calendar.MONTH),
				endCalendar.get(Calendar.DAY_OF_MONTH));

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_finish_edit_claim) {
			submitEdits();
		} else if (id == android.R.id.home && isNew) {
			ClaimManager.getInstance().deleteClaim(claim);
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * If the edits are valid they are applied to the model and serialized
	 * If it is a new claim, we open a ViewClaimActivity with it
	 * Otherwise we just finish
	 */
	
	private void submitEdits() {

		if (validate()) {

			claim.setDescription(descriptionEditText.getText().toString());
			claim.setDateRange(extractDateFromEditText(startDateEditText),
					extractDateFromEditText(endDateEditText));

			ClaimManager claimManager = ClaimManager.getInstance();

			claimManager.serialize(this);

			finish();

			if (isNew) {
				startActivity(claimManager.getViewClaimIntent(this, claim));
			}

		}
	}
	
	/* 
	 * Validates all fields and if they are invalid toasts the user what is wrongs
	 */

	private boolean validate() {
		boolean valid = true;

		if (descriptionEditText.getText().toString().isEmpty()) {
			//descriptionEditText.setError("Description cannot be empty!");

			Toast toast = Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT);
			toast.show();
			
			valid = false;
		}

		if (!claim.isDateRangeValid(extractDateFromEditText(startDateEditText),
				extractDateFromEditText(endDateEditText))) {
			
			Toast toast = Toast.makeText(this, "Start date must be before end date.", Toast.LENGTH_SHORT);
			toast.show();
			
			
			valid = false;
		}


		return valid;
	}


	/*
	 * Initialize all the fields to the claim's values
	 */
	
	private void initViews() {
		DateFormat formatter = DateFormat.getDateInstance();

		startDateEditText.setText(formatter.format(claim.getStartCalendar()
				.getTime()));
		endDateEditText.setText(formatter.format(claim.getEndCalendar()
				.getTime()));

		descriptionEditText.setText(claim.getDescription());

	}

	/*
	 * Finds all the views by their ids
	 */

	private void findViewsByIds() {
		descriptionEditText = (EditText) findViewById(R.id.edit_claim_description);
		startDateEditText = (EditText) findViewById(R.id.edit_claim_start_date);
		endDateEditText = (EditText) findViewById(R.id.edit_claim_end_date);
		layout = (LinearLayout) findViewById(R.id.edit_claim_layout);
	}

	/*
	 *Inflates the options menu 
	 * 
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_claim, menu);

		return true;
	}
	
	/*
	 * Takes an edittext and uses the users Locale to extract a date from it
	 */
	
	private Calendar extractDateFromEditText(EditText editText) {
		DateFormat formatter = DateFormat.getDateInstance();
		Date date = null;
		try {
			date = formatter.parse(editText.getText().toString());
		} catch (ParseException e) {
		}

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		return calendar;
	}
	
	//Helper function for formatting dates
	
	private String formatDate(int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = claim.getStartCalendar();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, monthOfYear);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		return DateFormat.getDateInstance().format(calendar.getTime());
	}



}



