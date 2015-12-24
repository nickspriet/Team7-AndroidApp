package com.howest.nmct.bob.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.howest.nmct.bob.data.EventsContract.EventEntry;
import com.howest.nmct.bob.data.EventsContract.PlaceEntry;

/**
 * illyism
 * 22/12/15
 */
public class EventProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EventsDbHelper mOpenHelper;

    static final int EVENT = 100;
    static final int EVENT_ID = 101;
    static final int PLACE = 300;
    static final int PLACE_ID = 301;

    static final String sEventWithPlace = EventEntry.TABLE_NAME + " LEFT OUTER JOIN " +
            PlaceEntry.TABLE_NAME +
            " ON " + EventEntry.COLUMN_PLACE_ID +
            " = " + PlaceEntry.TABLE_NAME +
            "." + PlaceEntry._ID;

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(EventsContract.CONTENT_AUTHORITY, EventsContract.PATH_EVENT, EVENT);
        uriMatcher.addURI(EventsContract.CONTENT_AUTHORITY, EventsContract.PATH_EVENT + "/*", EVENT_ID);
        uriMatcher.addURI(EventsContract.CONTENT_AUTHORITY, EventsContract.PATH_PLACE, PLACE);
        uriMatcher.addURI(EventsContract.CONTENT_AUTHORITY, EventsContract.PATH_PLACE + "/*", PLACE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new EventsDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case EVENT:
                return EventEntry.CONTENT_TYPE;
            case EVENT_ID:
                return EventEntry.CONTENT_ITEM_TYPE;
            case PLACE:
                return PlaceEntry.CONTENT_TYPE;
            case PLACE_ID:
                return PlaceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "event/*"
            case EVENT_ID:
            {
                retCursor = getEventById(uri, projection, selection, selectionArgs, sortOrder);
                break;
            }
            // "place"
            case PLACE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "event"
            case EVENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        sEventWithPlace,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getEventById(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(sEventWithPlace);
        qb.appendWhere("(" + EventEntry.TABLE_NAME + "." + EventEntry._ID + " = " + uri.getPathSegments().get(1) + ")");
        return qb.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EVENT: {
                long _id = db.insert(EventEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EventEntry.buildEventUri(Long.toString(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PLACE: {
                long _id = db.insert(PlaceEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = PlaceEntry.buildPlaceUri(Long.toString(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int deletedRows;

        // also return amount of rows deleted on null
        if (null == selection) selection = "1";
        switch (match) {
            case EVENT:
                deletedRows = db.delete(
                        EventEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case PLACE:
                deletedRows = db.delete(
                        PlaceEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int updatedRows = 0;

        switch (match) {
            case EVENT:
                updatedRows = db.update(
                        EventEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case PLACE:
                updatedRows = db.update(
                        PlaceEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case EVENT:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(EventEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PLACE:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(PlaceEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
