package ique.daitechagent.common;

//import com.iramml.uberclone.Interfaces.IFCMService;
//import com.iramml.uberclone.Interfaces.googleAPIInterface;
//import com.iramml.uberclone.Model.User;
//import com.iramml.uberclone.Retrofit.FCMClient;
//import com.iramml.uberclone.Retrofit.RetrofitClient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import ique.daitechagent.interfaces.IFCMService;
import ique.daitechagent.interfaces.googleAPIInterface;

import ique.daitechagent.model.User;
import ique.daitechagent.retrofit.FCMClient;
import ique.daitechagent.retrofit.RetrofitClient;

public class Common {
    public static final String driver_tbl="Drivers";
    public static final String user_driver_tbl="DriversInformation";
    public static final String history_driver = "DriverHistory";
    public static final String history_rider = "RiderHistory";
    public static final String user_rider_tbl="RidersInformation";
    public static final String pickup_request_tbl="PickupRequest";
    public static final String token_tbl="Tokens";
    public static User currentUser;
    public static String userID;
    public static String userPW;
    public static final int PICK_IMAGE_REQUEST = 9999;

    public static final String USER_NODE = "users";
    public static final String REPORT_NODE = "report";
    public static final String CUSTOMER_NODE = "customers";
    public static final String DAILY_SCHEDULE_NODE = "dailyplan";
    public static final String SCAN_BARCODE_NODE = "barcodescan";
    public static final String SCAN_PRODUCT_NODE = "product";
    public static final String REPORT_COMMENT_NODE = "report-comments";
    public static final String REPORT_COMMENT_NOTIFICATION_NODE = "report-notification";
    public static final String REPORT_COMMENT_NOTIFICATION_MANAGER_NODE = "manager";
    public static final String REPORT_COMMENT_NOTIFICATION_SUPERVISOR_NODE = "supervisor";
    public static final String REPORT_COMMENT_NOTIFICATION_AGENT_NODE = "agent";
    public static final String USER_PRESENCE_NODE = "user-presence";
    public static final String USER_LAST_LOGGED_IN_NODE = "user-lastlogin";
    public static final String GEOFENCE_NODE = "geofences";
    public static final String GEOFENCE_NOTIFICATION_NODE = "geofences-notification";

    public static final String DOMAIN_NAME = "daitechpharma.com";
    public static final String DATABASE_NAME = "diatechagent.db";

    public static final int REGION_QUERY_TYPE = 1;

    public static Double currentLat;
    public static Double currentLng;
    public static String locationaddress;

    public static final String baseURL="https://maps.googleapis.com";
    public static final String fcmURL="https://fcm.googleapis.com/";

    // Graph URL
    public static final String graphDataBaseURL="http://"+DOMAIN_NAME+"/php_stock/chartutils/";
    // Customer Sync URL
    public static final String customerDataBaseURL="http://"+DOMAIN_NAME+"/php_stock/webservicelib/customerutil/";
    // Regioning sync URL
    public static final String regionBaseURL="http://"+DOMAIN_NAME+"/php_stock/webservicelib/regioning/";
    // AuthenticationClient URL
    public static final String authenticationDataBaseURL="http://"+DOMAIN_NAME+"/php_stock/webservicelib/authentication/";

    // Agent Address book URL
    public static final String agentAddressBookURL="http://"+DOMAIN_NAME+"/php_stock/webservicelib/agentaddressbook/";

    public static double baseFare=2.55;
    private static double timeRate=0.35;
    private static double distanceRate=1.75;

    public static double formulaPrice(double km, double min){
        return baseFare+(distanceRate*km)+(timeRate*min);
    }
    public static googleAPIInterface getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(googleAPIInterface.class);
    }
    public static IFCMService getFCMService(){
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimum value
     * @param max Maximum value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        //
        // In particular, do NOT do 'Random rand = new Random()' here or you
        // will get not very good / not very random results.
        // Random rand;

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        // int randomNum = rand.nextInt((max - min) + 1) + min;

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        return randomNum;
    }

    /*
    public static boolean isNetworkAvailable (String TAG, Context context) {
        if (connectedToTheNetwork(TAG, context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection)
                        (new URL(customerDataBaseURL)
                                .openConnection());
                urlc.setRequestProperty("User-Agent", "Android");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 204 &&
                        urlc.getContentLength() == 0);

            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            } catch (Exception ex) {
                Log.e(TAG, "Error checking internet connection", ex);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }
    */
    //check for internet connection
    public static boolean isNetworkAvailable(String TAG, Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    Log.v(TAG,String.valueOf(i));
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.v(TAG, "connected!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static String lastSeenProper(String lastSeenDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date currentDate = new Date();
        String cuurentDateString = dateFormat.format(currentDate);
        Date nw = null;
        Date seen = null;
        try {
            nw = dateFormat.parse(cuurentDateString);
            seen = dateFormat.parse(lastSeenDate);
            long diff = nw.getTime() - seen.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffMinutes = diff / (60 * 1000) % 60;
            if (diffDays > 0) {
                String[] originalDate = lastSeenDate.split(" ");
                return "Last seen " + originalDate[0] + " " + Common.toCharacterMonth(Integer.parseInt(originalDate[1])) + " " + originalDate[2];
            } else if (diffHours > 0)
                return "Last seen " + diffHours + " hours ago";
            else if (diffMinutes > 0) {
                if (diffMinutes <= 1) {
                    return "Last seen 1 minute ago";
                } else {
                    return "Last seen " + diffMinutes + " minutes ago";
                }
            } else return "Last seen a moment ago";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


    }

    public static String toCharacterMonth(int month) {
        if (month == 1) return "Jan";
        else if (month == 2) return "Feb";
        else if (month == 3) return "Mar";
        else if (month == 4) return "Apr";
        else if (month == 5) return "May";
        else if (month == 6) return "Jun";
        else if (month == 7) return "Jul";
        else if (month == 8) return "Aug";
        else if (month == 9) return "Sep";
        else if (month == 10) return "Oct";
        else if (month == 11) return "Nov";
        else return "Dec";
    }

    public static int createUniqueIdPerUser(String userEmail) {
        String email = userEmail.split("@")[0].toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
        final Map<Character, Integer> map;
        map = new HashMap<>();
        map.put('a', 1);
        map.put('b', 2);
        map.put('c', 3);
        map.put('d', 4);
        map.put('e', 5);
        map.put('f', 6);
        map.put('g', 7);
        map.put('h', 8);
        map.put('i', 9);
        map.put('j', 10);
        map.put('k', 11);
        map.put('l', 12);
        map.put('m', 13);
        map.put('n', 14);
        map.put('o', 15);
        map.put('p', 16);
        map.put('q', 17);
        map.put('r', 18);
        map.put('s', 19);
        map.put('t', 20);
        map.put('u', 21);
        map.put('v', 22);
        map.put('w', 23);
        map.put('x', 24);
        map.put('y', 25);
        map.put('z', 26);
        String intEmail = "";

        for (char c : email.toCharArray()) {
            int val = 0;
            try {
                val = map.get(c);
            } catch (Exception e) {

            }
            intEmail += val;
        }

        if (intEmail.length() > 9) {
            intEmail = intEmail.substring(0, 9);
        }

        return Integer.parseInt(intEmail);

    }

    public static final String SYS_ADMIN_EMAIL = "pokoroji@daitechpharma.com:pokoroji@gmail.com:pokoroji1@gmail.com";
}
