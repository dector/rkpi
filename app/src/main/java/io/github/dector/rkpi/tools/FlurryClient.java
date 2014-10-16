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

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import io.github.dector.rkpi.BuildConfig;

/**
 * @author dector
 */
public class FlurryClient {

	public static class EventBuilder {

		private Event mEvent;
		private boolean mFinished;
		private Map<String, String> mParams;

		public EventBuilder(Event event) {
			mEvent = event;
			mParams = new HashMap<String, String>();
		}

		public EventBuilder finished() {
			mFinished = true;

			return this;
		}

		public EventBuilder param(String key, String value) {
			if (mParams == null) {
				mParams = new HashMap<String, String>();
			}

			mParams.put(key, value);

			return this;
		}

		public void log() {
			if (mFinished) {
				FlurryClient.logEnd(mEvent);
			} else {
				FlurryClient.log(mEvent, mParams);
			}
		}
	}

	public static enum Event {
		TOGGLE_PRESSED("Toggle pressed"),
		PLAYING("Playing"),
		ABOUT_PRESSED("About pressed"),
		SETTINGS_PRESSED("Settings pressed"),
		PAGE_OPENED("Page opened"),
		LINK_CLICKED("Link clicked"),
		STREAM_SELECTED("Stream selected"),
		APP_CLOSED("App closed"),
		APP_LAUNCHED("App launched"),
		INIT_APP("Init app"),
		APP_ORIENTATION("App orientation");

		public static final String KEY_WHERE            = "Where";
		public static final String KEY_WHAT             = "What";
		public static final String KEY_TYPE             = "Type";

		public static final String VALUE_WHERE_APP      = "App";
		public static final String VALUE_WHERE_TRAY     = "Tray";

		public static final String VALUE_TYPE_MAIN      = "Main";
		public static final String VALUE_TYPE_ABOUT     = "About";

		public static final String VALUE_WHERE_INIT     = "Init";
		public static final String VALUE_WHERE_SETTINGS = "Settings";

		public static final String VALUE_WHAT_HQ        = "HQ";
		public static final String VALUE_WHAT_LQ        = "LQ";

		public static final String VALUE_WHAT_LANDSCAPE = "Landscape";
		public static final String VALUE_WHAT_PORTRAIT  = "Portrait";

		public final String id;

		Event(String id) {
			this.id = id;
		}

		public EventBuilder builder() {
			return new EventBuilder(this);
		}
	}

	public static void onStart(Context context) {
        FlurryAgent.setVersionName(AppUtils.getAppVersionName(context));
        FlurryAgent.setLogEnabled(BuildConfig.DEBUG);
        FlurryAgent.setLogEvents(BuildConfig.DEBUG);

		FlurryAgent.onStartSession(context, BuildConfig.FLURRY_APP_KEY);
	}

	public static void onStop(Context context) {
		FlurryAgent.onEndSession(context);
	}

	public static void log(Event event, Map<String, String> params) {
		FlurryAgent.logEvent(event.id, params);
	}

	public static void logEnd(Event event) {
		FlurryAgent.endTimedEvent(event.id);
	}
}
