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
import android.content.SharedPreferences;

import io.github.dector.rkpi.components.player.PlayerManager;

/**
 * Custom application preference manager.
 * <b>!!! <u>Need to be initialized with init(Context) before using</u></b>
 *
 * @author dector
 *
 * @see #init(android.content.Context)
 */
public class PrefManager {

	/** Created android-base preferences wrapper */
	private static PrefWrapper sWrapper;

	/** Set of preference keys, which are used in wrapper */
	private static final String KEY_APP_INITIALIZED = "pref_app_initialized";
	private static final String KEY_STREAM_ID 		= "pref_stream_id";
	private static final String KEY_FOREGROUND      = "pref_foreground";
	private static final String KEY_IGNORE_AUDIO_FOCUS = "pref_ignore_audio_focus";

	/**
	 * Init PrefManager.
	 * <b>!!! <u>Need to be called once before using</u></b>
	 *
	 * @param context application context
	 */
	public static void init(Context context) {
		sWrapper = new PrefWrapper(context);
	}

	/**
	 * Check stored wrapper and return it if PrefManager was initialized
	 *
	 * @return stored wrapper
	 *
	 * @throws RuntimeException if PrefManager wasn't initialized
	 *
	 * @see #init(android.content.Context)
	 * @see PrefWrapper
	 */
	private static PrefWrapper getWrapper() {
		if (sWrapper == null) {
			throw new RuntimeException("Init PrefManager before using");
		}

		return sWrapper;
	}

	/**
	 * Returns true if android in-tray notifications enabled (with foreground play)
	 *
	 * @return true if android in-tray notifications enabled (with foreground play)
	 */
	public static boolean isForegroundEnabled() {
		return getWrapper().getBooleanValue(KEY_FOREGROUND);
	}

	/**
	 * Returns true if app was started at this device before
	 *
	 * @return true if app was started at this device before
	 */
	public static boolean isAppInitialized() {
		return getWrapper().getBooleanValue(KEY_APP_INITIALIZED);
	}

	public static boolean isIgnoreAudioFocus() {
		return getWrapper().getBooleanValue(KEY_IGNORE_AUDIO_FOCUS);
	}

	/**
	 * Returns stream quality selected by user
	 *
	 * @return stream quality selected by user
	 */
	public static PlayerManager.StreamQuality getStreamQuality() {
		int id = getWrapper().getIntValue(KEY_STREAM_ID);

		return PlayerManager.StreamQuality.values()[id];
	}

	/**
	 * Turns on/off using android in-tray notifications (with foreground play)
	 *
	 * @param value <b>true</b> to use android in-tray notifications (with foreground play)
	 */
	public static void setForegroundEnabled(boolean value) {
		getWrapper().setBooleanValue(KEY_FOREGROUND, value);
	}

	/**
	 * Sets was-started-on-this-device-before value
	 *
	 * @param value was-started-on-this-device-before
	 */
	public static void setAppInitialized(boolean value) {
		getWrapper().setBooleanValue(KEY_APP_INITIALIZED, value);
	}

	public static void setIgnoreAudioFocus(boolean value) {
		getWrapper().setBooleanValue(KEY_IGNORE_AUDIO_FOCUS, value);
	}

	/**
	 * Sets selected stream quality
	 *
	 * @param quality selected stream quality
	 */
	public static void setStreamQuality(PlayerManager.StreamQuality quality) {
		getWrapper().setIntValue(KEY_STREAM_ID, quality.ordinal());
	}


	/**
	 * Android level preferences wrapper
	 */
	private static class PrefWrapper {

		/** Preferences file name */
		private static final String PREFERENCES_NAME = "preferences";

		/** Android file-based preferences */
		private SharedPreferences mPrefs;

		/**
		 * Create new instance and open Android SharedPreferences
		 *
		 * @param context application context
		 */
		public PrefWrapper(Context context) {
			mPrefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		}

		/**
		 * Returns boolean value from SharedPreferences if stored, false if not
		 *
		 * @param key boolean preference key
		 * @return boolean value from SharedPreferences if stored, false if not
		 */
		public boolean getBooleanValue(String key) {
			return mPrefs.getBoolean(key, false);
		}

		/**
		 * Returns int value from SharedPreferences if stored, 0 if not
		 *
		 * @param key int preference key
		 * @return int value from SharedPreferences if stored, 0 if not
		 */
		public int getIntValue(String key) {
			return mPrefs.getInt(key, 0);
		}

		/**
		 * Stores boolean values with desired key
		 *
		 * @param key boolean preference key
		 * @param value new value to store
		 */
		public void setBooleanValue(String key, boolean value) {
			mPrefs.edit().putBoolean(key, value).commit();
		}

		/**
		 * Stores int values with desired key
		 *
		 * @param key int preference key
		 * @param value new value to store
		 */
		public void setIntValue(String key, int value) {
			mPrefs.edit().putInt(key, value).commit();
		}
	}
}
