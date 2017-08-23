package jason.tuchler.com.popularmovies;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class MoviesContentProvider extends ContentProvider {
    static final String PROVIDER_NAME = "jason.tuchler.com.popularmovies.MoviesContentProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/Movie";
    static final Uri CONTENT_URI = Uri.parse(URL);

    private static HashMap<String, String> MOVIES_PROJECTION_MAP;

    static final String _ID = "_id";
    static final String COLUMN_NAME_IMAGEPATH = "imagePath";
    static final String COLUMN_NAME_MOVIEID = "movieId";
    static final String COLUMN_NAME_OVERVIEW = "overview";
    static final String COLUMN_NAME_RELEASEDATE = "releaseDate";
    static final String COLUMN_NAME_TITLE = "title";
    static final String COLUMN_NAME_VOTECOUNT = "voteCount";

    static final int MOVIE = 1;
    static final int MOVIE_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "Movie", MOVIE);
        uriMatcher.addURI(PROVIDER_NAME, "Movie/#", MOVIE_ID);
    }

    private SQLiteDatabase db;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Movie.db";
    public static final String TABLE_NAME = "Movie";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_IMAGEPATH + " TEXT," +
                    COLUMN_NAME_MOVIEID + " TEXT," +
                    COLUMN_NAME_OVERVIEW + " TEXT," +
                    COLUMN_NAME_RELEASEDATE + " TEXT," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_VOTECOUNT + " TEXT)";


    public static class FavoriteMovieDBHelper extends SQLiteOpenHelper {

        public FavoriteMovieDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        FavoriteMovieDBHelper dbHelper = new FavoriteMovieDBHelper(context);

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOVIE:
                qb.setProjectionMap(MOVIES_PROJECTION_MAP);
                break;

            case MOVIE_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }


        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = COLUMN_NAME_MOVIEID;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        switch (uriMatcher.match(uri)){
            /**
             * Get all Movie records
             */
            case MOVIE:
                return "vnd.android.cursor.dir/vnd.example.Movie";
            /**
             * Get a particular Movie
             */
            case MOVIE_ID:
                return "vnd.android.cursor.item/vnd.example.Movie";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long rowID = db.insert(TABLE_NAME, "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)){
            case MOVIE:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;

            case MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                count = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOVIE_ID:
                count = db.update(TABLE_NAME, values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
