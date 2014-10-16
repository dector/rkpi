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
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Custom scroller for ViewPager for smooth auto-scroll
 *
 * @author dector
 */
public class FixedSpeedScroller extends Scroller {

	/** Scroll duration */
	private int mDuration = 800;

	/**
	 * Create new scroller instance
	 *
	 * @param context activity context
	 * @param interpolator scrolling encapsulated interpolator
	 */
	public FixedSpeedScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	/**
	 * Start scrolling by providing a starting point and the distance to travel.
	 *
	 * @param startX Starting horizontal scroll offset in pixels. Positive
	 *        numbers will scroll the content to the left.
	 * @param startY Starting vertical scroll offset in pixels. Positive numbers
	 *        will scroll the content up.
	 * @param dx Horizontal distance to travel. Positive numbers will scroll the
	 *        content to the left.
	 * @param dy Vertical distance to travel. Positive numbers will scroll the
	 *        content up.
	 * @param duration Duration of the scroll in milliseconds.
	 */
	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		// Ignore received duration, use fixed one instead
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	/**
	 * Start scrolling by providing a starting point and the distance to travel.
	 *
	 * @param startX Starting horizontal scroll offset in pixels. Positive
	 *        numbers will scroll the content to the left.
	 * @param startY Starting vertical scroll offset in pixels. Positive numbers
	 *        will scroll the content up.
	 * @param dx Horizontal distance to travel. Positive numbers will scroll the
	 *        content to the left.
	 * @param dy Vertical distance to travel. Positive numbers will scroll the
	 *        content up.
	 */
	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		// Ignore received duration, use fixed one instead
		super.startScroll(startX, startY, dx, dy, mDuration);
	}
}
