package com.adapei.navhelper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The database and the procedure of creation, deletion. It is extended from SQLiteOpenHelper.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "database";

    // Table Names
    private static final String TABLE_CHECKPOINTS = "checkpoints";
    private static final String TABLE_DESTINATIONS = "destinations";
    private static final String TABLE_PHONENUMBERS = "phoneNumbers";

    // PHONE NUMBER TABLE
    private static final String KEY_PHONENUMBER = "phoneNumber";
    private static final String KEY_NAMENUMBER = "nameNumber";

    // DESTINATION AND CHECKPOINT TABLE
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_ICONE = "icone";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LONG = "longitude";
    private static final String KEY_PRONON = "prononciation";
    private static final String KEY_CITY = "foyer";


    private static final String CREATE_TABLE_PHONENUMBERS =
            "CREATE TABLE "
            + TABLE_PHONENUMBERS + "("
            + KEY_PHONENUMBER + " TEXT, "
            + KEY_NAMENUMBER + " TEXT);";

    private static final String CREATE_TABLE_DESTINATIONS =
            "CREATE TABLE "
            + TABLE_DESTINATIONS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_NICKNAME + " TEXT, "
            + KEY_ICONE + " TEXT, "
            + KEY_LAT + " REAL, "
            + KEY_LONG + " REAL, "
            + KEY_PRONON + " TEXT, "
            + KEY_CITY + " TEXT);";

    private static final String CREATE_TABLE_CHECKPOINTS =
            "CREATE TABLE "
            + TABLE_CHECKPOINTS + "("
            + KEY_LAT + " REAL, "
            + KEY_LONG + " REAL, "
            + KEY_NAME + " TEXT, "
            + KEY_PRONON + " TEXT, "
            + KEY_ICONE + " TEXT PRIMARY KEY);";

    /**
     * Constructor of BusSQLiteDBHelper.
     * @param context Information about the application environment.
     * @param databaseName Database's name.
     */
    public SQLiteHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_PHONENUMBERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_DESTINATIONS);
        sqLiteDatabase.execSQL(CREATE_TABLE_CHECKPOINTS);
    }

    @Override
    /**
     * Drop older tables and update it with the new versions.
     */
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONENUMBERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHECKPOINTS);

        // create new tables
        onCreate(sqLiteDatabase);
    }
}
