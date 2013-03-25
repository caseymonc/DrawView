package com.itv.drawview;

import com.itv.drawview.Point.Type;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private DrawView drawView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawView = (DrawView)findViewById(R.id.surfaceView1);
	}

	public void onResume(){
		super.onResume();
		drawView.resume();
	}
	
	public void onPause(){
		super.onPause();
		drawView.pause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.x:
	        	drawView.setType(Type.X);
	            return true;
	        case R.id.line:
	        	drawView.setType(Type.Line);
	            return true;
	        case R.id.circle:
	        	drawView.setType(Type.Circle);
	            return true;
	        case R.id.rectangle:
	        	drawView.setType(Type.Rectangle);
	            return true;
	        case R.id.free_hand:
	        	drawView.setType(Type.FreeHand);
	            return true;
	        case R.id.arrow:
	        	drawView.setType(Type.Arrow);
	            return true;
	        case R.id.red:
	        	drawView.setColor(Color.RED);
	            return true;
	        case R.id.yellow:
	        	drawView.setColor(Color.YELLOW);
	            return true;
	        case R.id.green:
	        	drawView.setColor(Color.GREEN);
	            return true;
	        case R.id.blue:
	        	drawView.setColor(Color.BLUE);
	            return true;
	        case R.id.black:
	        	drawView.setColor(Color.BLACK);
	            return true;
	        case R.id.clear:
	        	drawView.clear();
	            return true;
	        case R.id.undo:
	        	drawView.undo();
	            return true;
	        case R.id.redo:
	        	drawView.redo();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
