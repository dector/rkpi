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
package io.github.dector.rkpi;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import io.github.dector.rkpi.common.PrefManager;
import io.github.dector.rkpi.common.ResManager;
import io.github.dector.rkpi.tools.DeviceTools;
import io.github.dector.rkpi.tools.Toaster;

import static io.github.dector.rkpi.tools.FlurryClient.Event;

@ReportsCrashes(
		formKey = "",
		formUri = BuildConfig.ACRA_FORM_URI,
		reportType = org.acra.sender.HttpSender.Type.JSON,
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
		formUriBasicAuthLogin= BuildConfig.ACRA_LOGIN,
		formUriBasicAuthPassword= BuildConfig.ACRA_PASSWORD
)

/**
 * @author dector
 */
public class RkpiApplication extends Application {

	private boolean mIsTablet;

	@Override
	public void onCreate() {
		super.onCreate();

		ACRA.init(this);

		PrefManager.init(this);
		ResManager.init(this);
        Toaster.init(this);

		mIsTablet = DeviceTools.isTablet(this);

		Event.APP_LAUNCHED.builder().log();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Event.APP_LAUNCHED.builder().finished().log();
	}

	public boolean isTablet() {
		return mIsTablet;
	}
}
