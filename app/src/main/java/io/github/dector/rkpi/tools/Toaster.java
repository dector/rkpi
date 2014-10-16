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
import android.os.Handler;
import android.widget.Toast;

/**
 * Show messages by android Toast
 *
 * @author dector
 */
public class Toaster {

	/** Singleton instance */
	private static Toaster sInstance;

	/**
	 * Init toaster before using
	 *
	 * @param context application context
	 */
	public static void init(Context context) {
		sInstance = new Toaster(context);
	}

	/**
	 * Returns singleton instance
	 *
	 * @return singleton instance
	 *
	 * @throws RuntimeException if Toaster wasn't initialized
	 */
	public static Toaster getInstance() {
		if (sInstance == null) {
			throw new RuntimeException("Toaster must be initialized before using");
		}

		return sInstance;
	}

	/** Android toast */
	private Toast mToast;

	/** UI-thread handler */
	private Handler mHandler;
	/** Action to show toast */
	private Runnable mAction;
	/** Toast message */
	private String mMessage;

	/**
	 * Create new instance
	 *
	 * @param context application context
	 */
	private Toaster(Context context) {
		if (context != null) {
			mHandler = new Handler();
			mAction = new Runnable() {
				@Override
				public void run() {
					mToast.setText(mMessage);
					mToast.show();
				}
			};

			mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
		}
	}

	/**
	 * Send notification text
	 *
	 * @param text text on toast
	 */
	public void send(String text) {
		mMessage = text;
		mHandler.post(mAction);
	}
}
