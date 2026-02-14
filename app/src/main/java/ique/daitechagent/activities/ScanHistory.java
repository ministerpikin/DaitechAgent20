package ique.daitechagent.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import ique.daitechagent.R;
import ique.daitechagent.adapters.scannedBarcodeHistoryAdapter;
import ique.daitechagent.adapters.scanningHistoryAdapter;
import ique.daitechagent.common.ClickListener;
import ique.daitechagent.common.Common;
import ique.daitechagent.controllers.SwipeController;
import ique.daitechagent.controllers.SwipeControllerActions;
import ique.daitechagent.database.DataContext;
import ique.daitechagent.model.BarcodeSscanning;
import ique.daitechagent.model.Customer;
import ique.daitechagent.model.Scannedproduct;

public class ScanHistory extends AppCompatActivity {

    private static final String TAG = "ScanHistory";

    RecyclerView rvHistory;
    RecyclerView rvScannedBarcode;
    scanningHistoryAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    scannedBarcodeHistoryAdapter scannedBC;

    FirebaseDatabase database;
    DatabaseReference drefscanHistory, drefcustomer, drefscannedbarcode;
    FirebaseAuth mAuth;
    ArrayList<BarcodeSscanning> listData, listScannedBarcodeData;

    //variables and widgets
    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private int mAppBarState;
    private AppBarLayout viewContactsBar, searchBar;
    private EditText mSearchContacts;
    private TextView mSearchToolbarHeading;

    SwipeController swipeController = null;
    ImageView menu_drawer_image;

    private BarcodeSscanning currentBarcodeScanning;
    private int newScan = 1; // New Scan = 1; Additional Scan = 2

    private DataContext db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);

        initToolbar();
        initRecyclerView();


        db = new DataContext(this, null, null, 1);

        FloatingActionButton fab = findViewById(R.id.fabscanhistory);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                newScan = 1;
                scanBarcode(null);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        Common.userID = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        drefcustomer = database.getReference(Common.CUSTOMER_NODE);
        drefscanHistory = database.getReference(Common.SCAN_BARCODE_NODE);
        listData = new ArrayList<>();
        adapter = new scanningHistoryAdapter(this, listData, new ClickListener() {
            @Override
            public void onClick(View view, int index) {
                newScan = 2;
                currentBarcodeScanning = listData.get(index);
                scanBarcode(null);

            }
        });
        rvHistory.setAdapter(adapter);
        getScanHistory();

        swipeController = new SwipeController(getApplicationContext(), new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {

                BarcodeSscanning barcodeSscanning = listData.get(position);
                showDeleteScanInfoDialog(position, Common.userID, barcodeSscanning.getBarcodescanninguuid());

            }

            @Override
            public void onLeftClicked(int position) {
                //super.onLeftClicked(position);
                BarcodeSscanning barcodeSscanning = listData.get(position);


                Log.v(TAG, barcodeSscanning.getBarcodescanninguuid() + ":" + barcodeSscanning.getCustomername());

                showScannedBarcodeNumbers(barcodeSscanning);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(rvHistory);

        rvHistory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c,  4);
            }

        });

        menu_drawer_image= findViewById(R.id.menu_menu);
        menu_drawer_image.setVisibility(View.GONE);
    }

    public void scanBarcode(View view) {

        final Activity scanActivity = this;

        if (newScan == 2) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
            alertDialogBuilder.setTitle("Add Additional Scanned Barcode");
            alertDialogBuilder.setMessage("\n\nYou are scanning additional barcode for same sales to \n\n"+ currentBarcodeScanning.getCustomername().toUpperCase()+". \n\nContinue?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                            new IntentIntegrator(scanActivity).initiateScan();

                        }
                    });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();
        }else {
            new IntentIntegrator(scanActivity).initiateScan();
        }
    }

    private void initToolbar() {
        /*
        Toolbar toolbar = findViewById(R.id.toolbarscanhistory);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Barcode Scanning History");

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        */

        /*
         * Let do the slide
         */
        viewContactsBar = findViewById(R.id.viewContactsToolbar);
        searchBar = findViewById(R.id.searchToolbar);
        mSearchContacts = findViewById(R.id.etSearchContacts);
        mSearchToolbarHeading = findViewById(R.id.toolbarHeading);

        mSearchToolbarHeading.setText("Barcode Scan List");

        setAppBarState(STANDARD_APPBAR);

        ImageView ivSearchContact = findViewById(R.id.ivSearchIcon);
        ivSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked search icon.");
                toggleToolBarState();
            }
        });

        ImageView ivBackArrow = findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");
                toggleToolBarState();
            }
        });

        mSearchContacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                //adapter.filter(text);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                //adapter.filter(text);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }
        });

    }

    private void initRecyclerView(){
        rvHistory = findViewById(R.id.rvscan);
        /*
        rvHistory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvHistory.setLayoutManager(layoutManager);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
        */

        rvHistory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());

        //layoutManager..setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setMoveDuration(1000);
        itemAnimator.setRemoveDuration(1000);


        rvHistory.setLayoutManager(layoutManager);
        rvHistory.setItemAnimator(itemAnimator);
        rvHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));
    }

    private void getScanHistory(){

        drefscanHistory.child(Common.userID).orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listData.clear();
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            BarcodeSscanning history = postSnapshot.getValue(BarcodeSscanning.class);
                            listData.add(history);
                        }
                        //adapter.notifyDataSetChanged();
                        String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                        if( text.length() == 0){
                            adapter.notifyDataSetChanged();
                            adapter.arrayList.addAll(listData);
                        }else{
                            adapter.arrayList.clear();
                            adapter.arrayList.addAll(listData);
                            adapter.filter(text);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    /**
     * Sets the appbar state for either the search 'mode' or 'standard' mode
     * @param state
     */
    private void setAppBarState(int state) {
        Log.d(TAG, "setAppBarState: changing app bar state to: " + state);

        mAppBarState = state;

        if(mAppBarState == STANDARD_APPBAR){
            searchBar.setVisibility(View.GONE);
            viewContactsBar.setVisibility(View.VISIBLE);

            //hide the keyboard
            View view = getCurrentFocus();// getView();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            try{
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (NullPointerException e){
                Log.d(TAG, "setAppBarState: NullPointerException: " + e.getMessage());
            }
        }

        else if(mAppBarState == SEARCH_APPBAR){
            viewContactsBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);

            //open the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

    }

    /**
     * Initiates the appbar state toggle
     */
    private void toggleToolBarState() {
        Log.d(TAG, "toggleToolBarState: toggling AppBarState.");
        if(mAppBarState == STANDARD_APPBAR){
            setAppBarState(SEARCH_APPBAR);
        }else{
            setAppBarState(STANDARD_APPBAR);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                addScannedInfo(result.getContents());
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    public void addScannedInfo(final String barcode){

        //AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        String strDialogTitle = "ADD SCANNED DETAILS";
        if (newScan == 1){
            strDialogTitle = "ADD SCANNED DETAILS";
        }else if (newScan == 2){
            strDialogTitle = "ADDITIONAL SCANNED DETAILS";
        }
        alertDialog.setTitle(strDialogTitle);

        final View view = findViewById(R.id.scanview);

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_pwd = inflater.inflate(R.layout.layout_add_scanned_product, null);

        final TextView longdate = layout_pwd.findViewById(R.id.scandate);
        final TextView scannedbarcode = layout_pwd.findViewById(R.id.scannedbarcode);
        final MaterialEditText scannedquantity = layout_pwd.findViewById(R.id.scannedquantity);

        final Date dNow = new Date( );
        SimpleDateFormat ft =
                new SimpleDateFormat ("EEE, dd MMM yyyy");
        String reportDate = ft.format(dNow);
        longdate.setText(reportDate);
        scannedbarcode.setText(barcode);

        final Spinner customerName = layout_pwd.findViewById(R.id.customerName);
        final TextView customerNameAdditionalScan = layout_pwd.findViewById(R.id.customerNameAdditional);

        if (newScan == 1) {
            customerName.setVisibility(View.VISIBLE);
            customerNameAdditionalScan.setVisibility(View.GONE);

            List<String> customerNameList = getCustomerDropDownList();
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_selected, customerNameList) {

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

            //int spinnerPosition = arrayAdapter.getPosition(customerName);
            //etCusterName.setSelection(spinnerPosition);

            /*
            drefcustomer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final List<String> arrCustomername = new ArrayList<String>();
                            arrCustomername.add("Select Customer...");
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String sCustomerName = dataSnapshot1.child("customername").getValue(String.class);
                                String sCustomerNumber = dataSnapshot1.child("customernumber").getValue(String.class);
                                arrCustomername.add(sCustomerName + " [" + sCustomerNumber + "]");
                            }
                            //ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, arrCustomername);
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_selected, arrCustomername) {

                                @Override
                                public boolean isEnabled(int position) {
                                    if (position == 0) {
                                        // Disable the first item from Spinner
                                        // First item will be use for hint
                                        return false;
                                    } else {
                                        return true;
                                    }
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
                            //arrayAdapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                            arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                            customerName.setAdapter(arrayAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

             */

        }else if (newScan == 2){
            customerName.setVisibility(View.GONE);
            customerNameAdditionalScan.setVisibility(View.VISIBLE);
            customerNameAdditionalScan.setText(currentBarcodeScanning.getCustomername());
        }
        alertDialog.setView(layout_pwd);

        // Lets populate the dropdown

        alertDialog.setPositiveButton("ADD SCAN DETAILS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (newScan == 1) {
                    if (TextUtils.isEmpty(customerName.getSelectedItem().toString())
                            || customerName.getSelectedItem().toString().equalsIgnoreCase("Select Customer...")) {
                        Snackbar.make(view, "Please select a customer", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }else if (newScan == 2){
                    if (TextUtils.isEmpty(customerNameAdditionalScan.getText().toString())) {
                        Snackbar.make(view, "Customer Name is Empty", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (TextUtils.isEmpty(scannedbarcode.getText().toString())) {
                    Snackbar.make(view, "Please make sure a barcode was scanned", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(scannedquantity.getText().toString())) {
                    Snackbar.make(view, "Please inpute the quantity sold for this scan", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                String userid = mAuth.getUid();

                BarcodeSscanning barcodeSscanning = new BarcodeSscanning();
                String key = "";

                if (newScan == 1) {
                    //barcodeSscanning = new BarcodeSscanning();
                    barcodeSscanning.setCustomername(customerName.getSelectedItem().toString());
                    barcodeSscanning.setLongDate(longdate.getText().toString());

                    SimpleDateFormat ft2 =
                            new SimpleDateFormat("yyyy-MM-dd");
                    String searchDate = ft2.format(dNow);

                    barcodeSscanning.setScandate(searchDate);


                    long timestamp = -1 * new Date().getTime();
                    barcodeSscanning.setTimestamp(timestamp);

                    key = drefscanHistory.child(userid).push().getKey();
                    barcodeSscanning.setBarcodescanninguuid(key);

                    ArrayList<Scannedproduct> arrScannedproducts = new ArrayList<>();
                    Scannedproduct scannedproduct = new Scannedproduct();
                    scannedproduct.setBarcode(scannedbarcode.getText().toString());
                    scannedproduct.setQuantity(Integer.parseInt(scannedquantity.getText().toString()));

                    String prodkey = drefscanHistory.child(userid).child(Common.SCAN_PRODUCT_NODE).child(key).push().getKey();
                    scannedproduct.setScannedproductuuid(prodkey);
                    arrScannedproducts.add(scannedproduct);
                    barcodeSscanning.setScannedproducts(arrScannedproducts);

                }else if (newScan == 2){

                    barcodeSscanning = currentBarcodeScanning;
                    key = barcodeSscanning.getBarcodescanninguuid();

                    //ArrayList<Scannedproduct> arrScannedproducts = barcodeSscanning.getScannedproducts();// new ArrayList<>();
                    Scannedproduct scannedproduct = new Scannedproduct();
                    scannedproduct.setBarcode(scannedbarcode.getText().toString());
                    scannedproduct.setQuantity(Integer.parseInt(scannedquantity.getText().toString()));

                    String prodkey = drefscanHistory.child(userid).child(Common.SCAN_PRODUCT_NODE).child(key).push().getKey();
                    scannedproduct.setScannedproductuuid(prodkey);

                    //arrScannedproducts.add(scannedproduct);
                    barcodeSscanning.getScannedproducts().add(scannedproduct);

                }

                //findViewById(R.id.drawer_layout)
                drefscanHistory.child(userid)
                        .child(key)
                        .setValue(barcodeSscanning)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(view, "Scan Successfully Added", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, getApplicationContext().getResources().getString(R.string.failed)+e.getMessage(), Snackbar.LENGTH_SHORT).show();
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

    public void showDeleteScanInfoDialog(final int position, final String ownerid, final String scanentryuuid){

        final View view = findViewById(R.id.scanview);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        alertDialogBuilder.setMessage("You are gonig to delete a Report. Continue?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        final android.app.AlertDialog waitingDialog = new SpotsDialog(view.getContext());
                        waitingDialog.show();

                        //DatabaseReference reportinfo = FirebaseDatabase.getInstance().getReference(Common.REPORT_NODE);
                        drefscanHistory.child(ownerid).child(scanentryuuid).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                waitingDialog.dismiss();
                                if (databaseError == null) {
                                    //adapter.items.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                    Toast.makeText(view.getContext(), "Scan information has been deleted!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(view.getContext(), "Error deleting scan information!" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }

                        });

                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //canGetLocation = false;
                //dialog.cancel();
                dialog.dismiss();
            }
        });
        //android.support.v7.app.AlertDialog alert = alertDialogBuilder.create();
        //alert.show();
        alertDialogBuilder.show();

    }

    public void showScannedBarcodeNumbers(final BarcodeSscanning barcodeSscanning) {
        StringBuilder productquantity = new StringBuilder();
        Iterator itr = barcodeSscanning.getScannedproducts().iterator();
        while (itr.hasNext()){
            Scannedproduct scannedproduct = (Scannedproduct)itr.next();

            productquantity.append(scannedproduct.getBarcode()).append(" (Quantity=>").append(scannedproduct.getQuantity()).append(")\n");
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        alertDialog.setTitle("Scanned Barcode");

        alertDialog.setMessage(productquantity.toString());

        alertDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

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

    /*
    public void showScannedBarcodeNumbers(final BarcodeSscanning barcodeSscanning) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        alertDialog.setTitle("Scanned Barcode");


        LayoutInflater inflater = LayoutInflater.from(this);
        View fragment_reportlist = inflater.inflate(R.layout.fragment_scanned_barcode, null);

        rvScannedBarcode =  fragment_reportlist.findViewById(R.id.rvScannedBarcode);
        rvScannedBarcode.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());


        rvScannedBarcode.setLayoutManager(layoutManager);
        rvScannedBarcode.setItemAnimator(new DefaultItemAnimator());
        rvScannedBarcode.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));

        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        drefscannedbarcode = database.getReference(Common.SCAN_BARCODE_NODE);
        listScannedBarcodeData = new ArrayList<>();
        //scannedBC = new scannedBarcodeHistoryAdapter(getApplicationContext(), listScannedBarcodeData)

        scannedBC = new scannedBarcodeHistoryAdapter(this, listScannedBarcodeData);

        rvScannedBarcode.setAdapter(scannedBC);
        getScannedBarcode(edcustomerName);

        alertDialog.setView(fragment_reportlist);

        // Lets populate the dropdown




        alertDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void getScannedBarcode(final String customername){
        //listData.clear();

        drefscannedbarcode = database.getReference(Common.SCAN_BARCODE_NODE);
        drefscannedbarcode.child(Common.userID)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        rptlistData.clear();
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                            //String id = dataSnapshot.getKey();
                            if(postSnapshot.child("customerName").exists() ){
                                if(postSnapshot.child("customerName").getValue(String.class).equals(customername) ){
                                    Report history = postSnapshot.getValue(Report.class);
                                    rptlistData.add(history);
                                }
                            }



                        }

                        String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
                        if( text.length() == 0){
                            rptadapter.notifyDataSetChanged();
                            adapter.arrayList.addAll(listData);
                        }else{
                            adapter.arrayList.clear();
                            adapter.arrayList.addAll(listData);
                            adapter.filter(text);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

     */

}
