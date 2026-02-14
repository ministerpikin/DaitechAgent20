package ique.daitechagent.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ique.daitechagent.R;
import ique.daitechagent.adapters.customerListAdapter;
import ique.daitechagent.adapters.rptHistoryAdapter;
import ique.daitechagent.common.ClickListener;
import ique.daitechagent.common.Common;
import ique.daitechagent.common.OnCommentClickListener;
import ique.daitechagent.controllers.SwipeController;
import ique.daitechagent.controllers.SwipeControllerActions;
import ique.daitechagent.database.DataContext;
import ique.daitechagent.model.Customer;
import ique.daitechagent.model.CustomerResult;
import ique.daitechagent.model.Report;
import ique.daitechagent.model.User;
import ique.daitechagent.retrofit.CustomerDataClient;
import ique.daitechagent.retrofit.IRetrofitCustomerData;
import retrofit2.Call;

//import static com.facebook.FacebookSdk.getApplicationContext;

//import android.app.Fragment;
//import android.support.v4.app.FragmentManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerList extends Fragment {

    private static final String TAG = "CustomerList";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    FirebaseDatabase database;
    DatabaseReference drefcustomerlist, drefreportlist;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvCustList;
    RecyclerView rvCustListSearchOnlie;
    customerListAdapter adapter;
    customerListAdapter adapterSearchOnline;
    FirebaseAuth mAuth;
    ArrayList<Customer> listData;
    ArrayList<Customer> listDataSearchOnline;

    ArrayList<Report> rptlistData;

    RecyclerView rvRptHistory;
    rptHistoryAdapter rptadapter;

    //variables and widgets
    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private int mAppBarState;
    private AppBarLayout viewContactsBar, searchBar;
    private EditText mSearchContacts;
    private TextView mSearchToolbarHeading;
    private ImageView mAddCustomerIcon;

    SwipeController swipeController = null;

    private DataContext db;
    android.app.AlertDialog waitingDialogCustomers;

    public CustomerList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportList.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerList newInstance(String param1, String param2) {
        CustomerList fragment = new CustomerList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        String customername, customernumber, customeraddress;


        db = new DataContext(getActivity(), null, null, 1);

        View fragment_customerlist = inflater.inflate(R.layout.fragment_customer_list, container, false);

        waitingDialogCustomers = new SpotsDialog(getActivity());

        viewContactsBar = ((AppCompatActivity)getActivity()).findViewById(R.id.viewContactsToolbar);//.getSupportActionBar();
        //viewContactsBar = (AppBarLayout) container.getChildAt(0).findViewById(R.id.viewContactsToolbar);
        //container.getParent()
        searchBar = ((AppCompatActivity)getActivity()).findViewById(R.id.searchToolbar);
        //contactsList = (ListView) fragment_reportlist.findViewById(R.id.contactsList);
        mSearchContacts = getActivity().findViewById(R.id.etSearchContacts);
        mSearchToolbarHeading = getActivity().findViewById(R.id.toolbarHeading);

        mAddCustomerIcon = getActivity().findViewById(R.id.ivAddCustomerIcon);
        mAddCustomerIcon.setVisibility(View.VISIBLE);
        mAddCustomerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked Add Customer icon.");
                addNewCustomer();

            }
        });

        mSearchToolbarHeading.setText("Customer List");

        setAppBarState(STANDARD_APPBAR);

        ImageView ivSearchContact = getActivity().findViewById(R.id.ivSearchIcon);
        ivSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked search icon.");
                toggleToolBarState();
            }
        });

        ImageView ivBackArrow = getActivity().findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");
                toggleToolBarState();
            }
        });

        initRecyclerView(fragment_customerlist);
        setupSearchRcycleView(fragment_customerlist);

        mAuth = FirebaseAuth.getInstance();
        Common.userID = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        drefcustomerlist = database.getReference(Common.CUSTOMER_NODE);
        drefreportlist = database.getReference(Common.REPORT_NODE);
        listData = new ArrayList<>();
        adapter = new customerListAdapter(getContext(), listData, new ClickListener() {
            @Override
            public void onClick(View view, int index) {

                Customer customer = listData.get(index);

                Log.v(TAG, customer.getCustomerid() + ":" + customer.getCustomername() + ":" + customer.getCustomernumber());

                String customername = customer.getCustomername() + " [" + customer.getCustomernumber() + "]";
                showCustomerReportList(customername);

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

        rvCustList.setAdapter(adapter);
        //getCustomerList();
        getCustomerListSQLITE();

        swipeController = new SwipeController(getContext(), new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {

                Customer customer = listData.get(position);
                String customername = customer.getCustomername() + " [" + customer.getCustomernumber() + "]";
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String customeruuid = customer.getCustomerid();
                //showDialogEditCordinates(customername, uid, customeruuid);
                showDialogEditCordinatesSQLLITE(customername, uid, customeruuid, customer.getCustomernumber());

            }

            @Override
            public void onLeftClicked(int position) {

                Customer customer = listData.get(position);
                String customername = customer.getCustomername() + " [" + customer.getCustomernumber() + "]";
                addReport(customername);

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(rvCustList);

        rvCustList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c, 2);
            }

        });

        return fragment_customerlist;//inflater.inflate(R.layout.fragment_report_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void setupSearchRcycleView(View view){

        listDataSearchOnline = new ArrayList<>();
        rvCustListSearchOnlie = view.findViewById(R.id.rvCustomerListSearchOnline);
        rvCustListSearchOnlie.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        //layoutManager..setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);

        rvCustListSearchOnlie.setLayoutManager(layoutManager);
        rvCustListSearchOnlie.setItemAnimator(new DefaultItemAnimator());
        rvCustListSearchOnlie.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adapterSearchOnline = new customerListAdapter(getContext(), listDataSearchOnline, new ClickListener() {
            @Override
            public void onClick(View view, int index) {

                Customer customer = listDataSearchOnline.get(index);

                Log.v(TAG, customer.getCustomerid() + ":" + customer.getCustomername() + ":" + customer.getCustomernumber());

                //String customername = customer.getCustomername() + " [" + customer.getCustomernumber() + "]";
                //showCustomerReportList(customername);
                showCustomerDetailsDailog(customer);

                rvCustList.setVisibility(View.VISIBLE);
                rvCustListSearchOnlie.setVisibility(View.GONE);

            }
        });

        rvCustListSearchOnlie.setAdapter(adapterSearchOnline);

    }

    private void showCustomerDetailsDailog(final Customer customer){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("Customer Information");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_update_new_customer_from_online, null);

        final TextView vCustomerName = layout_pwd.findViewById(R.id.vCustomerName);
        final TextView vCustomerNumber = layout_pwd.findViewById(R.id.vCustomerNumber);
        final TextView vCustomerAddress = layout_pwd.findViewById(R.id.vCustomerAddress);
        final TextView vContactPerson = layout_pwd.findViewById(R.id.vContactPerson);
        final TextView vPhoneNUmber = layout_pwd.findViewById(R.id.vPhoneNUmber);
        final TextView vLat = layout_pwd.findViewById(R.id.vLat);
        final TextView vLng = layout_pwd.findViewById(R.id.vLng);
        final TextView vEmail = layout_pwd.findViewById(R.id.vEmail);

        vCustomerName.setText(customer.getCustomername());
        vCustomerNumber.setText(customer.getCustomernumber());
        vCustomerAddress.setText(customer.getAddress());
        vContactPerson.setText(customer.getContactperson());
        vPhoneNUmber.setText(customer.getPhoneno());
        vLat.setText(String.valueOf(customer.getLat()));
        vLng.setText(String.valueOf(customer.getLng()));
        vEmail.setText(customer.getEmail());

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("Save To Local Database", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                waitingDialog.show();

                ArrayList<Customer> tempCustomer = new ArrayList<>();
                tempCustomer.add(customer);
                db.refreshCustomerList(tempCustomer);
                getCustomerListSQLITE();
                waitingDialog.dismiss();

            }
        });
        /*
        alertDialog.setNeutralButton("New Customer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNewCustomer();
            }
        });

         */

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public void addNewCustomer(){

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("CREATE NEW CUSTOMER");
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View layout_pwd = inflater.inflate(R.layout.layout_register_new_customer, null);

        final MaterialEditText mName = layout_pwd.findViewById(R.id.newCustomer);
        final MaterialEditText mAddress = layout_pwd.findViewById(R.id.newCustomerAddress);
        final MaterialEditText mContactperson = layout_pwd.findViewById(R.id.newCustomerContactPerson);
        final MaterialEditText mPhoneno = layout_pwd.findViewById(R.id.newCustomerPhoneNo);

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                //final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                //waitingDialog.show();

                String name = mName.getText().toString();
                String address = mAddress.getText().toString();
                String contactperson = mContactperson.getText().toString();
                String phone = mPhoneno.getText().toString();

                //String key = customers.child(Common.userID).push().getKey();
                Customer oCustomer = new Customer();

                if(TextUtils.isEmpty(name)) {
                    //waitingDialog.dismiss();
                    Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_name), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(address)) {
                    //waitingDialog.dismiss();
                    Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_address), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(contactperson)) {
                    //waitingDialog.dismiss();
                    Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_contact_person), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phone)) {
                    //waitingDialog.dismiss();
                    Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_phone), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                dialogInterface.dismiss();

                oCustomer.setCustomername(name);
                oCustomer.setAddress(address);
                oCustomer.setContactperson(contactperson);
                oCustomer.setPhoneno(phone);
                if ( Common.isNetworkAvailable(TAG, getActivity() )) {
                    CreateNewCustomerBackground createNewCustomerBackground = new CreateNewCustomerBackground();
                    createNewCustomerBackground.execute(oCustomer);
                }else {
                    Toast.makeText(getActivity(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }



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

    private class CreateNewCustomerBackground extends AsyncTask<Customer, Void, CustomerResult>{
        @Override
        protected void onPreExecute() {
            waitingDialogCustomers.show();
        }

        @Override
        protected void onPostExecute(CustomerResult customerResult) {

            try {
                if (customerResult.isResult()) {
                    ArrayList<Customer> customerList = customerResult.getCustomers();
                    db.refreshCustomerList(customerList);
                    getCustomerListSQLITE();

                    Toast.makeText(getActivity(), "Save successful.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Error saving customer details. " + customerResult.getErrormsg(), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                waitingDialogCustomers.dismiss();
                Log.e(TAG,ex.getMessage(), ex);
            }

            waitingDialogCustomers.dismiss();
        }

        @Override
        protected CustomerResult doInBackground(Customer... customers) {
            Customer customer = customers[0];

            IRetrofitCustomerData apiCustomer = CustomerDataClient.getRetrofitCustomerInstance().create(IRetrofitCustomerData.class);
            Call<CustomerResult> call = apiCustomer.createCustomer(Common.currentUser.getEmail(), customer.getCustomername(),
                                            customer.getContactperson(),customer.getAddress(), customer.getPhoneno());


            try {

                CustomerResult searchResult = call.execute().body();
                return searchResult;

            } catch (IOException e) {

                waitingDialogCustomers.dismiss();
                Toast.makeText(getActivity(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {

                waitingDialogCustomers.dismiss();
                Toast.makeText(getActivity(), "A general error occured.", Toast.LENGTH_SHORT).show();

            }

            return null;
        }
    }

    private void initRecyclerView(View view) {
        rvCustList = view.findViewById(R.id.rvCustomerList);
        rvCustList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        //layoutManager..setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);

        rvCustList.setLayoutManager(layoutManager);
        rvCustList.setItemAnimator(new DefaultItemAnimator());
        rvCustList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


    }

    public void setSearchedOnlineCustomer(ArrayList<Customer> customerArrayList){
        rvCustList.setVisibility(View.GONE);
        rvCustListSearchOnlie.setVisibility(View.VISIBLE);

        listDataSearchOnline.clear();

        //listDataSearchOnline = (ArrayList)customerArrayList.clone();
        listDataSearchOnline.addAll(customerArrayList);

        adapterSearchOnline.notifyDataSetChanged();
    }

    private void getCustomerList() {
        //listData.clear();

        drefcustomerlist.child(Common.userID)
                .orderByChild("customername")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listData.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final Customer history = postSnapshot.getValue(Customer.class);
                            String customername = history.getCustomername() + " [" + history.getCustomernumber() + "]";

                            Query query = drefreportlist.child(Common.userID).orderByChild("customerName").equalTo(customername);
                            query.addValueEventListener(new ValueEventListener() {
                                long reportcount;

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        reportcount = dataSnapshot.getChildrenCount();
                                        history.setReportcount(reportcount);
                                    } else {
                                        reportcount = 0;
                                        history.setReportcount(reportcount);
                                    }

                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            listData.add(history);
                        }
                        //adapter.notifyDataSetChanged();

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

    private void getCustomerListSQLITE() {
        listData.clear();

        ArrayList<Customer> customerssqlite = db.getCustomerList();
        for (final Customer customer : customerssqlite) {



            String customername = customer.getCustomername() + " [" + customer.getCustomernumber() + "]";

            Query query = drefreportlist.child(Common.userID).orderByChild("customerName").equalTo(customername);
            query.addValueEventListener(new ValueEventListener() {
                long reportcount;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        reportcount = dataSnapshot.getChildrenCount();
                        customer.setReportcount(reportcount);
                    } else {
                        reportcount = 0;
                        customer.setReportcount(reportcount);
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            listData.add(customer);


        }


        String text = mSearchContacts.getText().toString().toLowerCase(Locale.getDefault());
        if( text.length() == 0){
            adapter.notifyDataSetChanged();
            adapter.arrayList.addAll(listData);
        }else{
            adapter.arrayList.clear();
            adapter.arrayList.addAll(listData);
            adapter.filter(text);
        }

        mSearchToolbarHeading.setText("Customer List ["+adapter.getItemCount()+"]");

    }

    public void showDialogEditCordinates(final String customerName, final String ownerid,
                                         final String customerId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("UPDATE CUSTOMER GPS INFORMATION");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_edit_customer_gps, null);
        final TextView gpsCustomername = layout_pwd.findViewById(R.id.gpscustomername);
        final TextView gpsLongitude = layout_pwd.findViewById(R.id.gpslongitude);
        final TextView gpsLatitude = layout_pwd.findViewById(R.id.gpslatitude);

        gpsCustomername.setText(customerName);
        gpsLongitude.setText(String.valueOf(Common.currentLng));
        gpsLatitude.setText(String.valueOf(Common.currentLat));

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                waitingDialog.show();
                String strName = gpsCustomername.getText().toString();
                double lng = Double.parseDouble(gpsLongitude.getText().toString());
                double lat = Double.parseDouble(gpsLatitude.getText().toString());

                Map<String, Object> updateInfo = new HashMap<>();
                if (!TextUtils.isEmpty(strName)) {
                    updateInfo.put("lng", lng);
                    updateInfo.put("lat", lat);
                }

                DatabaseReference reportinfo = FirebaseDatabase.getInstance().getReference(Common.CUSTOMER_NODE);
                reportinfo.child(ownerid).child(customerId)
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if (task.isSuccessful())
                                    Toast.makeText(getActivity(), "Information Updated!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity(), "Information Update Failed!", Toast.LENGTH_SHORT).show();

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

    public void showDialogEditCordinatesSQLLITE(final String customerName, final String ownerid,
                                         final String customerId, final String customernumber) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("UPDATE CUSTOMER GPS INFORMATION");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_edit_customer_gps, null);
        final TextView gpsCustomername = layout_pwd.findViewById(R.id.gpscustomername);
        final TextView gpsLongitude = layout_pwd.findViewById(R.id.gpslongitude);
        final TextView gpsLatitude = layout_pwd.findViewById(R.id.gpslatitude);

        gpsCustomername.setText(customerName);
        gpsLongitude.setText(String.valueOf(Common.currentLng));
        gpsLatitude.setText(String.valueOf(Common.currentLat));

        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                Customer customer = new Customer();
                customer.setCustomernumber(customernumber);
                customer.setLat( Double.parseDouble(gpsLatitude.getText().toString()) );
                customer.setLng( Double.parseDouble(gpsLongitude.getText().toString()) );

                if ( Common.isNetworkAvailable(TAG, getActivity() )) {
                    asyncSaveCustomerCoordinates coordinates = new asyncSaveCustomerCoordinates();
                    coordinates.execute(customer);
                }else {
                    Toast.makeText(getActivity(), "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
                }

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

    private class asyncSaveCustomerCoordinates extends AsyncTask<Customer, Void, CustomerResult>{
        @Override
        protected void onPreExecute() {
            waitingDialogCustomers.show();
        }

        @Override
        protected void onPostExecute(CustomerResult customerResult) {

            try {
                if (customerResult.isResult()) {
                    ArrayList<Customer> customerList = customerResult.getCustomers();
                    db.refreshCustomerList(customerList);
                    getCustomerListSQLITE();

                    Toast.makeText(getActivity(), "Update successful.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Error saving customer coordinates details. " + customerResult.getErrormsg(), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                waitingDialogCustomers.dismiss();
                Log.e(TAG,ex.getMessage(), ex);
            }

            waitingDialogCustomers.dismiss();
        }

        @Override
        protected CustomerResult doInBackground(Customer... customers) {
            Customer customer = customers[0];

            IRetrofitCustomerData apiCustomer = CustomerDataClient.getRetrofitCustomerInstance().create(IRetrofitCustomerData.class);
            Call<CustomerResult> call = apiCustomer.updateCustomerCoordinates(Common.currentUser.getEmail(), customer.getCustomernumber(),
                                                customer.getLat(), customer.getLng());


            try {

                CustomerResult searchResult = call.execute().body();
                return searchResult;

            } catch (IOException e) {

                waitingDialogCustomers.dismiss();
                Toast.makeText(getActivity(), "Please check your internet connection.", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {

                waitingDialogCustomers.dismiss();
                Toast.makeText(getActivity(), "A general error occured.", Toast.LENGTH_SHORT).show();

            }

            return null;
        }
    }

    public void addReport(final String edcustomerName) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("ADD REPORT");


        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout_pwd = inflater.inflate(R.layout.layout_add_report, null);

        //final MaterialEditText customerName = (MaterialEditText) layout_pwd.findViewById(R.id.customerName);
        final MaterialEditText customerReport = layout_pwd.findViewById(R.id.customerReport);


        final Spinner customerName = layout_pwd.findViewById(R.id.customerName);
        List<String> customerNameList = getCustomerDropDownList();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_selected, customerNameList) {

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

        int spinnerPosition = arrayAdapter.getPosition(edcustomerName);
        customerName.setSelection(spinnerPosition);

        /*
        drefcustomerlist.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_selected, arrCustomername) {

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

                        int spinnerPosition = arrayAdapter.getPosition(edcustomerName);
                        customerName.setSelection(spinnerPosition);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

         */

        alertDialog.setView(layout_pwd);

        // Lets populate the dropdown

        alertDialog.setPositiveButton("ADD REPORT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(customerName.getSelectedItem().toString()) ||
                        customerName.getSelectedItem().toString().equalsIgnoreCase("Select Customer...")) {

                    //Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_customername), Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.enter_customername), Toast.LENGTH_SHORT);
                    return;
                }
                if (TextUtils.isEmpty(customerReport.getText().toString())) {
                    //Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_customerreport), Snackbar.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.enter_customerreport), Toast.LENGTH_SHORT);
                    return;
                }

                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                User user = new User();
                Report report = new Report();
                report.setCustomerName(customerName.getSelectedItem().toString());
                report.setCustomerReport(customerReport.getText().toString());
                report.setLat(Common.currentLat);
                report.setLng(Common.currentLng);
                report.setAddress(Common.locationaddress);
                report.setOwnerid(userid);

                long timestamp = -1 * new Date().getTime();
                report.setTimestamp(timestamp);

                String key = drefreportlist.child(userid).push().getKey();
                report.setReportId(key);

                drefreportlist.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(key)
                        .setValue(report)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Snackbar.make(getView(), "Report Successfully Added", Snackbar.LENGTH_SHORT).show();
                                Toast.makeText(getActivity(), "Report Successfully Added", Toast.LENGTH_SHORT);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Snackbar.make(getView(), getActivity().getResources().getString(R.string.failed) + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.failed) + e.getMessage(), Toast.LENGTH_SHORT);
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

    public void showCustomerReportList(final String edcustomerName) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("CUSTOMER REPORTS");


        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout_pwd = inflater.inflate(R.layout.layout_add_report, null);
        View fragment_reportlist = inflater.inflate(R.layout.fragment_report_list, null);

        rvRptHistory =  fragment_reportlist.findViewById(R.id.rvReportHistory);
        rvRptHistory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        //layoutManager..setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);
        rvRptHistory.setLayoutManager(layoutManager);
        rvRptHistory.setItemAnimator(new DefaultItemAnimator());
        rvRptHistory.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));

        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        drefreportlist = database.getReference(Common.REPORT_NODE);
        rptlistData = new ArrayList<>();
        rptadapter = new rptHistoryAdapter(getContext(), rptlistData, new ClickListener() {
            @Override
            public void onClick(View view, int index) {

                Report report = rptlistData.get(index);

                Log.v(TAG, report.getOwnerid() + ":" + report.getCustomerName() + ":" + report.getReportId());


            }
        }, new OnCommentClickListener() {
            @Override
            public void onClick(View view, int index) {
                Toast.makeText(getContext(), "Comments cannot be viewed in this mode", Toast.LENGTH_SHORT);
            }
        });

        rvRptHistory.setAdapter(rptadapter);
        getCustomerReportHistory(edcustomerName);

        alertDialog.setView(fragment_reportlist);

        // Lets populate the dropdown


        /* alertDialog.setPositiveButton("ADD REPORT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


            }
        }); */

        alertDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void getCustomerReportHistory(final String customername){
        //listData.clear();

        drefreportlist = database.getReference(Common.REPORT_NODE);
        drefreportlist.child(Common.userID)
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
            View view = getView();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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


}
