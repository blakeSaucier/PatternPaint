package pattern;

import java.util.ArrayList;

import pixel.Pixel;
import pixel.PixelFactory;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

public class DLA extends Pattern implements Runnable{
	
	PointF mStartPoint;
	private DLAGrid mDLAGrid;
	private boolean mIsRunning;
	
	public static int PIXEL_WIDTH = 20;
	private static int MAX_AGGREGATION = 400;
	
	private int mAggregationCount = 0;

	public DLA(Context context) {
		super(context);
	}
	
	public DLA(PointF startPoint, Context context) {
		super(context);
		this.mStartPoint = startPoint;
		this.mIsRunning = false;
		createGridPixels();
		setMaxDrawIndex(getAllPixelsSize());
	}
	
	public void stop() {
		this.mIsRunning = false;
	}
	
	public Pattern copyDLAToPattern() {
		Pattern tempPattern = new Pattern(mContext);
		synchronized (mAllPixels) {
			for (Pixel pixel: mAllPixels) {
				tempPattern.addPixel(pixel);
				tempPattern.incrementDrawIndex();
			}
		}
		tempPattern.setMaxDrawIndex(this.getAllPixelsSize());
		return tempPattern;
	}
	
	public boolean isRunning() {
		return this.mIsRunning;
	}
	
	public void reset() {
		// clear the pixel array
		// reset start point
	}
	
	public void setStartPoint(PointF startPoint) {
		this.mStartPoint = startPoint;
	}

	@Override
	public synchronized ArrayList<Pixel> getAllPixelsUpToDrawIndex() {
			return this.mAllPixels;
	}

	private void createGridPixels() {
		this.mDLAGrid = new DLAGrid(mStartPoint);
		try {
			mAllPixels.add(PixelFactory.makePixel(mStartPoint, PIXEL_WIDTH, getPixelColorSetting(), mSettings));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		mIsRunning = true;
		while (mAggregationCount < MAX_AGGREGATION && mIsRunning) {
			PointF nextPoint = mDLAGrid.getNextPoint();
			synchronized (mAllPixels) {
				try {
					mAllPixels.add(PixelFactory.makePixel(nextPoint, PIXEL_WIDTH, getPixelColorSetting(), mSettings));
					mAggregationCount++;
					incrementDrawIndex();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		mIsRunning = false;
		Log.i("DLA", "Done generating");
	}
}
