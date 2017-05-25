package be.t_ars.busyparkinglot.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by samsung on 25/05/2017.
 */

public class LotOpenHelper extends SQLiteOpenHelper {
    private static final String kDATABASE_NAME = "lot";
    private static final int kDATABASE_VERSION = 1;
    private static final String kLEVELS_TABLE_NAME = "levels";
    private static final String kKEY_LEVEL = "level";
    private static final String kLEVELS_TABLE_CREATE =
            "CREATE TABLE " + kLEVELS_TABLE_NAME + " (" +
                    kKEY_LEVEL + " INTEGER);";

    public LotOpenHelper(final Context context) {
        super(context, kDATABASE_NAME, null, kDATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(kLEVELS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
    }

    public Set<Integer> getFinishedLevels() {
        final Cursor result = getReadableDatabase().query(kLEVELS_TABLE_NAME, null, null, null, null, null, null);
        final Set<Integer> levels = new HashSet<>();
        while (result.moveToNext()) {
            levels.add(Integer.valueOf(result.getInt(0)));
        }
        return levels;
    }

    public void levelFinished(final int level) {
        final ContentValues values = new ContentValues(1);
        values.put(kKEY_LEVEL, Integer.valueOf(level));
        getWritableDatabase().insert(kLEVELS_TABLE_NAME, null, values);
    }
}
