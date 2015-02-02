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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/*
 * This class is a model for an expense. 
 * It ensures data consistency as well as providing helper methods for 
 * displaying it's fields correctly and with proper localization
 * 
 * 
 * Every expense has a unique id for easy transfer between activities
 * 
 * I use BigDecimal to avoid floating point errors
 */


public class Expense {

	/*
	 * From assignment spec: 
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
	
	//We use BigDecimal to avoid floating point errors.
	
	private BigDecimal amount;
	private Currency currency;
	private String id;
	
	//This set allows us fast checks when testing if a category is valid
	//The syntax is slightly confusing, but essentially we extend a set and \
	//then we call add multiple times on it in the default constructor
	
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
	
	
	/*
	 * Simple constructor, simply takes a bunch of arguments and applies them
	 */
	
	public Expense  (Calendar calendar, String category, String description, BigDecimal amount, Currency currency) {
		id = UUID.randomUUID().toString();
		
		setAmount(amount);
		setCategory(category);
		setCurrency(currency);
		setCalendar(calendar);
		this.description = description;	
		
	}

	/*
	 * This constructor allows us to easily create an expense with default values
	 */
	
	public Expense(Claim claim) {
		this(claim.getStart(), "meal", "", new BigDecimal("0.00"), Currency.getInstance(Locale.getDefault()));
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
		
		//Check that the category is in our set
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
		
		//Automatically trims the description of trailing and leading whitespace
		
		this.description = description.trim();
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
	
	/*
	 * This function applies localization to our currency by properly formatting the amount
	 * As well as putting the common symbol and or ISO code in front of the amount
	 */
	
	public static String getReadableCurrency(BigDecimal amount, Currency currency) {

		//http://stackoverflow.com/a/2057163/1036813 2015-02-01 Blaine Lewis
		
		//I had to use this solution because currency formats default to using parantheses for negative values
		// I Want $-20 I got ($20) without using setNegative****
		
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
		formatter.setCurrency(currency);
		String symbol = formatter.getCurrency().getSymbol();
		formatter.setNegativePrefix(symbol+"-");
		formatter.setNegativeSuffix("");
		
		return formatter.format(amount);
	}
	
	/*
	 * Returns the expense as formatted HTML 
	 */
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
