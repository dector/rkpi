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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import io.github.dector.rkpi.R;
import io.github.dector.rkpi.activities.MainActivity;
import io.github.dector.rkpi.common.PrefManager;
import io.github.dector.rkpi.components.player.PlayerStateObserver;
import io.github.dector.rkpi.components.player.PlayerState;
import io.github.dector.rkpi.tools.FlurryClient;

import static io.github.dector.rkpi.tools.FlurryClient.Event;

/**
 * Android tray notifications wrapper
 *
 * @author dector
 */
public class NotificationManager implements PlayerStateObserver {

	/** App-wide main notification id */
	private static final int NOTIFICATION_ID = 1;

	/** Stored application context */
	private Context mContext;

	/** Android system notification manager */
	private android.app.NotificationManager mManager;
	/** Notification builder. Used to update and build notification */
	private NotificationCompat.Builder mNotificationBuilder;
	/** Root notification view */
	private RemoteViews mRemoteViews;

	/**
	 * Create new instance
	 *
	 * @param context application context
	 */
	public NotificationManager(Context context) {
		mContext = context;

		mManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.icon_bar;

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

		PendingIntent playPauseIntent = PendingIntent.getBroadcast(context, 0,
				new Intent(Request.PLAYER_TOGGLE.encode()),
				PendingIntent.FLAG_UPDATE_CURRENT);

		PendingIntent exitIntent = PendingIntent.getBroadcast(context, 0,
				new Intent(Request.EXIT.encode()),
				PendingIntent.FLAG_CANCEL_CURRENT);

		mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
		mRemoteViews.setOnClickPendingIntent(R.id.toggleButton, playPauseIntent);
		mRemoteViews.setOnClickPendingIntent(R.id.closeButton, exitIntent);

		mNotificationBuilder = new NotificationCompat.Builder(context)
				.setSmallIcon(icon)
				.setContent(mRemoteViews)
				.setContentIntent(contentIntent)
				.setOngoing(true);
	}

	/**
	 * Toggle enabled (show/hide notification)
	 */
	public void toggle() {
		if (PrefManager.isForegroundEnabled()) {
			show();
		} else {
			hide();
		}
	}

    public void show() {
        if (mText != null) {
            sendNotification(mText);
        }
    }

	/**
	 * Hide notification and release context
	 */
	public void destroy() {
		hide();
		mContext = null;
	}

	/**
	 * Delete notification from tray
	 */
	public void hide() {
		mManager.cancel(NOTIFICATION_ID);
	}

	/**
	 * Called when player changed it's state. Updates notification view
	 *
	 * @param newState current player state
	 * @param data stores additional data (song info for example)
	 *
	 * @see PlayerStateObserver
	 */
	@Override
	public void onStateChanged(PlayerState newState, Object data) {
		switch (newState) {
			case STOPPED:
				Event.PLAYING.builder().finished().log();
				mRemoteViews.setImageViewResource(R.id.toggleButton, R.drawable.play_bar);
				sendNotification(R.string.stopped);
				break;
			case ERROR:
				Event.PLAYING.builder().finished().log();
				mRemoteViews.setImageViewResource(R.id.toggleButton, R.drawable.play_bar);
				sendNotification(R.string.connection_error);
				break;
			case PLAYING:
				Event.PLAYING.builder().log();
				mRemoteViews.setImageViewResource(R.id.toggleButton, R.drawable.pause_bar);
				if (data != null) {
					sendNotification(data.toString());
				} else {
					sendNotification(R.string.loading);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Show/update notification with custom string resource
	 *
	 * @param stringId string resource id
	 */
	private void sendNotification(int stringId) {
		sendNotification(mContext.getResources().getString(stringId));
	}

	/** Last notification text. Stored to show it after notification was hidden */
	private String mText;

	private void sendNotification(String text) {
		mText = text;
		mRemoteViews.setTextViewText(R.id.trackName, text);

		if (PrefManager.isForegroundEnabled()) {
			mManager.notify(NOTIFICATION_ID, getNotification());
		}
	}

    public Notification getNotification() {
        return mNotificationBuilder.build();
    }
}
