package pixel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class Pixel {

	private PointF mLocation;
	private float mWidth;
	private Paint mColor;
	private Boolean mUseOutline;
	
	public Pixel (PointF location) {
		mLocation = location;
		mWidth = 100;
	}
	
	public Pixel (PointF location, float width, Paint color, Boolean useOutline ) {
		mLocation = location;
		mWidth = width;
		mColor = color;
		mUseOutline = useOutline;
	}
	
	public float getWidth() {
		return mWidth;
	}
	
	public void setWidth(float width) {
		mWidth = width;
	}
	
	public void setLocation(PointF location) {
		mLocation = location;
	}
	
	public PointF getLocation() {
		return mLocation;
	}
	
	public float getX() {
		return mLocation.x;
	}
	
	public float getY() {
		return mLocation.y;
	}
	
	public void setColor(int color) {
		mColor.setColor(color);
	}
	
	public Paint getColor() {
		return mColor;
	}
	
	public Boolean useOutline() {
		return mUseOutline;
	}
	
	public abstract void draw(Canvas canvas);
}
