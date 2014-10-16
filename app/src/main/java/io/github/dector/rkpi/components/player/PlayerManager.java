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
package io.github.dector.rkpi.components.player;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.spoledge.aacdecoder.MP3Player;
import com.spoledge.aacdecoder.PlayerCallback;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLDecoder;

import io.github.dector.rkpi.R;
import io.github.dector.rkpi.common.PrefManager;
import io.github.dector.rkpi.components.notifications.Request;
import io.github.dector.rkpi.components.notifications.RequestObserver;
import io.github.dector.rkpi.tools.FlurryClient;
import io.github.dector.rkpi.tools.Logger;

/**
 * @author dector
 */
public class PlayerManager implements RequestObserver, PlayerCallback {

	private class OuterEventsListener implements AudioManager.OnAudioFocusChangeListener {

		private boolean mPaused;

		@Override
		public void onAudioFocusChange(int focusChange) {
			if (PrefManager.isIgnoreAudioFocus())
				return;

			switch (focusChange) {
				case AudioManager.AUDIOFOCUS_GAIN:
					if (mPaused) {
						mPaused = false;
						internalPlay();
					}
					break;
				case AudioManager.AUDIOFOCUS_LOSS:
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
					if (isPlaying()) {
						mPaused = true;
						internalStop();
					}
					break;
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
					// Fuck off :)
					break;
			}
		}
	}

	private Context mContext;
	/** Wrapped stream player */
	private MP3Player mPlayer;
	/** Selected stream quality */
	private StreamQuality mStreamQuality;
	/** Linked state manager */
	private StateManager mStateManager;

	private AudioManager mAudioManager;
	private OuterEventsListener mOuterEventsListener;

	private boolean mRestartPlay;

	public PlayerManager(Context context, StateManager stateManager) {
		mContext = context;
		mStateManager = stateManager;

		mPlayer = new MP3Player(this);
		mStreamQuality = PrefManager.getStreamQuality();

		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mOuterEventsListener = new OuterEventsListener();
	}

	/**
	 * Returns true if is playing now
	 *
	 * @return true if is playing now
	 */
	public boolean isPlaying() {
		return mStateManager.isPlaying();
	}

	// FOR OUTER USE ONLY
	public void play() {
		int focusRequestResult = mAudioManager.requestAudioFocus(mOuterEventsListener, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);

		if (PrefManager.isIgnoreAudioFocus()
				|| focusRequestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			internalPlay();
		} else {
			error(Error.AUDIO_FOCUS_ERROR);
		}
	}

	private void internalPlay() {
		mPlayer.playAsync(mStreamQuality.uri);
	}

	// FOR OUTER USE ONLY
	public void stop() {
		mAudioManager.abandonAudioFocus(mOuterEventsListener);
		internalStop();
	}

	private void internalStop() {
		mPlayer.stop();
	}

	public void onDestroy() {
		stop();
	}

	/**
	 * Called when wrapped player started playing
	 */
	@Override
	public void playerStarted() {
		mStateManager.setState(PlayerState.PLAYING);
	}

	/**
	 * Some PCM buffer state, disinterested with stream
	 *
	 * @param b
	 * @param i
	 * @param i2
	 */
	@Override
	public void playerPCMFeedBuffer(boolean b, int i, int i2) {}

	/**
	 * Wrapped player stopped playing
	 *
	 * @param i don't know :)
	 */
	@Override
	public void playerStopped(int i) {
		if (mStateManager.getState() == PlayerState.PLAYING) {
			internalStop();
		}
		mStateManager.setState(PlayerState.STOPPED);

		if (mRestartPlay) {
			internalPlay();

			mRestartPlay = false;
		}
	}

	/**
	 * Wrapped player exception occurred
	 *
	 * @param throwable throwable exception instance
	 */
	@Override
	public void playerException(Throwable throwable) {
		if (throwable instanceof ConnectException) {
			error(Error.CONNECTION_ERROR);
		} else {
			error(Error.ANY_ERROR);
		}

		Logger.log(throwable);
	}

	/**
	 * Error has been occurred
	 *
	 * @param error error info, may be null
	 */
	private void error(Error error) {
		stop();

		mStateManager.setState(PlayerState.ERROR, error);
	}

	/**
	 * Wrapped player metadata changed: {key -> value} pairs.
	 * Cyrillic symbols are not decoded correct, so I parse
	 * metadata from "StreamUrl"
	 *
	 * For example:
	 * &artist=James%20Brown&title=I%20Got%20You%20%28I%20Feel%20Good%29
	 *
	 * @param key metadata key
	 * @param value metadata value
	 */
	@Override
	public void playerMetadata(String key, String value) {
		if (key != null && key.equals("StreamUrl")) {
			SongInfo songInfo = new SongInfo();

			String[] urlElements = value.split("&");
			for (String s : urlElements) {
				if (s == null) continue;

				String[] parts = s.split("=");
				if (parts.length >= 2) {
					if (parts[0].equals("artist")) {
						try {
							songInfo.setArtist(URLDecoder.decode(parts[1], "Windows-1251"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					} else if (parts[0].equals("title")) {
						try {
							songInfo.setTitle(URLDecoder.decode(parts[1], "Windows-1251"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				}
			}

			if (! songInfo.isEmpty()) {
//				Log.d("Player", "Song changed --> " + songInfo);
				mStateManager.setState(PlayerState.PLAYING, songInfo);
			}
		}
	}

	/**
	 * Called when broadcast request received
	 *
	 * @param request received request
	 */
	@Override
	public void onRequestPerformed(Request request) {
		switch (request) {
			case PLAYER_TOGGLE:
				FlurryClient.Event.TOGGLE_PRESSED.builder()
						.param(FlurryClient.Event.KEY_WHERE, FlurryClient.Event.VALUE_WHERE_TRAY).log();
				togglePlayer();
				break;
			case TOGGLE_HQ:
				toggleHQ();
				break;
			case SET_HQ_STREAM:
				setStreamQuality(StreamQuality.HQ);
				break;
			case SET_LQ_STREAM:
				setStreamQuality(StreamQuality.LOW);
				break;
			case PLAYER_PAUSE:
				stop();
				break;
			case PLAYER_PLAY:
				play();
				break;
			default:
				Log.w("TODO Action", request.name() + " request not implemented");
				break;
		}
	}

	/**
	 * Change playing stream
	 *
	 * @param streamQuality selected stream
	 */
	private void setStreamQuality(StreamQuality streamQuality) {
		mStreamQuality = streamQuality;

		mRestartPlay = true;
		internalStop();
	}

	/**
	 * Toggle stream quality
	 */
	private void toggleHQ() {
		if (mStreamQuality == StreamQuality.LOW) {
			setStreamQuality(StreamQuality.HQ);
		} else if (mStreamQuality == StreamQuality.HQ) {
			setStreamQuality(StreamQuality.LOW);
		}
	}

	/**
	 * Toggle player state
	 */
	private void togglePlayer() {
		switch (mStateManager.getState()) {
			case STOPPED:
			case ERROR:
				play();
				break;
			case PLAYING:
				stop();
				break;
		}
	}

	/**
	 * Stream quality
	 */
	public static enum StreamQuality {
		LOW("http://77.47.130.190:8000/64kbps"),
		HQ("http://77.47.130.190:8000/radiokpi");

		public final String uri;

		private StreamQuality(String uri) {
			this.uri = uri;
		}

		public static StreamQuality getDefault() {
			return HQ;
		}
	}

	/**
	 * Playing errors
	 */
	public static enum Error {
		ANY_ERROR(R.string.any_error),
		CONNECTION_ERROR(R.string.connection_error),
		AUDIO_FOCUS_ERROR(R.string.audio_focus_busy);

		public final int messageId;

		private Error(int messageId) {
			this.messageId = messageId;
		}
	}
}
