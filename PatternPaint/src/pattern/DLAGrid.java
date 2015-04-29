package pattern;

import java.util.ArrayList;
import java.util.Random;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

public class DLAGrid {

	private static int GRID_WIDTH = DLA.PIXEL_WIDTH * 100;
	private static int GRID_CELL_COUNT = GRID_WIDTH / DLA.PIXEL_WIDTH;
	
	PointF mGridCenter;
	Point mWanderingPoint;
	Random mRandom;
	ArrayList <PointF> mNewParticleBuffer;
	
	private boolean[][] mParticleGrid = new boolean[GRID_CELL_COUNT][GRID_CELL_COUNT];
	
	public DLAGrid(PointF startPoint) {
		mGridCenter = startPoint;
		init();
	}

	private void init() {
		mNewParticleBuffer = new ArrayList<PointF>();
		mWanderingPoint = new Point();
		mRandom = new Random();
		intializeGrid();
	}
	
	private void intializeGrid() {
		for (int row = 0; row < mParticleGrid.length; row++) {
			for (int column = 0; column < mParticleGrid.length; column++) {
				mParticleGrid[row][column] = false;
			}
		}
		mParticleGrid[(GRID_CELL_COUNT / 2) - 1][(GRID_CELL_COUNT / 2) -1] = true;
	}

	public PointF getNextPoint() {
		boolean validAggregate = false;
		PointF nextAggregatePosition = new PointF();
		while (!validAggregate) {
			mNewParticleBuffer.clear();
			Log.i("DLA", "Creating new wanderer");
			setValidWanderingStartPoint();
			Log.i("DLA", "Wanderer is at " + String.valueOf(mWanderingPoint.x) + "  " + String.valueOf(mWanderingPoint.y) );
			wander();
			if (!mNewParticleBuffer.isEmpty()) {
				validAggregate = true;
				nextAggregatePosition.set(mNewParticleBuffer.get(0));
			}
		}
		return nextAggregatePosition;
	}
	
	private void setValidWanderingStartPoint() {
		Point validWanderingStartPoint = validWanderingStartPoint();
		mWanderingPoint.x = validWanderingStartPoint.x;
		mWanderingPoint.y = validWanderingStartPoint.y;
	}

	private void wander() {
		Log.i("DLA", "Starting to Wander");
		boolean doneWandering = false;
		while (!doneWandering) {
			int randomDX = mRandom.nextInt(3) - 1;
			int randomDY = mRandom.nextInt(3) - 1; 
			Log.i("DLA - Random Direction ", String.valueOf(randomDX) + ", " + String.valueOf(randomDY) );
			if ( isInsideBoundaries(mWanderingPoint.x + randomDX, mWanderingPoint.y + randomDY) ) {
				mWanderingPoint.x += randomDX;
				mWanderingPoint.y += randomDY;
				doneWandering = checkAdjacency();
			} else {
				doneWandering = true;
			}			
		}
	}
	
	private PointF convertWanderingGridPointToPointF() {
		PointF convertedPoint = new PointF();
		float x = mGridCenter.x + ((mWanderingPoint.x - (GRID_CELL_COUNT / 2) ) * DLA.PIXEL_WIDTH);
		float y = mGridCenter.y + ((mWanderingPoint.y - (GRID_CELL_COUNT / 2) ) * DLA.PIXEL_WIDTH);
		convertedPoint.set(x, y);
		return convertedPoint;
	}

	private boolean checkAdjacency() {
		if (isAdjacentToExisting(mWanderingPoint)) {
			Log.i("DLA", "Found Adjacent pixels");
			mNewParticleBuffer.add(convertWanderingGridPointToPointF());
			mParticleGrid[mWanderingPoint.y][mWanderingPoint.x] = true;
			return true;			
		}else {
			return false;
		}
	}

	private Point validWanderingStartPoint() {
		int randomRow;
		int randomColumn;
		Point tempPoint = new Point();
		boolean gotValidPoint = false;
		while (!gotValidPoint) {
			randomRow = mRandom.nextInt(GRID_CELL_COUNT);
			randomColumn = mRandom.nextInt(GRID_CELL_COUNT);
			if (!collision(randomRow, randomColumn)) {
				gotValidPoint = true;
				
				// X value is Column, Y value is Row
				tempPoint.set(randomColumn, randomRow);
			}
		}
		
		return tempPoint;
	}

	private boolean isAdjacentToExisting(Point location) {
		int row = location.y;
		int column = location.x;
		
		int up = row - 1;
		int down = row + 1;
		int left = column - 1;
		int right = column + 1;

		if 		( 	collision(up, column) || collision(down, column) || collision(row, left) || collision(row, right) 
				|| 	collision(up, right) || collision(down, right) || collision(down, left) || collision(up, left)
				) 
		{
			Log.i("ADJACENT?", String.valueOf(true));
			return true;
		} 
		Log.i("ADJACENT?", String.valueOf(false));
		return false;
	}
	
	private boolean collision(int row, int column) {
		if (!isInsideBoundaries(row, column)) {
			return false;
		} else if (mParticleGrid[row][column] == true) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isInsideBoundaries(int row, int column) {
		if (	row >= GRID_CELL_COUNT ||
				row < 0 ||
				column >= GRID_CELL_COUNT ||
				column < 0) {
			return false; 
		} else {
			return true;
		}
	}
}
