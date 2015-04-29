package pattern;

import java.util.ArrayList;

import pixel.Pixel;

import com.james.android.patternpaint.PatternPaintMainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

public class Pattern {
	protected ArrayList<Pixel> mAllPixels = new ArrayList<Pixel>();
	private int mDrawIndex;
	private int mMaxDrawIndex;
	protected Context mContext;
	protected SharedPreferences mSettings;
	
	public Pattern(Context context) {
		this.mDrawIndex = 0;
		this.mContext = context;
		mSettings = mContext.getSharedPreferences(PatternPaintMainActivity.PREFS_NAME, 0);
	}
	
	protected Pixel getPixelAtIndex(int index) {
		return mAllPixels.get(index);
	}
	
	protected ArrayList<Pixel> getAllPixels() {
		return this.mAllPixels;
	}
	
	protected void addPixel(Pixel pixel) {
		mAllPixels.add(pixel);
	}
	
	protected Paint getPixelColorSetting() {
		int colorSetting = mSettings.getInt(PatternPaintMainActivity.KEY_PIXEL_COLOR, Color.WHITE);
		Paint colorARGB = new Paint();
		colorARGB.setColor(colorSetting);
		return colorARGB;
	}
	
	public ArrayList<Pixel> getAllPixelsUpToDrawIndex() {
		ArrayList<Pixel> pixels = new ArrayList<Pixel>();
		for (int i = 0; i < mDrawIndex; i++) {
			pixels.add(mAllPixels.get(i));
		}
		return pixels; 
	}
	
	public void setMaxDrawIndex(int max) {
		this.mMaxDrawIndex = max;
	}
	
	public int getMaxDrawIndex(){
		return mMaxDrawIndex;
	}

	public int getDrawIndex() {
		return this.mDrawIndex;
	}
	
	public void incrementDrawIndex() {
		this.mDrawIndex++;
	}

	public int getAllPixelsSize() {
		return mAllPixels.size();
	}
}
