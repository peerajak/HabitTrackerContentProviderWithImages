package com.example.peerajak.habittracker;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import com.example.peerajak.habittracker.data.MealContact.MealEntry;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ShowlistActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final int mLoaderManagerId=1;
    MealCursorAdapter mMealcursor_adapter;
    private String[] mProjection = {
            MealEntry._ID,
            MealEntry.COLUMN_MEAL_NAME,
            MealEntry.COLUMN_MEAL_TRANS,
            MealEntry.COLUMN_MEAL_DRINKS,
            MealEntry.COLUMN_MEAL_DESC,
            MealEntry.COLUMN_MEAL_LATITUDE,
            MealEntry.COLUMN_MEAL_LONGITUDE,
            MealEntry.COLUMN_MEAL_IMAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showlist);

        mMealcursor_adapter = new MealCursorAdapter(this,null);
        ListView list_pets = (ListView) findViewById(R.id.list);
        list_pets.setAdapter(mMealcursor_adapter);
        View emptyView = findViewById(R.id.empty_view);
        list_pets.setEmptyView(emptyView);
        getSupportLoaderManager().initLoader(mLoaderManagerId, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case mLoaderManagerId:
                return new CursorLoader(this, MealEntry.CONTENT_URI, mProjection,
                        null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mMealcursor_adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMealcursor_adapter.changeCursor(null);
    }
}
