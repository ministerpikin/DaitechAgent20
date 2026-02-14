package ique.daitechagent.activities;

//import android.app.Fragment;
//import android.app.FragmentManager;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.AccessToken;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import ique.daitechagent.R;
import ique.daitechagent.common.Common;
import ique.daitechagent.database.DataContext;
import ique.daitechagent.fragments.CustomerList;
import ique.daitechagent.fragments.DailyscheduleList;
import ique.daitechagent.fragments.DailyscheduleList.OnFragmentInteractionListener;
import ique.daitechagent.fragments.Dashboard;
import ique.daitechagent.fragments.ReportList;
import ique.daitechagent.geofencing.GeofencingService;
import ique.daitechagent.gps.PermissionUtil;
import ique.daitechagent.helpers.FirebaseHelper;
import ique.daitechagent.model.AgentAddressBookResult;
import ique.daitechagent.model.AgentTracking;
import ique.daitechagent.model.Customer;
import ique.daitechagent.model.CustomerResult;
import ique.daitechagent.model.RegionQueryResult;
import ique.daitechagent.model.User;
import ique.daitechagent.retrofit.AgentAddressBookDataClient;
import ique.daitechagent.retrofit.CustomerDataClient;
import ique.daitechagent.retrofit.IRetrofitAgentAddressBookData;
import ique.daitechagent.retrofit.IRetrofitCustomerData;
import ique.daitechagent.retrofit.IRetrofitRegions;
import ique.daitechagent.retrofit.RegioningClient;
import ique.daitechagent.service.CommentService;
import ique.daitechagent.service.LocationSharingService;
import ique.daitechagent.service.NotificationReceiver;
import ique.daitechagent.service.SettingsUtil;
import ique.daitechagent.utils.DateUtils;
import ique.daitechagent.versioncheckservice.VersionCheckService;
import ique.softwareupdate.update.AppUtils;
import ique.softwareupdate.update.UpdateChecker;
import retrofit2.Call;
//import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ReportList.OnFragmentInteractionListener, CustomerList.OnFragmentInteractionListener, Dashboard.OnFragmentInteractionListener,
        OnFragmentInteractionListener,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback ,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity";
    private static final int INITIAL_REQUEST=1337;
    private static final int REQUEST_LOCATION=INITIAL_REQUEST+1;
    String CHANNEL_ID = "AgentTracking";

    private static final int BACKGROUND_LOCATION_PERMISSION_CODE=67890;
    private static final int REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE=BACKGROUND_LOCATION_PERMISSION_CODE;

    // Declarations for version check and download of updated app
    private static final String REQUEST_URL = "http://daitechpharma.com/php_stock/webservicelib/daitechagent/checkVersion.php";
    private MainActivity.VersionCheckServiceReceiver receiver;
    private int versionCode = 0;
    String appURI = "";
    private DownloadManager downloadManager;
    private long downloadReference;
    boolean bWritePermssonGranted;
    // End of variable declaration
    // Test download sturvs
    private DownloadManager dmTest;
    private long enqueueTest;


    boolean bCallPermssonGranted;


    FragmentManager fragmentManager;
    FrameLayout viewLayout;

    private GoogleApiClient mGoogleApiClient;
    GoogleSignInAccount account;

    //Facebook
    //AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedInFacebook = false;//accessToken != null && !accessToken.isExpired();

    View mLayout;

    Location mlocation; // location
    protected LocationManager locationManager;
    double latitude;
    double longitude;
    String address;
    boolean canGetLocation = false;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;//10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 100;// 1000 * 60 * 1; // 1 minute


    DatabaseReference onlineRef, currentUserRef;

    FirebaseHelper firebaseHelper;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    NavigationView navigationView;
    FloatingActionButton fab;

    public Fragment fragment = null;

    private NotificationManager mNotificationManager;
    ImageView menu_drawer_image;

    private Dialog permissionsDialog;
    //private DialogPermissionsBinding dialogPermissionsBinding;

    protected void dismissDialog(Dialog dialog) {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception ignored) {
            }
        }
    }

    Context context;
    //android.app.AlertDialog waitingDialogCustomers;
    private DataContext db;

    private static final int INSTALL_PACKAGES_REQUESTCODE = 10101;
    private static final int GET_UNKNOWN_APP_SOURCES = 10102;

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 10201;

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        mLayout = findViewById(R.id.drawer_layout);
        // Check whether write access is given if download needs to occur
        bWritePermssonGranted = isWriteStoragePermissionGranted();

        // Check and request call permissions
        bCallPermssonGranted = isPhoneCallPermissionGranted();



        db = new DataContext(this, null, null, 1);

        //CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/NotoSans.ttf").setFontAttrId(R.attr.fontPath).build());



        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseHelper = new FirebaseHelper(this, true);
        Common.userID =  FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lets start the monitoring service
        onAuthSuccess(Common.userID);

        if ( Common.isNetworkAvailable(TAG, this )) {
            SyncCustomerDatabases syncCustomerDatabases = new SyncCustomerDatabases();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            //String email = "daitechagent@daitechpharma.com";
            syncCustomerDatabases.execute(email);
        }else {
            Toast.makeText(this, "Please check your internet connection. Customers cannot be synced.", Toast.LENGTH_SHORT).show();
        }

        // Check if there is a new version of the application
        // Jeeezzus this was running as well as the other one at the bottom
        // checkForNewVersion();

        mLayout = findViewById(R.id.drawer_layout);

        fab = findViewById(R.id.fab);
        fab.setTag("empty");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check permission for Location services
                checkLocationPermission();

                if (fab.getTag().toString().toLowerCase().equals("reportlist")) {
                    if (canGetLocation) {
                        firebaseHelper.addReport(latitude, longitude, address);
                    } else {

                        Snackbar.make(view, "Unable to write report now", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
                }else if(fab.getTag().toString().toLowerCase().equals("customerlist")){

                    //firebaseHelper.addNewCustomer();
                    firebaseHelper.searchNewCustomerOnline();

                }else if(fab.getTag().toString().toLowerCase().equals("dailyschedulelist")){

                    String visitDateSelect;
                    if (fragment != null) {

                        visitDateSelect = ((DailyscheduleList)fragment).getCalendarDate();

                    }else{
                        Date dNow = new Date( );
                        SimpleDateFormat ft =
                                new SimpleDateFormat ("dd-MM-yyyy", Locale.getDefault());

                        visitDateSelect = ft.format(dNow);

                    }

                    firebaseHelper.addDailySchedule(visitDateSelect);
                }

            }
        });

        fab.hide();//.setVisibility(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewLayout = findViewById(R.id.viewLayout);
        fragmentManager = getSupportFragmentManager();

        menu_drawer_image= findViewById(R.id.menu_menu);

        // Sync the regions
        syncRegion();

        // Check to see if a new version is available
        checkForNewVersion();

        // Verify and Load user object into Common.currentUser
        verifyGoogleAccount();

        // Check permission for Location services
        checkLocationPermission();

        // Get permission for contacts
        // requestContactPermission();

        if (Build.VERSION.SDK_INT >= 23) {


            Log.d("MapaActivity", "Build.VERSION.SDK_INT >= 23 ");
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }

            context.startActivity(intent);
        }
    }

    private void syncRegion(){

        if ( Common.isNetworkAvailable(TAG, this )) {
            SyncAgentRegion syncAgentRegion = new SyncAgentRegion();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            syncAgentRegion.execute(email);
        }else {
            Toast.makeText(this, "Please check your internet connection. Customers cannot be synced.", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkForNewVersion(){

        //check of internet is available before making a web service request
        //Intent msgIntent;
        if(Common.isNetworkAvailable(TAG, this)){

            String usrName = usernameFromEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean result = getPackageManager().canRequestPackageInstalls();
                if (result) {
                    //UpdateChecker.checkForNotification(MainActivity.this, usrName);
                    UpdateChecker.checkForDialog(MainActivity.this, usrName);
                } else {
                    // request the permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
                }
            } else {
                //UpdateChecker.checkForNotification(MainActivity.this, usrName);
                UpdateChecker.checkForDialog(MainActivity.this, usrName);
            }

            Log.v(TAG, "Version Check Service started.");
        }else{
            Toast.makeText(getApplicationContext(), "Network not available to check for new version.", Toast.LENGTH_SHORT).show();
        }

    }

    /*
    private void checkForNewVersion(){
        // After Auth success
        //Overall information about the contents of a package
        //This corresponds to all of the information collected from AndroidManifest.xml.
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            //get the app version Name for display
            String version = pInfo.versionName;
            //get the app version Code for checking
            versionCode = pInfo.versionCode;

            //Broadcast receiver for our Web Request
            IntentFilter filter = new IntentFilter(VersionCheckServiceReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            receiver = new VersionCheckServiceReceiver();
            registerReceiver(receiver, filter);

            //Broadcast receiver for the download manager
            filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(downloadReceiver, filter);
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, e);
            //e.printStackTrace();
        }

    }

     */

    public void menu_menu_image(View view) {
        ((DrawerLayout) mLayout).openDrawer(Gravity.LEFT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DatabaseReference lastLogin = FirebaseDatabase.getInstance().getReference(Common.USER_LAST_LOGGED_IN_NODE);
        Map<String, Long> mapVal = new HashMap<>();
        mapVal.put("timestamp", new Date().getTime());
        lastLogin.child(Common.userID).setValue(mapVal);

        //unregisterReceiver(receiver);
    }

    private void verifyGoogleAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        OptionalPendingResult<GoogleSignInResult> opr=Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result= opr.get();
            handleSignInResult(result);
        }else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            account = result.getSignInAccount();
            Common.userID=account.getId();
            loadUser();
        }else if(isLoggedInFacebook){
            /*GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            String id=object.optString("id");
                            Common.userID=id;
                            loadUser();
                        }
                    });
            request.executeAsync();*/
        }else{
            Common.userID=FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadUser();
        }
    }

    private void loadUser(){
        FirebaseDatabase.getInstance().getReference(Common.USER_NODE)
                .child(Common.userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Common.currentUser = dataSnapshot.getValue(User.class);
                        if (Common.currentUser == null){
                            updateNullUseEntry();

                            // Unset the password if it was set before
                            Common.userPW = "";
                        }else{
                            firebaseHelper.updateUserAppversion(AppUtils.getVersionCode(context));
                            // Start Chat/Comment monitoring service
                            startCommentMonitoringService();
                            startGeofencingService();
                        }

                        initDrawer();
                        //loadDriverInformation();
                        onlineRef=FirebaseDatabase.getInstance().getReference().child(".info/connected");
                        currentUserRef=FirebaseDatabase.getInstance().getReference(Common.USER_PRESENCE_NODE).child(Common.userID);
                        onlineRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                currentUserRef.onDisconnect().removeValue();
                                currentUserRef.setValue(DateUtils.getTimeStamp());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // If initUser returns a null for the user object (means no entry in the user node)
    // create the entry for it.
    private void updateNullUseEntry(){

        DatabaseReference users=FirebaseDatabase.getInstance().getReference(Common.USER_NODE);
        String sEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String sUsername = usernameFromEmail(sEmail);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final User user = new User();
        user.setEmail(sEmail);
        user.setUsername(sUsername);
        user.setPassword(Common.userPW);
        user.setPhone("");
        user.setUserID(userID);

        // Update appversion
        user.setAppversion(AppUtils.getVersionCode(context));

        users.child(userID)
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Common.currentUser = user;

                        // Start Chat/Comment monitoring service
                        startCommentMonitoringService();
                        startGeofencingService();
                        try {
                            Snackbar.make(mLayout, "User Info Updated", Snackbar.LENGTH_SHORT).show();
                        }catch (Exception ex){
                            Toast.makeText(getApplicationContext(), "User Info Updated", Toast.LENGTH_SHORT);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                try {
                    Snackbar.make(mLayout, "User Update failed"+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "User Update failed"+e.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    public void initDrawer(){

        // Lets load the graph fragment
        if (Common.currentUser != null) {
            setDashboadFragment();
        }

        View navigationHeaderView = navigationView.getHeaderView(0);

        TextView tvAgentName= navigationHeaderView.findViewById(R.id.agentName);
        TextView tvAgentRegions= navigationHeaderView.findViewById(R.id.agentRegion);
        CircleImageView imageAvatar= navigationHeaderView.findViewById(R.id.imageAvatar);

        if (Common.currentUser != null) {
            tvAgentName.setText(Common.currentUser.getUsername());
            tvAgentRegions.setText(Common.currentUser.getAgentRegion());
        }


        if(isLoggedInFacebook) {
            Picasso.get().load("https://graph.facebook.com/" + Common.userID + "/picture?width=500&height=500").into(imageAvatar);
        }
        else if(account!=null) {
            Picasso.get().load(account.getPhotoUrl()).into(imageAvatar);
        }

        if (Common.currentUser != null) {
            if (Common.currentUser.getAvatarUrl() != null) {
                if (Common.currentUser.getAvatarUrl() != null &&
                        !TextUtils.isEmpty(Common.currentUser.getAvatarUrl())) {
                    Picasso.get().load(Common.currentUser.getAvatarUrl()).into(imageAvatar);
                }
            }
        }


    }


    int doubleBackToExitPressed = 1;
    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if ( ((DrawerLayout)mLayout).isDrawerOpen(GravityCompat.START)) {
            ((DrawerLayout)mLayout).closeDrawer(GravityCompat.START);
        } else {


            if (doubleBackToExitPressed == 2) {

                super.onBackPressed();
            }
            else {
                doubleBackToExitPressed++;
                Snackbar.make( mLayout, "Press Back again!", Snackbar.LENGTH_LONG).show();

            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressed=1;
                }
            }, 2000);



    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        String fragTag = "empty";

        switch (id){
            case R.id.nav_dashboard:
                setTitle("Performance Stats");
                setDashboadFragment();
                break;
            case R.id.nav_reports:
                //showReportHistory();
                fragment = new ReportList();
                fragTag = "reportlist";
                setupFragment(item, fragTag);
                fab.setTag(fragTag);
                setTitle("Report List");
                fab.show();
                fab.setImageResource(R.drawable.ic_add_report_white);
                break;
            case R.id.nav_schedule:
                fragment = new DailyscheduleList();
                fragTag = "dailyschedulelist";
                setupFragment(item, fragTag);
                fab.setTag(fragTag);
                setTitle("Daily Schedule");
                fab.show();
                fab.setImageResource(R.drawable.ic_add_report_white);
                break;
            case R.id.nav_scan:
                showScanHistory();
                setTitle("Scan Barcode");
                break;
            case R.id.nav_customerlist:
                fragment = new CustomerList();
                fragTag = "customerlist";
                setupFragment(item, fragTag);
                fab.setTag(fragTag);
                setTitle("Customer List");
                fab.show();
                fab.setImageResource(R.drawable.ic_add_person_icon);
                break;
            case R.id.nav_change_password:
                if(account!=null) {
                    showDialogChangePwd();
                }else if(isLoggedInFacebook){
                    Toast.makeText(MainActivity.this, "Facebook login cannot change password!", Toast.LENGTH_SHORT).show();
                }else{
                    changePasswordFirebase();
                }
                break;
            case R.id.nav_update_info:
                // Update pic and name
                firebaseHelper.showDialogUpdateInfo();
                break;
            case R.id.nav_settings:
                showDialogSettings();
            case R.id.nav_check_for_updates:
                checkForNewVersion();
                break;
            case R.id.nav_about:
                showDialogAbout();
                break;
            case R.id.nav_change_logout:
                signOut();
                break;
        }

        /*
        if (id != R.id.nav_scan) {
            if (fragment != null) {
                if (fragmentManager.getFragments().size() > 0) {
                    fragmentManager.getFragments().clear();
                }
                fragmentManager.beginTransaction().replace(R.id.viewLayout, fragment, fragTag).commit();
            }
        }
        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setDashboadFragment(){
        //fragment = new Dashboard();
        fragment = Dashboard.newInstance(Common.currentUser, Common.userID);
        String fragTag = "dashboard";
        setupFragment(null, fragTag);
        fab.setTag(fragTag);
        setTitle("Performance Stats");
        fab.hide();
    }

    private void setupFragment(MenuItem item, String fragTag){

        int id;
        if (item == null){
            id = 999999999;
        }else {
            id = item.getItemId();
        }


        if (id != R.id.nav_scan) {
            if (fragment != null) {
                if (fragmentManager.getFragments().size() > 0) {
                    fragmentManager.getFragments().clear();
                }
                fragmentManager.beginTransaction().replace(R.id.viewLayout, fragment, fragTag).commit();
            }
        }
    }

    private void showScanHistory() {
        Intent intent=new Intent(MainActivity.this, ScanHistory.class);
        startActivity(intent);
    }

    private void showDialogChangePwd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
        alertDialog.setTitle("CHANGE PASSWORD");


        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_change_pwd, null);

        final MaterialEditText edtPassword = layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = layout_pwd.findViewById(R.id.edtRepetPassword);

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("CHANGE PASSWORD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();

                if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    //Get auth credentials from the user for re-authentication.
                    //Example with only email
                    AuthCredential credential = EmailAuthProvider.getCredential(email, edtPassword.getText().toString());
                    FirebaseAuth.getInstance().getCurrentUser()
                            .reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser()
                                                .updatePassword(edtRepeatPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            //update driver information password column
                                                            Map<String, Object> password = new HashMap<>();
                                                            password.put("password", edtRepeatPassword.getText().toString());
                                                            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference(Common.USER_NODE);
                                                            userInformation.child(Common.userID)
                                                                    .updateChildren(password)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                                Toast.makeText(MainActivity.this, "Password was changed!", Toast.LENGTH_SHORT).show();
                                                                            else
                                                                                Toast.makeText(MainActivity.this, "Password was doesn't changed!", Toast.LENGTH_SHORT).show();
                                                                            waitingDialog.dismiss();

                                                                        }
                                                                    });

                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Password doesn't change", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                    } else {
                                        waitingDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }


            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //show dialog
        alertDialog.show();

    }

    private void showDialogAbout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
        //alertDialog.setTitle("ABOUT");


        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_about, null);

        TextView version = layout_pwd.findViewById(R.id.versioninfo);
        version.setText("version " + AppUtils.getVersionCode(context));

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //show dialog
        alertDialog.show();

    }

    private void changePasswordFirebase(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
        alertDialog.setTitle("CHANGE PASSWORD");


        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_change_pwd, null);

        final MaterialEditText edtPassword = layout_pwd.findViewById(R.id.edtPassword);
        final MaterialEditText edtNewPassword = layout_pwd.findViewById(R.id.edtNewPassword);
        final MaterialEditText edtRepeatPassword = layout_pwd.findViewById(R.id.edtRepetPassword);

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("CHANGE PASSWORD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();

                if (edtNewPassword.getText().toString().equals(edtRepeatPassword.getText().toString())) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    //Get auth credentials from the user for re-authentication.
                    //Example with only email
                    AuthCredential credential = EmailAuthProvider.getCredential(email, edtPassword.getText().toString());
                    FirebaseAuth.getInstance().getCurrentUser()
                            .reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser()
                                                .updatePassword(edtRepeatPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            //update driver information password column
                                                            Map<String, Object> password = new HashMap<>();
                                                            password.put("password", edtRepeatPassword.getText().toString());
                                                            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference(Common.USER_NODE);
                                                            userInformation.child(Common.userID)
                                                                    .updateChildren(password)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                                Toast.makeText(MainActivity.this, "Password was changed!", Toast.LENGTH_SHORT).show();
                                                                            else
                                                                                Toast.makeText(MainActivity.this, "Password was doesn't changed!", Toast.LENGTH_SHORT).show();
                                                                            waitingDialog.dismiss();

                                                                        }
                                                                    });

                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Password doesn't change", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                    } else {
                                        waitingDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Wrong old password", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                }


            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //show dialog
        alertDialog.show();
    }

    private void showDialogSettings(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
        alertDialog.setTitle("SETTINGS");


        LayoutInflater inflater = this.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_settings, null);

        // initiate a Switch
        final Switch simpleSwitch = layout_pwd.findViewById(R.id.switchService);
        simpleSwitch.setTextOn("On"); // displayed text of the Switch whenever it is in checked or on state
        simpleSwitch.setTextOff("Off"); // displayed text of the Switch whenever it is in unchecked i.e. off state

        final boolean isServiceRunning = SettingsUtil.isServiceRunning(this, LocationSharingService.class);

        simpleSwitch.setChecked(isServiceRunning);

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                if (simpleSwitch.isChecked() && !isServiceRunning){
                    onAuthSuccess(Common.userID);
                }else{

                    if (isServiceRunning) {
                        Intent stopServiceIntent = new Intent(MainActivity.this, LocationSharingService.class);
                        stopService(stopServiceIntent);
                    }
                }

                waitingDialog.dismiss();


            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //show dialog
        alertDialog.show();
    }

    private void signOut() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog);

        // set title
        alertDialogBuilder.setTitle("Daitech Mobile Workforce");

        // set dialog message
        alertDialogBuilder
                .setMessage("You are about to Sign Out of the application. Continue?")
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        comepleteSignOut();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void comepleteSignOut() {


        if(account!=null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Could not log out", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else if(isLoggedInFacebook){
            //LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }else{
            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkLocationPermission(){
        Log.i(TAG, "Checking Location Permission permission.");
        // BEGIN_INCLUDE(checkLocationPermission)
        // Check if the Location permission is already available.
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

//        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//
//            requestLocationPermission();
//
//        } else {
//
//            // Location permissions is already available, show the location.
//            Log.i(TAG,
//                    "Location permission has already been granted.");
//
//            getLocation();
//            if (!canGetLocation){
//                showGPSDisabledAlertToUser();
//            }
//
//        }
        // END_INCLUDE(checkLocationPermission)

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Background Location Permission is granted so do your work here

                    // Location permissions is already available, show the location.
                    Log.i(TAG,
                            "Location permission has already been granted.");

                    getLocation();
                    if (!canGetLocation){
                        showGPSDisabledAlertToUser();
                    }
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage();
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission();
        }

    }

    // New check location permistion
    /* This represents the new check permission functions
       and will be used in the futuer but first we need to test.
     */
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Fine Location permission is granted
            // Check if current android version >= 11, if >= 11 check for Background Location permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Background Location Permission is granted so do your work here
                } else {
                    // Ask for Background Location Permission
                    askPermissionForBackgroundUsage();
                }
            }
        } else {
            // Fine Location Permission is not granted so ask for permission
            askForLocationPermission();
        }
    }

    private void askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Location Permission Needed!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Permission is denied by the user
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        getLocation();
    }

    private void askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed!")
                    .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // User declined for Background Location Permission.
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE);
        }

        getLocation();
    }

    /**
     * New check permissions function from device management
     */
    /*private boolean checkPermissions( boolean startSettings ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // If the user didn't grant permissions, let him know and do not request until he confirms he want to retry
        if (permissionsDialog != null && permissionsDialog.isShowing()) {
            return false;
        }

        if (Utils.isDeviceOwner(this)) {
            if (settingsHelper.getConfig() != null && (ServerConfig.APP_PERMISSIONS_ASK_ALL.equals(settingsHelper.getConfig().getAppPermissions()) ||
                    ServerConfig.APP_PERMISSIONS_ASK_LOCATION.equals(settingsHelper.getConfig().getAppPermissions()))) {
                // Even in device owner mode, if "Ask for location" is requested by the admin,
                // let's ask permissions (so do nothing here, fall through)
            } else {
                // Do not request permissions if we're the device owner
                // They are added automatically
                return true;
            }
        }

        if (preferences.getInt(Const.PREFERENCES_DISABLE_LOCATION, Const.PREFERENCES_OFF) == Const.PREFERENCES_ON) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                if (startSettings) {
                    requestPermissions(new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE
                    }, PERMISSIONS_REQUEST);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return checkLocationPermissions(startSettings);
        }
    }*/

    /*private void createAndShowPermissionsDialog() {
        dismissDialog(permissionsDialog);
        permissionsDialog = new Dialog( this );
        dialogPermissionsBinding = DataBindingUtil.inflate(
                LayoutInflater.from( this ),
                R.layout.dialog_permissions,
                null,
                false );
        permissionsDialog.setCancelable( false );
        permissionsDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );

        permissionsDialog.setContentView( dialogPermissionsBinding.getRoot() );
        permissionsDialog.show();
    }*/


    /**
     * Requests the Location permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestLocationPermission() {
        Log.i(TAG, "Location permission has NOT been granted. Requesting permission.");

        // BEGIN_INCLUDE(location_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION )
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying Location permission rationale to provide additional context.");
            Snackbar.make(mLayout, R.string.permission_location_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.OK, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_LOCATION);
                        }
                    })
                    .show();
        } else {

            // Location permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        }
        getLocation();
        // END_INCLUDE(location_permission_request)
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager)getSystemService(Service.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.d(TAG, "no network provider is enabled");
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d(TAG, "Network is Enabled");
                    if (locationManager != null) {
                        mlocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mlocation != null) {
                            latitude = mlocation.getLatitude();
                            longitude = mlocation.getLongitude();

                            Common.currentLat = latitude;
                            Common.currentLng = longitude;

                            getAddressFromLocation(mlocation, getApplicationContext(), new GeoCoderHandler());
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (mlocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d(TAG, "GPS Enabled");
                        if (locationManager != null) {
                            mlocation = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mlocation != null) {
                                Log.d(TAG, "Location for GPS_PROVIDER not null.");
                                Snackbar.make(mLayout, R.string.location_return_not_null,
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                                //if (!addbtn.isEnabled())
                                //{
                                //    addbtn.setEnabled(true);
                                //}
                                //if (!updatebtn.isEnabled()){
                                //    updatebtn.setEnabled(true);
                                //}
                                // If we are getting values then show the fab button
                                if(!fab.getTag().toString().toLowerCase().equals("customerlist")) {
                                    fab.show();
                                }

                                latitude = mlocation.getLatitude();
                                longitude = mlocation.getLongitude();

                                Common.currentLat = latitude;
                                Common.currentLng = longitude;

                                getAddressFromLocation(mlocation, getApplicationContext(), new GeoCoderHandler());
                            }
                        }
                    }
                }
            }

        } catch (SecurityException se){
            Log.e(TAG, se.getMessage());
            se.printStackTrace();


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();

        }

        return mlocation;
    }

    private void showGPSDisabledAlertToUser() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.CustomAlertDialog);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                canGetLocation = false;
                dialog.cancel();
            }
        });
        androidx.appcompat.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void getAddressFromLocation(final Location location, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality() + ", " +  address.getCountryName() ;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class GeoCoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    break;
                default:
                    result = null;
            }

            //locationDisplay.setText("Lng: "+String.valueOf(longitude)+"Lat: "+String.valueOf(latitude) + ", address: " + result);
            if (result == null){
                result = "Cannot determine address.";
            }

            address = result;
            Common.locationaddress = address;
            //locationDisplay.setText("Location: " + result);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.v(TAG,"Your Location is - \nLat: " + location.getLatitude() + "\nLong: " + location.getLongitude());

        Common.currentLat = latitude;
        Common.currentLng = longitude;

        //Bros we may need to show this button
        if(!fab.getTag().toString().toLowerCase().equals("customerlist")) {
            fab.show();
        }

        // Because we dont have buttons here to enable any more. lets just set this canGetLocation variable to true
        canGetLocation = true;
        getAddressFromLocation(location, getApplicationContext(), new GeoCoderHandler());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for location permission.
            Log.i(TAG, "Received response for Location permission request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_location,
                                Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(TAG, "Location permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                                Snackbar.LENGTH_SHORT)
                        .show();
            }

        }else if (requestCode == REQUEST_BACKGROUND_LOCATION_PERMISSION_CODE) {
            //if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // User granted location permission
                // Now check if android version >= 11, if >= 11 check for Background Location Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Background Location Permission is granted so do your work here
                        // All required permissions have been granted, display contacts fragment.
                        Snackbar.make(mLayout, R.string.background_permision_available_location,
                                        Snackbar.LENGTH_SHORT)
                                .show();
                    } else {
                        // Ask for Background Location Permission
                        askPermissionForBackgroundUsage();
                    }
                }
            } else {
                // User denied location permission
                Log.i(TAG, "Background Location permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                                Snackbar.LENGTH_SHORT)
                        .show();
            }

        }else if (requestCode == 3) {

            Log.d(TAG, "External storage");

            //if(grantResults[0]== PackageManager.PERMISSION_GRANTED) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                bWritePermssonGranted = true;
                Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                Snackbar.make(mLayout, "Storage Permission Granted: " + permissions[0] + "was " + grantResults[0],
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                bWritePermssonGranted = false;
                Snackbar.make(mLayout, "Storage Permission Denied: " + permissions[0] + "was " + grantResults[0],
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }else if (requestCode == INSTALL_PACKAGES_REQUESTCODE){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                String usrName = usernameFromEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                UpdateChecker.checkForDialog(MainActivity.this, usrName);

            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
            }

        }else if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS){

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                SyncAgentAddressBook syncAgentAddressBook = new SyncAgentAddressBook();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                syncAgentAddressBook.execute(email);

            } else {
                Toast.makeText(this, "You have disabled a contacts permission. Application cannot continue.", Toast.LENGTH_LONG).show();
                closeNow();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }

    public void chooseImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent=new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, Common.PICK_IMAGE_REQUEST);
                        }else{
                            Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri saveUri=data.getData();
            if(saveUri!=null){
                final ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                String imageName= UUID.randomUUID().toString();
                final StorageReference imageFolder=storageReference.child("images/"+imageName);

                imageFolder.putFile(saveUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String, Object> avatarUpdate=new HashMap<>();
                                        avatarUpdate.put("avatarUrl", uri.toString());

                                        DatabaseReference userInfo=FirebaseDatabase.getInstance().getReference(Common.USER_NODE);
                                        userInfo.child(Common.userID).updateChildren(avatarUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful())
                                                            Toast.makeText(MainActivity.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(MainActivity.this, "Uploaded error!", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded "+progress+"%");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed to Upload Imagine!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else if (requestCode == GET_UNKNOWN_APP_SOURCES){
            String usrName = usernameFromEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            UpdateChecker.checkForDialog(MainActivity.this, usrName);
        }
    }


    public void onAuthSuccess(String userid) {

        AgentTracking agentTracking = new AgentTracking();
        agentTracking.setUserid(userid);

        // Go to MainActivity
        // Changed by Satya, to pass vehicle id to driver activity
        Bundle bundle = new Bundle();
        Intent dataServiceIntent = new Intent(this, LocationSharingService.class);
        bundle.putSerializable(userid, agentTracking);
        bundle.putString("userid", userid);
        dataServiceIntent.putExtras(bundle);
        dataServiceIntent.setAction(LocationSharingService.ACTION_START_FOREGROUND_SERVICE);

        // Start Location Monitoring Service
        startService(dataServiceIntent);

        // Add the notification panel on the task bar
        //addAgentTrackingNotification(agentTracking);

    }

    private void startCommentMonitoringService(){
        // Start Comment Notification Service
        startService(new Intent(this, CommentService.class));
    }

    private void startGeofencingService(){

        Intent geoIntent = new Intent(this, GeofencingService.class);
        /*
        geoIntent.putExtra("logName", "MAIN_ACTIVITY");
        geoIntent.putExtra(GeofenceService.BUNDLED_LISTENER, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);

                String val = resultData.getString("status");
                if (resultCode == Activity.RESULT_OK) {
                    AddStatusMessage("[" + val + "]");
                } else {
                    AddStatusMessage("ERR [" + val + "]");
                }
            }
        });

         */
        startService(geoIntent);
    }

    //@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    private void addNotification(AgentTracking agentTracking) {
        Intent stopSharingIntent = new Intent(this, NotificationReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(agentTracking.getUserid(), agentTracking);
        bundle.putString("userid", agentTracking.getUserid());
        stopSharingIntent.putExtras(bundle);

        PendingIntent pStopIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), stopSharingIntent, 0);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Notification.Action action = new Notification.Action.Builder(android.R.drawable.screen_background_light_transparent, "Stop Sharing", pStopIntent).build();

        Notification n  = new Notification.Builder(this)
                .setContentTitle("Agent Tracking System")
                .setContentText("Location is being Shared")
                .setSmallIcon( R.drawable.ic_daitech_notification_logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .addAction(action)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build();

        NotificationManager notificationManager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Agent Tracking System";
            String description = "Agent Real Time Tracking";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }else {

            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        notificationManager.notify(0, n);
    }

    private void addAgentTrackingNotification(AgentTracking agentTracking){

        Intent stopSharingIntent = new Intent(this, NotificationReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(agentTracking.getUserid(), agentTracking);
        bundle.putString("userid", agentTracking.getUserid());
        stopSharingIntent.putExtras(bundle);

        PendingIntent pStopIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) System.currentTimeMillis(), stopSharingIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(android.R.drawable.screen_background_light_transparent, "Stop Sharing", pStopIntent).build();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "agent_tracking_system");

        Intent ii = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,  (int) System.currentTimeMillis(), ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Location is being monitored by regional Supervisors and HQ.");
        bigText.setBigContentTitle("Agent Workforce Monitoring System");
        bigText.setSummaryText("Monitor");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.addAction(action);
        mBuilder.setSmallIcon(R.drawable.ic_daitech_notification_logo);
        mBuilder.setContentTitle("Agent Tracking System");
        mBuilder.setContentText("Location is being Shared");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )// Android.OS.BuildVersionCodes.O)
        {
            String channelId = "agent_tracking_channel";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Daitech Workforce Tracking System",
                    NotificationManager.IMPORTANCE_DEFAULT);//Android.App.NotificationImportance.Default);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        Notification notification = mBuilder.build();
        mNotificationManager.notify(0, notification);

        // Set forground stuff
        //int ONGOING_NOTIFICATION_ID = 1001;
        //startForeground(ONGOING_NOTIFICATION_ID, notification);

    }

    //broadcast receiver to get notification when the web request finishes
    public class VersionCheckServiceReceiver extends BroadcastReceiver {

        //public static final String PROCESS_RESPONSE = "com.as400samplecode.intent.action.PROCESS_RESPONSE";
        public static final String PROCESS_RESPONSE = "ique.daitechagent.versioncheckservice.action.PROCESS_RESPONSE";

        public VersionCheckServiceReceiver(){
            Log.v(TAG, "VersionCheckServiceReceiver: constructor");
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String reponseMessage = intent.getStringExtra(VersionCheckService.RESPONSE_MESSAGE);
            Log.v(TAG, reponseMessage);

            //parse the JSON response
            JSONObject responseObj;
            try {
                responseObj = new JSONObject(reponseMessage);
                boolean success = responseObj.getBoolean("success");
                //if the reponse was successful check further
                if(success){
                    //get the latest version from the JSON string
                    int latestVersion = responseObj.getInt("latestVersion");
                    //get the lastest application URI from the JSON string
                    appURI = responseObj.getString("appURI");
                    //check if we need to upgrade?
                    if(latestVersion > versionCode && bWritePermssonGranted){
                        //oh yeah we do need an upgrade, let the user know send an alert message
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
                        builder.setMessage("There is newer version of this application available, click OK to upgrade now?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    //if the user agrees to upgrade
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (isSDCardPresent()) {

                                        }else{
                                            Toast.makeText(getApplicationContext(), "No External SD Card Present.", Toast.LENGTH_SHORT);
                                        }
                                    }
                                })
                                .setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        //show the alert message
                        builder.create().show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    /*
    //broadcast receiver to get notification when the web request finishes
    public class VersionCheckServiceReceiver extends BroadcastReceiver {

        //public static final String PROCESS_RESPONSE = "com.as400samplecode.intent.action.PROCESS_RESPONSE";
        public static final String PROCESS_RESPONSE = "ique.daitechagent.versioncheckservice.action.PROCESS_RESPONSE";

        public VersionCheckServiceReceiver(){
            Log.v(TAG, "VersionCheckServiceReceiver: constructor");
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String reponseMessage = intent.getStringExtra(VersionCheckService.RESPONSE_MESSAGE);
            Log.v(TAG, reponseMessage);

            //parse the JSON response
            JSONObject responseObj;
            try {
                responseObj = new JSONObject(reponseMessage);
                boolean success = responseObj.getBoolean("success");
                //if the reponse was successful check further
                if(success){
                    //get the latest version from the JSON string
                    int latestVersion = responseObj.getInt("latestVersion");
                    //get the lastest application URI from the JSON string
                    appURI = responseObj.getString("appURI");
                    //check if we need to upgrade?
                    if(latestVersion > versionCode && bWritePermssonGranted){
                        //oh yeah we do need an upgrade, let the user know send an alert message
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
                        builder.setMessage("There is newer version of this application available, click OK to upgrade now?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    //if the user agrees to upgrade
                                    public void onClick(DialogInterface dialog, int id) {

                                        if (isSDCardPresent()) {
                                            //start downloading the file using the download manager
                                            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                            Uri Download_Uri = Uri.parse(appURI);
                                            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                            //request.setAllowedOverRoaming(false);
                                            request.setTitle("Work force Agent");
                                            request.setDescription("Daitech Pharmaceutical Medical Agent Mobile App.");

                                            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                                            String fileName = "app-debug.apk";
                                            destination += fileName;
                                            final Uri uri = Uri.parse("file://" + destination);
                                            // Delete update file if exists
                                            File file = new File(destination);
                                            if (file.exists())
                                                //file.delete() - test this, I think sometimes it doesnt work
                                                file.delete();

                                            request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "app-debug.apk");
                                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                            downloadReference = downloadManager.enqueue(request);
                                        }else{
                                            Toast.makeText(getApplicationContext(), "No External SD Card Present.", Toast.LENGTH_SHORT);
                                        }
                                    }
                                })
                                .setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        //show the alert message
                        builder.create().show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    */
    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public  boolean isPhoneCallPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            //if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //        == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Call Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Call Permission is revoked");
                //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
                    builder.setTitle("Read Agents Info permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts. It is required for the application to run.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                //getContacts();
                SyncAgentAddressBook syncAgentAddressBook = new SyncAgentAddressBook();
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                syncAgentAddressBook.execute(email);
            }
        } else {
            //getContacts();
            SyncAgentAddressBook syncAgentAddressBook = new SyncAgentAddressBook();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            syncAgentAddressBook.execute(email);
        }
    }

    //broadcast receiver to get notification about ongoing downloads
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadReference == referenceId){

                Log.v(TAG, "Downloading of the new Workforce app complete");
                //start the installation of the latest version
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadReference),
                        "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installIntent);

            }
        }
    };

    // Background process to copy out all the address book entry of the agent
    private class SyncAgentAddressBook extends AsyncTask<String, Void, AgentAddressBookResult>{

        private final String DISPLAY_NAME = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;

        private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

        private final String ORDER = String.format("%1$s COLLATE NOCASE", DISPLAY_NAME);

        @SuppressLint("InlinedApi")
        private final String[] PROJECTION = {
                ContactsContract.Contacts._ID,
                DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };

        @Override
        protected AgentAddressBookResult doInBackground(String... strings) {

            try {
                String agentEmail = strings[0];

                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION, FILTER, null, ORDER);

                if (cursor != null && cursor.moveToFirst()) {

                    do {
                        // get the contact's information
                        String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(DISPLAY_NAME));
                        Integer hasPhone = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        // get the user's email address
                        String email = null;
                        Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (ce != null && ce.moveToFirst()) {
                            email = ce.getString(ce.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
                            ce.close();
                        }

                        // get the user's phone number
                        String phone = null;
                        if (hasPhone > 0) {
                            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (cp != null && cp.moveToFirst()) {
                                phone = cp.getString(cp.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                cp.close();
                            }
                        }

                        // if the user user has an email or phone then add it to contacts
                        if (!TextUtils.isEmpty(phone)) {
                            /*Contact contact = new Contact();
                            contact.name = name;
                            contact.email = email;
                            contact.phoneNumber = phone;
                            contacts.add(contact);*/




                            IRetrofitAgentAddressBookData apiAgentAddressBook = AgentAddressBookDataClient.getRetrofitAgentAddressBookInstance().create(IRetrofitAgentAddressBookData.class);
                            Call<AgentAddressBookResult> call = apiAgentAddressBook.insertAddressBookEntry(agentEmail, name, phone);


                            try {

                                AgentAddressBookResult insertAction = call.execute().body();
                                Log.i(TAG, "Address Book Upload: " + insertAction.toString());
                                //return insertAction;

                            } catch (IOException e) {
                            } catch (Exception ex) {
                            }
                        }
                        //return null;

                    } while (cursor.moveToNext());

                    // clean up cursor
                    cursor.close();
                }

                return null;

            } catch (Exception ex) {
                return null;
            }
        }
    }

    private class SyncCustomerDatabases extends AsyncTask<String, Void, CustomerResult>{
        @Override
        protected void onPreExecute() {
            //waitingDialogCustomers.show();
        }

        @Override
        protected void onPostExecute(CustomerResult customerResult) {
            try {
                if (customerResult.isResult()) {
                    ArrayList<Customer> customerList = customerResult.getCustomers();

                    db.deleteAllCustomers();
                    db.refreshCustomerList(customerList);
                    //getCustomerListSQLITE();

                    Toast.makeText(getApplicationContext(), "Customer Sync successful.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Error syncing customer details. " + customerResult.getErrormsg(), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                //waitingDialogCustomers.dismiss();
                Log.e(TAG,ex.getMessage(), ex);
            }

            //waitingDialogCustomers.dismiss();
        }

        @Override
        protected CustomerResult doInBackground(String... strings) {

            String email = strings[0];

            IRetrofitCustomerData apiCustomer = CustomerDataClient.getRetrofitCustomerInstance().create(IRetrofitCustomerData.class);
            Call<CustomerResult> call = apiCustomer.getCutomers(email);


            try {

                CustomerResult searchResult = call.execute().body();
                return searchResult;

            } catch (IOException e) {

                //waitingDialogCustomers.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        //Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {

                //waitingDialogCustomers.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "A general error occured.", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(getApplicationContext(), "A general error occured.", Toast.LENGTH_SHORT).show();

            }

            return null;

        }
    }

    private class SyncAgentRegion extends AsyncTask<String, Void, RegionQueryResult>{
        @Override
        protected void onPreExecute() {
            //waitingDialogCustomers.show();
        }

        @Override
        protected void onPostExecute(RegionQueryResult regionQueryResult) {
            try {
                if (regionQueryResult.isResult()) {
                    String region = regionQueryResult.getRegion();

                    View nav = navigationView.getHeaderView(0);
                    TextView tvAgentRegions= nav.findViewById(R.id.agentRegion);
                    tvAgentRegions.setText(region);

                    firebaseHelper.updateUserRegion(region);

                    Toast.makeText(getApplicationContext(), "Region Set.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Error updating region details. " + regionQueryResult.getErrormsg(), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                //waitingDialogCustomers.dismiss();
                Log.e(TAG,ex.getMessage(), ex);
            }

            //waitingDialogCustomers.dismiss();
        }

        @Override
        protected RegionQueryResult doInBackground(String... strings) {

            String email = strings[0];

            IRetrofitRegions apiRegion = RegioningClient
                    .getRetrofitRegioningInstance()
                    .create(IRetrofitRegions.class);

            Call<RegionQueryResult> call = apiRegion.getRegion(email, Common.REGION_QUERY_TYPE);


            try {

                RegionQueryResult result = call.execute().body();
                return result;

            } catch (IOException e) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "A general error occured.", Toast.LENGTH_SHORT).show();
                    }
                });
                //Toast.makeText(getApplicationContext(), "A general error occured.", Toast.LENGTH_SHORT).show();

            }

            return null;

        }
    }



    public boolean isSDCardPresent() {
        return Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED);
    }

}
