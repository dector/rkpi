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
package io.github.dector.rkpi.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.TextView;

import io.github.dector.rkpi.R;
import io.github.dector.rkpi.components.player.PlayerStateObserver;
import io.github.dector.rkpi.components.player.PlayerState;
import io.github.dector.rkpi.layouts.AboutLayout;
import io.github.dector.rkpi.layouts.MainLayout;
import io.github.dector.rkpi.tools.DeviceTools;

import java.lang.reflect.Field;

import static io.github.dector.rkpi.tools.FlurryClient.Event;

/**
 * View pager to store layouts
 *
 * @author dector
 */
public class AppViewPager extends ViewPager {

	/** Use custom scroller or default */
	private static final boolean USE_CUSTOM_SCROLLER = true;

	/** About page index */
	private static final int ABOUT_PAGE = 0;
	/** Main page index */
	private static final int MAIN_PAGE = 1;

	public static final int DEFAULT_PAGE = MAIN_PAGE;

	/** Is main page opened now */
	private boolean mMainPageOpened;

	private AppAdapter mAdapter;

	/**
	 * Copy of default interpolator
	 */
	private static class CustomInterpolator implements Interpolator {
		@Override
		public float getInterpolation(float t) {
			t -= 1.0F;
			return t * t * t * t * t + 1.0F;
		}
	}

	/**
	 * Create new instance
	 *
	 * @param context activity context
	 */
	public AppViewPager(Context context) {
		super(context);

		if (USE_CUSTOM_SCROLLER) {
			setupScroller();
		}

		setPageMargin(0);
		initBackground(context);
	}

	/**
	 * Use custom scroller in ViewPager
	 */
	private void setupScroller() {
		try {
			Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
			scrollerField.setAccessible(true);
			Scroller mScroller = new FixedSpeedScroller(getContext(), new CustomInterpolator());
			scrollerField.set(this, mScroller);
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
	}

	public AppAdapter createAndSetAdapter(Context context) {
		mAdapter = new AppAdapter(context);
		setAdapter(mAdapter);

		setOnPageChangeListener(new PageChangeListener());
		setCurrentItem(MAIN_PAGE);

		return mAdapter;
	}

	/**
	 * Load, scale and setup background image
	 *
	 * @param context activity context
	 */
	private void initBackground(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		Point displaySize = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(displaySize);
		} else {
			displaySize.set(display.getWidth(), display.getHeight());
		}

		BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
		decodeOptions.inJustDecodeBounds = true;

		BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
		decodeOptions.inJustDecodeBounds = false;
		decodeOptions.inSampleSize = DeviceTools.getSampleSize(decodeOptions, displaySize);

		Bitmap sourceBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.background, decodeOptions);
		Bitmap background = Bitmap.createScaledBitmap(sourceBackground, displaySize.x, displaySize.y, true);
		Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), background);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			setBackground(bitmapDrawable);
		} else {
			setBackgroundDrawable(bitmapDrawable);
		}

		sourceBackground.recycle();
	}

	/**
	 * Switch between pages
	 */
	public void switchPages() {
		if (mMainPageOpened) {
			setCurrentItem(ABOUT_PAGE, true);
		} else {
			setCurrentItem(MAIN_PAGE, true);
		}
	}

	@Override
	public void setCurrentItem(int item) {
		super.setCurrentItem(item);
		Event.PAGE_OPENED.builder()
				.param(Event.KEY_TYPE, (item == MAIN_PAGE) ? Event.VALUE_TYPE_MAIN : Event.VALUE_TYPE_ABOUT)
				.log();
	}

	public void setOrientation(MainLayout.DesignOrientation orientation) {
		mAdapter.setOrientation(orientation);
	}

	/**
	 * Page change listener to determine selected page
	 */
	public class PageChangeListener implements ViewPager.OnPageChangeListener {
		@Override
		public void onPageScrolled(int i, float v, int i2) {}

		@Override
		public void onPageSelected(int i) {
			mMainPageOpened = i == MAIN_PAGE;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	}

	/**
	 * Custom PagerAdapter to store layouts
	 */
	public static class AppAdapter extends PagerAdapter implements PlayerStateObserver {

		/** Main layout */
		private AboutLayout mAboutLayout;
		/** About layout */
		private MainLayout mMainLayout;

		/**
		 * Create instance
		 *
		 * @param context activity context
		 */
		public AppAdapter(Context context) {
			mMainLayout = new MainLayout(context);
			mAboutLayout = new AboutLayout(context);
		}

		/**
		 * Create layout for selected page
		 *
		 * @param container page container
		 * @param position page index
		 * @return layout view
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View v = null;

			if (position == ABOUT_PAGE) {
				v = mAboutLayout;
			} else if (position == MAIN_PAGE) {
				v = mMainLayout;
			}

			container.addView(v, 0);
			return v;
		}

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeAllViews();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        /**
		 * Returns pages count
		 *
		 * @return 2 (Main and About page)
		 */
		@Override
		public int getCount() {
			return 2;
		}

        /**
		 * Returns 1 (full page) for main layout and <1 (partially) for about layout
		 *
		 * @param position page index
		 * @return page width in % / 100
		 */
		@Override
		public float getPageWidth(int position) {
			if (position == ABOUT_PAGE) {
                Log.w("Width page", "" + (float) mAboutLayout.getLayoutWidth() / mMainLayout.getLayoutWidth());

                return (float) mAboutLayout.getLayoutWidth() / mMainLayout.getLayoutWidth();
			} else {
				return 1;
			}
		}

        @Override
		public boolean isViewFromObject(View view, Object o) {
			return view.equals(o);
		}

		/**
		 * Set orientation for layouts
		 *
		 * @param orientation design orientation
		 */
		private void setOrientation(MainLayout.DesignOrientation orientation) {
			mMainLayout.setOrientation(orientation);
			mAboutLayout.setOrientation(orientation);

            notifyDataSetChanged();
		}

		/**
		 * Returns play button
		 *
		 * @return play button
		 */
		public Button getPlayButton() {
			return mMainLayout.getPlayButton();
		}

		/**
		 * Returns info button
		 *
		 * @return info button
		 */
		public Button getInfoButton() {
			return mMainLayout.getInfoButton();
		}

		/**
		 * Returns settings button
		 *
		 * @return settings button
		 */
		public Button getSettingsButton() {
			return mAboutLayout.getSettingsButton();
		}

        public TextView getTrackNameView() {
            return mMainLayout.getTrackNameView();
        }

		/**
		 * Update layout when player state changed
		 *
		 * @param newState current player state
		 * @param data additional data (song info for example)
		 */
		@Override
		public void onStateChanged(PlayerState newState, Object data) {
			mMainLayout.onStateChanged(newState, data);
		}
	}
}
