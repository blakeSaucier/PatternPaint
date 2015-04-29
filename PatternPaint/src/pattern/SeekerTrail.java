package pattern;

import java.util.ArrayList;

import pixel.Pixel;
import android.content.Context;

public class SeekerTrail extends Pattern {

	public SeekerTrail(Context context) {
		super(context);
	}
	
	public void clearPixels() {
		this.getAllPixels().clear();
	}
	
	public void add(Pixel pixel) {
		super.addPixel(pixel);
		this.incrementDrawIndex();
	}
	
	public ArrayList<Pixel> getTrail() {
		return this.getAllPixels();
	}

	@Override
	public ArrayList<Pixel> getAllPixelsUpToDrawIndex() {
		return this.getAllPixels();
	}
	
	public SeekerTrail copy() {
		SeekerTrail temp = new SeekerTrail(mContext);
		for (Pixel pixel: this.getAllPixels()) {
			temp.add(pixel);
		}
		return temp;
	}
}
