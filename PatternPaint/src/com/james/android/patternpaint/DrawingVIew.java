package com.james.android.patternpaint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class DrawingVIew extends SurfaceView implements SurfaceHolder.Callback  {
	private class AddSpawnPointRunnable implements Runnable {
		
		private PointF current;
		
		public AddSpawnPointRunnable(PointF current) {
			this.current = current;
		}
		
		@Override
		public void run() {
			while (mIsTouchHeld) {
				mDrawingThread.addInput(current);
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
		}
	}
	
	public static final String TAG = "BoxDrawingView";
	
	private SurfaceHolder holder;
	private Bitmap mCanvasBitmap;
	private Context mContext;
	private DrawingThread mDrawingThread;
	
    private boolean mIsTouchHeld;

    // Used when creating the view in code
    public DrawingVIew(Context context) {
        this(context, null);
    }
    
    // Used when inflating the view from XML
    public DrawingVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        mContext = context;
        holder = getHolder();
        holder.addCallback(this);
        
    }

	@Override
    public boolean onTouchEvent(MotionEvent event) {
    	PointF current = new PointF(event.getX(), event.getY());
    	Log.i(TAG, "Received event at x = " + current.x + ", y = " + current.y + " : " );
    	
    	switch(event.getAction()) {
    		case MotionEvent.ACTION_DOWN:
    			Log.i(TAG, " ACTION DOWN");
    			mIsTouchHeld = true;
    			addPatternSpawnPoints(current);
    			mDrawingThread.setTargetLocation(current);
    			break;    			
    		case MotionEvent.ACTION_UP:
    			Log.i(TAG, " ACTION UP");
    			mIsTouchHeld = false;
    			break;
    		case MotionEvent.ACTION_MOVE:
    			mDrawingThread.setTargetLocation(current);
    	}
    	return true;
    }

	private void addPatternSpawnPoints(PointF current) {
		AddSpawnPointRunnable heldTouch = new AddSpawnPointRunnable(current);
		Thread touchThread = new Thread(heldTouch);
		touchThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("LIFECYCLE", "Surface Changed Called");

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("LIFECYCLE", "Creating mDraingView");
		mCanvasBitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        mDrawingThread = new DrawingThread(holder, mContext, this);
        checkForCachedBitmap();
        mDrawingThread.setRunning(true);
        mDrawingThread.start();
        Log.i("LIFECYCLE", "is hardward accellerated? " + this.isHardwareAccelerated());
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("SURFACE DESTROYED", "Attempting to join threads");
		Log.i("LIFECYCLE", "Destroying surface...");

		cacheDrawingView();
		
		boolean retry = true;
		mDrawingThread.setRunning(false);
		while (retry) {
			try {
				Log.i("SURFACE DESTROYED", "Joining...");
				mDrawingThread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}

	private void cacheDrawingView() {
		if (mDrawingThread != null && getBoolSharedPreference(PatternPaintMainActivity.READY_TO_CACHE_DRAWING, PatternPaintMainActivity.DEFAULT_READY_TO_CACHE_DRAWING)) {
			if (mDrawingThread.hasPatterns() || mDrawingThread.usingFlattenedCanvas()) {
				
				FileOutputStream outStream;
				try {
					outStream = mContext.openFileOutput(PatternPaintMainActivity.CACHED_CANVAS_FILE, Context.MODE_PRIVATE);
					getBitmap().compress(Bitmap.CompressFormat.PNG, 100, outStream);
					outStream.close();
					setBoolSharedPreference(PatternPaintMainActivity.READY_TO_LOAD_CACHED_DRAWING, true);
					Log.i("LIFECYCLE", "Created cached bitmap");
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void checkForCachedBitmap() {
		if (getBoolSharedPreference(PatternPaintMainActivity.READY_TO_LOAD_CACHED_DRAWING, PatternPaintMainActivity.DEFAULT_READY_TO_LOAD_CACHED_DRAWING)) {
			FileInputStream inputStream;
			try {
				inputStream = mContext.openFileInput(PatternPaintMainActivity.CACHED_CANVAS_FILE);
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				mDrawingThread.setBackground(bitmap);
				Toast.makeText(mContext, "Canvas was flattened", Toast.LENGTH_LONG).show();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			setBoolSharedPreference(PatternPaintMainActivity.READY_TO_CACHE_DRAWING, false);
			setBoolSharedPreference(PatternPaintMainActivity.READY_TO_LOAD_CACHED_DRAWING, false);
		}
	}
	
	public Bitmap getBitmap() {
		return this.mCanvasBitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.mCanvasBitmap = bitmap;
	}
	
	public DrawingThread getThread() {
		return mDrawingThread;
	}

	private boolean getBoolSharedPreference(String key, boolean defaultValue) {
		boolean preferenceValue = mContext.getSharedPreferences(PatternPaintMainActivity.PREFS_NAME, 0).getBoolean(key, defaultValue);
		return preferenceValue;
	}
	
	private void setBoolSharedPreference(String key, boolean value) {
		SharedPreferences settings = mContext.getSharedPreferences(PatternPaintMainActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
}
