package blainelewis1.cmput301assignment1;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


public class Expense {

	/*
	 * 
	 * An expense item has a:
	 * calendar, 
	 * category (e.g., air fare, ground transport, vehicle rental, fuel, parking, registration, accommodation, meal), 
	 * textual description, 
	 * amount spent, 
	 * and unit of currency (e.g., CAD, USD, EUR, GBP, etc.).
	 */

	private Calendar calendar;
	private String category;
	private String description;
	private BigDecimal amount;
	private Currency currency;
	private String id;
	
	public static final Set<String> categories = Collections.unmodifiableSet(
			new HashSet<String>()
			{
				private static final long serialVersionUID = 1L;
				{
				add("air fare"); 
				add("ground transport"); 
				add("vehicle rental"); 
				add("fuel"); 
				add("parking"); 
				add("registration"); 
				add("accommodation");
				add("meal");
				}
			}
		);
	
	

	
	public Expense  (Calendar calendar, String category, String description, BigDecimal amount, Currency currency) {
		id = UUID.randomUUID().toString();
		
		setAmount(amount);
		setCategory(category);
		setCurrency(currency);
		setCalendar(calendar);
		this.description = description;	
		
	}

	public Expense(Claim claim) {
		this(claim.getStartCalendar(), "meal", "", new BigDecimal("0.00"), Currency.getInstance(Locale.getDefault()));
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		if(calendar == null) {
			throw new IllegalArgumentException("Calendar cannot be null!");
		}
		
		this.calendar = calendar;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		if(category == null) {
			throw new IllegalArgumentException("Category cannot be null!");
		}
		
		if(!categories.contains(category)) {
			throw new IllegalArgumentException("Invalid Category!");
		}
		
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if(description == null || description == "") {
			throw new IllegalArgumentException("Description cannot be empty!");
		}
		
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		if(currency == null) {
			throw new IllegalArgumentException("Currency cannot be null!");
		}
		
		this.currency = currency;
	}

	public String getId() {
		return id;
	}

	public String getReadableAmount() {
		return getReadableCurrency(amount, currency); 
		
	}
	public static String getReadableCurrency(BigDecimal amount, Currency currency) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		formatter.setCurrency(currency);
		formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
		formatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
		return formatter.format(amount);
	}
	
	public String getHTMLRepresentation() {
		StringBuilder sb = new StringBuilder();
		
		DateFormat formatter = DateFormat.getDateInstance();

		//TODO: make this prettier
		
		sb.append("<div class=\"expense\">")
		.append("<h2 class=\"description\">").append(description).append("</h2>")
		.append("<p class=\"date\">").append(formatter.format(calendar.getTime())).append("</p>")
		.append("<p class=\"category\">").append(category).append("</p>")
		.append("<p class=\"amount\">").append(getReadableAmount()).append("</p>")
		.append("</div>");
	
		return sb.toString();
		
	}
}
