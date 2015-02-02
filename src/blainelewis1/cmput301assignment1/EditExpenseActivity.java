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


package blainelewis1.cmput301assignment1;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/*
 * This activity allows for editing of new and existing expenses
 * 
 * On clicking the checkmark the activity is closed
 * 
 * If the back button is pressed and it is a new expense we delete the expense
 * 
 * Otherwise we simply throw away the changes and close
 * 
 * As in EditClaimActivity by using this activity for new and editing expenses
 * I complicated implementation at the benefit of reducing duplication
 * 
 * As well, there is a dependency on the claim that owns this expense that is not required.
 * However by including it we reduce the time required to find an expense.
 * 
 */

public class EditExpenseActivity extends Activity {
	
	private Expense expense;
	
	private Claim claim;
	private Spinner currencySpinner;
	private Spinner categorySpinner;
	private EditText dateEditText;
	private EditText descriptionEditText;
	private EditText amountEditText;
	private DatePickerDialog datePickerDialog;

	private ArrayAdapter<String> categoryAdapter;
	private ArrayAdapter<Currency> currencyAdapter;


	private boolean isNew;

	/*
	 * On creation we begin by loading the claim and its expense and determining if it is a new expense
	 * Then we attach all views by their ids
	 * Initialize them 
	 * and apply listeners to them
	 */
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_expense);
				
		claim = ActivityCommunicationUtils.extractClaim(savedInstanceState, getIntent());
		expense = ActivityCommunicationUtils.extractExpense(savedInstanceState, getIntent(), claim);
		isNew = ActivityCommunicationUtils.extractIsNew(savedInstanceState, getIntent());	
		
		//An unexpected error occurred, exit quietly
		if(claim == null || expense == null) {
			finish();
		}
		
		if(isNew) {
			setTitle("New Expense");
		} else {
			setTitle(expense.getDescription());
		}
		
		findViewsByIds();
		initViews();
		setListeners();
		
	}
	
	/*
	 * 
	 * Apply listeners to the various input fields
	 * 
	 * Date fields to open a datepicker dialog 
	 * 
	 * onFocusChange to the description for validation and changing the title
	 * 
	 */
	
	private void setListeners() {

		//Validate and edit the title after changing focus from the description
		
		descriptionEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					validate();
					setTitle(descriptionEditText.getText());
				}
			}
		});
		
		
		Calendar calendar = expense.getCalendar();
		
		datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				
				
				DateFormat formatter = DateFormat.getDateInstance();
				dateEditText.setText(formatter.format(calendar.getTime()));
				
				dateEditText.clearFocus();	
				
				validate();

			}
			
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		
		dateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					
					Calendar calendar = extractDate();
					datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
					datePickerDialog.show();
					
				}
				
			}
			
		});		
	}
	
	/* 
	 * In case android throws away our activity for memory, we save the expense so we can bring it back
	 * This could be improved by saving the changes we are making
	 */
	
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putString(ActivityCommunicationUtils.CLAIM_ID_STRING, claim.getId());
		savedInstanceState.putString(ActivityCommunicationUtils.EXPENSE_ID_STRING, expense.getId());
		
		if(isNew) {
			savedInstanceState.putBoolean(ActivityCommunicationUtils.IS_NEW, isNew);
		}
		
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}

	/*
	 * Initialize all the adapters and set the values of all fields to the expenses
	 */
	
	private void initViews() {
		Currency[] currencies = Currency.getAvailableCurrencies().toArray(new Currency[Currency.getAvailableCurrencies().size()]);
		
		
		currencyAdapter = new ArrayAdapter<Currency>(this, 
												android.R.layout.simple_spinner_item, 
												currencies);
		currencySpinner.setAdapter(currencyAdapter);
		
		currencySpinner.setSelection(currencyAdapter.getPosition(expense.getCurrency()));
		
	
		String[] categories = Expense.categories.toArray(new String[Expense.categories.size()]);
		
		categoryAdapter = new ArrayAdapter<String>(this,
													android.R.layout.simple_spinner_item,
													categories);
		
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setSelection(categoryAdapter.getPosition(expense.getCategory()));
		
		descriptionEditText.setText(expense.getDescription());
		
		DateFormat formatter = DateFormat.getDateInstance();
		
		dateEditText.setText(formatter.format(expense.getCalendar().getTime()));
		
		amountEditText.setText(String.valueOf(expense.getAmount()));
		
	}
	
	


	/*
	 * Handle action bar clicks
	 */
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		switch (id) {
		case R.id.action_settings:
			return true;
		case R.id.action_delete_expense:
			deleteExpense();
			return true;
		case R.id.action_finish_edit_expense:
			submitExpense();
			return true;
		case android.R.id.home:
			if(isNew) {
				claim.deleteExpense(expense);
			}
	        finish();
	        return true;
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * Override if it is a new expense we want to delete the expense
	 */
	
	@Override
	public void onBackPressed() {
		if(isNew) {
			claim.deleteExpense(expense);
		}
		
		super.onBackPressed();
	}
	
	/*
	 * Apply all changes made to the model and exit if the changes are valid
	 */
	
	public void submitExpense() {		
		
		if(validate()) {
			
			expense.setAmount(new BigDecimal(amountEditText.getText().toString()));
			expense.setDescription(descriptionEditText.getText().toString());
			expense.setCalendar(extractDate());			
			expense.setCategory((String) categorySpinner.getSelectedItem());
			expense.setCurrency((Currency) currencySpinner.getSelectedItem());
			
			ClaimManager.getInstance().serialize(this);
			
			finish();
		}
		
	}
	
	/*
	 * validates all the fields and toasts if they are not valid
	 */
	
	private boolean validate() {
		boolean valid = true;
		

		if(descriptionEditText.getText().toString().isEmpty()){
			
			Toast toast = Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT);
			toast.show();
			
			valid = false;
			
		} 
		
		return valid;
	}

	/*
	 * Deletes the expense we are editing and exits
	 */
	
	public void deleteExpense() {
		claim.deleteExpense(expense);
		
		ClaimManager.getInstance().serialize(this);
		
		finish();
	}
	
	/*
	 * Finds all the views by their ids
	 */
	
	private void findViewsByIds() {
		
		currencySpinner = (Spinner)findViewById(R.id.edit_expense_currency_spinner);
		dateEditText = (EditText) findViewById(R.id.edit_expense_date);
		descriptionEditText = (EditText) findViewById(R.id.edit_expense_description);
		amountEditText = (EditText) findViewById(R.id.edit_expense_amount);
		categorySpinner = (Spinner) findViewById(R.id.edit_expense_category);	
	}
	
	
	//Helper function to extract the date from its edittext
	
	private Calendar extractDate() {
		
		DateFormat formatter = DateFormat.getDateInstance();
		Date date = null;
		try {
			date = formatter.parse(dateEditText.getText().toString());
		} catch (ParseException e) {}
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.setTime(date);
		
		return calendar;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.expense, menu);
		
		return true;
	}

	
}
