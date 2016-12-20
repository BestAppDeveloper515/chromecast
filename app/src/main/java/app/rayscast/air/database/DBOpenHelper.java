package app.rayscast.air.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import app.rayscast.air.models.ItemWebURL;

/**
 * Created by vokrut on 13.3.2016.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "raycast.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String TABLE_CONTENT = "contenttable";
    private static final String TABLE_FAVORITES = "favTable";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "contentname";
    private static final String KEY_URL = "contentURL";


    private static final String KEY_FAV_ID = "id";
    private static final String KEY_FAV_NAME = "favPageTitle";
    private static final String KEY_FAV_URL = "favPageUrl";



    private static final String CREATE_CONTENT_TABLE = "CREATE TABLE " + TABLE_CONTENT + " (" +
            KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_URL + " TEXT);";


    private static final String CREATE_FAV_LINK_TABLE = "CREATE TABLE " + TABLE_FAVORITES + " (" +
            KEY_FAV_ID + " INTEGER PRIMARY KEY," + KEY_FAV_NAME + " TEXT," + KEY_FAV_URL + " TEXT);";

    private static final String SQL_CREATE_VIDEOS =
            "CREATE TABLE " + DatabaseContract.VideosTable.TABLE_NAME + " (" +
                    //DatabaseContract.PlaceTable._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.VideosTable.COLUMN_FILE_NAME + " TEXT_TYPE PRIMARY KEY," +
                    DatabaseContract.VideosTable.COLUMN_POSITION + INTEGER_TYPE +
                    ")";




    private static final String SQL_DELETE_VIDEOS =
            "DROP TABLE IF EXISTS " + DatabaseContract.VideosTable.TABLE_NAME;

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_VIDEOS);
        db.execSQL(CREATE_CONTENT_TABLE);
        db.execSQL(CREATE_FAV_LINK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_VIDEOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

        onCreate(db);
    }

    public long createWebContentWithItem(ItemWebURL webURL) {
        SQLiteDatabase p_db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, webURL.getContentName());
        values.put(KEY_URL, webURL.getContentURL());

        long tableID = p_db.insert(TABLE_CONTENT, null, values);
        return tableID;
    }

    public boolean isExistsInContent(String contentURL) {
        SQLiteDatabase p_db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CONTENT + " WHERE " + KEY_URL + " = '" + contentURL + "'";
        Cursor cursor = p_db.rawQuery(selectQuery, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public ItemWebURL getWebURL(String contentURL) {
        List<ItemWebURL> tmpWebContents = new ArrayList<ItemWebURL>();

        SQLiteDatabase p_db = this.getReadableDatabase();


        String selectQuery = "SELECT * FROM " + TABLE_CONTENT + " where " + KEY_URL + " = '" + contentURL + "'";

        Cursor cursor = p_db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ItemWebURL webURL = new ItemWebURL();

                    webURL.setContentName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                    webURL.setContentURL(cursor.getString(cursor.getColumnIndex(KEY_URL)));

                    tmpWebContents.add(webURL);
                } while (cursor.moveToNext());
            }
        }

        CloseDB();
        return tmpWebContents.get(0);
    }

    public List<ItemWebURL> getAllWebContents() {
        List<ItemWebURL> tmpWebContents = new ArrayList<ItemWebURL>();

        SQLiteDatabase p_db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CONTENT + " order by " + KEY_ID + " desc";

        Cursor cursor = p_db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ItemWebURL webURL = new ItemWebURL();

                    webURL.setContentName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                    webURL.setContentURL(cursor.getString(cursor.getColumnIndex(KEY_URL)));

                    tmpWebContents.add(webURL);
                } while (cursor.moveToNext());
            }
        }

        CloseDB();

        return tmpWebContents;
    }

    public void deleteWebContent(ItemWebURL webURL) {
        SQLiteDatabase p_db = getWritableDatabase();
        p_db.delete(TABLE_CONTENT, KEY_URL + " = ?", new String[]{webURL.getContentURL()});
    }


    public long addUrlToFavorite(ItemWebURL webURL) {
        SQLiteDatabase p_db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_FAV_NAME, webURL.getContentName());
        values.put(KEY_FAV_URL, webURL.getContentURL());

        long tableID = p_db.insert(TABLE_FAVORITES, null, values);
        return tableID;
    }

    public List<ItemWebURL> getAllFavorites() {
        List<ItemWebURL> favList = new ArrayList<ItemWebURL>();

        SQLiteDatabase p_db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITES + " order by " + KEY_ID + " desc";

        Cursor cursor = p_db.rawQuery(selectQuery, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    ItemWebURL webURL = new ItemWebURL();

                    webURL.setContentName(cursor.getString(cursor.getColumnIndex(KEY_FAV_NAME)));
                    webURL.setContentURL(cursor.getString(cursor.getColumnIndex(KEY_FAV_URL)));

                    favList.add(webURL);
                } while (cursor.moveToNext());
            }
        }

        CloseDB();

        return favList;
    }


    public void deletFavorite(ItemWebURL webURL) {
        SQLiteDatabase p_db = getWritableDatabase();
        p_db.delete(TABLE_FAVORITES, KEY_FAV_URL + " = ?", new String[]{webURL.getContentURL()});
    }


    public void deleteAllWebContents() {
        SQLiteDatabase p_database = this.getWritableDatabase();
        p_database.execSQL("delete from " + TABLE_CONTENT);
    }

    public void CloseDB() {
        SQLiteDatabase p_databse = this.getReadableDatabase();
        if (p_databse != null && p_databse.isOpen())
            p_databse.close();
    }

    public void deleteAllFavoritesContents(){
        SQLiteDatabase p_database=this.getWritableDatabase();
        p_database.execSQL("delete from "+TABLE_FAVORITES);
        Log.e("Table Favorite Dropped",": + ");
    }
}
