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
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.dector.rkpi.R;
import io.github.dector.rkpi.common.ResManager;
import io.github.dector.rkpi.views.AbstractScalableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.dector.rkpi.tools.FlurryClient.Event;

/**
 * Layout that describes information about application
 *
 * @author dector
 */
public final class AboutLayout extends AbstractScalableLayout {

	/** Label enum, used as key */
	private static enum Label {
		APP_TITLE1(R.string.about_app_title1),
		APP_TITLE2(R.string.about_app_title2),
		APP_TITLE3(R.string.about_app_title3),
		DESIGN_TITLE(R.string.about_design_title),
		DESIGN_NAME(R.string.about_design_name),
		PROGRAMMING_TITLE(R.string.about_programming_title),
		PROGRAMMING_NAME(R.string.about_programming_name);

		/** Label resource string id */
		public final int textId;

		/** Create instance */
		private Label(int textId) {
			this.textId = textId;
		}
	}

	private static enum Social {
		VKONTAKTE(R.drawable.vk, "http://vk.com/radiokpi"),
		FACEBOOK(R.drawable.fb, "https://www.facebook.com/r.kpi.ua"),
		TWITTER(R.drawable.tw, "https://twitter.com/RadioKPI"),
		YOUTUBE(R.drawable.yt, "http://www.youtube.com/user/RadioKPI");

		public final int imgId;
		public final String url;

		private Social(int imgId, String url) {
			this.imgId = imgId;
			this.url = url;
		}
	}

	/** Settings button */
	private final Button mSettingsButton;

	/** List of labels */
	private final List<TextView> mTextViews;
	/** Layout params for labels */
	private final Map<Label, LayoutParams> mTextViewLayoutParams;

	private final List<ImageView> mSocialButtons;
	private final Map<Social, LayoutParams> mSocialButtonsLayoutParams;

	/**
	 * Create new instance
	 *
	 * @param context activity context
	 */
	public AboutLayout(Context context) {
		super(context);

		mTextViews = new ArrayList<TextView>();
		mSocialButtons = new ArrayList<ImageView>();

		mTextViewLayoutParams = new HashMap<Label, LayoutParams>();
		mSocialButtonsLayoutParams = new HashMap<Social, LayoutParams>();

		mLayoutWidth = 1;
		mLayoutHeight = 1;

		mSettingsButton = new Button(context);
		mSettingsButton.setBackgroundResource(R.drawable.settings);
		addView(mSettingsButton, new LayoutParams(0, 0, 0, 0));

		addText(context, Label.APP_TITLE1, null, false);
        addText(context, Label.APP_TITLE2, null, true);
        addText(context, Label.APP_TITLE3, "r.kpi.ua", true);

		addText(context, Label.DESIGN_TITLE, null, false);
		addText(context, Label.DESIGN_NAME, "vavs.tv", true);

		addText(context, Label.PROGRAMMING_TITLE, null, false);
		addText(context, Label.PROGRAMMING_NAME, "dector.github.io", true);

		addSocialButton(context, Social.VKONTAKTE);
		addSocialButton(context, Social.FACEBOOK);
		addSocialButton(context, Social.TWITTER);
		addSocialButton(context, Social.YOUTUBE);
	}

	/**
	 * Create text view
	 *
	 * @param context activity context
	 * @param label corresponding label
	 * @param url url for label, may be null
	 * @param bold set true to use bold font
	 * @return
	 */
	private TextView addText(final Context context, Label label, final String url, boolean bold) {
		ResManager resManager = ResManager.getInstance();
		Resources res = context.getResources();

		TextView view = new TextView(context);
		view.setTag(label);
        String text = context.getResources().getString(label.textId);
        view.setText(Html.fromHtml(text));
		view.setSingleLine(true);
		view.setTypeface((bold) ? resManager.getBoldTypeface() : resManager.getNormalTypeface());
		view.setTextColor((url == null) ? res.getColor(R.color.about_text) : res.getColor(R.color.about_url));

		if (url != null) {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Event.LINK_CLICKED.builder()
							.param(Event.KEY_TYPE, url)
							.log();

					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse("http://" + url + "/"));
					context.startActivity(i);
				}
			});
		}

		mTextViews.add(view);
		addView(view, new LayoutParams(0, 0, 0, 0));

		return view;
	}

	private ImageView addSocialButton(final Context context, final Social social) {
		ImageView view = new ImageView(context);
		view.setTag(social);
		view.setImageResource(social.imgId);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Event.LINK_CLICKED.builder()
						.param(Event.KEY_TYPE, social.url)
						.log();

				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(social.url));
				context.startActivity(i);
			}
		});

		mSocialButtons.add(view);
		addView(view, new LayoutParams(0, 0, 0, 0));

		return view;
	}

	/**
	 * Return settings button
	 *
	 * @return settings button
	 */
	public Button getSettingsButton() {
		return mSettingsButton;
	}

	/**
	 * Configure layout with selected orientation
	 *
	 * @param orientation design orientation
	 */
	public void setOrientation(final MainLayout.DesignOrientation orientation) {
		switch (orientation) {
			case PORTRAIT_PHONE:
				mLayoutWidth = 287;
				mLayoutHeight = 800;
				break;
			case PORTRAIT_TABLET:
				mLayoutWidth = 408;
				mLayoutHeight = 1175;
				break;
			case LANDSCAPE_TABLET:
				mLayoutWidth = 350;
				mLayoutHeight = 800;
				break;
		}

        mSettingsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrientation(orientation);
                v.postInvalidate();
            }
        });

		LayoutParams lp = null;

		switch (orientation) {
			case PORTRAIT_PHONE:
				lp = new LayoutParams(43, 656, 92, 92).scaleProportional()/*.alignRight()*/;
				break;
			case PORTRAIT_TABLET:
				lp = new LayoutParams(102, 975, 96, 96).scaleProportional()/*.alignRight()*/;
				break;
			case LANDSCAPE_TABLET:
				lp = new LayoutParams(58, 650, 84, 84).scaleProportional()/*.alignRight()*/;
				break;
		}
		mSettingsButton.setLayoutParams(lp);

		switch (orientation) {
			case PORTRAIT_PHONE:
				mTextViewLayoutParams.clear();
				mTextViewLayoutParams.put(Label.APP_TITLE1, new LayoutParams(48, 48, 235, 25).textSize(21));
				mTextViewLayoutParams.put(Label.APP_TITLE2, new LayoutParams(48, 82, 235, 25).textSize(21));
				mTextViewLayoutParams.put(Label.APP_TITLE3, new LayoutParams(48, 116, 235, 25).textSize(21));
				mTextViewLayoutParams.put(Label.DESIGN_TITLE, new LayoutParams(48, 358, 235, 25).textSize(21));
				mTextViewLayoutParams.put(Label.DESIGN_NAME, new LayoutParams(48, 392, 235, 25).textSize(21));
				mTextViewLayoutParams.put(Label.PROGRAMMING_TITLE, new LayoutParams(48, 497, 235, 25).textSize(21));
				mTextViewLayoutParams.put(Label.PROGRAMMING_NAME, new LayoutParams(48, 531, 235, 25).textSize(21));

				mSocialButtonsLayoutParams.clear();
				mSocialButtonsLayoutParams.put(Social.VKONTAKTE, new LayoutParams(48, 166, 62, 62).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.FACEBOOK, new LayoutParams(148, 166, 62, 62).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.TWITTER, new LayoutParams(48, 268, 62, 62).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.YOUTUBE, new LayoutParams(148, 268, 62, 62).scaleProportional());
				break;
			case PORTRAIT_TABLET:
				mTextViewLayoutParams.put(Label.APP_TITLE1, new LayoutParams(107, 246, 384, 35).textSize(28));
				mTextViewLayoutParams.put(Label.APP_TITLE2, new LayoutParams(107, 286, 384, 35).textSize(28));
				mTextViewLayoutParams.put(Label.APP_TITLE3, new LayoutParams(107, 326, 384, 35).textSize(28));
				mTextViewLayoutParams.put(Label.DESIGN_TITLE, new LayoutParams(107, 677, 384, 35).textSize(28));
				mTextViewLayoutParams.put(Label.DESIGN_NAME, new LayoutParams(107, 717, 384, 35).textSize(28));
				mTextViewLayoutParams.put(Label.PROGRAMMING_TITLE, new LayoutParams(107, 833, 384, 35).textSize(28));
				mTextViewLayoutParams.put(Label.PROGRAMMING_NAME, new LayoutParams(107, 873, 384, 35).textSize(28));

				mSocialButtonsLayoutParams.clear();
				mSocialButtonsLayoutParams.put(Social.VKONTAKTE, new LayoutParams(108, 411, 86, 86).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.FACEBOOK, new LayoutParams(252, 411, 86, 86).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.TWITTER, new LayoutParams(108, 557, 86, 86).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.YOUTUBE, new LayoutParams(252, 557, 86, 86).scaleProportional());
				break;
			case LANDSCAPE_TABLET:
                mTextViewLayoutParams.put(Label.APP_TITLE1, new LayoutParams(58, 74, 334, 35).textSize(28));
                mTextViewLayoutParams.put(Label.APP_TITLE2, new LayoutParams(58, 114, 334, 35).textSize(28));
                mTextViewLayoutParams.put(Label.APP_TITLE3, new LayoutParams(58, 154, 334, 35).textSize(28));
                mTextViewLayoutParams.put(Label.DESIGN_TITLE, new LayoutParams(58, 384, 334, 35).textSize(28));
                mTextViewLayoutParams.put(Label.DESIGN_NAME, new LayoutParams(58, 424, 334, 35).textSize(28));
                mTextViewLayoutParams.put(Label.PROGRAMMING_TITLE, new LayoutParams(58, 524, 334, 35).textSize(28));
                mTextViewLayoutParams.put(Label.PROGRAMMING_NAME, new LayoutParams(58, 564, 334, 35).textSize(28));

				mSocialButtonsLayoutParams.clear();
				mSocialButtonsLayoutParams.put(Social.VKONTAKTE, new LayoutParams(58, 224, 62, 62).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.FACEBOOK, new LayoutParams(166, 224, 62, 62).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.TWITTER, new LayoutParams(58, 304, 62, 62).scaleProportional());
				mSocialButtonsLayoutParams.put(Social.YOUTUBE, new LayoutParams(166, 304, 62, 62).scaleProportional());
				break;
		}

		for (TextView view : mTextViews) {
			Label label = (Label) view.getTag();
			lp = mTextViewLayoutParams.get(label);
			view.setLayoutParams(lp);
		}

		for (ImageView view : mSocialButtons) {
			Social social = (Social) view.getTag();
			lp = mSocialButtonsLayoutParams.get(social);
			view.setLayoutParams(lp);
		}
	}
}
