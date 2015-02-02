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

public class ClaimManager {

	
	private static ClaimManager instance = null;
	private static final String saveFileName = "claims.sav";
	
	
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
	
	
	public ArrayList<Claim> getClaims() {
		return claims;
	}
	
		
}
