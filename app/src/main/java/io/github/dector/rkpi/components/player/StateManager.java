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

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Observable player state manager
 *
 * @author dector
 *
 * @see PlayerState
 * @see PlayerStateObserver
 */
public class StateManager {

	/** Current player state */
	private PlayerState mState;
	/** Additional data (song info for example) */
	private Object mData;

	/** Subscribed observers */
	private List<PlayerStateObserver> mObservers;

	/**
	 * Create new instance
	 */
	public StateManager() {
		mState = PlayerState.STOPPED;

		mObservers = new ArrayList<PlayerStateObserver>();
	}

	/**
	 * Subscribe new observer
	 *
	 * @param observer observer to be subscribed
	 */
	public void addObserver(PlayerStateObserver observer) {
		mObservers.add(observer);
	}

	/**
	 * Unsubscribe new observer
	 *
	 * @param observer observer to be ubsubscribed
	 */
	public void removeObserver(PlayerStateObserver observer) {
		mObservers.remove(observer);
	}

	/**
	 * Unsubscribe all observers
	 */
	public void clearObservers() {
		mObservers.clear();
	}

	/**
	 * Notify all observers immediately
	 */
	public void forceNotify() {
		notifyObservers();
	}

	/**
	 * Update player's state without additional data and notify observers
	 *
	 * @param newState current player state
	 */
	public void setState(PlayerState newState) {
		setState(newState, null);
	}

	/**
	 * Update player's state with additional data and notify observers
	 *
	 * @param newState current player state
	 * @param data additional data (for example song info)
	 */
	public void setState(PlayerState newState, Object data) {
		mState = newState;
		mData = data;

		Log.d("State metadata", "State changed to " + newState + " [" + data + "]");

		notifyObservers();
	}

	/**
	 * Returns current player state
	 *
	 * @return current player state
	 */
	public PlayerState getState() {
		return mState;
	}

    public boolean isPlaying() {
        return getState() == PlayerState.PLAYING;
    }

	/**
	 * Send stored state and data to all observers and clear stored data after
	 */
	private void notifyObservers() {
		for (PlayerStateObserver observer : mObservers) {
			observer.onStateChanged(mState, mData);
		}

//		mData = null;
	}
}
