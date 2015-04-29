package pixel;

import java.util.ArrayList;
import java.util.Random;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class Splatter extends Pixel {
	
	private Random mRandom;
	private ArrayList<Circle> mSplatter = new ArrayList<Circle>();
	
	public Splatter(PointF location, float width, int splatterSize, Paint color, boolean useOutline) {
		super(location, width, color, useOutline);
		mRandom = new Random();
		
		float left = (this.getLocation().x) - (this.getWidth() / 2);
		float right = (this.getLocation().x) + (this.getWidth() / 2);
		float top = (this.getLocation().y) - (this.getWidth() / 2);
		float bottom = (this.getLocation().y) + (this.getWidth() / 2);
		
		for(int i = 0; i < getSplatterCount(width); i++) {
			float xCoordinate = random(left, right);
			float yCoordinate = random(top, bottom);
			PointF tempPoint = new PointF(xCoordinate, yCoordinate);
			mSplatter.add(new Circle(tempPoint, splatterSize, this.getColor(), useOutline));
		}
	}
	
	private int getSplatterCount(float width) {
		float splatterCount = width / 10;
		return Math.round(splatterCount);
	}

	public Splatter(PointF location) {
		super(location);
		mRandom = new Random();
	}

	@Override
	public void draw(Canvas canvas) {
		for(Circle circle: mSplatter) {
			circle.draw(canvas);
		}
	}
	
	private float random(float min, float max) {
		float randomNum = mRandom.nextFloat();
		return randomNum * (max - min) + min;
	}
	
}
