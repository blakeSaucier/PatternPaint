package pattern;

import java.util.ArrayList;
import java.util.Random;

import pixel.Pixel;
import pixel.PixelFactory;

import com.james.android.patternpaint.PatternPaintMainActivity;
import com.james.android.patternpaint.Vector;

import android.content.Context;
import android.graphics.PointF;

public class Particle extends Pattern {
	private static final int DEFAULT_LIFESPAN = 50;
	private static final int SCREEN_TOP = 0;
	private static final int SCREEN_LEFT = 0;
	private static final int SCREEN_RIGHT = PatternPaintMainActivity.getDrawingViewWidth();
	private static final int SCREEN_BOTTOM = PatternPaintMainActivity.getDrawingViewHeight();
	private float mLifeSpan;
	private Random mRandomGenerator;
	private Vector mAcceleration;
	private Vector mLocation;
	private Vector mVelocity;
	
	public Particle(PointF startPoint, Context context) {
		super(context);
		this.mLocation = new Vector(startPoint);
		//Initialize Random generator here instead of inside random() method
		mRandomGenerator = new Random();
		checkGravitySettings();
		mLifeSpan = getParticleLength();
		generatePath();
		setMaxDrawIndex(getAllPixelsSize());
	}
		
	private float getParticleLength() {
		return mSettings.getInt(PatternPaintMainActivity.KEY_PARTICLE_LENGTH, DEFAULT_LIFESPAN);
	}

	private void checkGravitySettings() {
		int gravityDirection = mSettings.getInt(PatternPaintMainActivity.KEY_GRAVITY_DIRECTION, PatternPaintMainActivity.GRAVITY_DOWN);
		
		switch (gravityDirection) {
			case (PatternPaintMainActivity.GRAVITY_UP):
				this.mAcceleration = new Vector(0, -1f);
				this.mVelocity = new Vector(random(-25, 25), random(15, 25));
				break;
			case (PatternPaintMainActivity.GRAVITY_DOWN):
				this.mAcceleration = new Vector(0,1f);
				this.mVelocity = new Vector(random(-25f, 25f), random(-25f, -15f));
				break;
			case (PatternPaintMainActivity.GRAVITY_LEFT):
				this.mAcceleration = new Vector(-1f, 0);
				this.mVelocity = new Vector(random(15, 25), random(-25, 25));
				break;
			case (PatternPaintMainActivity.GRAVITY_RIGHT):
				this.mAcceleration = new Vector(1f, 0);
				this.mVelocity = new Vector(random(-25, -15), random(-25, 25));
				break;
		}
	}
	
	@Override
	public ArrayList<Pixel> getAllPixelsUpToDrawIndex() {
		ArrayList<Pixel> pixels = new ArrayList<Pixel>();
		for (int i = 0; i < getDrawIndex(); i++) {
			pixels.add(getPixelAtIndex(i));
		}
		if (getAllPixelsSize() > getDrawIndex()) {
			addParticleHead(pixels);
		}
		return pixels;
	}

	private void addParticleHead(ArrayList<Pixel> pixels) {
		PointF headBoxLocation = getPixelAtIndex(getDrawIndex()).getLocation();
		try {
			Pixel headPixel = PixelFactory.makePixel(headBoxLocation, 60f, getPixelColorSetting(), mSettings);
			pixels.add(headPixel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generatePath() {
		while (this.isAlive()) {
			try {
				Pixel tempPixel = PixelFactory.makePixel(mLocation.getPointFromVector(), 15f, getPixelColorSetting(), mSettings);
				addPixel(tempPixel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			collisionDetection();
			mVelocity.add(mAcceleration);
			mLocation.add(mVelocity);
			mLifeSpan--;
		}
	}

	private void collisionDetection() {
		if (mLocation.getX() >= SCREEN_RIGHT) {
			mVelocity.invertX();
			mVelocity.multiplyX(0.8f);
		}
		if (mLocation.getX() <= SCREEN_LEFT) {
			mVelocity.invertX();
			mVelocity.multiplyX(0.8f);
		}
		if (mLocation.getY() >= SCREEN_BOTTOM) {
			mVelocity.invertY();
			mVelocity.multiplyY(0.8f);
		}
		if (mLocation.getY() <= SCREEN_TOP) {
			mVelocity.invertY();
		}
	}

	private boolean isAlive() {
		if (this.mLifeSpan > 0) {
			return true;
		} else {
			return false;
		}
	}

	private float random(float min, float max) {
		float randomNum = mRandomGenerator.nextFloat();
		return randomNum * (max - min) + min;
	}
}