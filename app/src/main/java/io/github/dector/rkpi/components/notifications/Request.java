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

/**
 * Set of allowed broadcast requests
 *
 * @author dector
 *
 * @see #encode()
 * @see #decode(String)
 */
public enum Request {
    PLAYER_PLAY, PLAYER_PAUSE, PLAYER_TOGGLE,   // Player control
    TOGGLE_FOREGROUND,					        // Notifications showing toggle
	TOGGLE_HQ, SET_LQ_STREAM, SET_HQ_STREAM,    // Manage used stream
	EXIT;									    // Close application

	/** Broadcast request prefix */
	private static final String PREFIX = "io.github.dector.rkpi.";

	/**
	 * Returns name to use as Intent action
	 *
	 * @return action name
	 */
	public String encode() {
		return PREFIX + this.name();
	}

	/**
	 * Returns Request instance decoded from received action name
	 *
	 * @param value received action name
	 * @return Request instance
	 */
	public static Request decode(String value) {
		return valueOf(value.replaceFirst(PREFIX, ""));
	}
}