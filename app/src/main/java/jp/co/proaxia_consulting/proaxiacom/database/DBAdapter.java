package jp.co.proaxia_consulting.proaxiacom.database;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends Application {

    static final String DATABASE_NAME = "Proaxia.db";
    static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "ScheduleTable";
    public static final String COL_ID = "_id";
    public static final String COL_DATE = "date";
    public static final String COL_EVENT = "event";

    public static Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    private static DBAdapter sInstance;

    public DBAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
        db = dbHelper.getReadableDatabase();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static synchronized DBAdapter getInstance() {
        return sInstance;
    }
    //
    // SQLiteOpenHelper
    //
    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME + " ("
                            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_DATE + " TEXT NOT NULL,"
                            + COL_EVENT + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(
                SQLiteDatabase db,
                int oldVersion,
                int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    //
    // Adapter Methods
    //
    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    //
    // App Methods
    //
    public void saveEvent(String sdate, String sEvent){
        Integer nid = 0;
        ContentValues values = new ContentValues();
        values.put(COL_DATE, sdate);
        values.put(COL_EVENT, sEvent);
        nid = getEventCount(sdate);
        if(nid == 0){
            if (!"".equals(sEvent)){
                db.insertOrThrow(TABLE_NAME, null, values);
            }
        }else{
            if (!"".equals(sEvent)) {
                db.update(TABLE_NAME, values, COL_ID.concat(" = ").concat(Integer.toString(nid)), null);
            }else{
                db.delete(TABLE_NAME,COL_ID.concat(" = ").concat(Integer.toString(nid)), null);
            }
        }
    }
    /**
     * 日付文字列からイベント名を取得します
     * @param year 年をセット
     * @param month 月をセット
     * @param day 日をセット
     * @return String イベント名が戻ります(イベントが無い場合、空文字が戻ります)
     */
    public Integer getEventCount(String sdate) {

        StringBuilder sql = new StringBuilder();
        Integer nCount = 0;
        sql.append(" SELECT ");
        sql.append(COL_ID);
        sql.append(" FROM ");
        sql.append(TABLE_NAME);
        sql.append(" WHERE ");
        sql.append(COL_DATE);
        sql.append(" = ");
        sql.append(sdate);

        Cursor cursor = db.rawQuery(sql.toString(), null);
        if(cursor.moveToFirst()){
            nCount = cursor.getInt(0);
        }else{
            nCount = 0;
        }
        cursor.close();

        return nCount;
    }
    /**
     * 日付文字列からイベント名を取得します
     * @param 年月日(yyymmdd)をセット
     * @return String イベント名が戻ります(イベントが無い場合、空文字が戻ります)
     */
    public String getEventName(String sdate) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ");
        sql.append(COL_EVENT);
        sql.append(" FROM ");
        sql.append(TABLE_NAME);
        sql.append(" WHERE ");
        sql.append(COL_DATE);
        sql.append(" = ");
        sql.append(sdate);

        Cursor cursor = db.rawQuery(sql.toString(), null);
        StringBuilder sb = new StringBuilder();
        if(cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                sb.append(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return sb.toString();
    }
}

