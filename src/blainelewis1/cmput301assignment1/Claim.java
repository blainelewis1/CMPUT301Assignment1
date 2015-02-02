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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;


/*
 * This is a model for representing a claim, it also provides methods for nice representation of it
 * 
 * A claim is not modifiable if it's status is SUBMITTED or APPROVED and will throw an exception if you attemot to change it
 * 
 * The biggest design decision here was to not allow mutation of the start and end calendar separately
 * This allows easier validation, and it is essentially a range, not a start and end,
 * so it makes sense to set both at the same time.
 * 
 */

public class Claim {

	/*
	 * A travel claim has a:
	 * 	calendar range and a 
	 *  textual description (e.g., destination and reason for travel)
	 * 
	*/
	
	enum Status {IN_PROGRESS, SUBMITTED, RETURNED, APPROVED};
		
	private Calendar start;
	private Calendar end;
	private String description;
	private Status status;
	private String id;
	private ArrayList<Expense> expenses;
	
	public Claim(Calendar startCalendar, Calendar endCalendar, String description){
		status = Status.IN_PROGRESS;
		
		id = UUID.randomUUID().toString();
		
		setDateRange(startCalendar, endCalendar);
		setDescription(description);
		
		expenses = new ArrayList<Expense>();
	}
	
	/*
	 * Constructor to make creating default claims easy
	 */
	
	public Claim() {
		this(Calendar.getInstance(), Calendar.getInstance(), "");
	}

	public void addExpense(Expense expense){
		if(expense == null) {
			throw new IllegalArgumentException("Expense cannot be null!");
		}
		
		if(!this.isEditable()){
			throw new IllegalStateException("Claim is not in an editable state.");
		}
		
		expenses.add(expense);
	}

	public void deleteExpense(Expense expense) {
		if(!this.isEditable()){
			throw new IllegalStateException("Claim is not in an editable state.");
		}
		
		expenses.remove(expense);
	}
	
	public void removeExpense(Expense expense) {
		if(!this.isEditable()){
			throw new IllegalStateException("Claim is not in an editable state.");
		}
		
		expenses.remove(expense);
	}

	public Calendar getStart() {
		return start;
	}
	
	public Calendar getEnd() {
		return end;
	}

	/*
	 * End calendar and start calendar are set at the same time because they represent a range, and 
	 * one is not valid without the other
	 */

	public void setDateRange(Calendar startCalendar, Calendar endCalendar) {
		if(startCalendar == null) {
			throw new IllegalArgumentException("Start calendar cannot be null!");
		}
		if(endCalendar == null) {
			throw new IllegalArgumentException("End calendar cannot be null!");
		}
		
		if(!this.isEditable()){
			throw new IllegalArgumentException("Claim is not in an editable state.");
		}
		
		if(!isDateRangeValid(startCalendar, endCalendar)){
			throw new IllegalArgumentException("Start calendar must be before end calendar.");
		}
		
		this.start = startCalendar;
		this.end = endCalendar;
	}
	

	/*
	 * Tests if the date range is valid
	 */
	
	public boolean isDateRangeValid(Calendar startCalendar,
			Calendar endCalendar) {
		return startCalendar.compareTo(endCalendar) <= 0 && endCalendar.compareTo(startCalendar) >= 0;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if(!this.isEditable()){
			throw new IllegalStateException("Claim is not in an editable state.");
		}
		this.description = description.trim();
	}
	
	public boolean isEditable() {
		if (status == Status.IN_PROGRESS || status == Status.RETURNED) {
			return true;
		}
		return false;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getStatusString() {
		switch (status) {
		case APPROVED:
			return "Approved";
		case IN_PROGRESS:
			return "In Progress";
		case RETURNED:
			return "Returned";
		case SUBMITTED:
			return "Submitted";
		default:
			return "";
		}
		
	}

	public String getId() {
		return this.id;
	}

	/*
	 * Returns a list of the totals of all expenses, formatted and localized.
	 */
	
	public ArrayList<String> getTotalsAsStrings() {
		HashMap<String, BigDecimal> totals = new HashMap<String, BigDecimal>();
		
		for(Expense expense : expenses) {
			BigDecimal current = totals.get(expense.getCurrency());
			
			if(current == null) {
				totals.put(expense.getCurrency().getCurrencyCode(), expense.getAmount());
			} else { 
				totals.put(expense.getCurrency().getCurrencyCode(), expense.getAmount().add(current));
			}

		}

		ArrayList<String> totalStrings = new ArrayList<String>();
		
		for(Entry<String, BigDecimal> total : totals.entrySet()) {
			totalStrings.add(Expense.getReadableCurrency(total.getValue(), Currency.getInstance(total.getKey())));
		}
		
		
		return totalStrings;

	}

	public ArrayList<Expense> getExpenses() {
		return expenses;
	}

	/*
	 * Searches it's expenses for an expense with that id
	 */
	
	public Expense getExpenseById(String expenseId) {
		
		for (Expense expense : expenses) {
			if(expense.getId().equals(expenseId)) {
				return expense;
			}
		}
		
		return null;	
	}
	
	/*
	 * Returns the date range localized 
	 */
	
	public String getFormattedDateRange() {
		DateFormat formatter = DateFormat.getDateInstance();

		return formatter.format(start.getTime()) + " - " + formatter.format(end.getTime());
	}
	
	/*
	 * Displays this claim and it's expenses as HTML
	 */
	
	public String getHTMLRepresentation() {		
		
		StringBuilder sb = new StringBuilder();
		sb.append("<h1>").append(description).append("</h1>")
		.append("<h3>").append(getFormattedDateRange()).append("</h3>")
		.append("<h3>").append(getStatusString()).append("</h3>");
		
		for(Expense expense : expenses) {
			sb.append("<hr />").
			append(expense.getHTMLRepresentation());
		}
				
		return sb.toString();
	}


}
