package com.example.peerajak.habittracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.peerajak.habittracker.data.MealContact.MealEntry;
/**
 * Created by peerajak on 11/16/17.
 */

public class MealDbhelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "meals.db";
        private static final int DATABASE_VERSION=1;

        public  MealDbhelper(Context context){
            super(context, DATABASE_NAME,null,DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String sqlCreatePetsTable = "CREATE TABLE "+ MealEntry.TABLE_NAME+"("+
                    MealEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    MealEntry.COLUMN_MEAL_NAME + " TEXT NOT NULL, "+
                    MealEntry.COLUMN_MEAL_TRANS + " INTEGER NOT NULL, "+
                    MealEntry.COLUMN_MEAL_DRINKS + " INTEGER NOT NULL DEFAULT 0, "+
                    MealEntry.COLUMN_MEAL_DESC + " TEXT,"+
                    MealEntry.COLUMN_MEAL_IMAGE + " TEXT);";
            db.execSQL(sqlCreatePetsTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }

        public Cursor readAllHabits(){
            String[] projection = {
                    MealEntry._ID,
                    MealEntry.COLUMN_MEAL_NAME,
                    MealEntry.COLUMN_MEAL_TRANS,
                    MealEntry.COLUMN_MEAL_DRINKS,
                    MealEntry.COLUMN_MEAL_DESC,
                    MealEntry.COLUMN_MEAL_IMAGE
            };
            SQLiteDatabase db = getReadableDatabase();
            return db.query(MealEntry.TABLE_NAME,projection,null,null,null,null,null);
        }
}
