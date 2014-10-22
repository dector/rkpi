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

import android.content.*;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;

import eu.inmite.android.lib.dialogs.ISimpleDialogCancelListener;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;
import io.github.dector.rkpi.R;
import io.github.dector.rkpi.common.PrefManager;
import io.github.dector.rkpi.components.notifications.Request;
import io.github.dector.rkpi.components.notifications.RequestObserver;
import io.github.dector.rkpi.components.player.PlayerManager;
import io.github.dector.rkpi.services.ApplicationService;
import io.github.dector.rkpi.tools.AppUtils;
import io.github.dector.rkpi.tools.Logger;
import io.github.dector.rkpi.layouts.MainLayout;
import io.github.dector.rkpi.tools.Toaster;
import io.github.dector.rkpi.views.AppViewPager;

import static io.github.dector.rkpi.tools.FlurryClient.Event;

/**
 * Start activity. Contains main page layouts
 *
 * @author dector
 */
public class MainActivity extends RkpiActivity implements RequestObserver, ISimpleDialogListener, ISimpleDialogCancelListener {

	private static final String KEY_STATE_PAGE_OPENED = "KEY_STATE_PAGE_OPENED";

    private enum RequestCode { STREAM_QUALITY }

	/** Root layout container */
	private AppViewPager mViewPager;
	/** Custom ViewPager adapter. Stores layouts */
	private AppViewPager.AppAdapter mPagerAdapter;

    private static ServiceConnection sConnection;
    private ApplicationService mService;

    private boolean mServiceBound;

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

        mPagerAdapter.getTrackNameView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text = mPagerAdapter.getTrackNameView().getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    AppUtils.addToClipboard(MainActivity.this, text);
                    Toaster.getInstance().send(getString(R.string.track_name_copied));
                }

                return true;
            }
        });
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
                mServiceBound = true;
				mService.init(MainActivity.this, mPagerAdapter);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
                mServiceBound = false;
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

            SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
                    .setTitle(R.string.select_stream)
                    .setMessage(R.string.select_stream_msg)
                    .setRequestCode(RequestCode.STREAM_QUALITY.ordinal())
                    .setPositiveButtonText(R.string.hq_stream)
                    .setNegativeButtonText(R.string.lowq_stream)
                    .show();

            PrefManager.setAppInitialized(true);
		}
	}

    @Override
    public void onPositiveButtonClicked(int requestCode) {
        if (requestCode == RequestCode.STREAM_QUALITY.ordinal()) {
            streamSelected(PlayerManager.StreamQuality.HQ);
        }

        trackAppInit();
    }

    @Override
    public void onNegativeButtonClicked(int requestCode) {
        if (requestCode == RequestCode.STREAM_QUALITY.ordinal()) {
            streamSelected(PlayerManager.StreamQuality.LQ);
        }

        trackAppInit();
    }

    @Override
    public void onNeutralButtonClicked(int requestCode) {
    }

    @Override
    public void onCancelled(int requestCode) {
        if (requestCode == RequestCode.STREAM_QUALITY.ordinal()) {
            final PlayerManager.StreamQuality streamQuality = PlayerManager.StreamQuality.getDefault();
            PrefManager.setStreamQuality(streamQuality);

            String streamName = null;
            switch (streamQuality) {
                case HQ:
                    streamName = getString(R.string.hq_stream);
                    break;
                case LQ:
                    streamName = getString(R.string.lowq_stream);
                    break;
            }

            if (streamName != null) {
                Toaster.getInstance().send(getString(R.string.stream_selected, streamName));
            } else {
                Logger.log(new Throwable("Undefined stream quality: " + streamQuality));
            }
        }
    }

    private void streamSelected(PlayerManager.StreamQuality streamQuality) {
        final String what = (streamQuality == PlayerManager.StreamQuality.HQ) ? Event.VALUE_WHAT_HQ
                : (streamQuality == PlayerManager.StreamQuality.LQ) ? Event.VALUE_WHAT_LQ
                : "";

        Event.STREAM_SELECTED.builder()
                .param(Event.KEY_WHERE, Event.VALUE_WHERE_INIT)
                .param(Event.KEY_WHAT, what).log();
        PrefManager.setStreamQuality(streamQuality);
    }

    private void trackAppInit() {
        Event.INIT_APP.builder().log();
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

        if (mServiceBound) {
            unbindService(sConnection);
        }

        if (force || (mService != null && mService.isStoppable())) {
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