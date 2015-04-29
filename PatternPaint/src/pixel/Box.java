package pixel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Style;

public class Box extends Pixel {

	public Box(PointF location) {
		super(location);
	}
	
	public Box(PointF location, float width, Paint color, boolean useOutline ) {
		super(location, width, color, useOutline);
	}
	
	@Override
	public void draw(Canvas canvas) {
		float left = (this.getLocation().x) - (this.getWidth() / 2);
		float right = (this.getLocation().x) + (this.getWidth() / 2);
		float top = (this.getLocation().y) - (this.getWidth() / 2);
		float bottom = (this.getLocation().y) + (this.getWidth() / 2);
		if (useOutline()) {
			Paint outlinePaint = new Paint();
			outlinePaint.setStyle(Style.STROKE);
			outlinePaint.setColor(Color.BLACK);
			outlinePaint.setStrokeWidth(3);
			canvas.drawRect(left, top, right, bottom, outlinePaint);
		}
		canvas.drawRect(left, top, right, bottom, this.getColor());
	}
}
