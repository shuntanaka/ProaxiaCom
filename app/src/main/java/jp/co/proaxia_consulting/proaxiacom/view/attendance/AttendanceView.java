package jp.co.proaxia_consulting.proaxiacom.view.attendance;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Calendar;
import java.util.Locale;

import jp.co.proaxia_consulting.proaxiacom.R;
import jp.co.proaxia_consulting.proaxiacom.util.AnimationHelper;
import jp.co.proaxia_consulting.proaxiacom.view.FixableViewFlipper;

/**
 * 勤怠表クラス
 */
public class AttendanceView extends LinearLayout {

    private static final Animation inFromLeft = AnimationHelper.inFromLeftAnimation();
    private static final Animation outToRight = AnimationHelper.outToRightAnimation();
    private static final Animation inFromRight = AnimationHelper.inFromRightAnimation();
    private static final Animation outToLeft = AnimationHelper.outToLeftAnimation();

    private static final Animation inFromTop = AnimationHelper.inFromTopAnimation();
    private static final Animation outToBottom = AnimationHelper.outToBottomAnimation();
    private static final Animation inFromBottom = AnimationHelper.inFromBottomAnimation();
    private static final Animation outToTop = AnimationHelper.outToTopAnimation();

    private static final int DIRECTION_HORIZONTAL = 1;
    private static final int DIRECTION_VERTICAL = 0;

    protected java.util.Calendar calendar = java.util.Calendar.getInstance();
    protected ViewFlipper viewFlipper;
    protected GestureDetector detector;
    protected MonthlyAttendanceView mViewPrevious;
    protected MonthlyAttendanceView mViewNext;
    protected float lastTouchX;
    protected float lastTouchY;
    protected TextView txtHeader;

    //各色のデフォルト値 (デザイン時に使用する)
    protected int c_backgroud = Color.parseColor("#f0ffffff");
    protected int c_foregroud = Color.parseColor("#ff000000");
    protected int c_dark = Color.parseColor("#6456648f");
    protected int c_hilite = Color.parseColor("#ffffffff");
    protected int c_light = Color.parseColor("#64c6d4ef");
    protected int c_holidaty = Color.parseColor("#ffFF0000");
    protected int c_saturday = Color.parseColor("#ff0000FF");
    protected int c_selected = Color.parseColor("#64FFA500");

    protected static final String[] monthNames;// = new DateFormatSymbols().getShortMonths();
    static {
        monthNames = new String[] {
                DateUtils.getMonthString(Calendar.JANUARY, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.FEBRUARY, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.MARCH, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.APRIL, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.MAY, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.JUNE, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.JULY, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.AUGUST, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.SEPTEMBER, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.OCTOBER, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.NOVEMBER, DateUtils.LENGTH_LONG),
                DateUtils.getMonthString(Calendar.DECEMBER, DateUtils.LENGTH_LONG)
        };
        //new DateFormatSymbols().getShortWeekdays();
    }
    /**
     * コンストラクタ
     * @param context 親のコンテキストをセット
     */
    public AttendanceView(Context context) {
        super(context);
        this.init(context, null);
    }
    /**
     * コンストラクタ
     * @param context 親のコンテキストをセット
     * @param attrs 外部(XML)から取り込むアトリビュートをセット
     */
    public AttendanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    /**
     *  コンポーネントの初期化を実施します
     * @param context コンテキストをセット
     * @param attrs アトリビュートをセット
     */
    private void init(Context context, AttributeSet attrs) {

        //フレームワークリソースから色を取得
        Resources res = context.getResources();
        if (res != null) {
            this.c_backgroud = res.getColor(R.color.calendar_background);
            this.c_foregroud = res.getColor(R.color.calendar_foreground);
            this.c_dark = res.getColor(R.color.calendar_dark);
            this.c_hilite = res.getColor(R.color.calendar_hilite);
            this.c_light = res.getColor(R.color.calendar_light);
            this.c_holidaty = res.getColor(R.color.calendar_holiday);
            this.c_saturday = res.getColor(R.color.calendar_saturday);
            this.c_selected = res.getColor(R.color.calendar_selected);
        }

        this.setClickable(true);

        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.TOP | Gravity.CENTER);
        this.setBackgroundColor(this.c_backgroud);
        this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT
                , LayoutParams.FILL_PARENT));

        //ナビゲーションバー
        LinearLayout navBar = new LinearLayout(context);
        navBar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT
                , LayoutParams.WRAP_CONTENT));
        navBar.setPadding(1, 1, 1, 1);
        navBar.setBackgroundColor(this.c_backgroud);
        {
            // << ボタン
            Button btnBack = new Button(context, attrs);
            btnBack.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT
                    , LayoutParams.WRAP_CONTENT, 5)); //末尾のパラメタはweight
            btnBack.setBackgroundColor(Color.parseColor("#00000000"));
            btnBack.setText("<<");
            btnBack.setFocusable(false);
            btnBack.setFocusableInTouchMode(false);

            btnBack.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar.add(Calendar.MONTH, -1);
                    showPreviousMonth(DIRECTION_HORIZONTAL);
                }
            });

            navBar.addView(btnBack);

            //ヘッダ (年月を表示する)
            this.txtHeader = new TextView(context, attrs);
            this.txtHeader.setGravity(Gravity.CENTER);
            //this.txtHeader.setBackgroundColor(this.c_backgroud);
            this.txtHeader.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT
                    , LayoutParams.FILL_PARENT, 80)); //末尾のパラメタはweight
            this.txtHeader.setTextColor(this.c_foregroud);
            this.txtHeader.setBackgroundColor(Color.parseColor("#00000000"));// this.c_backgroud);
            this.txtHeader.setTextSize(20f);
            this.txtHeader.setTypeface(Typeface.SANS_SERIF);

            this.setHeader(this.calendar.get(java.util.Calendar.YEAR)
                    , this.calendar.get(java.util.Calendar.MONTH));

            this.txtHeader.setFocusable(true);
            navBar.addView(this.txtHeader);

            // >> ボタン
            Button btnFwd = new Button(context, attrs);
            btnFwd.setLayoutParams(
                    new LayoutParams(LayoutParams.WRAP_CONTENT
                            , LayoutParams.WRAP_CONTENT, 5)); //末尾のパラメタはweight
            btnFwd.setBackgroundColor(Color.parseColor("#00000000"));
            btnFwd.setFocusable(false);
            btnFwd.setFocusableInTouchMode(false);
            btnFwd.setText(">>");
            btnFwd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    calendar.add(Calendar.MONTH, 1);
                    showNextMonth(DIRECTION_HORIZONTAL);
                }
            });
            navBar.addView(btnFwd);
        }
        this.addView(navBar);

        //ビューフリッパーの追加
        this.viewFlipper = new FixableViewFlipper(context);
        this.viewFlipper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT
                , LayoutParams.FILL_PARENT));

        {
            //カレンダービューの追加
            this.mViewPrevious = new MonthlyAttendanceView(context);
            this.mViewPrevious.setCalendar(this.calendar);
            this.mViewPrevious.setBackgroundColor(this.c_backgroud);
            this.viewFlipper.addView(this.mViewPrevious);

            //カレンダービューの追加
            this.mViewNext = new MonthlyAttendanceView(context);
            this.mViewNext.setCalendar(this.calendar);
            this.mViewNext.setBackgroundColor(this.c_backgroud);
            this.viewFlipper.addView(this.mViewNext);
        }

        this.addView(this.viewFlipper);
    }
    /**
     * 前月を表示する
     * @param direction モーションの方向をセット(DIRECTION_VERTICAL|DIRECTION_HORIZONTAL)
     */
    private void showPreviousMonth(int direction) {
        int index = this.viewFlipper.getDisplayedChild() == 0 ? 1 : 0;
        MonthlyAttendanceView calendarView =
                (MonthlyAttendanceView)this.viewFlipper.getChildAt(index);
        if ( calendarView != null ) {
            calendarView.setCalendar(this.calendar);
            this.setHeader(this.calendar.get(java.util.Calendar.YEAR)
                    , calendar.get(java.util.Calendar.MONTH));
            if ( direction == DIRECTION_VERTICAL ) {
                this.viewFlipper.setInAnimation(inFromTop);
                this.viewFlipper.setOutAnimation(outToBottom);
            } else {
                this.viewFlipper.setInAnimation(inFromLeft);
                this.viewFlipper.setOutAnimation(outToRight);
            }
            this.viewFlipper.showPrevious();
        }
    }
    /**
     * 次月を表示する
     * @param direction モーションの方向をセット(DIRECTION_VERTICAL|DIRECTION_HORIZONTAL)
     */
    private void showNextMonth(int direction) {
        int index = this.viewFlipper.getDisplayedChild() == 0 ? 1 : 0;
        MonthlyAttendanceView calendarView =
                (MonthlyAttendanceView)this.viewFlipper.getChildAt(index);
        if ( calendarView != null ) {
            calendarView.setCalendar(this.calendar);
            this.setHeader(this.calendar.get(java.util.Calendar.YEAR)
                    , calendar.get(java.util.Calendar.MONTH));

            if ( direction == DIRECTION_VERTICAL ) {
                this.viewFlipper.setInAnimation(inFromBottom);
                this.viewFlipper.setOutAnimation(outToTop);
            } else {
                this.viewFlipper.setInAnimation(inFromRight);
                this.viewFlipper.setOutAnimation(outToLeft);
            }
            this.viewFlipper.showNext();
        }
    }
    /**
     * ヘッダの年月を設定します
     * @param year 年をセット
     * @param month 月をセット
     */
    private void setHeader(int year, int month) {
        this.txtHeader.setText(year
                + (Locale.getDefault().equals(Locale.JAPAN) ? "年" : " ")

                + monthNames[month]);
    }
}
