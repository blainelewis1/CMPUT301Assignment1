package blainelewis1.cmput301assignment1;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;


public class Claim {

	/*
	 * A travel claim has a:
	 * 	calendar range and a 
	 *  textual description (e.g., destination and reason for travel)
	 * 
	*/
	
	enum Status {IN_PROGRESS, SUBMITTED, RETURNED, APPROVED};
		
	private Calendar startCalendar;
	private Calendar endCalendar;
	String description;
	private Status status;

	private String id;
	
	ArrayList<Expense> expenses;
	
	public Claim(Calendar startCalendar, Calendar endCalendar, String description){
		status = Status.IN_PROGRESS;
		
		id = UUID.randomUUID().toString();
		
		setDateRange(startCalendar, endCalendar);
		setDescription(description);
		
		expenses = new ArrayList<Expense>();
	}
	
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
	
	public void removeExpense(Expense expense) {
		if(!this.isEditable()){
			throw new IllegalStateException("Claim is not in an editable state.");
		}
		
		expenses.remove(expense);
	}

	public Calendar getStartCalendar() {
		return startCalendar;
	}

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
		
		this.startCalendar = startCalendar;
		this.endCalendar = endCalendar;
	}

	public Calendar getEndCalendar() {
		return endCalendar;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if(!this.isEditable()){
			throw new IllegalStateException("Claim is not in an editable state.");
		}
		this.description = description;
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

	public ArrayList<String> getTotalsAsStrings() {
		HashMap<String, BigDecimal> totals = new HashMap<String, BigDecimal>();
		
		for(Expense expense : expenses) {
			BigDecimal current = totals.get(expense.getCurrency());
			
			if(current == null) {
				totals.put(expense.getCurrency().getSymbol(), expense.getAmount());
			} else { 
				totals.put(expense.getCurrency().getSymbol(), expense.getAmount().add(current));
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

	public Expense getExpenseByID(String expenseId) {
		
		for (Expense expense : expenses) {
			if(expense.getId().equals(expenseId)) {
				return expense;
			}
		}
		
		return null;	
	}

	public void deleteExpense(Expense expense) {
		expenses.remove(expense);
	}
	
	public String getFormattedDateRange() {
		DateFormat formatter = DateFormat.getDateInstance();

		return formatter.format(startCalendar.getTime()) + " - " + formatter.format(endCalendar.getTime());
	}

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

	public boolean isDateRangeValid(Calendar startCalendar,
			Calendar endCalendar) {
		return startCalendar.compareTo(endCalendar) <= 0 && endCalendar.compareTo(startCalendar) >= 0;
	}

}
