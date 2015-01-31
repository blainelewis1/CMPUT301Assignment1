package blainelewis1.cmput301assignment1;

import android.app.Activity;
import android.os.Bundle;


public class SerializingActivity extends Activity {
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		ClaimManager claimManager = ClaimManager.getInstance();
		
		claimManager.deserialize(this);
	}
	
	/*protected void onResume() {
		
		super.onResume();
		
		ClaimManager claimManager = ClaimManager.getInstance();
		claimManager.deserialize(this);
		
	}*/
	
	protected void onPause() {
		super.onPause();
		
		ClaimManager claimManager = ClaimManager.getInstance();
		claimManager.serialize(this);
	}

}
