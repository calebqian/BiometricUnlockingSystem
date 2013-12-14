package biometric.unlocking.system;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}
	
	public void redirectToTrainingAct(View v)
	{
		Intent intent = new Intent(this, TrainActivity.class);
		startActivity(intent);
		
	}
	
	public void redirectToUnlockingAct(View v)
	{
		Intent intent = new Intent(this, SurfaceActivity.class);
		startActivity(intent);
		
	}
}
