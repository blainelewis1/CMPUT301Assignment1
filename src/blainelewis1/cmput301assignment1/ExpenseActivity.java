package blainelewis1.cmput301assignment1;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Currency;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class ExpenseActivity extends SerializingActivity {

	//TODO: make categories finite

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		//TODO: remove limit date between claim range
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expense);
		
		loadClaimAndExpense(savedInstanceState);
		findViewsByIds();
		initViews();
		setListeners();
		update();
		
	}

	private void setListeners() {
		//TODO: validate data
		
		categorySpinner.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					expense.setCategory((String) categorySpinner.getSelectedItem());
					update();
					//TODO: there's a bug if you click submit to quickly without focus change.......
				}
			}
		});
		
		currencySpinner.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					expense.setCurrency((Currency) currencySpinner.getSelectedItem());
					update();
				}
				
			}
		});
		
		
		descriptionEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					
					try {
						expense.setDescription(descriptionEditText.getText().toString());
					} catch (IllegalArgumentException e) {
						
						descriptionEditText.setError(e.getMessage());
						Toast toast = Toast.makeText(ExpenseActivity.this, e.getMessage(), Toast.LENGTH_SHORT);		
						toast.show();
				
					}
				} //TODO: what to do about new expenses being labelled new expense
			}
		});
		
		amountEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					
					try {
						expense.setAmount(Double.parseDouble(amountEditText.getText().toString()));
						
					} catch (IllegalArgumentException e) {
						
						amountEditText.setError(e.getMessage());
						Toast toast = Toast.makeText(ExpenseActivity.this, e.getMessage(), Toast.LENGTH_SHORT);		
						toast.show();
				
					}
				}
				
			}
		});
		
		//TODO: don't limit date ranges
		Calendar expenseCalendar = expense.getCalendar();
		
		datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				//TODO: Proper access to getCalendar should just setcalendar...				
				Calendar calendar = expense.getCalendar();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				
				try {
					 
				} catch (IllegalArgumentException e) {
					Toast toast = Toast.makeText(ExpenseActivity.this, e.getMessage(), Toast.LENGTH_SHORT);		
					toast.show();
				}

				dateEditText.clearFocus();
				update();
				
				
			}
			
		}, expenseCalendar.get(Calendar.YEAR), expenseCalendar.get(Calendar.MONTH), expenseCalendar.get(Calendar.DAY_OF_MONTH));
		
		dateEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					datePickerDialog.show();
				}
				
			}
			
		});		
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
		
	}

	private void loadClaimAndExpense(Bundle savedInstanceState) {
		ClaimManager claimManager = ClaimManager.getInstance();
		
		if(savedInstanceState == null) {
			//Try to get claim from intent
			Intent intent = getIntent();
			claim = claimManager.getClaimById(intent.getStringExtra("CLAIM_ID"));
			expense = claim.getExpenseByID(intent.getStringExtra("EXPENSE_ID"));
			
		} else {
			claim = claimManager.getClaimById(savedInstanceState.getString("CLAIM_ID"));
			expense = claim.getExpenseByID(savedInstanceState.getString("EXPENSE_ID"));
		}
	}
	
	private void findViewsByIds() {
		
		currencySpinner = (Spinner)findViewById(R.id.edit_expense_currency_spinner);
		dateEditText = (EditText) findViewById(R.id.edit_expense_date);
		descriptionEditText = (EditText) findViewById(R.id.edit_expense_description);
		amountEditText = (EditText) findViewById(R.id.edit_expense_amount);
		categorySpinner = (Spinner) findViewById(R.id.edit_expense_category);
		
	}
	
	private void update() {
		
		if(expense.getDescription().isEmpty()){
			setTitle("New Expense");
		} else {
			setTitle(expense.getDescription());
		}
		
		currencySpinner.setSelection(currencyAdapter.getPosition(expense.getCurrency()));
				
		
		DateFormat formatter = DateFormat.getDateInstance();
		
		categorySpinner.setSelection(categoryAdapter.getPosition(expense.getCategory()));
		
		dateEditText.setText(formatter.format(expense.getCalendar().getTime()));		
		descriptionEditText.setText(expense.getDescription());
		
		
		
		amountEditText.setText(String.valueOf(expense.getAmount()));	
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
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void submitExpense(View view) {			
		finish();
	}
	
	public void deleteExpense() {
		claim.deleteExpense(expense);
		
		finish();
	}
	
}
