package com.example.peerajak.habittracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import com.example.peerajak.habittracker.data.MealContact.MealEntry;
import android.database.sqlite.SQLiteDatabase;
import com.example.peerajak.habittracker.MealDbhelper;
import android.util.Log;
/**
 * Created by peerajak on 11/22/17.
 */

/**
 * {@link ContentProvider} for Meals app.
 */
public class MealProvider extends ContentProvider {
    /** Tag for the log messages */
    private MealDbhelper mDbHelper;
    public static final String LOG_TAG = MealProvider.class.getSimpleName();
    private static final int MEALS = 100;
    private static final int MEAL_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(MealContact.CONTENT_AUTHORITY,MealContact.PATH_MEALS,MEALS);
        sUriMatcher.addURI(MealContact.CONTENT_AUTHORITY,MealContact.PATH_MEALS+"/#",MEAL_ID);
    }
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a MealDbHelper object to gain access to the pets database.
        mDbHelper = new MealDbhelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MEALS:
                cursor = database.query(MealEntry.TABLE_NAME,projection,selection,selectionArgs
                        ,null,null,sortOrder);
                break;
            case MEAL_ID:
                // For the MEAL_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = MealEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(MealEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEALS:
                return insertMeal(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertMeal(Uri uri, ContentValues values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        String name = values.getAsString(MealEntry.COLUMN_MEAL_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Meal requires a name");
        }
        Integer drinktype = values.getAsInteger(MealEntry.COLUMN_MEAL_DRINKS);
        if (drinktype == null || !MealEntry.isValidDrink(drinktype)) {
            throw new IllegalArgumentException("Meal requires valid drinktype");
        }
        Integer weight = values.getAsInteger(MealEntry.COLUMN_MEAL_TRANS);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Meal requires valid weight");
        }
        long id = database.insert(MealEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }
    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEALS:
                return updateMeal(uri, contentValues, selection, selectionArgs);
            case MEAL_ID:
                // For the MEAL_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = MealEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateMeal(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateMeal(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected pets in the pets database table with the given ContentValues
        if (values.containsKey(MealEntry.COLUMN_MEAL_NAME)) {
            String name = values.getAsString(MealEntry.COLUMN_MEAL_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Meal requires a name");
            }
        }
        if (values.containsKey(MealEntry.COLUMN_MEAL_DRINKS)) {
            Integer drinktype = values.getAsInteger(MealEntry.COLUMN_MEAL_DRINKS);
            if (drinktype == null || !MealEntry.isValidDrink(drinktype)) {
                throw new IllegalArgumentException("Meal requires valid drinktype");
            }
        }
        if (values.containsKey(MealEntry.COLUMN_MEAL_TRANS)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(MealEntry.COLUMN_MEAL_TRANS);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Meal requires valid weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        return database.update(MealEntry.TABLE_NAME,values,selection,selectionArgs);
    }



    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database


        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEALS:
                // Delete all rows that match the selection and selection args
                return deleteMeal(uri, selection, selectionArgs);
            case MEAL_ID:
                // Delete a single row given by the ID in the URI
                selection = MealEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return deleteMeal(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }
    private int deleteMeal(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int num_deleted = database.delete(MealEntry.TABLE_NAME, selection, selectionArgs);
        if (num_deleted!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return num_deleted;

    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MEALS:
                return MealEntry.CONTENT_LIST_TYPE;
            case MEAL_ID:
                return MealEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

