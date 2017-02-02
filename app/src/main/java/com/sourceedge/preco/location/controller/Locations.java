package com.sourceedge.preco.location.controller;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.sourceedge.preco.R;
import com.sourceedge.preco.homescreen.controller.HomeScreen;
import com.sourceedge.preco.location.model.ModelPrinters;
import com.sourceedge.preco.location.view.LocationListAdapter;
import com.sourceedge.preco.payment.controller.Payments;
import com.sourceedge.preco.support.Class_Genric;
import com.sourceedge.preco.support.Class_Model_DB;
import com.sourceedge.preco.support.Class_Static;
import com.sourceedge.preco.timeslots.controller.SlotSelection;
import com.sourceedge.preco.uploadfile.controller.UploadFile;

import static com.sourceedge.preco.support.Class_Model_DB.SelectedPrinter;
import static com.sourceedge.preco.viewer.controller.PdfViewer.context;

public class Locations extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    Toolbar toolbar;
    public static TextView next;
    //public static GoogleMap mMap;
    Dialog dialog;
    TextView copiesScan;
    Button nextButton, cancelButton;
    ImageView decrement, increment;
    RecyclerView recyclerView;
    TextView scrollup;
    public static GoogleMap mMap;
    MapFragment mapFragment;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LinearLayout footer;
    LinearLayoutManager linearLayoutManager;
    boolean isOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location);
        Class_Genric.turnGPSOn(Locations.this);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        footer = (LinearLayout) findViewById(R.id.upperlayout);
        scrollup = (TextView) findViewById(R.id.scrollup);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.locationslist);
        linearLayoutManager = new LinearLayoutManager(Locations.this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new LocationListAdapter());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        next = (TextView) findViewById(R.id.next);
        isOpen = false;
        footer.setVisibility(View.VISIBLE);
        OnClicks();

    }


    private void OnClicks() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedPrinter != null) {
                    if (Class_Static.isCopyScan) {
                        dialog = new Dialog(Locations.this);
                        dialog.setContentView(R.layout.dialog_copies);
                        copiesScan = (TextView) dialog.findViewById(R.id.copies_scan);
                        nextButton = (Button) dialog.findViewById(R.id.next_button);
                        cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
                        decrement = (ImageView) dialog.findViewById(R.id.decrement);
                        increment = (ImageView) dialog.findViewById(R.id.increment);

                        nextButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!copiesScan.getText().toString().matches("0")) {
                                    dialog.dismiss();
                                    startActivity(new Intent(Locations.this, Payments.class));
                                } else
                                    Toast.makeText(Locations.this, "Min Count is 1", Toast.LENGTH_SHORT).show();
                            }
                        });

                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        decrement.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ("".equals(copiesScan.getText().toString().trim())) {
                                    copiesScan.setText("1");
                                }
                                if (!copiesScan.getText().toString().matches("1")) {
                                    copiesScan.setText((Integer.parseInt(copiesScan.getText().toString()) - 1) + "");
                                    //productStock.setText("Stock Available : " + ((Integer.parseInt(productStock.getText().toString().split(": ")[1]) + 1) + ""));
                                } else
                                    Toast.makeText(Locations.this, "Min Count is 1", Toast.LENGTH_SHORT).show();
                            }
                        });

                        increment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                copiesScan.setText((Integer.parseInt(copiesScan.getText().toString()) + 1) + "");
                            }
                        });


                        dialog.show();
                    } else {
                        Class_Model_DB.SelectedPrinter = null;
                        startActivity(new Intent(Locations.this, UploadFile.class));
                    }
                } else
                    Toast.makeText(Locations.this, "Please Select A Machine", Toast.LENGTH_SHORT).show();
            }
        });

        scrollup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen)
                {
                    linearLayoutManager = new LinearLayoutManager(Locations.this) {
                        @Override
                        public boolean canScrollVertically() {
                            return true;
                        }
                    };
                    footer.setVisibility(View.VISIBLE);
                    scrollup.setText("View All");
                    isOpen=false;
                }
                else{
                    linearLayoutManager = new LinearLayoutManager(Locations.this) {
                        @Override
                        public boolean canScrollVertically() {
                            return true;
                        }
                    };
                    footer.setVisibility(View.GONE);
                    scrollup.setText("View Less");
                    isOpen=true;
                }
                recyclerView.setLayoutManager(linearLayoutManager);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {                //On Back Arrow pressed
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


       /* final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(13.021745, 77.554492))      // Sets the center of the map to Mountain View
                .zoom(18)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        for (ModelPrinters printer : Class_Model_DB.Printers) {
            mMap.addMarker(printer.getMarker()).setTitle(printer.getTitle());
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Class_Model_DB.SelectedPrinter = new ModelPrinters(arg0.getTitle(), arg0.getPosition());
                return true;
            }

        });
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        //mLocationRequest.setInterval(1000);
        //mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //Class_Model_DB.Printers.add(new ModelPrinters("My Location",new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))));
       // MarkerOptions markerOptions = new MarkerOptions();
       // markerOptions.position(latLng);
        // markerOptions.title("Current Position");
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        //mCurrLocationMarker = mMap.addMarker(markerOptions);

        for (ModelPrinters printer : Class_Model_DB.Printers) {
            /*mMap.addMarker(new MarkerOptions()
                    .position(printer.getMarker().getPosition())
                    .title(printer.getTitle())
                    .snippet("Population: 4,137,400")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));*/
            MarkerOptions markerOptions1 = new MarkerOptions();
            //IconGenerator iconFactory = new IconGenerator(this);
            //iconFactory.setStyle(IconGenerator.STYLE_RED);
            markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_printshop_black_24dp));

            //markerOptions1.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

            markerOptions1.position(printer.getMarker().getPosition());
            markerOptions1.title(printer.getTitle());
            //markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mMap.addMarker(markerOptions1);
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Class_Model_DB.SelectedPrinter = new ModelPrinters(arg0.getTitle(), arg0.getPosition());
                next.performClick();
                return true;
            }

        });

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
