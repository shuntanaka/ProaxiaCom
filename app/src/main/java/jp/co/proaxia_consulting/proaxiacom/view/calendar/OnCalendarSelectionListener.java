package jp.co.proaxia_consulting.proaxiacom.view.calendar;

import java.util.EventListener;


/**
 * カレンダーが選択されたイベントの監視を行うリスナインタフェースを提供します
 * 
 * @author Kazzz
 * @since JDK1.5 Android Level 4
 *
 */

public interface OnCalendarSelectionListener extends EventListener {
    /**
     * カレンダーが選択された際に呼ばれるリスナ通知メソッド
     * @param event 発生したイベント
     */
    void onCalendarSelection(CalendarSelectionEvent event);

}
