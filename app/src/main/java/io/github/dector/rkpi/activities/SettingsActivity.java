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
package io.github.dector.rkpi.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.*;

import io.github.dector.rkpi.BuildConfig;
import io.github.dector.rkpi.R;
import io.github.dector.rkpi.common.PrefManager;
import io.github.dector.rkpi.components.notifications.Request;
import io.github.dector.rkpi.components.player.PlayerManager;
import io.github.dector.rkpi.tools.FlurryClient;

import static io.github.dector.rkpi.tools.FlurryClient.Event;

/**
 * Just simple settings activity
 *
 * @author dector
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

	/**
	 * Key values for preference view identification
	 */
	private enum PreferenceKey {
		STREAM, NOTIFICATIONS, IGNORE_AUDIO_FOCUS
	}

	/**
	 * Create preference views and init them
	 *
	 * @param savedInstanceState Android stored instance
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setPreferenceScreen(createPreferenceScreen());
	}

	/**
	 * Create PreferenceScreen and init it's preference views
	 *
	 * @return
	 */
	private PreferenceScreen createPreferenceScreen() {
		Resources res = getResources();

		PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(this);

		// Stream
		String[] streamEntries = { res.getString(R.string.lowq_stream), res.getString(R.string.hq_stream) };
		String[] streamValues = { String.valueOf(PlayerManager.StreamQuality.LQ.ordinal()),
				String.valueOf(PlayerManager.StreamQuality.HQ.ordinal()) };
		PlayerManager.StreamQuality streamQuality = PrefManager.getStreamQuality();

		ListPreference streamPreference = new ListPreference(this);
		streamPreference.setTitle(R.string.preferences_stream_title);
		streamPreference.setEntries(streamEntries);
		streamPreference.setEntryValues(streamValues);
		streamPreference.setKey(PreferenceKey.STREAM.name());
		streamPreference.setOnPreferenceChangeListener(this);
		streamPreference.setValue(String.valueOf(streamQuality.ordinal()));
		updateStreamPreferenceSummary(streamPreference, streamQuality);
		screen.addPreference(streamPreference);

        if (BuildConfig.DEBUG) {
            Preference preference = new Preference(this);
            preference.setTitle(R.string.preferences_developers);
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(SettingsActivity.this, DevSettingsActivity.class));
                    return true;
                }
            });
            screen.addPreference(preference);
        }

		// Ignore audio focus
		/*CheckBoxPreference ignoreAudioFocusPreference = new CheckBoxPreference(this);
		ignoreAudioFocusPreference.setTitle(R.string.preferences_ignore_audio_focus);
		ignoreAudioFocusPreference.setChecked(PrefManager.isIgnoreAudioFocus());
		ignoreAudioFocusPreference.setKey(PreferenceKey.IGNORE_AUDIO_FOCUS.name());
		ignoreAudioFocusPreference.setOnPreferenceChangeListener(this);
		screen.addPreference(ignoreAudioFocusPreference);*/

		/*boolean foregroundEnabled = PrefManager.isForegroundEnabled();

		CheckBoxPreference foregroundPreference = new CheckBoxPreference(this);
		foregroundPreference.setTitle(R.string.preferences_foreground_title);
		foregroundPreference.setKey(PreferenceKey.NOTIFICATIONS.name());
		foregroundPreference.setOnPreferenceChangeListener(this);
		foregroundPreference.setDefaultValue(foregroundEnabled);
		foregroundPreference.setChecked(foregroundEnabled);
		screen.addPreference(foregroundPreference);*/

		return screen;
	}

	/**
	 * Called when preference value was changed
	 *
	 * @param preference Preference view, which has been changed
	 * @param newValue new preference value
	 * @return true, which calls Android to update this Preference view
	 */
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		PreferenceKey key = PreferenceKey.valueOf(preference.getKey());

		Intent i = null;

		switch (key) {
			case STREAM:
				int streamValueIndex = Integer.parseInt(newValue.toString());
				PlayerManager.StreamQuality streamQuality
						= PlayerManager.StreamQuality.values()[streamValueIndex];
				PrefManager.setStreamQuality(streamQuality);

				boolean hq = streamQuality == PlayerManager.StreamQuality.HQ;

				i = new Intent((hq)
						? Request.SET_HQ_STREAM.encode()
						: Request.SET_LQ_STREAM.encode());

				Event.STREAM_SELECTED.builder()
						.param(Event.KEY_WHERE, Event.VALUE_WHERE_SETTINGS)
						.param(Event.KEY_WHAT, (hq) ? Event.VALUE_WHAT_HQ : Event.VALUE_WHAT_LQ)
						.log();

				updateStreamPreferenceSummary(preference, streamQuality);
				break;
			case NOTIFICATIONS:
				boolean notificationsEnabled = Boolean.parseBoolean(newValue.toString());

				i = new Intent(Request.TOGGLE_FOREGROUND.encode());

				PrefManager.setForegroundEnabled(notificationsEnabled);
				break;
			case IGNORE_AUDIO_FOCUS:
				boolean ignoreAudioFocus = Boolean.parseBoolean(newValue.toString());
				PrefManager.setIgnoreAudioFocus(ignoreAudioFocus);
				break;
			default:
				break;
		}

		if (i != null) {
			sendBroadcast(i);
		}

		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		FlurryClient.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryClient.onStop(this);
	}

	/**
	 * Set summary to stream preference based on selected stream quality
	 *
	 * @param preference stream preference to be updated with summary
	 * @param streamQuality selected stream quality
	 */
	private void updateStreamPreferenceSummary(Preference preference, PlayerManager.StreamQuality streamQuality) {
		int streamSummary = (streamQuality == PlayerManager.StreamQuality.HQ) ? R.string.hq_stream : R.string.lowq_stream;
		preference.setSummary(streamSummary);
	}
}
