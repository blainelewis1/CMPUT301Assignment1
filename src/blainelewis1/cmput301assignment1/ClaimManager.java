package blainelewis1.cmput301assignment1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ClaimManager {
	
	   private static ClaimManager instance = null;
	   private static final String saveFileName = "claims.sav";
	   private static final String CLAIM_ID_STRING = "CLAIM_ID";
	private static final String EDIT_EXPENSE_ID = "EXPENSE_ID";
	   
	   private ArrayList<Claim> claims;
	   
	   protected ClaimManager() {
		   
		   /*
		   claims = new ArrayList<Claim>();
		   
		   Claim claim1 = new Claim(Calendar.getInstance(), Calendar.getInstance(), "Hello World!");
		   claim1.addExpense(new Expense(Calendar.getInstance(), "fuel", "Bought food", 70, Currency.getInstance("CAD")));
		   claim1.addExpense(new Expense(Calendar.getInstance(), "fuel", "Bought more food", 50, Currency.getInstance("CAD")));
		   
		   claims.add(claim1);
		   claims.add(new Claim(Calendar.getInstance(), Calendar.getInstance(), "YES"));
		   */
	   }
	   
	   public static ClaimManager getInstance() {
	      if(instance == null) {
	         instance = new ClaimManager();
	      }
	      return instance;
	   }
	   
	   public void addClaim(Claim claim) {
		   claims.add(claim);
	   }

	public ArrayList<Claim> getClaimsSortedByStartDate() {
		
		Collections.sort(claims, new Comparator<Claim>() {
	        @Override
	        public int compare(Claim  claim1, Claim  claim2)
	        {
	            return  claim1.getStartCalendar().compareTo(claim2.getStartCalendar());
	        }
	    });
		
		return claims;
	}
	
	public Claim createNewClaim() {
		Claim claim = new Claim();
		
		addClaim(claim);
		
		return claim;
	}
	
	public Claim getClaimById(String id){
		for (Claim claim : claims) {
			if(claim.getId().equals(id)) {
				return claim;
			}
		}
		
		return null;
	}

	public Expense createNewExpense(Claim claim) {
		Expense expense = new Expense(claim);
		
		claim.addExpense(expense);
		
		return expense;
	}

	public void deleteClaim(Claim claim) {		
		claims.remove(claim);
	}

	public void serialize(Context context) {
		
		if(this.claims == null) {
			return;
		}
		
		Gson gson = new Gson();
		
		try {
			
			OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(saveFileName, Context.MODE_PRIVATE));
			
			gson.toJson(claims, writer);
			
			writer.close();
			
		} catch (IOException e) {
			//TODO: what to do in error case
			Log.e("Error", "Serialize failed", e);
		}
		
	}

	public void deserialize(Context context) {

		if(this.claims != null) {
			return;
		}
		
		Gson gson = new Gson();

		try {
			
			InputStreamReader reader = new InputStreamReader(context.openFileInput(saveFileName));

			Type arrayListType = new TypeToken<ArrayList<Claim>>(){}.getType();
						
			claims = gson.fromJson(reader, arrayListType);
			
			reader.close();
			
		} catch (IOException e) {
			//TODO: exception what to do
			Log.e("Error", "Deserialize failed", e);		
		}
		
		if(claims == null) {
			claims = new ArrayList<Claim>();
		}

		
		
	}

	public Claim extractClaim(Bundle savedInstanceState, Intent intent) {
		Claim claim = null;
		
		if(savedInstanceState == null) {
			claim = getClaimById(intent.getStringExtra(CLAIM_ID_STRING));
		} else {
			claim = getClaimById(savedInstanceState.getString(CLAIM_ID_STRING));
		}
		
		return claim;
		
	}
	
	public void attachClaim(Intent intent, Claim claim) {
        
		intent.putExtra(CLAIM_ID_STRING, claim.getId());
        
	}
	public void attachClaim(Bundle bundle, Claim claim) {
		bundle.putString(CLAIM_ID_STRING, claim.getId());
		
	}

	//These get XXXX Intents decouples the activities from each other
	//while maintaining a reliable intent communication and less coupling between the model and the view
	
	//This prevents dangers such as the activity for 
	//editting a claim being different from that of creating a new one.
	
	public Intent getCreateExpenseIntent(Context context, Claim claim) {
		
		Expense expense = createNewExpense(claim);
		
		return getEditExpenseIntent(context, claim, expense);
	}
	public Intent getEditExpenseIntent(Context context, Claim claim, Expense expense) {
	
		Intent intent = new Intent(context, ExpenseActivity.class);
		
		intent.putExtra(EDIT_EXPENSE_ID, expense.getId());
		intent.putExtra(CLAIM_ID_STRING, claim.getId());

		
		return intent;
	}

	public Intent getEditClaimIntent(Context context, Claim claim) {
		Intent intent = new Intent(context, EditClaimActivity.class);

		intent.putExtra(CLAIM_ID_STRING, claim.getId());
		return intent;
	}

	public Intent getCreateClaimIntent(Context context) {
		Claim claim = createNewClaim();
		
		return getEditClaimIntent(context, claim);
	}

}
