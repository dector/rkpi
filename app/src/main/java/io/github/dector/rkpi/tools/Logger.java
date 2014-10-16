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

import java.util.Arrays;
import java.util.Date;

/**
 * In-app logger
 *
 * @author dector
 */
public class Logger {

	/** Wrapper instance */
	private static LogWrapper sWrapper;

	/**
	 * Log throwable object
	 *
	 * @param t throwable object
	 */
	public static void log(Throwable t) {
		getWrapper().log(t);
	}

	/**
	 * Returns stored log
	 *
	 * @return stored log
	 */
	public static String getLog() {
		return getWrapper().getLog();
	}

	/**
	 * Close wrapper
	 */
	public static void close() {
		getWrapper().close();
	}

	/**
	 * New instance
	 */
	private Logger() {}

	/**
	 * Returns wrapper instance (create it if needed)
	 *
	 * @return wrapper instance
	 */
	private static LogWrapper getWrapper() {
		if (sWrapper == null) {
			sWrapper = new LogWrapper();
		}

		return sWrapper;
	}

	/**
	 * Log wrapper
	 */
	private static class LogWrapper {

		/** Log container */
		private StringBuilder mLog;

		/**
		 * Create new instance
		 */
		public LogWrapper() {
			mLog = new StringBuilder();
		}

		/**
		 * Log throwable object
		 *
		 * @param t throwable object
		 */
		public void log(Throwable t) {
			StackTraceElement[] stackTrace = t.getStackTrace();

			mLog.append(new Date())
					.append("\n")
					.append(t)
					.append("\n")
					.append(Arrays.toString(stackTrace))
					.append("\n");
		}

		/**
		 * Returns stored log
		 *
		 * @return stored log
		 */
		public String getLog() {
			return mLog.toString();
		}

		/**
		 * Close wrapper
		 */
		public void close() {}
	}
}
