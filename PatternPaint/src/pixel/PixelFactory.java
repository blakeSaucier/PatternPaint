package pixel;

import com.james.android.patternpaint.PatternPaintMainActivity;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.PointF;

public class PixelFactory {
	public static Pixel makePixel(PointF location, SharedPreferences settings) throws Exception {
		Pixel pixel = null;
		int pixelType = settings.getInt(PatternPaintMainActivity.KEY_PIXEL_TYPE, 0);
		switch (pixelType) {
		case PatternPaintMainActivity.PIXEL_BOX:
			pixel = new Box(location);
			break;
		case PatternPaintMainActivity.PIXEL_CIRCLE:
			pixel = new Circle(location);
			break;
		case PatternPaintMainActivity.PIXEL_SPLATTER:
			pixel = new Splatter(location);
			break;
		default:
			throw new Exception("Could not construct pixel");
		}
		return pixel;
	}
	
	public static Pixel makePixel(PointF location, float width, Paint color, SharedPreferences settings) throws Exception {
		Pixel pixel = null;
		int pixelType = settings.getInt(PatternPaintMainActivity.KEY_PIXEL_TYPE, PatternPaintMainActivity.PIXEL_CIRCLE);
		boolean useOutline = settings.getBoolean(PatternPaintMainActivity.KEY_PIXEL_OUTLINE, PatternPaintMainActivity.DEFAULT_PIXEL_OUTLINE);
		switch (pixelType) {
		case PatternPaintMainActivity.PIXEL_BOX:
			pixel = new Box(location, width, color, useOutline);
			break;
		case PatternPaintMainActivity.PIXEL_CIRCLE:
			pixel = new Circle(location, width, color, useOutline);
			break;
		case PatternPaintMainActivity.PIXEL_SPLATTER:
			int splatterSize = settings.getInt(PatternPaintMainActivity.KEY_SPLATTER_SIZE, PatternPaintMainActivity.DEFAULT_SPLATTER_SIZE);
			pixel = new Splatter(location, width, splatterSize, color, useOutline);
			break;
		default:
			throw new Exception("Could not construct pixel");
		}
		return pixel;
	}
	
}
