package blainelewis1.cmput301assignment1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ClaimManager {

	//TODO: let's move serialization to on-demand, eg a claim is changed etc. etc.
	
	private static ClaimManager instance = null;
	private static final String saveFileName = "claims.sav";
	public static final String CLAIM_ID_STRING = "CLAIM_ID";
	public static final String EXPENSE_ID_STRING = "EXPENSE_ID";
	public static final String IS_NEW = "IS_NEW";
	
	
	private ArrayList<Claim> claims;

	protected ClaimManager() {

	}

	public static ClaimManager getInstance() {
		if (instance == null) {
			instance = new ClaimManager();
		}
		return instance;
	}

	public void addClaim(Claim claim) {
		claims.add(claim);
	}

	public Claim createNewClaim() {
		Claim claim = new Claim();

		addClaim(claim);

		return claim;
	}

	public Claim getClaimById(String id) {
		for (Claim claim : claims) {
			if (claim.getId().equals(id)) {
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

		if (this.claims == null) {
			return;
		}

		Gson gson = new Gson();

		try {

			OutputStreamWriter writer = new OutputStreamWriter(
					context.openFileOutput(saveFileName, Context.MODE_PRIVATE));

			gson.toJson(claims, writer);

			writer.close();

		} catch (IOException e) {
			// Fail silently, we can't do anything about this...
			Log.e("Error", "Serialize failed", e);
		}

	}

	public void deserialize(Context context) {

		if (this.claims != null) {
			return;
		}

		Gson gson = new Gson();

		try {

			InputStreamReader reader = new InputStreamReader(
					context.openFileInput(saveFileName));

			Type arrayListType = new TypeToken<ArrayList<Claim>>() {
			}.getType();

			claims = gson.fromJson(reader, arrayListType);

			reader.close();

		} catch (IOException e) {
			// fail silently there's nothing else we can do
			Log.e("Error", "Deserialize failed", e);
		}

		if (claims == null) {
			claims = new ArrayList<Claim>();
		}

	}

	public Claim extractClaim(Bundle savedInstanceState, Intent intent) {
		String id = "";

		if (savedInstanceState == null) {
			id = intent.getStringExtra(CLAIM_ID_STRING);
		} else {
			id = savedInstanceState.getString(CLAIM_ID_STRING);
		}

		return getClaimById(id);

	}
	
	public Expense extractExpense(Bundle savedInstanceState, Intent intent, Claim claim) {
		
		String id = "";	
		
		if (savedInstanceState == null) {
			id = intent.getStringExtra(EXPENSE_ID_STRING);
		} else {
			id = savedInstanceState.getString(EXPENSE_ID_STRING);
		}

		return claim.getExpenseByID(id);
	}


	public Intent getCreateExpenseIntent(Context context, Claim claim) {

		Expense expense = createNewExpense(claim);
		
		Intent intent = getEditExpenseIntent(context, claim, expense);
		intent.putExtra(IS_NEW, true);
		return intent;
	}

	public Intent getEditExpenseIntent(Context context, Claim claim,
			Expense expense) {

		Intent intent = new Intent(context, EditExpenseActivity.class);

		intent.putExtra(EXPENSE_ID_STRING, expense.getId());
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

		Intent intent = getEditClaimIntent(context, claim);
		intent.putExtra(IS_NEW, true);
		return intent;	
	}

	public Intent getViewClaimIntent(Context context,
			Claim claim) {
		
		Intent intent = new Intent(context, ViewClaimActivity.class);

		intent.putExtra(CLAIM_ID_STRING, claim.getId());
		return intent;
	}

	public ArrayList<Claim> getClaims() {
		return claims;
	}

	public boolean extractIsNew(Bundle savedInstanceState, Intent intent) {
		if(savedInstanceState != null) {
			return savedInstanceState.getBoolean(ClaimManager.IS_NEW);
		} else {
			return intent.getBooleanExtra(ClaimManager.IS_NEW, false);
		}
	}

}
