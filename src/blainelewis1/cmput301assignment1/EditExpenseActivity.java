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
import android.text.Editable;
import android.text.TextWatcher;
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
 * This activity handles creation of new expenses and editting old ones
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_expense);
		
		ClaimManager claimManager = ClaimManager.getInstance();
		
		claim = claimManager.extractClaim(savedInstanceState, getIntent());
		expense = claimManager.extractExpense(savedInstanceState, getIntent(), claim);
		isNew = claimManager.extractIsNew(savedInstanceState, getIntent());	
		
		if(isNew) {
			setTitle("New Expense");
		}
		
		findViewsByIds();
		initViews();
		setListeners();
		
	}

	private void setListeners() {

		
		TextWatcher validatingTextWatcher = new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.equals(descriptionEditText)) {
					setTitle(descriptionEditText.getText());
				}
				
				validate();
			}
			
						
		};
		

		amountEditText.addTextChangedListener(validatingTextWatcher);
		descriptionEditText.addTextChangedListener(validatingTextWatcher);
		dateEditText.addTextChangedListener(validatingTextWatcher);
		
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
				
				//TODO: this is extremely hacky forces focus where it doesn't need to be, we should focus the next item
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
	
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putString(ClaimManager.CLAIM_ID_STRING, claim.getId());
		savedInstanceState.putString(ClaimManager.EXPENSE_ID_STRING, expense.getId());
		
		if(isNew) {
			savedInstanceState.putBoolean(ClaimManager.IS_NEW, isNew);
		}
		
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
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
	
	private void findViewsByIds() {
		
		currencySpinner = (Spinner)findViewById(R.id.edit_expense_currency_spinner);
		dateEditText = (EditText) findViewById(R.id.edit_expense_date);
		descriptionEditText = (EditText) findViewById(R.id.edit_expense_description);
		amountEditText = (EditText) findViewById(R.id.edit_expense_amount);
		categorySpinner = (Spinner) findViewById(R.id.edit_expense_category);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.expense, menu);
		
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
		} else if (id == R.id.action_delete_expense) {
			deleteExpense();
		} else if(id == R.id.action_finish_edit_expense) {
			submitExpense();
		} else if(id == android.R.id.home && isNew) {
			claim.deleteExpense(expense);
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		if(isNew) {
			claim.deleteExpense(expense);
		}
		
		super.onBackPressed();
	}
	
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
	
	private boolean validate() {
		boolean valid = true;
		

		if(descriptionEditText.getText().toString().isEmpty()){
			
			//TODO: this isn't entirely true....
			//Make this a little bit more applicable

			Toast toast = Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT);
			toast.show();
			
			valid = false;
			
		} 
		
		return valid;
	}

	public void deleteExpense() {
		claim.deleteExpense(expense);
		
		ClaimManager.getInstance().serialize(this);
		
		finish();
	}
	
}
