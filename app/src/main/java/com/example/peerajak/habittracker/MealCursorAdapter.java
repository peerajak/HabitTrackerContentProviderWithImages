package com.example.peerajak.habittracker;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.peerajak.habittracker.data.MealContact.MealEntry;
import com.example.peerajak.habittracker.data.MealContact;

import java.io.File;

import static com.example.peerajak.habittracker.MainActivity.crupAndScale;

public class MealCursorAdapter extends CursorAdapter {

    public final int THUMBSIZE=120;
    public MealCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name_txtview = (TextView) view.findViewById(R.id.name);
        TextView breed_txtview = (TextView) view.findViewById(R.id.description);
        ImageView image_imgview = (ImageView) view.findViewById(R.id.item_image);

        String name_pet = cursor.getString(cursor.getColumnIndexOrThrow(MealEntry.COLUMN_MEAL_NAME));
        String breed_pet = cursor.getString(cursor.getColumnIndexOrThrow(MealEntry.COLUMN_MEAL_DESC));
        String image_path = cursor.getString(cursor.getColumnIndexOrThrow(MealEntry.COLUMN_MEAL_IMAGE));
        if (TextUtils.isEmpty(breed_pet)) {
            breed_pet = "No Description";
        }
        name_txtview.setText(name_pet);
        breed_txtview.setText(breed_pet);
        File file = new File(image_path);
        try {
            // TODO I will set OnClickListener where, if the list item clicked, the full size image will show up
            // TODO two lines below will do that.
            //Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
            // image_imgview.setImageBitmap(crupAndScale(bitmap,150));
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(file.getAbsolutePath()),
                    THUMBSIZE,
                    THUMBSIZE);
            image_imgview.setImageBitmap(crupAndScale(thumbImage,150));
        } catch (Exception e) {
            Log.e("ShowlistActivity","Create bitmap exception");
        }
    }
}
