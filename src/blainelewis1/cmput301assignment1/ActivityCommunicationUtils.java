package blainelewis1.cmput301assignment1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/* 
 * This class contains a series of utility functions that handle inter activity 
 * communication as well as decoupling the activities from each other, allowing 
 * easy plug and play of different views
 */

public abstract class ActivityCommunicationUtils {

	public static final String CLAIM_ID_STRING = "CLAIM_ID";
	public static final String EXPENSE_ID_STRING = "EXPENSE_ID";
	public static final String IS_NEW = "IS_NEW";
	
	/*
	 * Given an intent and bundle it will determine which to extract a claim id
	 * from and then get the claim corresponding to said id
	 */
	
	public static Claim extractClaim(Bundle savedInstanceState, Intent intent) {
		String id = "";

		if (savedInstanceState == null) {
			id = intent.getStringExtra(CLAIM_ID_STRING);
		} else {
			id = savedInstanceState.getString(CLAIM_ID_STRING);
		}

		return ClaimManager.getInstance().getClaimById(id);

	}
	
	/*
	 * Given an intent, bundle and claim it will determine which to extract a expense id
	 * from and then get the expense corresponding to said id
	 */
	
	public static Expense extractExpense(Bundle savedInstanceState, Intent intent, Claim claim) {
		
		String id = "";	
		
		if (savedInstanceState == null) {
			id = intent.getStringExtra(EXPENSE_ID_STRING);
		} else {
			id = savedInstanceState.getString(EXPENSE_ID_STRING);
		}

		return claim.getExpenseById(id);
	}
	
	/*
	 * Given an intent and bundle and claim it will determine 
	 * If the activity with this intent and bundle is creating a new whatever it corresponds to
	 */
	
	public static boolean extractIsNew(Bundle savedInstanceState, Intent intent) {
		if(savedInstanceState != null) {
			return savedInstanceState.getBoolean(IS_NEW);
		} else {
			return intent.getBooleanExtra(IS_NEW, false);
		}
	}

	

	/*
	 * returns an intent which opens an activity that can create an expense
	 */
	
	public static Intent getCreateExpenseIntent(Context context, Claim claim) {

		Expense expense = ClaimManager.getInstance().createNewExpense(claim);
		
		Intent intent = getEditExpenseIntent(context, claim, expense);
		intent.putExtra(IS_NEW, true);
		return intent;
	}


	/*
	 * returns an intent which opens an activity that can edit an expense
	 */
	
	public static Intent getEditExpenseIntent(Context context, Claim claim,
			Expense expense) {

		Intent intent = new Intent(context, EditExpenseActivity.class);

		intent.putExtra(EXPENSE_ID_STRING, expense.getId());
		intent.putExtra(CLAIM_ID_STRING, claim.getId());

		return intent;
	}

	/*
	 * returns an intent which opens an activity that can edit a claim
	 */
	
	
	public static Intent getEditClaimIntent(Context context, Claim claim) {
		Intent intent = new Intent(context, EditClaimActivity.class);

		intent.putExtra(CLAIM_ID_STRING, claim.getId());
		return intent;
	}

	/*
	 * returns an intent which opens an activity that can create a claim
	 */
	
	
	public static Intent getCreateClaimIntent(Context context) {
		Claim claim = ClaimManager.getInstance().createNewClaim();

		Intent intent = getEditClaimIntent(context, claim);
		intent.putExtra(IS_NEW, true);
		return intent;	
	}

	/*
	 * returns an intent which opens an activity that can view a claim
	 */
	
	
	public static Intent getViewClaimIntent(Context context,
			Claim claim) {
		
		Intent intent = new Intent(context, ViewClaimActivity.class);

		intent.putExtra(CLAIM_ID_STRING, claim.getId());
		return intent;
	}
	
}
