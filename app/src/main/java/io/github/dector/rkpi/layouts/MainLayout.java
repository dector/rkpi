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
package io.github.dector.rkpi.layouts;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.dector.rkpi.R;
import io.github.dector.rkpi.components.player.PlayerManager;
import io.github.dector.rkpi.components.player.PlayerStateObserver;
import io.github.dector.rkpi.common.ResManager;
import io.github.dector.rkpi.components.player.PlayerState;
import io.github.dector.rkpi.components.player.SongInfo;
import io.github.dector.rkpi.views.AbstractScalableLayout;
import io.github.dector.rkpi.views.MarqueeTextView;

/**
 * Layout that describes main application UI
 *
 * @author dector
 */
public final class MainLayout extends AbstractScalableLayout implements PlayerStateObserver {

	/** Info button */
	private final Button mInfoButton;
	/** Play button */
	private final Button mPlayButton;

	/** Track name label */
	private final TextView mTrackNameView;

	/** Speaker image */
	private final ImageView mSpeakerImage;
	/** Radio logo image */
	private final ImageView mLogoImage;

	/** Android handler */
	private final Handler mHandler;

	/** True if was track name already displayed after playing started */
	private boolean mTrackNameDisplayed;

	/**
	 * Create new instance
	 *
	 * @param context activity context
	 */
	public MainLayout(Context context) {
		super(context);

		mLayoutWidth = 1;
		mLayoutHeight = 1;

		mSpeakerImage = new ImageView(context);
		mSpeakerImage.setImageResource(R.drawable.sound);
		addView(mSpeakerImage, new LayoutParams(0, 0, 0, 0));

		mLogoImage = new ImageView(context);
		mLogoImage.setImageResource(R.drawable.logo);
		addView(mLogoImage, new LayoutParams(0, 0, 0, 0));

		mInfoButton = new Button(context);
		mInfoButton.setBackgroundResource(R.drawable.info);
		addView(mInfoButton, new LayoutParams(0, 0, 0, 0));

		mPlayButton = new Button(context);
		mPlayButton.setBackgroundResource(R.drawable.play);
		addView(mPlayButton, new LayoutParams(0, 0, 0, 0));

		mTrackNameView = new MarqueeTextView(context);
		mTrackNameView.setTypeface(ResManager.getInstance().getNormalTypeface());
		mTrackNameView.setHorizontalFadingEdgeEnabled(true);
		mTrackNameView.setTextSize(20);
		mTrackNameView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		addView(mTrackNameView, new LayoutParams(0, 0, 0, 0));

		mHandler = createHandler();
	}

	private Handler createHandler() {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				PlayerState state = PlayerState.values()[msg.what];
				Object data = msg.obj;

				switch (state) {
					case STOPPED:
						getPlayButton().setBackgroundResource(R.drawable.play);
						mTrackNameView.setText(R.string.empty_string);
						mTrackNameDisplayed = false;
						break;
					case ERROR:
						getPlayButton().setBackgroundResource(R.drawable.play);
						mTrackNameView.setText((data != null) ? ((PlayerManager.Error) data).messageId : R.string.error);
						mTrackNameDisplayed = false;
						break;
					case PLAYING:
						getPlayButton().setBackgroundResource(R.drawable.pause);

						boolean hasTrackName = data != null;
						mTrackNameDisplayed |= hasTrackName;

						String trackTitle;
						if (hasTrackName) {
							if (data instanceof SongInfo) {
								SongInfo info = (SongInfo) data;

								trackTitle = getResources().getString(R.string.track_title);
								trackTitle = String.format(trackTitle, info.getArtistOrEmpty(), info.getTitleOrEmpty());
							} else {
								trackTitle = data.toString();
							}
						} else {
							trackTitle = getResources().getString(R.string.loading);
						}

						if (hasTrackName || ! mTrackNameDisplayed) {
							mTrackNameView.setText(trackTitle);
						}
						break;
					default:
						break;
				}
			}
		};
	}

	/**
	 * Returns play button
	 *
	 * @return play button
	 */
	public Button getPlayButton() {
		return mPlayButton;
	}

	/**
	 * Returns info button
	 *
	 * @return info button
	 */
	public Button getInfoButton() {
		return mInfoButton;
	}

	/**
	 * Configure layout with selected orientation
	 *
	 * @param orientation design orientation
	 */
	public void setOrientation(DesignOrientation orientation) {
		switch (orientation) {
			case PORTRAIT_PHONE:
				mLayoutWidth = 480;
				mLayoutHeight = 800;
				break;
			case PORTRAIT_TABLET:
				mLayoutWidth = 800;
				mLayoutHeight = 1175;
				break;
			case LANDSCAPE_TABLET:
				mLayoutWidth = 1175;
				mLayoutHeight = 800;
				break;
		}

		LayoutParams lp;

		lp = (LayoutParams) mSpeakerImage.getLayoutParams();
		switch (orientation) {
			case PORTRAIT_PHONE:
				lp = new LayoutParams(42, 88, 396, 396).centerHorizontal();
				break;
			case PORTRAIT_TABLET:
				lp = new LayoutParams(107, 108, 591, 591).centerHorizontal();
				break;
			case LANDSCAPE_TABLET:
				lp = new LayoutParams(98, 107, 540, 540).centerVertical();
				break;
		}
		mSpeakerImage.setLayoutParams(lp);

		lp = (LayoutParams) mLogoImage.getLayoutParams();
		switch (orientation) {
			case PORTRAIT_PHONE:
				lp = new LayoutParams(55, 241, 371, 100).centerHorizontal();
				break;
			case PORTRAIT_TABLET:
				lp = new LayoutParams(214, 358, 375, 100).centerHorizontal();
				break;
			case LANDSCAPE_TABLET:
				lp = new LayoutParams(120, 336, 500, 106).centerVertical();
				break;
		}
		mLogoImage.setLayoutParams(lp);

		lp = (LayoutParams) mInfoButton.getLayoutParams();
		switch (orientation) {
			case PORTRAIT_PHONE:
				lp = new LayoutParams(51, 657, 94, 94).scaleProportional();
				break;
			case PORTRAIT_TABLET:
				lp = new LayoutParams(210, 979, 96, 96).scaleProportional();
				break;
			case LANDSCAPE_TABLET:
				lp = new LayoutParams(676, 336, 112, 112).scaleProportional().centerVertical();
				break;
		}
		mInfoButton.setLayoutParams(lp);

		lp = (LayoutParams) mPlayButton.getLayoutParams();
		switch (orientation) {
			case PORTRAIT_PHONE:
				lp = new LayoutParams(189, 657, 240, 94)/*.scaleProportional()/*.alignRight()*/;
				break;
			case PORTRAIT_TABLET:
				lp = new LayoutParams(350, 979, 248, 96)/*.scaleProportional()/*.alignRight()*/;
				break;
			case LANDSCAPE_TABLET:
				lp = new LayoutParams(830, 336, 222, 112).centerVertical()/*.scaleProportional()/*.alignRight()*/;
				break;
		}
		mPlayButton.setLayoutParams(lp);

		lp = (LayoutParams) mTrackNameView.getLayoutParams();
		switch (orientation) {
			case PORTRAIT_PHONE:
				lp = new LayoutParams(0, 558, 480, 40).textSize(30);
				break;
			case PORTRAIT_TABLET:
				lp = new LayoutParams(20, 883, 760, 60).textSize(45);
				break;
			case LANDSCAPE_TABLET:
				lp = new LayoutParams(620, 250, 500, 60).textSize(40);
				break;
		}
		mTrackNameView.setLayoutParams(lp);
	}

	/**
	 * Update UI through handler
	 *
	 * @param newState current player state
	 * @param data additional data (song info for example)
	 */
	@Override
	public void onStateChanged(PlayerState newState, Object data) {
		Message message = Message.obtain(mHandler, newState.ordinal(), data);
		message.sendToTarget();
	}

	/**
	 * Layout orientation
	 */
	public static enum DesignOrientation {
		PORTRAIT_PHONE,
		PORTRAIT_TABLET, LANDSCAPE_TABLET
	}
}
