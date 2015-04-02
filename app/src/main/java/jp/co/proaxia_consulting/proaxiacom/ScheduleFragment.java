package jp.co.proaxia_consulting.proaxiacom;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import jp.co.proaxia_consulting.proaxiacom.database.DBAdapter;
import jp.co.proaxia_consulting.proaxiacom.view.calendar.CalendarSelectionEvent;
import jp.co.proaxia_consulting.proaxiacom.view.calendar.CalendarView;
import jp.co.proaxia_consulting.proaxiacom.view.calendar.DateInfo;
import jp.co.proaxia_consulting.proaxiacom.view.calendar.OnCalendarSelectionListener;
import jp.co.proaxia_consulting.proaxiacom.view.dialog.SingleChoiceDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleFragment extends Fragment implements OnCalendarSelectionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";

    // イベントダイアログのイベントを設定
    private final String[] items = new String[] {"", "オフィス", "日立", "IBM", "出張", "休み"};
    private final String[] items_fukuoka = new String[] {"", "江崎", "上籠", "田中洋", "作永", "田中俊","吉村","小森","戸上"};
    private String singleSelectItem = "";
    private DBAdapter dbAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance(int sectionNumber) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        dbAdapter = new DBAdapter(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        //カレンダービューを取得
        CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.Calendar);

        //カレンダ選択監視リスナを設定
        calendarView.addOnCalendarSelectionListener(this);

        //拠点ボタンリスナ設定
        Button btn = (Button)rootView.findViewById(R.id.btn_fukuoka);
        btn.setOnClickListener(new onFukuoka());

        return rootView;
    }

    /*
        拠点ボタンクリックリスナー
     */
    class onFukuoka implements OnClickListener{
        public void onClick(View v){
            final int checkedItem = 0;
            singleSelectItem = "";

            //イベントダイアログを表示
            SingleChoiceDialog dialogFragment = SingleChoiceDialog
                    .newInstance(R.string.fukuoka, items_fukuoka, checkedItem);

            //OKボタンが押されたとき
            dialogFragment.setOnOkClickListener(new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                }
            });
            //キャンセルボタンが押されたとき
            dialogFragment.setOnCancelClickListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialogFragment.show(getFragmentManager(), "aaa");

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached( getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /*
        カレンダーが選択された際に呼ばれるメソッド
     */
    public void onCalendarSelection(CalendarSelectionEvent event) {

        final DateInfo dateInfo = event.getDateInfo();
        final CalendarView calendarView = event.getCalendarView();
        final int checkedItem = 0;
        singleSelectItem = "";

        //イベントダイアログを表示
        SingleChoiceDialog dialogFragment = SingleChoiceDialog
                .newInstance(R.string.app_name, items, checkedItem);

        //リストが選択されたとき
        dialogFragment.setOnSingleChoiceClickListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                singleSelectItem = items[item];
            }
        });

        //OKボタンが押されたとき
        dialogFragment.setOnOkClickListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dbAdapter.open();
                dbAdapter.saveEvent(dateInfo.getYMD(), singleSelectItem);
                dbAdapter.close();
                calendarView.setCalendarDraw(dateInfo.getYear(), dateInfo.getMonth(), dateInfo.getDay());
            }
        });
        //キャンセルボタンが押されたとき
        dialogFragment.setOnCancelClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialogFragment.show(getFragmentManager(), "aaa");

    }

}
