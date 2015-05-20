package com.cityos.frano.tracker;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.widget.Toast;

import com.cityos.frano.tracker.Constants;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class AddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    public AddressIntentService() {
        super("AddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String errorMessage = "";

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                Constants.LOCATION_DATA_EXTRA);

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Toast.makeText(this,
                    errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(),
                    Toast.LENGTH_SHORT).show();
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Toast.makeText(this, getString(R.string.address_found), Toast.LENGTH_SHORT).show();
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                                    TextUtils.join(System.getProperty("line.separator"), addressFragments));

        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }
}
