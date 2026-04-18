package com.mcdonalds.mcduikit.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mcdonalds.mcduikit.widget.util.ToolBarViewType;

import java.io.File;

public class McDToolBarView extends RelativeLayout {
    private static final int ID_ARCHUS_HEADER_CONTAINER = 0x7f0b01a6;
    private static final int ID_BASKET_ERROR = 0x7f0b0212;
    private static final int ID_BASKET_LAYOUT = 0x7f0b0219;
    private static final int ID_BASKET_PRICE = 0x7f0b021c;
    private static final int ID_GET_HELP_ORDER_STATUS = 0x7f0b091c;
    private static final int ID_IV_QR_CODE_ARROW = 0x7f0b0bc8;
    private static final int ID_LOYALTY_DASHBOARD_QR_CODE = 0x7f0b0d5a;
    private static final int ID_MCD_BACK_NAVIGATION_BUTTON = 0x7f0b0e8a;
    private static final int ID_MY_MCD_LOGO = 0x7f0b0eed;
    private static final int ID_PROGRESS_TRACKER = 0x7f0b13eb;
    private static final int ID_QR_CODE = 0x7f0b1416;
    private static final int ID_REWARD_DEALS = 0x7f0b1576;
    private static final int ID_SEPARATOR = 0x7f0b16e0;
    private static final int ID_TOOLBAR = 0x7f0b1965;
    private static final int ID_TOOLBAR_CENTER_IMAGE_ICON = 0x7f0b1967;
    private static final int ID_TOOLBAR_LOYALTY_POINT_BALANCE_TEXT = 0x7f0b1968;
    private static final int ID_TOOLBAR_RESET_BUTTON = 0x7f0b1969;
    private static final int ID_TOOLBAR_RIGHT_BUTTON = 0x7f0b196a;
    private static final int ID_TOOLBAR_ROA_HEADER_TEXT = 0x7f0b196b;
    private static final int ID_TOOLBAR_SEARCH_ICON = 0x7f0b196f;
    private static final int ID_TOOLBAR_TITLE_TEXT = 0x7f0b1970;
    private static final int ID_TOOLBAR_CANCEL = 0x7f0b1973;
    private static final int ID_TOOLBAR_POINT_BALANCE = 0x7f0b1975;
    private static final int ID_TOOLBAR_SAVE = 0x7f0b1976;

    private final ImageView mBackIcon;
    private final ImageView mCenterImageIcon;
    private final ImageView mSearchIcon;
    private final ImageView mBasketErrorIcon;
    private final ImageView mRightIcon;
    private final ImageView mHeaderIcon;
    private final ImageView mLoyaltyQrCode;
    private final ImageView mQrArrowIcon;
    private final ImageView mMyMcDLogoIcon;
    private final LinearLayout mArchusHeaderContainer;
    private final LinearLayout mBasketLayout;
    private final LinearLayout mRewardDeals;
    private final LinearLayout mToolbarPointBalance;
    private final McDTextView mBasketTextView;
    private final McDTextView mTitle;
    private final McDTextView mCancelTextView;
    private final McDTextView mSaveTextView;
    private final McDTextView mResetTextView;
    private final McDTextView mRoaTitleTextView;
    private final McDTextView mGetHelpLink;
    private final McDTextView mLoyaltyPointBalanceText;
    private final McDAppCompatTextView mQrTextView;
    private final ProgressStateTracker mProgressTracker;
    private boolean mHomeScreen;
    private boolean mImmersiveThemeApplied;

    public McDToolBarView(Context context) {
        super(context);
        setId(ID_TOOLBAR);
        setBackgroundColor(0xFF4A148C);
        LayoutParams rootLp = new LayoutParams(LayoutParams.MATCH_PARENT, dp(56));
        setLayoutParams(rootLp);

        mBackIcon = buildImage(context, ID_MCD_BACK_NAVIGATION_BUTTON, ALIGN_PARENT_START);
        mBackIcon.setVisibility(View.GONE);
        addView(mBackIcon, leftIconLayoutParams());

        mQrTextView = new McDAppCompatTextView(context);
        mQrTextView.setId(ID_QR_CODE);
        mQrTextView.setVisibility(View.GONE);
        addView(mQrTextView, leftTextLayoutParams());

        mArchusHeaderContainer = new LinearLayout(context);
        mArchusHeaderContainer.setId(ID_ARCHUS_HEADER_CONTAINER);
        mArchusHeaderContainer.setOrientation(LinearLayout.HORIZONTAL);
        mArchusHeaderContainer.setGravity(Gravity.CENTER);
        LayoutParams headerLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        headerLp.addRule(CENTER_IN_PARENT);
        addView(mArchusHeaderContainer, headerLp);

        mTitle = new McDTextView(context);
        mTitle.setId(ID_TOOLBAR_TITLE_TEXT);
        mTitle.setTextColor(0xFFFFFFFF);
        mTitle.setVisibility(View.GONE);
        mArchusHeaderContainer.addView(mTitle);

        mCenterImageIcon = new ImageView(context);
        mCenterImageIcon.setId(ID_TOOLBAR_CENTER_IMAGE_ICON);
        mCenterImageIcon.setVisibility(View.GONE);
        mArchusHeaderContainer.addView(mCenterImageIcon);

        mProgressTracker = new ProgressStateTracker(context);
        mProgressTracker.setId(ID_PROGRESS_TRACKER);
        mProgressTracker.setVisibility(View.GONE);
        LayoutParams trackerLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        trackerLp.addRule(CENTER_IN_PARENT);
        addView(mProgressTracker, trackerLp);

        mBasketLayout = new LinearLayout(context);
        mBasketLayout.setId(ID_BASKET_LAYOUT);
        mBasketLayout.setOrientation(LinearLayout.HORIZONTAL);
        mBasketLayout.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams basketLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        basketLp.addRule(ALIGN_PARENT_END);
        basketLp.addRule(CENTER_VERTICAL);
        basketLp.rightMargin = dp(12);
        addView(mBasketLayout, basketLp);

        mBasketTextView = new McDTextView(context);
        mBasketTextView.setId(ID_BASKET_PRICE);
        mBasketTextView.setTextColor(0xFFFFFFFF);
        mBasketLayout.addView(mBasketTextView);

        mBasketErrorIcon = new ImageView(context);
        mBasketErrorIcon.setId(ID_BASKET_ERROR);
        mBasketErrorIcon.setVisibility(View.GONE);
        mBasketLayout.addView(mBasketErrorIcon);

        mRightIcon = new ImageView(context);
        mRightIcon.setId(ID_TOOLBAR_RIGHT_BUTTON);
        mRightIcon.setVisibility(View.GONE);
        mBasketLayout.addView(mRightIcon);

        mSearchIcon = new ImageView(context);
        mSearchIcon.setId(ID_TOOLBAR_SEARCH_ICON);
        mSearchIcon.setVisibility(View.GONE);
        LayoutParams searchLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        searchLp.addRule(LEFT_OF, ID_BASKET_LAYOUT);
        searchLp.addRule(CENTER_VERTICAL);
        searchLp.rightMargin = dp(12);
        addView(mSearchIcon, searchLp);

        mCancelTextView = buildText(context, ID_TOOLBAR_CANCEL);
        mCancelTextView.setVisibility(View.GONE);
        addView(mCancelTextView, leftTextLayoutParams());

        mSaveTextView = buildText(context, ID_TOOLBAR_SAVE);
        mSaveTextView.setVisibility(View.GONE);
        addView(mSaveTextView, rightTextLayoutParams());

        mResetTextView = buildText(context, ID_TOOLBAR_RESET_BUTTON);
        mResetTextView.setVisibility(View.GONE);
        addView(mResetTextView, rightTextLayoutParams());

        mRoaTitleTextView = buildText(context, ID_TOOLBAR_ROA_HEADER_TEXT);
        mRoaTitleTextView.setVisibility(View.GONE);
        addView(mRoaTitleTextView, centerTextLayoutParams());

        mGetHelpLink = buildText(context, ID_GET_HELP_ORDER_STATUS);
        mGetHelpLink.setVisibility(View.GONE);
        addView(mGetHelpLink, rightTextLayoutParams());

        mToolbarPointBalance = new LinearLayout(context);
        mToolbarPointBalance.setId(ID_TOOLBAR_POINT_BALANCE);
        mToolbarPointBalance.setOrientation(LinearLayout.HORIZONTAL);
        mToolbarPointBalance.setVisibility(View.GONE);
        addView(mToolbarPointBalance, rightTextLayoutParams());

        mLoyaltyPointBalanceText = buildText(context, ID_TOOLBAR_LOYALTY_POINT_BALANCE_TEXT);
        mToolbarPointBalance.addView(mLoyaltyPointBalanceText);

        mRewardDeals = new LinearLayout(context);
        mRewardDeals.setId(ID_REWARD_DEALS);
        mRewardDeals.setOrientation(LinearLayout.HORIZONTAL);
        mRewardDeals.setVisibility(View.GONE);
        addView(mRewardDeals, rightTextLayoutParams());

        mLoyaltyQrCode = new ImageView(context);
        mLoyaltyQrCode.setId(ID_LOYALTY_DASHBOARD_QR_CODE);
        mLoyaltyQrCode.setVisibility(View.GONE);
        mRewardDeals.addView(mLoyaltyQrCode);

        mQrArrowIcon = new ImageView(context);
        mQrArrowIcon.setId(ID_IV_QR_CODE_ARROW);
        mQrArrowIcon.setVisibility(View.GONE);
        mRewardDeals.addView(mQrArrowIcon);

        mMyMcDLogoIcon = new ImageView(context);
        mMyMcDLogoIcon.setId(ID_MY_MCD_LOGO);
        mMyMcDLogoIcon.setVisibility(View.GONE);
        addView(mMyMcDLogoIcon, leftIconLayoutParams());

        View separator = new View(context);
        separator.setId(ID_SEPARATOR);
        separator.setBackgroundColor(0x33000000);
        LayoutParams sepLp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        sepLp.addRule(ALIGN_PARENT_BOTTOM);
        addView(separator, sepLp);

        mHeaderIcon = mCenterImageIcon;
    }

    public McDToolBarView(Context context, AttributeSet attrs) {
        this(context);
    }

    public McDToolBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.max(1, (int) (value * density + 0.5f));
    }

    private ImageView buildImage(Context context, int id, int alignRule) {
        ImageView image = new ImageView(context);
        image.setId(id);
        image.setVisibility(View.GONE);
        return image;
    }

    private McDTextView buildText(Context context, int id) {
        McDTextView view = new McDTextView(context);
        view.setId(id);
        view.setTextColor(0xFFFFFFFF);
        return view;
    }

    private LayoutParams leftIconLayoutParams() {
        LayoutParams lp = new LayoutParams(dp(40), dp(40));
        lp.addRule(ALIGN_PARENT_START);
        lp.addRule(CENTER_VERTICAL);
        lp.leftMargin = dp(8);
        return lp;
    }

    private LayoutParams leftTextLayoutParams() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_START);
        lp.addRule(CENTER_VERTICAL);
        lp.leftMargin = dp(8);
        return lp;
    }

    private LayoutParams rightTextLayoutParams() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(ALIGN_PARENT_END);
        lp.addRule(CENTER_VERTICAL);
        lp.rightMargin = dp(12);
        return lp;
    }

    private LayoutParams centerTextLayoutParams() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(CENTER_IN_PARENT);
        return lp;
    }

    private void setIcon(ImageView view, int resId, String description) {
        if (view == null) {
            return;
        }
        if (resId != 0) {
            try {
                view.setImageResource(resId);
            } catch (Throwable ignored) {
            }
        }
        if (description != null) {
            view.setContentDescription(description);
        }
        view.setVisibility(View.VISIBLE);
    }

    public void A(boolean value) {
        mImmersiveThemeApplied = value;
    }

    public void B() {
        mBasketLayout.setVisibility(View.GONE);
    }

    public void a() {
        mTitle.setVisibility(View.VISIBLE);
    }

    public void b() {
        mBackIcon.setVisibility(View.GONE);
    }

    public final void c() {
        mProgressTracker.setVisibility(View.GONE);
    }

    public void d() {
        mRightIcon.setVisibility(View.GONE);
    }

    public void e() {
        mSearchIcon.setVisibility(View.GONE);
    }

    public void f() {
        mBasketLayout.setVisibility(View.VISIBLE);
    }

    public void g() {
        mCenterImageIcon.setVisibility(View.VISIBLE);
    }

    public RelativeLayout.LayoutParams getActualToolbarHeaderContainerLayoutParams() {
        return (RelativeLayout.LayoutParams) mArchusHeaderContainer.getLayoutParams();
    }

    public ImageView getHeaderIcon() {
        return mHeaderIcon;
    }

    public boolean getHomeScreen() {
        return mHomeScreen;
    }

    public String getLeftIconContentDescription() {
        CharSequence description = mBackIcon.getContentDescription();
        return description != null ? description.toString() : null;
    }

    public ImageView getLeftIconView() {
        return mBackIcon;
    }

    public ImageView getLocationSearchIcon() {
        return mSearchIcon;
    }

    public ImageView getProgressTrackerFirstStateDotView() {
        return mProgressTracker.getFirstStateDotView();
    }

    public ImageView getProgressTrackerSecondStateDotView() {
        return mProgressTracker.getSecondStateDotView();
    }

    public ImageView getProgressTrackerThirdStateDotView() {
        return mProgressTracker.getThirdStateDotView();
    }

    public McDAppCompatTextView getQrTextView() {
        return mQrTextView;
    }

    public ImageView getRightIcon() {
        return mRightIcon;
    }

    public McDTextView getTitle() {
        return mTitle;
    }

    public void h() {
        mBackIcon.setVisibility(View.VISIBLE);
    }

    public final void i() {}

    public boolean j() {
        return mBasketLayout.getVisibility() == View.VISIBLE;
    }

    public boolean k() {
        return mBackIcon.getVisibility() == View.VISIBLE;
    }

    public void l() {
        mTitle.setVisibility(View.GONE);
    }

    public void m(boolean visible) {
        mBasketLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public final void n() {}

    public void o() {
        mHeaderIcon.setVisibility(View.GONE);
    }

    public void p(boolean visible) {
        mSearchIcon.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void q(boolean visible) {
        mRightIcon.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void r(boolean visible) {
        mBackIcon.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void s(boolean visible) {
        mTitle.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setAccessibilityEvent(ToolBarViewType type) {}

    public void setArchLogoFromFile() {
        mCenterImageIcon.setVisibility(View.VISIBLE);
    }

    public void setBasketError(int resId, String description) {
        setIcon(mBasketErrorIcon, resId, description);
    }

    public void setBasketImageFromFile() {
        mBasketErrorIcon.setVisibility(View.VISIBLE);
    }

    public void setBasketText(String text) {
        mBasketTextView.setText(text);
        mBasketLayout.setVisibility(View.VISIBLE);
    }

    public void setDarkTextTheme(McDTextView view) {
        if (view != null) {
            view.setTextColor(0xFF27251F);
        }
    }

    public void setDefaultThemeTextAndIcon() {
        setBackgroundColor(0xFF4A148C);
        setTextAndIconTheme(true, false);
    }

    public void setErrorImageFromFile() {
        mBasketErrorIcon.setVisibility(View.VISIBLE);
    }

    public void setGetHelpLinkListener(View.OnClickListener listener) {
        mGetHelpLink.setOnClickListener(listener);
        mGetHelpLink.setVisibility(View.VISIBLE);
    }

    public void setHeaderIcon(int resId) {
        setIcon(mHeaderIcon, resId, null);
    }

    public void setHeaderImageFiles(File header, File basket, File error) {
        if (header != null) {
            mHeaderIcon.setVisibility(View.VISIBLE);
        }
        if (basket != null) {
            mBasketLayout.setVisibility(View.VISIBLE);
        }
        if (error != null) {
            mBasketErrorIcon.setVisibility(View.VISIBLE);
        }
    }

    public void setHomeScreen(boolean homeScreen) {
        mHomeScreen = homeScreen;
    }

    public void setIconAndDescription(int resId, String description, ToolBarViewType type) {
        if (type == null) {
            setIcon(mRightIcon, resId, description);
            return;
        }
        switch (type) {
            case BACK:
            case LEFT_ICON:
                setLeftIconAndDescription(resId, description, type);
                break;
            case LOCATION_SEARCH:
                setSearchIconAndDescription(resId, description, type);
                break;
            case BASKET:
            case RIGHT_ICON:
                setRightIconAndDescription(resId, description, type);
                break;
            case ARCH_LOGO:
                setHeaderIcon(resId);
                mHeaderIcon.setContentDescription(description);
                break;
            default:
                setIcon(mRightIcon, resId, description);
                break;
        }
    }

    public void setImageFromFile(ImageView view, File file) {
        if (view != null && file != null && file.exists()) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void setImmersiveBackground(File file) {
        if (file != null) {
            setBackgroundColor(0xFF27251F);
        }
    }

    public void setImmersiveBackgroundColor(String colorString) {
        if (colorString == null || colorString.isEmpty()) {
            return;
        }
        try {
            setBackgroundColor(android.graphics.Color.parseColor(colorString));
        } catch (Throwable ignored) {
        }
    }

    public void setImmersiveThemeApplied(boolean applied) {
        mImmersiveThemeApplied = applied;
    }

    public void setImmersiveToolbarForLoyalty(File file) {
        setImmersiveBackground(file);
        mRewardDeals.setVisibility(View.VISIBLE);
    }

    public void setLeftIcon(int resId) {
        setIcon(mBackIcon, resId, mBackIcon.getContentDescription() != null
                ? mBackIcon.getContentDescription().toString() : null);
    }

    public void setLeftIconAndDescription(int resId, String description, ToolBarViewType type) {
        setIcon(mBackIcon, resId, description);
    }

    public void setLightTextTheme(McDTextView view) {
        if (view != null) {
            view.setTextColor(0xFFFFFFFF);
        }
    }

    public void setLoyaltyPointBalance(String text) {
        mLoyaltyPointBalanceText.setText(text);
        mToolbarPointBalance.setVisibility(View.VISIBLE);
    }

    public void setLoyaltyPointBalanceDescription(String text) {
        mLoyaltyPointBalanceText.setContentDescription(text);
    }

    public void setLoyaltyPointBalanceListener(View.OnClickListener listener) {
        mToolbarPointBalance.setOnClickListener(listener);
    }

    public void setLoyaltyQRCodeListener(View.OnClickListener listener) {
        mLoyaltyQrCode.setOnClickListener(listener);
        mRewardDeals.setOnClickListener(listener);
    }

    public void setMyMcDLogoIcon(int resId) {
        setIcon(mMyMcDLogoIcon, resId, null);
    }

    public void setPointBalanceTextColor(int color) {
        mLoyaltyPointBalanceText.setTextColor(color);
    }

    public void setProgressTrackerState(ProgressStateTracker.ProgressState state) {
        mProgressTracker.setVisibility(View.VISIBLE);
        mProgressTracker.setState(state);
    }

    public void setQRCodeIconImageFile(File file) {
        if (file != null) {
            mLoyaltyQrCode.setVisibility(View.VISIBLE);
        }
    }

    public void setRightIconAndDescription(int resId, String description, ToolBarViewType type) {
        setIcon(mRightIcon, resId, description);
    }

    public void setSaveText(int resId) {
        try {
            mSaveTextView.setText(resId);
        } catch (Throwable ignored) {
        }
        mSaveTextView.setVisibility(View.VISIBLE);
    }

    public void setSearchClickListener(View.OnClickListener listener) {
        mSearchIcon.setOnClickListener(listener);
    }

    public void setSearchIconAndDescription(int resId, String description, ToolBarViewType type) {
        setIcon(mSearchIcon, resId, description);
    }

    public void setStringHelpLink(String text) {
        mGetHelpLink.setText(text);
        mGetHelpLink.setVisibility(View.VISIBLE);
    }

    public void setTextAndIconTheme(boolean lightText, boolean darkBackground) {
        int color = lightText ? 0xFFFFFFFF : 0xFF27251F;
        mTitle.setTextColor(color);
        mBasketTextView.setTextColor(color);
        mCancelTextView.setTextColor(color);
        mSaveTextView.setTextColor(color);
        mResetTextView.setTextColor(color);
        mRoaTitleTextView.setTextColor(color);
        mGetHelpLink.setTextColor(color);
        mLoyaltyPointBalanceText.setTextColor(color);
        if (darkBackground) {
            setBackgroundColor(0xFF27251F);
        }
    }

    public void setToolBarTitleContentDescription(String description) {
        mTitle.setContentDescription(description);
    }

    public void setToolbarHeaderContainerLayoutParams(RelativeLayout.LayoutParams params) {
        if (params != null) {
            mArchusHeaderContainer.setLayoutParams(params);
        }
    }

    public void t() {
        mRewardDeals.setVisibility(View.GONE);
    }

    public void u(boolean visible) {
        mRewardDeals.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void v(String title) {
        mTitle.setText(title);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void w(int textResId, View.OnClickListener listener, Typeface typeface) {
        try {
            mSaveTextView.setText(textResId);
        } catch (Throwable ignored) {
        }
        if (typeface != null) {
            mSaveTextView.setTypeface(typeface);
        }
        mSaveTextView.setOnClickListener(listener);
        mSaveTextView.setVisibility(View.VISIBLE);
    }

    public void x(int leftResId, int rightResId, int searchResId, View.OnClickListener listener) {
        setLeftIcon(leftResId);
        setRightIconAndDescription(rightResId, null, ToolBarViewType.RIGHT_ICON);
        setSearchIconAndDescription(searchResId, null, ToolBarViewType.LOCATION_SEARCH);
        mRightIcon.setOnClickListener(listener);
    }

    public void y(SpannableStringBuilder text, int flags) {
        mTitle.setText(text);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void z(String title, int resId) {
        mTitle.setText(title);
        if (resId != 0) {
            setHeaderIcon(resId);
        }
        mTitle.setVisibility(View.VISIBLE);
    }
}
