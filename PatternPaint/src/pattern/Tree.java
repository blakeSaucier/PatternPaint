package pattern;

import java.util.ArrayList;

import pixel.Pixel;
import pixel.PixelFactory;

import com.james.android.patternpaint.PatternPaintMainActivity;

import android.content.Context;
import android.graphics.PointF;

public class Tree extends Pattern {
	
	
	private static final float BRANCH_DECREASE = 0.75f;
	private static final double BRANCH_ANGLE = Math.toRadians(50);
	
	// drawing parameters taken from shared prefs
	private double mAngle;
	private float mShrinkRate;
	private int mPixelEndWidth;
	private int mPixelStartWidth;
	private int mMax_arcLength;
	private double mAngleOffset;
	
	ArrayList<Pixel> mDrawnPixels;	
	float mPixelCurrentWidth;
	PointF mCurrentPosition;
	PointF mNextPosition;
		
	public Tree(PointF startPoint, Context context) {
		super(context);
		init(startPoint);
		try {
			Pixel startPixel = PixelFactory.makePixel(mCurrentPosition, mPixelCurrentWidth, getPixelColorSetting(), mSettings);
			addPixel(startPixel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		generateTree();
		setMaxDrawIndex(getAllPixelsSize());
	}

	private void init(PointF startPoint) {
		mDrawnPixels = new ArrayList<Pixel>();
		mAngle = getDirectionFromGravitySetting();
		mMax_arcLength = mSettings.getInt(PatternPaintMainActivity.KEY_TREE_MAX_ARC_LENGTH, PatternPaintMainActivity.DEFAULT_TREE_MAX_ARC_LENGTH);		
		mAngleOffset = Math.toRadians(mSettings.getInt(PatternPaintMainActivity.KEY_TREE_ARC_ANGLE, PatternPaintMainActivity.DEFAULT_TREE_ARC_ANGLE));
		mShrinkRate = mSettings.getFloat(PatternPaintMainActivity.KEY_TREE_SHRINK_RATE, PatternPaintMainActivity.DEFAULT_TREE_SHRINK_RATE);
		mPixelEndWidth = mSettings.getInt(PatternPaintMainActivity.KEY_TREE_END_SIZE, PatternPaintMainActivity.DEFAULT_TREE_END_SIZE);
		mPixelStartWidth = mSettings.getInt(PatternPaintMainActivity.KEY_TREE_START_SIZE, PatternPaintMainActivity.DEFAULT_TREE_START_SIZE);
		
		mPixelCurrentWidth = mPixelStartWidth;
		mCurrentPosition = startPoint;
	}

	private void generateTree() {
		while (mPixelCurrentWidth > mPixelEndWidth) {
			if (fiftyFiftry()){
				leanLeft();
				branchLeft();
				branchRight();
				leanRight();
			} else {
				leanRight();
				branchLeft();
				branchRight();
				leanLeft();
			}
		}
	}
	
	private double getDirectionFromGravitySetting() {
		double initializedAngle = Math.toRadians(270);

		int gravityDirection = mSettings.getInt(PatternPaintMainActivity.KEY_GRAVITY_DIRECTION, PatternPaintMainActivity.GRAVITY_DOWN);
		
		switch (gravityDirection) {
			case(PatternPaintMainActivity.GRAVITY_UP):
				initializedAngle = Math.toRadians(270);			
				break;
			case(PatternPaintMainActivity.GRAVITY_DOWN):
				initializedAngle = Math.toRadians(90);			
				break;
			case(PatternPaintMainActivity.GRAVITY_LEFT):
				initializedAngle = Math.toRadians(180);			
				break;
			case(PatternPaintMainActivity.GRAVITY_RIGHT):
				initializedAngle = Math.toRadians(0);			
				break;
		}
		return initializedAngle;
	}

	private PointF arcLeft(PointF currentPosition) {
	      float newx = (float) (currentPosition.x + Math.cos(mAngle) * mPixelCurrentWidth);       
	      float newy = (float) (currentPosition.y + Math.sin(mAngle) * mPixelCurrentWidth);
	      mPixelCurrentWidth = mPixelCurrentWidth * mShrinkRate;
	      mAngle = mAngle + mAngleOffset;
	      return new PointF(newx, newy);
	}

	private PointF arcRight(PointF currentPosition) {
		float newx = (float) (currentPosition.x + Math.cos(mAngle) * ( mPixelCurrentWidth ));       
		float newy = (float) (currentPosition.y + Math.sin(mAngle) * ( mPixelCurrentWidth ));
		mPixelCurrentWidth = mPixelCurrentWidth * mShrinkRate;
		mAngle = mAngle - mAngleOffset;
		return new PointF(newx, newy);
	}

	private void branchLeft() {
		// store the member variables as they will be manipulated and assigned back again
		PointF storedCurrentPosition = mCurrentPosition;
		PointF storedNextPosition = mNextPosition;
		double storedTrunkAngle = mAngle;
		float storedBoxCurrentWidth = Float.valueOf(mPixelCurrentWidth);
		
		mAngle -= BRANCH_ANGLE;
		mPixelCurrentWidth = mPixelCurrentWidth * BRANCH_DECREASE;
		while ( mPixelCurrentWidth > mPixelEndWidth ) {
			leanLeft();
			leanRight();
		}
		mAngle = storedTrunkAngle;
		mCurrentPosition = storedCurrentPosition;
		mNextPosition = storedNextPosition;
		mPixelCurrentWidth = storedBoxCurrentWidth;
	}

	private void branchRight() {
		// store the member variables as they will be manipulated and assigned back again
		PointF storedCurrentPosition = mCurrentPosition;
		PointF storedNextPosition = mNextPosition;
		double storedTrunkAngle = mAngle;
		float storedBoxCurrentWidth = mPixelCurrentWidth;
		
		mAngle += BRANCH_ANGLE;
		mPixelCurrentWidth = mPixelCurrentWidth * BRANCH_DECREASE;
		while ( mPixelCurrentWidth > mPixelEndWidth ) {
			leanRight();
			leanLeft();
		}
		mAngle = storedTrunkAngle;
		mCurrentPosition = storedCurrentPosition;
		mNextPosition = storedNextPosition;
		mPixelCurrentWidth = storedBoxCurrentWidth;
	}
	
	private boolean fiftyFiftry() {
		return Math.random() > 0.5;
	}
	
	private int getArcLength() {
		double randomValue = ( Math.random() ) * mMax_arcLength;
		return (int) Math.ceil(randomValue);
	}

	private void leanLeft() {
		for ( int i = 0; i < getArcLength(); i++) {
			mNextPosition = arcLeft(mCurrentPosition);
			try {
			Pixel tempPixel = PixelFactory.makePixel(mNextPosition, mPixelCurrentWidth, getPixelColorSetting(), mSettings);
			addPixel(tempPixel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			moveToNextPoint();
			if (mPixelCurrentWidth <= mPixelEndWidth) {
				break;
			}
		}
	}

	private void leanRight() {
		for ( int i = 0; i < getArcLength(); i++) {
			mNextPosition = arcRight(mCurrentPosition);
			try {
			Pixel tempPixel = PixelFactory.makePixel(mNextPosition, mPixelCurrentWidth, getPixelColorSetting(), mSettings);
			addPixel(tempPixel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			moveToNextPoint();
			if (mPixelCurrentWidth <= mPixelEndWidth) {
				break;
			}
		}
	}

	private void moveToNextPoint() {
		mCurrentPosition = mNextPosition;
	}
}
