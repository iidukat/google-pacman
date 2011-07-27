package jp.or.iidukat.example.pacman;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

class Presentation {
	String id;
	int width;
	int height;
	float left;
	float top;
	float bgPosX = Float.NaN;
	float bgPosY = Float.NaN;
	int bgColor;
	boolean visibility = true;
	Rect src = new Rect();
	RectF dest = new RectF();
	Paint paint = new Paint();
	Presentation parent;
	
	boolean hasBackground() {
		return !Float.isNaN(bgPosX) && !Float.isNaN(bgPosY);
	}
	
	void drawBitmap(Bitmap sourceImage, Canvas c) {
		float top = 0;
		float left = 0;
		Presentation p = this;
		do {
			top += p.top;
			left += p.left;
		} while ((p = p.parent) != null);

		// TODO: floatをintに変更して問題ないかどうか検討すること
		src.set(
			Math.round(bgPosX),
			Math.round(bgPosY),
			Math.round(bgPosX + width),
			Math.round(bgPosY + height));
		dest.set(
				left,
				top,
				left + width,
				top + height);
		c.drawBitmap(sourceImage, src, dest, null);
	}
	
	void drawRectShape(Canvas c) {
		float top = 0;
		float left = 0;
		Presentation p = this;
		do {
			top += p.top;
			left += p.left;
		} while ((p = p.parent) != null);

		dest.set(
				left,
				top,
				left + width,
				top + height);
		
		paint.setColor(bgColor);
		paint.setAlpha(0xff);
		
		c.drawRect(dest, paint);
	}

}
