/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 dector9@gmail.com
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

import android.app.AlertDialog;
import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import io.github.dector.rkpi.R;
import io.github.dector.rkpi.common.PrefManager;
import io.github.dector.rkpi.components.notifications.Request;
import io.github.dector.rkpi.components.notifications.RequestObserver;
import io.github.dector.rkpi.components.player.PlayerManager;
import io.github.dector.rkpi.services.ApplicationService;
import io.github.dector.rkpi.tools.Logger;
import io.github.dector.rkpi.layouts.MainLayout;
import io.github.dector.rkpi.views.AppViewPager;

import static io.github.dector.rkpi.tools.FlurryClient.Event;

/**
 * Start activity. Contains main page layouts
 *
 * @author dector
 */
public class MainActivity extends RkpiActivity implements RequestObserver {

	private static final String KEY_STATE_PAGE_OPENED = "KEY_STATE_PAGE_OPENED";

	/** Root layout container */
	private AppViewPager mViewPager;
	/** Custom ViewPager adapter. Stores layouts */
	private AppViewPager.AppAdapter mPagerAdapter;

    private static ServiceConnection sConnection;
    private ApplicationService mService;

	/**
	 * Create activity and init application components and resources
	 *
	 * @param savedInstanceState Android stored instance
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setOrientation();
		createUI();
		initService();
		testFirstrun();
	}

	private void setOrientation() {
		if (getRkpiApplication().isTablet()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	private void createUI() {
		mViewPager = new AppViewPager(this);
		mPagerAdapter = mViewPager.createAndSetAdapter(this);
		setContentView(mViewPager);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Event.APP_ORIENTATION.builder()
					.param(Event.KEY_WHAT, Event.VALUE_WHAT_LANDSCAPE).log();
			setOrientation(Configuration.ORIENTATION_LANDSCAPE);
		} else {
			Event.APP_ORIENTATION.builder()
					.param(Event.KEY_WHAT, Event.VALUE_WHAT_PORTRAIT).log();
			setOrientation(Configuration.ORIENTATION_PORTRAIT);
		}

		mPagerAdapter.getPlayButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Event.TOGGLE_PRESSED.builder()
						.param(Event.KEY_WHERE, Event.VALUE_WHERE_APP).log();
				mService.onRequestPerformed(Request.PLAYER_TOGGLE);
			}
		});
		mPagerAdapter.getInfoButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Event.ABOUT_PRESSED.builder().log();
				mViewPager.switchPages();
			}
		});
		mPagerAdapter.getSettingsButton().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Event.SETTINGS_PRESSED.builder().log();
				startActivity(new Intent(MainActivity.this, SettingsActivity.class));
			}
		});
	}

	private void initService() {
		sConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = ((ApplicationService.Binder) service).getService();
				mService.init(MainActivity.this, mPagerAdapter);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
		};
		startService(new Intent(MainActivity.this, ApplicationService.class));
		bindService(new Intent(MainActivity.this, ApplicationService.class),
				sConnection, Context.BIND_AUTO_CREATE);
	}

	private void testFirstrun() {
		if (! PrefManager.isAppInitialized()) {
			PrefManager.setForegroundEnabled(true);
			PrefManager.setIgnoreAudioFocus(false);

			DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							Event.STREAM_SELECTED.builder()
									.param(Event.KEY_WHERE, Event.VALUE_WHERE_INIT)
									.param(Event.KEY_WHAT, Event.VALUE_WHAT_HQ).log();
							PrefManager.setStreamQuality(PlayerManager.StreamQuality.HQ);
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							Event.STREAM_SELECTED.builder()
									.param(Event.KEY_WHERE, Event.VALUE_WHERE_INIT)
									.param(Event.KEY_WHAT, Event.VALUE_WHAT_LQ).log();
							PrefManager.setStreamQuality(PlayerManager.StreamQuality.LOW);
							break;
						default:
							break;
					}

					Event.INIT_APP.builder().log();
					PrefManager.setAppInitialized(true);
				}
			};

			new AlertDialog.Builder(this)
					.setTitle(R.string.select_stream)
					.setMessage(R.string.select_stream_msg)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton(R.string.hq_stream, listener)
					.setNegativeButton(R.string.lowq_stream, listener)
					.create()
					.show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_STATE_PAGE_OPENED, mViewPager.getCurrentItem());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mViewPager.setCurrentItem(savedInstanceState.getInt(KEY_STATE_PAGE_OPENED, AppViewPager.DEFAULT_PAGE));
	}

	/**
	 * Determines custom DesignOrientation value based on device orientation
	 * and updates layouts
	 *
	 * @param configOrientation Configuration.ORIENTATION_* constant
	 */
	private void setOrientation(int configOrientation) {
		MainLayout.DesignOrientation orientation;

		boolean isTablet = getRkpiApplication().isTablet();

		if (configOrientation == Configuration.ORIENTATION_PORTRAIT) {
			orientation = (isTablet) ? MainLayout.DesignOrientation.PORTRAIT_TABLET : MainLayout.DesignOrientation.PORTRAIT_PHONE;
		} else if (configOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			orientation = (isTablet) ? MainLayout.DesignOrientation.LANDSCAPE_TABLET : MainLayout.DesignOrientation.PORTRAIT_PHONE;
		} else {
			orientation = MainLayout.DesignOrientation.PORTRAIT_PHONE;
		}

		mViewPager.setOrientation(orientation);
	}

	/**
	 * Broadcast request executed
	 *
	 * @param request action request to application
	 */
	@Override
	public void onRequestPerformed(Request request) {
        if (request == Request.EXIT) {
	        Event.APP_CLOSED.builder().param(Event.KEY_WHERE, Event.VALUE_WHERE_TRAY).log();
	        // TODO remake it
	        exitApp();
        }
	}

	/** Flag to indicate, that app has been finished by-hand
	 * and destroy() phase for components has been executed */
	private boolean mStopped;

    private boolean mStopService;

	/**
	 * Prepare application for exiting.
	 * Stop and destroy() all app components
	 *
	 * @param force if <b>true</b>, application will be prepared for finishing
	 *              even if player is playing, else player will be stopped too
	 */
	public void stopApplication(boolean force) {
		if (mStopped) { return; }

		Logger.close();

        unbindService(sConnection);

        if (force || (mService != null && ! mService.isStoppable())) {
            mStopService = true;
        }

		mStopped = true;
	}

	/**
	 * Overriden Android Activity method to force destroy app
	 * components and release resources first <b>(forced)</b>
	 */
	public void exitApp() {
		stopApplication(true);
		finish();
	}

	/**
	 * Trying to destroy app components and release resources
	 * before activity destroying <b>(not forced)</b>
	 */
	@Override
	protected void onDestroy() {
		stopApplication(false);

        if (mStopService) {
            mService.destroy();
            mService = null;
            mStopService = false;
        }

		super.onDestroy();
	}
}