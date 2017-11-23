package com.example.peerajak.habittracker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import com.example.peerajak.habittracker.data.MealContact.MealEntry;

import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , LocationListener {
    MealDbhelper mDbHelper;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;

    double mLatitude=0;
    double mLongitude=0;

    private GoogleApiClient googleApiClient;
    Location mCurrentLocation=null;


    ImageView mImageView;
    EditText mNameEditText;
    EditText mTransEditText;
    Spinner mDrinkSpinner;
    EditText mDescEditText;
    TextView mOutput;
    int mDrink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNameEditText = (EditText) findViewById(R.id.name_edit);
        mTransEditText= (EditText) findViewById(R.id.transfat_edit);
        mDrinkSpinner = (Spinner) findViewById(R.id.coffee_tea_none);
        mDescEditText = (EditText) findViewById(R.id.food_desc_edit);
        mOutput = (TextView) findViewById(R.id.see_output);
        mImageView = (ImageView) findViewById(R.id.camphoto);
        mDbHelper = new MealDbhelper(this);
        setupSpinner();
        //displayDatabaseInfo();

        Button outbutton = (Button) findViewById(R.id.see_output);
        outbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showIntent = new Intent(MainActivity.this, ShowlistActivity.class);
                startActivity(showIntent);
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentdata)  {
        Log.i("MainActivity", "onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE ){
            Log.i("MainActivity", "ImageRequest:"+resultCode);
            if( resultCode == RESULT_OK) {

                File file = new File(mCurrentPhotoPath);
                //String currentThumbnailPath = new StringBuilder(mCurrentPhotoPath).insert(mCurrentPhotoPath.length()-4, "thumbnail").toString();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(file));
                    Bitmap thumbnail = crupAndScale(bitmap.copy(bitmap.getConfig(),true),150);
                    mImageView.setImageBitmap(thumbnail);
                    //File fileThumbnail = new File(currentThumbnailPath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                    //OutputStream fOut = new FileOutputStream(file);
                    //thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title" , "Description");
                } catch (Exception e) {
                    Log.e("MainActivity","Create bitmap exception");
                }
                //addImageToGallery(mCurrentPhotoPath,MainActivity.this);

            }
            if( resultCode == RESULT_CANCELED)
            {
                Log.i("MainActivity", "RESULT_CANCELED");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("MainActivity","onStart");
        googleApiClient.connect();
        if( googleApiClient.isConnected())
        {
           Toast.makeText(this,"google Location API connected",Toast.LENGTH_SHORT).show();
            Log.i("MainActivity","google Location API connected");
        }else{
            Log.i("MainActivity","google Location API NOT connected");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("MainActivity","onStop");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.i("MainActivity","onConnected");
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);

        if (locationAvailability.isLocationAvailable()) {
            // Call Location Services
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000);
            Log.i("MainActivity","onConnected2");
            //if(locationRequest!=null)
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            }catch (Exception e){
                Log.e("MainActivity", "Error in onConnected.  "+e.getMessage());
            }
            Log.i("MainActivity","onConnected3");
        } else {
            // Do something when Location Provider not available
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("MainActivity","OnLocationChanged");
        TextView locationStatus = (TextView) findViewById(R.id.location_status);
        locationStatus.setText("Status: CONNECTED");
        mCurrentLocation = location;
        mLatitude = mCurrentLocation.getLatitude();
        mLongitude = mCurrentLocation.getLongitude();
        TextView latitude_text = (TextView) findViewById(R.id.latitude);
        latitude_text.setText("" +mLatitude);
        TextView longitude_text = (TextView) findViewById(R.id.longitude);
        longitude_text.setText("" + mLongitude);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static  Bitmap crupAndScale (Bitmap source, int scale){
        int factor = source.getHeight() <= source.getWidth() ? source.getHeight(): source.getWidth();
        int longer = source.getHeight() >= source.getWidth() ? source.getHeight(): source.getWidth();
        int x = source.getHeight() >= source.getWidth() ?0:(longer-factor)/2;
        int y = source.getHeight() <= source.getWidth() ?0:(longer-factor)/2;
        source = Bitmap.createBitmap(source, x, y, factor, factor);
        source = Bitmap.createScaledBitmap(source, scale, scale, false);
        return source;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
               Log.e("MainActivity","create photoFile error");
            }
            if (photoFile != null) {
                Uri uri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("MainActivity",mCurrentPhotoPath.toString());
        return image;
    }
    private void setupSpinner() {

        ArrayAdapter drinkSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.drinks_array, android.R.layout.simple_spinner_item);

        drinkSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mDrinkSpinner.setAdapter(drinkSpinnerAdapter);


        mDrinkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.COFFEE))) {
                        mDrink = MealEntry.DRINK_COFFEE;
                    } else if (selection.equals(getString(R.string.TEA))) {
                        mDrink = MealEntry.DRINK_TEA;
                    } else {
                        mDrink = MealEntry.DRINK_NONE;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDrink = 0;
            }
        });
    }
    public void takePhoto(View view){
        dispatchTakePictureIntent();
    }

    public void insertDb(View view) {
        Log.i("MainActivity","insertDb");

        String name = mNameEditText.getText().toString();
        String desc = mDescEditText.getText().toString();
        String transfat_str = mTransEditText.getText().toString();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(transfat_str)){
            return;
        }

        int transfat = Integer.parseInt(transfat_str.trim());

        ContentValues values = new ContentValues();

        values.put(MealEntry.COLUMN_MEAL_NAME,name.trim());
        values.put(MealEntry.COLUMN_MEAL_DESC,desc.trim());
        values.put(MealEntry.COLUMN_MEAL_DRINKS,mDrink);
        values.put(MealEntry.COLUMN_MEAL_TRANS,transfat);
        values.put(MealEntry.COLUMN_MEAL_LATITUDE,mLatitude);
        values.put(MealEntry.COLUMN_MEAL_LONGITUDE,mLongitude);
        values.put(MealEntry.COLUMN_MEAL_IMAGE,mCurrentPhotoPath);

        Uri newUri = getContentResolver().insert(MealEntry.CONTENT_URI, values);
        if(newUri == null) {
            Toast.makeText(this,"Error insertion",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"1 row added",Toast.LENGTH_SHORT).show();
            // Log.i("CatalogActivity","1 row added");
        }
    }

    public void getGPSclicked(View view){

         if(mCurrentLocation != null) {
             mLatitude = mCurrentLocation.getLatitude();
             mLongitude = mCurrentLocation.getLongitude();
             TextView latitude_text = (TextView) findViewById(R.id.latitude);
             latitude_text.setText("" +mLatitude);
             TextView longitude_text = (TextView) findViewById(R.id.longitude);
             longitude_text.setText("" + mLongitude);
         }
    }

    public void getMap(View view){

    }
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}
