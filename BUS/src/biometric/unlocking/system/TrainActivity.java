package biometric.unlocking.system;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class TrainActivity extends SurfaceActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		mView = (HandrawingView)findViewById(R.id.handView);
	}

	public void TrainMe(View v)
	{
		String dir = Environment.getExternalStorageDirectory()+File.separator;
		EditText symbolText = (EditText) findViewById(R.id.symbolText);
		String symbolStr = symbolText.getText().toString();
		if(symbolStr==null || symbolStr.equals(""))
		{
			setMessage(R.id.statsView, "You must define a symbol", Color.RED);
			
		}
		File SymbolListFile = new File(dir+"bus-symbollist.csv");
		try
		{
			boolean found = false;
			if(SymbolListFile.exists())
			{
				FileReader fr = new FileReader(SymbolListFile);
				CSVReader csvr = new CSVReader(fr);
				List<String[]> readData = csvr.readAll();

				for(String[] data : readData)
				{
					if(data[0].equals(symbolStr))
					{
						/*already exist*/
						found = true;
						break;
					}
					
				}
				csvr.close();

			}
			if(!found){
				/*if not found, append it into symbol list*/
				FileWriter fw = new FileWriter(SymbolListFile, true);
				CSVWriter csvw = new CSVWriter(fw);
				String [] strArray = {symbolStr};
				List<String []> writeData = new ArrayList<String[]>();
				writeData.add(strArray);
				csvw.writeAll(writeData);
				csvw.close();
			}
			
			SendAsTrainingData(symbolStr);
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		mView.clearFields();

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.train, menu);
		return true;
	}

}
