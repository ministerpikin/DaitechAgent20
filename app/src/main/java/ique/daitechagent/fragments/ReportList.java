package ique.daitechagent.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
//import android.support.v4.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import ique.daitechagent.R;
import ique.daitechagent.activities.ChatActivity;
import ique.daitechagent.adapters.ReportCommentListAdapter;
import ique.daitechagent.adapters.rptHistoryAdapter;
import ique.daitechagent.common.ClickListener;
import ique.daitechagent.common.Common;
import ique.daitechagent.common.OnCommentClickListener;
import ique.daitechagent.controllers.SwipeController;
import ique.daitechagent.controllers.SwipeControllerActions;
import ique.daitechagent.database.DataContext;
import ique.daitechagent.model.Customer;
import ique.daitechagent.model.Report;
import ique.daitechagent.model.ReportComment;
import ique.daitechagent.model.User;
import ique.daitechagent.utils.DateUtils;

//import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportList extends Fragment {

    private static final String TAG = "ReportList";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    FirebaseDatabase database;
    DatabaseReference reportHistory;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView rvRptHistory;
    rptHistoryAdapter adapter;
    FirebaseAuth mAuth;
    ArrayList<Report> listData;

    // Variables for comments
    RecyclerView rvReportComments;
    RecyclerView.LayoutManager layoutManagerReportComments;
    ReportCommentListAdapter reportCommentListAdapter;
    ArrayList<ReportComment> reportCommentArrayList;
    DatabaseReference drefReportComment;

    //variables and widgets
    private static final int STANDARD_APPBAR = 0;
    private static final int SEARCH_APPBAR = 1;
    private int mAppBarState;
    private AppBarLayout viewContactsBar, searchBar;
    private EditText mSearchContacts;
    private ImageView mAddCustomerIcon;

    private TextView mSearchToolbarHeading;

    SwipeController swipeController = null;
    private DataContext db;

    public static String COMMENT_REPORT_ID = "reportid";
    public static String COMMENT_USER_ID = "userid";
    public static String COMMENT_CUSTOMER = "customer";
    public static String COMMENT_REPORT_MSG = "reportmsg";

    public ReportList() {
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
    public static ReportList newInstance(String param1, String param2) {
        ReportList fragment = new ReportList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        //args.pu
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

        Log.d(TAG, "onCreateView: started.");

        // Inflate the layout for this fragment

        String customerName, customerReport;

        View fragment_reportlist = inflater.inflate(R.layout.fragment_report_list, container, false);
        db = new DataContext(getActivity(), null, null, 1);
        /*
            Lets do something with the toolbars
         */
        //View view = inflater.inflate(R.layout.fragment_viewcontacts, container, false);
        //LayoutInflater.from(par)

        viewContactsBar = ((AppCompatActivity)getActivity()).findViewById(R.id.viewContactsToolbar);//.getSupportActionBar();
        //viewContactsBar = (AppBarLayout) container.getChildAt(0).findViewById(R.id.viewContactsToolbar);
        //container.getParent()
        searchBar = ((AppCompatActivity)getActivity()).findViewById(R.id.searchToolbar);
        //contactsList = (ListView) fragment_reportlist.findViewById(R.id.contactsList);
        mSearchContacts = getActivity().findViewById(R.id.etSearchContacts);

        mSearchToolbarHeading = getActivity().findViewById(R.id.toolbarHeading);

        mSearchToolbarHeading.setText("Report List");

        mAddCustomerIcon = getActivity().findViewById(R.id.ivAddCustomerIcon);
        mAddCustomerIcon.setVisibility(View.GONE);

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


        initRecyclerView(fragment_reportlist);

        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        reportHistory = database.getReference(Common.REPORT_NODE);
        listData = new ArrayList<>();
        adapter = new rptHistoryAdapter(getContext(), listData, new ClickListener() {
            @Override
            public void onClick(View view, int index) {

                Report report = listData.get(index);

                Log.v(TAG, report.getOwnerid() + ":" + report.getCustomerName() + ":" + report.getReportId());

                showDialogEditReport(report.getCustomerName(), report.getCustomerReport(),
                        report.getOwnerid(), report.getReportId());

            }
        }, new OnCommentClickListener() {
            @Override
            public void onClick(View view, int index) {
                Log.v(TAG,  String.valueOf(index));

                Report report = listData.get(index);
                //showDialogReportComments(report.getOwnerid(), report.getReportId());

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ReportList.COMMENT_REPORT_ID, report.getReportId());
                intent.putExtra(ReportList.COMMENT_USER_ID, report.getOwnerid());
                intent.putExtra(ReportList.COMMENT_CUSTOMER, report.getCustomerName());
                intent.putExtra(ReportList.COMMENT_REPORT_MSG, report.getCustomerReport());
                startActivity(intent);
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

        rvRptHistory.setAdapter(adapter);
        getReportHistory();

        swipeController = new SwipeController(getContext(), new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {

                Report report = listData.get(position);
                showDeleteReportDialog(position, report.getOwnerid(), report.getReportId());


            }

            @Override
            public void onLeftClicked(int position) {
                //super.onLeftClicked(position);
                Report report = listData.get(position);

                Log.v(TAG, report.getOwnerid() + ":" + report.getCustomerName() +  ":" + report.getReportId());

                showDialogEditReport( report.getCustomerName(),  report.getCustomerReport(),
                        report.getOwnerid(), report.getReportId());
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(rvRptHistory);

        rvRptHistory.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c,  1);
            }

        });

        return fragment_reportlist;//inflater.inflate(R.layout.fragment_report_list, container, false);
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

    private void initRecyclerView(View view){
        rvRptHistory =  view.findViewById(R.id.rvReportHistory);
        rvRptHistory.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());

        //layoutManager..setReverseLayout(true);
        //layoutManager.setStackFromEnd(true);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setMoveDuration(1000);
        itemAnimator.setRemoveDuration(1000);


        rvRptHistory.setLayoutManager(layoutManager);
        //rvRptHistory.setItemAnimator(new DefaultItemAnimator());
        rvRptHistory.setItemAnimator(itemAnimator);
        //rvRptHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));



    }

    private void getReportHistory(){
        //listData.clear();

        reportHistory.child(Common.userID)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listData.clear();
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Report history = postSnapshot.getValue(Report.class);
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

    public void showDeleteReportDialog(final int position, final String ownerid, final String reportId){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialogBuilder.setMessage("You are gonig to delete a Report. Continue?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                        waitingDialog.show();

                        DatabaseReference reportinfo = FirebaseDatabase.getInstance().getReference(Common.REPORT_NODE);
                        reportinfo.child(ownerid).child(reportId).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                waitingDialog.dismiss();
                                if (databaseError == null) {
                                    //adapter.items.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                                    Toast.makeText(getActivity(), "Report has been deleted!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), "Error deleting report!" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void showDialogEditReport(final String customerName, final String customerReport, final String ownerid, final String reportId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("UPDATE REPORT");
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
        etCustomerReport.setText(customerReport);


        alertDialog.setView(layout_pwd);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                waitingDialog.show();
                String strName = etCusterName.getSelectedItem().toString();
                String strReport = etCustomerReport.getText().toString();

                if (TextUtils.isEmpty(strName) ||
                        strName.equalsIgnoreCase("Select Customer...")) {

                    Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_customername), Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strReport)) {
                    Snackbar.make(getView(), getActivity().getResources().getString(R.string.enter_customerreport), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> updateInfo = new HashMap<>();
                if(!TextUtils.isEmpty(strName))
                    updateInfo.put("customerName", strName);
                if(!TextUtils.isEmpty(strReport))
                    updateInfo.put("customerReport",strReport);

                DatabaseReference reportinfo = FirebaseDatabase.getInstance().getReference(Common.REPORT_NODE);
                reportinfo.child(ownerid).child(reportId)
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

    public void showDialogReportComments(final String ownerid, final String reportId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        alertDialog.setTitle("Comments");

        //final AlertDialog alertDialog = alertDialog1.create();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout_pwd = inflater.inflate(R.layout.layout_report_comments_alt, null);
        final EditText edittext_chatbox = layout_pwd.findViewById(R.id.edittext_chatbox);
        final Button button_chatbox_send = layout_pwd.findViewById(R.id.button_chatbox_send);


        rvReportComments =  layout_pwd.findViewById(R.id.reyclerview_message_list);
        rvReportComments.setHasFixedSize(true);
        layoutManagerReportComments = new LinearLayoutManager(getContext());

        rvReportComments.setLayoutManager(layoutManagerReportComments);
        rvReportComments.setItemAnimator(new DefaultItemAnimator());
        //rvReportComments.addItemDecoration(new DividerItemDecoration(getApplicationContext(),LinearLayoutManager.VERTICAL));

        mAuth = FirebaseAuth.getInstance();
        Common.userID =  mAuth.getCurrentUser().getUid();
        database = FirebaseDatabase.getInstance();
        drefReportComment = database.getReference(Common.REPORT_COMMENT_NODE);
        reportCommentArrayList = new ArrayList<>();
        reportCommentListAdapter = new ReportCommentListAdapter(getContext(), reportCommentArrayList);

        rvReportComments.setAdapter(reportCommentListAdapter);
        getReportComments(ownerid, reportId);
        layoutManagerReportComments.scrollToPosition(reportCommentArrayList.size() - 1);

        button_chatbox_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                //waitingDialog.show();

                String sComment = edittext_chatbox.getText().toString();
                ReportComment rc = new ReportComment();
                rc.setMessage(sComment);
                rc.setCreatedAt(DateUtils.getTimeStamp());
                rc.setUserID(Common.userID);
                String key =  drefReportComment.child(reportId).push().getKey();
                rc.setKey(key);
                User user = Common.currentUser;
                user.setPassword("");
                user.setPhone("");
                rc.setSender(user);

                drefReportComment.child(reportId)
                        .child(key)
                        .setValue(rc)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //waitingDialog.dismiss();
                                if(task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Comment Sent", Toast.LENGTH_SHORT).show();
                                    edittext_chatbox.getText().clear();
                                    layoutManagerReportComments.scrollToPosition(reportCommentArrayList.size() - 1);
                                }
                                else
                                    Toast.makeText(getActivity(),"Comment sending failed",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

        // Window shapping calculations
        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        alertDialog.setView(layout_pwd);
        /*
        alertDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                final android.app.AlertDialog waitingDialog = new SpotsDialog(getActivity());
                waitingDialog.show();

                String sComment = edittext_chatbox.getText().toString();
                ReportComment rc = new ReportComment();
                rc.setMessage(sComment);
                rc.setCreatedAt(DateUtils.getTimeStamp());
                rc.setUserID(Common.userID);
                String key =  drefReportComment.child(reportId).push().getKey();
                rc.setKey(key);
                User user = Common.currentUser;
                user.setPassword("");
                user.setPhone("");
                rc.setSender(user);

                drefReportComment.child(reportId)
                        .child(key)
                        .setValue(rc)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                waitingDialog.dismiss();
                                if(task.isSuccessful())
                                    Toast.makeText(getActivity(),"Comment Sent",Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity(),"Comment sending failed",Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });
        */
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();//.getWindow().setLayout((int)(displayRectangle.width() *
                       //0.8f), (int)(displayRectangle.height() * 0.8f));



    }

    private void getReportComments(final String ownerid, final String reportid){


        drefReportComment.child(reportid)
                .orderByChild("createdAt")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reportCommentArrayList.clear();
                        for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){

                            ReportComment comments = postSnapshot.getValue(ReportComment.class);
                            //comments.setSender(Common.currentUser);
                            reportCommentArrayList.add(comments);

                        }
                        reportCommentListAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
