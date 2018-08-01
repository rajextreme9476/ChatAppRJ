package com.datavim.chatapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.datavim.chatapp.R;
import com.datavim.chatapp.comman.Constants;
import com.datavim.chatapp.utils.PreferenceManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;
import java.io.IOException;
import java.util.List;
import java.util.Locale;



public class ChatLocationShare extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener
{

    private GoogleMap mMap;
    private Toolbar toolbar;
    private Double latitude = 0.0;
    private Double longitude = 0.0;

    View view;
    String TAG = "SearchAddress";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        initToolbar();
        //getLatLon();
        latitude = 19.0760;


        longitude = 72.8777;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        view = this.getWindow().getDecorView().findViewById(android.R.id.content);
        mapFragment.getMapAsync(this);
        //startSearch();
    }


    private void startSearch()
    {

        try
        {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }
        catch (GooglePlayServicesRepairableException e)
        {
            // TODO: Handle the error.
        }
        catch (GooglePlayServicesNotAvailableException e)
        {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latLng = place.getLatLng();
                mapMove(latLng, place.getAddress().toString());
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR)
            {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED)
            {
                // The user canceled the operation.
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void search()
    {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(Place place)
            {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status)
            {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void getLatLon()
    {
        latitude = Double.parseDouble(PreferenceManager.getPreference(getApplicationContext(), PreferenceManager.KEY_LATITUDE));
        longitude = Double.parseDouble(PreferenceManager.getPreference(getApplicationContext(), PreferenceManager.KEY_LONGITUDE));

        latitude = 19.0760;
        longitude = 72.8777;


    }


    private void initToolbar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Send Location");

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.menu_share_location);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                int id = item.getItemId();
                if (id == R.id.search_location)
                {
                    startSearch();
                }
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 8));
        getAddressFromLatLong(latitude, longitude);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapClickListener(this);
    }

/*
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) SearchAddress.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null; {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchItem != null)
            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(SearchAddress.this.getComponentName()));
            }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                try {
                    hideSoftKeyboard();
                    getLatLonFromAddress(query);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//              if (searchView.isExpanded() && TextUtils.isEmpty(newText)) {
           //     callSearch(newText);
//              }
                return true;
            }

            public void callSearch(String query) {
                Log.e("Result --------- ", query);
                //Do searching
              */
/*  Intent intentContactUs = new Intent(HomeActivity.this, SearchDealsActivity.class);
                startActivity(intentContactUs);*//*

            }

        });
        return true;

    }
*/

    private void mapMove(Double latitude, Double longitude, String address)
    {
        mMap.clear();

        final Double lat = latitude;
        final Double lon = longitude;
        LatLng latLng = new LatLng(lat, lon);
        MarkerOptions a = new MarkerOptions()
                .position(latLng);
        Marker m = mMap.addMarker(a);
        mMap.addMarker(new MarkerOptions().position(latLng).title("" + address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        final String locationAdd = "" + address;
        final Snackbar snack = Snackbar.make(view, locationAdd, Snackbar.LENGTH_INDEFINITE);
        View view_ = snack.getView();
        TextView tv = (TextView) view_.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTypeface(null, Typeface.ITALIC);
        tv.setTextColor(Color.WHITE);
        snack.setAction("Done", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.LOCATION, locationAdd);
                bundle.putDouble(Constants.LATITUDE, lat);
                bundle.putDouble(Constants.LONGITUDE, lon);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        snack.show();
    }


    private void mapMove(final LatLng latLng, String address)
    {
        mMap.clear();

        MarkerOptions a = new MarkerOptions()
                .position(latLng);
        mMap.addMarker(new MarkerOptions().position(latLng).title("" + address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        final String locationAdd = "" + address;


        final Snackbar snack = Snackbar.make(view, locationAdd, Snackbar.LENGTH_INDEFINITE);
        View view_ = snack.getView();
        TextView tv = (TextView) view_.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTypeface(null, Typeface.ITALIC);
        tv.setTextColor(Color.WHITE);
        snack.setAction("Done", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.LOCATION, locationAdd);
                bundle.putDouble(Constants.LATITUDE, latLng.latitude);
                bundle.putDouble(Constants.LONGITUDE, latLng.longitude);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        snack.show();
    }

    public void hideSoftKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public Barcode.GeoPoint getLocationFromAddress(String strAddress)
    {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        Barcode.GeoPoint p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new Barcode.GeoPoint((1), (location.getLatitude() * 1E6), (location.getLongitude() * 1E6));

            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney").draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));
            return p1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return p1;
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        getAddressFromLatLong(latLng.latitude, latLng.longitude);
    }

    private String getAddressFromLatLong(double latitude, double longitude)
    {
        String address = "";
        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1);
            mapMove(latitude, longitude, address);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return address;
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker
    )
    {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0)
                {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else
                {
                    if (hideMarker)
                    {
                        marker.setVisible(false);
                    } else
                    {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

}
