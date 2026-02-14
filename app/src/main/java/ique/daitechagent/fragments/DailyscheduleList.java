package ique.daitechagent.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ique.daitechagent.R;
import ique.daitechagent.adapters.dailyscheduleListAdapter;
import ique.daitechagent.common.ClickListener;
import ique.daitechagent.common.Common;
import ique.daitechagent.controllers.SwipeController;
import ique.daitechagent.controllers.SwipeControllerActions;
import ique.daitechagent.database.DataContext;
import ique.daitechagent.model.Customer;
import ique.daitechagent.model.Dailyschedule;
import ique.daitechagent.model.Report;
import ique.daitechagent.model.User;

//import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DailyscheduleList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyscheduleList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyscheduleList extends Fragment {

    private static final String TAG = "DailyscheduleList";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //variables and widgets
    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private int mAppBarState;
    private AppBarLayout viewContactsBar, searchBar;
    private EditText mSearchContacts;
    private TextView mSearchToolbarHeading;
    private Spinner schedulePeriod;
    private ImageView mAddCustomerIcon;

    private CalendarView mCalendar;

    FirebaseDatabase database;
    DatabaseReference drefdailyschedule;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvDailyScheduleList;
    dailyscheduleListAdapter adapter;
    FirebaseAuth mAuth;
    ArrayList<Dailyschedule> listData;

    SwipeController swipeController = null;
    String globalDateSelected = "";

    BottomSheetBehavior sheetBehavior;
    LinearLayout layoutBottomSheet;

    TextView dailyCustomerName, dailyCustomerReportCount, dailyCustomerAddress, hiddenDailyID;

    Button btnCheckIn, btnEndVisit, btnWrtieReport;

    private DataContext db;

    public DailyscheduleList() {
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
    public static DailyscheduleList newInstance(String param1, String param2) {
        DailyscheduleList fragment = new DailyscheduleList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        String customername, customernumber, customeraddress;

        View fragment_daily_schedule_list = inflater.inflate(R.layout.fragment_daily_plan_list, container, false);
        db = new DataContext(getActivity(), null, null, 1);

        viewContactsBar = ((AppCompatActivity)getActivity()).findViewById(R.id.viewContactsToolbar);//.getSupportActionBar();
        //viewContactsBar = (AppBarLayout) container.getChildAt(0).findViewById(R.id.viewContactsToolbar);
        //container.getParent()
        searchBar = ((AppCompatActivity)getActivity()).findViewById(R.id.searchToolbar);
        //contactsList = (ListView) fragment_reportlist.findViewById(R.id.contactsList);
        mSearchContacts = getActivity().findViewById(R.id.etSearchContacts);
        mSearchToolbarHeading = getActivity().findViewById(R.id.toolbarHeading);

        mAddCustomerIcon = getActivity().findViewById(R.id.ivAddCustomerIcon);
        mAddCustomerIcon.setVisibility(View.GONE);

        mSearchToolbarHeading.setText("Daily Schedule List");

        setAppBarState(STANDARD_APPBAR);

        setupCalendar(fragment_daily_schedule_list);

        // Setup bottom sheet
        layoutBottomSheet = fragment_daily_schedule_list.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        // Setup slide up button stuff ,
        dailyCustomerName = layoutBottomSheet.findViewById(R.id.dailyCustomerName);
        dailyCustomerReportCount = layoutBottomSheet.findViewById(R.id.dailyCustomerReportCount);
        dailyCustomerAddress = layoutBottomSheet.findViewById(R.id.dailyCustomerAddress);
        hiddenDailyID = layoutBottomSheet.findViewById(R.id.hiddenDailyID);


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

        initRecyclerView(fragment_daily_schedule_list);

        mAuth = FirebaseAuth.getInstance();
        Common.userID = mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        drefdailyschedule = database.getReference(Common.DAILY_SCHEDULE_NODE);
        listData = new ArrayList<>();
        adapter = new dailyscheduleListAdapter(getContext(), listData, new ClickListener() {
            @Override
            public void onClick(View view, int index) {

                Dailyschedule dailyschedule = listData.get(index);

                Log.v(TAG, dailyschedule.getDailyPlanID() + ":" + dailyschedule.getCustomername() + ":" + dailyschedule.getDescription());

                //String customername = dailyschedule.getCustomername();
                //showDailyVisitMenu(dailyschedule.getCustomername());
                setCustomerDetails(dailyschedule.getCustomername(), dailyschedule.getDailyPlanID(),
                        dailyschedule.getCheckin(), dailyschedule.getCheckout());
                toggleBottomSheet();



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

        rvDailyScheduleList.setAdapter(adapter);

        SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String selectedDate = date_format.format(new Date(mCalendar.getDate()));
        getDailyScheduleList(selectedDate);

        swipeController = new SwipeController(getContext(), new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {

                Dailyschedule dailyschedule = listData.get(position);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String dailyscheduleuuid = dailyschedule.getDailyPlanID();

                showDeleteDailyScheduleDialog(position, dailyscheduleuuid, uid);

            }

            @Override
            public void onLeftClicked(int position) {

                Dailyschedule dailyschedule = listData.get(position);

                Log.v(TAG, dailyschedule.getDailyPlanID() + ":" + dailyschedule.getCustomername() +  ":" + dailyschedule.getDescription());

                showDialogEditDailySchedule( dailyschedule.getCustomername(),  dailyschedule.getDescription(),
                        dailyschedule.getDailyPlanID(), dailyschedule.getVisitDate(), dailyschedule.getVisitPeriod());

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(rvDailyScheduleList);

        rvDailyScheduleList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c, 3);
            }

        });



        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        //btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        //btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        btnCheckIn = layoutBottomSheet.findViewById(R.id.btnCheckIn);
        btnEndVisit = layoutBottomSheet.findViewById(R.id.btnEndVisit);
        btnWrtieReport = layoutBottomSheet.findViewById(R.id.btnWriteReport);

        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCheckInCheckOut(1, hiddenDailyID.getText().toString());
            }
        });

        btnEndVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogCheckInCheckOut(2, hiddenDailyID.getText().toString());
            }
        });

        btnWrtieReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.currentLat != null) {
                    showDialogNewReport(dailyCustomerName.getText().toString(), Common.currentLat, Common.currentLng, Common.locationaddress);
                }else{
                    showDialogNewReport(dailyCustomerName.getText().toString(), 0, 0, "");
                }
            }
        });




        return fragment_daily_schedule_list;//inflater.inflate(R.layout.fragment_report_list, container, false);
    }


    public void showDialogCheckInCheckOut(final int checkincheckout, final String scheduleID) {
        String operations = "Check In";
        if (checkincheckout == 1){
            operations = "Check In";
        }else if (checkincheckout == 2){
            operations = "Check Out";
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle(operations);

        Date dNow = new Date( );
        final long timestamp = dNow.getTime();
        SimpleDateFormat ft = new SimpleDateFormat ("E dd-MM-yyyy 'at' hh:mm:ss a");
        String strCheckInCheckOutTime = ft.format(dNow);

        String msg = "Date and Time for " + operations + " is " + strCheckInCheckOutTime;
        alertDialog.setMessage(msg + ". Continue?");

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                waitingDialog.show();

                Map<String, Object> updateInfo = new HashMap<>();
                if (checkincheckout == 1){
                    updateInfo.put("checkin", timestamp);
                }else if (checkincheckout == 2){
                    updateInfo.put("checkout", timestamp);
                }
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference dailyinfo = FirebaseDatabase.getInstance().getReference(Common.DAILY_SCHEDULE_NODE);
                dailyinfo.child(uid).child(scheduleID)
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if (task.isSuccessful()) {

                                    if (checkincheckout == 1) {
                                        btnCheckIn.setEnabled(false);
                                        btnCheckIn.setBackgroundColor(getResources().getColor(R.color.colorGreyDark));
                                        btnCheckIn.setText("ALREADY CHECKED IN");

                                        btnEndVisit.setEnabled(true);
                                        btnEndVisit.setBackgroundColor(getResources().getColor(R.color.black));
                                        btnEndVisit.setText("END VISIT");

                                    } else if (checkincheckout == 2) {

                                        btnCheckIn.setEnabled(false);
                                        btnCheckIn.setBackgroundColor(getResources().getColor(R.color.colorGreyDark));
                                        btnCheckIn.setText("ALREADY CHECKED IN");

                                        btnEndVisit.setEnabled(false);
                                        btnEndVisit.setBackgroundColor(getResources().getColor(R.color.colorGreyDark));
                                        btnEndVisit.setText("ALREADY ENDED VISIT");
                                    }

                                    Toast.makeText(getActivity(), "Information Updated!", Toast.LENGTH_SHORT).show();
                                }else
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


    private void setCustomerDetails(final String customerName, final String dailyID, long checkInValue, long checkOutVal){
        dailyCustomerName.setText(customerName);
        hiddenDailyID.setText(dailyID);

        if (checkInValue == 0){
            btnCheckIn.setEnabled(true);
            btnCheckIn.setBackgroundColor(getResources().getColor( R.color.colorPrimaryDark));
            btnCheckIn.setText("CHECK IN");

            btnEndVisit.setEnabled(false);
            btnEndVisit.setBackgroundColor(getResources().getColor( R.color.colorGreyDark));
            btnEndVisit.setText("END VISIT (NOT CHECKED IN)");
        }else{
            btnCheckIn.setEnabled(false);
            btnCheckIn.setBackgroundColor(getResources().getColor( R.color.colorGreyDark));
            btnCheckIn.setText("ALREADY CHECKED IN");

            btnEndVisit.setEnabled(true);
            btnEndVisit.setBackgroundColor(getResources().getColor( R.color.black));
            btnEndVisit.setText("END VISIT");
        }
        if (checkOutVal != 0){
            btnCheckIn.setEnabled(false);
            btnCheckIn.setBackgroundColor(getResources().getColor( R.color.colorGreyDark));
            btnCheckIn.setText("ALREADY CHECKED IN");

            btnEndVisit.setEnabled(false);
            btnEndVisit.setBackgroundColor(getResources().getColor( R.color.colorGreyDark));
            btnEndVisit.setText("ALREADY ENDED VISIT");
        }

        DatabaseReference drefcustomersDetails = FirebaseDatabase.getInstance().getReference(Common.CUSTOMER_NODE);
        final DatabaseReference drefreportlist = FirebaseDatabase.getInstance().getReference(Common.REPORT_NODE);

        drefcustomersDetails.child(Common.userID)
                .orderByChild("customername")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            final Customer history = postSnapshot.getValue(Customer.class);
                            String searchCustomerName = history.getCustomername() + " [" + history.getCustomernumber() + "]";
                            if (customerName.equalsIgnoreCase(searchCustomerName)) {
                                dailyCustomerAddress.setText(history.getAddress());

                                Query query = drefreportlist.child(Common.userID).orderByChild("customerName").equalTo(searchCustomerName);
                                query.addValueEventListener(new ValueEventListener() {
                                    long reportcount;

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            reportcount = dataSnapshot.getChildrenCount();
                                            dailyCustomerReportCount.setText("Reports Written: " + reportcount);
                                        } else {
                                            reportcount = 0;
                                            dailyCustomerReportCount.setText("Reports Written: " + reportcount);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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

    private void getDailyScheduleList(String filterDate) {
        listData.clear();
        adapter.notifyDataSetChanged();


        drefdailyschedule.child(Common.userID)
                .orderByChild("visitDate")
                .equalTo(filterDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listData.clear();
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                            Dailyschedule history = postSnapshot.getValue(Dailyschedule.class);
                            listData.add(history);
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void initRecyclerView(View view) {

        rvDailyScheduleList = view.findViewById(R.id.rvDailyScheduleHistory);
        rvDailyScheduleList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        //layoutManager..setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);

        rvDailyScheduleList.setLayoutManager(layoutManager);
        rvDailyScheduleList.setItemAnimator(new DefaultItemAnimator());
        rvDailyScheduleList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

    }

    public void showDeleteDailyScheduleDialog(final int position, final String dailyscheduleuuid, final String uid){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialogBuilder.setMessage("You are going to delete a Schedule entry. Continue?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                        waitingDialog.show();

                        //DatabaseReference reportinfo = FirebaseDatabase.getInstance().getReference(Common.REPORT_NODE);
                        drefdailyschedule.child(uid).child(dailyscheduleuuid).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                waitingDialog.dismiss();
                                if (databaseError == null) {
                                    //adapter.items.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                    Toast.makeText(getActivity(), "Schedule entry has been deleted!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), "Error deleting Schedule entry!" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void showDialogEditDailySchedule(final String scustomername,  String sdescription,
                                            final String dailyplanid, String svisitdate, String svisitperiod) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        //alertDialog.setTitle("EDIT SCHEDULE");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_add_daily_schedule, null);

        initializePeriodSpinner(layout_pwd, svisitperiod);

        //final MaterialEditText etCusterName = (MaterialEditText) layout_pwd.findViewById(R.id.edcustomerName);
        final MaterialEditText scheduleDescription = layout_pwd.findViewById(R.id.scheduledescription);
        final TextView scheduleDate = layout_pwd.findViewById(R.id.scheduledate);

        DatabaseReference customers = FirebaseDatabase.getInstance().getReference(Common.CUSTOMER_NODE);
        //ArrayAdapter<String> arrayAdapter;

        final Spinner etCusterName = layout_pwd.findViewById(R.id.customerName);

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
        etCusterName.setAdapter(arrayAdapter);

        int spinnerPosition = arrayAdapter.getPosition(scustomername);
        etCusterName.setSelection(spinnerPosition);

        /*
        customers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<String> arrCustomername = new ArrayList<String>();
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String sCustomerName = dataSnapshot1.child("customername").getValue(String.class);
                            String sCustomerNumber = dataSnapshot1.child("customernumber").getValue(String.class);
                            arrCustomername.add(sCustomerName + " [" + sCustomerNumber + "]");
                        }
                        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrCustomername);
                        //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_selected, arrCustomername);
                        //arrayAdapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

                        etCusterName.setAdapter(arrayAdapter);

                        int spinnerPosition = arrayAdapter.getPosition(scustomername);
                        etCusterName.setSelection(spinnerPosition);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

         */


        scheduleDate.setText(svisitdate);
        scheduleDescription.setText(sdescription);


        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                waitingDialog.show();
                String strName = etCusterName.getSelectedItem().toString();
                String strDescription = scheduleDescription.getText().toString();
                String strPeriod = schedulePeriod.getSelectedItem().toString();
                String strDate = scheduleDate.getText().toString();

                Map<String, Object> updateInfo = new HashMap<>();
                if(TextUtils.isEmpty(strName) ||
                        strName.equalsIgnoreCase("Select Customer...")) {
                    Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_customerreport), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(strDescription)){
                    Snackbar.make(getView(), "Description is Empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(strPeriod) || strPeriod.equalsIgnoreCase("Select Period...")){
                    Snackbar.make(getView(), "Time period is empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(strDate)){
                    Snackbar.make(getView(), "Date is empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                updateInfo.put("customername", strName);
                updateInfo.put("visitlocation", strName);
                updateInfo.put("description",strDescription);
                updateInfo.put("visitPeriod", strPeriod);
                updateInfo.put("visitDate",strDate);





                String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference dailyscheduleinfo = FirebaseDatabase.getInstance().getReference(Common.DAILY_SCHEDULE_NODE);
                dailyscheduleinfo.child(userid).child(dailyplanid)
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                    Toast.makeText(getActivity(),"Information Updated!",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity(),"Information Update Failed!",Toast.LENGTH_SHORT).show();

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

    public void showDailyVisitMenu(final String scustomername) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle(scustomername);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.fragment_daily_visit_tasks, null);
        Button btnCheckIn  = layout_pwd.findViewById(R.id.btnCheckIn);
        Button btnEndVisit  = layout_pwd.findViewById(R.id.btnEndVisit);
        Button btnWriteReport  = layout_pwd.findViewById(R.id.btnWriteReport);
        Button btnCancel  = layout_pwd.findViewById(R.id.btnCancel);

        alertDialog.setView(layout_pwd);

        // create alert dialog
        final AlertDialog dialog = alertDialog.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnWriteReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogEditReport(scustomername, Common.currentLat, Common.currentLng, Common.locationaddress);
            }
        });



        dialog.show();
    }

    public void showDialogNewReport(final String customerName, final double latitude,
                                    final double longitude, final String address) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("NEW REPORT");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_edit_report, null);
        //final MaterialEditText etCusterName = (MaterialEditText) layout_pwd.findViewById(R.id.edcustomerName);
        final MaterialEditText etCustomerReport = layout_pwd.findViewById(R.id.edcustomerReport);

        DatabaseReference customers = FirebaseDatabase.getInstance().getReference(Common.CUSTOMER_NODE);
        //ArrayAdapter<String> arrayAdapter;

        final Spinner etCusterName = layout_pwd.findViewById(R.id.edcustomerName);

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
        etCusterName.setAdapter(arrayAdapter);

        int spinnerPosition = arrayAdapter.getPosition(customerName);
        etCusterName.setSelection(spinnerPosition);

        /*
        customers.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<String> arrCustomername = new ArrayList<String>();
                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                            String sCustomerName = dataSnapshot1.child("customername").getValue(String.class);
                            String sCustomerNumber = dataSnapshot1.child("customernumber").getValue(String.class);
                            arrCustomername.add(sCustomerName + " [" + sCustomerNumber + "]");
                        }
                        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrCustomername);
                        //arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_selected, arrCustomername);
                        //arrayAdapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

                        etCusterName.setAdapter(arrayAdapter);

                        int spinnerPosition = arrayAdapter.getPosition(customerName);
                        etCusterName.setSelection(spinnerPosition);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
        */

        //etCusterName.sets.setText(customerName);
        //etCustomerReport.setText(customerReport);


        alertDialog.setView(layout_pwd);
        // Lets populate the dropdown

        alertDialog.setPositiveButton("ADD REPORT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (TextUtils.isEmpty(etCusterName.getSelectedItem().toString()) ||
                        etCusterName.getSelectedItem().toString().equalsIgnoreCase("Select Customer...")){
                    Snackbar.make(getView(), "Enter Customer Name", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(etCustomerReport.getText().toString())){
                    Snackbar.make(getView(), "Enter Report", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                String userid = FirebaseAuth.getInstance().getUid();

                User user=new User();
                Report report = new Report();
                report.setCustomerName(etCusterName.getSelectedItem().toString());
                report.setCustomerReport(etCustomerReport.getText().toString());
                report.setLat(latitude);
                report.setLng(longitude);
                report.setAddress(address);
                report.setOwnerid(userid);

                long timestamp = -1 * new Date().getTime();
                report.setTimestamp(timestamp);

                DatabaseReference reports=FirebaseDatabase.getInstance().getReference(Common.REPORT_NODE);
                String key = reports.child(userid).push().getKey();
                report.setReportId(key);

                reports.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(key)
                        .setValue(report)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(getView(), "Report Successfully Added", Snackbar.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(getView(), "Failed: " +e.getMessage(), Snackbar.LENGTH_SHORT).show();
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

    /*
    private void initializePeriodSpinner(View view, String visitPeriod){

        schedulePeriod = (Spinner)view.findViewById(R.id.scheduleperiod);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getActivity(),
                R.array.period_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        // adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        schedulePeriod.setAdapter(adapterSpinner);

        int spinnerPosition = adapterSpinner.getPosition(visitPeriod);
        schedulePeriod.setSelection(spinnerPosition);
    }
    */

    private void initializePeriodSpinner(View view, String visitPeriod){

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

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_selected, arrPeriods){

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

        schedulePeriod.setAdapter(adapterSpinner);

        int spinnerPosition = adapterSpinner.getPosition(visitPeriod);
        schedulePeriod.setSelection(spinnerPosition);
    }

    private void setupCalendar(View view) {

        mCalendar = view.findViewById(R.id.calendar);

        if (globalDateSelected.isEmpty()){
            SimpleDateFormat date_format_initialize = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String selectedDateInitialize = date_format_initialize.format(new Date(mCalendar.getDate()));
            globalDateSelected = selectedDateInitialize;
        }

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String selectedDate = date_format.format(new Date(view.getDate()));
                Log.i("onSelectedDayChange", selectedDate);

                final int imonth = month + 1;
                final int iyear = year;
                final int day = dayOfMonth; //String.format("%05d", yournumber);
                selectedDate = String.format("%02d", day) + "-" + String.format("%02d", imonth) + "-" + iyear;
                //Log.i("date", dayOfMonth + "-" + imonth + "-" + year);
                globalDateSelected = selectedDate;//dayOfMonth + "-" + imonth + "-" + year;
                getDailyScheduleList(globalDateSelected);
            }
            /*
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, final int i, final int i1, final int i2) {
                final int i_m = i1 + 1;
                Log.i("date", i2 + "/" + i_m + "/" + i);

                getDailyScheduleList(i2 + "/" + i_m + "/" + i);

            }
            */
        });
    }


    public String getCalendarDate(){
        //SimpleDateFormat date_format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        //String selectedDate = date_format.format(new Date(mCalendar.getDate()));
        //String selectedDate2 = globalDateSelected;
        return globalDateSelected;
    }

    private void toggleBottomSheet(){
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            //btnBottomSheet.setText("Close sheet");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //btnBottomSheet.setText("Expand sheet");
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
