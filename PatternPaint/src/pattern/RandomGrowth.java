package pattern;

import java.util.ArrayList;
import java.util.Random;

import pixel.Pixel;
import pixel.PixelFactory;
import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

public class RandomGrowth extends Pattern {
	
	private enum Direction {
		UP(0), DOWN(1), LEFT(2), RIGHT(3);
		private int direction;
		
		private Direction(int direction) {
			this.direction = direction;
		}
	}

	private static final int MAX_GROWTHS = 100;
	private static final int PIXEL_WIDTH = 20;
	
	private ArrayList<PointF> mPointsBuffer;
	private PointF mStartPoint;
	private int mGrowthCount = 0;
	private Random mRandom;
	
	public RandomGrowth(Context context) {
		super(context);
	}
	
	public RandomGrowth(PointF startPoint, Context context) {
		super(context);
		mStartPoint = startPoint;
		init();
		generateDLA();
		setMaxDrawIndex(getAllPixelsSize());
	}

	private void init() {
		createFirstPixel();
		mRandom = new Random();
		mPointsBuffer = new ArrayList<PointF>();
	}

	private void createFirstPixel() {
		try {
			mAllPixels.add(PixelFactory.makePixel(mStartPoint, PIXEL_WIDTH, getPixelColorSetting(), mSettings));
			mGrowthCount++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generateDLA() {
		while (mGrowthCount < MAX_GROWTHS) {
			grow();
			addGrowthPointsToAllPixels();
		}
	}

	private void addGrowthPointsToAllPixels() {
		if (!mPointsBuffer.isEmpty()) {
			for (PointF point: mPointsBuffer) {
				try {
					mAllPixels.add(PixelFactory.makePixel(point, PIXEL_WIDTH, getPixelColorSetting(), mSettings));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mPointsBuffer.clear();
		}
	}

	private void grow() {
		tryToAddPixel(selectRandomPixel());
	}

	private Pixel selectRandomPixel() {
		return mAllPixels.get(mRandom.nextInt(mAllPixels.size()));
	}

	private void tryToAddPixel(Pixel pixel) {
		Direction randomDirection = getRandomDirection();
		switch(randomDirection) {
			case UP:
				tryToMoveUp(pixel);
				break;
			case DOWN:
				tryToMoveDown(pixel);
				break;
			case LEFT:
				tryToMoveLeft(pixel);
				break;
			case RIGHT:
				tryToMoveRight(pixel);
				break;
		}
	}

	private Direction getRandomDirection() {
		int direction = mRandom.nextInt(4);
		switch (direction) {
			case 0:
				Log.i("RANDOM DIRECTION", String.valueOf(0));
				return Direction.UP;
			case 1:
				Log.i("RANDOM DIRECTION", String.valueOf(1));
				return Direction.DOWN;
			case 2:
				Log.i("RANDOM DIRECTION", String.valueOf(2));
				return Direction.LEFT;
			case 3:
				Log.i("RANDOM DIRECTION", String.valueOf(3));
				return Direction.RIGHT;
			default:
				return Direction.UP;
		}
	}	
	
	private void tryToMoveRight(Pixel pixel) {
		if (isRoomRight(pixel.getLocation())) {
			PointF movedRight = new PointF(pixel.getX() + PIXEL_WIDTH, pixel.getY());
			mPointsBuffer.add(movedRight);
			mGrowthCount++;
		}
	}
	
	private void tryToMoveLeft(Pixel pixel) {
		if (isRoomLeft(pixel.getLocation())) {
			PointF movedLeft = new PointF(pixel.getX() - PIXEL_WIDTH, pixel.getY());
			mPointsBuffer.add(movedLeft);
			mGrowthCount++;
		}
	}

	private void tryToMoveUp(Pixel pixel) {
		if (isRoomAbove(pixel.getLocation())) {
			PointF movedUp = new PointF(pixel.getX(), pixel.getY() - PIXEL_WIDTH);
			mPointsBuffer.add(movedUp);
			mGrowthCount++;
		}
	}

	private void tryToMoveDown(Pixel pixel) {
		if (isRoomBelow(pixel.getLocation())) {
			PointF movedDown = new PointF(pixel.getX(), pixel.getY() + PIXEL_WIDTH);
			mPointsBuffer.add(movedDown);
			mGrowthCount++;
		}
	}	
	
	private boolean isRoomRight(PointF originalLocation) {
		PointF right = new PointF(originalLocation.x + PIXEL_WIDTH, originalLocation.y);
		return !isCollision(right);
	}
	
	private boolean isRoomLeft(PointF originalLocation) {
		PointF left = new PointF(originalLocation.x - PIXEL_WIDTH, originalLocation.y);
		return !isCollision(left);
	}
	
	private boolean isRoomBelow(PointF originalLocation) {
		PointF below = new PointF(originalLocation.x, originalLocation.y + PIXEL_WIDTH);
		return !isCollision(below);
	}

	private boolean isRoomAbove(PointF originalLocation) {
		PointF above = new PointF(originalLocation.x, originalLocation.y - PIXEL_WIDTH);
		return !isCollision(above);
	}

	private boolean isCollision(PointF newLocation) {
		for (Pixel pixel: mAllPixels) {
			if ( 	(Math.abs(newLocation.x - pixel.getX()) < PIXEL_WIDTH) &&
					(Math.abs(newLocation.y - pixel.getY()) < PIXEL_WIDTH)) {
				// the new pixel location is within the bounds of another pixel. There IS a collision
				Log.i("IS COLLISION", String.valueOf(true));
				return true;
			}
		}
		Log.i("IS COLLISION", String.valueOf(false));
		return false;
	}
}
