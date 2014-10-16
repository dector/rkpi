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
package io.github.dector.rkpi.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import io.github.dector.rkpi.common.PrefManager;
import io.github.dector.rkpi.components.notifications.NotificationManager;
import io.github.dector.rkpi.components.notifications.Request;
import io.github.dector.rkpi.components.notifications.RequestObserver;
import io.github.dector.rkpi.components.notifications.RequestReceiver;
import io.github.dector.rkpi.components.player.PlayerManager;
import io.github.dector.rkpi.components.player.PlayerStateObserver;
import io.github.dector.rkpi.components.player.StateManager;

/**
 * @author dector
 */
public class ApplicationService extends Service implements RequestObserver {

    private boolean inited;

	private Binder mBinder;

    /** Observable player state container */
    private StateManager mStateManager;

    /** Manager to work with Android notifications */
    private NotificationManager mNotificationManager;

	private PlayerManager mPlayerManager;

    /** Broadcast receiver to handle commands from notifications */
    private RequestReceiver mReceiver;

	public ApplicationService() {
		mBinder = new Binder();
	}

    public void init(RequestObserver requestObserver, PlayerStateObserver stateObserver) {
        if (! inited) {
            mReceiver = new RequestReceiver(this);
            mReceiver.addObserver(this);

            mNotificationManager = new NotificationManager(this);

            mStateManager = new StateManager();
            mStateManager.addObserver(mNotificationManager);
            mStateManager.forceNotify();

	        mPlayerManager = new PlayerManager(this, mStateManager);

            if (PrefManager.isForegroundEnabled()) {
                startForeground();
            }

            inited = true;
        }

        mReceiver.addObserver(requestObserver);
        mStateManager.addObserver(stateObserver);
        mStateManager.forceNotify();
    }

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	public boolean isStoppable() {
		return ! mPlayerManager.isPlaying();
	}

	public void destroy() {
        stopSelf();
	}

    @Override
    public void onDestroy() {
        mStateManager.clearObservers();

	    mPlayerManager.onDestroy();
        mNotificationManager.destroy();

        mReceiver.clearObservers();
        mReceiver.unregisterAndRelease();

        super.onDestroy();
    }

    @Override
	public void onRequestPerformed(Request request) {
        switch (request) {
            case TOGGLE_FOREGROUND:
                if (PrefManager.isForegroundEnabled()) {
                    startForeground();
                } else {
                    mNotificationManager.hide();
                    stopForeground(true);
                }
                break;
            case EXIT:
                destroy();
                break;
        }

	    mPlayerManager.onRequestPerformed(request);
	}

    private void startForeground() {
        mNotificationManager.show();

        startForeground(1, mNotificationManager.getNotification());
    }

    public class Binder extends android.os.Binder {
		public ApplicationService getService() {
			return ApplicationService.this;
		}
	}
}
