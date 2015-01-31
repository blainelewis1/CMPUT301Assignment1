package blainelewis1.cmput301assignment1;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
		
		setStartCalendar(startCalendar);
		setEndCalendar(endCalendar);
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

	public void setStartCalendar(Calendar startCalendar) {
		if(startCalendar == null) {
			throw new IllegalArgumentException("Start calendar cannot be null!");
		}
		
		if(!this.isEditable()){
			throw new IllegalArgumentException("Claim is not in an editable state.");
		}
		
		if(endCalendar != null && startCalendar.compareTo(this.endCalendar) > 0){
			throw new IllegalArgumentException("Start calendar must be before end calendar.");
		}
		
		this.startCalendar = startCalendar;
	}

	public Calendar getEndCalendar() {
		return endCalendar;
	}

	public void setEndCalendar(Calendar endCalendar) {
		if(endCalendar == null) {
			throw new IllegalArgumentException("End calendar cannot be null!");
		}
		
		if(!this.isEditable()){
			throw new IllegalStateException("Claim is not in an editable state.");
		}
		
		 if(startCalendar != null && endCalendar.compareTo(this.startCalendar) < 0){
		 
			throw new IllegalArgumentException("Start calendar must be before end calendar.");
		}
		
		
		this.endCalendar = endCalendar;
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
		HashMap<String, Double> totals = new HashMap<String, Double>();
		
		for(Expense expense : expenses) {
			Double current = totals.get(expense.getCurrency());
			
			if(current == null) {
				totals.put(expense.getCurrency().getSymbol(), expense.getAmount());
			} else { 
				totals.put(expense.getCurrency().getSymbol(), expense.getAmount() + current);
			}

		}

		ArrayList<String> totalStrings = new ArrayList<String>();
		
		for(Entry<String, Double> total : totals.entrySet()) {
			totalStrings.add(total.getKey() + 
							String.valueOf(total.getValue()));
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
		//.append("<table>");
		
		for(Expense expense : expenses) {
			sb.append(expense.getHTMLRepresentation());
		}
		
		//sb.append("</table>");
		
		return sb.toString();
	}

	public boolean isDateRangeValid(Calendar startDate,
			Calendar endDate) {
		return startDate.compareTo(endDate) <= 0 && endDate.compareTo(startDate) >= 0;
	}

}
