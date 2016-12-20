package app.rayscast.air.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by vokrut on 13.3.2016.
 */
public class AppDAO {

    private static AppDAO sIntance = null;
    private static final String TAG = AppDAO.class.getSimpleName();

    public static AppDAO getsIntance() {
        if (sIntance == null) {
            sIntance = new AppDAO();
        }
        return sIntance;
    }

    private void clearTable(String tableName, SQLiteDatabase db) {
        db.delete(tableName, null, null);
    }

    public void clearTable(Context context, String tableName) {
        SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();
        db.delete(tableName, null, null);
    }

    public int getPositionVideoFromDB(Context context, String fileName) {

        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
if(fileName!=null) {
    Cursor cursor = db.query(
            DatabaseContract.VideosTable.TABLE_NAME,
            null,
            DatabaseContract.VideosTable.COLUMN_FILE_NAME + "=?",
            new String[]{fileName},
            null,
            null,
            null);

    int position = 0;

    if (cursor.moveToNext()) {

        position = cursor.getInt(cursor.getColumnIndex(DatabaseContract.VideosTable.COLUMN_POSITION));

    }

    cursor.close();
    db.close();

    return position;
}
        return 0;
    }

    public void updatePositionVideo(Context context, String fileName, int positionPlayback) {
        SQLiteDatabase db = new DBOpenHelper(context).getWritableDatabase();
        db.beginTransaction();

        ContentValues values = new ContentValues();
        //values.put(DatabaseContract.VideosTable.COLUMN_FILE_NAME, fileName);
        values.put(DatabaseContract.VideosTable.COLUMN_POSITION, positionPlayback);

        String selection = DatabaseContract.VideosTable.COLUMN_FILE_NAME + " = ?";
        String[] selectionArgs = { fileName };

        int count = db.update(
                DatabaseContract.VideosTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count == 0) {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseContract.VideosTable.COLUMN_FILE_NAME, fileName);
            cv.put(DatabaseContract.VideosTable.COLUMN_POSITION, positionPlayback);
            db.insert(DatabaseContract.VideosTable.TABLE_NAME, null, cv);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();
    }

}
