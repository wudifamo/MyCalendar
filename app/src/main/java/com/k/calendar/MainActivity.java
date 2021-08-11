package com.k.calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.github.florent37.viewanimator.AnimationBuilder;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements CalendarView.OnCalendarRangeSelectListener, CalendarView.OnMonthChangeListener, RequestListener {
    private CalendarView mCalendarView;
    private TextView tvYi;
    private TextView tvJi, tvMonth;
    private FrameLayout yiContainer;
    private Context mContext = this;
    private int tvMargin, tvHeight;
    private List<CalendarData> yiList = new ArrayList<>();
    private CalendarData calendarData;
    private String rs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
        setContentView(R.layout.activity_main);
        tvMargin = CViewUtil.dp2px(mContext, 10);
        tvHeight = CViewUtil.dp2px(mContext, 32);
        tvYi = findViewById(R.id.tv_yi);
        tvJi = findViewById(R.id.tv_ji);
        yiContainer = findViewById(R.id.yi_container);
        tvMonth = findViewById(R.id.tv_month);
        initCalendar();
    }

    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    private void initCalendar() {
        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setOnCalendarRangeSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setRange(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay(), mCalendarView.getCurYear() + 1, mCalendarView.getCurMonth(), mCalendarView.getCurDay());
        handleDays();
    }

    private Calendar getDayData(CalendarData data) {
        String[] list = data.getYangli().split("-");
        Calendar calendar = new Calendar();
        calendar.setYear(Integer.parseInt(list[0]));
        calendar.setMonth(Integer.parseInt(list[1]));
        calendar.setDay(Integer.parseInt(list[2]));

        String ji = data.getJi();
        String yi = data.getYi();
        if (ji.contains("嫁娶") || ji.contains("领证")) {
            calendar.setScheme("忌");
            calendar.setSchemeColor(Color.RED);
        } else if (yi.contains("嫁娶") || yi.contains("领证")) {
            calendar.setScheme("宜");
            calendar.setSchemeColor(0xFF008800);
            if (!calendar.isWeekend()) {
                yiList.add(data);
            }
        } else {
            calendar.setScheme("平");
            calendar.setSchemeColor(Color.BLACK);
        }
        return calendar;
    }

    private void handleDays() {
        Map<String, Calendar> map = new HashMap<>();
        List<CalendarData> list = DatabaseUtil.queryAll(null, CalendarData.class);
        for (CalendarData data : list) {
            Calendar calendar = getDayData(data);
            map.put(calendar.toString(), calendar);
        }
        mCalendarView.setSchemeDate(map);
    }

    @Override
    public void onCalendarSelectOutOfRange(com.haibin.calendarview.Calendar calendar) {

    }

    @Override
    public void onSelectOutOfRange(com.haibin.calendarview.Calendar calendar, boolean isOutOfMinRange) {

    }

    @Override
    public void onCalendarRangeSelect(com.haibin.calendarview.Calendar calendar, boolean isEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd", Locale.CHINA);
        String date = sdf.format(new Date(calendar.getTimeInMillis()));

        CalendarData data = DatabaseUtil.queryByPrimaryKey("yangli", date, CalendarData.class);
        if (data != null) {
            updateYi(data.getYi());
            tvJi.setText("忌: " + data.getJi());
        } else {
            tvYi.setText("");
            tvJi.setText("");
        }
    }

    @Override
    public void onMonthChange(int year, int month) {
        String str = month + "月 " + year;
        tvMonth.setText(SpanUtil.sizeAndColorSpan(12, ContextCompat.getColor(this, R.color.secondary_white), str, year + ""));
        getData();
    }

    private void getData() {
        if (true) {
            return;
        }
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.MONTH, 6);
        for (int i = 0; i < 100; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("key", "ed7ae112b9583c64cf417c67014fe1b2");
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.CHINA);
            map.put("date", dateFormat.format(calendar.getTime()));

            HttpUtil.getRequest("http://v.juhe.cn/laohuangli/d", map, this);

            calendar.add(java.util.Calendar.DATE, 1);
        }
    }

    @Override
    public void onSuccess(String url, String result) {
        CalendarBean calendarBean = JsonUtils.string2Object(result, CalendarBean.class);
        if (calendarBean.getError_code() == 0) {
            CalendarData resultBean = calendarBean.getResult();
            DatabaseUtil.saveOrUpdateOnce(resultBean);
        }
    }

    @Override
    public void onSuccess(String url, String result, String tag) {

    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onFailed(String url, IOException e) {

    }

    private void updateYi(String yi) {
        //先隐藏
        for (int i = 0; i < yiContainer.getChildCount(); i++) {
            View view = yiContainer.getChildAt(i);
            if (view.getScaleX() != 1) {
                break;
            }
            ViewAnimator.animate(view).scale(1, 0).duration(300).decelerate().startDelay(i * 50).start();
        }
        String[] list = yi.split(" ");
        yiContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                int width = yiContainer.getWidth();
                int tvWidth = (width - getResources().getDimensionPixelOffset(R.dimen.normal_margin) + tvMargin) / 5 - tvMargin;
                for (int i = 0; i < list.length; i++) {
                    String str = list[i];
                    TextView textView;
                    if (yiContainer.getChildCount() > i) {
                        textView = (TextView) yiContainer.getChildAt(i);
                        ViewAnimator.animate(textView).scale(0, 1).duration(300).decelerate().startDelay(i * 50).start();
                    } else {
                        textView = new TextView(mContext);
                        textView.setTextColor(ContextCompat.getColor(mContext, R.color.primary_black));
                        textView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.circle_pop));
                        textView.setGravity(Gravity.CENTER);
                        textView.setTextSize(12);
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(tvWidth, tvHeight);
                        lp.leftMargin = i % 5 * (tvWidth + tvMargin);
                        lp.topMargin = i / 5 * (tvHeight + tvMargin);
                        yiContainer.addView(textView, lp);
                    }
                    textView.setText(str);
                }
            }
        }, 300);

    }

    private ViewGroup bubbleLayout;
    ArrayList<ViewAnimator> animators = new ArrayList<ViewAnimator>();
    Timer timer = null;
    List<Point> pl = new ArrayList<>();

    public void onClick(View view) {
        if (view.getId() == R.id.btn) {


        } else {
            if (false) {
                flower();
                return;
            }

            View luckView = findViewById(R.id.luck_view);
            luckView.setVisibility(View.VISIBLE);
            setStatusBarColor(this, ContextCompat.getColor(this, R.color.transparent_bg));
            bubbleLayout = (ViewGroup) findViewById(R.id.animation_heart);
//            WaveView waveView = findViewById(R.id.waveView);
            KLoadingView loadingView = findViewById(R.id.loadingView);
            View btnView = findViewById(R.id.btn);
            View c1 = findViewById(R.id.circle1);
            View c2 = findViewById(R.id.circle2);
            View c3 = findViewById(R.id.circle3);
            btnView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ViewAnimator.animate(btnView)
                                .scale(1, 1.5f)
                                .decelerate()
                                .duration(500)
                                .start();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        c1.setVisibility(View.GONE);
                        c2.setVisibility(View.GONE);
                        c3.setVisibility(View.GONE);
                        int r = new Random().nextInt(yiList.size());
                        calendarData = yiList.get(r);
                        rs = calendarData.getYangli();

                        ViewAnimator.animate(btnView)
                                .scale(1.5f, 0)
                                .interpolator(new AnticipateInterpolator())
                                .duration(500)
                                .onStop(new AnimationListener.Stop() {
                                    @Override
                                    public void onStop() {
                                        onBubble();
//                                        waveView.setVisibility(View.VISIBLE);
//                                        waveView.start();
                                        loadingView.setVisibility(View.VISIBLE);
                                        loadingView.start();
                                        loadingView.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                loadingView.setVisibility(View.INVISIBLE);
                                                flower();

                                                TextView textView = findViewById(R.id.tv_random);
                                                textView.setVisibility(View.VISIBLE);
                                                textView.setText(rs);

                                                ViewAnimator.animate(textView)
                                                        .alpha(0, 1)
                                                        .decelerate()
                                                        .duration(2500)
                                                        .start();

                                                loadingView.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String[] dd = calendarData.getYangli().split("-");
                                                        mCalendarView.scrollToCalendar(Integer.parseInt(dd[0]), Integer.parseInt(dd[1]), Integer.parseInt(dd[2]));
                                                        ViewAnimator.animate(luckView)
                                                                .alpha(1, 0)
                                                                .decelerate()
                                                                .duration(1000)
                                                                .start();
                                                    }
                                                }, 8000);

                                            }
                                        }, 5000);
                                    }
                                })
                                .start();
                    }
                    return false;
                }
            });
            ViewAnimator.animate(luckView)
                    .translationY(-luckView.getHeight(), 0)
                    .interpolator(new BounceInterpolator())
                    .duration(1000)
                    .onStop(new AnimationListener.Stop() {
                        @Override
                        public void onStop() {
                            ViewAnimator.animate(btnView)
                                    .translationY(500, 0)
                                    .interpolator(new OvershootInterpolator())
                                    .duration(1000)
                                    .onStop(new AnimationListener.Stop() {
                                        @Override
                                        public void onStop() {
                                            c1.setVisibility(View.VISIBLE);
                                            c2.setVisibility(View.VISIBLE);
                                            c3.setVisibility(View.VISIBLE);
                                            animateCircle(c1, 0);
                                            animateCircle(c2, 1000);
                                            animateCircle(c3, 2000);
                                        }
                                    })
                                    .start();
                            btnView.setVisibility(View.VISIBLE);
                        }
                    })
                    .start();
            luckView.setVisibility(View.VISIBLE);
        }
    }

    private void flower() {

        FrameLayout lp = findViewById(R.id.flowerContainer);
        pl.add(new Point(-200, -100));
        pl.add(new Point(0, -200));
        pl.add(new Point(200, -100));
        pl.add(new Point(-130, 100));
        pl.add(new Point(130, 100));
        for (int i = 0; i < lp.getChildCount(); i++) {
            View fv = lp.getChildAt(i);
            if (i >= pl.size()) {
                break;
            }
            Point point = pl.get(i);
            fv.setVisibility(View.VISIBLE);
            ViewAnimator.animate(fv)
                    .translationX(0, point.x)
                    .translationY(0, point.y)
                    .rotation(0, 360)
                    .repeatCount(ViewAnimator.INFINITE)
                    .repeatMode(ViewAnimator.RESTART)
                    .alpha(1, 0)
                    .decelerate()
                    .duration(2500)
                    .start();
        }
    }

    private void animateCircle(View view, long delay) {
        ViewAnimator.animate(view)
                .repeatMode(ViewAnimator.RESTART)
                .repeatCount(ViewAnimator.INFINITE)
                .scale(1, 2)
                .alpha(1, 0)
                .decelerate()
                .duration(3000)
                .startDelay(delay)
                .start();
    }

    public void onBubble() {
        bubbleLayout.removeAllViews();
        for (ViewAnimator animator : animators) {
            animator.cancel();
        }
        animators.clear();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bubbleLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        addHeart();
                    }
                });
            }
        }, 0, 200);
    }

    private void addHeart() {
        if (animators.size() == 50) {
            timer.cancel();
            timer = null;
            return;
        }
        final ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageResource(R.mipmap.heart);
        bubbleLayout.addView(imageView);
        AnimationBuilder builder = ViewAnimator.animate(imageView);
        builder.path(PathUtils.createBubble(bubbleLayout.getWidth(), bubbleLayout.getHeight()));
        builder.fadeOut();
        builder.duration(3000);
        builder.repeatCount(ViewAnimator.INFINITE);
        builder.interpolator(new LinearInterpolator());
        ViewAnimator animator = builder.start();
        animators.add(animator);
    }
}