package com.k.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


/**
 * @author k
 * @date 2018/4/11
 */
public class CViewUtil {
    /**
     * fragment切换动画时长
     */
    public static final int FRAGMENT_TRANSLATION_DURATION = 300;

    /**
     * 将dp转化为px
     */
    public static int dp2px(Context context, int dp) {
        //获取手机密度
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    public static SpannableStringBuilder expireDate(String dateStr, int day) {
        String date = "  剩" + day + "天";
        return SpanUtil.colorSpan(Color.parseColor("#99000000"), dateStr + date, date);
    }

    public static SpannableStringBuilder expireDateColorSize(String dateStr, int day) {
        String date = " 剩" + day + "天";
        return SpanUtil.sizeAndColorSpan(11, Color.parseColor("#99000000"), dateStr + date, date);
    }

    public static float dp2px(Context context, float dp) {
        //获取手机密度
        float density = context.getResources().getDisplayMetrics().density;
        return (float) (dp * density + 0.5);
    }

    public static int px2dp(Context context, int px) {
        //获取手机密度
        float density = context.getResources().getDisplayMetrics().density;
        //实现四舍五入
        return (int) (px / density + 0.5);
    }

    public static int px2sp(Context context, float px) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5F);
    }

    public static int sp2px(Context context, float sp) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5F);
    }

    public static void setupLayout(AppCompatActivity activity, int container, Fragment
            fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide(Gravity.LEFT);
            slideTransition.setDuration(FRAGMENT_TRANSLATION_DURATION);
            fragment.setReenterTransition(slideTransition);
            fragment.setExitTransition(slideTransition);
        }
        activity.getSupportFragmentManager().beginTransaction()
                .add(container, fragment)
                .commit();
    }

    public static void changeFragment(AppCompatActivity activity, int container, Fragment
            baseFragment, Fragment to) {
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (!to.isAdded()) {
            // 先判断是否被add过
            fragmentTransaction.add(container, to).hide(baseFragment);
        } else {
            fragmentTransaction.show(to).hide(baseFragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 改变状态栏文字图标颜色
     */
    public static void setDarkStatusIcon(Activity activity, boolean bDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (bDark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    /**
     * 带动画
     */
    public static void setupLayoutWithAnim(AppCompatActivity activity, int container, Fragment
            fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide(Gravity.LEFT);
            slideTransition.setDuration(FRAGMENT_TRANSLATION_DURATION);
            fragment.setReenterTransition(slideTransition);
            fragment.setExitTransition(slideTransition);
        }
        activity.getSupportFragmentManager().beginTransaction()
                .replace(container, fragment)
                .show(fragment)
                .commit();
    }

    public static void changeFragment(AppCompatActivity activity, int container, Fragment
            baseFragment, Object... views) {
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
                .beginTransaction();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide(Gravity.RIGHT);
            slideTransition.setDuration(FRAGMENT_TRANSLATION_DURATION);
            // Create nextFragment and define some of it transitions
            baseFragment.setEnterTransition(slideTransition);
            baseFragment.setAllowEnterTransitionOverlap(true);
            baseFragment.setAllowReturnTransitionOverlap(true);
            if (views != null) {
                for (Object view1 : views) {
                    View view = (View) view1;
                    if (view != null) {
                        ChangeBounds changeBoundsTransition = new ChangeBounds();
                        changeBoundsTransition.setDuration(FRAGMENT_TRANSLATION_DURATION);
                        baseFragment.setSharedElementEnterTransition(changeBoundsTransition);
                        fragmentTransaction.addSharedElement(view, view.getTransitionName());
                    }
                }
            }
        }
        fragmentTransaction.replace(container, baseFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * 左侧关闭的fragment也有动画
     */
    public static void changeLeftFragment(AppCompatActivity activity, int container, Fragment
            baseFragment, Object... views) {
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager()
                .beginTransaction();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slideTransition = new Slide(Gravity.RIGHT);
            slideTransition.setDuration(FRAGMENT_TRANSLATION_DURATION);
            Slide slideLeft = new Slide(Gravity.LEFT);
            slideLeft.setDuration(FRAGMENT_TRANSLATION_DURATION);
            baseFragment.setReenterTransition(slideLeft);
            baseFragment.setExitTransition(slideLeft);
            // Create nextFragment and define some of it transitions
            baseFragment.setEnterTransition(slideTransition);
            baseFragment.setAllowEnterTransitionOverlap(true);
            baseFragment.setAllowReturnTransitionOverlap(true);
            if (views != null) {
                for (Object view1 : views) {
                    View view = (View) view1;
                    if (view != null) {
                        ChangeBounds changeBoundsTransition = new ChangeBounds();
                        changeBoundsTransition.setDuration(FRAGMENT_TRANSLATION_DURATION);
                        baseFragment.setSharedElementEnterTransition(changeBoundsTransition);
                        fragmentTransaction.addSharedElement(view, view.getTransitionName());
                    }
                }
            }
        }
        fragmentTransaction.replace(container, baseFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * 隐藏软键盘
     */
    public static void hideKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    /**
     * 判断是否隐藏软键盘
     */
    public static void isShouldHideInput(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getRawX() <= left || event.getRawX() >= right
                    || event.getRawY() <= top || event.getRawY() >= bottom) {
                hideKeyboard(v);
                v.clearFocus();
            }
        }
    }

    public static void setTextViewFont(Context context, TextView tv, String path) {
        Typeface customFont = Typeface.createFromAsset(context.getAssets(), path);
        tv.setTypeface(customFont);
    }

    public static boolean openOtherApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        if (checkPackInfo(context, packageName)) {
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查包是否存在
     *
     * @param packName 包名
     */
    private static boolean checkPackInfo(Context context, String packName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        int ppp = CViewUtil.px2dp(context, result);
        float pdd = Resources.getSystem().getDisplayMetrics().density * ppp;
        return (int) pdd;
    }

    public static void setTitlePadding(Context context, View view) {
        view.setPadding(0, getStatusBarHeight(context), 0, 0);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public void getScreenProperty(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)


        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
    }
}
