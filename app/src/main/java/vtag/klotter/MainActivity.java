package vtag.klotter;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.LocationManager;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.EditText;
import android.text.InputType;
import android.content.DialogInterface;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.location.LocationListener;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import vtag.klotter.BusinessLogic.HttpTask;
import vtag.klotter.DomainObjects.Message;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    public static String D = "Klotter Debug";
    public Location lm;

    private FragmentActivity currActivity;

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(D, "app start");
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        currActivity = this;

        configureBtn(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        //create a default location and set last known to that
        Location defaultLocation = new Location("Default Location");
        defaultLocation.setLatitude(37.5665);
        defaultLocation.setLongitude(126.9780);
        setLastKnownLocation(defaultLocation);


        //create location listner subscribe to location updates and register
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if(location != null) {
                    setLastKnownLocation(location);
                    Log.e(D, "location changed");
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        int permissionCheck = ContextCompat.checkSelfPermission(currActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        // Register the listener with the Location Manager to receive location updates
        if(permissionCheck != -1) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 0, locationListener);
            Log.e(D, "registered location manager");
        }
        else
            Log.e(D, "no permission abcdefg");

        //just a temporary test for the http get method
        new HttpTask(37.5665, 126.9780).execute();
    }

    private void configureBtn(final FragmentActivity currentActivity) {

        Button addBtn = (Button) findViewById(R.id.addBtn);
        Button msgBtn = (Button) findViewById(R.id.msgBtn);
        msgBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(D, "msg");
            }
        });
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        addBtn.setOnClickListener(new View.OnClickListener() {
            private String m_Text;

            @Override
            public void onClick(final View view) {
                /* Alert Dialog Code Start*/
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
                alert.setMessage("Enter Your Name Here"); //Message here

                // Set an EditText view to get user input
                final EditText input = new EditText(view.getContext());
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                        String str = input.getEditableText().toString();
                        m_Text = str;
                        Toast.makeText(view.getContext(), str, Toast.LENGTH_LONG).show();

                        int permissionCheck = ContextCompat.checkSelfPermission(currentActivity,
                                android.Manifest.permission.ACCESS_FINE_LOCATION);
                        int permissionCheck2 = ContextCompat.checkSelfPermission(currentActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION);


                        Log.e("LANDON", "" + permissionCheck);

                        Location myLocation = getLastLocation();

                        LatLng myCurrLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(myCurrLocation).title(m_Text));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCurrLocation));


                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); //End of alert.setPositiveButton
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37, 126);
        mMap.addMarker(new MarkerOptions().position(seoul).title("In Incheon!!!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(currActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        int permissionCheck = ContextCompat.checkSelfPermission(currActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == -1)
            ActivityCompat.requestPermissions(currActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        else {
            // Acquire a reference to the system Location Manager


            /*
            String myProvider = lManager.getBestProvider(new Criteria(), true);

            if (myProvider != null) {

                Location myLocation = lManager.getLastKnownLocation(myProvider);

                setLastKnownLocation(myLocation);

                LatLng myCurrLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myCurrLocation));
                mMap.addCircle(new CircleOptions().center(myCurrLocation).radius(5));
                mMap.addCircle(new CircleOptions().center(myCurrLocation).radius(300));

            }
            */
        }
    }

    public void setLastKnownLocation(Location currentLocation) {
        this.lm = currentLocation;
    }

    public Location getLastLocation() {
        return this.lm;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch(requestCode)
        {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
