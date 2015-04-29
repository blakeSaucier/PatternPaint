package seekerTarget;

import pattern.SeekerTrail;
import pixel.Pixel;
import pixel.PixelFactory;

import com.james.android.patternpaint.PatternPaintMainActivity;
import com.james.android.patternpaint.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Seeker {
	private Context mContext;
	private Vector mLocation;
	private Vector mVelocity;
	private Vector mAcceleration;
	
	private SharedPreferences mSettings;
	private float mMaxSpeed;
	private float mMaxForce;
	private boolean mIsRunning;
	
	private Paint mSeekerPaint;
	private SeekerTrail mSeekerTrail;
	
	public Seeker(Context context, float x, float y, SeekerTrail seekerTrail) {
		mContext = context;
		mLocation = new Vector(x, y);
		mVelocity = new Vector(0, 0);
		mAcceleration = new Vector(0, 0);
		mSettings = mContext.getSharedPreferences(PatternPaintMainActivity.PREFS_NAME, 0);
		mMaxSpeed = getMaxSpeedPreference();
		mMaxForce = getForcePreference();
		mIsRunning = false;
		
		mSeekerPaint = new Paint();
		mSeekerPaint.setColor(Color.YELLOW);
		mSeekerTrail = seekerTrail;
	}
	
	public boolean isRunning() {
		return mIsRunning;
	}
	
	public void setRunning(boolean isRunning) {
		mIsRunning = isRunning;
	}
	
	public void reset(float x, float y) {
		mIsRunning = false;
		clearTrail();
		mLocation.set(x, y);
		mVelocity.set(0, 0);
		mAcceleration.set(0, 0);
	}
	
	public SeekerTrail stop(float x, float y) {
		mIsRunning = false;
		SeekerTrail tempTrail = mSeekerTrail.copy();
		clearTrail();
		mLocation.set(x, y);
		mVelocity.set(0, 0);
		mAcceleration.set(0, 0);
		return tempTrail;
	}
	
	public void pause() {
		mIsRunning = false;
		clearTrail();
	}
	
	public void updateSeeker(Vector target) {
		if (mIsRunning) {
			Paint boxColor = getColorSetting();
			try {
				Pixel tempPixel = PixelFactory.makePixel(getLocation(), 15f, boxColor, mSettings);
				mSeekerTrail.add(tempPixel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.seek(target);
			this.updateVectors();
		}
	}
	
	public SeekerTrail getSeekerTrail() {
		return this.mSeekerTrail;
	}
	
	private void clearTrail() {
		this.mSeekerTrail.clearPixels();
	}
	
	private void seek(Vector target) {
		mMaxSpeed = getMaxSpeedPreference();
		mMaxForce = getForcePreference();
		
		Vector desired = Vector.subtract(target, mLocation);
		desired.normalize();
		desired.multiply(mMaxSpeed);
		
		Vector steer = Vector.subtract(desired, mVelocity);
		steer.limit(mMaxForce);
		mAcceleration.add(steer);
	}
	
	private void updateVectors() {
		mVelocity.add(mAcceleration);
		mLocation.add(mVelocity);
		mAcceleration.multiply(0);
	}
	
	public PointF getLocation() {
		return new PointF(mLocation.getX(), mLocation.getY());
	}
	
	public float getX() {
		return mLocation.getX();
	}
	
	public float getY() {
		return mLocation.getY();
	}
	
	public Paint getSeekerColor() {
		return mSeekerPaint;
	}
	
	private float getForcePreference() {
		return getFloatPreference(PatternPaintMainActivity.KEY_SEEKER_FORCE, PatternPaintMainActivity.DEFAULT_SEEKER_FORCE);
	}
	
	private float getMaxSpeedPreference() {
		return getFloatPreference(PatternPaintMainActivity.KEY_SEEKER_MAXSPEED, PatternPaintMainActivity.DEFAULT_SEEKER_MAXSPEED);
	}
	
	private Paint getColorSetting() {
		int colorSetting = mSettings.getInt(PatternPaintMainActivity.KEY_PIXEL_COLOR, Color.WHITE);
		Paint colorARGB = new Paint();
		colorARGB.setColor(colorSetting);
		return colorARGB;
	}
	
	private float getFloatPreference(String key, float defaultValue) {
		float preference = mSettings.getFloat(key, defaultValue);
		return preference;
	}
}
