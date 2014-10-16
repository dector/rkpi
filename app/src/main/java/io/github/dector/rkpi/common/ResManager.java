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
package io.github.dector.rkpi.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Custom application resource manager. Now stores only fonts from assets folder
 * <b>!!! <u>Need to be initialized with init(Context) before using</u></b>
 *
 * @author dector
 *
 * @see #init(android.content.Context)
 */
public class ResManager {

	/** Singleton instance */
	private static ResManager sInstance;

	/**
	 * Check and return singleton instance if was initialized
	 *
	 * @return stored instance
	 *
	 * @throws RuntimeException if ResManager wasn't initialized
	 *
	 * @see #init(android.content.Context)
	 */
	public static ResManager getInstance() {
		if (sInstance == null) {
			throw new RuntimeException("Init ResManager before using");
		}

		return sInstance;
	}

	/**
	 * Init ResManager instance.
	 * <b>!!! <u>Need to be called once before using</u></b>
	 *
	 * @param context application context
	 */
	public static void init(Context context) {
		sInstance = new ResManager(context.getAssets());
	}

	/** Normal font */
	private Typeface mNormalTypeface;
	/** Bold font */
	private Typeface mBoldTypeface;

	/**
	 * Create new instance and load fonts
	 *
	 * @param assets Android AssetManager instance
	 */
	private ResManager(AssetManager assets) {
		mNormalTypeface = Typeface.createFromAsset(assets, "fonts/roboto_thin.ttf");
		mBoldTypeface = Typeface.createFromAsset(assets, "fonts/roboto_bold.ttf");
	}

	/**
	 * Return normal application font
	 *
	 * @return normal application font
	 */
	public Typeface getNormalTypeface() {
		return mNormalTypeface;
	}

	/**
	 * Return bold application font
	 *
	 * @return bold application font
	 */
	public Typeface getBoldTypeface() {
		return mBoldTypeface;
	}
}
