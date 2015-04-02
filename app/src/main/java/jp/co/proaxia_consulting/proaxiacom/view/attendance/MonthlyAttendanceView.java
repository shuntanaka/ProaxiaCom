package jp.co.proaxia_consulting.proaxiacom.view.attendance;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

import jp.co.proaxia_consulting.proaxiacom.R;
import jp.co.proaxia_consulting.proaxiacom.view.calendar.DateInfo;

/**
 * Created by user on 2015/03/24.
 */
public class MonthlyAttendanceView extends View {
    protected static final String TAG = MonthlyAttendanceView.class.getSimpleName();
    protected static final String SELECT_COL = "selectCol";
    protected static final String SELECT_ROW = "selectRow";
    protected static final String VIEW_STATE = "viewState";
    protected static final int DEF_WIDTH = 320; //HT-03A他
    protected static final int DEF_HEIGHT_CAPTION = (DEF_WIDTH / 7)/2; //曜日表示部の高さ
    protected static final int DEF_HEIGHT = DEF_WIDTH - DEF_HEIGHT_CAPTION;

    protected float cellWidth;    // セルの横の長さ
    protected float cellHeight;   // セルの縦の長さ
    protected float captionHeight;// 曜日セルの高さ
    protected int selCol;         // 選択されたセルの列の添字
    protected int selRow;         // 選択されたセルの行の添字
    protected final Rect selRect = new Rect();

    //内部カレンダ
    protected Calendar calendar = Calendar.getInstance();
    protected int today = calendar.get(Calendar.DAY_OF_MONTH);
    protected DateInfo[][] matrix = new DateInfo[6][7]; //[週][日]

    //各色のデフォルト値 (デザイン時に使用する)
    protected int c_backgroud = Color.parseColor("#f0ffffff");
    protected int c_foregroud = Color.parseColor("#ff000000");
    protected int c_dark = Color.parseColor("#6456648f");
    protected int c_hilite = Color.parseColor("#ffffffff");
    protected int c_light = Color.parseColor("#64c6d4ef");
    protected int c_holidaty = Color.parseColor("#ffFF0000");
    protected int c_saturday = Color.parseColor("#ff0000FF");
    protected int c_selected = Color.parseColor("#64FFA500");

    //各種描画情報
    protected Paint background = new Paint();
    protected Paint dark = new Paint();
    protected Paint hilite = new Paint();
    protected Paint light = new Paint();
    protected Paint weekdayText = new Paint(Paint.SUBPIXEL_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
    protected Paint holidayText = new Paint(Paint.SUBPIXEL_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
    protected Paint selected = new Paint();
    protected Paint.FontMetrics metricsForD;
    protected Paint.FontMetrics metricsForH;

    //ロケール舞に曜日名の配列を取得しておく
    protected static final String[] weekdays;
    static {
        weekdays = new String[] {
                DateUtils.getDayOfWeekString(Calendar.SUNDAY, DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.MONDAY, DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.TUESDAY, DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.WEDNESDAY, DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.THURSDAY, DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.FRIDAY, DateUtils.LENGTH_MEDIUM),
                DateUtils.getDayOfWeekString(Calendar.SATURDAY, DateUtils.LENGTH_MEDIUM),
        };
        //new DateFormatSymbols().getShortWeekdays();
    }
    /**
     * コンストラクタ (デザイン時はこちらが呼ばれる)
     * @param context
     */
    public MonthlyAttendanceView(Context context) {
        super(context);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setClickable(true);
        this.initResource(null);
    }
    /**
     * コンストラクタ
     * @param context コンテキストをセット
     * @param attrs 外部(XML)から取り込むアトリビュートをセット
     */
    public MonthlyAttendanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setClickable(true);
        this.initResource(attrs);
    }
    /**
     * リソースを初期化する
     */
    private void initResource(AttributeSet attrs) {
        //フレームワークリソースから色を取得
        Resources res = this.getContext().getResources();
        if ( res != null ) {
            this.c_backgroud = res.getColor(R.color.calendar_background);
            this.c_foregroud = res.getColor(R.color.calendar_foreground);
            this.c_dark = res.getColor(R.color.calendar_dark);
            this.c_hilite = res.getColor(R.color.calendar_hilite);
            this.c_light = res.getColor(R.color.calendar_light);
            this.c_holidaty = res.getColor(R.color.calendar_holiday);
            this.c_saturday = res.getColor(R.color.calendar_saturday);
            this.c_selected = res.getColor(R.color.calendar_selected);
        }

        /**
         * 描画情報の初期化
         */

        //色の設定
        this.background.setColor(this.c_backgroud);
        this.dark.setColor(this.c_dark);
        this.hilite.setColor(this.c_hilite);
        this.light.setColor(this.c_light);
        this.selected.setColor(this.c_selected);
        this.holidayText.setColor(this.c_holidaty);

        //カレンダマトリクスの計算
        this.calcCalendarMatrix();
    }
    /**
     * カレンダーを計算する
     */
    protected void calcCalendarMatrix() {
        // 月の初めの曜日を取得

        //配列クリア
        for ( int row = 0; row < 5; row++ ) {
            for ( int col = 0; col < 31; col++ ) {
                this.matrix[row][col] = null;
            }
        }
        calendar.set(Calendar.DATE, 1); //初日にセット
        int startDay = calendar.get(Calendar.DAY_OF_WEEK); //曜日

        // 月末の日付を取得(次の月の初日-1日)
        this.calendar.add(Calendar.MONTH, 1);
        this.calendar.add(Calendar.DATE, -1);
        int lastDate = calendar.get(Calendar.DATE);

        // マトリクス生成
        int row = 0;
        int column = startDay - 1; // 曜日は1オリジンなので-1する: 日曜日 = 1, 月曜日 = 2, ...
        for (int date = 1; date <= lastDate; date++) {
            this.matrix[row][column] = new DateInfo(
                    this.calendar.get(Calendar.YEAR)
                    , this.calendar.get(Calendar.MONTH) + 1 //月は0オリジン
                    , date
            );
            if (column == 4) {
                row++;
                column = 0;
            } else {
                column++;
            }
        }
        this.invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        //描画対象の幅と高さを取得しておく
        int width = this.getWidth();
        int height = this.getHeight();

        //背景を描画
        //canvas.drawRect(0, 0, width, height , this.background);

        // セル目を区切る線を描画
        float offsetY = 0;
        for (int i = 0; i < 5; i++) {
            // |
            canvas.drawLine(i * this.cellWidth, 0
                    , i * this.cellWidth, height, this.light);
            // ||
            canvas.drawLine(i * this.cellWidth + 1, 0
                    , i * this.cellWidth + 1, height, this.hilite);

            switch (i) {
                case 0:
                    offsetY =  0;
                    break;
                case 1:
                    offsetY +=  this.captionHeight;
                    break;

                default:
                    offsetY +=  this.cellHeight;
                    break;
            }

            // --
            canvas.drawLine(0, offsetY, width
                    , offsetY, this.dark);
            // ==
            canvas.drawLine(0, offsetY + 1
                    , width, offsetY + 1, this.hilite);

        }

        canvas.drawRect(this.selRect, this.selected);

    }
    /**
     * calendarを設定します
     * @param calendar calendarをセットします
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        this.calcCalendarMatrix();
    }
}
