package blainelewis1.cmput301assignment1;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;




public class ClaimAdapter extends ArrayAdapter<Claim> {

	private Context context;
	
	private static class ViewHolder {
		  TextView statusText;
		  TextView descriptionText;
	}
	
	
	public ClaimAdapter(Context context, int resource, List<Claim> objects) {
		super(context, resource, objects);
		this.context = context;
	}
	


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		ViewHolder holder;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.claim_layout, parent, false);
			
			holder = new ViewHolder();
			
			holder.statusText = (TextView) convertView.findViewById(R.id.claim_list_item_status);
			holder.descriptionText = (TextView) convertView.findViewById(R.id.claim_list_item_description);
	    
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Claim claim = getItem(position);
		
		holder.statusText.setText(claim.getStatusString());
		holder.descriptionText.setText(claim.getDescription());
		
	    return convertView;
	}
}
