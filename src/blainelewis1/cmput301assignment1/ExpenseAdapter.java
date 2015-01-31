package blainelewis1.cmput301assignment1;

import java.text.DateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
		holder.amountText.setText(expense.getCurrency().getSymbol() + expense.getAmount());


		
	    return convertView;	
	  	}

	
}
