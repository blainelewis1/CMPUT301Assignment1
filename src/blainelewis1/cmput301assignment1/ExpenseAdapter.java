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

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * Simple adapter for showing claims, simply applies a custom layout to make them look nice
 * 
 * I decided to implement the "ViewHolder" pattern because eclipse suggested it...
 */

public class ExpenseAdapter extends ArrayAdapter<Expense> implements
		android.widget.ListAdapter {

	private static class ViewHolder {
		  TextView amountText;
		  TextView dateText;
		  TextView descriptionText;
	}	
	
	private Context context;
	
	public ExpenseAdapter(Context context, int resource, ArrayList<Expense> expenses) {
		super(context, resource, expenses);
		
		this.context = context;
	}

	/*
	 * Binds the expense fields to the layout to represent them. 
	 */
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			
		ViewHolder holder;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.expenses_layout, parent, false);
			
			holder = new ViewHolder();
			
			holder.amountText = (TextView) convertView.findViewById(R.id.expense_list_item_amount);
			holder.descriptionText = (TextView) convertView.findViewById(R.id.expense_list_item_description);
			holder.dateText = (TextView) convertView.findViewById(R.id.expense_list_item_date);
	    
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		Expense expense = getItem(position);
		
		holder.descriptionText.setText(expense.getDescription());
		DateFormat dateFormatter = DateFormat.getDateInstance();
		
		holder.dateText.setText(dateFormatter.format(expense.getCalendar().getTime()));
		holder.amountText.setText(expense.getReadableAmount());


		
	    return convertView;	
	  
	}

	
}
