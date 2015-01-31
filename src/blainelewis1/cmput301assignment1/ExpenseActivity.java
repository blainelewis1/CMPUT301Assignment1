package blainelewis1.cmput301assignment1;

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

public class ExpenseActivity extends Activity {
	
	//TODO: what if back on a new claim, maybe pass flag in intent, because it needs to be deleted
	//TODO: clicking back is going back for some reason....
	
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

	private boolean valid;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expense);
		
		claim = ClaimManager.getInstance().extractClaim(savedInstanceState, getIntent());
		expense = ClaimManager.getInstance().extractExpense(savedInstanceState, getIntent(), claim);

		
		findViewsByIds();
		initViews();
		setListeners();
		
	}

	private void setListeners() {
				
		OnFocusChangeListener validatingFocusChange = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				validate();
			}

		};
		
		descriptionEditText.setOnFocusChangeListener(validatingFocusChange);
		amountEditText.setOnFocusChangeListener(validatingFocusChange);

		Calendar calendar = expense.getCalendar();
		
		datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				
				
				DateFormat formatter = DateFormat.getInstance();
				dateEditText.setText(formatter.format(calendar.getTime()));
				
				//TODO: this is extremely hacky forces focus where it doesn't need to be, we should focus the next item
				dateEditText.clearFocus();				
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
	
	private Calendar extractDate() {
		
		DateFormat formatter = DateFormat.getInstance();
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
		
		categoryAdapter = new ArrayAdapter<String>(this,
													android.R.layout.simple_spinner_item,
													Expense.categories.toArray(new String[Expense.categories.size()]));
		
		categorySpinner.setAdapter(categoryAdapter);
		
		descriptionEditText.setText(expense.getDescription());
		
		DateFormat formatter = DateFormat.getInstance();
		
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

		menu.findItem(R.id.action_finish_edit_expense).setVisible(valid);
		
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
		}
		
		
		return super.onOptionsItemSelected(item);
	}
	
	public void submitExpense() {		
		
		if(validate()) {

			
			expense.setAmount(Integer.parseInt(amountEditText.getText().toString()));
			expense.setDescription(descriptionEditText.getText().toString());
			expense.setCalendar(extractDate());			
			expense.setCategory((String) categorySpinner.getSelectedItem());
			expense.setCurrency((Currency) currencySpinner.getSelectedItem());
			
			ClaimManager.getInstance().serialize(this);
			
			finish();
		}
		
	}
	
	private boolean validate() {
		valid = true;
		

		if(descriptionEditText.getText().toString().isEmpty()){
			
			//TODO: this isn't entirely true....
			//Make this a little bit more applicable
			
			setTitle("New Expense");

			descriptionEditText.setError("Description cannot be empty!");
			
			valid = false;
			
		} else {
			setTitle(descriptionEditText.toString());
		}
		
		invalidateOptionsMenu();
		
		return valid;
	}

	public void deleteExpense() {
		claim.deleteExpense(expense);
		
		ClaimManager.getInstance().serialize(this);
		
		finish();
	}
	
}
