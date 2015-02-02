package blainelewis1.cmput301assignment1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;	
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * This is a singleton that manages the list of claims by 
 * providing means of (de) serialization and interfaces to get all of the claims
 */

public class ClaimManager {
	
	private static ClaimManager instance = null;
	
	//This is the name of the file we serialize to, it's arbitrary...
	private static final String saveFileName = "claims.sav";
	
	private ArrayList<Claim> claims;

	protected ClaimManager() {

	}

	/*
	 * Returns an instance of the claimManager
	 */
	
	public static ClaimManager getInstance() {
		if (instance == null) {
			instance = new ClaimManager();
		}
		return instance;
	}

	/*
	 * Adds a claim to the list
	 */
	
	public void addClaim(Claim claim) {
		claims.add(claim);
	}

	public Claim createNewClaim() {
		Claim claim = new Claim();

		addClaim(claim);

		return claim;
	}
	
	/*
	 * Finds a claim by its id
	 */

	public Claim getClaimById(String id) {
		for (Claim claim : claims) {
			if (claim.getId().equals(id)) {
				return claim;
			}
		}

		return null;
	}
	
	/*
	 * Creates an expense from it's claim. 
	 * We manage expense creation to ensure it is done properly
	 */

	public Expense createNewExpense(Claim claim) {
		Expense expense = new Expense(claim);

		claim.addExpense(expense);

		return expense;
	}

	/*
	 * Deletes a claim
	 */
	
	public void deleteClaim(Claim claim) {
		claims.remove(claim);
	}

	/*
	 * Serializes the claims as JSON using GSON 
	 */
	
	public void serialize(Context context) {

		
		//Why write nothing?
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
	
	/*
	 * Deserializes the claims that are stored as JSON using GSON 
	 */

	public void deserialize(Context context) {

		
		//Deserializing while we already have claims is dangerous and will overwrite data
		if (this.claims != null) {
			return;
		}

		Gson gson = new Gson();

		try {

			InputStreamReader reader = new InputStreamReader(
					context.openFileInput(saveFileName));

			//https://sites.google.com/site/gson/gson-user-guide 02-02-2015

			Type arrayListType = new TypeToken<ArrayList<Claim>>() {
			}.getType();

			claims = gson.fromJson(reader, arrayListType);

			reader.close();

		} catch (IOException e) {
			// fail silently there's nothing else we can do
			Log.e("Error", "Deserialize failed", e);
		}

		//Either we failed to load the claims or there were none
		
		if (claims == null) {
			claims = new ArrayList<Claim>();
		}

	}
	
	/*
	 * Returns all the claims
	 */
	public ArrayList<Claim> getClaims() {
		return claims;
	}
	
		
}
