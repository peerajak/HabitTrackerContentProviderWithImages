package com.example.peerajak.habittracker.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by peerajak on 11/16/17.
 */

public final class MealContact {

    private MealContact(){}//An empty private constructor makes sure that the class is not going to be initialised.
    public static final String CONTENT_AUTHORITY = "com.example.peerajak.habittracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MEALS = "meals";


    public static final class MealEntry implements BaseColumns {

        public static final String TABLE_NAME = "meals";
        public static final String COLUMN_MEAL_NAME = "name";
        public static final String COLUMN_MEAL_TRANS = "trans";
        public static final String COLUMN_MEAL_DRINKS = "drinks";
        public static final String COLUMN_MEAL_DESC = "description";
        public static final String COLUMN_MEAL_IMAGE = "photo";

        public static final int DRINK_COFFEE = 1;
        public static final int DRINK_TEA = 2;
        public static final int DRINK_NONE = 0;


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MEALS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEALS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MEALS;


        public static boolean isValidDrink(int drinktype) {
            if (drinktype == DRINK_COFFEE || drinktype == DRINK_TEA || drinktype == DRINK_NONE) {
                return true;
            }
            return false;
        }
    }

}


