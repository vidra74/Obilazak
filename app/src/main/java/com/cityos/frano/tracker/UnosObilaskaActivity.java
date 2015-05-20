package com.cityos.frano.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.cityos.frano.tracker.ObilazakPoint;
import com.cityos.frano.tracker.Constants;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class UnosObilaskaActivity
        extends ActionBarActivity
        implements GoogleApiClient.ConnectionCallbacks,
                    GoogleApiClient.OnConnectionFailedListener,
                    LocationListener {


    class AddressResultReceiver extends ResultReceiver {

        private String mAddressOutput;
        private UnosObilaskaActivity mReceiver;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            mReceiver.displayAddressOutput(mAddressOutput);

            // Show a toast message if an address was found.
            /*
            if (resultCode == Constants.SUCCESS_RESULT) {

                Toast.makeText(getApplicationContext(), getString(R.string.address_found), Toast.LENGTH_SHORT).show();
                // showToast(getString(R.string.address_found));
            }
            */
        }

        public void setReceiver(UnosObilaskaActivity receiver) {
            mReceiver = receiver;
        }

        public String getAddress(){
            return mAddressOutput;
        }
    }

    private ObilazakPoint m_op;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private AddressResultReceiver mResultReceiver;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates;
    private String mLastUpdateTime ;
    private LocationServices locationManager;
    private ImageView mImageView;
    private String mCurrentPhotoPath;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unos_obilaska);

        mRequestingLocationUpdates = false;

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        m_op = new ObilazakPoint(dateFormat.format(date), "0.000000", "0.000000", message, "");
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        if (mRequestingLocationUpdates){
            createLocationRequest();
        };

        TextView tv = (TextView)findViewById(R.id.textUnosVrijeme);
        tv.setText(m_op.getVrijeme());

        napuniKoordinate();

    }

    protected void startIntentService() {

        mResultReceiver = new AddressResultReceiver(new Handler());
        mResultReceiver.setReceiver(this);

        Intent intent = new Intent(this, AddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        if (mRequestingLocationUpdates) {
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mCurrentLocation);
        }else{
            intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        }
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_unos_obilaska, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    public void btnSpremiPodatke(View view){

        TextView tv = (TextView)findViewById(R.id.textUnosOpis);
        m_op.setOpis(tv.getText().toString());
        String imeDatoteke = imeDatoteke();
        m_op.Spremi("CityOs.ser", getApplicationContext());
        m_op.Spremi(imeDatoteke, getApplicationContext());

        Intent resultIntent = new Intent();
        resultIntent.putExtra(MainActivity.STATUS_MESSAGE, getString(R.string.uspjeh));  // put data that you want returned to activity A
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        //precizni update ili zadnja lokacija
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                m_op.setLatitude(String.valueOf(mLastLocation.getLatitude()));
                m_op.setLongitude(String.valueOf(mLastLocation.getLongitude()));
            }

            if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                startIntentService();
            }
        }

        napuniKoordinate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getDateTimeInstance().format(new Date());

        m_op.setVrijeme(mLastUpdateTime);
        m_op.setLongitude(String.valueOf(mCurrentLocation.getLongitude()));
        m_op.setLatitude(String.valueOf(mCurrentLocation.getLatitude()));

        stopLocationUpdates();
        mRequestingLocationUpdates = false;

        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }

        updateUI();
    }

    private void updateUI() {
        napuniKoordinate();

        TextView tv = (TextView)findViewById(R.id.textUnosVrijeme);
        tv.setText(m_op.getVrijeme());
    }

    public void napuniKoordinate(){
        TextView tv = (TextView)findViewById(R.id.textUnosLongitude);
        tv.setText(getString(R.string.longitude) + ": " + m_op.getLongitude());
        tv = (TextView)findViewById(R.id.textUnosLatitude);
        tv.setText(getString(R.string.latitude) + ": " + m_op.getLatitude());
    }

    public String imeDatoteke(){
        String datoteka = m_op.getVrijeme();
        datoteka = datoteka.replaceAll(" ", "");
        datoteka = datoteka.replaceAll(":", "");
        return datoteka + ".koo";
    }

    private void displayAddressOutput(String adresa) {

        TextView tv = (TextView)findViewById(R.id.textUnosOpis);
        tv.setText(adresa + ": " + tv.getText().toString());
    }
/*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
*/

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // ...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
