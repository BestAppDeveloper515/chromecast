package app.rayscast.air.database;

import android.provider.BaseColumns;

/**
 * Created by vokrut on 13.3.2016.
 */
public final class DatabaseContract {
    public DatabaseContract() {
    }

    public static abstract class VideosTable implements BaseColumns {
        public static final String TABLE_NAME = "videos";
        public static final String COLUMN_FILE_NAME = "file_name";
        public static final String COLUMN_POSITION = "position";
    }

}
