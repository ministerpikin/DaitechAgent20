package ique.daitechagent.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
/*

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
*/

import ique.daitechagent.R;
import ique.daitechagent.common.Common;
import ique.daitechagent.helpers.FirebaseHelper;
import ique.daitechagent.messages.Errors;
import ique.daitechagent.messages.Message;
//import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.calligraphy3.FontMapper;
import io.github.inflationx.viewpump.ViewPump;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity { //} implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Login";

    //private GoogleApiClient googleApiClient;
    public static final int SIGN_IN_CODE_GOOGLE=157;
    Button btnSignIn, btnLogIn, btnResetPwd;

    FirebaseHelper firebaseHelper;
    //GoogleSignInAccount account;

    private boolean bGooglePlayAvaiable;

    //facebook
    //CallbackManager mFacebookCallbackManager;
    //LoginManager mLoginManager;
    //AccessToken accessToken = AccessToken.getCurrentAccessToken();
    boolean isLoggedIn = false; //accessToken != null && !accessToken.isExpired();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            FirebaseHelper.persistData();
        }catch(Exception ex){
            Log.e("FirebaseHelper", ex.getMessage(), ex);
        }

        /*
         *  This would not be necessary any more
         */
        /*
        if(!Common.isNetworkAvailable(TAG, this)){
            Intent intentNetwork = new Intent(Login.this, NetworkAvailability.class);
            startActivity(intentNetwork);
            finish();
            return;
        }
         */



        bGooglePlayAvaiable = Common.isGooglePlayServicesAvailable(this);
        if (!bGooglePlayAvaiable) {
            try {
                Snackbar.make(findViewById(R.id.root), "Google Play Services Unavailable", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }catch (Exception ex){
                Toast.makeText(this, "Google Play Services Unavailable", Toast.LENGTH_LONG).show();
            }
        }



        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/NotoSans.ttf").setFontAttrId(R.attr.fontPath).build());

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/NotoSans.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .setFontMapper(new FontMapper() {
                                    @Override
                                    public String map(String font) {
                                        return font;
                                    }
                                })
                                .addCustomViewWithSetTypeface(CustomViewWithTypefaceSupport.class)
                                .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                                .build()))
                .build());*/
        setContentView(R.layout.activity_login);
        firebaseHelper=new FirebaseHelper(this);

        //FancyButton signInButtonGoogle=findViewById(R.id.login_button_Google);
        //FancyButton signInButtonFacebook=findViewById(R.id.facebookLogin);

//        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        googleApiClient=new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
        /*
        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE_GOOGLE);
            }
        });
        setupFacebookStuff();
        signInButtonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccessToken.getCurrentAccessToken() != null){
                    mLoginManager.logOut();
                } else {
                    mLoginManager.logInWithReadPermissions(Login.this, Arrays.asList("email", "user_birthday", "public_profile"));
                }
            }
        });

         */
        btnSignIn=findViewById(R.id.btnSignin);
        btnLogIn=findViewById(R.id.btnLogin);
        btnResetPwd=findViewById(R.id.btnPwdReset);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firebaseHelper.showRegistrerDialog();
                firebaseHelper.showRegistrerDialogAsync();
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //firebaseHelper.showLoginDialog();
                firebaseHelper.showLoginDialogAsync();
            }
        });
        btnResetPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.resetPasswordFirebase();
            }
        });
    }

    /*private void verifyGoogleAccount() {
        OptionalPendingResult<GoogleSignInResult> opr= Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()){
            GoogleSignInResult result= opr.get();
            if (result.isSuccess())
                firebaseHelper.loginSuccess(result.getSignInAccount().getId());

        }
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if(isLoggedIn){
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
        //verifyGoogleAccount();
    }

    /*private void setupFacebookStuff() {

        // This should normally be on your application class
        FacebookSdk.sdkInitialize(getApplicationContext());

        mLoginManager = LoginManager.getInstance();
        mFacebookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //login
                firebaseHelper.registerByFacebookAccount();
            }

            @Override
            public void onCancel() {
                Toast.makeText(Login.this,"The login was canceled",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(Login.this,"There was an error in the login",Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode==SIGN_IN_CODE_GOOGLE) {//Google
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        */
    }

    /*private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            account = result.getSignInAccount();
            firebaseHelper.registerByGoogleAccount(account);
        }else{
            Message.messageError(this, Errors.ERROR_LOGIN_GOOGLE);
        }
    }*/

    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Message.messageError(this, Errors.ERROR_LOGIN_GOOGLE);
    }*/

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    /*@Override
    protected void attachBaseContext(Context newBase) {
        //super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/





}

