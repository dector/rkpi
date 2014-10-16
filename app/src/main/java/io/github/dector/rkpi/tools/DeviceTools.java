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
package io.github.dector.rkpi.tools;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author dector
 */
public class DeviceTools {

	private static final int DEFAULT_TABLET_DIAG_INCH = 6;

	public static boolean isTablet(Context context) {
		return isTablet(context, DEFAULT_TABLET_DIAG_INCH);
	}

	public static boolean isTablet(Context context, int requestedDiagInch) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		float wInch = metrics.widthPixels /metrics.xdpi;
		float hInch = metrics.heightPixels / metrics.ydpi;

		float diagInch = (float) Math.sqrt(wInch*wInch + hInch*hInch);

		return diagInch >= requestedDiagInch;
	}

	public static int getSampleSize(BitmapFactory.Options opts, Point displaySize) {
		int sampleSize = 1;

		final int w = opts.outWidth;
		final int h = opts.outHeight;
		final int reqW = displaySize.x;
		final int reqH = displaySize.y;

		if (h > reqH || w > reqW) {
			final int ratioW = Math.round((float) w / reqW);
			final int ratioH = Math.round((float) h / reqH);

			sampleSize = ratioW < ratioH ? ratioW : ratioH;
		}

		return sampleSize;
	}
}
