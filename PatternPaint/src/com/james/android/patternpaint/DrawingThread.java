package com.james.android.patternpaint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import pattern.DLA;
import pattern.Particle;
import pattern.Pattern;
import pattern.SeekerTrail;
import pattern.Tree;
import pixel.Pixel;
import seekerTarget.Seeker;
import seekerTarget.Target;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

@SuppressLint("NewApi") public class DrawingThread extends Thread {
	// Drawing settings
	private Paint mBackgroundPaint;
	private Paint mTargetPaint;
	private Target mTarget;
	private Seeker mSeeker;
	private DLA mDLA;
	
	// Thread management 
	private DrawingVIew mMainView;
	private Context mContext;
	public Handler mHandler;
	private Canvas mCanvas;
	private SurfaceHolder mHolder;
    private boolean mRun;

    // input queue and patterns to be drawn
    private boolean mUsingFlattenedCanvas;
    private Bitmap mFlattenedCanvasBitmap;
	private Queue<PointF> mInputQueue;
	private ArrayList<Pattern> mPatterns;
	private SeekerTrail mSeekerTrailBuffer;
	private SharedPreferences mPreferenceSettings;
	
	// FTS management
	private final static int MAX_FPS = 30;
	private final static int MAX_FRAMES_SKIPPED = 5;
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	
	
	public DrawingThread(SurfaceHolder holder, Context context, DrawingVIew mainView) {
		Log.i("LIFECYCLE", "creating drawing thread");
        mMainView = mainView;
        mHolder = holder;
        mContext = context;
        mRun = false;
		mPreferenceSettings = mContext.getSharedPreferences(PatternPaintMainActivity.PREFS_NAME, 0);
        mUsingFlattenedCanvas = false;
        mFlattenedCanvasBitmap = Bitmap.createBitmap(mMainView.getWidth(), mMainView.getHeight(), Bitmap.Config.ARGB_8888);
        mInputQueue = new LinkedList<PointF>();
        mPatterns = new ArrayList<Pattern>();
        mSeekerTrailBuffer = new SeekerTrail(mContext);
        
        mTarget = new Target(mMainView.getWidth()/2 , mMainView.getHeight()/2);
        mSeeker = new Seeker(context, mMainView.getWidth()/2 , mMainView.getHeight()/2, mSeekerTrailBuffer);
		mTargetPaint = new Paint();
		mTargetPaint.setColor(Color.WHITE);
		mTargetPaint.setAlpha(120);
		mBackgroundPaint = new Paint();
  	}
	
	public void setBackground(Bitmap bitmap) {
		mFlattenedCanvasBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		mUsingFlattenedCanvas = true;
	}

	public void addInput(PointF click) {
		mInputQueue.add(click);
	}
	
	public void setTargetLocation(PointF point) {
		mTarget.setVisibility(true);
		mTarget.setLocation(point);
	}

	public boolean isRunning() {
		return mRun;
	}
	
	public boolean hasPatterns() {
		return !mPatterns.isEmpty();
	}
	
	public boolean usingFlattenedCanvas() {
		return mUsingFlattenedCanvas;
	}
	
	public ArrayList<Pattern> getPatterns() {
		return mPatterns;
	}
	
	public void setRunning(boolean running) {
		mRun = running;
	}
	
	public void setPatterns(ArrayList<Pattern> patterns) {
		 this.mPatterns.addAll(patterns);
	}
		
	@Override
	public void run() {
		// FPS management
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;
	
		Log.i("THREAD", "Thread is starting");
		while(mRun) {
			try {
				mCanvas = mHolder.lockCanvas();
				synchronized (mHolder) {
					beginTime = System.currentTimeMillis();
					framesSkipped = 0;
					
					// Core of the Update/Render Cycle
					update();
					render(mCanvas);

					timeDiff = System.currentTimeMillis() - beginTime;
					sleepTime = (int) (FRAME_PERIOD - timeDiff);
					
					if (sleepTime > 0) {
						// We're okay
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					while ( (sleepTime < 0) && (framesSkipped < MAX_FRAMES_SKIPPED) ) {
						// We need to catch up
						update();
						sleepTime += FRAME_PERIOD;
						framesSkipped++;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (mCanvas != null) {
					mHolder.unlockCanvasAndPost(mCanvas);
				}
			}
		}
	}


	
	private void update() {
		checkForBackgroundColor();
		setUpMainBitmap();
		checkForNewPattern();
		growPatterns();
		updateSeeker();
		checkForUndo();
		checkForPause();
		checkForScreenClear();
		checkForFlattenCanvas();
	}


	private void render(Canvas canvas) {
		
		Bitmap tempBitmap = Bitmap.createBitmap(mMainView.getWidth(), mMainView.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(tempBitmap);
		
		if (canvas != null)  {
			drawBackground(c);
			if (!mPatterns.isEmpty()) {
				for (Pattern pattern: mPatterns) {
					drawPixels(pattern.getAllPixelsUpToDrawIndex(), c);

				}
			}
			if (mSeeker.isRunning()) {
				drawPixels(mSeekerTrailBuffer.getTrail(), c);
			}
			
			if (mTarget.isVisisble() && mSeeker.isRunning()) {
				drawTarget(c);
				drawSeeker(c);	
			}
			if (mDLA != null) {
				if (mDLA.isRunning()) {
					drawPixels(mDLA.getAllPixelsUpToDrawIndex(), c);
				}
			}
			
			mMainView.setBitmap(tempBitmap);
			canvas.drawBitmap(tempBitmap, 0,  0, null);	
		}
	}

	private void drawBackground(final Canvas canvas) {
		if (mUsingFlattenedCanvas) {
			canvas.drawBitmap(mFlattenedCanvasBitmap, 0, 0, null);
		} else {
			canvas.drawPaint(mBackgroundPaint);
		}
	}
	
	private void drawSeeker(Canvas canvas) {
		canvas.drawCircle(mSeeker.getX(), mSeeker.getY(), 25f, mSeeker.getSeekerColor());
	}
	
	private void drawTarget(Canvas canvas) {
		canvas.drawCircle(mTarget.getX(), mTarget.getY(), mTarget.getWidth()/2 , mTargetPaint);
	}
	
	private  void drawPixels(ArrayList<Pixel> pixels, Canvas canvas) {
		synchronized (pixels) {
			for (Pixel pixel: pixels) {
				pixel.draw(canvas);
	    	}
		}
	}

	private void checkForBackgroundColor() {
		int backgroundColorPreference = mPreferenceSettings.getInt(PatternPaintMainActivity.KEY_BACKGROUND_COLOR, Color.DKGRAY);
		if (backgroundColorPreference != Color.BLACK) {
			int red = Color.red(backgroundColorPreference);
			int green = Color.green(backgroundColorPreference);
			int blue = Color.blue(backgroundColorPreference);
			int tempColor = Color.rgb(red, green, blue);
			mBackgroundPaint.setColor(tempColor);
		}
	}

	private void updateSeeker() {
		mSeeker.updateSeeker(mTarget.getTargetVector());
			
	}

	private void checkForUndo() {
		if (getBoolSharedPreference(PatternPaintMainActivity.KEY_UNDO_PATTERN, false)) {
			if ( (mPatterns != null) && (!mPatterns.isEmpty())) {
				Log.i("UNDO BUTTON", "pattern array is: " + String.valueOf(mPatterns.size()));
				mPatterns.remove(mPatterns.size() - 1);
			}
		}
		turnOffPreference(PatternPaintMainActivity.KEY_UNDO_PATTERN);
	}

	private void setUpMainBitmap() {
		if (mMainView.getBitmap() == null) {
			mMainView.setBitmap(Bitmap.createBitmap(mMainView.getWidth(), mMainView.getHeight(), Bitmap.Config.ARGB_8888));
		}
	}

	private void checkForNewPattern() {
		if (!mInputQueue.isEmpty()) {
			PointF startPoint = mInputQueue.remove();
			
			int patternType = mPreferenceSettings.getInt(PatternPaintMainActivity.KEY_PATTERN, PatternPaintMainActivity.TREE_PATTERN);
			
			switch (patternType) {
				case PatternPaintMainActivity.TREE_PATTERN: {
					pauseSeeker();
					Pattern spawnedPattern = new Tree(startPoint, mContext);
					mPatterns.add(spawnedPattern);
					break;
				}
				case PatternPaintMainActivity.PARTICLE_PATTERN: {
					pauseSeeker();
					Pattern spawnedPattern = new Particle(startPoint, mContext);
					mPatterns.add(spawnedPattern);
					break;
				}
				case PatternPaintMainActivity.TARGET_PATTERN: {
					mTarget.setVisibility(true);
					mSeeker.setRunning(true);
					break;
				}
/*				case DragAndDrawActivity.RANDOM_GROWTH_PATTERN: {
					pauseSeeker();
					Pattern spawnedPattern = new RandomGrowth(startPoint, mContext);
					mPatterns.add(spawnedPattern);
					break;
				}*/
				case PatternPaintMainActivity.DLA_PATTERN: {
					pauseSeeker();
					
					mDLA = new DLA(startPoint, mContext);
					Thread dlaThread = new Thread(mDLA);
					dlaThread.start();
				}
			}
 		}
	}
	
	private void growPatterns() {
		if (!mPatterns.isEmpty()) {
			for (Pattern pattern : mPatterns) {
				grow(pattern);
			}
		}
	}
	
	private void grow(Pattern pattern) {
		if (pattern.getDrawIndex() < pattern.getMaxDrawIndex()) {
			pattern.incrementDrawIndex();
		}
	}

	private void checkForFlattenCanvas() {
		if (mPreferenceSettings.getBoolean(PatternPaintMainActivity.KEY_FLATTEN_CANVAS, false)) {
			if (!mPatterns.isEmpty()) {
				if (mSeeker.isRunning()) {
					pauseSeeker();
				}
				mFlattenedCanvasBitmap = mMainView.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
				
				mUsingFlattenedCanvas = true;
				clearAndResetScreen();
			}
			turnOffPreference(PatternPaintMainActivity.KEY_FLATTEN_CANVAS);
			Log.i("FLATTEN SCREEN", "Turning off flatten flag");
		}
	}
	
	private void checkForScreenClear() {
		if (getBoolSharedPreference(PatternPaintMainActivity.KEY_CLEAR_SCREEN, false)) {
			clearAndResetScreen();
			mSeeker.reset(mMainView.getWidth()/2, mMainView.getHeight()/2);
			mUsingFlattenedCanvas = false;
			turnOffPreference(PatternPaintMainActivity.READY_TO_CACHE_DRAWING);
			turnOffPreference(PatternPaintMainActivity.KEY_CLEAR_SCREEN);
		}
	}

	private void clearAndResetScreen() {
		mPatterns.clear();
		mTarget.setVisibility(false);
	}
	
	private void checkForPause() {
		if (pauseSetting() == true) {
			pauseSeeker();
			stopDLA();
			Log.i("SEEKER TRAIL", "Adding trail buffer to mPatterns");
			if (!mPatterns.isEmpty()) {
				for(Pattern pattern : mPatterns) {
					//halt drawing
					pattern.setMaxDrawIndex(pattern.getDrawIndex());
				}
			}
			turnOffPreference(PatternPaintMainActivity.KEY_PAUSE_SCREEN);
		}
	}

	private void pauseSeeker() {
		if (mSeeker.isRunning()) {
				mTarget.setVisibility(false);
				mPatterns.add(mSeekerTrailBuffer.copy());
				mSeeker.pause();
		}
	}
	
	private void stopDLA() {
		if (mDLA != null) {
			if (mDLA.isRunning()) {
				mDLA.stop();
				mPatterns.add(mDLA.copyDLAToPattern());
			}
		}
	}
	
	private boolean pauseSetting() {
		return getBoolSharedPreference(PatternPaintMainActivity.KEY_PAUSE_SCREEN, PatternPaintMainActivity.DEFAULT_PAUSE_SCREEN);
	}

	private boolean getBoolSharedPreference(String key, boolean defaultValue) {
		boolean preferenceValue = mPreferenceSettings.getBoolean(key, defaultValue);
		return preferenceValue;
	}
	private void turnOffPreference(String key) {
		SharedPreferences.Editor editor = mPreferenceSettings.edit();
		editor.putBoolean(key , false);
		editor.commit();
	}
}
