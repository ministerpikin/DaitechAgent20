package ique.daitechagent.helpers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.AccessToken;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import android.app.AlertDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ique.daitechagent.activities.MainActivity;
import ique.daitechagent.common.Common;
import ique.daitechagent.R;
import ique.daitechagent.database.DataContext;
import ique.daitechagent.fragments.CustomerList;
import ique.daitechagent.model.AuthenticationQueryResult;
import ique.daitechagent.model.Customer;
import ique.daitechagent.model.CustomerResult;
import ique.daitechagent.model.Dailyschedule;
import ique.daitechagent.model.Report;
import ique.daitechagent.model.User;
import ique.daitechagent.retrofit.CustomerDataClient;
import ique.daitechagent.retrofit.IRetrofitCustomerData;
import ique.softwareupdate.update.AppUtils;
import retrofit2.Call;

public class FirebaseHelper {

    private final static String TAG = "FirebaseHelper";
    AppCompatActivity activity;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference users, reports, customers, drefdailyschedule;

    ConstraintLayout root;

    Spinner schedulePeriod;
    AlertDialog waitingDialogCustomers;

    private DataContext db;

    public FirebaseHelper(AppCompatActivity activity){
        this.activity=activity;
        root=activity.findViewById(R.id.root);



        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        users=firebaseDatabase.getReference(Common.USER_NODE);
        reports=firebaseDatabase.getReference(Common.REPORT_NODE);
        customers=firebaseDatabase.getReference(Common.CUSTOMER_NODE);
        drefdailyschedule=firebaseDatabase.getReference(Common.DAILY_SCHEDULE_NODE);



        db = new DataContext(activity, null, null, 1);

        waitingDialogCustomers = new SpotsDialog(activity);

        if(firebaseAuth.getUid()!=null)
            loginSuccess(firebaseAuth.getCurrentUser().getUid());
    }

    public FirebaseHelper(AppCompatActivity activity, boolean alreadysignedin){
        this.activity=activity;
        root=activity.findViewById(R.id.content_main);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        users=firebaseDatabase.getReference(Common.USER_NODE);
        reports=firebaseDatabase.getReference(Common.REPORT_NODE);
        customers=firebaseDatabase.getReference(Common.CUSTOMER_NODE);
        drefdailyschedule=firebaseDatabase.getReference(Common.DAILY_SCHEDULE_NODE);

        db = new DataContext(activity, null, null, 1);

        waitingDialogCustomers = new SpotsDialog(activity);

        if(firebaseAuth.getUid()!=null){
            if (!alreadysignedin)
                loginSuccess(firebaseAuth.getCurrentUser().getUid());
        }
    }

    public void showLoginDialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle(activity.getResources().getString(R.string.login));
        alertDialog.setMessage(activity.getResources().getString(R.string.fill_fields));

        LayoutInflater inflater= LayoutInflater.from(activity);
        View login_layout=inflater.inflate(R.layout.layout_login, null);
        final MaterialEditText etEmail=login_layout.findViewById(R.id.etEmail);
        final MaterialEditText etPassword=login_layout.findViewById(R.id.etPassword);

        alertDialog.setView(login_layout);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //btnLogIn.setEnabled(false);
                if (TextUtils.isEmpty(etEmail.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_email), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_password), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length()<6){
                    Snackbar.make(root, activity.getResources().getString(R.string.password_short), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                final String password = etPassword.getText().toString();

                final SpotsDialog waitingDialog=new SpotsDialog(activity);
                waitingDialog.show();

                // Lets check to see if this email exists on the platform
                if (!isUserValid(etEmail.getText().toString())){
                    waitingDialog.dismiss();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Lets start the service
                        // onAuthSuccess(firebaseAuth.getCurrentUser().getUid());

                        waitingDialog.dismiss();
                        //goToMainActivity();
                        // In case the account has been created but user profile hasnt
                        // we need to save the password
                        Common.userPW = password;

                        Snackbar.make(root, "Successfully logged in: ", Snackbar.LENGTH_SHORT).show();

                        loginSuccess(firebaseAuth.getCurrentUser().getUid());
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(root, "Invalid Credentials :" + activity.getResources().getString(R.string.failed) +e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        //btnLogIn.setEnabled(true);
                    }
                });
            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void showLoginDialogAsync(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle(activity.getResources().getString(R.string.login));
        alertDialog.setMessage(activity.getResources().getString(R.string.fill_fields));

        LayoutInflater inflater= LayoutInflater.from(activity);
        View login_layout=inflater.inflate(R.layout.layout_login, null);
        final MaterialEditText etEmail=login_layout.findViewById(R.id.etEmail);
        final MaterialEditText etPassword=login_layout.findViewById(R.id.etPassword);

        alertDialog.setView(login_layout);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //btnLogIn.setEnabled(false);
                if (TextUtils.isEmpty(etEmail.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_email), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_password), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length()<6){
                    Snackbar.make(root, activity.getResources().getString(R.string.password_short), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                final String password = etPassword.getText().toString();


                if ( Common.isNetworkAvailable(TAG, activity )) {

                    doAsyncLogIn(etEmail.getText().toString(), etPassword.getText().toString());

                }else {
                    Snackbar.make(root, "Please check your internet connection. Customers cannot be synced.", Snackbar.LENGTH_SHORT).show();
                }



            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void showRegistrerDialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle(activity.getResources().getString(R.string.signin));
        alertDialog.setMessage(activity.getResources().getString(R.string.fill_fields));

        LayoutInflater inflater= LayoutInflater.from(activity);
        View registrer_layout=inflater.inflate(R.layout.layout_register, null);
        final MaterialEditText etEmail=registrer_layout.findViewById(R.id.etEmail);
        final MaterialEditText etPassword=registrer_layout.findViewById(R.id.etPassword);
        final MaterialEditText etName=registrer_layout.findViewById(R.id.etName);
        final MaterialEditText etPhone=registrer_layout.findViewById(R.id.etPhone);

        alertDialog.setView(registrer_layout);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (TextUtils.isEmpty(etEmail.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_email), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_password), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length()<6){
                    Snackbar.make(root, activity.getResources().getString(R.string.password_short), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etName.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_name), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPhone.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_phone), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Lets check to see if this email exists on the platform
                if (!isUserValid(etEmail.getText().toString())){
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user=new User();
                                user.setEmail(etEmail.getText().toString());
                                user.setUsername(etName.getText().toString());
                                user.setPassword(etPassword.getText().toString());
                                user.setPhone(etPhone.getText().toString());
                                user.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                // Update appversion
                                user.setAppversion(AppUtils.getVersionCode(activity));

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(root, "Registered", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void showRegistrerDialogAsync(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle(activity.getResources().getString(R.string.signin));
        alertDialog.setMessage(activity.getResources().getString(R.string.fill_fields));

        LayoutInflater inflater= LayoutInflater.from(activity);
        View registrer_layout=inflater.inflate(R.layout.layout_register, null);
        final MaterialEditText etEmail=registrer_layout.findViewById(R.id.etEmail);
        final MaterialEditText etPassword=registrer_layout.findViewById(R.id.etPassword);
        final MaterialEditText etName=registrer_layout.findViewById(R.id.etName);
        final MaterialEditText etPhone=registrer_layout.findViewById(R.id.etPhone);

        alertDialog.setView(registrer_layout);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.register), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                if (TextUtils.isEmpty(etEmail.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_email), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPassword.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_password), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length()<6){
                    Snackbar.make(root, activity.getResources().getString(R.string.password_short), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etName.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_name), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etPhone.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_phone), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // Do the necessary checks
                if ( Common.isNetworkAvailable(TAG, activity )) {

                    doAsyncCreateAccount(etEmail.getText().toString(), etPassword.getText().toString(), etName.getText().toString(), etPhone.getText().toString());

                }else {
                    Snackbar.make(root, "Please check your internet connection. Customers cannot be synced.", Snackbar.LENGTH_SHORT).show();
                }


            }
        });

        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }


    public void showRegisterPhone(final User user, final GoogleSignInAccount account){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle(activity.getResources().getString(R.string.signin));
        alertDialog.setMessage(activity.getResources().getString(R.string.fill_fields));

        LayoutInflater inflater= LayoutInflater.from(activity);
        View register_phone_layout=inflater.inflate(R.layout.layout_register_phone, null);
        final MaterialEditText etPhone=register_phone_layout.findViewById(R.id.etPhone);

        alertDialog.setView(register_phone_layout);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                user.setEmail(account.getEmail());
                user.setUsername(account.getDisplayName());
                user.setPassword(null);
                user.setPhone(etPhone.getText().toString());
                user.setUserID(account.getId());

                //user.setCarType("UberX");
                /*
                users.child(account.getId())
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(root, activity.getResources().getString(R.string.registered), Snackbar.LENGTH_SHORT).show();
                                loginSuccess();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                });
                */

                users.child(account.getId())
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(root, activity.getResources().getString(R.string.registered), Snackbar.LENGTH_SHORT).show();
                                loginSuccess(account.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                });
            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
    public void showRegisterPhone(final User user, final String id, final String name, final String email){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle(activity.getResources().getString(R.string.signin));
        alertDialog.setMessage(activity.getResources().getString(R.string.fill_fields));

        LayoutInflater inflater= LayoutInflater.from(activity);
        View register_phone_layout=inflater.inflate(R.layout.layout_register_phone, null);
        final MaterialEditText etPhone=register_phone_layout.findViewById(R.id.etPhone);

        alertDialog.setView(register_phone_layout);
        alertDialog.setPositiveButton(activity.getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                user.setEmail(email);
                user.setUsername(name);
                //user.setPassword(null);
                user.setPhone(etPhone.getText().toString());
                user.setUserID(id);
                //user.setCarType("UberX");

                users.child(id)
                        .setValue(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(root, activity.getResources().getString(R.string.registered), Snackbar.LENGTH_SHORT).show();
                                loginSuccess(id);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                });
            }
        });
        alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //LoginManager.getInstance().logOut();
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }
    public void loginSuccess(String userid){
        //onAuthSuccess(userid);
        goToMainActivity();
    }
    private void goToMainActivity(){
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }
    /*public void registerByGoogleAccount(final GoogleSignInAccount account){
        final User user=new User();
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //User post = dataSnapshot.child(account.getId()).getValue(User.class);
                User post = dataSnapshot.child(account.getId()).getValue(User.class);

                if(post==null) {
                    showRegisterPhone(user, account);
                }
                else {
                    //onAuthSuccess(account.getId());
                    loginSuccess(account.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void registerByFacebookAccount(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        final String name=object.optString("name");
                        final String id=object.optString("id");
                        final String email=object.optString("email");
                        final User user=new User();
                        users.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User post = dataSnapshot.child(id).getValue(User.class);

                                if(post==null) {
                                    showRegisterPhone(user, id, name, email);
                                }
                                else {
                                    //onAuthSuccess(id);
                                    loginSuccess(id);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
        request.executeAsync();
    }*/

    public void resetPasswordFirebase(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle("CHANGE PASSWORD");


        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout_pwd = inflater.inflate(R.layout.layout_forgot_pwd, null);

        final MaterialEditText edtEmail = layout_pwd.findViewById(R.id.edtEmail);

        alertDialog.setView(layout_pwd);

        alertDialog.setPositiveButton("REST PASSWORD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(activity);
                waitingDialog.show();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(edtEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //Toast.makeText(activity, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(root, "We have sent you instructions to reset your password!", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(activity, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(root, "Failed to send reset email!", Snackbar.LENGTH_SHORT).show();
                                }

                                waitingDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        //Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });

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

    public void addReport(final double latitude, final double longitude, final String address){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle("ADD REPORT");


        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout_pwd = inflater.inflate(R.layout.layout_add_report, null);

        //final MaterialEditText customerName = (MaterialEditText) layout_pwd.findViewById(R.id.customerName);
        final MaterialEditText customerReport = layout_pwd.findViewById(R.id.customerReport);


        final Spinner customerName = layout_pwd.findViewById(R.id.customerName);

        List<String> customerNameList = getCustomerDropDownList();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_selected, customerNameList) {

            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);

                } else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        customerName.setAdapter(arrayAdapter);

        //int spinnerPosition = arrayAdapter.getPosition(edcustomerName);
        //customerName.setSelection(spinnerPosition);

        /*
        customers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<String> arrCustomername = new ArrayList<String>();
                        arrCustomername.add("Select Customer...");
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String sCustomerName = dataSnapshot1.child("customername").getValue(String.class);
                            String sCustomerNumber = dataSnapshot1.child("customernumber").getValue(String.class);
                            arrCustomername.add(sCustomerName + " [" + sCustomerNumber + "]");
                        }
                        //ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, arrCustomername);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_selected, arrCustomername){

                            @Override
                            public boolean isEnabled(int position){
                                if(position == 0)
                                {
                                    // Disable the first item from Spinner
                                    // First item will be use for hint
                                    return false;
                                }
                                else
                                {
                                    return true;
                                }
                            }
                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if(position == 0){
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);

                                }
                                else {
                                    tv.setTextColor(Color.WHITE);
                                }
                                return view;
                            }
                        };
                        //arrayAdapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        customerName.setAdapter(arrayAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(activity,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

         */

        alertDialog.setView(layout_pwd);

        // Lets populate the dropdown
        alertDialog.setPositiveButton("ADD REPORT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(customerName.getSelectedItem().toString())
                        || customerName.getSelectedItem().toString().equalsIgnoreCase("Select Customer...")){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_customername), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(customerReport.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_customerreport), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String userid = firebaseAuth.getUid();

                User user=new User();
                Report report = new Report();
                report.setCustomerName(customerName.getSelectedItem().toString());
                report.setCustomerReport(customerReport.getText().toString());
                report.setLat(latitude);
                report.setLng(longitude);
                report.setAddress(address);
                report.setOwnerid(userid);

                long timestamp = -1 * new Date().getTime();
                report.setTimestamp(timestamp);

                String key = reports.child(userid).push().getKey();
                report.setReportId(key);

                reports.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(key)
                        .setValue(report)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(root, "Report Successfully Added", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //alertDialog.
        //show dialog
        alertDialog.show();
    }

    public static void persistData(){
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public void showDialogUpdateInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle("UPDATE INFORMATION");
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_update_information, null);
        final MaterialEditText etName = layout_pwd.findViewById(R.id.etName);
        final MaterialEditText etPhone = layout_pwd.findViewById(R.id.etPhone);
        final ImageView image_upload = layout_pwd.findViewById(R.id.imageUpload);
        image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)activity).chooseImage();
            }
        });
        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(activity);
                waitingDialog.show();
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();

                Map<String, Object> updateInfo = new HashMap<>();
                if(!TextUtils.isEmpty(name))
                    updateInfo.put("username", name);
                if(!TextUtils.isEmpty(phone))
                    updateInfo.put("phone",phone);
                DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Common.USER_NODE);
                userInfo.child(Common.userID)
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                    Toast.makeText(activity,"Information Updated!",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(activity,"Information Update Failed!",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void updateUserRegion(String region){

        Map<String, Object> updateInfo = new HashMap<>();
        updateInfo.put("agentRegion", region);

        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Common.USER_NODE);
        userInfo.child(Common.userID)
                .updateChildren(updateInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                            Toast.makeText(activity,"Information Updated!",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(activity,"Information Update Failed!",Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void updateUserAppversion(int version){

        Map<String, Object> updateInfo = new HashMap<>();
        updateInfo.put("appversion", version);

        DatabaseReference userInfo = FirebaseDatabase.getInstance().getReference(Common.USER_NODE);
        userInfo.child(Common.userID)
                .updateChildren(updateInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                            Toast.makeText(activity,"Information Updated!",Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(activity,"Information Update Failed!",Toast.LENGTH_SHORT).show();

                    }
                });

    }


    public void addDailySchedule(String sVisitDate){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        //alertDialog.setTitle("ADD REPORT");


        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout_pwd = inflater.inflate(R.layout.layout_add_daily_schedule, null);

        final MaterialEditText scheduleDescription = layout_pwd.findViewById(R.id.scheduledescription);
        final TextView scheduleDate = layout_pwd.findViewById(R.id.scheduledate);

        scheduleDate.setText(sVisitDate);

        initializePeriodSpinner(layout_pwd);

        final Spinner customerName = layout_pwd.findViewById(R.id.customerName);

        List<String> customerNameList = getCustomerDropDownList();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_item_selected, customerNameList) {

            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);

                } else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        customerName.setAdapter(arrayAdapter);

        //int spinnerPosition = arrayAdapter.getPosition(scustomername);
        //etCusterName.setSelection(spinnerPosition);

        alertDialog.setView(layout_pwd);

        // Lets populate the dropdown

        alertDialog.setPositiveButton("ADD SCHEDULE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(customerName.getSelectedItem().toString())
                        || customerName.getSelectedItem().toString().equalsIgnoreCase("Select Customer...")){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_customername), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(scheduleDescription.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_daily_schedule_description), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(scheduleDate.getText().toString())){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_daily_schedule_date), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(schedulePeriod.getSelectedItem().toString())
                        || schedulePeriod.getSelectedItem().toString().equalsIgnoreCase("Select Period...")){
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_daily_schedule_period), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String userid = firebaseAuth.getUid();

                Dailyschedule dailyschedule = new Dailyschedule();
                dailyschedule.setCustomername(customerName.getSelectedItem().toString());
                dailyschedule.setDescription(scheduleDescription.getText().toString());
                dailyschedule.setVisitPeriod(schedulePeriod.getSelectedItem().toString());
                dailyschedule.setVisitDate(scheduleDate.getText().toString());

                // add the region
                dailyschedule.setUserregion(Common.currentUser.getAgentRegion());

                long timestamp = -1 * new Date().getTime();
                dailyschedule.setTimestamp(timestamp);

                String key = drefdailyschedule.child(userid).push().getKey();
                dailyschedule.setDailyPlanID(key);

                drefdailyschedule.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(key)
                        .setValue(dailyschedule)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(root, "Schedule Successfully Added", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, activity.getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //alertDialog.
        //show dialog
        alertDialog.show();
    }

    public void addNewCustomer(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle("CREATE NEW CUSTOMER INFO");
        LayoutInflater inflater = activity.getLayoutInflater();

        View layout_pwd = inflater.inflate(R.layout.layout_register_new_customer, null);

        final MaterialEditText mName = layout_pwd.findViewById(R.id.newCustomer);
        final MaterialEditText mAddress = layout_pwd.findViewById(R.id.newCustomerAddress);
        final MaterialEditText mContactperson = layout_pwd.findViewById(R.id.newCustomerContactPerson);
        final MaterialEditText mPhoneno = layout_pwd.findViewById(R.id.newCustomerPhoneNo);

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                final android.app.AlertDialog waitingDialog = new SpotsDialog(activity);
                waitingDialog.show();

                String name = mName.getText().toString();
                String address = mAddress.getText().toString();
                String contactperson = mContactperson.getText().toString();
                String phone = mPhoneno.getText().toString();

                String key = customers.child(Common.userID).push().getKey();
                Customer oCustomer = new Customer();

                if(TextUtils.isEmpty(name)) {
                    waitingDialog.dismiss();
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_name), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(address)) {
                    waitingDialog.dismiss();
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_address), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(contactperson)) {
                    waitingDialog.dismiss();
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_contact_person), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phone)) {
                    waitingDialog.dismiss();
                    Snackbar.make(root, activity.getResources().getString(R.string.enter_phone), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                dialogInterface.dismiss();

                oCustomer.setCustomername(name);
                oCustomer.setAddress(address);
                oCustomer.setContactperson(contactperson);
                oCustomer.setPhoneno(phone);
                oCustomer.setCustomernumber("[new]");

                customers.child(Common.userID)
                        .child(key)
                        .setValue(oCustomer)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                    Toast.makeText(activity,"Information Updated!",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(activity,"Information Update Failed!",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    public void searchNewCustomerOnline(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialog);
        alertDialog.setTitle("Search");
        LayoutInflater inflater = activity.getLayoutInflater();

        View layout_pwd = inflater.inflate(R.layout.layout_search_online_db_customer, null);

        final MaterialEditText searchKey = layout_pwd.findViewById(R.id.searchKey);
        final ImageButton searchButton = layout_pwd.findViewById(R.id.bt_Search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchKey.getText().toString().trim().equals("") && searchKey.getText().toString().length() > 2) {
                    if (Common.isNetworkAvailable("FIREBASE_HELPER", activity)) {
                        Toast.makeText(activity, "Clicked Search", Toast.LENGTH_SHORT);

                        FindCustomersOnline t = new FindCustomersOnline();
                        t.execute(searchKey.getText().toString().trim());

                        Toast.makeText(activity, "Search Finished.", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(activity, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    searchKey.setText("");
                    Toast.makeText(activity, "Input at least 3 characters", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialog.setView(layout_pwd);
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    public class FindCustomersOnline extends AsyncTask<String , Void, CustomerResult>{

        @Override
        protected void onPreExecute() {
            waitingDialogCustomers.show();
            //super.onPreExecute();
        }

        @Override
        protected CustomerResult doInBackground(String... strings) {

            String searchString = strings[0];

            IRetrofitCustomerData apiCustomer = CustomerDataClient.getRetrofitCustomerInstance().create(IRetrofitCustomerData.class);
            Call<CustomerResult> call = apiCustomer.searchCustomers(searchString);


            try {

                CustomerResult searchResult = call.execute().body();
                return searchResult;

            } catch (IOException e) {

                waitingDialogCustomers.dismiss();
                Toast.makeText(activity, "Please check your internet connection.", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {

                waitingDialogCustomers.dismiss();
                Toast.makeText(activity, "A general error occured.", Toast.LENGTH_SHORT).show();

            }

            return null;
        }

        @Override
        protected void onPostExecute(CustomerResult customerResult) {

            try {
                if (customerResult.isResult()) {
                    ArrayList<Customer> customerList = customerResult.getCustomers();
                    ((CustomerList)((MainActivity)activity).fragment).setSearchedOnlineCustomer(customerList);
                }
            }catch (Exception ex){
                waitingDialogCustomers.dismiss();
                Log.e("FIREBASE_HELPER",ex.getMessage(), ex);
            }

            waitingDialogCustomers.dismiss();
            //super.onPostExecute(customerResult);
        }
    }

    private void initializePeriodSpinner(View view){

        schedulePeriod = view.findViewById(R.id.scheduleperiod);

        // Create an ArrayAdapter using the string array and a default spinner layout
        //final ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(activity,
        //        R.array.period_array, android.R.layout.simple_spinner_item){

        //};
        // Specify the layout to use when the list of choices appears
        // adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner

        final List<String> arrPeriods = new ArrayList<String>();
        arrPeriods.add("Select Period...");
        arrPeriods.add("Morning Schedule");
        arrPeriods.add("Afternoon Schedule");
        arrPeriods.add("Evening Schedule");
        arrPeriods.add("Night Schedule");

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(activity, R.layout.spinner_item_selected, arrPeriods){

            @Override
            public boolean isEnabled(int position){
                // Disable the first item from Spinner
                // First item will be use for hint
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);

                }
                else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };

        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown_item);
        schedulePeriod.setAdapter(adapterSpinner);
    }

    public String getSearchDate() {

        Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("dd-MM-yyyy", Locale.getDefault());

        String searchDate = ft.format(dNow);

        return searchDate;
    }


    /*

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
        startService(dataServiceIntent);
        addAgentTrackingNotification(agentTracking);

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
        bigText.bigText("Location is being monitored by regional heads and HQ. Location sharing can be suspending at any time.");
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

        mNotificationManager.notify(0, mBuilder.build());

    }
    */

    private List<String> getCustomerDropDownList(){

        ArrayList<Customer> customerArrayList = db.getCustomerList();
        List<String> stringList = new ArrayList<>();

        stringList.add("Select Customer...");
        for ( Customer customer : customerArrayList){
            String customername = customer.getCustomername() + " [" + customer.getCustomernumber() + "]";
            stringList.add(customername);
        }
        return stringList;
    }

    private boolean isUserValid(String email){

        boolean bValue = false;

        // Lets check to see if this email exists on the platform
        if ( !Common.SYS_ADMIN_EMAIL.toLowerCase().contains(email)) {

            if (Common.isNetworkAvailable(TAG, activity)) {
                AuthenticationQueryResult query = Authentication.doesUserExistInCloud(email, activity);
                if (query != null) {
                    if (!query.isResult()) {
                        Snackbar.make(root, activity.getResources().getString(R.string.failed_existence) + ":" + query.getErrormsg(), Snackbar.LENGTH_LONG).show();
                    }else{
                        bValue = true;
                    }
                } else {
                    Snackbar.make(root, activity.getResources().getString(R.string.failed), Snackbar.LENGTH_LONG).show();
                }
            }else{
                Snackbar.make(root, activity.getResources().getString(R.string.check_internet_connection), Snackbar.LENGTH_LONG).show();
            }

        }else{
            bValue = true;
        }

        return bValue;
    }

    public interface AutheticationCallback {
        void AuthSuccess(String output);
        void AuthFailed(String output);
    }

    private void doAsyncLogIn(final String email, final String password){

        final SpotsDialog waitingDialog=new SpotsDialog(activity);
        waitingDialog.show();

        Authentication authSecurity = new Authentication(activity, email, new AutheticationCallback(){

            @Override
            public void AuthSuccess(String output) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Lets start the service
                        // onAuthSuccess(firebaseAuth.getCurrentUser().getUid());

                        waitingDialog.dismiss();
                        //goToMainActivity();
                        // In case the account has been created but user profile hasnt
                        // we need to save the password
                        Common.userPW = password;

                        Snackbar.make(root, "Successfully logged in: ", Snackbar.LENGTH_LONG).show();

                        loginSuccess(firebaseAuth.getCurrentUser().getUid());

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(root, "Invalid Credentials :" + activity.getResources().getString(R.string.failed) +" : "+e.getMessage(), Snackbar.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void AuthFailed(String output) {
                waitingDialog.dismiss();
                Snackbar.make(root, activity.getResources().getString(R.string.failed) + " : " + output, Snackbar.LENGTH_LONG).show();
            }
        });
        authSecurity.execute();
    }

    private void doAsyncCreateAccount(final String email, final String password, final String name, final String phone){

        final SpotsDialog waitingDialog=new SpotsDialog(activity);
        waitingDialog.show();

        Authentication authSecurity = new Authentication(activity, email, new AutheticationCallback(){

            @Override
            public void AuthSuccess(String output) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user=new User();
                                user.setEmail(email);
                                user.setUsername(name);
                                user.setPassword(password);
                                user.setPhone(phone);
                                user.setUserID(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                // Update appversion
                                user.setAppversion(AppUtils.getVersionCode(activity));

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                waitingDialog.dismiss();
                                                Snackbar.make(root, "Registration Successful. Use the log in button to log in. ", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        waitingDialog.dismiss();
                                        Snackbar.make(root, activity.getResources().getString(R.string.registration_failed)+ " - " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(root, activity.getResources().getString(R.string.registration_failed)+ " - " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void AuthFailed(String output) {
                waitingDialog.dismiss();
                Snackbar.make(root, activity.getResources().getString(R.string.registration_failed) + " : " + output, Snackbar.LENGTH_LONG).show();
            }
        });
        authSecurity.execute();
    }
}
