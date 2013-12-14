package biometric.unlocking.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class HandrawingView extends SurfaceView implements SurfaceHolder.Callback {

	private Context mContext;
	private Path mPath = null;

	private Paint mPaint;
	private int N = 30;
	private int noiseNumber = 5;
	private ArrayList<Pair<Float, Float>> touchPoints;
	private boolean startFlag = true;
	private int numPath = 0;
	private long startTime = 0l;
	private long endTime = -1l;

	private void generalConstruction(Context context){
		mContext = context;
		getHolder().addCallback(this);
		setWillNotDraw(false);
		mPath = new Path();

		mPaint = new Paint();
		mPaint.setDither(true);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(3);
		touchPoints = new ArrayList<Pair<Float, Float>>();
	}

	public HandrawingView(Context context) {
		super(context);
		generalConstruction(context);

	}
	public HandrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		generalConstruction(context);
		// TODO Auto-generated constructor stub
	}

	public HandrawingView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		generalConstruction(context);
		// TODO Auto-generated constructor stub
	}
	public void clearFields() {
		startTime = 0l;
		startFlag = true;
		endTime = -1l;
		numPath = 0;
		touchPoints.clear();
		clearPaths();
	}



	private ArrayList<Pair<Float, Float>> getScaledPoints()
	{
		ArrayList<Pair<Float, Float>> scaledPoints = new ArrayList<Pair<Float, Float>>();
		int numPoints = touchPoints.size();
		if(numPoints < N)
		{
			Toast.makeText(mContext, "You write too fast!", Toast.LENGTH_SHORT).show();
			return null;
		}
		for(int i = 0; i<N; i++)
		{
			scaledPoints.add(touchPoints.get(numPoints*i/N));
		}

		return scaledPoints;

	}

	private Pair<Pair<Float, Float>,Pair<Float, Float>>  getMaxMin()
	{
		if(touchPoints.size()==0){
			return null;
		}
		float max_x = 0, max_y = 0;
		float min_x = 0, min_y = 0;

		for(Pair<Float, Float> point : touchPoints)
		{

			if(point.first.floatValue()>max_x)
				max_x = point.first.floatValue();
			if(point.first.floatValue()<min_x)
				min_x = point.first.floatValue();
			if(point.second.floatValue()>max_y)
				max_y = point.second.floatValue();
			if(point.second.floatValue()<min_y)
				min_y = point.second.floatValue();

		}

		Pair<Float, Float> max = new Pair<Float, Float>(max_x, max_y);
		Pair<Float, Float> min = new Pair<Float, Float>(min_x, min_y);
		Pair<Pair<Float, Float>, Pair<Float, Float>> aggr = new Pair<Pair<Float, Float>, Pair<Float, Float>>(max, min);
		return aggr;
	}
	private boolean checkAndInsertNumStrokesData(String filename, String label, int num)
	{
		String csv = Environment.getExternalStorageDirectory() + File.separator
				+ filename;
		File strokesFile = new File(csv);
		int check = GetNumStrokesData(filename, label);
		try {
			if(check==-1)
			{
				/*not found*/
				FileWriter fw;
				if(strokesFile.exists())
				{
					/*File does exist, entry is new, append*/
					fw = new FileWriter(strokesFile, true);

				}
				else{
					/*File does not exist*/
					fw = new FileWriter(strokesFile, false);

				}
				CSVWriter csvw = new CSVWriter(fw);
				List<String []> data = new ArrayList<String[]>();
				data.add(new String [] {label, Integer.toString(num)});
				csvw.writeAll(data);
				csvw.close();
				return true;

			}
			else{
				/*found*/
				if(check==num)
				{
					/*matching*/
					return true;
				}
				else{
					/*not matching*/
					return false;
				}
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;

	}


	private int GetNumStrokesData(String filename, String label)
	{
		String csv = Environment.getExternalStorageDirectory() + File.separator
				+ filename;
		File strokesFile = new File(csv);
		if(strokesFile.exists())
		{
			return -1; /*did not find*/

		}
		try {
			CSVReader reader = new CSVReader(new FileReader(strokesFile));
			List<String[]> data = reader.readAll();

			for(String[] p : data)
			{
				if(p[0]==label)
				{		
					reader.close();
					return Integer.valueOf(p[1]);
				}
			}

			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return -1;

	}
	public void addWhiteNoise(String filename, String noiseLabel)
	{
		String dir = Environment.getExternalStorageDirectory()+File.separator+filename;
		File file = new File(dir);


		try{
			FileOutputStream fStream = new FileOutputStream(file, true);
			file.createNewFile();
			for(int i = 0; i < noiseNumber; i++){
				fStream.write((noiseLabel+" ").getBytes());
				int j = 0;
				int n = 1;
				/*first iterate all the positions*/
				for(j = 0; j<N*2;j++)
				{
					float x = new Random().nextFloat();
					String BigN = Integer.toString(n);
					fStream.write((BigN+":"+Float.toString(x)+" ").getBytes());
					n++;
				}
				/*second, iterate all the tangents*/
				for(j=0;j<N*2;j++)
				{
					String BigN = Integer.toString(n);
					float x = new Random().nextFloat();
					float rand = (float) (x*Math.PI-Math.PI/2);
					fStream.write((BigN+":"+Float.toString(rand)+" ").getBytes());
					n++;
				}
				String BigN = Integer.toString(n);
				int time = new Random().nextInt(10000)+500;
				int strokes = new Random().nextInt(10);
				fStream.write((BigN+":"+Integer.toString(time)+" ").getBytes());
				n++;
				BigN = Integer.toString(n);
				fStream.write((BigN+":"+Integer.toString(strokes)+"\n").getBytes());
			}
			fStream.close();

		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}

	public void exportToTrainingData(String filename, String symbolName, EditText mLabelEdit, boolean append)
	{
		List<String []> data = getScaledThenNormalizedPoints();
		if(data==null)
			return;
		/*add noise here*/
		String noiseLabel = "-1";



		String labelText = mLabelEdit.getText().toString();
		//	exportWhiteNoise(filename, "-1");
		/*
			boolean check = checkAndInsertNumStrokesData("bus-numstrokes.csv", labelText, numPath);
			if(check==false)
			{
				Toast.makeText(mContext, "Wrong number of strokes, not accetable training tuple.", Toast.LENGTH_SHORT);
				return;
			}
		 */
		String dir = Environment.getExternalStorageDirectory()+File.separator+filename+"."+symbolName;
		File file = new File(dir);

		try {
			file.createNewFile();
			/*appending to old training set*/
			FileOutputStream fStream = new FileOutputStream(file, append);
			fStream.write((noiseLabel+" ").getBytes());
			int i = 1;
			int n = 1;
			for(String [] pair : data)
			{
				String I = Integer.toString(i);
				fStream.write((I+":"+new Random().nextFloat()+" ").getBytes());
				i++;
				I = Integer.toString(i);
				fStream.write((I+":"+new Random().nextFloat()+" ").getBytes());
				i++;
				I = Integer.toString(i);

				fStream.write((I+":"+(new Random().nextFloat()*2-1)+" ").getBytes());
				i++;
				I = Integer.toString(i);
				fStream.write((I+":"+(new Random().nextFloat()*2-1)+" ").getBytes());
				i++;
				n++;
			}

			String I = Integer.toString(i);
			fStream.write((I+":"+Long.toString(new Random().nextInt(1000)+500)+" ").getBytes());
			i++;
			I = Integer.toString(i);
			fStream.write((I+":"+Integer.toString(new Random().nextInt(9)+1)).getBytes());
			fStream.write("\n".getBytes());
			/*write actual data*/
			fStream.write((labelText+" ").getBytes());
			i = 1;
			n = 1;
			for(String [] pair : data)
			{
				I = Integer.toString(i);
				fStream.write((I+":"+pair[0]+" ").getBytes());
				i++;
				I = Integer.toString(i);
				fStream.write((I+":"+pair[1]+" ").getBytes());
				i++;
				I = Integer.toString(i);
				PathMeasure pm = new PathMeasure(mPath, false);
				float len = pm.getLength();
				float [] tangent = new float[2];
				float dis = (n)*len/N;
				pm.getPosTan(dis, null, tangent);
				I = Integer.toString(i);
				fStream.write((I+":"+tangent[0]+" ").getBytes());
				i++;
				I = Integer.toString(i);
				fStream.write((I+":"+tangent[1]+" ").getBytes());
				i++;

				n++;
			}
			long timeElapsed = endTime - startTime;
			I = Integer.toString(i);
			fStream.write((I+":"+Long.toString(timeElapsed)+" ").getBytes());
			i++;
			I = Integer.toString(i);
			fStream.write((I+":"+Integer.toString(numPath)).getBytes());
			fStream.write("\n".getBytes());
			fStream.close();
			Toast.makeText(mContext, "Training tuple added.", Toast.LENGTH_SHORT).show();
			String [] argv = {"-t", "2", dir, dir+".model"};
			svm_train.main(argv);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void clearAllExistingTrainingData(String filename, List<String []> SymbolList)
	{
		String dir = Environment.getExternalStorageDirectory()+File.separator+filename;
		if(SymbolList==null || SymbolList.size()==0)
		{
			Toast.makeText(mContext, "No training data found.", Toast.LENGTH_SHORT).show();
			return;
		}
		int numOfSyms = SymbolList.size();
		int success = 0;
		for(String[] sym : SymbolList){
			File file = new File(dir+sym[0]);
			File model = new File(dir+sym[0]+".model");
			//File numStrokes = new File(Environment.getExternalStorageDirectory()+File.separator+"bus-numstrokes.csv");
			// boolean result_strokes = numStrokes.delete();
			boolean result = file.delete();
			boolean result_model = model.delete();
			if(result&&result_model)
			{
				success++;

			}

		}
		if(numOfSyms == success)
		{
			Toast.makeText(mContext, "Training data deleted.", Toast.LENGTH_SHORT).show();

		}
		else{
			Toast.makeText(mContext, "Training data deletion failed. Please manually delete.", Toast.LENGTH_SHORT).show();

		}
	}



	private List<String[]> getScaledThenNormalizedPoints()
	{
		List<String[]> data = new ArrayList<String []> ();
		ArrayList<Pair<Float, Float>> exportPoints = getScaledPoints();
		if(exportPoints == null)
		{

			return null;
		}
		Pair<Pair<Float, Float>, Pair<Float, Float>> maxMin = getMaxMin();
		float max_x = maxMin.first.first;
		float max_y = maxMin.first.second;
		float min_x = maxMin.second.first;
		float min_y = maxMin.second.second;
		float maxminusmin_x = max_x - min_x;
		float maxminusmin_y = max_y - min_y;
		for(Pair<Float, Float> point : exportPoints)
		{
			Float normX = (point.first-min_x)/maxminusmin_x;
			Float normY = (point.second-min_x)/maxminusmin_y;
			Pair<Float, Float> normPair = new Pair<Float, Float>(normX, normY);
			data.add(new String[]{normPair.first.toString(), normPair.second.toString()});
		}
		return data;
	}
	public void ExportScaledThenNormalizedCSV(String filename)
	{
		ArrayList<Pair<Float, Float>> exportPoints = getScaledPoints();
		Pair<Pair<Float, Float>, Pair<Float, Float>> maxMin = getMaxMin();
		float max_x = maxMin.first.first;
		float max_y = maxMin.first.second;
		float min_x = maxMin.second.first;
		float min_y = maxMin.second.second;
		float maxminusmin_x = max_x - min_x;
		float maxminusmin_y = max_y - min_y;

		String csv = Environment.getExternalStorageDirectory() + File.separator
				+ filename;
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(csv));
			List<String[]> data = new ArrayList<String[]>();
			for(Pair<Float, Float> point : exportPoints)
			{
				Float normX = (point.first-min_x)/maxminusmin_x;
				Float normY = (point.second-min_x)/maxminusmin_y;
				Pair<Float, Float> normPair = new Pair<Float, Float>(normX, normY);
				data.add(new String[]{normPair.first.toString(), normPair.second.toString()});
			}

			writer.writeAll(data);
			writer.close();
			Toast.makeText(this.getContext(), "CSV file exported: " + filename,
					Toast.LENGTH_LONG).show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void exportFeaturesToFile(String filename) {
		long timeElapsed = endTime - startTime;

		String csv = Environment.getExternalStorageDirectory() + File.separator
				+ filename;
		Log.i("debug-bus", csv);
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(csv));
			List<String[]> data = new ArrayList<String[]>();

			data.add(new String[] { Long.toString(timeElapsed),
					Integer.toString(numPath) });

			writer.writeAll(data);
			writer.close();
			Toast.makeText(this.getContext(), "CSV file exported: " + filename,
					Toast.LENGTH_LONG).show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void exportTouchPointsToFile(String filename) {

		if (touchPoints != null && touchPoints.size() != 0) {
			String csv = Environment.getExternalStorageDirectory()
					+ File.separator + filename;
			Log.i("debug-bus", csv);
			try {
				CSVWriter writer = new CSVWriter(new FileWriter(csv));
				List<String[]> data = new ArrayList<String[]>();
				for (Pair<Float, Float> point : touchPoints) {
					data.add(new String[] { point.first.toString(),
							point.second.toString() });
				}
				writer.writeAll(data);
				writer.close();
				Toast.makeText(this.getContext(),
						"CSV file exported: " + filename, Toast.LENGTH_LONG)
						.show();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void clearPaths() {
		if (mPath != null) {
			mPath.reset();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Log.i("Debug", "Entering onDraw function!");
		canvas.drawPath(mPath, mPaint);
	}

	public boolean submitTestingForUnlocking(EditText mLabelEdit, String model_name,  String test_filename, String output_filename, boolean append, float ownerId)
	{
		String labelText = mLabelEdit.getText().toString();
		String dir = Environment.getExternalStorageDirectory()+File.separator;
		String model_dir = dir+model_name;
		Log.i("debug-bus", "model_name: "+model_dir);
		String test_dir = dir+test_filename;
		String output_dir = dir+output_filename;
		File modelFile = new File(model_dir);
		File testFile = new File(test_dir);
		File outFile = new File(output_dir);

		/*check if the model file exists*/
		if(!modelFile.exists())
		{
			Toast.makeText(mContext, "You don't have a training set!", Toast.LENGTH_SHORT).show();
			return false;
		}


		List<String []> data = getScaledThenNormalizedPoints();
		if(data == null)
		{
			return false;
		}
		try {
			testFile.createNewFile();
			/*appending to old training set*/
			FileOutputStream fStream = new FileOutputStream(testFile, append);
			fStream.write((labelText+" ").getBytes());
			int i = 1;
			int n = 1;
			for(String [] pair : data)
			{
				String I = Integer.toString(i);
				fStream.write((I+":"+pair[0]+" ").getBytes());
				i++;
				I = Integer.toString(i);
				fStream.write((I+":"+pair[1]+" ").getBytes());
				i++;
				I = Integer.toString(i);
				PathMeasure pm = new PathMeasure(mPath, false);
				float len = pm.getLength();
				float [] tangent = new float[2];
				float dis = (n)*len/N;
				pm.getPosTan(dis, null, tangent);
				I = Integer.toString(i);
				fStream.write((I+":"+tangent[0]+" ").getBytes());
				i++;
				I = Integer.toString(i);
				fStream.write((I+":"+tangent[1]+" ").getBytes());
				i++;

				n++;
			}
			long timeElapsed = endTime - startTime;
			String I = Integer.toString(i);
			fStream.write((I+":"+Long.toString(timeElapsed)+" ").getBytes());
			i++;
			I = Integer.toString(i);
			fStream.write((I+":"+Integer.toString(numPath)).getBytes());
			fStream.write("\n".getBytes());
			fStream.close();
			//Toast.makeText(mContext, "Once-testing file created.", Toast.LENGTH_SHORT).show();
			String argv[] = {test_dir, model_dir, output_dir};
			svm_predict.main(argv);

			if(!outFile.exists())
				Log.i("debug-bus", "outpu file does not exist!");

			FileReader namereader = new FileReader(outFile);
			BufferedReader in = new BufferedReader(namereader);
			String outLabel = in.readLine();
			in.close();
			float outLabelId = Float.valueOf(outLabel);
			//Toast.makeText(mContext, "I predict you are person "+outLabel, Toast.LENGTH_SHORT).show();
			if(outLabelId == ownerId){
				return true;
			}

			else return false;

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//	super.onTouchEvent(event);
		float eventX = event.getX();
		float eventY = event.getY();

		touchPoints.add(new Pair<Float, Float>(eventX, eventY));
		// Log.i("Debug", "entering touching event!"+mPath.isEmpty());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (startFlag == true) {
				Date tmp = new Date();
				startTime = tmp.getTime();
				startFlag = false;
			}
			if (mPath == null)
				mPath = new Path();
			mPath.moveTo(eventX, eventY);
			mPath.lineTo(eventX, eventY);
			return true;
		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(eventX, eventY);
			break;
		case MotionEvent.ACTION_UP:
			Date tmp = new Date();
			endTime = tmp.getTime();
			numPath++;
			// reset the single path
			// mPath.reset();
			break;
		default:
			return false;
		}

		// Schedules a repaint.
		invalidate();
		return true;
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

}
