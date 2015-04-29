package pixel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;

public class Circle extends Pixel {

	public Circle(PointF location) {
		super(location);
	}
	
	public Circle(PointF location, float width, Paint color, boolean useOutline ) {
		super(location, width, color, useOutline);
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (useOutline()) {
			// Draw the shape outline
			Paint outlinePaint = new Paint();
			outlinePaint.setStyle(Style.STROKE);
			outlinePaint.setColor(Color.BLACK);
			outlinePaint.setStrokeWidth(3);
			canvas.drawCircle(this.getX(), this.getY(), this.getWidth()/2 , outlinePaint);
		}
		canvas.drawCircle(this.getX(), this.getY(), this.getWidth()/2 , this.getColor());
	}
}
