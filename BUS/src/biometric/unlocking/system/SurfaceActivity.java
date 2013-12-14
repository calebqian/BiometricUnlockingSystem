package biometric.unlocking.system;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Color;
import libsvm.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

public class SurfaceActivity extends Activity {
	
	protected float ownerLabelId = 1.0f;
	protected List <String []> symbolList;
	protected String currSym = "";
    protected HandrawingView mView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        mView = (HandrawingView)findViewById(R.id.handView);
        try{
        	String dir = Environment.getExternalStorageDirectory()+File.separator;
        	File symbolCSV = new File(dir+"bus-symbollist.csv");
        	if(!symbolCSV.exists())
        	{
        		setMessage(R.id.textView1, "No training data found!", Color.RED);
        		return;
        	}
        	CSVReader csvr = new CSVReader(new FileReader(symbolCSV));
        	symbolList = csvr.readAll();
        	if(symbolList==null)
        	{
        		setMessage(R.id.textView1, "No training data found!", Color.RED);
        		csvr.close();
        		return;
        	}
        	int numOfData = symbolList.size();
        	if(numOfData==0)
        	{
        		setMessage(R.id.textView1, "No training data found!", Color.RED);
        		csvr.close();
        		return;
        	}
        	int symbolIndex = new Random().nextInt(numOfData);
        	currSym = symbolList.get(symbolIndex)[0];
        	TextView instructView = (TextView) findViewById(R.id.SymbolView);
        	instructView.setText("Write symbol: "+currSym+" to unlock");
        	csvr.close();
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        	
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.surface, menu);
        return true;
    }
    
    public void setMessage(int viewId, String msg, int color)
    {
    	TextView mText = (TextView) findViewById(viewId);
    	mText.setText(msg);
    	mText.setTextColor(color);
    }
    
    public void UnlockMe(View v)
    {
    	EditText mLabelEdit = (EditText) findViewById(R.id.editText1);
    	boolean result = mView.submitTestingForUnlocking(mLabelEdit, "bus-training.svm"+"."+currSym+".model", "bus-once.svm", "bus-once.out", false, ownerLabelId);
    	Log.i("debug-bus", "Unlocking symbol: "+currSym);
    	String msg;
    	int c = Color.RED;
    	if(result)
    	{
    		msg = "Yes";
    		
    		c = Color.GREEN;
    		
    	}
    	else{
    		msg = "No";
    		
    	}
    	//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    	setMessage(R.id.textView1, msg, c);
    	mView.clearFields();
    }
    
    protected void ClearSurfaceCanvas()
    {
    	
    	mView.clearPaths();
    	mView.clearFields();
    	mView.postInvalidate();	
    }
    
    protected void CaptureSurfaceCanvas()
    {
    	//make a bitmap snapshot here, save to file
    	Log.i("debug-bus", "capturing...");
    	mView.exportTouchPointsToFile("touch-points.csv");
    	mView.exportFeaturesToFile("bus-features.csv");
    	mView.ExportScaledThenNormalizedCSV("bus-normalized.csv");
    	mView.clearFields();
    	
    	
    }
    
    protected void SendAsTrainingData(String symbolName)
    {
    	EditText mLabelEdit = (EditText) findViewById(R.id.editText1);

    	mView.exportToTrainingData("bus-training.svm", symbolName, mLabelEdit, true);
    	mView.clearFields();
    	
    }
    
    protected void ClearTrainingData()
    {
    	mView.clearAllExistingTrainingData("bus-training.svm", symbolList);
    	mView.clearFields();
    	
    	
    }
    
    
    protected void SubmitForUnlocking()
    {
    	EditText mLabelEdit = (EditText) findViewById(R.id.editText1);
    	boolean result = mView.submitTestingForUnlocking(mLabelEdit, "bus-training.svm.model", "bus-once.svm", "bus-once.out", false, ownerLabelId);
    	String msg;
    	if(result)
    	{
    		msg = "Yes! Unlocked!";
    	}
    	else{
    		msg = "No! Not the right one!";
    	}
    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    	mView.clearFields();
    	
    }
    
    protected void AddWhiteNoise()
    {
    	mView.addWhiteNoise("bus-training.svm", "-1");
    	
    }
    
    protected void SendAsTestingData()
    {
    	
    	
    }
    
    protected float getAccuracy()
    {
    	float accu = 0.0f;
    	
    	return accu;
    	
    }
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//Log.i("Debug", Integer.toString(item.getItemId()));
    	if(item.getItemId()==R.id.action_clear_canvas)
    	{
    		ClearSurfaceCanvas();
    		
    	}
    	
    	else if (item.getItemId()==R.id.action_clear_training_data){
    		ClearTrainingData();
    		
    	}
    	else
    	{
    		return false;
    		
    	}
    	
		return true;
    
    }

}
