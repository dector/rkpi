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
package io.github.dector.rkpi.components.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom observable BroadcastReceiver to handle Requests
 *
 * @author dector
 *
 * @see Request
 * @see RequestObserver
 */
public class RequestReceiver extends BroadcastReceiver {

	/** Stored application context */
	private Context mContext;

	/** List of observers */
	private List<RequestObserver> mObservers;

	/**
	 * Create new instance and register receiver
	 *
	 * @param context application context
	 */
	public RequestReceiver(Context context) {
		mContext = context;

		mObservers = new ArrayList<RequestObserver>();

		IntentFilter intentFilter = new IntentFilter();
        for (Request request : Request.values()) {
            intentFilter.addAction(request.encode());
        }
		/*intentFilter.addAction(Request.PLAYER_TOGGLE.encode());
		intentFilter.addAction(Request.TOGGLE_FOREGROUND.encode());
		intentFilter.addAction(Request.SET_HQ_STREAM.encode());
		intentFilter.addAction(Request.SET_LQ_STREAM.encode());
		intentFilter.addAction(Request.SET_LQ_STREAM.encode());
		intentFilter.addAction(Request.EXIT.encode());*/
		context.registerReceiver(this, intentFilter);
	}

	/**
	 * Prepare receiver to be destroyed: unregister receiver
	 * and release stored context
	 */
	public void unregisterAndRelease() {
		mContext.unregisterReceiver(this);
		mContext = null;
	}

	/**
	 * Subscribe new observer
	 *
	 * @param observer observer to be subscribed
	 */
	public void addObserver(RequestObserver observer) {
		mObservers.add(observer);
	}

	/**
	 * Unsubscribe new observer
	 *
	 * @param observer observer to be unsubscribed
	 */
	public void removeObserver(RequestObserver observer) {
		mObservers.remove(observer);
	}

	/**
	 * Ubsubscribe all observers
	 */
	public void clearObservers() {
		mObservers.clear();
	}

	/**
	 * BroadcastReceiver's method. Called when Android broadcast intent received.
	 * Decodes Request and send it to all observers
	 *
	 * @param context The Context in which the receiver is running.
	 * @param intent The Intent being received.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Request request = Request.decode(action);

		for (RequestObserver observer : mObservers) {
			observer.onRequestPerformed(request);
		}
	}
}
