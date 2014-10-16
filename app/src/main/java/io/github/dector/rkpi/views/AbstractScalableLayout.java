/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 dector
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.dector.rkpi.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Layout, that scales to fit screen
 *
 * @author dector
 */
public class AbstractScalableLayout extends ViewGroup {

	/** Base layout width */
	protected int mLayoutWidth;
	/** Base layout height */
	protected int mLayoutHeight;

	/**
	 * New instance
	 *
	 * @param context activity context
	 */
	public AbstractScalableLayout(Context context) {
		super(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = r - l;
		int height = b - t;

		float scaleX = (float) width / mLayoutWidth;
		float scaleY = (float) height / mLayoutHeight;
		float scaleProp = Math.min(scaleX, scaleY);

		final boolean scaledByY = scaleY < scaleX;

		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			LayoutParams lp = (LayoutParams) v.getLayoutParams();

			float concreteXScale = (lp.scaleProportional) ? scaleProp : scaleX;
			float concreteYScale = (lp.scaleProportional) ? scaleProp : scaleY;

			int scaledWidth = (int) (lp.width * concreteXScale);
			int scaledHeight = (int) (lp.height * concreteYScale);

			/*int leftOffset = 0;
			if (lp.scaleProportional) {
//				leftOffset = (scaledByY)
//						? scaledWidth - (int) (lp.width * scaleX) : ;
				leftOffset = ((int) (lp.width * scaleX) - scaledWidth) / 2;
				Log.d("Offset", "Left: " + leftOffset);
			}*/

			/*int leftOffset = (lp.alignRight)
					? ((scaledByY) ? Math.abs(scaledWidth - (int) (lp.width * scaleX)) : 0)
					: 0;*/

			int left = /*leftOffset +*/ ((lp.centerHorizontal)
					? (width - scaledWidth) / 2
					: (int) (lp.x * scaleX));
			int top = (lp.centerVertical)
					? (height - scaledHeight) / 2
					: (int) (lp.y * scaleY);
			int right = left + scaledWidth;
			int bottom = top + scaledHeight;

			if (lp.textSize > 0 && v instanceof TextView) {
				((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (lp.textSize * concreteYScale));
			}

			v.layout(left, top, right, bottom);

			/*if (v.getTag() != null) {
				Log.d("Tag", v.getTag().toString() + " " + v.getWidth() + " " + v.getX());
			}*/
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = resolveSize(MeasureSpec.getSize(widthMeasureSpec), widthMeasureSpec);
		int height = resolveSize(MeasureSpec.getSize(heightMeasureSpec), heightMeasureSpec);

		float scaleX = width / mLayoutWidth;
		float scaleY = height / mLayoutHeight;

		int specX = MeasureSpec.getMode(widthMeasureSpec);
		int specY = MeasureSpec.getMode(heightMeasureSpec);

		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);

			if (v.getVisibility() != GONE) {
				LayoutParams lp = (LayoutParams) v.getLayoutParams();

				int childWidth = (int) (lp.width * scaleX);
				int childHeight = (int) (lp.height * scaleY);

				measureChild(
						v,
						MeasureSpec.makeMeasureSpec(childWidth, specX),
						MeasureSpec.makeMeasureSpec(childHeight, specY)
				);
			}
		}

		setMeasuredDimension(width, height);
	}

	/**
	 * Returns base layout width
	 *
	 * @return base layout width
	 */
	public int getLayoutWidth() {
		return mLayoutWidth;
	}

	/**
	 * Custom layout params for abstract scalable layout
	 */
	public class LayoutParams extends ViewGroup.LayoutParams {

		/** X coordinate */
		public int x;
		/** Y coordinate */
		public int y;

		/** Base text size for text views */
		public int textSize;

		/** Scale view proportional */
		public boolean scaleProportional;
		/** Center view horizontal */
		public boolean centerHorizontal;
		/** Center view vertical */
		public boolean centerVertical;
		/** Align view to right border, not to left */
		public boolean alignRight;

		/**
		 * Create new instance and copy values from base layout
		 *
		 * @param lp base layout
		 */
		public LayoutParams(LayoutParams lp) {
			this(lp.x, lp.y, lp.width, lp.height);

			this.scaleProportional = lp.scaleProportional;
			this.centerHorizontal = lp.centerHorizontal;
			this.centerVertical = lp.centerVertical;
			this.alignRight = lp.alignRight;
		}

		/**
		 * Create new instance with desired parameters
		 *
		 * @param x x coordinate
		 * @param y y coordinate
		 * @param width view's width
		 * @param height view's height
		 */
		public LayoutParams(int x, int y, int width, int height) {
			super(width, height);

			this.x = x;
			this.y = y;
		}

		/**
		 * Scale this view proportional
		 *
		 * @return layout params instance
		 */
		public LayoutParams scaleProportional() {
			scaleProportional = true;

			return this;
		}

		/**
		 * Center view horizontal
		 *
		 * @return layout params instance
		 */
		public LayoutParams centerHorizontal() {
			centerHorizontal = true;

			return this;
		}

		/**
		 * Center view vertical
		 *
		 * @return layout params instance
		 */
		public LayoutParams centerVertical() {
			centerVertical = true;

			return this;
		}

		/**
		 * Set text size for TextView
		 *
		 * @param textSize desired text size
		 * @return layout params instance
		 */
		public LayoutParams textSize(int textSize) {
			this.textSize = textSize;

			return this;
		}

		/**
		 * Align view to right border
		 *
		 * @return layout params instance
		 */
		public LayoutParams alignRight() {
			alignRight = true;

			return this;
		}
	}
}
