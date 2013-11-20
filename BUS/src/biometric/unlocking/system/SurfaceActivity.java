package biometric.unlocking.system;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SurfaceActivity extends Activity {

    private HandrawingView mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mView = new HandrawingView(this);
        super.onCreate(savedInstanceState);
        setContentView(mView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.surface, menu);
        return true;
    }
    
    protected void ClearSurfaceCanvas()
    {
    	mView.clearPaths();
    	mView.postInvalidate();	
    }
    
    protected void CaptureSurfaceCanvas()
    {
    	//make a bitmap snapshot here, save to file
    	
    	
    	
    }
    
    protected boolean SubmitForUnlocking()
    {
    	
    	return false;
    	
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//Log.i("Debug", Integer.toString(item.getItemId()));
    	if(item.getItemId()==R.id.action_clear_canvas)
    	{
    		ClearSurfaceCanvas();
    		
    	}
    	else if(item.getItemId()==R.id.action_capture)
    	{
    		CaptureSurfaceCanvas();
    		
    	}
    	else if(item.getItemId()==R.id.action_submit)
    	{
    		boolean unlocResult = SubmitForUnlocking();
    		
    		
    	}
    	else
    	{
    		return false;
    		
    	}
    	
		return true;
    
    }

}
