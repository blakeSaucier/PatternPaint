package seekerTarget;

import com.james.android.patternpaint.Vector;

import android.graphics.PointF;

public class Target {
	private PointF mLocation;
	private float mWidth;
	private boolean mIsVisible;
	
	public Target(PointF point) {
		this.mLocation = new PointF(point.x, point.y);
		mWidth = 100f;
		mIsVisible = false;
	}
	
	public Target(float x, float y) {
		this.mLocation = new PointF(x, y);
		mWidth = 100f;
		mIsVisible = false;
	}
	
	public float getX() {
		return mLocation.x;
	}
	
	public float getY() {
		return mLocation.y;
	}
	
	public void setLocation(float x, float y) {
		mLocation.set(x, y);
	}
	
	public void setLocation(PointF point) {
		mLocation.set(point);
	}
	
	public void setWidth(float width) {
		this.mWidth = width;
	}
	
	public float getWidth() {
		return mWidth;
	}
	
	public Vector getTargetVector() {
		return new Vector(mLocation.x, mLocation.y);
	}
	
	public void setVisibility(boolean isVisible) {
		this.mIsVisible = isVisible;
	}
	
	public boolean isVisisble() {
		return mIsVisible;
	}
}
